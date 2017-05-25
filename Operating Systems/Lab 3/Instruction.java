import java.util.ArrayList;

/*
  	Represents an Instruction
*/

public class Instruction {
	private String activity; 					// Possible activities are initiate, request, compute, release and terminate
	private int taskNum; 						// ID of the task that uses the instruction
	private int resource; 						// Index of the resource used
	private int units; 							// Number of units required
	private int cycles; 						// Number of cycles required
	private ArrayList<Integer> initialClaim; 	// Initial claim of task
	private ArrayList<Integer> currentClaim;	// Current claim of task
	private ArrayList<Integer> maxClaim;		// Max claim of task
	private boolean lastComputed;				// True if last instruction is "compute"
	private boolean counted;					// True if the instruction has been counted
	private boolean exceedsClaim; 				// True if the task exceeds claim during execution (Used for Banker)
	
	// Constructor
	public Instruction() {
		this.initialClaim = new ArrayList<Integer>();
		this.currentClaim = new ArrayList<Integer>();
		this.maxClaim = new ArrayList<Integer>();
		this.lastComputed = false;
		this.counted = false;
		this.exceedsClaim = false;
	}
	
	// Decrement cycles
	public void decCycles() {
		this.cycles--;
	}
	
	// Initialize initialClaim with 0s
	public void initalizeClaims(int resourceCount) {
		for (int i = 0; i < resourceCount; i++) {
			this.initialClaim.add(0);
		}
	}
	
	// Getters and Setters
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public int getTaskNum() {
		return taskNum;
	}
	public void setTaskNum(int taskNum) {
		this.taskNum = taskNum;
	}
	public int getResource() {
		return resource;
	}
	public void setResource(int resource) {
		this.resource = resource;
	}
	public int getUnits() {
		return units;
	}
	public void setUnits(int units) {
		this.units = units;
	}
	public int getCycles() {
		return cycles;
	}
	public void setCycles(int cycles) {
		this.cycles = cycles;
	}
	public ArrayList<Integer> getInitialClaim() {
		return initialClaim;
	}
	public void setInitialClaim(ArrayList<Integer> initialClaim) {
		this.initialClaim = initialClaim;
	}
	public ArrayList<Integer> getCurrentClaim() {
		return currentClaim;
	}
	public void setCurrentClaim(ArrayList<Integer> currentClaim) {
		this.currentClaim = currentClaim;
	}
	public void setCurrentClaimInitial(ArrayList<Integer> currentClaim, int resourceCount) {
		this.currentClaim = currentClaim;
		for (int i = 0; i < resourceCount; i++) {
			this.currentClaim.add(0);
		}
	}
	public boolean isLastComputed() {
		return lastComputed;
	}
	public void setLastComputed(boolean lastComputed) {
		this.lastComputed = lastComputed;
	}
	public boolean isCounted() {
		return counted;
	}
	public void setCounted(boolean counted) {
		this.counted = counted;
	}
	public ArrayList<Integer> getMaxClaim() {
		return maxClaim;
	}
	public void setMaxClaim(ArrayList<Integer> maxClaim) {
		this.maxClaim = maxClaim;
	}
	public boolean isExceedsClaim() {
		return exceedsClaim;
	}
	public void setExceedsClaim(boolean exceedsClaim) {
		this.exceedsClaim = exceedsClaim;
	}
}