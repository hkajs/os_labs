import java.util.ArrayList;

public class Task {
	int id = 0; //id of task
	int resource = 0; //held
	int[] held; //array of held resources
	ArrayList<Integer> claims = new ArrayList<Integer>(); //arraylist of claims
	int cycle = 0; //cycle current on
	int waiting = 0; //how long this has waited
	String terminated = "0"; //if its terminated, aborted or neither, and finishing time if normal termination occurs
	ArrayList<Activity> activities = new ArrayList<Activity>(); //list of actions
	
	public Task(int i){
		this.id = i;
	}
	
}
