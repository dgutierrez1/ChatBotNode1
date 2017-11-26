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

/**Clase que se encarga de correr los threads de enviar y recibir texto
 * y de crear la interfaz grafica.
 * 
 * 
 */
public class Cliente extends JFrame{
    public JTextField campoTexto; //Para mostrar mensajes de los usuarios
    public JTextArea areaTexto; //Para ingresar mensaje a enviar
    private static ServerSocket servidor; //
    private static Socket cliente; //Socket para conectarse con el cliente
    private static String ip = "127.0.0.1"; //ip a la cual se conecta
    private static KeyAgreement clientKeyAgree;
    private static SecretKeySpec clientAesKey;
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	Interfaz interfaz = new Interfaz();
        
        
        ExecutorService executor = Executors.newCachedThreadPool(); //Para correr los threads
 
        try {
        	interfaz.agregarMensaje("Buscando Servidor ...");
            cliente = new Socket(InetAddress.getByName(ip), 11111); //comunicarme con el servidor
            interfaz.agregarMensaje("Conectado a :" + cliente.getInetAddress().getHostName());
            
          
            ObjectOutputStream  out = new ObjectOutputStream(cliente.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(cliente.getInputStream());
            
            byte[] serverPubKeyEnc = null;
			try {
				serverPubKeyEnc = (byte[]) in.readObject();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    
            try {
            	
            	KeyFactory clientKeyFac = KeyFactory.getInstance("DH");
                X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverPubKeyEnc);

                PublicKey serverPubKey = clientKeyFac.generatePublic(x509KeySpec);

                /*
                 * Bob gets the DH parameters associated with Alice's public key.
                 * He must use the same parameters when he generates his own key
                 * pair.
                 */
                DHParameterSpec dhParamFromServerPubKey = ((DHPublicKey)serverPubKey).getParams();

                // Bob creates his own DH key pair
                System.out.println("Client: Generate DH keypair ...");
                KeyPairGenerator clientKpairGen = KeyPairGenerator.getInstance("DH");
                clientKpairGen.initialize(dhParamFromServerPubKey);
                KeyPair clientKpair = clientKpairGen.generateKeyPair();

                // Bob creates and initializes his DH KeyAgreement object
                System.out.println("Client: Initialization ...");
                clientKeyAgree = KeyAgreement.getInstance("DH");
                clientKeyAgree.init(clientKpair.getPrivate());

                // Bob encodes his public key, and sends it over to Alice.
                byte[] clientPubKeyEnc = clientKpair.getPublic().getEncoded();
                
                out.writeObject(clientPubKeyEnc);
                System.out.println("clientPubKeyEnc: "+clientPubKeyEnc);
                out.flush();
                
                //AQUI ESTA BIEN 
                
                /*
                 * Bob uses Alice's public key for the first (and only) phase
                 * of his version of the DH
                 * protocol.
                 */
                System.out.println("Client: Execute PHASE1 ...");
                clientKeyAgree.doPhase(serverPubKey, true);
                interfaz.agregarMensaje("serverPubKey"+serverPubKey);
                
                int serverLen=in.readInt();
                byte[] clientSharedSecret = new byte[serverLen];
                clientKeyAgree.generateSecret(clientSharedSecret, 0);
                
             
                clientAesKey = new SecretKeySpec(clientSharedSecret,0,16,"AES");
                
                interfaz.agregarMensaje("DH, VERIFICADO, la información viaja encriptada");
            	
            }catch(Exception e) {
            	
            }
            interfaz.habilitarInput(true); //habilita el texto
            
            //Ejecucion de los Threads
            executor.execute(new ThreadRecibe(cliente, interfaz,clientAesKey));
            executor.execute(new ThreadEnvia(cliente, interfaz,clientAesKey)); 
            
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } //Fin del catch
        finally {
        }
        executor.shutdown();
    }
}
