package Cliente;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;

/**
 * Clase que se encargada de iniciar la intefaz y de hacer todo lo necesario para iniciar la comunicacion 
 * cifrada entre cliente y servidor.
 * Una vez establecida la conexion, crea los Threads encargados de enviar y recibir mensajes. 
 */
public class Cliente{
    private static Socket socket; 
    private final static String IP_SERVIDOR = "192.168.0.20"; 
    private static KeyAgreement clientKeyAgree;
    private static SecretKeySpec clientAesKey;
    

    public static void main(String[] args) {
    	Interfaz interfaz = new Interfaz();
        
        // Executor para manejar los Threads
        ExecutorService executor = Executors.newCachedThreadPool(); 
 
        try {
        	interfaz.agregarMensaje("Buscando Servidor ...", "", true);
        	
        	//Crea el Socket para comunicarse con el servidor
        	socket = new Socket(InetAddress.getByName(IP_SERVIDOR), 11111); 
            interfaz.agregarMensaje("Conectado a :" , socket.getInetAddress().getHostName(), true);
            
            // Crea los Streams de salida y entrada
            ObjectOutputStream  out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            byte[] serverPubKeyEnc = null;
			try {
				// Recibe la clave publica del servidor
				serverPubKeyEnc = (byte[]) in.readObject();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
    
            try {
            	
            	// Obtener clave publica del servidor
            	KeyFactory clientKeyFac = KeyFactory.getInstance("DH");
                X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverPubKeyEnc);
                PublicKey serverPubKey = clientKeyFac.generatePublic(x509KeySpec);

                // Obtener los parametros asociados con la clave publica del servidor
                // Se deben usar estos parametros para generar el par de claves del cliente
                DHParameterSpec dhParamFromServerPubKey = ((DHPublicKey)serverPubKey).getParams();

                // Generando por de claves propio
                System.out.println("Cliente: Generando par de claves DH ...");
                KeyPairGenerator clientKpairGen = KeyPairGenerator.getInstance("DH");
                clientKpairGen.initialize(dhParamFromServerPubKey);
                KeyPair clientKpair = clientKpairGen.generateKeyPair();

                // Bob creates and initializes his DH KeyAgreement object
                System.out.println("Cliente: Inicializacion ...");
                clientKeyAgree = KeyAgreement.getInstance("DH");
                clientKeyAgree.init(clientKpair.getPrivate());

                // Se obtiene la clave publica y se envia al servidor
                byte[] clientPubKeyEnc = clientKpair.getPublic().getEncoded();
                
                out.writeObject(clientPubKeyEnc);
                System.out.println("Clave publica del cliente cifrada: "+clientPubKeyEnc);
                out.flush();

                
                // Se usa la clave publica del servidor para ejecutar la fase 1 del protocolo DH
                System.out.println("Client: Executando fase 1 ...");
                clientKeyAgree.doPhase(serverPubKey, true);
                interfaz.agregarMensaje("Iniciando cifrado", "", true);
                System.out.println("Clave publica del servidor: "+serverPubKey.toString());

                
                // Recibe la longitud del secreto del servidor y genera el secreto a partir de eso
                int serverLen=in.readInt();
                byte[] clientSharedSecret = new byte[serverLen];
                clientKeyAgree.generateSecret(clientSharedSecret, 0);
                
                // Genera la clave para cifrar y decifrar los mensajes que se envien 
                clientAesKey = new SecretKeySpec(clientSharedSecret,0,16,"AES");
                
                interfaz.agregarMensaje("DH VERIFICADO, la información ahora viaja encriptada", "", true);
            	
            }catch(Exception e) {
            	e.printStackTrace();
            }
            interfaz.habilitarInput(true); 
            
            //Ejecucion de los Threads
            executor.execute(new ThreadRecibe(socket, interfaz,clientAesKey));
            executor.execute(new ThreadEnvia(socket, interfaz,clientAesKey)); 
            
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally {
        	
        }
        executor.shutdown();
    }
}
