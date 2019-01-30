package practica1;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;


// ===========================================================================
public class GUIPrimoSencillo1a {
// ===========================================================================

  // Declaration of variables.
  JFrame      container;
  JPanel      jpanel;
  JTextField  txfNumero, txfMensajes, txfSugerencias;
  JButton     btnPulsaAqui, btnComienzaCalculo;
  int         numVecesPulsado = 0;

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    GUIPrimoSencillo1a gui = new GUIPrimoSencillo1a();
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        gui.go();
      }
    });
  }

  // -------------------------------------------------------------------------
  
  private class Hebra extends Thread{
	  
	  long numero;
	  
	  public Hebra(long numero) {
		  this.numero=numero;
	  }
	  
	  public void run() {		  
		  boolean primo = esPrimo( numero );
          if( primo ) {
            System.out.println( "El numero " + numero + " SI es primo." );
          } else {
            System.out.println( "El numero " + numero + " NO es primo." );
          }
	  }
	  
  }
  
  
  public void go() {
    // Variables.
    JPanel     tempPanel;

    // Crea el JFrame principal.
    container = new JFrame( "GUI Primo Sencillo 1a" );

    // Consigue el panel principal del Frame "container".
    jpanel = ( JPanel ) container.getContentPane();
    //// jpanel.setPreferredSize( new Dimension( maxWinX, maxWinY ) );
    jpanel.setLayout( new GridLayout( 4, 1 ) );

    // Crea y anyade la zona de entrada de datos.
    tempPanel = new JPanel();
    tempPanel.setLayout( new FlowLayout() );
    tempPanel.add( new JLabel( "Numero a estudiar:" ) );
    txfNumero = new JTextField( "", 20 );
    tempPanel.add( txfNumero );
    jpanel.add( tempPanel );

    // Crea y anyade la zona de control (botones).
    tempPanel = new JPanel();
    tempPanel.setLayout( new FlowLayout() );

    btnPulsaAqui = new JButton( "Pulsa aqui" );
    btnPulsaAqui.addActionListener( new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          numVecesPulsado++;
          txfMensajes.setText( "Has pulsado " + numVecesPulsado + 
                               " veces el boton 'Pulsa aqui'" );
        }
      } 
    );
    tempPanel.add( btnPulsaAqui );

    btnComienzaCalculo = new JButton( "Comienza calculo" );
    btnComienzaCalculo.addActionListener( new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          if( txfNumero.getText().trim().length() == 0 ) {
            txfMensajes.setText( "Debes escribir un numero." );
          } else {
            try {
              long numero = Long.parseLong( txfNumero.getText().trim() );
              System.out.println( "Examinando numero: " + numero );
              
              Hebra h = new Hebra(numero);
              //h.setDaemon(true);
              h.start();
              
              /*boolean primo = esPrimo( numero );
              if( primo ) {
                System.out.println( "El numero " + numero + " SI es primo." );
              } else {
                System.out.println( "El numero " + numero + " NO es primo." );
              }*/
            } catch( NumberFormatException ex ) {
              txfMensajes.setText( "No es un numero correcto." );
            }
          }
        }
      } 
    );
    tempPanel.add( btnComienzaCalculo );

    jpanel.add( tempPanel );

    // Crea y anyade la zona de mensajes.
    tempPanel = new JPanel();
    tempPanel.setLayout( new FlowLayout() );
    tempPanel.add( new JLabel( "Mensajes: " ) );
    txfMensajes = new JTextField( "", 30 );
    tempPanel.add( txfMensajes );
    jpanel.add( tempPanel );

    // Crea e inserta el cuadro de texto de sugerencias.
    txfSugerencias = new JTextField( 40 );
    txfSugerencias.setEditable( false );
    txfSugerencias.setText( "321534781, 433494437, 780291637, 1405695061, 2971215073" );
    tempPanel = new JPanel();
    tempPanel.setLayout( new FlowLayout() );
    tempPanel.add( new JLabel( "Sugerencias: " ) );
    tempPanel.add( txfSugerencias );
    jpanel.add( tempPanel );

    // Fija caracteristicas del container.
    container.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    container.pack();
    container.setResizable( false );
    container.setVisible( true );

    System.out.println( "% End of routine: go.\n" );
  }

  // -------------------------------------------------------------------------
  static boolean esPrimo( long num ) {
    boolean primo;
    if( num < 2 ) {
      primo = false;
    } else {
      primo = true;
      long i = 2;
      while( ( i < num )&&( primo ) ) { 
        primo = ( num % i != 0 );
        i++;
      }
    }
    return( primo );
  }
}
