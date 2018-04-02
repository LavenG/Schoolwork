import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class MemoryManager {
	// Variables
	static Scanner input; 							
	static StringBuilder FIFOOutput;			// Output for FIFO
	static StringBuilder bankerOutput;			// Output for Banker
	static int taskCount;					// Number of tasks
	static int resourceCount;				// Number of resources
	static ArrayList<Integer> resources;			// List of resources
	static ArrayList<Integer> initialResources;		// List of initial resources
	static ArrayList<Task> tasks;				// List of tasks
	static ArrayList<Task> blockedTasks;			// List of blocked tasks
	static Instruction inst;				// The current instruction
	static Instruction rootInst;				// The root instruction
	static Instruction testInstBlock;			// Test for current instruction
	static Instruction testRootBlock;			// Test for root instruction
	static int time;					// Time
	static int blockedCount;				// Number of blocked tasks
	static int terminatedCount;				// Number of terminated tasks
	static int[] activeTasksCount;				// Number of active tasks per resource
	static boolean abort;					// True if a current task must abort
	static int totalTime;					// Total amount of time used by all tasks
	static int totalWaitingTime;				// Total amount of time all tasks waited
	static double totalPercentWaiting;			// Percentage of total time spent waiting
	
	/*
	 	Reads/parses input file
	*/
	public static void readInput(String filePath) throws FileNotFoundException {
		FileReader fReader = new FileReader(filePath);
        input = new Scanner(fReader);
        tasks = new ArrayList<Task>();
        
        taskCount = input.nextInt();
        resourceCount = input.nextInt();
        
        // Add number of tasks to taskList
        for (int i = 0; i < taskCount; i++) {
        	Task t = new Task(resourceCount);
        	t.setTaskNumber(i);
        	tasks.add(t);
        }
        
        // Adds values to resources
        resources = new ArrayList<Integer>(resourceCount);
        initialResources = new ArrayList<Integer>(resources);
        for (int i = 0; i < resourceCount; i++) {
        	int tempNum = input.nextInt();
        	resources.add(tempNum);
        	initialResources.add(tempNum);
        }

        // Add each instruction to its specific task
        while (input.hasNext()) {
        	inst = new Instruction();
        	inst.setActivity(input.next());
        	inst.setTaskNum(input.nextInt());
        	inst.setResource(input.nextInt());
        	inst.setUnits(input.nextInt());
        	if (inst.getActivity().equals("compute"))
        		inst.setCycles(inst.getResource());
        	
        	tasks.get(inst.getTaskNum() - 1).addInstruction(inst);  	
        }
        
        // Setup claims and initial resources
        for (int i = 0; i < taskCount; i++) {
        	rootInst = tasks.get(i).getInstruction(0);
        	// rootInst.setInitialClaim(new ArrayList<Integer>(resourceCount));
        	for (int k = 0; k < tasks.get(i).getInstructions().size(); k++) {
        		tasks.get(i).getInstruction(k).initalizeClaims(resourceCount);
        	}
        
        		
        	for (int j = 0; j < tasks.get(i).getInstructions().size(); j++) {
        		Instruction temp = tasks.get(i).getInstruction(j);
        		if (temp.getActivity().equals("initiate")) {
        			rootInst.getInitialClaim().set((temp.getResource() - 1), temp.getUnits());
        		}
        	}
        	
        	for (int j = 0; j < tasks.get(i).getInstructions().size(); j++) {
        		tasks.get(i).getInstruction(j).setCurrentClaimInitial(new ArrayList<Integer>(resourceCount), resourceCount);
        	}
        	for (int j = 0; j < resourceCount; j++) {
        		if (rootInst.getInitialClaim().get(j) > resources.get(j))
        			tasks.get(rootInst.getTaskNum() - 1).setMustAbort(true);
        	}
        }
	}
	
	public static boolean checkSafeState(Task t, ArrayList<Integer> tempResources) {
		boolean safe = true;
		int[] totalClaims = new int[resourceCount];
		int[] availableResources = new int[resourceCount];
		int returnableResources = 0;
		ArrayList<Integer> temp = new ArrayList<Integer>(resourceCount);
		Instruction rootTemp = t.getInstruction(0);
		Instruction rootTemp2 = rootTemp;
		int taskNumber = t.getCurrentInstruction().getTaskNum();
		
		// Simulate that the request terminated and returned resources
		temp = new ArrayList<Integer>(rootTemp.getCurrentClaim());
		for (int i = 0; i < resourceCount; i++) {
			rootTemp.getCurrentClaim().set(i, 0);
		}
		
		// Set the total claims and available resources for each task
		for (int i = 0; i < taskCount; i++) {
			rootTemp = tasks.get(i).getInstruction(0);
			rootTemp.setMaxClaim(new ArrayList<Integer>(resourceCount));
			for (int j = 0; j < resourceCount; j++) {
				rootTemp.getMaxClaim().add(0);
				rootTemp.getMaxClaim().set(j, rootTemp.getMaxClaim().get(j) + 
				(rootTemp.getInitialClaim().get(j) - rootTemp.getCurrentClaim().get(j)));
				totalClaims[j] += rootTemp.getCurrentClaim().get(j);
				availableResources[j] = (initialResources.get(j) - totalClaims[j]);
			}
		}
		
		rootTemp2.setCurrentClaim(temp);
		// Check if task has everything it needs
		for (int i = 0; i < resourceCount; i++) {
			if (rootTemp2.getMaxClaim().get(i) > availableResources[i] && !t.isTerminated() && !t.isAborted()) {
				safe = false;
			}
		}
		
		// Loop through all tasks
		for (int i = 0; i < taskCount; i++) {
			rootTemp = tasks.get(i).getInstruction(0);
			if (tasks.get(i).getTaskNumber() != taskNumber && tasks.get(i).isTerminated() && !tasks.get(i).isAborted()) {
				// Check if maxClaim is less or equal to available resources
				for (int j = 0; j < resourceCount; j++) {
					if (rootTemp.getMaxClaim().get(j) <= availableResources[j]) {
						returnableResources++;
					}
					else {
						safe = false;
					}
				}
				if (returnableResources == resourceCount) {
					for (int j = 0; j < resourceCount; j++) {
						availableResources[j] += rootInst.getCurrentClaim().get(j);
					}
				}
			}
		}
		return safe;
	}
	
	/*
	  	Runs a simulation of Optimistic Resource Manager and saves results in FIFOOutput
	*/
	public static void runFIFO() {
		// Setting up variables
		FIFOOutput = new StringBuilder();
		time = 0;
		blockedCount = 0;
		terminatedCount = 0;
		activeTasksCount = new int[resources.size()];
		ArrayList<Integer> tempResources;
		abort = true;
		
		// Run until all tasks have been terminated
		while (terminatedCount < taskCount) {
			tempResources = new ArrayList<Integer>(resources);
			ArrayList<Integer> releasedResources = new ArrayList<Integer>(resourceCount);
			for (int i = 0; i < resourceCount; i++) 
				releasedResources.add(0);
			
			// Loop through all tasks
			for (int i = 0; i < taskCount; i++) {
				Task currentTask = tasks.get(i);
				rootInst = currentTask.getInstruction(0);
				abort = true;
				
				// Check if task is blocked
				if (currentTask.isBlocked() && !currentTask.isAborted()) {
					inst = currentTask.getCurrentInstruction();
					
					// Check if have to abort
					for (int j = 0; j < taskCount; j++) {
						testRootBlock = tasks.get(j).getInstruction(0);
						if (tasks.get(j).isBlocked() && !tasks.get(j).isAborted()) {
							testInstBlock = tasks.get(j).getCurrentInstruction();
							if (!testInstBlock.getActivity().equals("terminate")) {
								if (testInstBlock.getUnits() <= resources.get(testInstBlock.getResource() - 1)) {
									abort = false;
								}
							}
						}
					}
					
					// Task is blocked
					if (inst.getUnits() > tempResources.get(inst.getResource() - 1)) {
						currentTask.incWaitTime();
						
						// Check if current task must be aborted
						if (blockedCount == activeTasksCount[inst.getResource() -1] && abort) {
							currentTask.setAborted(true);
							abort = true;
							blockedCount--;
							terminatedCount++;
							activeTasksCount[inst.getResource() - 1]--;
							
							for (int j = 0; j < resourceCount; j++) {
								tempResources.set(j, (tempResources.get(j) + currentTask.getUsedResource(j)));
							}
						}
					}
					// Unblock tasks
					else {
						Instruction temp;
						Instruction rootTemp = new Instruction();
						int lowTime = 100000;
						
						// Find the first instruction that was blocked
						for (int j = 0; j < taskCount; j++) {
							Task t = tasks.get(j);
							testRootBlock = t.getInstruction(0);
							if (t.isBlocked() && !t.isAborted()) {
								testInstBlock = t.getCurrentInstruction();
								if (!t.isTerminated()) {
									if (testInstBlock.getUnits() <= tempResources.get(testInstBlock.getResource() - 1)) {
										if (t.getBlockedTime() < lowTime) {
											rootTemp = testRootBlock;
											lowTime = t.getBlockedTime();
										}
									}
								}
							}
						}
						
						temp = tasks.get(rootTemp.getTaskNum() - 1).getCurrentInstruction();
						tasks.get(rootTemp.getTaskNum() - 1).setBlocked(false);
						tasks.get(rootTemp.getTaskNum() - 1).setJustReleased(true);
						blockedCount--;
						tempResources.set(temp.getResource() - 1, (tempResources.get(temp.getResource() - 1) - temp.getUnits()));
						tasks.get(rootTemp.getTaskNum() - 1).getUsedResources().set(temp.getResource() - 1, 
						tasks.get(rootTemp.getTaskNum() - 1).getUsedResource(temp.getResource() - 1) + temp.getUnits());
					}
				}
			}
			
			// Loop through all tasks
			for (int i = 0; i < taskCount; i++) {
				Task currentTask = tasks.get(i);
				rootInst = currentTask.getInstruction(0);
				
				if (!currentTask.isBlocked() && !currentTask.isAborted() && !currentTask.isTerminated()) {
					inst = currentTask.getCurrentInstruction();
					
					// If task is in "compute", it should not move to the next instruction
					if (!inst.getActivity().equals("compute") || rootInst.isLastComputed()) {
						currentTask.incCurrentInstruction();
						rootInst.setLastComputed(false);
					}
					
					// Initiate
					if (inst.getActivity().equals("initiate")) {
						activeTasksCount[inst.getResource() - 1]++;
					}
					// Request
					else if (inst.getActivity().equals("request")) {
						// Block if there are not enough resources available
						if (inst.getUnits() > tempResources.get(inst.getResource() - 1) && !currentTask.isJustReleased()) {
							currentTask.setBlocked(true);
							currentTask.incWaitTime();
							currentTask.setBlockedTime(time);
							currentTask.decCurrentInstruction();
							blockedCount++;
						}
						// There are enough resources available to grant to the task
						else {
							if (!currentTask.isJustReleased()) {
								tempResources.set(inst.getResource() - 1, (tempResources.get(inst.getResource() - 1) - inst.getUnits()));
							}
							else {
								currentTask.setJustReleased(false);
							}
							
							currentTask.getUsedResources().set(inst.getResource() - 1, currentTask.getUsedResource(inst.getResource() -1) + inst.getUnits());
						}
					}
					// Compute
					else if (inst.getActivity().equals("compute")) {
						inst.decCycles();
						
						if (inst.getCycles() == 0) {
							currentTask.incCurrentInstruction();
							rootInst.setLastComputed(true);
							inst = currentTask.getCurrentInstruction();
							
							// Terminate
							if (inst.getActivity().equals("terminate") && !inst.isCounted()) {
								inst.setCounted(true);
								terminatedCount++;
								rootInst.setActivity("terminate");
								currentTask.setTotalTime(time + 1);
								currentTask.setTerminated(true);
							}
						}
					}
					// Release
					else if (inst.getActivity().equals("release")) {
						releasedResources.set(inst.getResource() - 1, releasedResources.get(inst.getResource() - 1) + inst.getUnits());
						currentTask.setBlockedTime(0);
						currentTask.getUsedResources().set(inst.getResource() - 1, currentTask.getUsedResource(inst.getResource() -1) - inst.getUnits());
					
						// Terminate
						int temp = inst.getResource() - 1;
						inst = currentTask.getCurrentInstruction();
						if (inst.getActivity().equals("terminate") && !inst.isCounted()) {
							terminatedCount++;
							activeTasksCount[temp]--;
							inst.setCounted(true);
							rootInst.setActivity("terminate");
							currentTask.setTotalTime(time + 1);
							currentTask.setTerminated(true);
						}
					}
				}
			}
			// Set temporary resources to resources
			for (int i = 0; i < tempResources.size(); i++) {
				tempResources.set(i, tempResources.get(i) + releasedResources.get(i));
			}
			Collections.copy(resources, tempResources);
			time++;
		}
		// Output results to FIFOOutput
		totalTime = 0;
		totalWaitingTime = 0;
		
		FIFOOutput.append("FIFO\n\n");
		for (Task t : tasks) {
			if (!t.isAborted()) {
				t.setPercentWaiting();
				totalTime += t.getTotalTime();
				totalWaitingTime += t.getWaitTime();
			}
			FIFOOutput.append(t.toString() + "\n");
		}
		
		totalPercentWaiting = (double) totalWaitingTime / (double) totalTime;
		FIFOOutput.append("Total:     ")
		.append(totalTime + "    ")
		.append(totalWaitingTime + "    ")
		.append(Math.round(100 * totalPercentWaiting) + "%");
	}
	
	/*
  		Runs a simulation of Banker's algorithm and saves results in bankerOutput
	*/
	public static void runBanker() {
		// Setting up variables, reset and read file again
		bankerOutput = new StringBuilder();
		time = 0;
		blockedCount = 0;
		terminatedCount = 0;
		activeTasksCount = new int[resourceCount];
		int[] totalResourcesUsed = new int[resourceCount];
		ArrayList<Integer> tempResources;
		blockedTasks = new ArrayList<Task>(taskCount);
		inst = null;
		rootInst = null;
		testInstBlock = null;
		testRootBlock = null;
		totalTime = 0;
		totalWaitingTime = 0;
		totalPercentWaiting = 0;
		
		// First check if any task exceeds its initial claim
		Instruction temp;
		for (int i = 0; i < taskCount; i++) {
			Task t = tasks.get(i);
			temp = t.getInstruction(0);
			for (int j = 0; j < resourceCount; j++) {
				if (temp.getInitialClaim().get(j) > initialResources.get(j)) {
					t.setAborted(true);
				}
				
				// Now check if the task exceeds its claim during execution
				rootInst = t.getInstruction(0);
				
				for (int k = 0; k < t.getInstructions().size(); k++) {
					temp = t.getInstruction(k);
					if (temp.getActivity().equals("request")) {
						totalResourcesUsed[temp.getResource() - 1] += temp.getUnits();
						
						for (int n = 0; n < resourceCount; n++) {
							if (totalResourcesUsed[n] > rootInst.getInitialClaim().get(n)) {
								temp.setExceedsClaim(true);
							}
						}
					}
					else if (temp.getActivity().equals("release")) {
						totalResourcesUsed[temp.getResource() - 1] -= temp.getUnits();
					}
				}
				
			}
		}
		
		// Checks if the task is aborted
		for (int i = 0; i < taskCount; i++) {
			if (tasks.get(i).isAborted()) {
				tasks.get(i).setTerminated(true);
				terminatedCount++;
			}
		}
		
		// Run until all tasks have been terminated
		while (terminatedCount < taskCount) {
			tempResources = new ArrayList<Integer>(resources);
			ArrayList<Integer> releasedResources = new ArrayList<Integer>(resourceCount);
			for (int i = 0; i < resourceCount; i++) 
				releasedResources.add(0);
			
			// Loop through each blocked task
			for (int i = 0; i < blockedTasks.size(); i++) {
				Task currentTask = blockedTasks.get(i);
				rootInst = currentTask.getInstruction(0);
				inst = currentTask.getInstruction(tasks.get(i).getCurrentInstructionIndex());
				
				// If task is blocked
				if (currentTask.isBlocked() && !currentTask.isAborted()) {
					currentTask.setSafe(checkSafeState(currentTask, tempResources));
					
					// If current task is safe, unblock it
					if (currentTask.isSafe()) {
						rootInst = blockedTasks.get(0).getInstruction(0);
						inst = tasks.get(rootInst.getTaskNum() - 1).getCurrentInstruction();
						blockedTasks.get(0).setBlocked(false);
						blockedTasks.get(0).setJustReleased(true);
						blockedCount--;
						tempResources.set(inst.getResource() - 1, (tempResources.get(inst.getResource() - 1) - inst.getUnits()));
						rootInst.getCurrentClaim().set(inst.getResource() - 1, 
						rootInst.getCurrentClaim().get(inst.getResource() - 1) + inst.getUnits());
						blockedTasks.get(0).getUsedResources().set(inst.getResource() - 1, 
						blockedTasks.get(0).getUsedResource(inst.getResource() - 1) + inst.getUnits());
					}
					else {
						currentTask.incWaitTime();
						// Check if the task must be aborted
						if (currentTask.isMustAbort()) {
							currentTask.setAborted(true);
							currentTask.setBlocked(false);
							blockedTasks.remove(currentTask);
							blockedCount--;
							activeTasksCount[inst.getResource() - 1]--;
							terminatedCount++;
							
							for (int j = 0; j < resourceCount; j++) {
								tempResources.set(j, tempResources.get(j) + currentTask.getUsedResource(j));
							}
						}
					}
				}
			}
			
			// Loop through each task
			for (int i = 0; i < taskCount; i++) {
				Task currentTask = tasks.get(i);
				rootInst = currentTask.getInstruction(0);
				inst = currentTask.getCurrentInstruction();
				
				if (!currentTask.isBlocked() && !currentTask.isAborted() && !currentTask.isTerminated()) {
					// If task is in "compute", it should not move to the next instruction
					if (!inst.getActivity().equals("compute") || rootInst.isLastComputed()) {
						currentTask.incCurrentInstruction();
						rootInst.setLastComputed(false);
					}
					
					// Initiate
					if (inst.getActivity().equals("initiate")) {
						activeTasksCount[inst.getResource() - 1]++;
					}
					// Request
					else if (inst.getActivity().equals("request")) {
						// Check if task is safe
						currentTask.setSafe(checkSafeState(currentTask, tempResources));
						
						// Block if not safe
						if (!currentTask.isSafe() && !currentTask.isJustReleased()) {
							currentTask.setBlocked(true);
							blockedTasks.add(currentTask);
							blockedCount++;
							currentTask.setBlockedTime(time);
							currentTask.incWaitTime();
							currentTask.decCurrentInstruction();
						}
						// Grant resources if safe
						else {
							if (!inst.isExceedsClaim()) {
								if (!currentTask.isJustReleased()) {
									tempResources.set(inst.getResource() - 1, (tempResources.get(inst.getResource() - 1) - inst.getUnits()));
									rootInst.getCurrentClaim().set(inst.getResource() - 1, rootInst.getCurrentClaim().get(inst.getResource() - 1) + inst.getUnits());
								}
								else {
									blockedTasks.remove(0);
									currentTask.setJustReleased(false);
									rootInst.getCurrentClaim().set(inst.getResource() - 1, rootInst.getCurrentClaim().get(inst.getResource() - 1) + inst.getUnits());
								}
								
								currentTask.getUsedResources().set(inst.getResource() - 1, currentTask.getUsedResource(inst.getResource() -1) + inst.getUnits());
							}
							else {
								currentTask.setAborted(true);
								terminatedCount++;
								for (int j = 0; j < resourceCount; j++) {
									rootInst.getCurrentClaim().set(j, 0);
								}
							}
						}
					}
					// Compute
					else if (inst.getActivity().equals("compute")) {
						inst.decCycles();
						
						if (inst.getCycles() == 0) {
							rootInst.setLastComputed(true);
							currentTask.incCurrentInstruction();
							inst = currentTask.getCurrentInstruction();
							
							// Terminate
							if (inst.getActivity().equals("terminate") && !inst.isCounted()) {
								inst.setCounted(true);
								terminatedCount++;
								rootInst.setActivity("terminate");
								currentTask.setTotalTime(time + 1);
								currentTask.setTerminated(true);
								rootInst.setCurrentClaimInitial(new ArrayList<Integer>(resourceCount), resourceCount);
								rootInst.setMaxClaim(new ArrayList<Integer>(resourceCount));
							}
						}
					}
					// Release
					else if (inst.getActivity().equals("release")) {
						releasedResources.set(inst.getResource() - 1, releasedResources.get(inst.getResource() - 1) + inst.getUnits());
						currentTask.setBlockedTime(0);
						rootInst.getCurrentClaim().set(inst.getResource() - 1, 0);
						currentTask.getUsedResources().set(inst.getResource() - 1, currentTask.getUsedResource(inst.getResource() -1) - inst.getUnits());
						
						// Terminate
						int t = inst.getResource() - 1;
						inst = currentTask.getCurrentInstruction();
						if (inst.getActivity().equals("terminate") && !inst.isCounted()) {
							terminatedCount++;
							activeTasksCount[t]--;
							inst.setCounted(true);
							rootInst.setActivity("terminate");
							rootInst.setCurrentClaimInitial(new ArrayList<Integer>(resourceCount), resourceCount);
							rootInst.setMaxClaim(new ArrayList<Integer>(resourceCount));
							currentTask.setTotalTime(time + 1);
							currentTask.setTerminated(true);
						}
					}
				}
			}
			// Set temporary resources to resources
			for (int i = 0; i < tempResources.size(); i++) {
				tempResources.set(i, tempResources.get(i) + releasedResources.get(i));
			}
			Collections.copy(resources, tempResources);
			time++;
		}
		// Output results to bankerOutput
		totalTime = 0;
		totalWaitingTime = 0;
		
		bankerOutput.append("BANKER's\n\n");
		for (Task t : tasks) {
			if (!t.isAborted()) {
				t.setPercentWaiting();
				totalTime += t.getTotalTime();
				totalWaitingTime += t.getWaitTime();
			}
			bankerOutput.append(t.toString() + "\n");
		}
		
		totalPercentWaiting = (double) totalWaitingTime / (double) totalTime;
		bankerOutput.append("Total:     ")
		.append(totalTime + "    ")
		.append(totalWaitingTime + "    ")
		.append(Math.round(100 * totalPercentWaiting) + "%");
	}
	
	public static void main(String args []) {
		try {
			readInput(args[0]); // Read input file
			runFIFO();			// Run FIFO
			readInput(args[0]);	// Read input file again to reset variables for Banker
			runBanker();		// Run Banker
			// Print results
			System.out.println(FIFOOutput.toString() + "\n\n" + bankerOutput.toString());
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Error! Sample Command:\njava MemoryManager input-1");
		}
		catch (FileNotFoundException e) {
			System.out.println("Error! File not found!");
		}
	}
}
