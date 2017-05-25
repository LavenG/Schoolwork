/* 
    This class represents a Process
*/

public class Process {
	private int processID;
    private double a;
    private double b; 
    private double c;
    private int size;
    private int references;
    private int word;
    private int total = 0;
    private int faults = 0;
    private int evictions = 0;
    
    // Constructor
    public Process(int pid, double A, double B, double C, int S, int N) {
        this.processID = pid;
    	this.a = A;
        this.b = B;
        this.c = C;
        this.size = S;
        this.references = N;
        this.word = (111 * this.processID) % S;
    }
    
	public int getProcessID() {
		return processID;
	}
    public double getA() {
        return a;
    }
    public double getB() {
        return b;
    }
    public double getC() {
        return c;
    }  
    public int getSize() {
        return size;
    }  
    public int getReferences() {
        return references;
    }
    public void decrementN() {
        references--;
    }
    public int getFaults() {
        return faults;
    }
    public void incrementFaults() {
        this.faults++;
    }
    public int getEvictions() {
        return evictions;
    }
    public void incrementEvictions() {
        this.evictions++;
    }
    public int getWord() {
        return word;
    }
    public void setWord(int word) {
        this.word = word;
    }
    public int getTotal() {
        return total;
    }
    public void addTotal(int x) {
        this.total += x;
    }
}