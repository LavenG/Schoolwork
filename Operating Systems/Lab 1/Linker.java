import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class Linker {
	// Variables
	static int useCount = 0;
	static int symbolUse = 0;
	static int definitionCount = 0;
	static int definition = 0;
	static int moduleCount = 0;
	static int moduleSize = 0;
	static int absoluteAddress = 0;
	static int address = 0;
	static int resolvedAddress = 0;
	static int memoryMapCount = 0;
	static int[] baseAddresses = new int[10];
	static Map<String, Integer> symbolDefinedLocation = new HashMap<String, Integer>();;
	static ArrayList<Integer> symbolUses;
	static Map<String, Integer> definitionList = new HashMap<String, Integer>();
	static Map<String, Integer> defList = new HashMap<String, Integer>();
	static Map<String, Integer> symbolTable = new HashMap<String, Integer>();
	static Map<String, Integer> symbolTableUsed = new HashMap<String, Integer>();
	static Map<String, ArrayList<Integer>> symbolUseList = new HashMap<String, ArrayList<Integer>>();
	static Map<String, ArrayList<Integer>> currentModuleUseList = new HashMap<String, ArrayList<Integer>>();
	static String addressType;
	static String symbol;
	static String symbolUsed;
	static Scanner input;
	static StringBuilder output = new StringBuilder();
    	static StringBuilder warnings = new StringBuilder();
    
	public static void runLinker(String filePath) throws FileNotFoundException {
		// Reading in the file
		FileReader fReader = new FileReader(filePath);
		input = new Scanner(fReader);

		/*
			First Pass: Determines the base address for each module and the
			absolute address for each external symbol, storing the latter in
			a symbol table.
		*/
		output.append("Symbol Table");
		moduleCount = input.nextInt();

		for (int i = 0; i < moduleCount; i++) {
			// Definition List
			definitionCount = input.nextInt();
			for (int j = 0; j < definitionCount; j++) {
				symbol = input.next();
				definition = input.nextInt();
				definitionList.put(symbol, definition);
				defList.put(symbol, definition);
				absoluteAddress = definition + baseAddresses[i];
				if (symbolTable.containsKey(symbol)) {
					// Rule 1: If a symbol is multiply defined, print an error message and use the value given in the first definition.
					output.append(" Error: This variable is multiply defined; first value used."); 
				}
				else {
					symbolTable.put(symbol, absoluteAddress);
					symbolDefinedLocation.put(symbol, i + 1);
					output.append("\n" + symbol + " = " + absoluteAddress);
				}
			}

			// Use List
			useCount = input.nextInt();
			for (int j = 0; j < useCount; j++) {
				symbolUses = new ArrayList<Integer>();
				symbol = input.next();
				while ((symbolUse = input.nextInt()) != -1) {
					symbolUses.add(symbolUse);
				}
				symbolUseList.put(symbol, symbolUses);
			}

			// Program Text
			moduleSize = input.nextInt();
			baseAddresses[i + 1] += baseAddresses[i] + moduleSize;

			// Rule 5: If an address appearing in a definition exceeds the size of the module, 
			//         print an error message and treat the address given as 0 (relative).
			for (Entry<String, Integer> entry : definitionList.entrySet()) {
				if (entry.getValue() + 1 > moduleSize) {
					int tempDef = symbolTable.get(entry.getKey());
					symbolTable.put(entry.getKey(), tempDef - entry.getValue());
					output.replace(0, output.length(), output.toString().replace(entry.getKey() + " = " + tempDef, 
					entry.getKey() + " = " + symbolTable.get(entry.getKey()) + " Error: Definition exceeds module size; first word in module used."));

				}
			}
			definitionList.clear();

			// Rule 6: If an address appearing in a use list exceeds the size of the module, 
				// 		   print an error message and ignore this particular use.
			if (useCount != 0) {
				if (symbolUseList != null) {
					for (Map.Entry<String, ArrayList<Integer>> entry : symbolUseList.entrySet()) {
						if (entry.getKey().equals(symbol)) {
							for (Integer k : entry.getValue()) {
							if (k > moduleSize) {
								warnings.append("Error: Use of " + entry.getKey() + " in module " + (i + 1) + " exceeds module size; use ignored.\n");
							}
						}
						}
					}
				}
			}

			for (int j = 0; j < moduleSize; j++) {
				addressType = input.next();
				address = input.nextInt();
			}
		}

		// Reset
		input.close();
		input = null;
		fReader = new FileReader(filePath);
		input = new Scanner(fReader);
		symbolUseList.clear();

		/*
			Second Pass: Uses the base addresses and the symbol table from pass one to
			generate the actual output by relocating relative addresses and resolving
			external references.
		*/
		output.append("\n\nMemory Map\n");
		moduleCount = input.nextInt();

		for (int i = 0; i < moduleCount; i++) {
			// Definition List
			definitionCount = input.nextInt();
			for (int j = 0; j < definitionCount; j++) {
				symbol = input.next();
				definition = input.nextInt();
				definitionList.put(symbol, definition);
			}

			// Use List
			useCount = input.nextInt();
			currentModuleUseList.clear();
			symbolUseList.clear();
			for (int j = 0; j < useCount; j++) {
				ArrayList<Integer> temp1 = new ArrayList<Integer>();
				symbolUses = new ArrayList<Integer>();
				symbol = input.next();
				if (symbolTable.get(symbol) != null) {
					definition = symbolTable.get(symbol);
					symbolTable.remove(symbol);
					symbolTableUsed.put(symbol, definition);
				}
				if (symbolTableUsed.get(symbol) == null) {
					symbolTableUsed.put(symbol, 0);
				}

				while ((symbolUse = input.nextInt()) != -1) {
					symbolUses.add(symbolUse);
					temp1.add(symbolUse);
				}

				currentModuleUseList.put(symbol, temp1);
				ArrayList<Integer> tempSymbolUses = new ArrayList<Integer>();
				for (Map.Entry<String, ArrayList<Integer>> entry : symbolUseList.entrySet()) {
					for (int k : entry.getValue()) {
						tempSymbolUses.add(k);
					}
				}
				symbolUses.removeAll(tempSymbolUses);
				symbolUseList.put(symbol, symbolUses);
			}

			// Program Text
			moduleSize = input.nextInt();
			for (int j = 0; j < moduleSize; j++) {
				addressType = input.next();
				address = input.nextInt();

				// Immediate Address
				if (addressType.equals("I")) {
					output.append(memoryMapCount + ": " + address + "\n");
				}
				// Absolute Address
				else if (addressType.equals("A")) {
					if ((address % 1000) > 200) {
						// Rule 7: If an absolute address exceeds the size of the machine, 
						// 		   print an error message and use the value zero.
						output.append(memoryMapCount + ": " + (address / 1000 * 1000))
						.append(" Error: Absolute address exceeds machine size; zero used.\n");
					}
					else {
						output.append(memoryMapCount + ": " + address + "\n");
					}
				}
				// Relative Address
				else if (addressType.equals("R")) {
					if ((address % 1000) > baseAddresses[i + 1] - baseAddresses[i]) {
						// Rule 8: If a relative address exceeds the size of the module, 
						// 		   print an error message and use the value zero (absolute).
						output.append(memoryMapCount + ": " + (address / 1000 * 1000))
						.append(" Error: Relative address exceeds module size; zero used.\n");
					}
					else {
						address = baseAddresses[i] + address;
						output.append(memoryMapCount + ": " + address + "\n");
					}
				}
				// External Address
				else if (addressType.equals("E")) {
				for (Map.Entry<String, ArrayList<Integer>> entry : symbolUseList.entrySet()) {
					for (int k : entry.getValue()) {
						if (k == j) {
							symbolUsed = entry.getKey();
						}
					}
				}

					if (symbolTableUsed.containsKey(symbolUsed) && defList.containsKey(symbolUsed)) {
						String temp = Integer.toString(address).substring(0, 1);
						int definition = symbolTableUsed.get(symbolUsed);
						if (definition < 10) {
							address = Integer.parseInt(temp += "00" + definition);
						}
						else if (definition < 100) {
							address = Integer.parseInt(temp += "0" + definition);
						}
						else {
							address = Integer.parseInt(temp += definition);
						}
						output.append(memoryMapCount + ": " + address + "\n");
					}
					else {
						// Rule 2: If a symbol is used but not defined, print an error message and use the value zero.
						address = Integer.parseInt(Integer.toString(address).substring(0, 1) + "000");
						output.append(memoryMapCount + ": " + address);
						output.append(" Error: " + symbol + " is not defined; zero used.\n");
					}
				}

				// Rule 4: If multiple symbols are listed as used in the same instruction, 
				//         print an error message and ignore all but the first usage given.
				ArrayList<Integer> tempArray = new ArrayList<Integer>();
				for (Map.Entry<String, ArrayList<Integer>> entry : currentModuleUseList.entrySet()) {
					for (int k : entry.getValue()) {
						if (tempArray.contains(k)) {
							if (j == k) {
								output.deleteCharAt(output.length() - 1);
								output.append(" Error: Multiple variables used in instruction; all but first ignored.\n");
							}
						}
						else {
						tempArray.add(k);
						}
					}
				}

				memoryMapCount++;
			}
		}

		// Rule 3: If a symbol is defined but not used, print a warning message and continue.
		for (String s : symbolTable.keySet()) {
			if (!symbolTableUsed.containsKey(s)) {
				for (Map.Entry<String, Integer> entry : symbolDefinedLocation.entrySet()) {
						if (entry.getKey().equals(s)) {
							warnings.append("Warning: " + s + " was defined in module " + entry.getValue() + " but never used.\n");
						}
				}
			}
		}

		// Print output
		System.out.println(output.toString());
		System.out.println(warnings.toString());	
	}
	
	public static void main(String args[]) {
		try {
			runLinker(args[0]);
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found! Sample Command: \njava Linker input-1\n");
		}
	}
}
