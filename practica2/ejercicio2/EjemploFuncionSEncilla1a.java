package practica2.ejercicio2;

//============================================================================
class EjemploFuncionSEncilla1a {
// ============================================================================

  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
    int     n, numHebras;
    long    t1, t2;
    double  tt, tc, tb, sumaX, sumaY;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <tamanyo>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
      n         = Integer.parseInt( args[ 1 ] );
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      n         = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }
    
    

    // Crea los vectores.
    double vectorX[] = new double[ n ];
    double vectorY[] = new double[ n ];

    //
    // Implementacion secuencial.
    //
    inicializaVectorX( vectorX );
    inicializaVectorY( vectorY );
    
    System.out.println( "Parte secuencial." );
    t1 = System.nanoTime();
    for( int i = 0; i < n; i++ ) {
      vectorY[ i ] = evaluaFuncion( vectorX[ i ] );
    }
    t2 = System.nanoTime();
    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo secuencial (seg.):                    " + tt );

    // Comprueba el resultado. 
    sumaX = sumaVector( vectorX );
    sumaY = sumaVector( vectorY );
    System.out.println( "Suma del vector X:          " + sumaX );
    System.out.println( "Suma del vector Y:          " + sumaY );
    
    
    
    
    inicializaVectorY( vectorY );
    
    //Parte paralela cíclica
    System.out.println( "Parte paralela." );
 // Crea las hebras (parte añadida)
    
    
    t1 = System.nanoTime();
    HebraCiclica[] hebras = new HebraCiclica[numHebras];
    for( int i = 0; i < numHebras; i++ ) {
     hebras[i] = new HebraCiclica(i, numHebras, vectorX, vectorY);
     hebras[i].start();
    }
    
    
    
    for(int i=0; i < numHebras; i++) {
    	try {
			hebras[i].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    t2 = System.nanoTime();
    tc = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    
    System.out.println( "Tiempo paralela (seg.):                    " + tt/tc );
    //// imprimeResultado( vectorX, vectorY );
    
    
    
    inicializaVectorY( vectorY );
    
    //Parte paralela por bloques
    System.out.println( "Parte paralela por bloques." );
    
    
    t1 = System.nanoTime();
    HebraBloque[] hebrasBloque = new HebraBloque[numHebras];
    for( int i = 0; i < numHebras; i++ ) {
     hebrasBloque[i] = new HebraBloque(i, numHebras, vectorX, vectorY, n);
     hebrasBloque[i].start();
    }
    
    
    
    for(int i=0; i < numHebras; i++) {
    	try {
			hebrasBloque[i].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    t2 = System.nanoTime();
    tb = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    
    System.out.println( "Tiempo paralela (seg.):                    " + tt/tb );
    //// imprimeResultado( vectorX, vectorY );
    
    
    
    // Comprueba el resultado. 
    sumaX = sumaVector( vectorX );
    sumaY = sumaVector( vectorY );
    System.out.println( "Suma del vector X:          " + sumaX );
    System.out.println( "Suma del vector Y:          " + sumaY );


    System.out.println( "Fin programa." );
    
    
  }

  // -------------------------------------------------------------------------
  
  public static class HebraCiclica extends Thread{
	  int id;
	  int numHebras;
	  double[] vectorX;
	  double[] vectorY;
	  
	  public HebraCiclica(int id, int numHebras, double[] vectorX, double[] vectorY) {
		  this.id = id;
		  this.numHebras = numHebras;
		  this.vectorX = vectorX;
		  this.vectorY = vectorY;
	  }
	  
	  public void run() {
		  for(int i=id; i < vectorX.length; i+=numHebras) {
			 vectorY[ i ] = evaluaFuncion( vectorX[ i ] );
		  }
		  
		  
	  }
  }
  
  

  public static class HebraBloque extends Thread{
	  int id;
	  int numHebras;
	  double[] vectorX;
	  double[] vectorY;
	  int  n;
	  
	  public HebraBloque(int id, int numHebras, double[] vectorX, double[] vectorY, int n) {
		  this.id = id;
		  this.numHebras = numHebras;
		  this.vectorX = vectorX;
		  this.vectorY = vectorY;
		  this.n =n;
	  }
	  
	  public void run() {
		  for(int i=id; i < vectorX.length; i+=numHebras) {
			 vectorY[ i ] = evaluaFuncion( vectorX[ i ] );
		  }
		  
		  
	  }
  }
  
  
  
  // --------------------------------------------------------------------------
  static void inicializaVectorX( double vectorX[] ) {
    if( vectorX.length == 1 ) {
      vectorX[ 0 ] = 0.0;
    } else {
      for( int i = 0; i < vectorX.length; i++ ) {
        vectorX[ i ] = 10.0 * ( double ) i / ( ( double ) vectorX.length - 1 );
      }
    }
  }

  // --------------------------------------------------------------------------
  static void inicializaVectorY( double vectorY[] ) {
    for( int i = 0; i < vectorY.length; i++ ) {
      vectorY[ i ] = 0.0;
    }
  }

  // --------------------------------------------------------------------------
  static double sumaVector( double vector[] ) {
    double  suma = 0.0;
    for( int i = 0; i < vector.length; i++ ) {
      suma += vector[ i ];
    }
    return suma;
  }

  // --------------------------------------------------------------------------
  static double evaluaFuncion( double x ) {
	  return 2.5*x;
	  }

  // --------------------------------------------------------------------------
  static void imprimeVector( double vector[] ) {
    for( int i = 0; i < vector.length; i++ ) {
      System.out.println( " vector[ " + i + " ] = " + vector[ i ] );
    }
  }

  // --------------------------------------------------------------------------
  static void imprimeResultado( double vectorX[], double vectorY[] ) {
    for( int i = 0; i < Math.min( vectorX.length, vectorY.length ); i++ ) {
      System.out.println( "  i: " + i + 
                          "  x: " + vectorX[ i ] +
                          "  y: " + vectorY[ i ] );
    }
  }

}

