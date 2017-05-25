Compile the program with:

javac Process.java Pager.java

To run the program:

java Pager M P S J N R D

M, the machine size in words.
P, the page size in words.
S, the size of a process, i.e., the references are to virtual addresses 0..S-1.
J, the 'job mix', which determines A, B, and C, as described below.
N, the number of references for each process.
R, the replacement algorithm, LIFO, RANDOM, or LRU. algorithm name is case insensitive)
D, the level of debugging output (Not implemented).

Sample input:

java Pager 10 10 20 1 10 LRU 0