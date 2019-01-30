package practica4;
// ===========================================================================
class Acumula {
// ===========================================================================
  double  suma;

  // -------------------------------------------------------------------------
  Acumula() {
    suma = 0.0;
  }

  // -------------------------------------------------------------------------
  synchronized void acumulaDato( double dato ) {
    suma = suma + dato;
  }

  // -------------------------------------------------------------------------
  synchronized double dameDato() {
    return suma;
  }
}

// ===========================================================================
class MiHebraMultAcumulaciones1a extends Thread {
// ===========================================================================
  int      miId, numHebras;
  long     numRectangulos;
  Acumula  a;

  // -------------------------------------------------------------------------
  MiHebraMultAcumulaciones1a( int miId, int numHebras, long numRectangulos, 
                              Acumula a ) {
	  this.miId = miId;
	  this.numHebras = numHebras;
	  this.numRectangulos = numRectangulos;
	  this.a = a;
  }

  // -------------------------------------------------------------------------
  public void run() {
    
	  double x;
	  double baseRectangulo = 1.0/((double) numRectangulos);
	  
	  for(int i = miId; i < numRectangulos; i+=numHebras) {
		  x = baseRectangulo*(((double) i) + 0.5);
		  a.acumulaDato(EjemploNumeroPI1a.f(x));
	  }
	  
  }
}


//===========================================================================
class MiHebraUnaAcumulacion1a extends Thread {
//===========================================================================
	int      miId, numHebras;
	long     numRectangulos;
	Acumula  a;
	
	// -------------------------------------------------------------------------
	MiHebraUnaAcumulacion1a( int miId, int numHebras, long numRectangulos, 
	                           Acumula a ) {
		  this.miId = miId;
		  this.numHebras = numHebras;
		  this.numRectangulos = numRectangulos;
		  this.a = a;
	}
	
	// -------------------------------------------------------------------------
	public void run() {
	 
		  double x;
		  double baseRectangulo = 1.0/((double) numRectangulos);
		  double suma = 0.0;
		  
		  for(int i = miId; i < numRectangulos; i+=numHebras) {
			  x = baseRectangulo*(((double) i) + 0.5);
			  suma += EjemploNumeroPI1a.f(x);
		  }
		  
		  a.acumulaDato(suma);
	}
}

// ===========================================================================
class EjemploNumeroPI1a {
// ===========================================================================

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    long                        numRectangulos;
    double                      baseRectangulo, x, suma, pi;
    int                         numHebras;
    MiHebraMultAcumulaciones1a  vt[];
    MiHebraUnaAcumulacion1a  vt2[];
    Acumula                     a;
    long                        t1, t2;
    double                      tSec, tPar, tPar2;

    // Comprobacion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.out.println( "ERROR: numero de argumentos incorrecto.");
      System.out.println( "Uso: java programa <numHebras> <numRectangulos>" );
      System.exit( -1 );
    }
    try {
      numHebras      = Integer.parseInt( args[ 0 ] );
      numRectangulos = Long.parseLong( args[ 1 ] );
    } catch( NumberFormatException ex ) {
      numHebras      = -1;
      numRectangulos = -1;
      System.out.println( "ERROR: Numeros de entrada incorrectos." );
      System.exit( -1 );
    }

    System.out.println();
    System.out.println( "Calculo del numero PI mediante integracion." );

    //
    // Calculo del numero PI de forma secuencial.
    //
    System.out.println();
    System.out.println( "Comienzo del calculo secuencial." );
    t1 = System.nanoTime();
    baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    suma           = 0.0;
    for( long i = 0; i < numRectangulos; i++ ) {
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      suma += f( x );
    }
    pi = baseRectangulo * suma;
    t2 = System.nanoTime();
    tSec = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Version Secuencial. Numero PI: " + pi );
    System.out.println( "Tiempo transcurrido (s.):      " + tSec );

    //
    // Calculo del numero PI de forma paralela: 
    // Multiples acumulaciones por hebra.
    System.out.println();
    System.out.println( "Comienzo del calculo paralelo multiples acumulaciones." );
    t1 = System.nanoTime();
    
    a = new Acumula();
    
    vt = new MiHebraMultAcumulaciones1a[numHebras];
    for(int i=0; i < numHebras; i++) {
    	vt[i] = new MiHebraMultAcumulaciones1a(i, numHebras, numRectangulos, a);
    	vt[i].start();
    }
    
    for(int i=0; i < numHebras; i++) {
    	try {
			vt[i].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    pi = baseRectangulo * a.dameDato();
    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Version Paralela multAcum. Numero PI: " + pi );
    System.out.println( "Tiempo transcurrido (parl.Mult):      " + tPar );
    System.out.println( "Incremento (parl.Mult):      " + tSec/tPar );

    
    
    //
    // Calculo del numero PI de forma paralela: 
    // Una acumulacion por hebra.
    System.out.println();
    System.out.println( "Comienzo del calculo paralelo una acumulacion." );
    t1 = System.nanoTime();
    
    a = new Acumula();
    
    vt2 = new MiHebraUnaAcumulacion1a[numHebras];
    for(int i=0; i < numHebras; i++) {
    	vt2[i] = new MiHebraUnaAcumulacion1a(i, numHebras, numRectangulos, a);
    	vt2[i].start();
    }
    
    for(int i=0; i < numHebras; i++) {
    	try {
			vt2[i].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    pi = baseRectangulo * a.dameDato();
    t2 = System.nanoTime();
    tPar2 = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Version Paralela UnaAcum. Numero PI: " + pi );
    System.out.println( "Tiempo transcurrido (parl.Una):      " + tPar2 );
    System.out.println( "Incremento (parl.Una):      " + tSec/tPar2 );

    System.out.println();
    System.out.println( "Fin de programa." );
  }

    

  
  
  
  // -------------------------------------------------------------------------
  static double f( double x ) {
    return ( 4.0/( 1.0 + x*x ) );
  }
}

