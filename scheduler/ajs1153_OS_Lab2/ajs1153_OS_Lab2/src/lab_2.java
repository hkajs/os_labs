import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class lab_2 {
	//make verbose global and check in algo if its true if true then print if not then dont
	static boolean verbose = false; //set to false before submit
	
	public static int randomOS(int in, int u){
		int x = 1 + (in % u);
		return x;
	}
	
	//if calculated cpu burst is 5 your run it for 2 seconds then put it back into ready, but burst is now 3. Dont recalculate burst until it = 0, each variable has their own burst that decreases every time while (quantum). 
	//for hprn ready priority queue
	
	//this is the problem
	//blocked comes first
	//do blocked process iterate through decrement block time 
	//do running process
	//do arriving
	//do ready
	
	public static int getMax(ArrayList<process> list){
	    double max = -1;
	    int index = 0;
	    for(int x = 0; x < list.size(); x++){
	        if(list.get(x).ratio > max){
	            max = list.get(x).ratio;
	            index = x;
	        }
	    }
	    return index;
	}
	
	public static void lcfs(ArrayList<process> lcfs) throws FileNotFoundException{
		Scanner random = new Scanner(new FileInputStream("random.txt"));
		
		System.out.println();
		System.out.print("The original input was: ");
		for (int x = 0; x < lcfs.size(); x++){
			System.out.print(lcfs.size() + " (" + lcfs.get(x).A + " " + lcfs.get(x).B + " " + lcfs.get(x).C + " " + lcfs.get(x).M + ") ");
		}
		
		Collections.sort(lcfs); //sort based on arrival time
		for (int x = 0; x < lcfs.size();x++){
			lcfs.get(x).process_index = x;
		}
		System.out.println();
		System.out.print("The (sorted) input was: ");
		for (int x = 0; x < lcfs.size(); x++){
			System.out.print(lcfs.size() + " (" + lcfs.get(x).A + " " + lcfs.get(x).B + " " + lcfs.get(x).C + " " + lcfs.get(x).M + ") ");
		}
		System.out.println();
		

		Queue<process> processes = new LinkedList<process>(); 
		LinkedList<process> ready = new LinkedList<process>(); 
		ArrayList<process> blocked = new ArrayList<process>();
		Queue<process> terminated = new LinkedList<process>();

		processes.addAll(lcfs);
		process running = null;
		int finishing_time = 0;
		int burst = 0;
		int block_time = 0;
		int count = 0;
		
		while (terminated.size() < lcfs.size()){
			
			ArrayList<process> readylist = new ArrayList<process>();
			for (int x = 0; x < blocked.size(); x++){
				if (blocked.get(x).blocked == 0){
					readylist.add(blocked.remove(x));
				}
			}
			if (readylist.size() == 1){
				ready.add(readylist.get(0));
			}else{
				Collections.sort(readylist);
				ready.addAll(readylist);
			}
			
			if (count == 0 && verbose == true){
				System.out.println();
			}
			
			while(!processes.isEmpty() && processes.peek().A <= count){
				//Collections.reverse(ready);
				ready.add(processes.remove()); 
			}

			if (running == null && !ready.isEmpty()){
				
				if (ready.peek().process_index == lcfs.size() - 1){
					running = ready.remove();
					ready.add(running);
				}
				//if ((ready.peek().process_index != lcfs.size()-1) || (terminated.size() == lcfs.size()-1)){
					running = ready.remove();
					int rand = 0;
					if (random.hasNextLine()){
						rand = Integer.parseInt(random.nextLine());
						//System.out.println();
						//System.out.print("random: " + rand);
					}
					
					if (burst == 0 || (burst > 0 && randomOS(rand,running.B) < burst)){
						burst = randomOS(rand,running.B);
						//block_time = randomOS(rand, running.B) * running.M;
					}
					
					finishing_time += burst;
					block_time = burst * running.M;
					//running.io_time += block_time;
			}
			
			if (running != null && burst > 0){
				running.cpu_time--;
				burst--;

			}
			
			count++;

			//ArrayList<process> blocked_to_ready = new ArrayList<process>();
			for (int x = 0; x < blocked.size(); x++){
				if (blocked.get(x).blocked > 0){
					blocked.get(x).blocked--;
					blocked.get(x).io_time++;
					
					/*
					if (blocked.get(x).blocked == 0){
						ready.add(blocked.remove(x));
					}
					*/
					if (burst > 0){
						
					}else if (lcfs.size() - terminated.size() == 1){ 
						finishing_time++;
						count++;
					}
					
					 //logically not sound
					
				}
				
			}

			
			if (running != null && running.cpu_time == 0){ //Possibly move in front of blocked part
				if (burst > 0){
					//System.out.println("burst: "+burst);
				}
				running.finishing_time = finishing_time + running.finishing_time;
				running.turnaround_time = running.finishing_time - running.A;
				terminated.add(running);
				running.finished = 1;
				running = null;
			}
			

			if (verbose == true){
				System.out.println();
				System.out.print(count);
	
				for (int x = 0; x < lcfs.size(); x++){
					if (blocked.contains(lcfs.get(x))){
						System.out.print(" BLOCKED ");
					}
					if (ready.contains(lcfs.get(x))){
						System.out.print(" READY ");
	
					}
					if (processes.contains(lcfs.get(x))){
						System.out.print(" UNSTARTED ");
					}
	
					if (lcfs.get(x).equals(running)){
						System.out.print(" RUNNING ");
					}
					if (terminated.contains(lcfs.get(x))){
						System.out.print(" TERMINATED ");
					}
					System.out.print(" Process: " + lcfs.get(x).process_index + " has " + lcfs.get(x).cpu_time + " time left and is blocked for " + lcfs.get(x).blocked + " cycles;");
				}
			}

			if (running != null && running.cpu_time > 0 && burst == 0){ //burst can be greater than 0 
				running.blocked = block_time;
				blocked.add(running);
				running = null;
			}
		}
		Collections.sort(lcfs);
		System.out.println();
		System.out.println("The scheduling algorithm used was Last Come First Served");
		int iotime = 0;
		int cputime = 0;
		int turnaroundtime = 0;
		int waitingtime = 0;
		for (int z = 0; z < lcfs.size(); z++){
			iotime += lcfs.get(z).io_time;
			cputime +=  lcfs.get(z).C;
			turnaroundtime += lcfs.get(z).turnaround_time;
			waitingtime += (lcfs.get(z).turnaround_time - lcfs.get(z).io_time - lcfs.get(z).C);
			System.out.println();
			System.out.println("Process " + lcfs.get(z).process_index + ":");
			System.out.println('\t'+"(A,B,C,M)" + " = " + "(" + lcfs.get(z).A + "," + lcfs.get(z).B + "," + lcfs.get(z).C + "," + lcfs.get(z).M + ") ");
			System.out.println('\t'+"Finishing time: " + lcfs.get(z).finishing_time);
			System.out.println('\t'+"Turnaround time: " + lcfs.get(z).turnaround_time);
			System.out.println('\t'+"I/O time: " + lcfs.get(z).io_time);
			System.out.println('\t'+"Waiting time: " + (lcfs.get(z).turnaround_time - lcfs.get(z).io_time - lcfs.get(z).C));
		}
		
		System.out.println();
        System.out.println("Summary Data:");
        int max = 0;
        for (int x = 0; x < lcfs.size(); x++){
                if (max < lcfs.get(x).finishing_time){
                        max = lcfs.get(x).finishing_time;
                }       
        }
        System.out.println("\tFinishing time: " + max);
		System.out.println("\tCpu Utilization: " + String.format("%.6f", (((double) cputime/max))));
		System.out.println("\tI/O Utilization: " + String.format("%.6f", (((double) iotime/max))));
		System.out.println("\tThroughput: " + String.format("%.6f", ((lcfs.size()*100.0/max))));
		System.out.println("\tAverage turnaround time: " + String.format("%.6f", ((double) turnaroundtime/lcfs.size())));
		System.out.println("\tAverage waiting time: " + String.format("%.6f", ((double) waitingtime/lcfs.size())));
	}
	
	public static void fcfs(ArrayList<process> fcfs) throws Exception{
		Scanner random = new Scanner(new FileInputStream("random.txt"));
		
		System.out.println();
		System.out.print("The original input was: ");
		for (int x = 0; x < fcfs.size(); x++){
			System.out.print(fcfs.size() + " (" + fcfs.get(x).A + " " + fcfs.get(x).B + " " + fcfs.get(x).C + " " + fcfs.get(x).M + ") ");
		}
		
		Collections.sort(fcfs); //sort based on arrival time
		
		for (int x = 0; x < fcfs.size();x++){
			fcfs.get(x).process_index = x;
		}
		
		System.out.println();
		System.out.print("The (sorted) input was: ");
		for (int x = 0; x < fcfs.size(); x++){
			System.out.print(fcfs.size() + " (" + fcfs.get(x).A + " " + fcfs.get(x).B + " " + fcfs.get(x).C + " " + fcfs.get(x).M + ") ");
		}
		System.out.println();

		Queue<process> processes = new LinkedList<process>(); 
		Queue<process> ready = new LinkedList<process>(); 
		ArrayList<process> blocked = new ArrayList<process>();
		Queue<process> terminated = new LinkedList<process>();

		processes.addAll(fcfs);
		process running = null;
		int finishing_time = 0;
		int burst = 0;
		int block_time = 0;
		int count = 0;
		while (terminated.size() < fcfs.size()){
			
			ArrayList<process> readylist = new ArrayList<process>();
			for (int x = 0; x < blocked.size(); x++){
				if (blocked.get(x).blocked == 0){
					readylist.add(blocked.remove(x));
				}
			}
			if (readylist.size() == 1){
				ready.add(readylist.get(0));
			}else{
				Collections.sort(readylist);
				ready.addAll(readylist);
			}
			
			if (ready.peek() != null){
				//System.out.println();
				//System.out.print(count + " " + ready.peek().process_index + " ");
				if (running != null){
					//System.out.print(running.process_index);
				}else{
					//System.out.print("null");
				}
			}

			
			if (count == 0 && verbose == true){
				System.out.println();
			}
			
			while(!processes.isEmpty() && processes.peek().A <= count){
				ready.add(processes.remove()); 
			}
			

			if (running == null && !ready.isEmpty()){
				running = ready.remove();
				int rand = 0;
				if (random.hasNextLine()){
					rand = Integer.parseInt(random.nextLine());
				}
				
				if (burst == 0 || (burst > 0 && randomOS(rand,running.B) < burst)){
					burst = randomOS(rand,running.B);
					//block_time = randomOS(rand, running.B) * running.M;
				}
				
				finishing_time += burst;
				block_time = burst * running.M;
				//running.io_time += block_time;
			}
			
			if (running != null && burst > 0){
				running.cpu_time--;
				burst--;

			}
			
			
			count++;
			
			//ArrayList<process> blocked_to_ready = new ArrayList<process>();
			for (int x = 0; x < blocked.size(); x++){
				if (blocked.get(x).blocked > 0){
					blocked.get(x).blocked--;
					blocked.get(x).io_time++;
					
					/*
					if (blocked.get(x).blocked == 0){
						ready.add(blocked.remove(x));
					}
					*/
					
					if (burst > 0){
						
					}else if (fcfs.size() - terminated.size() == 1){ 
						finishing_time++;
						count++;
					}
					
				}
			}
			
			if (running != null && running.cpu_time == 0){ //Possibly move in front of blocked part
				if (burst > 0){
					//System.out.println("burst: "+burst);
				}
				running.finishing_time = finishing_time + running.finishing_time;
				running.turnaround_time = running.finishing_time - running.A;
				terminated.add(running);
				running.finished = 1;
				running = null;
			}

			if (verbose == true){
				System.out.println();
				System.out.print(count);
	
				for (int x = 0; x < fcfs.size(); x++){
					if (blocked.contains(fcfs.get(x))){
						System.out.print(" BLOCKED ");
					}
					if (ready.contains(fcfs.get(x))){
						System.out.print(" READY ");
	
					}
					if (processes.contains(fcfs.get(x))){ //arrived.contains
						System.out.print(" UNSTARTED ");
					}
	
					if (fcfs.get(x).equals(running)){
						System.out.print(" RUNNING ");
					}
					if (terminated.contains(fcfs.get(x))){
						System.out.print(" TERMINATED ");
					}
					System.out.print(" Process: " + fcfs.get(x).process_index + " has " + fcfs.get(x).cpu_time + " time left and is blocked for " + fcfs.get(x).blocked + " cycles;");
				}
			}
			

			if (running != null && running.cpu_time > 0 && burst == 0){ //burst can be greater than 0 
				running.blocked = block_time;
				blocked.add(running);
				running = null;
			}
			
		}
		Collections.sort(fcfs);
		System.out.println();
		System.out.println("The scheduling algorithm used was First Come First Served");
		int iotime = 0;
		int cputime = 0;
		int turnaroundtime = 0;
		int waitingtime = 0;
		for (int z = 0; z < fcfs.size(); z++){
			iotime += fcfs.get(z).io_time;
			cputime +=  fcfs.get(z).C;
			turnaroundtime += fcfs.get(z).turnaround_time;
			waitingtime += (fcfs.get(z).turnaround_time - fcfs.get(z).io_time - fcfs.get(z).C);
			System.out.println();
			System.out.println("Process " + fcfs.get(z).process_index + ":");
			System.out.println('\t'+"(A,B,C,M)" + " = " + "(" + fcfs.get(z).A + "," + fcfs.get(z).B + "," + fcfs.get(z).C + "," + fcfs.get(z).M + ") ");
			System.out.println('\t'+"Finishing time: " + fcfs.get(z).finishing_time);
			System.out.println('\t'+"Turnaround time: " + fcfs.get(z).turnaround_time);
			System.out.println('\t'+"I/O time: " + fcfs.get(z).io_time);
			System.out.println('\t'+"Waiting time: " + (fcfs.get(z).turnaround_time - fcfs.get(z).io_time - fcfs.get(z).C));
		}
		
		System.out.println();
        System.out.println("Summary Data:");
        int max = 0;
        for (int x = 0; x < fcfs.size(); x++){
                if (max < fcfs.get(x).finishing_time){
                        max = fcfs.get(x).finishing_time;
                }       
        }
        System.out.println("\tFinishing time: " + max);
		System.out.println("\tCpu Utilization: " + String.format("%.6f", (((double) cputime/max))));
		System.out.println("\tI/O Utilization: " + String.format("%.6f", (((double) iotime/max))));
		System.out.println("\tThroughput: " + String.format("%.6f", ((fcfs.size()*100.0/max))));
		System.out.println("\tAverage turnaround time: " + String.format("%.6f", ((double) turnaroundtime/fcfs.size())));
		System.out.println("\tAverage waiting time: " + String.format("%.6f", ((double) waitingtime/fcfs.size())));
		
	}
	
	public static void rr(ArrayList<process> rr) throws Exception{
		Scanner random = new Scanner(new FileInputStream("random.txt"));
		
		System.out.println();
		System.out.print("The original input was: ");
		for (int x = 0; x < rr.size(); x++){
			System.out.print(rr.size() + " (" + rr.get(x).A + " " + rr.get(x).B + " " + rr.get(x).C + " " + rr.get(x).M + ") ");
		}
		
		Collections.sort(rr); //sort based on arrival time
		for (int x = 0; x < rr.size();x++){
			rr.get(x).process_index = x;
		}
		System.out.println();
		System.out.print("The (sorted) input was: ");
		for (int x = 0; x < rr.size(); x++){
			System.out.print(rr.size() + " (" + rr.get(x).A + " " + rr.get(x).B + " " + rr.get(x).C + " " + rr.get(x).M + ") ");
		}
		System.out.println();
		
		Queue<process> processes = new LinkedList<process>(); 
		Queue<process> ready = new LinkedList<process>(); 
		ArrayList<process> blocked = new ArrayList<process>();
		Queue<process> terminated = new LinkedList<process>();

		processes.addAll(rr);
		process running = null;
		int finishing_time = 0;
		int burst = 0;
		int block_time = 0;
		int count = 0;
		int quantum = 0;
		
		while (terminated.size() < rr.size()){
			ArrayList<process> readylist = new ArrayList<process>();
			for (int x = 0; x < blocked.size(); x++){
				if (blocked.get(x).blocked == 0){
					readylist.add(blocked.remove(x));
				}
			}
			if (readylist.size() == 1){
				ready.add(readylist.get(0));
			}else{
				Collections.sort(readylist);
				ready.addAll(readylist);
			}
			
			if (count == 0 && verbose == true){
				System.out.println();
			}
			
			while(!processes.isEmpty() && processes.peek().A <= count){
				ready.add(processes.remove()); 
			}
			
			
			if (running == null && !ready.isEmpty()){
				running = ready.remove();

				int rand = 0;
				if (random.hasNextLine()){
					rand = Integer.parseInt(random.nextLine());
				}
				
				
				if (running.burst == 0){
					running.burst = randomOS(rand,running.B);
				}
				
				/*
				 * might cause errors need if to check if quantum effects
				 */
				finishing_time += running.burst;
				block_time = running.burst * running.M;
				//running.io_time += block_time;
			}
			
			
			if (running != null && running.burst > 0){
				running.cpu_time--;
				running.burst--;
				quantum++;
			}
			
			count++;
			
			for (int x = 0; x < blocked.size(); x++){
				if (blocked.get(x).blocked > 0){
					blocked.get(x).blocked--;
					blocked.get(x).io_time++;
					
					/*
					if (blocked.get(x).blocked == 0){
						ready.add(blocked.remove(x));
					}
					*/
					if (burst > 0){
						
					}else if (rr.size() - terminated.size() == 1){ 
						finishing_time++;
						count++;
					}					
				}
			}
			
			if (running != null && running.cpu_time == 0){ //Possibly move in front of blocked part
				if (running.burst > 0){
					//System.out.println("burst: "+burst);
				}
				running.finishing_time = finishing_time + running.finishing_time;
				running.turnaround_time = running.finishing_time - running.A;
				terminated.add(running);
				running.finished = 1;
				running = null;
			}
			

			if (verbose == true){
				System.out.println();
				System.out.print(count);
	
				for (int x = 0; x < rr.size(); x++){
					if (blocked.contains(rr.get(x))){
						System.out.print(" BLOCKED ");
					}
					if (ready.contains(rr.get(x))){
						System.out.print(" READY ");
	
					}
					if (processes.contains(rr.get(x))){
						System.out.print(" UNSTARTED ");
					}
	
					if (rr.get(x).equals(running)){
						System.out.print(" RUNNING ");
					}
					if (terminated.contains(rr.get(x))){
						System.out.print(" TERMINATED ");
					}
					System.out.print(" Process: " + rr.get(x).process_index + " has " + rr.get(x).cpu_time + " time left and is blocked for " + rr.get(x).blocked + " cycles;");
				}
			}
			
			if (running != null && running.cpu_time > 0 && running.burst == 0){ //burst can be greater than 0
				running.blocked = block_time;
				blocked.add(running);
				running = null;
			}
			
			if (running != null && quantum == 2){
				quantum = 0;
				ready.add(running);
				running = null;
			}
			
			
		}
		
		Collections.sort(rr);
		System.out.println();
		System.out.println("The scheduling algorithm used was Round Robin");
		int iotime = 0;
		int cputime = 0;
		int turnaroundtime = 0;
		int waitingtime = 0;
		for (int z = 0; z < rr.size(); z++){
			iotime += rr.get(z).io_time;
			cputime +=  rr.get(z).C;
			turnaroundtime += rr.get(z).turnaround_time;
			waitingtime += (rr.get(z).turnaround_time - rr.get(z).io_time - rr.get(z).C);
			System.out.println();
			System.out.println("Process " + rr.get(z).process_index + ":");
			System.out.println('\t'+"(A,B,C,M)" + " = " + "(" + rr.get(z).A + "," + rr.get(z).B + "," + rr.get(z).C + "," + rr.get(z).M + ") ");
			System.out.println('\t'+"Finishing time: " + rr.get(z).finishing_time);
			System.out.println('\t'+"Turnaround time: " + rr.get(z).turnaround_time);
			System.out.println('\t'+"I/O time: " + rr.get(z).io_time);
			System.out.println('\t'+"Waiting time: " + (rr.get(z).turnaround_time - rr.get(z).io_time - rr.get(z).C));
		}
		

		System.out.println();
        System.out.println("Summary Data:");
        int max = 0;
        for (int x = 0; x < rr.size(); x++){
                if (max < rr.get(x).finishing_time){
                        max = rr.get(x).finishing_time;
                }       
        }
        System.out.println("\tFinishing time: " + max);
		System.out.println("\tCpu Utilization: " + String.format("%.6f", (((double) cputime/max))));
		System.out.println("\tI/O Utilization: " + String.format("%.6f", (((double) iotime/max))));
		System.out.println("\tThroughput: " + String.format("%.6f", ((rr.size()*100.0/max))));
		System.out.println("\tAverage turnaround time: " + String.format("%.6f", ((double) turnaroundtime/rr.size())));
		System.out.println("\tAverage waiting time: " + String.format("%.6f", ((double) waitingtime/rr.size())));
		
	}
	
	public static void hprn(ArrayList<process> hprn) throws Exception{
		Scanner random = new Scanner(new FileInputStream("random.txt"));
		
		System.out.println();
		System.out.print("The original input was: ");
		for (int x = 0; x < hprn.size(); x++){
			System.out.print(hprn.size() + " (" + hprn.get(x).A + " " + hprn.get(x).B + " " + hprn.get(x).C + " " + hprn.get(x).M + ") ");
		}
		
		Collections.sort(hprn); //sort based on arrival time
		for (int x = 0; x < hprn.size();x++){
			hprn.get(x).process_index = x;
		}
		System.out.println();
		System.out.print("The (sorted) input was: ");
		for (int x = 0; x < hprn.size(); x++){
			System.out.print(hprn.size() + " (" + hprn.get(x).A + " " + hprn.get(x).B + " " + hprn.get(x).C + " " + hprn.get(x).M + ") ");
		}
		System.out.println();

		Queue<process> processes = new LinkedList<process>(); 
		ArrayList<process> ready = new ArrayList<process>(); 
		ArrayList<process> blocked = new ArrayList<process>();
		Queue<process> terminated = new LinkedList<process>();

		processes.addAll(hprn);
		process running = null;
		int finishing_time = 0;
		int burst = 0;
		int block_time = 0;
		int count = 0;
		while (terminated.size() < hprn.size()){
			
			ArrayList<process> readylist = new ArrayList<process>();
			for (int x = 0; x < blocked.size(); x++){
				if (blocked.get(x).blocked == 0){
					readylist.add(blocked.remove(x));
				}
			}
			if (readylist.size() == 1){
				ready.add(readylist.get(0));
			}else{
				Collections.sort(readylist);
				ready.addAll(readylist);
			}
			
			if (count == 0 && verbose == true){
				System.out.println();
			}
			
			while(!processes.isEmpty() && processes.peek().A <= count){
				ready.add(processes.remove()); 
			}
			
		
			if (running == null && !ready.isEmpty()){
				for (int x = 0; x < ready.size(); x++){
					ready.get(x).ratioize(count);
				}
				
				int index = getMax(ready);
				
				running = ready.remove(index);
				int rand = 0;
				if (random.hasNextLine()){
					rand = Integer.parseInt(random.nextLine());
				}
				
				if (burst == 0 || (burst > 0 && randomOS(rand,running.B) < burst)){
					burst = randomOS(rand,running.B);
				}
				
				finishing_time += burst;
				block_time = burst * running.M;
			}
			
			if (running != null && burst > 0){
				running.cpu_time--;
				burst--;
			}
			
			count++;


			//ArrayList<process> blocked_to_ready = new ArrayList<process>();
			for (int x = 0; x < blocked.size(); x++){
				if (blocked.get(x).blocked > 0){
					blocked.get(x).blocked--;
					blocked.get(x).io_time++;
					
					if (burst > 0){
						
					}else if (hprn.size() - terminated.size() == 1){ 
						//finishing_time++;
						//count++;
					}
				}
			}
			
			
			if (running != null && running.cpu_time == 0){ //Possibly move in front of blocked part
				if (burst > 0){
					//System.out.println("burst: "+burst);
				}
				running.finishing_time = count + running.finishing_time;
				running.turnaround_time = running.finishing_time - running.A;
				terminated.add(running);
				running.finished = 1;
				running = null;
			}

			if (verbose == true){
				System.out.println();
				System.out.print(count);
	
				for (int x = 0; x < hprn.size(); x++){
					if (blocked.contains(hprn.get(x))){
						System.out.print(" BLOCKED ");
					}
					if (ready.contains(hprn.get(x))){
						System.out.print(" READY ");
	
					}
					if (processes.contains(hprn.get(x))){ //arrived.contains
						System.out.print(" UNSTARTED ");
					}
	
					if (hprn.get(x).equals(running)){
						System.out.print(" RUNNING ");
					}
					if (terminated.contains(hprn.get(x))){
						System.out.print(" TERMINATED ");
					}
					System.out.print(" Process: " + hprn.get(x).process_index + " has " + hprn.get(x).cpu_time + " time left and is blocked for " + hprn.get(x).blocked + " cycles;");
				}
			}

			if (running != null && running.cpu_time > 0 && burst == 0){ //burst can be greater than 0 
				running.blocked = block_time;
				blocked.add(running);
				running = null;
			}
					
		}
		Collections.sort(hprn);
		System.out.println();
		System.out.println("The scheduling algorithm used was Highest Penalty Ratio Next");
		int iotime = 0;
		int cputime = 0;
		int turnaroundtime = 0;
		int waitingtime = 0;
		for (int z = 0; z < hprn.size(); z++){
			iotime += hprn.get(z).io_time;
			cputime +=  hprn.get(z).C;
			turnaroundtime += hprn.get(z).turnaround_time;
			waitingtime += (hprn.get(z).turnaround_time - hprn.get(z).io_time - hprn.get(z).C);
			System.out.println();
			System.out.println("Process " + hprn.get(z).process_index + ":");
			System.out.println('\t'+"(A,B,C,M)" + " = " + "(" + hprn.get(z).A + "," + hprn.get(z).B + "," + hprn.get(z).C + "," + hprn.get(z).M + ") ");
			System.out.println('\t'+"Finishing time: " + hprn.get(z).finishing_time);
			System.out.println('\t'+"Turnaround time: " + hprn.get(z).turnaround_time);
			System.out.println('\t'+"I/O time: " + hprn.get(z).io_time);
			System.out.println('\t'+"Waiting time: " + (hprn.get(z).turnaround_time - hprn.get(z).io_time - hprn.get(z).C));
		}
		
		System.out.println();
        System.out.println("Summary Data:");
        int max = 0;
        for (int x = 0; x < hprn.size(); x++){
                if (max < hprn.get(x).finishing_time){
                        max = hprn.get(x).finishing_time;
                }       
        }
        System.out.println("\tFinishing time: " + max);
		System.out.println("\tCpu Utilization: " + String.format("%.6f", (((double) cputime/max))));
		System.out.println("\tI/O Utilization: " + String.format("%.6f", (((double) iotime/max))));
		System.out.println("\tThroughput: " + String.format("%.6f", ((hprn.size()*100.0/max))));
		System.out.println("\tAverage turnaround time: " + String.format("%.6f", ((double) turnaroundtime/hprn.size())));
		System.out.println("\tAverage waiting time: " + String.format("%.6f", ((double) waitingtime/hprn.size())));
				
		
	}
	
	public static void main(String[] args) throws Exception{
		ArrayList<process> rr = new ArrayList<process>();
		ArrayList<process> fcfs = new ArrayList<process>();
		ArrayList<process> lcfs = new ArrayList<process>();
		ArrayList<process> hprn = new ArrayList<process>();
		String filename="";
		
		if (args.length == 2){
			if (args[0].equals("--verbose")){
				verbose = true;
			}else{
				System.err.println("Incorrect args try again");
				return;
			}
			filename = args[1];
		}else if (args.length == 1){
			filename = args[0];
		}else{
			System.err.println("Incorrect args try again");
			return;
		}
		
		try {
			
		File file = new File(filename);
		Scanner sc = new Scanner(file);
		
		
		int count = 0;
		int count2 = 0;
		int size = 0;
		int input = 0;
		if (sc.hasNext()){
			size = Integer.parseInt(sc.next());
		}
		
		for (int y1 = 0; y1 < size; y1++){
			process p = new process();
			rr.add(p);
			rr.get(y1).process_index = y1;
		}
		for (int y2 = 0; y2 < size; y2++){
			process p = new process();
			fcfs.add(p);
			fcfs.get(y2).process_index = y2;
		}
		for (int y3 = 0; y3 < size; y3++){
			process p = new process();
			lcfs.add(p);
			lcfs.get(y3).process_index = y3;
		}
		for (int y4 = 0; y4 < size; y4++){
			process p = new process();
			hprn.add(p);
			hprn.get(y4).process_index = y4;
		}
		for (int x = 0; x < size*4; x++){
			input = Integer.parseInt(sc.next());
				if (count == 0){
					rr.get(count2).setA(input);
					fcfs.get(count2).setA(input);;
					lcfs.get(count2).setA(input);;
					hprn.get(count2).setA(input);;
	
				}	
				if (count == 1){
					rr.get(count2).setB(input);
					fcfs.get(count2).setB(input);;
					lcfs.get(count2).setB(input);;
					hprn.get(count2).setB(input);;
				}
				if (count == 2){
					rr.get(count2).setC(input);
					fcfs.get(count2).setC(input);;
					lcfs.get(count2).setC(input);;
					hprn.get(count2).setC(input);;
				}
				if (count == 3){
					rr.get(count2).setM(input);
					fcfs.get(count2).setM(input);;
					lcfs.get(count2).setM(input);;
					hprn.get(count2).setM(input);;
					count2++;	
				}
					count++;
				if (count > 3){
					count = 0;
				}
								
		}
		
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
		
		
		fcfs(fcfs);
		lcfs(lcfs);
		rr(rr);
		hprn(hprn);
		
	}

}
