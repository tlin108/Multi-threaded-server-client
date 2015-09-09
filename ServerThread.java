import java.io.*;
import java.net.*;


public class ServerThread extends Thread{

	public Socket socket= null;
	Event event = null; 
	
	private boolean inClass;
	
	ServerThread(Socket accept_, Event event_) {
		super("ServerThread");
		socket = accept_;
		event = event_; 
	}
	
	public void run(){
			event.updateTime();
			
			try (
		        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    ) {
		        String inputLineName, inputMethodNum;
		        
		        while((inputLineName = in.readLine()) != null){
		        	inputMethodNum = in.readLine();    
		        	
		        	/* the client threads will give server thread an input methodNum
		        	 * so that the Server thread can execute method depending on the methodNum
		        	 */
		        	if(inputLineName != null && inputMethodNum != null){
		        		
		        		if(inputMethodNum.equals("0")){
		        			event.start();
		        		}
		        		if(inputMethodNum.equals("1")){
		        			event.wakeUp(inputLineName);
		        		}
		        		if(inputMethodNum.equals("2")){
		        			event.onTheWay(inputLineName);
		        		}
		        		if(inputMethodNum.equals("3")){
		        			event.tryUseBathroom(inputLineName);
		        		}
		        		if(inputMethodNum.equals("4")){
		        			inClass = event.waitLecture(inputLineName, 1);
		        			out.println(inClass);
		        		}
		        		if(inputMethodNum.equals("5")){
		        			event.breaktime(inputLineName, 1);
		        		}
		        		if(inputMethodNum.equals("6")){
		        			inClass = event.waitLecture(inputLineName, 2);
		        			out.println(inClass);
		        		}
		        		if(inputMethodNum.equals("7")){
		        			event.breaktime(inputLineName, 2);
		        		}
		        		if(inputMethodNum.equals("8")){
		        			inClass = event.waitLecture(inputLineName, 3);
		        			out.println(inClass);
		        		}
		        		if(inputMethodNum.equals("9")){
		        			event.breaktime(inputLineName, 3);
		        		}
		        		if(inputMethodNum.equals("10")){
		        			event.workDay(inputLineName);
		        		}
		        		if(inputMethodNum.equals("11")){
		        			event.goBackDorm(inputLineName);
		        		}
		        		if(inputMethodNum.equals("12")){
		        			event.StayAfter(inputLineName);
		        		}
		        		if(inputMethodNum.equals("13")){
		        			event.summary();
		        		}
		        	
		        	}
		        }
		        socket.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		     }
	}
}
