import java.io.IOException;
import java.net.ServerSocket;


public class Server {
	
	//Default values
	private static int num_Students = 13;
	private static int TableCapacity = 4;

	
	public static void main(String[] args) throws IOException {
		
		//port number hard-coded if it's not given
		int portNumber = 6999;
	    
		//command line input
		if(args.length > 0){
			portNumber = Integer.parseInt(args[0]);
			if(args.length > 1)
				num_Students = Integer.parseInt(args[0]);
			if(args.length > 2)
				TableCapacity = Integer.parseInt(args[1]);
		}
		
	    Event event = new Event(num_Students, TableCapacity);     	
		boolean listen = true;	
		
	    try (
	        	
	      	ServerSocket serverSocket = new ServerSocket(portNumber)
	        	
	    ) { 
	    	System.out.println("The server is now running");
	        while (listen) {
	            new ServerThread(serverSocket.accept(), event).start();
	        }
       } catch (IOException e) {
	            System.err.println("Could not listen on port " + portNumber);
	            System.exit(-1);
       }
	}
}
