import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class lab_1 {	
	//ajs1153
	//Anthony Schalhoub
	//Lab 1 Linker
	//1 pass but very inefficient
	
	/*STRUCTURE OF EXAMPLE INPUT
	 * module 1 {
	 * line 1 { 1 xy 2 } (1 is part 1, xy is part 2, 2 is part 3)
	 * line 2 { 2 z 2 -1 xy 4 -1 } (keeps going until while loop reaches the size # of -1's; in this case two -1's)
	 * line 3 { 5 10043 56781  20004  80023 70014 } (addresses)
	 * module 2 
	 * line 1 { ... }
	 * ...
	 */
	public static void main(String[] args){
		ArrayList<String> errors = new ArrayList<String>();
		ArrayList<tuple> addresses = new ArrayList<tuple>();
		ArrayList<triple> triples = new ArrayList<triple>();
		ArrayList<triple> size_comparison = new ArrayList<triple>();
		Hashtable<String, Integer> symbols = new Hashtable<String, Integer>(); // string = x, integer = 15
		Scanner sc = new Scanner(System.in);
		HashSet<String> set = new HashSet<String>();
		//Scanner sc2 = new Scanner(System.in);
		String in = "";
		final int machinesize = 300; //if absolute address > 300 then error
		int part = 0; //parts of a line
		int line = 0; //lines of a block (each block has 3 lines)
		int size = 0; //size of a line (to check when to go the next line)
		int inlinecount = 0; //check against size to go to next line
		int modules = 0; //# of modules
		int currmod = 1; //current module that while loop is on
		int relativeindex = 0;
		int absoluteindex = 0; //+1 everytime new address is added to arraylist, then used to calculate symbol values
		String var = ""; //name of symbol/key for hashtable
		int location = 0; //local value of symbol, add to index to get real value
		int counter = 0; //to make getting # of modules easier
		//could either do for loop to check if any definitions pass size of addresses of module or i could 
		while (sc.hasNext() && ((currmod <= modules) || currmod == 1)){
			in = sc.next();
			//System.out.println(in);

			//System.out.print(in + " ");
			if (counter == 0){
				counter++;
				modules = Integer.parseInt(in);
			}else if (currmod <= modules){
				//first line is always setting symbols
				//second line is setting locations of which addresses on which symbols are used
				//third line is addresses
				if (line == 0){
					if(part == 0){
						size = Integer.parseInt(in);
						part = 1;
					}else if (part == 1){
						var = in;
						part = 2;
					}else if (part == 2){
						location = Integer.parseInt(in);
						triple size_trip = new triple(var, location, currmod);
						size_comparison.add(size_trip);
						inlinecount++;
						part = 3;
					}
					Integer loc = new Integer(location+absoluteindex);
					if (size != 0 && part == 3){
						if (symbols.containsKey(var)){
							errors.add("Error: Symbol " + var + " is multiply defined; last value will be used");
						}
						symbols.put(var, loc);
						set.add(var);
						part = 1;
					}
					if (inlinecount == size){
						line++;
						inlinecount = 0;
						part = 0;
					}
					
				}else if (line == 1){ //planning to use arraylist of TUPLES to pair symbol to addresses

					if(part == 0){
						size = Integer.parseInt(in); //correct
					}else if (part == 1 && !in.equals("-1")){
						var = in;
						set.add(var);
					}else if (part > 1 && !in.equals("-1")){
						triple trip = new triple(var, Integer.parseInt(in), currmod);
						triples.add(trip);
					}
					if (in.equals("-1")){
						part = 0;
						inlinecount++;
					}
					if (inlinecount == size){
						line++;
						inlinecount = 0;
						part = -1;
					}
					part++;
					
				}else if (line == 2){

					if(part == 0){
						size = Integer.parseInt(in);						
						for (int x = 0; x < size_comparison.size(); x++){
							if (size_comparison.get(x).address > size-1){
								errors.add("Error: Definition of symbol " + size_comparison.get(x).symbol + " exceeds size of address space on module " + size_comparison.get(x).module + "; last word in module used");
								symbols.replace(size_comparison.get(x).symbol, (absoluteindex +  (size - 1)));
							}
						}
						size_comparison.clear();
					}else if (part >= 1 && part <= size){
						int address = Integer.parseInt(in);
						//System.out.println(in.charAt(4));
						if (in.charAt(4) == '2' && Integer.parseInt(in.substring(1,4)) > 299){
							char digit = in.charAt(0);
							errors.add("Error: Absolute address of address with index " +  absoluteindex + " exceeds machine size; largest legal value used");
							in = digit + "2992";
							address = Integer.parseInt(in);
						}
						if (in.charAt(4) == '3'){
							address /= 10;
							address += relativeindex;
						}else{
							address /= 10;
						}
						tuple tup = new tuple(address, currmod);
						addresses.add(tup);
						absoluteindex++;
					}
					if (part == size){
						line = 0;
						part = -1;
						currmod++;
						relativeindex = absoluteindex;
					}
					part++;
				}
			}else{
				//throw module size error
			}			
		}
		System.out.println("Symbol Table");
		Set<String> keys = symbols.keySet();
		for(String key: keys){
            System.out.println(key + " = " + symbols.get(key));
		}
		//keep an iterator for each module
		int y = 0;
		int x = 0;
		while (x < triples.size()){
			for (int z = 0; z < addresses.size(); z++){
				if (triples.get(x).module == addresses.get(z).module){
					if (triples.get(x).address == y){ 
						String addr = Integer.toString(addresses.get(z).address);
						char digit = addr.charAt(0);
						String addr2 = digit + "000";
						addresses.get(z).address = Integer.parseInt(addr2);
						try {
							if (addresses.get(z).modified == 1){
								errors.add("Error: Multiple variables used in instruction on address with index " + z + "; all but last ignored");
							}
							addresses.get(z).address += symbols.get(triples.get(x).symbol);
							addresses.get(z).modified = 1;
						}catch(Exception e){
							errors.add("Error: Undefined symbol used in address with index " + z + "; value replacement is 111");
							addresses.get(z).address += 111;
						}
					}
					y++;
				}
			}
			y = 0;
			x++;
		}
		System.out.println();
		System.out.println("Memory Map");
		int ind = 0;
		for (int z = 0; z < addresses.size(); z++){
			System.out.print(ind + ":" + "  ");
			System.out.println(addresses.get(z).address);
			ind++;
		}
		for (int w = 0; w < triples.size(); w++){
			if (set.contains(triples.get(w).symbol)){
				set.remove(triples.get(w).symbol);
			}
		}
    	System.out.println();
    	for (int err = 0; err < errors.size(); err++){
    		System.out.println(errors.get(err));
    	}
		Iterator<String> it = set.iterator();
	     while(it.hasNext()){
	        System.out.println("Warning: " + it.next() + " was defined but was not used");
	     }
	}
}


/*
 * 
 * If a symbol is multiply defined, print an error message and use the value given in the last definition. done
• If a symbol is used but not defined, print an error message and use the value 111. done
• If a symbol is defined but not used, print a warning message and continue. done
• If an absolute address exceeds the size of the machine, print an error message and use the largest legal value. done
• If multiple symbols are listed as used in the same instruction, print an error message and ignore all but the last usage
given. done
• If an address appearing in a definition exceeds the size of the MODULE, print an error message and treat the address
given as the last word in the module. not started
*/
