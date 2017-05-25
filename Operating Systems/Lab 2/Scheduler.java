import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Scheduler {
	// Variables
	static Boolean verbose;
	static ArrayList<Process> processes = new ArrayList<Process>();
	static ArrayList<Process> tempProcesses = new ArrayList<Process>();
	static ArrayList<Process> readyProcesses = new ArrayList<Process>();
	static ArrayList<Process> readyProcesses2 = new ArrayList<Process>();
	static ArrayList<Process> terminatedProcesses = new ArrayList<Process>();
	static int processCount;
	static int A;
	static int B;
	static int C;
	static int M;
	static int time;
	static int finishTime;
	static int CPUBurst;
	static int maxFinishTime;
	static int count;
	static int quantum = 2;
	static int temp;
	static int min;
	static int currentRunningProcessIndex;
	static Process currentRunningProcess;
	static double CPUUtilization;
	static double IOUtilization;
	static double throughput;
	static double avgTurnaroundTime;
	static double avgWaitTime;
	static String content;
	static String originalInput;
	static String sortedInput;
	static File randomNumbers;
	static Scanner randoms;
	static Scanner sc;
	static DecimalFormat numberFormat = new DecimalFormat("#0.000000");
	static StringBuilder output = new StringBuilder();
	
	public static void runScheduler(String filePath) throws FileNotFoundException {
		// Reading the random numbers file
		randomNumbers = new File("random-numbers.txt");
		randoms = new Scanner(randomNumbers);

		// Reading the input file
		FileReader fReader = new FileReader(filePath);
		sc = new Scanner(fReader);
		content = sc.useDelimiter("\\A").next();
		originalInput = "The original input was:\t" + content;
		sc.close();
		
		// Parsing the input file
		content = content.replaceAll("[()]", "");
		sc = new Scanner(content);
		processCount = sc.nextInt();
		while (sc.hasNext()) {
			A = sc.nextInt();
			B = sc.nextInt();
			C = sc.nextInt();
			M = sc.nextInt();
			processes.add(new Process(A, B, C, M));
		}
		
		// Sorting the input
		Collections.sort(processes);
		sortedInput = "The (sorted) input is:\t" + processCount + " ";
		for (Process p : processes) {
			sortedInput += "(";
			sortedInput += p.getA() + " ";
			sortedInput += p.getB() + " ";
			sortedInput += p.getC() + " ";
			sortedInput += p.getM() + ") ";
		}
		
		output.append(originalInput + "\n" + sortedInput + "\n");
		
		/*
			FCFS : First Come First Served
		*/
		time = 0;
		finishTime = 0;
		CPUUtilization = 0;
		IOUtilization = 0;
		throughput = 0;
		avgTurnaroundTime = 0;
		avgWaitTime = 0;
		// Null if nothing is running
		currentRunningProcess = null;
		// Copy process list into temporary array
		cloneProcesses(); 

		if (verbose) {
			output.append("\nThis detailed printout gives the state and remaining burst for each process\n");
		}
		
		// Run until every process has been terminated
		while (terminatedProcesses.size() < processCount) {
			if (verbose) {
				output.append("\nBefore cycle	" + time + ":	");
				for (Process p : tempProcesses) {
					printVerbose(p);
				}
			}

			// Unblock processes if blocked time is 0
			for (Process p: tempProcesses) {
				if (p.getBlockedTime() == 0) {
					p.decBlockedTime();
					if (p.getProcessState() != 4) {
						p.setProcessState(1);
						readyProcesses.add(p);
					}
				}
			}
			
			// Check for new process
			for (Process p : tempProcesses) {
				if (p.getA() == time) {
					p.setProcessState(1);
					readyProcesses.add(p);
				}
			}
			
			// If no process is currently running
			if (currentRunningProcess == null) {
				if (!readyProcesses.isEmpty()) {
					readyProcesses.get(0).setProcessState(2);
					currentRunningProcessIndex = tempProcesses.indexOf(readyProcesses.get(0));
					currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
					CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
					currentRunningProcess.setBurst(CPUBurst);
					readyProcesses.remove(0);
				}
			}
			// Check to block
			else if (currentRunningProcess != null && CPUBurst == 1) {
				if (currentRunningProcess.getC() != 1) {
					currentRunningProcess.setProcessState(3);
					currentRunningProcess.decC();
					currentRunningProcess.setBlockedTime(currentRunningProcess.getM() * currentRunningProcess.getBurst());
					currentRunningProcess.setIOTime(currentRunningProcess.getIOTime() + currentRunningProcess.getBlockedTime());
				}
				else {
					if (currentRunningProcess.getFinishTime() == 0) {
						currentRunningProcess.setFinishTime(time);
					}
					currentRunningProcess.setProcessState(4);
					currentRunningProcess.decC();
				}

				if (!readyProcesses.isEmpty()) {
					currentRunningProcessIndex = tempProcesses.indexOf(readyProcesses.get(0));
					currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
					currentRunningProcess.setProcessState(2);
					readyProcesses.remove(0);
					CPUBurst = randomOS(currentRunningProcess.getB());
					currentRunningProcess.setBurst(CPUBurst);
				}
				else {
					currentRunningProcess = null;
				}
			}
			// If a process is currently running
			else if (currentRunningProcess != null && CPUBurst > 0) {
				CPUBurst--;
				currentRunningProcess.decC();
			}
			
			// Check if CPU time is finished
			for (Process p : tempProcesses) {
				if (p.getC() == 0) {
					if (currentRunningProcess == p) {
						if (!readyProcesses.isEmpty()) {
							currentRunningProcessIndex = tempProcesses.indexOf(readyProcesses.get(0));
							currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
							currentRunningProcess.setProcessState(2);
							readyProcesses.remove(0);
							CPUBurst = randomOS(currentRunningProcess.getB());
						}
					}
					
					if (p.getFinishTime() == 0) {
						p.setFinishTime(time);
					}
					p.setProcessState(4);
					readyProcesses.remove(p);
					if (!terminatedProcesses.contains(p)) {
						terminatedProcesses.add(p);
					}
				}
			}
			
			// Decrement the blocked time for each process
			for (Process p : tempProcesses) {
				if (p.getBlockedTime() > 0) {
					p.decBlockedTime();
				}
			}
			
			// Increment the waiting time for ready processes
			for (Process p : tempProcesses) {
				if (p.getProcessState() == 1) {
					p.incWaitingTime();
				}
			}

			// If a process is running, increment CPUUtilization
			if (currentRunningProcess != null) {
				CPUUtilization++;
			}
			
			// For every blocked process, increment IOUtilization
			for (Process p : tempProcesses) {
				if (p.getBlockedTime() >= 0) {
					IOUtilization++;
					break;
				}
			}
			time++;
		}

		output.append(("\nThe scheduling algorithm used was First Come First Served\n\n"));

		// Print processes
		printProcesses();
		
		// Print summary
		finishTime = maxFinishTime;
		CPUUtilization /=  maxFinishTime;
		IOUtilization /= maxFinishTime;
		throughput = ((double)processCount / (double)maxFinishTime) * 100;
		avgTurnaroundTime = avgTurnaroundTime / processCount;
		avgWaitTime = avgWaitTime / processCount;
		printSummary(finishTime, CPUUtilization, IOUtilization, throughput, avgTurnaroundTime, avgWaitTime);
		
		/*
			RR: Round Robin
		*/
		// Reset variables
		randoms.close();
		randoms = null;
		randoms = new Scanner(randomNumbers);
		time = 0;
		finishTime = 0;
		CPUUtilization = 0;
		IOUtilization = 0;
		throughput = 0;
		avgTurnaroundTime = 0;
		avgWaitTime = 0;
		// Null if nothing is running
		currentRunningProcess = null;
		// Copy process list into temporary array
		cloneProcesses();
		readyProcesses.clear();
		terminatedProcesses.clear();
		
		if (verbose) {
			output.append("\nThis detailed printout gives the state and remaining burst for each process\n");
		}
		
		// Run until every process has been terminated
		while (terminatedProcesses.size() < processCount) {
			if (verbose) {
				output.append("\nBefore cycle	" + time + ":	");
				for (Process p : tempProcesses) {
					printVerbose(p);
				}
			}
			
			if (time == 1725) {
				System.out.println("Here");
			}
			// Unblock processes if blocked time is 0
			for (Process p: tempProcesses) {
				if (p.getBlockedTime() == 0) {
					p.decBlockedTime();
					if (p.getProcessState() != 4) {
						p.setProcessState(1);
						readyProcesses2.add(p);
					}
				}
			}
			
			// Check for new process
			for (Process p : tempProcesses) {
				if (p.getA() == time) {
					p.setProcessState(1);
					readyProcesses2.add(p);
				}
			}
			
			// If no process is currently running
			if (currentRunningProcess == null) {
				if (!readyProcesses.isEmpty()) {
					readyProcesses.get(0).setProcessState(2);
					currentRunningProcessIndex = tempProcesses.indexOf(readyProcesses.get(0));
					currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
					if (currentRunningProcess.getBurstCount() == 0) {
						CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
						currentRunningProcess.setBurstCount(CPUBurst);
						currentRunningProcess.setBurst(CPUBurst);
					}
					readyProcesses.remove(0);
					quantum = 2;
				}
				else if (!readyProcesses2.isEmpty()) {
					readyProcesses2.get(0).setProcessState(2);
					currentRunningProcessIndex = tempProcesses.indexOf(readyProcesses2.get(0));
					currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
					if (currentRunningProcess.getBurstCount() == 0) {
						CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
						currentRunningProcess.setBurstCount(CPUBurst);
						currentRunningProcess.setBurst(CPUBurst);
					}
					readyProcesses2.remove(0);
					quantum = 2;
				}
			}
			// Check to block
			else if (currentRunningProcess != null && currentRunningProcess.getBurstCount() == 1) {
				if (currentRunningProcess.getC() != 1) {
					currentRunningProcess.setProcessState(3);
					currentRunningProcess.decBurstCount();
					currentRunningProcess.decC();
					currentRunningProcess.setBlockedTime(currentRunningProcess.getM() * currentRunningProcess.getBurst());
					currentRunningProcess.setIOTime(currentRunningProcess.getIOTime() + currentRunningProcess.getBlockedTime());
				}
				else {
					if (currentRunningProcess.getFinishTime() == 0) {
						currentRunningProcess.setFinishTime(time);
					}
					currentRunningProcess.setProcessState(4);
					currentRunningProcess.decC();
				}

				if (!readyProcesses.isEmpty()) {
					currentRunningProcessIndex = tempProcesses.indexOf(readyProcesses.get(0));
					currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
					currentRunningProcess.setProcessState(2);
					if (currentRunningProcess.getBurstCount() == 0) {
						CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
						currentRunningProcess.setBurstCount(CPUBurst);
						currentRunningProcess.setBurst(CPUBurst);
					}
					readyProcesses.remove(0);
					quantum = 2;
				}
				else if (!readyProcesses2.isEmpty()) {
					currentRunningProcessIndex = tempProcesses.indexOf(readyProcesses2.get(0));
					currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
					currentRunningProcess.setProcessState(2);
					if (currentRunningProcess.getBurstCount() == 0) {
						CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
						currentRunningProcess.setBurstCount(CPUBurst);
						currentRunningProcess.setBurst(CPUBurst);
					}
					readyProcesses2.remove(0);
					quantum = 2;
				}
				else {
					currentRunningProcess = null;
				}
			}
			// If a process is currently running
			else if (currentRunningProcess != null && currentRunningProcess.getBurstCount() > 0) {
				if (quantum != 1) {
					quantum--;
					currentRunningProcess.decBurstCount();
					currentRunningProcess.decC();
				}
				else {
					currentRunningProcess.setProcessState(1);
					currentRunningProcess.decBurstCount();
					currentRunningProcess.decC();
					readyProcesses2.add(currentRunningProcess);
					if (!readyProcesses.isEmpty()) {
						currentRunningProcessIndex = tempProcesses.indexOf(readyProcesses.get(0));
						currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
						currentRunningProcess.setProcessState(2);
						if (currentRunningProcess.getBurstCount() == 0) {
							CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
							currentRunningProcess.setBurstCount(CPUBurst);
							currentRunningProcess.setBurst(CPUBurst);
						}
						readyProcesses.remove(0);
						quantum = 2;
					}
					else if (!readyProcesses2.isEmpty()) {
						temp = 0;
						min = tempProcesses.size() - 1;
						for (Process p : readyProcesses2) {
							if (tempProcesses.indexOf(p) < min) {
								min = tempProcesses.indexOf(p);
								temp = readyProcesses2.indexOf(p);
							}
						}
						
						currentRunningProcessIndex = tempProcesses.indexOf(readyProcesses2.get(temp));
						currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
						currentRunningProcess.setProcessState(2);
						if (currentRunningProcess.getBurstCount() == 0) {
							CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
							currentRunningProcess.setBurstCount(CPUBurst);
							currentRunningProcess.setBurst(CPUBurst);
						}
						readyProcesses2.remove(temp);
						quantum = 2;
					}
				}
			}
			
			Collections.sort(readyProcesses2);
			readyProcesses.addAll(readyProcesses2);
			readyProcesses2.clear();
			
			// Check if CPU time is finished
			for (Process p : tempProcesses) {
				if (p.getC() == 0) {
					if (currentRunningProcess == p) {
						if (!readyProcesses.isEmpty()) {
							currentRunningProcessIndex = tempProcesses.indexOf(readyProcesses.get(0));
							currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
							currentRunningProcess.setProcessState(2);
							if (currentRunningProcess.getBurstCount() == 0) {
								CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
								currentRunningProcess.setBurstCount(CPUBurst);
								currentRunningProcess.setBurst(CPUBurst);
							}
							readyProcesses.remove(0);
							quantum = 2;
						}
						else if (!readyProcesses2.isEmpty()) {
							currentRunningProcessIndex = tempProcesses.indexOf(readyProcesses2.get(0));
							currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
							currentRunningProcess.setProcessState(2);
							if (currentRunningProcess.getBurstCount() == 0) {
								CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
								currentRunningProcess.setBurstCount(CPUBurst);
								currentRunningProcess.setBurst(CPUBurst);
							}
							readyProcesses2.remove(0);
							quantum = 2;
						}
					}
					
					if (p.getFinishTime() == 0) {
						p.setFinishTime(time);
					}
					p.setProcessState(4);
					readyProcesses.remove(p);
					if (!terminatedProcesses.contains(p)) {
						terminatedProcesses.add(p);
						if (currentRunningProcess == p) {
							currentRunningProcess = null;
						}
					}
				}
			}
			
			// Decrement the blocked time for each process
			for (Process p : tempProcesses) {
				if (p.getBlockedTime() > 0) {
					p.decBlockedTime();
				}
			}
			
			// Increment the waiting time for ready processes
			for (Process p : tempProcesses) {
				if (p.getProcessState() == 1) {
					p.incWaitingTime();
				}
			}

			// If a process is running, increment CPUUtilization
			if (currentRunningProcess != null) {
				CPUUtilization++;
			}
			
			// For every blocked process, increment IOUtilization
			for (Process p : tempProcesses) {
				if (p.getBlockedTime() >= 0) {
					IOUtilization++;
					break;
				}
			}
			time++;
		}
		
		output.append(("\nThe scheduling algorithm used was Round Robin\n\n"));
		
		// Print processes
		printProcesses();

		// Print summary
		finishTime = maxFinishTime;
		CPUUtilization /=  maxFinishTime;
		IOUtilization /= maxFinishTime;
		throughput = ((double)processCount / (double)maxFinishTime) * 100;
		avgTurnaroundTime = avgTurnaroundTime / processCount;
		avgWaitTime = avgWaitTime / processCount;
		printSummary(finishTime, CPUUtilization, IOUtilization, throughput, avgTurnaroundTime, avgWaitTime);
		
		/*
			Uniprogrammed
		*/
		// Reset variables
		randoms.close();
		randoms = null;
		randoms = new Scanner(randomNumbers);
		time = 0;
		finishTime = 0;
		CPUUtilization = 0;
		IOUtilization = 0;
		throughput = 0;
		avgTurnaroundTime = 0;
		avgWaitTime = 0;
		// Null if nothing is running
		currentRunningProcess = null;
		// Copy process list into temporary array
		cloneProcesses();
		terminatedProcesses.clear();
		
		if (verbose) {
			output.append("\nThis detailed printout gives the state and remaining burst for each process\n");
		}
		
		// Run until every process has been terminated
		while (terminatedProcesses.size() < processCount) {
			if (verbose) {
				output.append("\nBefore cycle	" + time + ":	");
				for (Process p : tempProcesses) {
					printVerbose(p);
				}
			}
			
			// Reset first and ran
			for (Process p : tempProcesses) {
				p.setFirst(false);
				p.setRan(false);
			}
			
			// Unblock current process if blocked time is 0
			if (currentRunningProcess != null) {
				if (currentRunningProcess.getBlockedTime() == 0) {
					currentRunningProcess.decBlockedTime();
					if (currentRunningProcess.getProcessState() != 4) {
						CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
						currentRunningProcess.setBurst(CPUBurst);
						currentRunningProcess.setProcessState(2);
						currentRunningProcess.setFirst(true);
					}
				}
			}
			
			// Check for new process
			for (Process p : tempProcesses) {
				if (p.getA() == time) {
					if (currentRunningProcess == null) {
						currentRunningProcessIndex = tempProcesses.indexOf(p);
						currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
						CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
						currentRunningProcess.setBurst(CPUBurst);
						p.setProcessState(2);
						p.setFirst(true);
					}
					else {
						p.setProcessState(1);
					}
				}
			}
			
			// Check to block
			if (currentRunningProcess != null && CPUBurst == 1 && currentRunningProcess.isFirst() == false) {
				if (currentRunningProcess.getProcessState() == 2) {
					if (currentRunningProcess.getC() == 1) {
						if (currentRunningProcess.getFinishTime() == 0) {
							currentRunningProcess.setFinishTime(time);
						}
						currentRunningProcess.setProcessState(4);
						currentRunningProcess.decC();
						
						if (currentRunningProcessIndex == tempProcesses.size() - 1) {
							terminatedProcesses.add(currentRunningProcess);
						}
						else if (tempProcesses.get(currentRunningProcessIndex + 1).getProcessState() == 1) {
							currentRunningProcessIndex++;
							currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
							currentRunningProcess.setProcessState(2);
							CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
							currentRunningProcess.setBurst(CPUBurst);
						}
						else {
							currentRunningProcess = null;
						}
					}
					else {
						currentRunningProcess.setProcessState(3);
						currentRunningProcess.decC();
						currentRunningProcess.setBlockedTime(currentRunningProcess.getM() * currentRunningProcess.getBurst());
						currentRunningProcess.setIOTime(currentRunningProcess.getIOTime() + currentRunningProcess.getBlockedTime());
					}
				}
			}
			// If a process is currently running
			else if (currentRunningProcess != null && CPUBurst > 1 && currentRunningProcess.isFirst() == false) {
				if (currentRunningProcess.getProcessState() == 2) {
					if (currentRunningProcess.getC() != 1) {
						CPUBurst--;
						currentRunningProcess.decC();
					}
					else {
						if (currentRunningProcess.getFinishTime() == 0) {
							currentRunningProcess.setFinishTime(time);
						}
						currentRunningProcess.setProcessState(4);
						currentRunningProcess.decC();
						if (currentRunningProcessIndex == tempProcesses.size() - 1) {
							terminatedProcesses.add(currentRunningProcess);
						}
						else if (tempProcesses.get(currentRunningProcessIndex + 1).getProcessState() == 1) {
							currentRunningProcessIndex++;
							currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
							currentRunningProcess.setProcessState(2);
							CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
							currentRunningProcess.setBurst(CPUBurst);
						}
					}
				}
			}
			
			// Check if CPU time is finished
			for (Process p : tempProcesses) {
				if (p.getC() == 0) {
					if (!terminatedProcesses.contains(p)) {
						terminatedProcesses.add(p);
					}
				}
			}
			
			// Decrement blocked time
			if (currentRunningProcess.getBlockedTime() > 0) {
				currentRunningProcess.decBlockedTime();
			}

			// Increment the waiting time for processes
			for (Process p : tempProcesses) {
				if (p.getProcessState() == 2) {
					p.setRan(true);
				}
				if (p.getProcessState() == 1) {
					p.incWaitingTime();
				}
			}

			// If a process ran, increment CPUUtilization
			int ran = 0;
			for (Process p : tempProcesses) {
				if (p.isRan() == true) {
					ran++;
				}
			}
			if (ran > 0) {
				CPUUtilization++;
			}
			
			// For every blocked process, increment IOUtilization
			for (Process p : tempProcesses) {
				if (p.getBlockedTime() >= 0) {
					IOUtilization++;
					break;
				}
			}
			time++;
		}
		
		output.append(("\nThe scheduling algorithm used was Uniprocessing\n\n"));
		
		// Print processes
		printProcesses();
		
		// Print summary
		finishTime = maxFinishTime;
		CPUUtilization /=  maxFinishTime;
		IOUtilization /= maxFinishTime;
		throughput = ((double)processCount / (double)maxFinishTime) * 100;
		avgTurnaroundTime = avgTurnaroundTime / processCount;
		avgWaitTime = avgWaitTime / processCount;
		printSummary(finishTime, CPUUtilization, IOUtilization, throughput, avgTurnaroundTime, avgWaitTime);
		
		/*
			SJF: Shortest Job First
		*/
		randoms.close();
		randoms = null;
		randoms = new Scanner(randomNumbers);
		time = 0;
		finishTime = 0;
		CPUUtilization = 0;
		IOUtilization = 0;
		throughput = 0;
		avgTurnaroundTime = 0;
		avgWaitTime = 0;
		// Null if nothing is running
		currentRunningProcess = null;
		// Copy process list into temporary array
		cloneProcesses();
		readyProcesses.clear();
		terminatedProcesses.clear();
		
		if (verbose) {
			output.append("\nThis detailed printout gives the state and remaining burst for each process\n");
		}
		
		// Run until every process has been terminated
		while (terminatedProcesses.size() < processCount) {
			if (verbose) {
				output.append("\nBefore cycle	" + time + ":	");
				for (Process p : tempProcesses) {
					printVerbose(p);
				}
			}
			// Unblock processes if blocked time is 0
			for (Process p: tempProcesses) {
				if (p.getBlockedTime() == 0) {
					p.decBlockedTime();
					if (p.getProcessState() != 4) {
						p.setProcessState(1);
						readyProcesses.add(p);
					}
				}
			}
			
			// Check for new process
			for (Process p : tempProcesses) {
				if (p.getA() == time) {
					p.setProcessState(1);
					readyProcesses.add(p);
				}
			}
			
			// If no process is currently running
			if (currentRunningProcess == null) {
				temp = -1;
				min = Integer.MAX_VALUE;
				if (!readyProcesses.isEmpty()) {
					for (int i = 0; i < readyProcesses.size(); i++) {
						if (readyProcesses.get(i).getC() < min) {
							temp = i;
							min = readyProcesses.get(temp).getC();
						}
					}
					readyProcesses.get(temp).setProcessState(2);
					currentRunningProcessIndex = tempProcesses.indexOf(readyProcesses.get(temp));
					currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
					CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
					currentRunningProcess.setBurst(CPUBurst);
					readyProcesses.remove(temp);
				}
			}
			
			// Check to block
			else if (currentRunningProcess != null && CPUBurst == 1) {
				if (currentRunningProcess.getC() != 1) {
					currentRunningProcess.setProcessState(3);
					currentRunningProcess.decC();
					currentRunningProcess.setBlockedTime(currentRunningProcess.getM() * currentRunningProcess.getBurst());
					currentRunningProcess.setIOTime(currentRunningProcess.getIOTime() + currentRunningProcess.getBlockedTime());
				}
				else {
					if (currentRunningProcess.getFinishTime() == 0) {
						currentRunningProcess.setFinishTime(time);
					}
					currentRunningProcess.setProcessState(4);
					currentRunningProcess.decC();
				}
				
				if (!readyProcesses.isEmpty()) {
					temp = -1;
					min = Integer.MAX_VALUE;
					for (int i = 0; i < readyProcesses.size(); i++) {
						if (readyProcesses.get(i).getC() < min) {
							temp = i;
							min = readyProcesses.get(temp).getC();
						}
					}
					readyProcesses.get(temp).setProcessState(2);
					currentRunningProcessIndex = tempProcesses.indexOf(readyProcesses.get(temp));
					currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
					CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
					currentRunningProcess.setBurst(CPUBurst);
					readyProcesses.remove(temp);
				}
				else {
					currentRunningProcess = null;
				}
			}
			// If a process is currently running
			else if (currentRunningProcess != null && CPUBurst > 0) {
				CPUBurst--;
				currentRunningProcess.decC();
			}
			
			// Check if CPU time is finished
			for (Process p : tempProcesses) {
				if (p.getC() == 0) {
					if (currentRunningProcess == p) {
						if (!readyProcesses.isEmpty()) {
							temp = -1;
							min = Integer.MAX_VALUE;
							for (int i = 0; i < readyProcesses.size(); i++) {
								if (readyProcesses.get(i).getC() < min) {
									temp = i;
									min = readyProcesses.get(temp).getC();
								}
							}
							readyProcesses.get(temp).setProcessState(2);
							currentRunningProcessIndex = tempProcesses.indexOf(readyProcesses.get(temp));
							currentRunningProcess = tempProcesses.get(currentRunningProcessIndex);
							CPUBurst = randomOS(tempProcesses.get(currentRunningProcessIndex).getB());
							currentRunningProcess.setBurst(CPUBurst);
							readyProcesses.remove(temp);
						}
						else {
							currentRunningProcess = null;
						}
					}
					
					if (p.getFinishTime() == 0) {
						p.setFinishTime(time);
					}
					p.setProcessState(4);
					readyProcesses.remove(p);
					if (!terminatedProcesses.contains(p)) {
						terminatedProcesses.add(p);
					}
				}
			}
			
			// Decrement the blocked time for each process
			for (Process p : tempProcesses) {
				if (p.getBlockedTime() > 0) {
					p.decBlockedTime();
				}
			}
			
			// Increment the waiting time for ready processes
			for (Process p : tempProcesses) {
				if (p.getProcessState() == 1) {
					p.incWaitingTime();
				}
			}

			// If a process is running, increment CPUUtilization
			if (currentRunningProcess != null) {
				CPUUtilization++;
			}
			
			// For every blocked process, increment IOUtilization
			for (Process p : tempProcesses) {
				if (p.getBlockedTime() >= 0) {
					IOUtilization++;
					break;
				}
			}
			time++;
		}
		
		output.append(("\nThe scheduling algorithm used was Shortest Job First\n\n"));
		
		// Print processes
		printProcesses();
		
		// Print summary
		finishTime = maxFinishTime;
		CPUUtilization /=  maxFinishTime;
		IOUtilization /= maxFinishTime;
		throughput = ((double)processCount / (double)maxFinishTime) * 100;
		avgTurnaroundTime = avgTurnaroundTime / processCount;
		avgWaitTime = avgWaitTime / processCount;
		printSummary(finishTime, CPUUtilization, IOUtilization, throughput, avgTurnaroundTime, avgWaitTime);
		
		/*
			Done! Print the total output for all 4 scheduling algorithms
		*/
		System.out.println(output.toString());
	}
	
	// Random
	public static int randomOS(int U) {
		int i = randoms.nextInt();
		return 1 + (i % U);
	}
	
	// Clone process list to temporary list
	public static void cloneProcesses() {
		tempProcesses.clear();
		for (Process p : processes) {
			Process p2 = new Process(p.getA(), p.getB(), p.getC(), p.getM());
			tempProcesses.add(p2);
		}
	}
	
	// Prints the detailed verbose output
	public static void printVerbose(Process p) {
		if (p.getProcessState() == 0)
			output.append("unstarted 0\t");
		else if (p.getProcessState() == 1)
			output.append("ready 0\t\t");
		else if (p.getProcessState() == 2) {
			output.append("running " + quantum + "\t");
		}
		else if (p.getProcessState() == 3) {
			output.append("blocked " + (p.getBlockedTime() + 1) + "\t");
		}
		else if (p.getProcessState() == 4)
			output.append("terminated 0\t");
		
		if (p == tempProcesses.get(tempProcesses.size() - 1)) {
			output.deleteCharAt(output.length() - 1);
			if (p.getProcessState() == 1)
				output.deleteCharAt(output.length() - 1);
			output.append(".");
		}
	}
	
	// Prints the processes
	public static void printProcesses() {
		count = 0;
		maxFinishTime = -1;
		for (Process p : tempProcesses) {
			if (p.getFinishTime() > maxFinishTime) {
				maxFinishTime = p.getFinishTime();
			}
			p.setC(processes.get(count).getC());
			p.setTurnaroundTime(p.getFinishTime() - p.getA());
			avgWaitTime += p.getWaitingTime();
			avgTurnaroundTime += p.getFinishTime() - p.getA();
			output.append("Process " + count++ + ":\n");
			output.append(p.toString());
		}
	}
	
	// Prints the summary for a set of processes
	public static void printSummary(int finishTime, double CPUUtilization, double IOUtilization, double throughput, double avgTurnaroundTime, double avgWaitTime) {
		output.append("Summary Data:\n")
		.append("\tFinishing time: " + finishTime + "\n")
		.append("\tCPU Utilization: " + numberFormat.format(CPUUtilization) + "\n")
		.append("\tI/O Utilization: " + numberFormat.format(IOUtilization) + "\n")
		.append("\tThroughput: " + numberFormat.format(throughput) + " processes per hundred cycles\n")
		.append("\tAverage turnaround time: " + numberFormat.format(avgTurnaroundTime) + "\n")
		.append("\tAverage waiting time: " + numberFormat.format(avgWaitTime) + "\n")
		.append("----------------------------------------------------------------------------------------");
	}
	
	public static void main(String args []) {
		try {
			// Check for verbose argument
			if (args[0].equals("--verbose")) 
				verbose = true;
			else 
				verbose = false;
			
			if (verbose == true) 
				runScheduler(args[1]);
			else 
				runScheduler(args[0]);
		}
		catch (FileNotFoundException e) {
			System.out.println("Error! Sample Commands:\njava Scheduler input-1\njava Scheduler --verbose input-1");
		}
	}
}