package Cliente;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
        
public class ThreadEnvia implements Runnable {
    private Interfaz main; 
    private ObjectOutputStream salida;
    private String mensaje;
    private Socket conexion; 
    private SecretKeySpec clientAesKey;
   
    public ThreadEnvia(Socket conexion, Interfaz main, SecretKeySpec clientAesKey){
        this.conexion = conexion;
        this.main = main;
        this.clientAesKey=clientAesKey;
        
        // Agrega un callback para cuando se quiera enviar un mensajes
        main.campoTexto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                mensaje = event.getActionCommand();
                enviarDatos(mensaje); 
                main.campoTexto.setText(""); 
            } 
        });
    } 
    
   private void enviarDatos(String mensaje){
      try {
    	  // Se inicializa el cifrado
    	  Cipher cf = Cipher.getInstance("AES/CBC/PKCS5Padding");
          cf.init(Cipher.ENCRYPT_MODE,clientAesKey);
          
          // Obtiene los parametros cifrados
          byte[] encodedParams = cf.getParameters().getEncoded();
          
          //Envia los parametros cifrado al servidor
          salida.writeObject(encodedParams);
          
          // Cifra el mensahe
          byte[] theCph = cf.doFinal(mensaje.getBytes());
          
          // Envia el mensaje cifrado al servidor
          salida.writeObject(theCph);
          salida.flush();
          main.agregarMensaje(conexion.getInetAddress().getHostName()+" - Tú: ", mensaje, true);
      } 
      catch (IOException ioException){ 
         main.agregarMensaje("Error escribiendo Mensaje","", true);
      } catch (NoSuchAlgorithmException e) {
    	  e.printStackTrace();
      } catch (NoSuchPaddingException e) {
    	  e.printStackTrace();
      } catch (InvalidKeyException e) {
    	  e.printStackTrace();
      } catch (IllegalBlockSizeException e) {
    	  e.printStackTrace();
      } catch (BadPaddingException e) {
    	  e.printStackTrace();
      }
   } 

   
    public void run() {
         try {
            salida = new ObjectOutputStream(conexion.getOutputStream());
            salida.flush(); 
        } catch (SocketException ex) {
        } catch (IOException ioException) {
          ioException.printStackTrace();
        } catch (NullPointerException ex) {
        }
    }   
   
} 
