import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Pager {
	/*
	    Command line arguments:
	        M, the machine size in words.
	        P, the page size in words.
	        S, the size of a process, i.e., the references are to virtual addresses 0..S-1.
	        J, the 'job mix', which determines A, B, and C, as described below.
	        N, the number of references for each process.
	        R, the replacement algorithm, LIFO, RANDOM, or LRU.
	        D, the level of debugging output (Not implemented).
	 */
	static int M;
	static int P;
	static int S;
	static int J;
	static int N;
	static int D;
	static String R;
	// Variables
	static StringBuilder output = new StringBuilder();
	static final int QUANTUM = 3;
	static Scanner randomNumberScanner;
	static ArrayList<Process> processes;
	static ArrayList<Integer> frameTable;
	static ArrayList<Integer> lru;
	static ArrayList<Integer> lifo;
	static ArrayList<Integer> residency;
	static int finishedProcesses;
	static int framesCount;
	static int framesTakenCount;
	static int currentPage;
	static int updatedCurrentPage;
	static int pagesPerProcess;
	static int faults;
	static int frameHit;
	static int total;
	static int totalEvictions;
	static int currentAddress;
	static int nextAddress;
	static double randomNumber;
	static double averageResidency;
	static double overallAverageResidency;
	static boolean hit;
	
	// Setup all variables/lists
	private static void setup() {
		/*
        	Sets the process from user input, j refers to the job mix number
        	There are four possible sets of processes (i.e., values for j)
		*/
		processes = new ArrayList<Process>();
		
		// One process with A=1 and B=C=0, the simplest (fully sequential) case.
        if (J == 1) {
            Process p1 = new Process(1, 1, 0, 0, S, N);
            processes.add(p1);
        }
        // Four processes, each with A=1 and B=C=0.
        else if (J == 2) {
            Process p1 = new Process(1, 1, 0, 0, S, N);
            Process p2 = new Process(2, 1, 0, 0, S, N);
            Process p3 = new Process(3, 1, 0, 0, S, N);
            Process p4 = new Process(4, 1, 0, 0, S, N);
            Collections.addAll(processes, p1, p2, p3, p4);
        }
        // Four processes, each with A=B=C=0 (fully random references).
        else if (J == 3) {
            Process p1 = new Process(1, 0, 0, 0, S, N);
            Process p2 = new Process(2, 0, 0, 0, S, N);
            Process p3 = new Process(3, 0, 0, 0, S, N);
            Process p4 = new Process(4, 0, 0, 0, S, N);
            Collections.addAll(processes, p1, p2, p3, p4);
        }
        // One process with A=.75, B=.25 and C=0; one process with A=.75, B=0, and C=.25; one process
        // with A=.75, B=.125 and C=.125; and one process with A=.5, B=.125 and C=.125.
        else if (J == 4) {
            Process p1 = new Process(1, .75, .25, 0, S, N);
            Process p2 = new Process(2, .75, 0, .25, S, N);
            Process p3 = new Process(3, .75, .125, .125, S, N);
            Process p4 = new Process(4, .5, .125, .125, S, N);
            Collections.addAll(processes, p1, p2, p3, p4);
        }
        
        // Invalid J/job mix number
        else {
            System.out.println("\nInvalid job mix number!" +
                               "\nPlease enter a number from 1-4");
            System.exit(0);
        }
        
        // Initialize variables   
        framesCount = M / P;
        pagesPerProcess = S / P;
        frameTable = new ArrayList<Integer>(framesCount);
        lru = new ArrayList<Integer>(framesCount);
        lifo = new ArrayList<Integer>(framesCount);
        residency = new ArrayList<Integer>(framesCount);
        for (int i = 0; i < framesCount; i++) {
            frameTable.add(-1);
            lru.add(-1);
            lifo.add(-1);
            residency.add(-1);
        }
        finishedProcesses = 0;
        framesTakenCount = 0;
        currentPage = 0;
        updatedCurrentPage = 0;
        faults = 0;
        frameHit = 0;
        total = 0;
        totalEvictions = 0;
        averageResidency = 0;
        hit = false;  
	}
	
	// Returns the next address for a Process.
    private static int nextAddress(Process p) {
        currentAddress = p.getWord();

        randomNumber = (randomNumberScanner.nextInt() / (Integer.MAX_VALUE + 1d));
        if (randomNumber < p.getA()) {
            nextAddress = (currentAddress + 1) % p.getSize();
        }
        else if (randomNumber < p.getA() + p.getB()) {
            nextAddress = (currentAddress - 5 + p.getSize()) % p.getSize();
        }
        else if (randomNumber < p.getA() + p.getB() + p.getC()) {
            nextAddress = (currentAddress + 4) % p.getSize();
        }
        else {
            nextAddress = (int) randomNumberScanner.nextInt() % p.getSize();
        }
        
        return nextAddress;
    }
    
    // Runs the pager
    private static void runPager() {
    	// Run until all processes have finished
    	 while (finishedProcesses < processes.size()) {
    		 for (Process p : processes) {
                 // Quantum (3) references for each process
                 for (int i = 0; i < QUANTUM; i++) {

                     // Check if the process has finished by checking it's references (N)
                     if (p.getReferences() <= 0) {
                         finishedProcesses += 1;
                         break;
                     }

                     currentPage = p.getWord() / P;
                     updatedCurrentPage = currentPage + ((p.getProcessID() - 1) * pagesPerProcess);

                     // Now check if the page is in the table
                     hit = false;
                     for (int j = 0; j < frameTable.size(); j++) {
                         if (frameTable.get(j) == updatedCurrentPage) {
                             hit = true;
                             frameHit = j;
                             break;
                         }
                     }

                     if (hit == true) {
                    	 lru.set(frameHit, 0);
                     }
                     else {
                         // Fill the page table
                         if (framesTakenCount++ < framesCount) {
                        	 frameTable.set(framesCount - framesTakenCount, updatedCurrentPage);
                        	 lru.set(framesCount - framesTakenCount, 0);
                        	 lifo.set(framesCount - framesTakenCount, 0);
                        	 residency.set(framesCount - framesTakenCount, 0);
                             faults += 1;
                             p.incrementFaults();
                         }
                         else {
                             // Replacement Algorithms: LRU, LIFO and RANDOM
                             int replace = 0;
                             if (R.equalsIgnoreCase("LRU")) {
                                 int lruReplace = 0;
                                 for (int lruIndex = 1; lruIndex < lru.size(); lruIndex++) {
                                     if (lru.get(lruIndex) > lru.get(lruReplace)) {
                                         lruReplace = lruIndex;
                                     }
                                 }

                                 replace = frameTable.get(lruReplace);
                                 faults += 1;
                                 p.incrementFaults();

                                 processes.get(((replace / pagesPerProcess))).incrementEvictions();
                                 processes.get(((replace / pagesPerProcess))).addTotal(residency.get(lruReplace));
                                 frameTable.set(lruReplace, updatedCurrentPage);
                                 lru.set(lruReplace, 0);
                                 residency.set(lruReplace, 0);
                             }
                             else if (R.equalsIgnoreCase("LIFO")) {
                                 int lifoReplace = 0;
                                 for (int lifoIndex = 1; lifoIndex < lifo.size(); lifoIndex++) {
                                     if (lifo.get(lifoIndex) < lifo.get(lifoReplace)) {
                                         lifoReplace = lifoIndex;
                                     }
                                 }

                                 replace = frameTable.get(lifoReplace);
                                 faults += 1;
                                 p.incrementFaults();

                                 processes.get(((replace / pagesPerProcess))).incrementEvictions();
                                 processes.get(((replace / pagesPerProcess))).addTotal(residency.get(lifoReplace));
                                 frameTable.set(lifoReplace, updatedCurrentPage);
                                 lifo.set(lifoReplace, 0);
                                 residency.set(lifoReplace, 0);
                             }
                             else if (R.equalsIgnoreCase("RANDOM")) {
                                 int randomReplace = (int) randomNumberScanner.nextInt() % frameTable.size();
                                 replace = frameTable.get(randomReplace);
                                 faults += 1;
                                 p.incrementFaults();

                                 processes.get(((replace / pagesPerProcess))).incrementEvictions();
                                 processes.get(((replace / pagesPerProcess))).addTotal(residency.get(randomReplace));
                                 frameTable.set(randomReplace, updatedCurrentPage);
                                 residency.set(randomReplace, 0);
                             }
                             else {
                                 System.out.println("\n Error! Only LRU, LIFO and RANDOM replacement algorithms are allowed!");
                                 System.exit(0);
                             }
                         }
                     }

                     p.setWord(nextAddress(p));

                     // Increment the count in the time dependent arrays
                     for (int j = 0; j < lru.size(); j++) {
                    	 lru.set(j, lru.get(j) + 1);
                    	 lifo.set(j, lifo.get(j) + 1);
                    	 residency.set(j, residency.get(j) + 1);
                     }

                     p.decrementN();
                 }
             }
    	 }
    }
    
    // Print the results
    private static void printResults() {
    	// Print out the user's input
        output.append("The machine size is " + M)
        .append("\nThe page size is " + P)
        .append("\nThe process size is " + S)
        .append("\nThe job mix number is " + J)
        .append("\nThe number of references per process is " + N)
        .append("\nThe replacement algorithm is " + R)
        .append("\nThe level of debugging output is " + D + "\n");
        
        for (Process p : processes) {
        	if (p.getEvictions() != 0) {
        		averageResidency = p.getTotal() / (double) p.getEvictions();
                output.append("\nProcess " + p.getProcessID() + " had " + p.getFaults() + " faults and " + averageResidency + " average residency");
                totalEvictions += p.getEvictions();
                total += p.getTotal();
        	}
        	else {
                output.append("\nProcess " + p.getProcessID() + " had " + p.getFaults() + " faults\n\tWith no evictions, the average residence is undefined");
            }
        }
        
        if (totalEvictions == 0) {
            output.append("\n\nThe total number of faults is " + faults + ".\n\tWith no evictions, the overall average residence is undefined");
        }
        else {
        	overallAverageResidency = total / (double) totalEvictions;
            output.append("\n\nThe total number of faults is " + faults + " and the overall average residency is " + overallAverageResidency);
        }
        System.out.println(output.toString());
    }
	
	public static void main(String [] args) {
		try {
			// Parse the user's input and assign it to the argument variables
			M = Integer.parseInt(args[0]);
            P = Integer.parseInt(args[1]);
            S = Integer.parseInt(args[2]);
            J = Integer.parseInt(args[3]);
            N = Integer.parseInt(args[4]);
            R = args[5];
            D = Integer.parseInt(args[6]);
            
            // Setup scanner for random numbers file
            randomNumberScanner = new Scanner(new File("random-numbers.txt"));
            
            setup();
            runPager();
            printResults();
		}
		catch (FileNotFoundException e ) {
			System.out.println("\nRandom number input file not found!");
		}
		catch (Exception e) {
			System.out.println("An error has occurred!" +
                    "\nThe correct format for input is M P S J N R D, 6 positive integers and one string(R)" +
                    "\n\nM, the machine size in words." +
                    "\nP, the page size in words." +
                    "\nS, the size of a process, i.e., the references are to virtual addresses 0..S-1." +
                    "\nJ, the 'job mix', which determines A, B, and C, as described below." +
                    "\nN, the number of references for each process." +
                    "\nR, the replacement algorithm, LIFO, RANDOM, or LRU." +
                    "\nD, the level of debugging output (Not implemented).");
		}
	}
}