package practica5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

// ============================================================================
public class GUITiroAlBlanco2a {
  // ============================================================================
  // Declaracion de constantes.
  static final int  tamMarco  = 10;
  static final int  maxWinX   = 600 + 2 * tamMarco;
  static final int  maxWinY   = 400 + 2 * tamMarco;

  // Declaracion de variables.
  JFrame             container;
  JPanel             jpanel;
  JTextField         txfMensajes;
  CanvasCampoTiro2a cnvCampoTiro;
  JTextField         txfVelocidadInicial;
  JTextField         txfAnguloInicial;
  JButton            btnDispara;

  //TODO Revisar esto
  LinkedBlockingDeque<NuevoDisparo> zonaIntercambio;

  // --------------------------------------------------------------------------
  public static void main(String args[]) {
    GUITiroAlBlanco2a gui = new GUITiroAlBlanco2a();
    gui.go();
  }

  // --------------------------------------------------------------------------
  public void go() {
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        // Declaracion de variables locales.
        JPanel  controles;

        // Crea el JFrame principal.
        container = new JFrame( "GUI Tiro Al Blanco 2a" );

        // Consigue el panel principal del Frame "container".
        jpanel = ( JPanel ) container.getContentPane();
        jpanel.setPreferredSize( new Dimension( maxWinX, maxWinY ) );

        jpanel.setLayout( new BorderLayout() );

        // Crea y anyade el campo de mensajes.
        txfMensajes = new JTextField();
        jpanel.add( "North",  txfMensajes );

        // Crea y anyade el canvas para el campo de tiro.
        cnvCampoTiro = new CanvasCampoTiro2a();
        jpanel.add( "Center", cnvCampoTiro );

        // Crea y anyade el panel con los controles de disparo.
        controles = new JPanel();
        controles.setLayout( new FlowLayout() );
        controles.add( new JLabel( " Velocidad:" ) );
        txfVelocidadInicial  = new JTextField(
                                              new Double(
                                                         Math.round( 5000.0 + Math.random() * 1000.0 )
                                                         ).toString(), 6 );
        controles.add( txfVelocidadInicial );

        controles.add( new JLabel( " Angulo:" ) );
        txfAnguloInicial = new JTextField(
                                          new Double(
                                                     Math.round( 40.0 + Math.random() * 10.0 )
                                                     ).toString(), 6 );
        controles.add( txfAnguloInicial );
        btnDispara = new JButton( "Dispara" );



      zonaIntercambio = new LinkedBlockingDeque<>();
      MiHebraCalculadoraVariosDisparos hebraDisparos = new MiHebraCalculadoraVariosDisparos(cnvCampoTiro,txfMensajes,zonaIntercambio);
      hebraDisparos.start();



        // Anyade un codigo para procesar el evento del boton "Dispara".
        btnDispara.addActionListener( new ActionListener() {
          public void actionPerformed( ActionEvent e ) {
            //String mensaje;
            try {
              double velocidad = Double.parseDouble(
                                                    txfVelocidadInicial.getText().trim() ) / 100.0;
              double angulo = Double.parseDouble(
                                                 txfAnguloInicial.getText().trim() );
              if( ( 0.0 < angulo )&&( angulo < 90 )&&( velocidad > 0 ) ) {
                txfMensajes.setText( "Calculando y dibujando nueva trayectoria" );
                NuevoDisparo disparo = new NuevoDisparo(velocidad,angulo);
                zonaIntercambio.put(disparo);

              } else {
                txfMensajes.setText( "Error: Datos incorrectos." );
              }
            } catch( NumberFormatException ex ) {
              txfMensajes.setText( "No son numeros correctos." );
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
          }
        } );
        controles.add( btnDispara );
        jpanel.add( "South",  controles );

        // Fija caracteristicas del container.
        container.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        container.pack();
        container.setResizable( false );
        container.setVisible( true );
      }
    });
  }
}

// ============================================================================
class CanvasCampoTiro2a extends Canvas {
  // ============================================================================
  // Declaration of variables.
  static final int  tamProyectil             = 3;
  static final int  tamObjetivoX             = 20;
  static final int  tamObjetivoY             = 10;
  static final int  tamCanyonX               = 20;
  static final int  tamCanyonY               = 10;
  volatile boolean  targetAndDimsInitialized = false;
  volatile int      objetivoX, objetivoY, maxDimX, maxDimY;

  // --------------------------------------------------------------------------
  public void paint( Graphics g ) {
    double    mitad;
    Rectangle r = getBounds();

    // Comprueba si el objeto no ha sido inicializado.
    if( ! targetAndDimsInitialized ) {
      targetAndDimsInitialized = true;

      // Inicializa las dimensiones maximas.
      maxDimX = r.width;
      maxDimY = r.height;
      mitad = ( ( double ) ( maxDimX - 1 ) )/ 2.0;
      // Inicializa la posicion del objetivo.
      objetivoX = Math.max(
                           1,
                           ( int ) ( mitad +
                                    Math.round( Math.random() * mitad ) ) );
      objetivoY = 0;
    }

    // Fija el color de fodo.
    this.setBackground( Color.gray );

    // Dibuja el rectangulo principal.
    g.setColor( Color.black );
    g.drawRect( 0, 0, maxDimX - 1, maxDimY - 1 );

    // Dibuja la posicion del canyon.
    g.setColor( Color.black );
    g.fillRect( gCoorX( 0 )-10, gCoorY( 0 )-5,
               tamCanyonX, tamCanyonY );

    // Dibuja la posicion del objetivo.
    g.setColor( Color.green );
    g.fillRect( gCoorX( objetivoX )-10, gCoorY( objetivoY )-5,
               tamObjetivoX, tamObjetivoY );
  }

  // --------------------------------------------------------------------------
  public void dibujaProyectil( int x, int y ) {

      final Graphics g = this.getGraphics();


      SwingUtilities.invokeLater(new Runnable() {
                                     @Override
                                     public void run() {
                                         // Dibuja el proyectil.
                                         if( ( 0 <= x )&&( x < maxDimX )&&( 0 <= y )&&( y < maxDimY ) ) {
                                             g.setColor( Color.red );
                                             g.fillOval( gCoorX( x ), gCoorY( y ), tamProyectil, tamProyectil );
                                         }
                                     }
                                 }

      );

      // Dibuja el proyectil.


  }

  // --------------------------------------------------------------------------
  public void borraProyectil( int x, int y ) {
    Graphics g = this.getGraphics();
    if( ( 0 <= x )&&( x < maxDimX )&&( 0 <= y )&&( y < maxDimY ) ) {
      g.setColor( Color.white );
      g.fillOval( gCoorX( x ), gCoorY( y ), tamProyectil, tamProyectil );
    }
  }

  // --------------------------------------------------------------------------
  int getObjetivoX() {
    if( targetAndDimsInitialized ) {
      return objetivoX;
    } else {
      return -1;
    }
  }

  // --------------------------------------------------------------------------
  int getObjetivoY() {
    if( targetAndDimsInitialized ) {
      return objetivoY;
    } else {
      return -1;
    }
  }

  // --------------------------------------------------------------------------
  int gCoorX( int x ) {
    return Math.max( 0, Math.min( maxDimX - 1, x ) );
  }

  // --------------------------------------------------------------------------
  int gCoorY( int y ) {
    return Math.max( 0, Math.min( maxDimY - 1, ( maxDimY - 1 - y ) ) );
  }
}

// ============================================================================
class Proyectil2a {
  // ============================================================================
  // Declaracion de constantes.
  static final double  GRAVITY = 9.8;
  static final double  TO_RAD  = ( 2.0 * Math.PI ) / 360.0;
  static final double  DELTA_T = 5.0E-3;

  // Declaracion de variables.
  int     estadoProyectil  = 0;
  double  anguloRad        = 0.0;
  double  velX             = 0.0;
  double  velY             = 0.0;
  double  posX             = 0.0;
  double  posY             = 0.0;
  int     intPosX          = 0;
  int     intPosY          = 0;
  int     intPosXOld       = 0;
  int     intPosYOld       = 0;

  // --------------------------------------------------------------------------
  Proyectil2a(double velocidadInicial, double anguloInicial ) {
    this.anguloRad = anguloInicial * TO_RAD;
    this.velX      = Math.cos( anguloRad ) * velocidadInicial;
    this.velY      = Math.sin( anguloRad ) * velocidadInicial;
  }

  // --------------------------------------------------------------------------
  void mueveDuranteUnIncremental( int objetivoX, int objetivoY ) {
    // Mueve el proyectil solo si no ha impactado.
    // Actualiza su estado considerando que el objetivo esta en los parametros.
    if( this.estadoProyectil == 0 ) {
      duermeUnPoco();

      // Actualiza el espacio y la velocidad.
      this.posX += velX * DELTA_T;
      this.posY += velY * DELTA_T;
      //// velX = velX; This speed does not change.
      this.velY = velY - GRAVITY * DELTA_T;

      // Guarda la anterior posicion grafica.
      this.intPosXOld = intPosX;
      this.intPosYOld = intPosY;

      // Calcula la nueva posicion grafica.
      this.intPosX = ( int ) posX;
      this.intPosY = ( int ) posY;

      // Fija el estado del proyectil con la nueva posicion.
      setEstadoProyectil( objetivoX, objetivoY );
    }
  }

  // --------------------------------------------------------------------------
  void muestra() {
    System.out.format( "  Estado: %1d  " + "Pos:( %6.2f %6.2f )" +
                      " Vel:( %6.2f %6.2f )" + " IntPos:( %4d %4d )%n",
                      this.estadoProyectil, this.posX, this.posY,
                      this.velX, this.velY, this.intPosX, this.intPosY );
  }

  // --------------------------------------------------------------------------
  public void dibujaProyectil( final CanvasCampoTiro2a cnvCampoTiro ) {
    // Dibuja la nueva posicion del proyectil, pero solo si la nueva
    // posicion es distinta de la anterior.
    if( ( this.intPosX != this.intPosXOld )||
       ( this.intPosY != this.intPosYOld ) ) {

      // Borra la posicion anterior.
      cnvCampoTiro.borraProyectil( this.intPosXOld, this.intPosYOld );

      // Dibuja la nueva posicion del proyectil.
      cnvCampoTiro.dibujaProyectil( this.intPosX, this.intPosY );
    }
  }

  // --------------------------------------------------------------------------
  void setEstadoProyectil( int objetivoX, int objetivoY ) {
    // Modifica el estado solo si esta en vuelo.
    if( this.estadoProyectil == 0 ) {
      if ( ( this.intPosX == objetivoX )&&( this.intPosY == objetivoY ) ) {
        // Impactado contra el suelo: SI ha acertado.
        this.estadoProyectil = 2;
      } else if( ( this.intPosY <= 0 )&&( this.velY < 0.0 ) ) {
        // Impactado contra el suelo: NO ha acertado.
        this.estadoProyectil = 1;
      }
    }
  }

  // --------------------------------------------------------------------------
  int getEstadoProyectil() {
    return estadoProyectil;
  }

  // --------------------------------------------------------------------------
  int getIntPosX() {
    return intPosX;
  }

  // --------------------------------------------------------------------------
  int getIntPosY() {
    return intPosY;
  }

  // --------------------------------------------------------------------------
  int getIntPosXOld() {
    return intPosXOld;
  }

  // --------------------------------------------------------------------------
  int getIntPosYOld() {
    return intPosYOld;
  }

  // --------------------------------------------------------------------------
  static void duermeUnPoco() {
    try {
      Thread.sleep( 1L );
    } catch( InterruptedException ex ) {
      ex.printStackTrace();
    }
  }
}

class MiHebraCalculadoraVariosDisparos extends Thread {

    CanvasCampoTiro2a canvas;
    JTextField cuadroTexto;
    LinkedList<Proyectil2a> proyectiles;
    LinkedBlockingDeque<NuevoDisparo> zonaIntercambio;

    public MiHebraCalculadoraVariosDisparos(CanvasCampoTiro2a canvas, JTextField cuadroTexto, LinkedBlockingDeque<NuevoDisparo> zonaIntercambio) {
        this.canvas = canvas;
        this.cuadroTexto = cuadroTexto;
        this.proyectiles = new LinkedList<>();
        this.zonaIntercambio = zonaIntercambio;
    }

    @Override
    public void run() {

        while (true) {
            while (proyectiles.isEmpty() || !zonaIntercambio.isEmpty()) {
                try {
                    NuevoDisparo disparo = zonaIntercambio.take();
                    Proyectil2a proyectil = new Proyectil2a(disparo.velocidad, disparo.angulo);
                    proyectiles.add(proyectil);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Proyectil2a p = zonaIntercambio.take();
            }

            for(int i =0;i<proyectiles.size(); i++) {
               Proyectil2a p = proyectiles.get(i);
                p.mueveDuranteUnIncremental(canvas.getObjetivoX(),
                        canvas.getObjetivoY());

                int estado = p.getEstadoProyectil();
                // Comprueba si el proyectil ha impactado contra el suelo o no.


                final int estado2 = estado;

                // Muestra en pantalla los datos del proyectil p.
                p.muestra();
                p.dibujaProyectil(canvas);


                if (estado != 0) {
                    String mensaje;

                    proyectiles.remove(p);
                    i--;
                    if (estado2 == 2) {
                        mensaje = "Destruido!";
                    } else {
                        mensaje = "Fallado. El objetivo esta en: " +
                                canvas.getObjetivoX() +
                                "  Has disparado a: " + p.getIntPosX();
                    }


                    final String mensaje2 = mensaje;
                    SwingUtilities.invokeLater(new Runnable() {
                                                   @Override
                                                   public void run() {

                                                       cuadroTexto.setText(mensaje2);
                                                   }

                                               }

                    );

                    // El proyectil ha impactado contra el suelo.
                    // Construye y muestra mensaje adecuado.


                }



            }

        }

    }
}
class NuevoDisparo {

  double velocidad;
  double angulo;

  public NuevoDisparo(double velocidad, double angulo) {
    this.velocidad = velocidad;
    this.angulo = angulo;
  }


}