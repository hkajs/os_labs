import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class ResourceAllocator {
	
	public static void calcNeed(int[][] need, int[][] max, int[][] allocated){ 
		for (int x = 0; x < need.length; x++){
			for (int y = 0; y < need[0].length; y++){
				need[x][y] = max[x][y] - allocated[x][y]; //calculates the need array based on max - allocated
			}
		}
	}
	
	public static int[] isSafe(ArrayList<Task> tasks, Hashtable<Integer, Resource> resources, int[] available, int[][] max, int[][] allocated, int[][] need){
		
				
		boolean[] finish = new boolean[tasks.size()]; //initiate boolean finish array
		for (int x = 0; x < finish.length; x++){ //set all to false
			finish[x] = false;
		}
		
		int[] safeSeq = new int[tasks.size()]; //initiate safe sequence array
		
		int[] work = new int[resources.size()]; //initiate work array
		
		for (int x = 0; x < resources.size(); x++){
			work[x] = available[x]; //set to current available matrix
		}
		
		int count = 0;
		while (count < tasks.size()){ //iterate through tasks
			
			boolean found = false;
			for (int y = 0; y < tasks.size(); y++){ //iterate through tasks in each iteration
				
				if (finish[y] == false){ //if finish is false as in it hasn't been tested yet
					int z;
					for (z = 0; z < resources.size(); z++){ //run through resources to check if any are impossible
						if (need[y][z] > work[z]){
							break; //stop z from passing next if because unsafe
						}
					}
					if (z == resources.size()){ //if iterations reaches resource size all are safe
						for (int i = 0; i < resources.size(); i++){
							work[i] += allocated[y][i]; //safe
						}
						safeSeq[count] = y; //add to sequence
						finish[y] = true; //switch to true
						found = true; //switch to true
						count++;
					}
				}
			}
				
			if (found == false){
				return null; //if no safesequence found return null
			}
		}

		return safeSeq; //return safeseq
		
	}
	
	public static void rebuildMatrices(ArrayList<Task> runnable, Hashtable<Integer,Resource> resources, int[][] max, int[][] allocated){
		//this rebuilds the matrices to prevent aborted/terminated tasks from being part of allocated/max
		for (int x = 0; x < runnable.size(); x++){
			for (int y = 0; y < runnable.get(x).claims.size(); y++){
				max[x][y] = runnable.get(x).claims.get(y);
			}
		}
		
		for (int x = 0; x < runnable.size(); x++){
			for (int y = 0; y < resources.size(); y++){
				allocated[x][y] = runnable.get(x).held[y+1];
			}
		}
		
	}
	
	public static void Banker3(Hashtable<Integer,Task> tasks, Hashtable<Integer,Resource> resources){
		int terminated = 0;
		int count = 0;
		ArrayList<Task> aborted = new ArrayList<Task>();
		ArrayList<Task> runnable = new ArrayList<Task>();
		
		//go through the tasks and abort any tasks with claims not possible
		for (int x = 1; x <= tasks.size(); x++){ 
			for (int y = 0; y < tasks.get(x).activities.size(); y++){
				if (tasks.get(x).activities.get(y).activity.equals("initiate")){ //while request type is initiate
					tasks.get(x).claims.add(tasks.get(x).activities.get(y).action); //add to claims of that task
				}
			}
		}
		
		for (int x = 1; x <= tasks.size(); x++){ //this part aborts anything that has a claim larger than what the resources available is
			runnable.add(tasks.get(x));
			for (int y = 0; y < tasks.get(x).claims.size(); y++){ 
				if (tasks.get(x).claims.get(y) > resources.get(y+1).available){
					tasks.get(x).terminated = "aborted";
					tasks.get(x).activities.clear();
					terminated++;
					aborted.add(tasks.get(x));
					runnable.remove(x-1);//
					System.out.println("abort");
					break;
				}
			}
		}
		
		int[][] max = new int[runnable.size()][resources.size()];
		int[][] allocated = new int[runnable.size()][resources.size()];
		int[][] need = new int[runnable.size()][resources.size()];
		int[] available = new int[resources.size()];
		
		for (int x = 0; x < runnable.size(); x++){ //this creates the matrix that only holds any tasks that are not terminated or aborted
			for (int y = 0; y < runnable.get(x).claims.size(); y++){
				max[x][y] = runnable.get(x).claims.get(y);
				allocated[x][y] = 0;
			}
		}
		
		for (int x = 1; x <= resources.size(); x++){ //this creates the array which holds all the available values for resources
			available[x-1] = resources.get(x).available;
		}
		
		calcNeed(need, max, allocated); //this calculates need matrix

				
		ArrayList<Activity> waiting = new ArrayList<Activity>();
		while (terminated < tasks.size()){ //this is the cycle loop, as long as not all are terminated it keeps going
			ArrayList<Integer> indexes = new ArrayList<Integer>(); //this holds all the task ids that are in waiting to stop them from running in the normal part
			for (int o = 0; o < waiting.size(); o++){ //iterate through waiting tasks
				int check = 0;
				if (waiting.size() > 0){ 
					int[][] allocatedcopy = new int[allocated.length][allocated[0].length]; // create copy of allocated
					for (int x = 0; x < allocated.length; x++){
						for (int y = 0; y < allocated[0].length; y++){
							allocatedcopy[x][y] = allocated[x][y];
						}
					}
					Activity action = waiting.get(o); //set action to current waiting action
					indexes.add(action.task);  //task id added to what has been in waiting this cycle
					
					int index;
					for (index = 0; index < runnable.size(); index++){ //goes through runnable to get the index of the current task in runnable
						if (runnable.get(index).id == action.task){
							break;
						}
					}
					
					calcNeed(need, max, allocated);
					if (action.action <= need[index][action.resource-1]){ //checks if the request is small enough to be possible in terms of claims
						if (action.action <= available[action.resource-1]){ //checks if there is enough units in available
							allocatedcopy[index][action.resource-1] += action.action; //changes allocated copy to show changes that could potentially happen
							available[action.resource-1] -= action.action;
							need[index][action.resource-1] -= action.action;
							if (isSafe(runnable, resources, available, max, allocatedcopy, need) != null){ //checks if this change in allocated is safe
								for (int y = 0; y < allocated.length; y++){
									for (int z = 0; z < allocated[0].length; z++){
										allocated[y][z] = allocatedcopy[y][z];
									}
								}
								tasks.get(action.task).held[action.resource] += action.action;
								resources.get(action.resource).available -= action.action;
								resources.get(action.resource).used += action.action;
								check = 1;
							}else{ //else if not safe reverse changes to available and need
								available[action.resource-1] += action.action; 
								need[index][action.resource-1] += action.action;
							}
						}
					}
					tasks.get(action.task).cycle++; //add to cycle time
					tasks.get(action.task).waiting++; //add to waiting time

					if (check == 1){
						tasks.get(action.task).activities.remove(0);  //if request was successful remove request from task
						waiting.remove(o); //remove from waiting
						o--; //move index back down
					}
				}
			}
			
			for (int x = 1; x <= tasks.size(); x++){ //go through non-waiting tasks
				int check = 0;
				if (runnable.contains(tasks.get(x)) && !indexes.contains(x) && tasks.get(x).activities.size() > 0){ //if not in waiting and not terminated
					Activity action = tasks.get(x).activities.get(0); //get action

					int index;
					for (index = 0; index < runnable.size(); index++){ //get index in runnable of current task
						if (runnable.get(index).id == action.task){
							break;
						}
					}

					if (!action.activity.equals("compute")){ //if action is not compute
						if (action.activity.equals("initiate")){ //if its initiate
							check = 1;
							tasks.get(action.task).held = new int[resources.size()+1]; //initiate
							tasks.get(action.task).cycle++; //add to cycle	
						
						}else if (action.activity.equals("request")){ //else if its request units
							int[][] allocatedcopy = new int[allocated.length][allocated[0].length]; //build copy of allocated
							for (int y = 0; y < allocated.length; y++){
								for (int z = 0; z < allocated[0].length; z++){
									allocatedcopy[y][z] = allocated[y][z];
								}
							}
							calcNeed(need, max, allocated); //calculate need matrix
							if (action.action <= need[index][action.resource-1]){ //if its not an illegal request
								if (action.action <= available[action.resource-1]){ //if its possible in terms of available
									allocatedcopy[index][action.resource-1] += action.action; //change all matrixes in to check if safe
									available[action.resource-1] -= action.action;
									need[index][action.resource-1] -= action.action;
									if (isSafe(runnable, resources, available, max, allocatedcopy, need) != null){ //if the allocated copy is safe
										for (int y = 0; y < allocated.length; y++){ //set allocated to the copy of allocated
											for (int z = 0; z < allocated[0].length; z++){
												allocated[y][z] = allocatedcopy[y][z];
											}
										}
										tasks.get(action.task).held[action.resource] += action.action;
										resources.get(action.resource).available -= action.action;
										resources.get(action.resource).used += action.action;
										check = 1;
									}else{
										waiting.add(runnable.get(index).activities.get(0)); //add to waiting if not safe
										available[action.resource-1] += action.action;
										need[index][action.resource-1] += action.action;
									}
								}else{
									waiting.add(runnable.get(index).activities.get(0)); //add to waiting if not enough available
								}
								
							}else{ //abort if its an illegal request
								
								runnable.remove(index); //remove from runnable
								
								rebuildMatrices(runnable, resources, max, allocated); //rebuild matrices
 								int released = 0;
								for (int y = 1; y < tasks.get(action.task).held.length; y++){ //release all units held by aborted task
									released += tasks.get(action.task).held[y];
									resources.get(y).released += tasks.get(action.task).held[y];
								}
								
								
								for (int z = 0; z < waiting.size(); z++){ //remove task from waiting just in case
									if (waiting.get(z).task == tasks.get(action.task).id){
										waiting.remove(z);
									}
								}
								
								tasks.get(action.task).activities.clear(); //clear its action list
								tasks.get(action.task).terminated = "aborted"; //set to aborted
								terminated++; //add to terminated counter
							}
							tasks.get(action.task).cycle++; //add to cycle
							
						}else if (action.activity.equals("release")){ //if action is release, release units
							allocated[index][action.resource-1] -= action.action;
							tasks.get(action.task).held[action.resource] -= action.action; 
							resources.get(action.resource).released += action.action;
							resources.get(action.resource).used -= action.action;
							check = 1;
							tasks.get(action.task).cycle++; //add to cycle
						}
						
						if (check == 1){
							tasks.get(x).activities.remove(0); //this only runs if action was successful
						}
						
						if (tasks.get(x).terminated.equals("0") && tasks.get(x).activities.size() > 0 && tasks.get(x).activities.get(0).activity.equals("terminate")){ //check if next action is terminate
							runnable.remove(index); //remove from runnable tasks
							rebuildMatrices(runnable, resources, max, allocated); //rebuild
							tasks.get(action.task).terminated = Integer.toString(tasks.get(action.task).cycle); //set finish time
							terminated++; //add to terminated counter
							tasks.get(x).activities.remove(0); //remove action from action list
						}
					
					}else{ //if action was compute
						tasks.get(x).cycle++; //add to cycle
						action.resource--; //subtract 1 from computation time
						if (action.resource == 0){ //when compute time reaches 0
							tasks.get(x).activities.remove(0); //remove action
						}
						
						if (tasks.get(x).terminated.equals("0") && tasks.get(x).activities.size() > 0 && tasks.get(x).activities.get(0).activity.equals("terminate")){ //if next action is terminate
							runnable.remove(index); //remove from runnable
							rebuildMatrices(runnable, resources, max, allocated); //rebuild matrices
							
							tasks.get(action.task).terminated = Integer.toString(tasks.get(action.task).cycle); //set finish time
							terminated++; //add to terminated counter
							tasks.get(x).activities.remove(0); //remove from action list
						}
					}
				}
			}
			
			for (int x = 0; x < resources.size(); x++){ //move released units from this cycle into available for next cycle
				available[x] += resources.get(x+1).released;
				resources.get(x+1).available += resources.get(x+1).released;
				resources.get(x+1).released = 0;
			}
			count++; //for debugging
		}
	}
	
	
	public static void FIFO(Hashtable<Integer,Task> tasks, Hashtable<Integer,Resource> resources){
		int terminated = 0;
		int count = 0;
		ArrayList<Activity> waiting = new ArrayList<Activity>();
		while (terminated < tasks.size()){ //cycle while loop while not all tasks are terminated
			count++;
			int tcheck = 0;
			int unfulfilled = 0;
			ArrayList<Integer> requests = new ArrayList<Integer>(); //this is to help with aborting multiple tasks in one cycle
			ArrayList<Integer> indexes = new ArrayList<Integer>(); //to hold indexes of tasks that were in waiting this cycle
			for (int o = 0; o < waiting.size(); o++){ //go through waiting list
				int check = 0;
				if (waiting.size() > 0){
					Activity action = waiting.get(o); //get current action
					indexes.add(action.task); //add current action's task's id to indexes
					
					if (resources.get(action.resource).available >= action.action){ //if its possible
						tasks.get(action.task).held[action.resource] += action.action; //add to held
						resources.get(action.resource).available -= action.action; //subtract from available
						resources.get(action.resource).used += action.action; //add to used
						check = 1;
					}else{
						unfulfilled++; //if not add to unfulfilled counter (for aborting conditional)
						requests.add(action.action); //add action to the requests list
					}
					tasks.get(action.task).cycle++; //add to cycle
					tasks.get(action.task).waiting++; //add to waiting
					if (check == 1){
						tasks.get(action.task).activities.remove(0); //if successful request remove from waiting and from task's activity list
						waiting.remove(o);
						o--; //move index back 1
					}
				}
			}
			
			
			for (int x = 1; x <= tasks.size(); x++){ //go through the non-waiting tasks list
				int check = 0;
				if (!indexes.contains(x) && tasks.get(x).activities.size() > 0){ //check if this task was acted on during the waiting code block
					Activity action = tasks.get(x).activities.get(0); //set action

					if (!action.activity.equals("compute")){ //if not compute
						if (action.activity.equals("initiate")){ //if initiate
							check = 1;
							tasks.get(action.task).held = new int[resources.size()+1]; //initiate held
							tasks.get(action.task).cycle++;	//add to cycle
						
						}else if (action.activity.equals("request")){ //if action is request units
							if (resources.get(action.resource).available >= action.action){ //check if it is possible
								tasks.get(action.task).held[action.resource] += action.action; //if it is then add to held
								resources.get(action.resource).available -= action.action; //subtract from available
								resources.get(action.resource).used += action.action; //add to used
								check = 1;

							}else{ //if not possible
								unfulfilled++; //add to unfulfilled
								requests.add(action.action); //add to requests that were not possible this cycle
								waiting.add(action); //add to waiting list
							}
							tasks.get(action.task).cycle++; //add to cycle time
							
						}else if (action.activity.equals("release")){ //if action was release
							tasks.get(action.task).held[action.resource] -= action.action; //release from held
							resources.get(action.resource).released += action.action; //add to released this cycle
							resources.get(action.resource).used -= action.action;
							check = 1;
							tasks.get(action.task).cycle++; //add to cycle time

						}
						
						if (check == 1){
							tasks.get(x).activities.remove(0); //this only runs if action is successfully performed
						}
						
						if (tasks.get(x).terminated.equals("0") && tasks.get(x).activities.size() > 0 && tasks.get(x).activities.get(0).activity.equals("terminate")){ //if next action is terminate
							tcheck++;
							tasks.get(action.task).terminated = Integer.toString(tasks.get(action.task).cycle); //terminate task
							terminated++; //add to terminate counter
							tasks.get(x).activities.remove(0); //remove from action list
						}
					
					}else{ //if action is compute
						
						tasks.get(x).cycle += action.resource; //add to compute time to cycle
						tasks.get(x).activities.remove(0); //remove from activities
						if (tasks.get(x).terminated.equals("0") && tasks.get(x).activities.size() > 0 && tasks.get(x).activities.get(0).activity.equals("terminate")){ //check if next action is terminate
							tcheck++;
							tasks.get(action.task).terminated = Integer.toString(tasks.get(action.task).cycle); //set finish time
							terminated++; //add to terminated counter
							tasks.get(x).activities.remove(0); //remove from action list
						}
					}					
				}
			}
			
			
			if (tcheck == 0 && unfulfilled == tasks.size() - terminated){ //if number of unfulfilled request equals the number of tasks that are not terminated and nothing had been terminated this cycle
				//not fully generalized for aborting multiple requests
				int minIndex = requests.indexOf(Collections.min(requests)); //get index of smallest request of units
				while (resources.get(1).available + resources.get(1).released <  requests.get(minIndex)){ //while there are not enough units to satisfy the smallest request abort tasks
					for (int y = 0; y < tasks.size(); y++){  //get the first task that is not terminated
						if (tasks.get(y+1).terminated.equals("0")){
							for (int i = 1; i < tasks.get(y+1).held.length; i++){ //release all held units
								resources.get(i).released += tasks.get(y+1).held[i];
								resources.get(i).used -= tasks.get(y+1).held[i];
								tasks.get(y+1).held[i] = 0; 
							}
							tasks.get(y+1).terminated = "aborted"; //abort task
							tasks.get(y+1).activities.clear(); //clear its activity list
							terminated++; //add to terminated counter
							
							for (int z = 0; z < waiting.size(); z++){ //go through waiting and remove actions from task that has been aborted
								if (waiting.get(z).task == tasks.get(y+1).id){
									waiting.remove(z);
								}
							}
							
							break; //stop it from continuing once something has been aborted
						}
					}
				}
			}
			
			for (int x = 0; x < resources.size(); x++){ //move released this cycle to available next cycle
				resources.get(x+1).available += resources.get(x+1).released;
				resources.get(x+1).released = 0;
			}

		}
		
	}
	
	
	
	public static void main(String[] args) throws FileNotFoundException{

		
		String filename="";
		if (args.length == 1){ //get files name from argument
			filename = args[0];
		}else{
			System.err.println("Incorrect args try again");
			return;
		}
		
			
		ArrayList<Task> tasks = new ArrayList<Task>();
		ArrayList<Resource> resources = new ArrayList<Resource>();
		ArrayList<Activity> actions = new ArrayList<Activity>();
		Hashtable<Integer, Task> htasks = new Hashtable<Integer, Task>();
		Hashtable<Integer, Resource> hresources = new Hashtable<Integer, Resource>();
		Hashtable<Integer, Task> htasks2 = new Hashtable<Integer, Task>();
		Hashtable<Integer, Resource> hresources2 = new Hashtable<Integer, Resource>();
		ArrayList<Integer> indexes = new ArrayList<Integer>();
	
	//try {
		File file = new File(filename); //set file object to file name
		Scanner sc = new Scanner(file); //scanner for going through file
		//3 for init input
		//4 for each action
		int count = 0;
		int count2 = 0;
		int count3 = 0;
		
		String a = ""; //action type
		int b = 0; //task id of action
		int c = 0; //resource id of action
		int d = 0; //how much units the action is releasing or requesting
		
		while (sc.hasNext()){
			String inp = "";
			if (count != 2){
				inp = sc.next();
			}
			
			if (count == 0){
				for (int x = 0; x < Integer.parseInt(inp); x++){
					Task task = new Task(x+1); //create new tasks
					Task task2 = new Task(x+1);
					tasks.add(task);
					htasks.put(x+1,task); //add to hashtables of tasks
					htasks2.put(x+1, task2);
				}
				
			}else if (count == 1){
				for (int x = 0; x < Integer.parseInt(inp); x++){
					Resource resource = new Resource(x+1); //create new resource object
					Resource resource2 = new Resource(x+1);

					resources.add(resource); 
					hresources.put(x+1,resource); //add to hashtables of resources
					hresources2.put(x+1,resource2);
				}
				
			}else if (count == 2){
				for (int x = 0; x < resources.size(); x++){
					inp = sc.next();
					resources.get(x).available = Integer.parseInt(inp);
					hresources.get(x+1).available = Integer.parseInt(inp); //set available amount of units to resources
					hresources2.get(x+1).available = Integer.parseInt(inp);

				}
				
			}else if (count2 == 0){
				a = inp; //set action type
				count2++;

				
			}else if (count2 == 1){
				if (b != Integer.parseInt(inp)){
					//get index
					indexes.add(count3); //this code is no longer used because its not sufficient to deal with waiting lists
				}
				b = Integer.parseInt(inp); //set task id of action
				count2++;
				count3++;

			
			}else if (count2 == 2){
				c = Integer.parseInt(inp); //set resource id of action
				count2++;

				
			}else if (count2 == 3){
				d = Integer.parseInt(inp); //set amount of units requested or released
				Activity action = new Activity(a,b,c,d); //maybe switch from index based actions to task object action list field
				Activity action2 = new Activity(a,b,c,d);
				htasks.get(b).activities.add(action); //give activity list to tasks
				htasks2.get(b).activities.add(action2);
				actions.add(action);
				count2 = 0;
			}
			count++;
		}
		
		/*
		 * THE FOLLOWING IS CODE FOR OUTPUTTING FINISHING TIMES AND WAITING TIMES
		 */
		System.out.println(filename);
		System.out.println("	FIFO");
		FIFO(htasks, hresources);
		double total = 0;
		double totalwait = 0;
		for (int x = 0; x < htasks.size(); x++){
			if (htasks.get(x+1).terminated.equals("aborted")){
				System.out.println("Task" + htasks.get(x+1).id + "\t" + htasks.get(x+1).terminated);
			}else{
				System.out.println("Task " + htasks.get(x+1).id + "\t" + htasks.get(x+1).terminated + "\t" + htasks.get(x+1).waiting + "\t" + Math.round(100 * htasks.get(x+1).waiting/Double.parseDouble(htasks.get(x+1).terminated)) + "%");
				total += Integer.parseInt(htasks.get(x+1).terminated);
				totalwait += htasks.get(x+1).waiting;
			}
		}
		System.out.println("total " + "\t" + (int)total + "\t" + (int)totalwait + "\t" + Math.round(100 * totalwait / total) + "%");
		System.out.println("	BANKER'S");
		Banker3(htasks2, hresources2);
		total = 0;
		totalwait = 0;
		for (int x = 0; x < htasks2.size(); x++){
			if (htasks2.get(x+1).terminated.equals("aborted")){
				System.out.println("Task " + htasks2.get(x+1).id + "\t" + htasks2.get(x+1).terminated);
			}else{
				System.out.println("Task " + htasks2.get(x+1).id + "\t" + htasks2.get(x+1).terminated + "\t" + htasks2.get(x+1).waiting + "\t" + Math.round(100 * htasks2.get(x+1).waiting/Double.parseDouble(htasks2.get(x+1).terminated)) + "%");
				total += Integer.parseInt(htasks2.get(x+1).terminated);
				totalwait += htasks2.get(x+1).waiting;
			}
		}
		System.out.println("total " + "\t" + (int)total + "\t" + (int)totalwait + "\t" + Math.round(100 * totalwait / total) + "%");
		System.out.println();
	
			
		//} catch (Exception e){
			//System.out.println(e.getMessage());
		//}
	}

	
}
