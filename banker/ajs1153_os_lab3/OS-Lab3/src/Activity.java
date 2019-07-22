
public class Activity {
	String activity = ""; //action type
	int task = 0; //task id
	int resource = 0; //resource id
	int action = 0; //how much is being requested or released
	
	public Activity(String a, int b, int c, int d){
		this.activity = a;
		this.task = b;
		this.resource = c;
		this.action = d;
	}
	

}
