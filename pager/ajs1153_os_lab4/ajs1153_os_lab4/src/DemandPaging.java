import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Scanner;

public class DemandPaging {
	public static final int RAND_MAX=Integer.MAX_VALUE;

	
	public static void LIFO(ArrayList<Frame> frametable, Hashtable<Integer, Process> processes, int process, int page, int count){
		int id = frametable.indexOf(Collections.max(frametable));
		Frame frame = new Frame();
		frame.id = id;
		frame.process = process;
		frame.page = page;
		frame.count = count;
		frame.last = count;
		frame.fault = count;
		processes.get(frametable.get(id).process).residency += (count-frametable.get(id).fault);
		processes.get(frametable.get(id).process).evictions++;
		processes.get(process).faults++;
		//System.out.println(process + " references word " + processes.get(process).rindex + " (page " + page + ") " + "at time " + count + ": Fault, evicting page " + frametable.get(id).page + " of " + frametable.get(id).process + " from frame " + frame.id);
		frametable.set(id, frame);
	}
	
	public static void RANDOM(ArrayList<Frame> frametable, Hashtable<Integer, Process> processes, int process, int page, int count, Scanner random){
		//choosing a random frame
		int rand = Integer.parseInt(random.nextLine());
		int id = rand%frametable.size();
		Frame frame = new Frame();
		frame.id = id;
		frame.process = process;
		frame.page = page;
		frame.count = count;
		frame.last = count;
		frame.fault = count;
		processes.get(frametable.get(id).process).residency += (count-frametable.get(id).fault);
		processes.get(frametable.get(id).process).evictions++;
		processes.get(process).faults++;
		//System.out.println(process + " references word " + processes.get(process).rindex + " (page " + page + ") " + "at time " + count + ": Fault, evicting page " + frametable.get(id).page + " of " + frametable.get(id).process + " from frame " + frame.id);
		frametable.set(id, frame);
	}
	
	public static void LRU(ArrayList<Frame> frametable, Hashtable<Integer, Process> processes, int process, int page, int count){
		int id = frametable.indexOf(Collections.min(frametable)); //incorrect
		Frame frame = new Frame();
		frame.id = id;
		frame.process = process;
		frame.page = page;
		frame.count = count;
		frame.last = count;
		frame.fault = count;
		processes.get(frametable.get(id).process).residency += (count-frametable.get(id).fault);
		processes.get(frametable.get(id).process).evictions++;
		processes.get(process).faults++;
		//System.out.println(process + " references word " + processes.get(process).rindex + " (page " + page + ") " + "at time " + count + ": Fault, evicting page " + frametable.get(id).page + " of " + frametable.get(id).process + " from frame " + id);
		frametable.set(id, frame);
	}
	
	public static int Mod(int x, int y, Process p){
		return (x - y + p.size)%p.size;
	}
	
	public static void chooseReference(Process process, Scanner random) throws FileNotFoundException{
		int rand = Integer.parseInt(random.nextLine());
		//System.out.println(rand);
		int S = process.size;
		double y = rand/(RAND_MAX + 1d); //1
		if (y < process.A){
			process.rindex = (process.rindex+1+S)%S;
		}else if (y < (process.A + process.B)){
			process.rindex = (process.rindex-5+S)%S;
		}else if (y < (process.A + process.B + process.C)){
			process.rindex = (process.rindex+4+S)%S;
		}else{
			rand = Integer.parseInt(random.nextLine());
			process.rindex=(rand+S)%S; //hmm
			//choose random and mod by number of frames for frame index to put in 
		}

		
	}
	
	public static void Run(ArrayList<Frame> frametable, Hashtable<Integer,Process> processes, String algo, int P) throws FileNotFoundException{
		Scanner random = new Scanner(new FileInputStream("random.txt"));
		int count = 1;
		int quantum = 0;
		int N = processes.get(1).references;
		int check = 0;
		while (quantum < N/3){ 
			for (int x = 1; x <= processes.size(); x++){
				Process process = processes.get(x);
				
				for (int y = 0; y < 3; y++){ //three refs
					int page = process.rindex/P;
					check = 0;

					for (int z = frametable.size()-1; z >= 0; z--) {
						if (frametable.get(z) == null){
							Frame frame = new Frame();
							if (algo.equals("lifo")){
								frame.compare = 1;
							}
							frame.process = x;
							frame.page = page;
							frame.count = count;
							frame.last = count;
							frame.fault = count;
							process.faults++;
							frametable.set(z,frame);
							//System.out.println(x + " references word " + process.rindex + " (page " + page + ") " + "at time " + count + ": Fault, using free frame " + z);
							break;
						}else if (frametable.get(z).page == page && frametable.get(z).process == x){
							frametable.get(z).last = count;
							//System.out.println(x + " references word " + process.rindex + " (page " + page + ") " + "at time " + count + ": Hit in frame " + z);
							break;
						}else if (z == 0){
							check = 1;
						}
					}
					if (check == 1){
						if (algo.equals("lru")){
							LRU(frametable, processes, process.id, page, count);
						}else if (algo.equals("lifo")){
							LIFO(frametable, processes, process.id, page, count);
						}else if (algo.equals("random")){
							RANDOM(frametable, processes, process.id, page, count, random);
						}
					}
					chooseReference(process, random);
					count++;
				}
			}
			quantum++;
		}
		
		for (int x = 1; x <= processes.size(); x++){
			Process process = processes.get(x);
			
			for (int y = 0; y < N%3; y++){
				int page = process.rindex/P;
				check = 0;

				for (int z = frametable.size()-1; z >= 0; z--) {
					if (frametable.get(z) == null){
						Frame frame = new Frame();
						if (algo.equals("lifo")){
							frame.compare = 1;
						}
						frame.process = x;
						frame.page = page;
						frame.last = count;
						frame.count = count;
						frame.fault = count;
						process.faults++;
						//System.out.println(x + " references word " + process.rindex + " (page " + page + ") " + "at time " + count + ": Fault, using free frame " + z);
						frametable.set(z,frame);
						break;
					}else if (frametable.get(z).page == page && frametable.get(z).process == x){
						frametable.get(z).count = count;
						//System.out.println(x + " references word " + process.rindex + " (page " + page + ") " + "at time " + count + ": Hit in frame " + z);
						break;
					}else if (z == 0){
						check = 1;
					}
				}
				if (check == 1){
						if (algo.equals("lru")){
							LRU(frametable, processes, process.id, page, count);
						}else if (algo.equals("lifo")){
							LIFO(frametable, processes, process.id, page, count);
						}else if (algo.equals("random")){
							RANDOM(frametable, processes, process.id, page, count, random);
						}
					}
				chooseReference(process, random);
				count++;

			}
		}
		
				
		
	}
	
	
	public static void main(String[] args) throws FileNotFoundException{
		int M = 0; //machine size in words
		int P = 0; //page size in words
		int S = 0; //size of each process
		int J = 0; //job mix
		int N = 0; //num of references per process
		String R = ""; //replace algo
		
		if (args.length == 6){ //get files name from argument
			M = Integer.parseInt(args[0]);
			P = Integer.parseInt(args[1]);
			S = Integer.parseInt(args[2]);
			J = Integer.parseInt(args[3]);
			N = Integer.parseInt(args[4]);
			R = args[5];
		}else{
			System.err.println("Incorrect args try again");
			return;
		}
		
		System.out.println("The machine size is " + M);
		System.out.println("The page size is " + P);
		System.out.println("The process size is " + S);
		System.out.println("The job mix number is " + J);
		System.out.println("The number of references per process is " + N);
		System.out.println("The replacement algorithm is " + R);
		System.out.println();
		
		int pages = S/P;
		int frames =  M/P;
			
		ArrayList<Frame> frametable = new ArrayList<Frame>();
		
		for (int x = 0; x < frames; x++){
			frametable.add(x,null);
		}

		
		Hashtable<Integer,Process> processes = new Hashtable<Integer,Process>();
		if (J == 1){
			Process p = new Process();
			p.id = 1;
			p.A = 1;
			p.B = 0;
			p.C = 0;
			p.size = S;
			p.rindex = (111*1+S)%S;
			p.references = N;
			processes.put(1,p);
			
		}else if (J == 2){
			for (int x = 1; x < 5; x++){
				Process p = new Process();
				p.id = x;
				p.A = 1;
				p.B = 0;
				p.C = 0;
				p.size = S;
				p.rindex = (111*x+S)%S;
				p.references = N;
				processes.put(x,p);
			}
		}else if (J == 3){
			for (int x = 1; x < 5; x++){
				Process p = new Process();
				p.id = x;
				p.A = 0;
				p.B = 0;
				p.C = 0;
				p.size = S;
				p.rindex = (111*x+S)%S;
				p.references = N;
				processes.put(x,p);
			}
		}else if (J == 4){
			Process p1 = new Process();
			Process p2 = new Process();
			Process p3 = new Process();
			Process p4 = new Process();
			
			p1.id = 1;
			p1.A = .75;
			p1.B = .25;
			p1.C = 0;
			p1.size = S;
			p1.rindex = (111*1+S)%S;
			p1.references = N;
			
			p2.id = 2;
			p2.A = .75;
			p2.B = 0;
			p2.C = .25;
			p2.size = S;
			p2.rindex = (111*2+S)%S;
			p2.references = N;
			
			p3.id = 3;
			p3.A = .75;
			p3.B = .125;
			p3.C = .125;
			p3.size = S;
			p3.rindex = (111*3+S)%S;
			p3.references = N;
			
			p4.id = 4;
			p4.A = .5;
			p4.B = .125;
			p4.C = .125;
			p4.size = S;
			p4.rindex = (111*4+S)%S;
			p4.references = N;
			
			processes.put(1,p1);
			processes.put(2,p2);
			processes.put(3,p3);
			processes.put(4,p4);
		}
		
		Run(frametable, processes, R, P);
		
		int total_faults = 0;
		int total_evictions = 0;
		int total_residency = 0;
		for (int x = 1; x <= processes.size(); x++){
			int total_residency_process = 0;
			total_faults += processes.get(x).faults;
			total_evictions += processes.get(x).evictions;
			if (processes.get(x).evictions > 0){
				total_residency_process += processes.get(x).residency;
				System.out.println("Process " + x + " had " + processes.get(x).faults + " faults and "+ ((double) total_residency_process/processes.get(x).evictions) + " average residency.");
			}else{
				System.out.println("Process " + x + " had " + processes.get(x).faults + " faults.");
				System.out.println("\tWith no evictions, average residency is undefined.");
			}
			total_residency += total_residency_process;
		}
		if (total_evictions == 0){
			System.out.println("\nThe total number of faults is " + total_faults + ".");
			System.out.println("\tWith no evictions, the overall average residence is undefined.");
		}else{
			System.out.println("\nThe total number of faults is " + total_faults + " and the overall average residency is " + ((double) total_residency/total_evictions)+".");
		}
	}

}

