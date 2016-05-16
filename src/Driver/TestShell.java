package Driver;

import java.io.*;

import System.Components;
import System.VMSystem;

/**
 * The TestShell class will implement the driver for the VM system. It provides for initializing the PM (from an input file) and for 
 * translating VA to PA with and without TLB (given another input file). The resulting PA are written into separate files (one for TLB
 * and the other one for no TLB).
 * @author David García Santacruz, ID#: 51062654
 */
public class TestShell {
	
	VMSystem vm;
	File inputFile1;	// File initializing the VM.
	File inputFile2;	// File containing VA's to be translated.
	File outputFile1;	// File where PA translation of VA's will be written (no TLB).
	File outputFile2;	// File where PA translation of VA's will be written (TLB).
	
	
	/**
	 * Class constructor.
	 * Initializes the input 1 and 2, and output 1 and 2 file paths as well as the VM system.
	 */
	public TestShell(){
		vm = new VMSystem();
		inputFile1 = new File("E:/input1.txt");
		inputFile2 = new File("E:/input2.txt");
		outputFile1 = new File("E:/54062651_1.txt"); //54062651
		outputFile2 = new File("E:/54062651_2.txt"); //54062651
		
		// Check existance of input and output files
    	try {
    		if(!inputFile1.exists()) {
    			System.out.println("ERROR: input file 1 doesn't exist");
    		}
    		if(!inputFile2.exists()) {
    			System.out.println("ERROR: input file 2 doesn't exist");
    		}
    		outputFile1.createNewFile();
    		outputFile2.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Reads the first input file and initializes the VM system.
	 * Reads the second input file and translates the VA to PA addresses according to the PM scheme from the first file.
	 * Writes the resulting PAs to the output files (one for translation with TLB and another one without TLB).
	 */
	public void run(){
        String inST = null;		// This will reference the line with pairs to initialize the ST.
        String inPT = null;		// This will reference the line with triples to initialize the PT.
        String addr = null;		// This will reference the line with pairs representing read/write and VA.

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReaderInit = new FileReader(inputFile1);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReaderInit = new BufferedReader(fileReaderInit);
            
            // Read the two lines of the input file.
            inST = bufferedReaderInit.readLine();
            inPT = bufferedReaderInit.readLine();
           
            // Initialize the PM.
            init(inST, inPT);

            // Always close files.
            bufferedReaderInit.close();            
        } catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + inputFile1 + "'");                
        } catch(IOException ex) {
            System.out.println("Error reading file '" + inputFile1 + "'");                   
        }
        
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReaderTransl = new FileReader(inputFile2);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReaderTransl = new BufferedReader(fileReaderTransl);

            // Read the single line of addresses.
            addr = bufferedReaderTransl.readLine();
           
            // Translate every single address with TLB.
            transl(addr, false, outputFile1);
            
            // Clear physical memory and reinitialize it with the same values.
            init(inST, inPT);
            
            // Translate every single address without TLB.
            transl(addr, true, outputFile2);

            // Always close files.
            bufferedReaderTransl.close();            
        } catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + inputFile1 + "'");                
        } catch(IOException ex) {
            System.out.println("Error reading file '" + inputFile1 + "'");                   
        }
	}
	
	
	/**
	 * Translate VAs into PAs.
	 * @param addr	String containing R/W + address pairs
	 * @param mode	"true" for TLB assistance, "false" for no TLB.
	 * @param file	output file in which the results of the translation will be written.
	 */
	private void transl(String addr, boolean mode, File file) {
		System.out.println("Translating VAs.");
		
		// Parse the String containing R/W+address pairs into an array
		String [] pairs = addr.split("\\s+");
		for(String s : pairs){
			s = s.replaceAll("[^\\w]", "");
		}
		
		try {
            // Assume default encoding.
            FileWriter fileWriter = new FileWriter(file, true);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // For every two consecutive values of the array (representing R/W+address), run the translation with the given mode (with/without TLB)
    		for(int i = 0; i<pairs.length; i+=2){
    			System.out.println(pairs[i] + " " + pairs[i+1]);
    			Components c = new Components(Integer.parseInt(pairs[i+1]));
    			System.out.println(c.toString());
    			String result =vm.translateAddress(Integer.parseInt(pairs[i]), Integer.parseInt(pairs[i+1]), mode);
    			bufferedWriter.write(result + " ");
    		}
    		
            // Always close files.
            bufferedWriter.close();
        } catch(IOException ex) {
            System.out.println("Error writing to file '" + file + "'");
        }
	}


	/**
	 * Initializes the PM.
	 * @param inST	String containing s-f pairs.
	 * @param inPT	String containing s-p-f pairs.
	 */
	private void init(String inST, String inPT) {
		System.out.println("Initializing the VM system.");
		vm = new VMSystem();
		
		// Parse the String containing s-f pairs into an array
		String [] pairs = inST.split("\\s+");
		for(String s : pairs){
			s = s.replaceAll("[^\\w]", "");
		}
		
		// Initialize the ST according to those pairs
		for(int i = 0; i<pairs.length; i+=2){
			System.out.println(pairs[i] + " " + pairs[i+1]);
			vm.initializeST(Integer.parseInt(pairs[i]), Integer.parseInt(pairs[i+1]));
		}
		
		// Parse the String containing s-p-f triples into an array
		pairs = inPT.split("\\s+");
		for(String s : pairs){
			s = s.replaceAll("[^\\w]", "");
		}
		
		// Initialize the ST according to those triples
		for(int i = 0; i<pairs.length; i+=3){
			System.out.println(pairs[i] + " " + pairs[i+1] + " " + pairs[i+2]);
			vm.initializePT(Integer.parseInt(pairs[i]), Integer.parseInt(pairs[i+1]), Integer.parseInt(pairs[i+2]));
		}
	}


	public static void main(String[] args) {
		TestShell driver = new TestShell();
		driver.run();
	}
}
