package assembler;

import gui.CacheHitWindow;
import gui.RegistersTable;
import instructions.Instruction;

import java.util.ArrayList;
import java.util.HashMap;

import memory.Memory;
import tomasulo.InstructionQueue;
import tomasulo.RSMaster;
import tomasulo.RSType;
import tomasulo.ReorderBuffer;
import utilities.CacheDetailsHolder;
import utilities.Pair;


public class Program {
	Memory memory;
	int startAddress;
	int endAddress;
	int numOfInstructions;
	int numOfCycles;

	public Program(String code, int startAddress, int MemAccessTime,
			CacheDetailsHolder[] cacheDetails,
			HashMap<Integer, Integer> editedAddress,
			HashMap<RSType, Integer> rStations,
			int ROBSize) {
		ArrayList<CacheDetailsHolder> caches = new ArrayList<>();
		for (int i = 0; i < cacheDetails.length; i++) {
			caches.add(cacheDetails[i]);
		}
		String[] lines = code.split("\n");
		Instruction[] instructions = Assembler.assembleProgram(lines);
		memory = Memory.getInstance();
		memory.initialize(caches, MemAccessTime, instructions, startAddress,
				editedAddress);
		this.startAddress = startAddress;
		this.endAddress = startAddress + (instructions.length - 1);
		this.numOfInstructions = 0;
		
		RSMaster.init(rStations);
		ReorderBuffer.init(ROBSize);
		
		this.run();
		this.afterRun();
	}
	
	public void run() {
		int val = 0;
		memory.setRegisterValue("PC", startAddress);
		
		do {
			numOfCycles++;
			// Main Tomasulo Loop
			int m = InstructionQueue.getPipelineWidth();
			val = memory.getRegisterValue("PC");
			// Prefetch m instructions
			for(int i = 0; i < m && !InstructionQueue.isFull() && val != endAddress; i++) {
				Instruction current = memory.getInstruction(val);
				memory.setRegisterValue("PC", val + 1);
				InstructionQueue.enqueue(current);
				// TODO: check this logic. I'm not sure what this counter should do in case pre-fetched instructions were cleared from the buffer due to branch misprediction.
				numOfInstructions++;
				val = memory.getRegisterValue("PC");
			}
			// Issue
			if(!InstructionQueue.isEmpty())
				InstructionQueue.issue();
			// Execute, Write
			if(!RSMaster.isEmpty())
				RSMaster.stepForth();
			// Commit
			// TODO: decide where to place commit logic
		} while (val != endAddress || !InstructionQueue.isEmpty());
	}

	public void afterRun() {
		RegistersTable.updateRegisters();
		double amat = memory.getAMAT();
		ArrayList<Pair<Integer, Integer>> hitRatios = memory.getCacheStats();
		new CacheHitWindow(hitRatios, amat, numOfInstructions);
	}
}
