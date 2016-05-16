package System;

/**
 * The VMSystem class will implement the virtual memory system, consisting of a physical memory and functions that allows us to translate
 * a VA with and without using a TLB. The virtual memory uses segmentation and paging.
 * @author David García Santacruz, ID#: 51062654
 */
public class VMSystem {

	int [] PM;			// Physical Memory
	BitMap BM;			// Bit Map
	TLBuffer [] TLB;	// Translation Look-Aside Buffer
	
	
	/**
	 * Constructor. Initializes the VM System parameters.
	 */
	public VMSystem(){
		PM = new int [524288];
		BM = new BitMap();
		TLB = new TLBuffer [4];
		for(int i = 0; i<4; i++){
			TLB[i] = new TLBuffer(i, -1, 0);
		}
		BM.markFrameAsUsed(0);		// The ST occupies the first frame
	}
	
	
	/**
	 * Initializes the ST: the PT of segment s starts at address f.
	 * @param s	segment number.
	 * @param f	starting address of the segment.
	 */
	public void initializeST(int s, int f){
		int frame = f / 512;
		PM[s] = f;
		
		// A PT occupies two consecutive frames
		if(frame != 0){		// Not mark the frames as used when it's the frame 0 (for instance, because f=-1, not resident, or f=0, not existent)
			BM.markFrameAsUsed(frame);
			BM.markFrameAsUsed(frame+1);
		}
	}
	
	
	/**
	 * Initializes the PT: the page p of segment s starts at address f.
	 * @param p	page number.
	 * @param s	segment number.
	 * @param f	starting address of the page.
	 */
	public void initializePT(int p, int s, int f){
		int frame = f / 512;
		PM[PM[s]+p] = f;
		
		// A page occupies a single frame
		if(frame != 0){		// Not mark the frame as used when it's the frame 0 (for instance, because f=-1, not resident, or f=0, not existent)
			BM.markFrameAsUsed(frame);
		}
	}
	
	
	/**
	 * Translates a VA to a PA.
	 * @param type	type of memory access: read (0), write (1).
	 * @param VA	integer representing the VA
	 * @param mode 	"true" for running the translation with the TLB, "false" to run it without the TLB.
	 * @return		"pf" if there was a page fault; "err" if a non-existent PT or page was read; the PA corresponding to the VA otherwise.
	 * If 
	 */
	public String translateAddress(int type, int VA, boolean mode){
		// Break the VA into the three components: s, p, w
		Components comp = new Components(VA);
		// Physical address corresponding to VA
		int PA;
		
		// In case the TLB is used, look for a match in the TLB first
		if(mode){
			// Search TLB for a match on sp
			for(int i = 0; i<4; i++){
				// TLB hit: a match on sp
				if(TLB[i].sp == comp.sp){
					updateTLB(i);				// Update the LRU info.
					PA = TLB[i].f + comp.w;
					return "h " + PA;
				}
			}
			// TLB miss: no match is found -> Proceed like with no TLB
		}
		
		
		// CHECK IF PT IS NOT RESIDENT OR DOES NOT EXIST
		// PT is currently not resident
		if(PM[comp.s] == -1){
			return "pf";
		}
		
		// PT does not exist (case: read)
		if (type == 0 && PM[comp.s] == 0){
			return "err";
		}
		
		// PT does not exist (case: write)
		if (type == 1 && PM[comp.s] == 0){
			// Create a new PT
			int frame = BM.searchFreeFrame(2);		// Free frame number (a PT requires two frames)
			BM.markFrameAsUsed(frame);				// Update the BM
			BM.markFrameAsUsed(frame+1);
			frame *= 512;							// Free frame actual address
			PM[comp.s] = frame;
		} 
		
		
		// CHECK IF PAGE IS NOT RESIDENT OR DOES NOT EXIST
		// Page is currently not resident
		if(PM[PM[comp.s]+comp.p] == -1){
			return "pf";
		}
		
		// Page does not exist (case: read)
		if (type == 0 && PM[PM[comp.s]+comp.p] == 0){
			return "err";
		}
		
		// Page does not exist (case: write)
		if (type == 1 && PM[PM[comp.s]+comp.p] == 0){
			// Create a new page
			int frame = BM.searchFreeFrame(1);		// Free frame number (a page requires one frame)
			BM.markFrameAsUsed(frame);				// Update the BM
			frame *= 512;							// Free frame actual address
			PM[PM[comp.s]+comp.p] = frame;
		}
		
		
		// Physical address calculation
		PA = PM[PM[comp.s]+comp.p]+comp.w;
		
		// TLB miss: no match is found -> Update the TLB
		if(mode){
			// Search the least recently used entry in the TLB...
			for(int i = 0; i<4; i++){
				if(TLB[i].LRU == 0){					// And update its info. with the current VA mapping
					TLB[i].sp = comp.sp;
					TLB[i].f = PM[PM[comp.s]+comp.p];
					updateTLB(i);						// Update the LRU info.
					break;
				}
			}
		}
		
		String result = (mode) ? "m " + PA : "" + PA;
		return result;
	}
	
	
	/**
	 * Updates the LRU values of the TLB.
	 * @param k	index of the TLB entry to take as reference for the update.
	 */
	private void updateTLB(int k) {
		// Search all the LRU values larger than the current LRU and decrement it by 1
		for(int i = 0; i<4; i++){
			if(TLB[i].LRU > TLB[k].LRU){
				TLB[i].LRU--;
			}
		}
		TLB[k].LRU = 3;		// Make the current LRU equal to 3 (=most recently used)
	}
	
	
	public static void main(String[] args){ 
//		VMSystem vm = new VMSystem();
//		vm.initializeST(2, 2048);
//		vm.initializePT(0, 2, 512);
//		vm.initializePT(1, 2, -1);
//		System.out.println("NO TLB");
//		System.out.println(vm.translateAddress(0, 0, false));
//		System.out.println(vm.translateAddress(0, 1048576, false));
//		System.out.println(vm.translateAddress(1, 1048586, false));
//		System.out.println(vm.translateAddress(1, 1049088, false));
//		System.out.println("\nTLB");
//		System.out.println(vm.translateAddress(0, 0, true));
//		System.out.println(vm.translateAddress(0, 1048576, true));
//		System.out.println(vm.translateAddress(1, 1048586, true));
//		System.out.println(vm.translateAddress(1, 1049088, true));
	}
}
