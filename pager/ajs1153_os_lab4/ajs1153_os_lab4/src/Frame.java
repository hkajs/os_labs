
public class Frame implements Comparable<Frame>{
	
	/*
	 * 
	 * The pager routine processes each reference and determines if a fault occurs, in which case it makes this page resi- dent. 
	 * If there are no free frames for this faulting page, a resident page is evicted using replacement algorithm R. 
	 * The algorithms are global (i.e., the victim can be any frame not just ones used by the faulting process). 
	 * Because the lab only simulates demand paging and does not simulate the running of actual processs, 
	 * I believe you will find it eas- iest to just implement a frame table 
	 * (see next paragraph) and not page tables. My program is written that way. (This is advice not a requirement.)
		As we know, each process has an associated page table, which contains in its ith entry the number of the frame con- taining this process’s ith page (or an indication that the page is not resident). 
		The frame table (there is only one for the entire system) contains the reverse mapping: The ith entry in the frame table specifies the page contained in the ith frame (or an indication that the frame is empty).
 		Specifically the ith entry contains the pair (P, p) if page p of process P is contained in frame i.
	 */
	int count = 0;
	int last = 0;
	int id = 0;
	int size = 0;
	int fault = 0;
	int page = 0;
	int process = 0;
	int compare = 0;
	@Override
	public int compareTo(Frame o) {
		// TODO Auto-generated method stub
		if (compare == 0){
			if (this.last < o.last){
				return -1;
			}else if (this.last == o.last){
				return 0;
			}else{
				return 1;
			}
		}else{
			if (this.fault < o.fault){
				return -1;
			}else if (this.fault == o.fault){
				return 0;
			}else{
				return 1;
			}
		}
	}

}
