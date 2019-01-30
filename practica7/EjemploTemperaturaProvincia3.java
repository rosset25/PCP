package practica7;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class EjemploTemperaturaProvincia3 {
  public static void main(String[] args) {
    int                numHebras, codProvincia, desp;
    long               t1, t2, tt[];
    double             ts, tp;
    PuebloMaximaMinima3 MaxMin;
    

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 3 ) {
      System.out.println( "ERROR: numero de argumentos incorrecto.");
      System.out.println( "Uso: java programa <numHebras> <provincia> <desplazamiento>" );
      System.exit( -1 );
    }
    try {
      numHebras    = Integer.parseInt( args[ 0 ] );
      codProvincia = Integer.parseInt( args[ 1 ] );
      desp         = Integer.parseInt( args[ 2 ] );
    } catch( NumberFormatException ex ) {
      numHebras    = -1;
      codProvincia = -1;
      desp         = -1;
      System.out.println( "ERROR: Numero de entrada incorrecto." );
      System.exit( -1 );
    }
    if (numHebras <= 0) {
      System.out.println( "ERROR: El numero de Hebras debe ser un numero entero mayor que 0." );
      System.exit( -1 );
    }
    if ((codProvincia < 1) || (codProvincia > 50)) {
      System.out.println( "ERROR: El codigo de la provincia debe ser un numero entero " +
                          "comprendido entre 1 y 50." );
      System.exit( -1 );
    }
    if ((desp < 0) || (desp > 6)) {
      System.out.println( "ERROR: El desplazamiento debe ser un numero entero comprendido " +
                          "entre 0 y 6." );
      System.exit( -1 );
    }

    System.out.println();
    System.out.println( "Obtiene el pueblo de una provincia con mayor diferencia " +
                        "de temperatura." );

    // Seleccion del dia elegido
    String fecha;
    Calendar c = Calendar.getInstance();
    Integer dia, mes, anyo;

    c.add(Calendar.DAY_OF_MONTH, desp);
    dia = c.get(Calendar.DATE);
    mes = c.get(Calendar.MONTH) + 1;
    anyo = c.get(Calendar.YEAR);

    fecha = String.format("%02d", anyo) + "-" + String.format("%02d", mes) + "-" +
            String.format("%02d", dia);
    System.out.println(fecha);

    //
    // Implementacion secuencial sin temporizar.
    //
    /*MaxMin = new PuebloMaximaMinima2();
    obtenMayorDiferencia_SecuencialAFichero (fecha, codProvincia, MaxMin);
    System.out.println( "  Pueblo: " + MaxMin.damePueblo() + " , Maxima = " +
                        MaxMin.dameTemperaturaMaxima() + " , Minima = " +
                        MaxMin.dameTemperaturaMinima() );
*/
    //
    // Implementacion secuencial.
    //
    System.out.println();
    t1 = System.nanoTime();
    MaxMin = new PuebloMaximaMinima3();
    obtenMayorDiferencia_SecuencialDeFichero3(fecha, codProvincia, MaxMin);
    t2 = System.nanoTime();
    ts = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion secuencial.                           " );
    System.out.println( " Tiempo(s): " + ts );
    System.out.println( "  Pueblo: " + MaxMin.damePueblo() + " , Maxima = " +
                           MaxMin.dameTemperaturaMaxima() + " , Minima = " +
                           MaxMin.dameTemperaturaMinima() );

    //
    // Implementacion paralela.
    //
    System.out.println();
    t1 = System.nanoTime();
//    Hebra1[] hebras = new Hebra1[numHebras];
//    LinkedBlockingQueue<Tarea> colaTareas = new LinkedBlockingQueue<Tarea>();
//   
//    for(int i = 0 ;i<numHebras;i++) {
//        hebras[i]=new Hebra1(fecha, colaTareas, MaxMin);
//        hebras[i].start();
//    }
    
    obtenMayorDiferencia_ParaleloDeFichero3 (numHebras, fecha, MaxMin);
    
    
//    for(int i = 0 ;i<numHebras;i++) {
//        try {
//                        hebras[i].join();
//                } catch (InterruptedException e) {
//                        e.printStackTrace();
//                }
//    }

    t2 = System.nanoTime();
    tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;

    System.out.print( "Implementacion paralela.                           " );
    System.out.println( " Tiempo(s): " + tp );
    System.out.println( "  Pueblo: " + MaxMin.damePueblo() + " , Maxima = " +
                           MaxMin.dameTemperaturaMaxima() + " , Minima = " +
                           MaxMin.dameTemperaturaMinima() );

    System.out.println("Incremento paralelo: " + ts/tp);

  }

  // --------------------------------------------------------------------------
  public static void obtenMayorDiferencia_SecuencialAFichero3(String fecha, int codProvincia,
                                                              PuebloMaximaMinima3 MaxMin) {
    FileWriter  fichero = null;
    PrintWriter pw      = null;

    // Verifica todas los codigos de pueblos y escribe el fichero  "codPueblos.txt"
    try
    {
      // Apertura del fichero y creacion de FileWriter para poder
      // hacer una escritura comoda (disponer del metodo println()).
      fichero = new FileWriter("codPueblos.txt");
      pw = new PrintWriter(fichero);

      for (int i=codProvincia*1000; i<(codProvincia+1)*1000; i++){
        if (ProcesaPueblo3(fecha, i, MaxMin, false) == true) {
          pw.println(i);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        // Se aprovecha el finally para asegurar el cierre del fichero,
        // tanto si todo va bien como si salta una excepcion.
        if (null != fichero)
          fichero.close();
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }
  }


  // --------------------------------------------------------------------------

  public static void obtenMayorDiferencia_SecuencialDeFichero3 (String fecha, int codProvincia,
                                                               PuebloMaximaMinima3 maxMin) {
    File           archivo = null;
    FileReader     fr      = null;
    BufferedReader br      = null;

    // Procesa el fichero "codPueblos.txt"
    try
    {
      // Apertura del fichero y creacion de BufferedReader para poder
      // hacer una lectura comoda (disponer del metodo readLine()).
      archivo = new File ("codPueblos.txt");
      fr = new FileReader (archivo);
      br = new BufferedReader(fr);

      String linea;
      while( ( linea = br.readLine() ) != null ) {
        int codPueblo = Integer.parseInt(linea);
        ProcesaPueblo3(fecha, codPueblo, maxMin, false);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }finally{
      // Se aprovecha el finally para asegurar el cierre del fichero,
      // tanto si todo va bien como si salta una excepcion.
      try{
        if( null != fr ){
          fr.close();
        }
      }catch (Exception e2){
        e2.printStackTrace();
      }
    }
  }

  public static void obtenMayorDiferencia_ParaleloDeFichero3(int numHebras, String fecha, PuebloMaximaMinima3 maxMin) {
          File           archivo = null;
          FileReader     fr      = null;
          BufferedReader br      = null;

          System.out.println("proceso fichero");
          // Procesa el fichero "codPueblos.txt"
          try
          {
                  // Apertura del fichero y creacion de BufferedReader para poder
                  // hacer una lectura comoda (disponer del metodo readLine()).
                  archivo = new File ("codPueblos.txt");
                  fr = new FileReader (archivo);
                  br = new BufferedReader(fr);

                  //creamos el ThreadPool
                  ExecutorService exec =  Executors.newFixedThreadPool(numHebras);
                  
                  ArrayList < Future <PuebloMaximaMinima3> > alf = new ArrayList<>();
                  Future<PuebloMaximaMinima3> resultado;
                  
                  String linea;
                  while( ( linea = br.readLine() ) != null ) {
                          int codPueblo = Integer.parseInt(linea);
                          resultado= exec.submit(new Tarea3(codPueblo, fecha, maxMin));
                          alf.add(resultado);   
                  }
                  
                  for(int i=0; i < alf.size(); i++) {
                	  resultado = alf.get(i);
                	  PuebloMaximaMinima3 maxMin_local = resultado.get();
                	  maxMin.actualizaMaxMin(maxMin_local.damePueblo(), maxMin_local.dameCodigo(), maxMin_local.dameTemperaturaMaxima(), maxMin_local.dameTemperaturaMinima());  
                  }
                 
                  
                 

          } catch (Exception e) {
                  e.printStackTrace();
          }finally{
                  // Se aprovecha el finally para asegurar el cierre del fichero,
                  // tanto si todo va bien como si salta una excepcion.
                  try{
                          if( null != fr ){
                                  fr.close();
                          }
                  }catch (Exception e2){
                          e2.printStackTrace();
                  }
          }
  }
  
  // --------------------------------------------------------------------------
  
  public static boolean ProcesaPueblo3(String fecha, int codPueblo, PuebloMaximaMinima3 maxMin, boolean imprime) {
	    URL            url;
	    InputStream    is = null;
	    BufferedReader br;
	    String         line, poblacion = new String (), provincia = new String ();
	    int            state, num[]=new int[2];
	    boolean        res = false;

	    // Procesamiento de la informacion XML asociada a codPueblo
	    // Actualizacion de MaxMin de acuerdo a los valores obtenidos
	    try {
	      String urlStr = "http://www.aemet.es/xml/municipios/localidad_" +
	                      String.format("%05d",codPueblo)+ ".xml";
	      url = new URL(urlStr);
	      is  = url.openStream();  // throws an IOException
	      br  = new BufferedReader(new InputStreamReader(is));
	      
	      if (imprime) System.out.println(urlStr);
	
	      state = 0;
	      while (((line = br.readLine()) != null) && (state < 6)) {
	        //        System.out.println (line);
	        if ((state == 0) && (line.contains ("nombre"))) {
	          poblacion=line.split(">")[1].split("<")[0].split("/")[0];
	          state++;
	        } else if ((state == 1) && (line.contains ("provincia"))) {
	          provincia=line.split(">")[1].split("<")[0].split("/")[0];
	          state++;
	        } else if ((state == 2) && (line.contains (fecha))) {
	          state++;
	        } else if ((state == 3) && (line.contains ("temperatura"))) {
	          state++;
	        } else if ((state > 3) && ((line.contains ("maxima")) || (line.contains ("minima")))) {
	          num[state-4] = Integer.parseInt (line.split(">")[1].split("<")[0]);
	          state++;
	        }
	      }
	      // System.out.println("(" + codPueblo + ") " + poblacion + "(" + provincia + ") => " +
	        //                  "(" + num[0] + " , " + num[1] + ")");
	      maxMin.actualizaMaxMin (poblacion, codPueblo, num[0], num[1]);
	      res = true;
	    } catch (MalformedURLException mue) {
	      mue.printStackTrace();
	    } catch (IOException ioe) {
	      //      ioe.printStackTrace();
	    } finally {
	      try {
	        if (is != null) is.close();
	      } catch (IOException ioe) {
	        // nothing to see here
	      }
	    }
	    return res;
	  }

  // --------------------------------------------------------------------------
  public static PuebloMaximaMinima3 ProcesaPueblo3_paralelo(String fecha, int codPueblo, PuebloMaximaMinima3 maxMin) {
    URL            url;
    InputStream    is = null;
    BufferedReader br;
    String         line, poblacion = new String (), provincia = new String ();
    int            state, num[]=new int[2];
    PuebloMaximaMinima3 res;

    // Procesamiento de la informacion XML asociada a codPueblo
    // Actualizacion de MaxMin de acuerdo a los valores obtenidos
    try {
      String urlStr = "http://www.aemet.es/xml/municipios/localidad_" +
                      String.format("%05d",codPueblo)+ ".xml";
      url = new URL(urlStr);
      is  = url.openStream();  // throws an IOException
      br  = new BufferedReader(new InputStreamReader(is));
      //TODO: COGER
//      if (imprime) System.out.println(urlStr);
//
//      state = 0;
//      while (((line = br.readLine()) != null) && (state < 6)) {
//        //        System.out.println (line);
//        if ((state == 0) && (line.contains ("nombre"))) {
//          poblacion=line.split(">")[1].split("<")[0].split("/")[0];
//          state++;
//        } else if ((state == 1) && (line.contains ("provincia"))) {
//          provincia=line.split(">")[1].split("<")[0].split("/")[0];
//          state++;
//        } else if ((state == 2) && (line.contains (fecha))) {
//          state++;
//        } else if ((state == 3) && (line.contains ("temperatura"))) {
//          state++;
//        } else if ((state > 3) && ((line.contains ("maxima")) || (line.contains ("minima")))) {
//          num[state-4] = Integer.parseInt (line.split(">")[1].split("<")[0]);
//          state++;
//        }
//      }
      // System.out.println("(" + codPueblo + ") " + poblacion + "(" + provincia + ") => " +
      //                    "(" + num[0] + " , " + num[1] + ")");
      maxMin.actualizaMaxMin (poblacion, codPueblo, num[0], num[1]);

    } catch (MalformedURLException mue) {
      mue.printStackTrace();
    } catch (IOException ioe) {
      //      ioe.printStackTrace();
    } finally {
      try {
        if (is != null) is.close();
      } catch (IOException ioe) {
        // nothing to see here
      }
    }
    return maxMin;
  }
}

// ============================================================================
class PuebloMaximaMinima3 {
// ============================================================================
  String poblacion;
  int    codigo, max, min;


  // --------------------------------------------------------------------------
  public PuebloMaximaMinima3() {
    poblacion = null;
    codigo    = -1;
    max       = -1;
    min       = -1;
  }

  // --------------------------------------------------------------------------
  public void actualizaMaxMin( String poblacion, int codigo, int max, int min ) {
    if ((this.poblacion == null) || ((this.max-this.min) < (max-min)) ||
        (((this.max-this.min) == (max-min)) && (this.min > min)) ||
        (((this.max-this.min) == (max-min)) && (this.min == min) && (this.codigo > codigo))
        ) {
      //      (((this.max-this.min) == (max-min)) && (this.max < max))) {
      this.poblacion = poblacion;
      this.codigo = codigo;
      this.max = max;
      this.min = min;
    }
  }

  // --------------------------------------------------------------------------
  public String damePueblo() {
    return this.poblacion + "(" + this.codigo + ")";
  }

  // --------------------------------------------------------------------------
  public int dameCodigo() {
    return this.codigo;
  }

  // --------------------------------------------------------------------------
  public int dameTemperaturaMaxima() {
    return this.max;
  }

  // --------------------------------------------------------------------------
  public int dameTemperaturaMinima() {
    return this.min;
  }
}
//------------------------------------------------------------
class Tarea3 implements Callable{

        //private boolean envenenada;
        int pueblo;
        String fecha;
        PuebloMaximaMinima3 MaxMin;

        public Tarea3(int pueblo, String fecha, PuebloMaximaMinima3 maxMin) {
                this.pueblo=pueblo;
                this.fecha=fecha;
                this.MaxMin=new PuebloMaximaMinima3();
                
        }

        public int getPueblo() {
                return pueblo;
        }
       
		@Override
		public PuebloMaximaMinima3 call() throws Exception {
			return EjemploTemperaturaProvincia3.ProcesaPueblo3_paralelo(fecha, pueblo, MaxMin);
		}


}


