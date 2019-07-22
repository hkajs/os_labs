
public class Resource {
	int id = 0; //id of resource
	int available = 0; //currently available
	int released = 0; //released (available next cycle)
	int used = 0; //used
	
	public Resource(int i){
		this.id = i;
	}
	
}
