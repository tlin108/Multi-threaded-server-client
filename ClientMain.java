import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientMain {

	//Thread classes
	private static StudentThreads[] students;
	private static TeacherThread teacher;
	public static Event event;
	
	//Default values
	private static int num_Students = 13;
	private static int TableCapacity = 4;
	
	public static void main(String[] args){
	
		// initializing host and local port
		InetAddress host = null;
		try {
			host = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String hostName = host.getHostName();
		int portNumber = 6999;
		
		if (args.length > 0) {
	        hostName = args[0];
	    	portNumber = Integer.parseInt(args[1]);
	    	num_Students = Integer.parseInt(args[2]);
    	}
		
		teacher = new TeacherThread(hostName, portNumber);
		teacher.start();
		
		students = new StudentThreads[num_Students+1];
		
		for(int i=1;i<=num_Students;i++){
			students[i] = new StudentThreads(i, hostName, portNumber);
			students[i].start();
		}
		
		event = new Event(num_Students, TableCapacity);
		
		for(int j=1;j<=3;j++){
			while(!event.lectureEnd(j)){
				//Busy Wait until it's time to end lecture N
			}
			//interrupts student threads that are currently in lecture
			for(int i=1; i<=num_Students;i++){
				if(students[i].getState().toString()=="TIMED_WAITING"){
					students[i].interrupt();
				}
			}
		}
		
		/*
		 * Students threads call join in a sequential descending order.
		 */
		
		for(int i=num_Students;i>0; i--){
			if(students[i].isAlive()){
				try{
					students[i].join();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}