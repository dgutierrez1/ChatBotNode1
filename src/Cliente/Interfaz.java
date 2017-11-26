package Cliente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Interfaz extends JFrame{
	
	private JTextArea mensajes;
	JTextField campoTexto;
	
	public static void main(String[] args) {
		new Interfaz();
	}
	
	public Interfaz() {
		
		Font textFont =  new Font("Sans Serif", Font.PLAIN, 50);
		
		// CONFIGURACION DE LA VENTANA
		this.setSize(2000, 1500);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		
		int x = (dim.width/2)-(this.getWidth()/2);
		int y = (dim.height/2)-(this.getHeight()/2);
		
		this.setLocation(x, y);
		this.setTitle("Chat cifrado");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		// PANELES
		JPanel panelInformacion = new JPanel();
		JPanel panelMensajes = new JPanel();
		JPanel panelInput = new JPanel();
		
		
		// Panel informacion
		JLabel labTitulo = new JLabel("Chat cifrado");
		labTitulo.setFont(new Font("Sans Serif", Font.PLAIN, 180));
		
		panelInformacion.add(labTitulo);
		
		this.add(panelInformacion, BorderLayout.NORTH);
		
		//Panel mensajes
		mensajes = new JTextArea(); 
		mensajes.setFont(textFont);
		mensajes.setEditable(false);
		
        add(panelMensajes.add(new JScrollPane(mensajes)), BorderLayout.CENTER);

        
        //Panel input
        campoTexto = new JTextField();
        campoTexto.setEditable(true); 
        campoTexto.setPreferredSize( new Dimension( 1800, 70 ) );
        campoTexto.setFont(textFont);
        
        panelInput.add(campoTexto);
        add(panelInput, BorderLayout.SOUTH);
	
		
		this.setVisible(true);
	}
	
	 public void agregarMensaje(String mensaje) {
        mensajes.append(mensaje + "\n");
    } 
    public void habilitarInput(boolean editable) {
        campoTexto.setEditable(editable);
    }
    public void limpiarInput() {
    	campoTexto.setText("");
    }

	

}
