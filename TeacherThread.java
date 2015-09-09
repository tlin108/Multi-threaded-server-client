import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class TeacherThread extends Thread{
	
	int port; 								
	String serverName;
	
	public TeacherThread(String serverName, int port){
		
		setName("Teacher");
		this.port = port;
		this.serverName = serverName;
		
	} // constructor
	
	public void run(){
		
		
		try (
				
			Socket teacherSocket = new Socket(serverName, port);
			PrintWriter out = new PrintWriter(teacherSocket.getOutputStream(), true);
		
		){
			
			// Teacher on his way
			out.println(getName());   				
			out.println("2");
			
			// Teacher starts teaching lectures
			out.println(getName());
			out.println("10");
			
			// Teacher goes home
			out.println(getName());
			out.println("12");
			
			// Teacher prints summary
			out.println(getName());
			out.println("13");				
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
