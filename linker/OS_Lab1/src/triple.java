
public class triple {
	String symbol = "";
	int address = 0;
	int module = 0;
	public int used = 0;
	
	public triple(String symbol, int address, int module){
		this.symbol = symbol;
		this.address = address;
		this.module = module;
	}
	
	public int getUsed(){
		if (this.used == 1){
			return 1;
		}else{
			return 0;
		}
	}
	public void setUsed(int i){
		used = i;
	}

}
