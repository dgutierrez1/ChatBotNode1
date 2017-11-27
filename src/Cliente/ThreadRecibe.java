package Cliente;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class ThreadRecibe implements Runnable {
    private Interfaz main;
    private byte[] mensaje; 
    private ObjectInputStream entrada;
    private Socket cliente;
    private SecretKeySpec clientAesKey;
    private String reconstitutedString;
   
    
   //Inicializar chatServer y configurar GUI
   public ThreadRecibe(Socket cliente, Interfaz main, SecretKeySpec clientAesKey){
       this.cliente = cliente;
       this.main = main;
       this.clientAesKey=clientAesKey;
   }  

  
    public void run() {
        try {
            entrada = new ObjectInputStream(cliente.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ThreadRecibe.class.getName()).log(Level.SEVERE, null, ex);
        }
        do { 
            try {
            	// Recibe los parametros cifrados del servidor
            	byte[] encodedParams=(byte[]) entrada.readObject();
            	
            	//Recibe el mensaje cifrado del servidor
            	mensaje = (byte[]) entrada.readObject(); 
            	
            	// Inicializa el descrifrado
            	AlgorithmParameters aesParams = AlgorithmParameters.getInstance("AES");
                aesParams.init(encodedParams);
                Cipher serverCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                serverCipher.init(Cipher.DECRYPT_MODE, clientAesKey, aesParams);
                
                // Decifra el mensaje enviado por el servidor
                byte[] recovered = serverCipher.doFinal(mensaje);             
				reconstitutedString = new String(recovered);
                
				// Muestra el mensaje
                String serverIP = cliente.getInetAddress().getHostAddress();
                String nombreServer = cliente.getInetAddress().getHostName();
                main.agregarMensaje(serverIP+" - "+ nombreServer +" Servidor:" , reconstitutedString, false);

            } catch (SocketException ex) {
				ex.printStackTrace();
            }catch (EOFException eofException) {
                main.agregarMensaje("Fin de la conexion","", true);
                break;
            } catch (IOException ex) {
                Logger.getLogger(ThreadRecibe.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException classNotFoundException) {
                main.agregarMensaje("Objeto desconocido","", true);
            } catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}

        } while (!mensaje.equals("TERMINAR")); //Ejecuta hasta que el server escriba TERMINATE

        try {
        	// Cerrar los flujos de informacion
            entrada.close(); 
            cliente.close(); 
        } 
        catch (IOException ioException) {
            ioException.printStackTrace();
        } 

        main.agregarMensaje("Fin de la conexion","", true);
        System.exit(0);
    
        
    
    }
      
} 
