public class Process implements Comparable<Process> {
	/*
	 	A - Arrival Time
	 	B - Limit of random CPU burst
	 	C - Total CPU time needed
	 	M - Multiplier for calculating I/O burst 
	*/
	private int A;
	private int B;
	private int C;
	private int M;
	/*
	  	Process State:
	  	0 - Not started
	  	1 - Ready
	  	2 - Running
	  	3 - Blocked
	  	4 - Terminated
	*/
	private int processState = 0;
	private int finishTime = 0;
	private int turnaroundTime = 0;
	private int IOTime = 0;
	private int waitingTime = 0;
	private int burst = 0;
	private int burstCount = 0;
	// Set to 0 if not blocked
	private int blockedTime = -1;
	private boolean first;
	private boolean ran;
	
	public Process(int a, int b, int c, int m) {
		A = a;
		B = b;
		C = c;
		M = m;
	}
	
	public String toString() {
		StringBuilder output = new StringBuilder();;
		output.append("\t(A,B,C,M) = ")
		.append("(" + this.A + "," + this.B + "," + this.C + "," + this.M + ")\n")
		.append("\tFinishing time: " + this.finishTime + "\n")
		.append("\tTurnaround time: " + this.turnaroundTime + "\n")
		.append("\tI/O time: " + this.IOTime + "\n")
		.append("\tWaiting time: " + this.waitingTime + "\n\n");
		return output.toString();
	}
	
	public int compareTo(Process p){
		if (p.A < this.A)
			return 1;
		else if (p.A > this.A)
			return -1;
		else if (p.B < this.B)
			return 1;
		else if (p.B > this.B)
			return -1;
		else if (p.C < this.C)
			return 1;
		else if (p.C > this.C)
			return -1;
		else if (p.M < this.M)
			return 1;
		else if (p.M > this.M)
			return -1;
		else
			return 0;
	}
	
	// Getters and Setters
	public int getA() {
		return A;
	}
	public void setA(int a) {
		A = a;
	}
	public int getB() {
		return B;
	}
	public void setB(int b) {
		B = b;
	}
	public int getC() {
		return C;
	}
	public void setC(int c) {
		C = c;
	}
	public void decC() {
		this.C--;
	}
	public int getM() {
		return M;
	}
	public void setM(int m) {
		M = m;
	}
	public int getProcessState() {
		return processState;
	}
	public void setProcessState(int processState) {
		this.processState = processState;
	}
	public int getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}
	public int getTurnaroundTime() {
		return turnaroundTime;
	}
	public void setTurnaroundTime(int turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
	}
	public int getIOTime() {
		return IOTime;
	}
	public void setIOTime(int iOTime) {
		IOTime = iOTime;
	}
	public int getWaitingTime() {
		return waitingTime;
	}
	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}
	public void incWaitingTime() {
		this.waitingTime++;
	}
	public int getBurst() {
		return burst;
	}
	public void setBurst(int burst) {
		this.burst = burst;
	}
	public int getBurstCount() {
		return burstCount;
	}
	public void setBurstCount(int burstCount) {
		this.burstCount = burstCount;
	}
	public void decBurstCount() {
		this.burstCount--;
	}
	public int getBlockedTime() {
		return blockedTime;
	}
	public void setBlockedTime(int blockedTime) {
		this.blockedTime = blockedTime;
	}
	public void decBlockedTime() {
		this.blockedTime--;
	}
	public boolean isFirst() {
		return first;
	}
	public void setFirst(boolean first) {
		this.first = first;
	}
	public boolean isRan() {
		return ran;
	}
	public void setRan(boolean ran) {
		this.ran = ran;
	}
}