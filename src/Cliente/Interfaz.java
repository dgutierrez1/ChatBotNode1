package Cliente;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Interfaz extends JFrame{
	
	private JTextPane mensajes;
	JTextField campoTexto;
	String inputPlaceholder;
	
	//Estilos
	StyledDocument d;
	SimpleAttributeSet normal;
	SimpleAttributeSet boldBlue;
	SimpleAttributeSet boldRed;
	
	public static void main(String[] args) {
		new Interfaz();
	}
	
	public Interfaz() {
		
		
		Font textFont =  new Font("Sans Serif", Font.PLAIN, 20);
		inputPlaceholder = "Escriba su mensaje y presione ENTER!";
		
		
		// CONFIGURACION DE LA VENTANA
		this.setSize(1000, 700);
		
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
		JLabel labTitulo = new JLabel("Chat cifrado ");
		labTitulo.setFont(new Font("Sans Serif", Font.PLAIN, 70));
		labTitulo.setHorizontalAlignment(JLabel.CENTER);

		
		JLabel labSubtitulo = new JLabel("Daniel Gutierrez - Jhon Tobar \n");
		labSubtitulo.setFont(new Font("Sans Serif", Font.PLAIN, 25));
		labSubtitulo.setHorizontalAlignment(JLabel.CENTER);
		
		JLabel descripcion = new JLabel("Este chat funciona con intercambio de claves Diffie-Hellman y cifrado AES128. Los mensajes viajan a través de Sockets despues de ser cifrados.");
		descripcion.setFont(new Font("Sans Serif", Font.PLAIN, 15));
		descripcion.setHorizontalAlignment(JLabel.CENTER);
		
		panelInformacion.setLayout(new GridLayout(3, 1));
		panelInformacion.add(labTitulo);
		panelInformacion.add(labSubtitulo);
		panelInformacion.add(descripcion);
	
		this.add(panelInformacion, BorderLayout.NORTH);
		
		//Panel mensajes
		mensajes = new JTextPane(); 
		mensajes.setFont(textFont);
		mensajes.setEditable(false);
		
        add(panelMensajes.add(new JScrollPane(mensajes)), BorderLayout.CENTER);

        
        //Panel input
        campoTexto = new JTextField();
        campoTexto.setEditable(false);
        iniciarPlaceholderParaTextField();
        campoTexto.setPreferredSize( new Dimension( 800, 30 ) );
        campoTexto.setFont(textFont);
        
        panelInput.add(campoTexto);
        add(panelInput, BorderLayout.SOUTH);
	
        
        // Configuracion de la impresion de los mensajes
        d = (StyledDocument) mensajes.getDocument(); 
		
		
		normal = new SimpleAttributeSet();
        StyleConstants.setFontFamily(normal, "SansSerif");
        StyleConstants.setFontSize(normal, 16);
		
		boldBlue = new SimpleAttributeSet(normal);
	    StyleConstants.setBold(boldBlue, true);
        StyleConstants.setForeground(boldBlue, Color.blue);
        
        boldRed = new SimpleAttributeSet(normal);
	    StyleConstants.setBold(boldRed, true);
        StyleConstants.setForeground(boldRed, Color.red);
        
		this.setVisible(true);
	}
	
	public void agregarMensaje( String sender, String mensaje, boolean mensajePropio) {
		
		SimpleAttributeSet attr = boldRed;
		 
		if(mensajePropio) {
			attr = boldBlue;
		}
		
        if(mensaje.equals("")) {
        	sender += "\n";
        }
        
        try {
        	d.insertString(d.getLength(), sender , attr);
			d.insertString(d.getLength(), mensaje + "\n",normal);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
    }
	
    public void habilitarInput(boolean editable) {
        campoTexto.setEditable(editable);
        campoTexto.setText(inputPlaceholder);
    }
    
    private void iniciarPlaceholderParaTextField() {
    	campoTexto.addFocusListener(new FocusListener() {
    		
    		
    	    @Override
    	    public void focusGained(FocusEvent e) {
    	        if (campoTexto.getText().equals(inputPlaceholder)) {
    	        	campoTexto.setText("");
    	        	campoTexto.setForeground(Color.BLACK);
    	        }
    	    }
    	    @Override
    	    public void focusLost(FocusEvent e) {
    	        if (campoTexto.getText().isEmpty()) {
    	        	campoTexto.setForeground(Color.GRAY);
    	        	campoTexto.setText(inputPlaceholder);
    	        }
    	    }
    	});
    }

}
