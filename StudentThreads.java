import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class StudentThreads extends Thread {

	int port; 								
	String serverName;
	boolean skipClass=false;
	
	public StudentThreads(int id, String hostName, int portNumber) {
		setName("Student-"+ id);
		this.serverName = hostName;
		this.port = portNumber;
	}

	public void run(){
			
			try (
					
				Socket studentSocket = new Socket(serverName, port);
				PrintWriter out = new PrintWriter(studentSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(studentSocket.getInputStream()));
			
			){

				// Student waking up
				out.println(getName());   				
				out.println("1");						
				
				// Student use bathroom
				out.println(getName());
				out.println("3");
				
				// Student goes to lecture 1
				out.println(getName());
				out.println("4");
				skipClass = Boolean.parseBoolean(in.readLine());
				if(skipClass){			
					try {
						sleep(10000);
					} catch (InterruptedException e) {
					}
					// Student takes a break
					out.println(getName());
					out.println("5");
				}
				
				// Student goes to lecture 2
				out.println(getName());
				out.println("6");
				skipClass = Boolean.parseBoolean(in.readLine());
				if(skipClass){			
					try {
						sleep(10000);
					} catch (InterruptedException e) {
					}
					// Student takes a break
					out.println(getName());
					out.println("7");
				}
				
				// Student goes to lecture 3
				out.println(getName());
				out.println("8");
				skipClass = Boolean.parseBoolean(in.readLine());
				if(skipClass){			
					try {
						sleep(10000);
					} catch (InterruptedException e) {
					}
					// Student takes a break
					out.println(getName());
					out.println("9");
				}
				
				// Student goes back to dorm
				out.println(getName());
				out.println("11");
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
}
