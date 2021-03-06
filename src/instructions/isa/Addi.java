package instructions.isa;

import instructions.Instruction;
import tomasulo.RSType;
import utilities.Utilities;

public class Addi extends Instruction {

	public Addi(String[] params) {
		super(params, new String[] { "regA", "regB", "imm" }, "0001", RSType.ADD);
	}

	@Override
	public void execute() {
		mem.setRegisterValue(regA, mem.getRegisterValue(regB) + immValue);
	}
	
	public int compute(int valA, int valB) {
		return valA + valB;
	}

	@Override
	public String getMachineCode() {
		return getOpcode() + Utilities.getBinaryNumber(getRegANum(), 3)
				+ Utilities.getBinaryNumber(getRegBNum(), 3)
				+ Utilities.getBinaryNumber(getImmValue(), 7);
	}

}
