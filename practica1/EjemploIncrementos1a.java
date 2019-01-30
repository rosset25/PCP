package practica1;
// ============================================================================
class CuentaIncrementos1a {
// ============================================================================
  long contador = 0;

  // --------------------------------------------------------------------------
  void incrementaContador() {
    contador++;
  }

  // --------------------------------------------------------------------------
  long dameContador() {
    return( contador );
  }
}

class Hebra extends Thread{
	
	int id;
	CuentaIncrementos1a cont;

	
	public Hebra(int idHilo, CuentaIncrementos1a contador) {
		id = idHilo;
		cont = contador;
	}
	
	public void run() {
		System.out.println("soy hebra: "+ id + ", empiezo a incrementar");
		
		for(int i=0; i < 1000000; i++) {
			cont.incrementaContador();
		}
		
		System.out.println("soy hebra: "+ id + ", he terminado");
	}

}


// ============================================================================
class EjemploIncrementos1a {
// ============================================================================

	

  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
    int  numHebras;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 1 ) {
      System.err.println( "Uso: java programa <numHebras>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }
    
    System.out.println( "numHebras: " + numHebras );
    
    CuentaIncrementos1a contador = new CuentaIncrementos1a();
    
    
    System.out.println("contador inicial: "+ contador.dameContador());
    
    Hebra[] hebras = new Hebra[numHebras];
    
    for(int i=0; i < numHebras; i++) {
    	hebras[i] = new Hebra(i, contador);
    	hebras[i].start();
    }
    
    for(int i=0; i < numHebras; i++) {
    	try {
			hebras[i].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    
    System.out.println("contador final: "+ contador.dameContador());
  
  }
}

