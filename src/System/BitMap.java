package System;

/**
 * The BitMap class will implement the bit map that controls which frames in the physical memory are free and which are occupied.
 * It includes functions to mark a frame as used and to search for a free frame as well as some auxiliary functions.
 * @author David García Santacruz, ID#: 51062654
 */
public class BitMap {
	
	int [] BM;			// Bit Map
	int[] MASK;			// Mask to set, reset and search for bits in the BM
	
	public BitMap(){
		BM = new int [32];
		initializeMask();
	}

	
	/**
	 * Initializes the values of the mask that will be used to manipulate individual bits of the BM.
	 */
	private void initializeMask(){
		MASK = new int[32];
		MASK[31] = 1;
		for(int i = 30; i>=0; i--){
			MASK[i] = MASK[i+1] << 1;
		}
	}
	
	
	/**
	 * Marks the given frame as used (set to 1 in the BM).
	 * @param frame frame number.
	 */
	public void markFrameAsUsed(int frame) {
		int i = frame / 32;
		int j = frame % 32;
		BM[i] = BM[i] | MASK[j];
	}

	
	/**
	 * Searches the BM for n consecutive free frames.
	 * @param n	number of consecutive free frames needed.
	 * @returns frame number of the first free frame.
	 */
	public int searchFreeFrame(int n){
		boolean found = false;
		int frame = -1;
		
		for(int i = 0; i<1024 && !found; i++){
			if(checkFreeFrame(i)){
				
				boolean nextFree = true;
				for(int j = 1; j<n && nextFree; j++){
					nextFree = checkFreeFrame(i+j);
				}
				
				if(nextFree){
					frame = i;
					found = true;
				}
			}
		}
		return frame;
	}
	
	
	/**
	 * Checks if the given frame is free.
	 * @param frame	frame number to check.
	 * @return 'true' if the frame is free, 'false' if it is occupied.
	 */
	private boolean checkFreeFrame(int frame){
		int i = frame / 32;
		int j = frame % 32;
		int test = BM[i] & MASK[j];
		boolean result = (test == 0) ? true : false;
		return result;
	}
}
