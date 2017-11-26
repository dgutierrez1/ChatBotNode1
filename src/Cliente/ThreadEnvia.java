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
        
        //Evento que ocurre al escribir en el campo de texto
        main.campoTexto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                mensaje = event.getActionCommand();
                enviarDatos(mensaje); //se envia el mensaje
                main.campoTexto.setText(""); //borra el texto del enterfield
            } //Fin metodo actionPerformed
        } 
        );//Fin llamada a addActionListener
    } 
    
   //enviar objeto a cliente 
   private void enviarDatos(String mensaje){
      try {
    	  
    	  Cipher cf = Cipher.getInstance("AES/CBC/PKCS5Padding");
          cf.init(Cipher.ENCRYPT_MODE,clientAesKey);
          byte[] encodedParams = cf.getParameters().getEncoded();

          salida.writeObject(encodedParams);
          byte[] theCph = cf.doFinal(mensaje.getBytes());
          
         salida.writeObject(theCph);
         salida.flush(); //flush salida a cliente
         main.agregarMensaje("Cliente>>> " + mensaje);
      } //Fin try
      catch (IOException ioException){ 
         main.agregarMensaje("Error escribiendo Mensaje");
      } //Fin catch  
 catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchPaddingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidKeyException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalBlockSizeException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (BadPaddingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      
   } //Fin metodo enviarDatos

   
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
