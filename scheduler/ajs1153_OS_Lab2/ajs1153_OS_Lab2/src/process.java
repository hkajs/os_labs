
public class process implements Comparable<process>{
	int A;
	int B;
	int C;
	int M;
	
	int cpu_time = 0; //how much cpu time is required to finish process
	int finishing_time = 0; //time mark when this process has ended
	int turnaround_time = 0; //finishing time - A
	int io_time = 0; //how much time it has spent in io
	int waiting_time = 0; //how much time it has been ready but not processes
	
	int finished = 0; //fcfs = 1, lcfs = 2, rr = 3, hprn = 4
	int process_index = 0;
	
	int blocked = 0;
	int overall_time = 0;
	int burst = 0;
	int ratio = 0;
	
	public process(){
	}
	
	
	public process(int A, int B, int C, int M){
		this.A = A;
		this.B = B;
		this.C = C;
		this.M = M;
	}
		
	public void setA(int i){
		this.A = i;
	}
	public void setB(int i){
		this.B = i;
	}
	public void setC(int i){
		this.C = i;
		this.cpu_time = i;
	}
	public void setM(int i){
		this.M = i;
	}
	
	public void setPindex(int i){
		process_index = i;
	}
	
	public void ratioize(int count){
		
		if (cpu_time == C){
			ratio = (count - A)/Math.max(1, C - cpu_time);
		}else{
			ratio = (count - A) / (C - cpu_time);
		}
	}


	@Override
	public int compareTo(process o) {
		// TODO Auto-generated method stub
		if (finished == 0){
			if (this.A < o.A){
					return -1;
				}else if (this.A == o.A){
					return 0;
				}else{
					return 1;
				}
		}else{
			if (this.process_index < o.process_index){
				return -1;
			}else if (this.process_index == o.process_index){
				return 0;
			}else{
				return 1;
			}
		}
	}

}
