import java.util.ArrayList;

/*
  	Represents a Task
*/

public class Task {
	private int taskNum; 							// The task's ID
	private ArrayList<Instruction> instructions; 	// List of instructions associated with the task
	private ArrayList<Integer> usedResources;		// List of resources used by the task
	private int currentInstruction;					// Index of current instruction
	private int totalTime;							// Time taken for task to finish
	private int waitTime;							// Amount of time the task waited
	private double percentWaiting;					// Percentage of total time spent waiting
	private int blockedTime;						// Amount of time the task was blocked
	private boolean mustAbort;						// True if the task has to be aborted (Used for Banker)
	private boolean aborted;						// True if the task has been aborted
	private boolean blocked;						// True if the task is blocked
	private boolean terminated;						// True if the task is terminated
	private boolean justReleased;					// True if the task has just been unblocked
	private boolean safe;							// True if the task is safe (Used for Banker)
	
	// Constructor
	public Task(int resourceCount) {
		this.instructions = new ArrayList<Instruction>();
		this.usedResources = new ArrayList<Integer>();
		for (int i = 0; i < resourceCount; i++) {
			this.usedResources.add(0);
		}
		this.currentInstruction = 0;
		this.totalTime = 0;
		this.waitTime = 0;
		this.percentWaiting = 0;
		this.blockedTime = 0;
		this.aborted = false;
		this.blocked = false;
		this.terminated = false;
		this.justReleased = false;
	}
	
	// Add instruction
	public void addInstruction(Instruction inst) {
		this.instructions.add(inst);
	}
	
	// Get instruction
	public Instruction getInstruction(int index) {
		return this.instructions.get(index);
	}
	
	// Get current instruction
	public Instruction getCurrentInstruction() {
		return this.instructions.get(this.currentInstruction);
	}
	
	// Get used resource
	public int getUsedResource(int index) {
		return this.usedResources.get(index);
	}
	
	// Increment waiting time
	public void incWaitTime() {
		this.waitTime++;
	}
	
	// Increment block time
	public void incBlockedTime() {
		this.setBlockedTime(this.getBlockedTime() + 1);
	}
	
	// Increment current instruction index
	public void incCurrentInstruction() {
		this.currentInstruction++;
	}
	
	// Decrement current instruction index
	public void decCurrentInstruction() {
		this.currentInstruction--;
	}
	
	// Print out task
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Task " + (this.taskNum + 1) + ":    ");
		if (this.isAborted()) {
			sb.append("aborted");
		}
		else {
			sb.append(this.totalTime + "    ")
			.append(this.waitTime + "    ")
			.append(Math.round(100 * this.percentWaiting) + "%");
		}
		
		return sb.toString();
	}
	
	// Getters and Setters
	public int getTaskNumber() {
		return taskNum;
	}
	public void setTaskNumber(int taskNumber) {
		this.taskNum = taskNumber;
	}
	public ArrayList<Instruction> getInstructions() {
		return instructions;
	}
	public void setInstructions(ArrayList<Instruction> instructions) {
		this.instructions = instructions;
	}
	public boolean isAborted() {
		return aborted;
	}
	public void setAborted(boolean aborted) {
		this.aborted = aborted;
	}
	public boolean isBlocked() {
		return blocked;
	}
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	public int getCurrentInstructionIndex() {
		return this.currentInstruction;
	}
	public void setCurrentInstruction(int currentInstruction) {
		this.currentInstruction = currentInstruction;
	}
	public int getWaitTime() {
		return waitTime;
	}
	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}
	public ArrayList<Integer> getUsedResources() {
		return usedResources;
	}
	public void setUsedResources(ArrayList<Integer> usedResources) {
		this.usedResources = usedResources;
	}
	public boolean isTerminated() {
		return terminated;
	}
	public void setTerminated(boolean terminated) {
		this.terminated = terminated;
	}
	public int getBlockedTime() {
		return blockedTime;
	}
	public void setBlockedTime(int blockedTime) {
		this.blockedTime = blockedTime;
	}
	public boolean isJustReleased() {
		return justReleased;
	}
	public void setJustReleased(boolean justReleased) {
		this.justReleased = justReleased;
	}
	public double getPercentWaiting() {
		return percentWaiting;
	}
	public void setPercentWaiting() {
		this.percentWaiting =  (double) this.waitTime / (double) this.totalTime;
	}
	public int getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(int total) {
		this.totalTime = total;
	}
	public boolean isMustAbort() {
		return mustAbort;
	}
	public void setMustAbort(boolean mustAbort) {
		this.mustAbort = mustAbort;
	}
	public boolean isSafe() {
		return safe;
	}
	public void setSafe(boolean safe) {
		this.safe = safe;
	}
}