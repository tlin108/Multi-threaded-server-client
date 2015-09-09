import java.io.FileReader;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;


public class Event extends Thread {
	
	private static int num_Students;
	private static int TableCapacity;
	private static int studentNum = 0;
	private volatile static int groupNum = 0;
	private static int LabCount = 0;
	private static long time = System.currentTimeMillis();
	private static long random;
	private static Random r = new Random();
	private boolean BRinUse = false;
	private static int studentWentHome = 0;
	private static volatile boolean lectureSession1;
	private static volatile boolean lectureSession2;
	private static volatile boolean labSession;
	private static volatile boolean canGoHome;
	private static int[][] recordBook;
	private int waiting=2000;
	
	Vector<Object> BRLine;
	Vector<Object> LabGroup;
	
	//constructor
	Event(int num_Students_, int TableCapacity_){
		setName("Event");
		num_Students = num_Students_;
		TableCapacity = TableCapacity_;
		BRLine = new Vector();
		LabGroup = new Vector();
		recordBook = new int [num_Students+2][4];
	}
	
	//getter for number of students
	public int getnumStudent(){
		return num_Students;
	}
	
	//return whether the bathroom is free or not
	public synchronized boolean isBathroomFree(){
		return BRinUse;
	}
	
	// Stimulates teacher on his way from home
	public void onTheWay(String id){
		msg(id, "is on his way to school from home");
		try {
			Thread.sleep(2000);
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/*	
	 *  Stimulates students waking up in different times.
	 */
	public void wakeUp(String id){
		random = r.nextInt(700 - 400) + 400;
		try {
			msg(id ,"is still asleep");
			Thread.sleep(random);
			msg(id, "is now awake");
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// procedure student has to follow when trying to use bathroom
	public void tryUseBathroom(String id){
		msg(id, "is going to try use the bathroom to get ready for school");
		if(isBathroomFree()){
			msg(id, "is going to take a break because bathroom is taken");
			yield();
			msg(id,"is done taking a break and now standing on line for his turn to use bathroom");
		}
		useBathroom(id);
	}
	
	/*
	 *  Stimulate the bathroom use with notification object for each student
	 */
	public void useBathroom( String id){
		Object BRKey = new Object();
		synchronized (BRKey) {
			if(BRinUse(BRKey))
				while(true)
					try { BRKey.wait(); break; }
					// notify() after interrupt() race condition ignored
					catch(InterruptedException e) { continue; }
		}
		try {			
			random = r.nextInt(200 - 100) + 100;
			msg(id, "Bathroom is currently in use by "+id);
			Thread.sleep(random);
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			msg(id, "Bathroom is finished using by "+id);
			if(BRLine.size() > 0){
				synchronized (BRLine.elementAt(0)) {
					BRLine.elementAt(0).notify();
				}
				BRLine.removeElementAt(0);
			} else 
				BRinUse = false;
		}
	}
	
	/*
	 *  add student object to vector BRline if bathroom is in use 
	 */
	private synchronized boolean BRinUse (Object BRKey){
		boolean status;
		if(BRinUse){
			BRLine.addElement(BRKey);
			status = true;
		} else {
			BRinUse = true;
			status = false;
		}
		return status;
	}
	
	// Work schedule for the teacher, teaches 2 lectures + 1 lab
	public void workDay(String id){
		for(int i=1;i<=3;i++){
			startLecture(id, i);
			while(!lectureEnd(i)){
				//Busy Wait until it's time to end lecture N
			}
			//set the lectureSession to false
			endLectureSession(id,i);
		}
	}
	/*	
	 *  Stimulates students waiting for lecture
	 *  if lecture is not on, wait for the teacher to open door
	 *  else go to an errand come back and wait for the next lecture to start
	 */
	public boolean waitLecture(String id, int n){
		int studentNum = Integer.parseInt(id.replaceAll("[\\D]", ""));
		if(!lectureStarted(n)){
			if(n==3){
				msg(id, "is going to wait for the teacher to open the auditorium door for lab");
			}
			else
				msg(id, "is going to wait for the teacher to open the auditorium door for lecture "+n);
			while(!lectureStarted(n)){
				// Busy Wait until teacher opens auditorium door
			}
			if(n==3){
				LabCount++;
				msg(id, "is now in lab session, trying to get in a group of 4 for lab.");
				addNumClass(studentNum);
				updateClassTaken(studentNum, n);
				getInGroup(id, studentNum);
			}
			else{
				msg(id, "is now in lecture session "+n);
				addNumClass(studentNum);
				updateClassTaken(studentNum, n);
			}
			return true;
		}
		//Stimulate student going for an errand
		else{
			if(n==3){
				LabCount++;
				msg(id, "is going to run errands because lab has started");
				if(LabCount==getnumStudent()) //if the student who skipped lab is the last student
					endGroup();				  //this will release those students who are in Lab waiting for more members
			}
			else
				msg(id, "is going to run errands because lecture "+n+" has started");
			random = r.nextInt(500 - 200) + 200;
			try {
				Thread.sleep(random);
				if(n==3){
					msg(id, "is now done with errands, ready to go back to dorm");
					return false;
				}
				else {
					msg(id, "is now done with errands, going back to see if lecture "+n+" is over");
					return false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/*	
	 *  Stimulates student taking break in between lectures
	 */
	public void breaktime(String id, int n){
		if(n==3){
			msg(id, "is now finished with lab session "+n+", ready to go back to dorm");
		}
		else{
			msg(id, "is now finished with lecture session "+n+", going to have fun during the break");
		}
		//this.setPriority(getPriority()+1);
		random = (r.nextInt(600 - 300) + 300);
		try {
			Thread.sleep(random);
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.setPriority(NORM_PRIORITY);
	}
	
	/*	
	 *  Add 1 to the number of classes student has taken already
	 */
	private void addNumClass(int studentNum){
		recordBook[studentNum][0]++;
	}
	
	/*	
	 *  Mark which class the student has taken
	 */
	private void updateClassTaken(int studentNum, int n){
		switch (n)
		{
			case 1: 
				
				recordBook[studentNum][1] = 1;
				break;
				
			case 2:
			
				recordBook[studentNum][2] = 1;
				break;
			
			case 3:
			
				recordBook[studentNum][3] = 1;
				break;
			
			default:
				return;
		}
	}
	
	/*
	 *  Stimulate students getting in a group
	 */
	public void getInGroup(String id, int studentNum){

		try {
			formGroup(id, studentNum);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	 *  Stimulate forming group in lab with notification object for each table
	 */
	public void formGroup(String id, int studentNum) throws InterruptedException{
		Object LabKey = new Object();
		synchronized (LabKey) {
			studentNum++;
			groupNum = ((studentNum-1)/TableCapacity)+1;
			if(LabSession(LabKey)){
				while(true){
					msg(id, "is now in group #"+groupNum);
					try { LabKey.wait(); break; }
					// notify() after interrupt() race condition ignored
					catch(InterruptedException e) { continue; }
				}
			}
			else {
				msg(id, "is now in group #"+groupNum);
				endGroup();
			}
		}
	}
	
	/*
	 * stimulate releasing other students on queue
	 */
	
	public synchronized void endGroup(){
		if(LabGroup.size() > 0){
			for(int i=1;i<=TableCapacity-1;i++){
				if(LabGroup.size()>0){
				synchronized (LabGroup.elementAt(0)) {
					LabGroup.elementAt(0).notify();
				}
				LabGroup.removeElementAt(0);
				}
			}
		}
	}
	
	/*
	 * add student to vector Labgroup if student num is not table capacity
	 */

	private synchronized boolean LabSession (Object LabKey){
		boolean status;
		if(studentNum%TableCapacity==0){
			status = false;
		} else {
			LabGroup.addElement(LabKey);
			status = true;
		}
		return status;
	}
	
	//will return the value of the lecture# asked, the value will be true if lecture # has started, false otherwise
		public synchronized boolean lectureStarted(int n){
			switch (n)
			{
				case 1: 
				
					return lectureSession1;
				
				case 2:
				
					return lectureSession2;
				
				case 3:
				
					return labSession;
				
				default:
					return false;
			}
		}
		
		/*	
		 *  Stimulates teacher starting lecture at fixed time
		 */
		private void startLecture(String id, int n){	
			while(!timeforLecture(n)){
				//Busy Wait until its time for lecture N
			}
			
			switch (n)
			{
				case 1: 
				
					msg(id, "has started teaching lecture "+n);
					lectureSession1 = true;
					return;
					
				case 2:
					
					msg(id, "has started teaching lecture "+n);
					lectureSession2 = true;
					return;
				
				case 3:
		
					msg(id, "has started teaching lab");
					labSession = true;
					return;
				
				default:
					return;
			}
		}
		
		// will set the value of lecture # to false after lecture ended
		public void endLectureSession(String id, int n){
			switch (n)
			{
				case 1: 
					
					msg(id, "has finished teaching lecture "+n);
					lectureSession1 = false;
					break;
				
				case 2:
					
					msg(id, "has finished teaching lecture "+n);
					lectureSession2 = false;
					break;
				
				case 3:
					
					msg(id, "has finished teaching lab ");
					labSession = false;
					break;
				
				default:
					return ;
			}
		}
		
		// will return true if it's time for lecture # to start, false otherwise
			private boolean timeforLecture(int n){
				switch (n)
				{
					case 1: //the time when first lecture starts
					
						if(age()<2000){
							return false;
						}
						return true;
					
					
					case 2://the time when second lecture starts
					
						if(age()<4000){
							return false;
						}
						return true;
					
					case 3://the time when lab starts
						
						if(age()<6000){
							return false;
						}
						return true;
					
					default:
						return false;
				}
			}
			
	// will return true if it's time for lecture # to end, false otherwise
	public synchronized boolean lectureEnd(int n){
		switch (n)
		{
			case 1: //the time when first lecture ends
					
				if(age()<3500){
					return false;
				}
				return true;
					
			case 2://the time when second lecture ends
					
				if(age()<5500){
					return false;
				}
				return true;
					
			case 3://the time when lab ends
					
				if(age()<7500){
					return false;
				}
				return true;
			
			default:
				return false;
		}
	}
	
	// Stimulates waiting for another student
	public void goBackDorm(String id){
		int studentNum = Integer.parseInt(id.replaceAll("[\\D]", ""));
		while(lectureStarted(3)){
			// B.W those who didn't go to lecture until lecture 3 finishes
		}
		/*	
		 * the below expression is used to determine wait time needed for thread to be called join.
		 * higher id (ex.10) will have shorter waiting time
		 * lower id (ex.1) will have longer waiting time
		 * this is to make sure no lower student #s will terminate before the higher student #s.
		 */
		try{
			sleep(waiting-(studentNum*100));
		} 
		catch (InterruptedException e){
			// TODO Auto-generated catch block
		} finally{
			studentWentHome++;
			msg(id, "is going back to dorm");
			if(studentWentHome==num_Students){
				release();
			}
		}
				
	}
	
	// allows teacher to go home, signal by the last student
	public synchronized void release(){
		canGoHome = true;
		return;
	}
	
	// blocks teacher until the last student goes home
	public void StayAfter(String id){
		while(!canGoHome) {}
		msg(id, "goes home since all the students have went back to dorm.");
	}
	
	/*
	 *  print summary of the classes that the students have each attended
	 */
	public void summary(){
		System.out.println("");
		System.out.println("Classes Attended Summary");
		System.out.println("Student Name\tTotal Numer of classes taken\tClass Name");
		for(int i=1;i<=num_Students;i++){
			System.out.print("Student # "+i+"\t\t\t"+recordBook[i][0]+"\t\t");
			for(int k=1;k<=3;k++){
				if(recordBook[i][k]==1)
					System.out.print(k+" ");
			}
			System.out.println("");
		}
		
		System.out.println("");
		System.out.println("Simulation finishes: A Day in the Dormitory.");
	}
	
	// return age of event thread
	public long age(){
		return System.currentTimeMillis()-time;
	}	
	
	// update the time when server thread is created
	public void updateTime(){
		time = System.currentTimeMillis();
	}
	public void msg(String id, String m){
		System.out.println("["+(System.currentTimeMillis()-time)+"]"+id+": "+m);
	}
	
	public void run(){
		
	}

}
