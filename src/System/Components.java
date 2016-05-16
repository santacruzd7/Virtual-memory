package System;

/**
 * The Components class will implement the components in which a VA can be broken: s (ST index), p (PT index), w (offset).
 * It also includes sp for compatibility with translations using the TLB.
 * @author David García Santacruz, ID#: 51062654
 */
public class Components {
	int s;
	int p;
	int sp;
	int w;
	
	/**
	 * Constructor. Initializes the values of s, p, sp, and w given a VA.
	 * @param VA	integer representing the VA.
	 */
	public Components(int VA){
		s = VA>>19;		// s p w -> 0 0 s
		
		sp = VA>>9;		// s p w -> 0 s p
		
		int temp = sp<<9;	// 0 s p -> s p 0
		w = VA - temp;		// 0 0 w
		
		temp = s<<19;		// 0 0 s -> s 0 0
		temp = VA - temp;	// 0 p w 
		p = temp>>9;		// 0 p w -> 0 0 p
	}
	
	
	/**
	 * Prints a visual representation of the components of a VA (for debugging purposes).
	 */
	public String toString(){
		return "s: " + s + ";  p: " + p + ";  w: " + w + ";  sp: " + sp;
	}
}