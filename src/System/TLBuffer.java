package System;

/**
 * The TLBuffer class will implement an entry of the TLB (Translation Look-Aside Buffer).
 * @author David García Santacruz, ID#: 51062654
 */
public class TLBuffer {

	int LRU;	// Least Recently Used information: 0 - 3
	int sp;		// Combined s, and p components of a VA
	int f;		// Starting PA of the frame
	
	
	/**
	 * Constructor.
	 * @param least		LRU value.
	 * @param segpag	sp value.
	 * @param frame		f value.
	 */
	public TLBuffer(int least, int segpag, int frame){
		LRU = least;
		sp = segpag;
		f = frame;
	}
}
