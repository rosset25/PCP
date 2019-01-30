package practica2.ejercicio1;

public class CiclicoEjercicio1 {

	static int numHebras;
	static int n;
	
	public static void main( String args[]) {
		
		
		
		if(args.length != 2) {
			System.err.println("ERROR: número de argumentos inválido");
			System.exit(-1);
		}
		
		try {
			numHebras = Integer.parseInt(args[0]);
			n = Integer.parseInt(args[1]);
		}catch(NumberFormatException e) {
			numHebras = -1;
			n = -1;
			System.err.println("ERROR: no son enteros");
			System.exit(-1);
		}
		
		//no sé si servirá luego o dará igual
		Hebra[] hebras = new Hebra[numHebras];
		
		for(int i=0; i < numHebras; i++) {
			hebras[i] = new Hebra(i);
			hebras[i].start();
		}
		
		for(int i=0; i < numHebras; i++) {
			try {
				hebras[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public static class Hebra extends Thread{
		int id;
		
		public Hebra(int idHebra) {
			id = idHebra;
		}
		
		public void run() {
			int iniElem = id;
			int finElem = n;
			for(int i=iniElem; i < finElem; i+=numHebras) {
				System.out.println(i + ", soy la hebra: "+ id);
			}
			
		}
		
		
	}
	
}
