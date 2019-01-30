package practica3;

import java.util.concurrent.atomic.AtomicInteger;

// ===========================================================================
public class EjemploMuestraPrimosEnVector2a {
// ===========================================================================

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    int     numHebras;
    long    t1, t2;
    double  tt, tc, tb, td;
    AtomicInteger indice = new AtomicInteger();
    /*long    vectorNumeros[] = {
                200000033L, 200000039L, 200000051L, 200000069L, 
                200000081L, 200000083L, 200000089L, 200000093L, 
                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L
            };*/
     long    vectorNumeros[] = {
                 200000033L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
                 200000039L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
                 200000051L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
                 200000069L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
                 200000081L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
                 200000083L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
                 200000089L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
                 200000093L, 4L, 4L, 4L, 4L, 4L, 4L, 4L
             };

    /*
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
    */
    
    
    if(args.length != 1) {
		System.err.println("ERROR: número de argumentos inválido");
		System.exit(-1);
	}
	
	try {
		numHebras = Integer.parseInt(args[0]);
	}catch(NumberFormatException e) {
		numHebras = -1;
		System.err.println("ERROR: no son enteros");
		System.exit(-1);
	}

    //
    // Implementacion secuencial.
    //
    System.out.println( "" );
    System.out.println( "Implementacion secuencial." );
    t1 = System.nanoTime();
    for( int i = 0; i < vectorNumeros.length; i++ ) {
      if( esPrimo( vectorNumeros[ i ] ) ) {
        System.out.println( "  Encontrado primo: " + vectorNumeros[ i ] );
      }
    }
    t2 = System.nanoTime();
    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo secuencial (seg.):                    " + tt );
    
    //
    // Implementacion Paralela ciclica
    //
    
    System.out.println( "" );
    System.out.println( "Implementacion paralela ciclica." );
    
    t1 = System.nanoTime();
    HebraCiclica[] hebras1 = new HebraCiclica[numHebras];
	
	for(int i=0; i < numHebras; i++) {
		hebras1[i] = new HebraCiclica(i, vectorNumeros, numHebras);
		hebras1[i].start();
	}
	
	for(int i=0; i < numHebras; i++) {
		try {
			hebras1[i].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	t2 = System.nanoTime();
	tc = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
	System.out.println( "Tiempo paralela ciclica (seg.):                    " + tc );
	System.out.println( "Incremento paralela ciclica:                    " + tt/tc);
    
	
	//
    // Implementacion Paralela por bloques
    //
    
    System.out.println( "" );
    System.out.println( "Implementacion paralela por boques." );
    
    t1 = System.nanoTime();
    HebraBloque[] hebras2 = new HebraBloque[numHebras];
	
	for(int i=0; i < numHebras; i++) {
		hebras2[i] = new HebraBloque(i, vectorNumeros, numHebras);
		hebras2[i].start();
	}
	
	for(int i=0; i < numHebras; i++) {
		try {
			hebras2[i].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	t2 = System.nanoTime();
	tb = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
	System.out.println( "Tiempo paralela por bloques (seg.):                    " + tb );
	System.out.println( "Incremento paralela bloques:                    " + tt/tb);
	
	//
    // Implementacion Paralela dinamica
    //
    
    System.out.println( "" );
    System.out.println( "Implementacion paralela dinamica." );
    
    t1 = System.nanoTime();
    HebraDinamica[] hebras3 = new HebraDinamica[numHebras];
	
	for(int i=0; i < numHebras; i++) {
		hebras3[i] = new HebraDinamica(i, vectorNumeros, indice);
		hebras3[i].start();
	}
	
	for(int i=0; i < numHebras; i++) {
		try {
			hebras3[i].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	t2 = System.nanoTime();
	td = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
	System.out.println( "Tiempo paralela dinamica (seg.):                    " + td );
	System.out.println( "Incremento paralela dinamica:                    " + tt/td);
    
    
    
  }
  
  //-----------------------------------------------------------------------------
  	
  public static class HebraCiclica extends Thread{
		int id;
		long vector[];	
		int nHebras;
		
		public HebraCiclica(int idHebra, long vector[], int nHebras) {
			this.id = idHebra;
			this.vector=vector;
			this.nHebras=nHebras;
		}
		
		public void run() {
			int iniElem = id;
			int finElem = vector.length;
			for(int i=iniElem; i < finElem; i+=nHebras) {
				if(esPrimo(vector[i])) {
					System.out.println("Encontrado primo: "+vector[i]);
				}
				
			}
			
		}
		
	}
  
  public static class HebraBloque extends Thread{
		int id;
		long vector[];	
		int nHebras;
		
		public HebraBloque(int idHebra, long vector[], int nHebras) {
			this.id = idHebra;
			this.vector=vector;
			this.nHebras=nHebras;
		}
		
		public void run() {
			int tamanyo  = (vector.length-1)/nHebras;
			int iniElem = tamanyo*id;			
			int finElem = Math.min(iniElem+tamanyo, vector.length);
			for(int i=iniElem; i < finElem; i++) {
				if(esPrimo(vector[i])) {
					System.out.println("Encontrado primo: "+vector[i]);
				}
			}
			
		}
		
	}
  
  
  public static class HebraDinamica extends Thread{
		int id;
		long vector[];	
		AtomicInteger indice;
		
		public HebraDinamica(int idHebra, long vector[], AtomicInteger indice) {
			this.id = idHebra;
			this.vector=vector;
			this.indice=indice;
		}
		
		public void run() {
			int i = indice.getAndIncrement();
			while(i<vector.length) {
				if(esPrimo(vector[i])) {
					System.out.println("Encontrado primo: "+vector[i]);
				}
				i=indice.getAndIncrement();
			}
		}
		
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

