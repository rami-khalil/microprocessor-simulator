package instructions.isa;

import tomasulo.RSType;
import utilities.Utilities;
import instructions.Instruction;

public class Sub extends Instruction {

	public Sub(String[] params) {
		super(params, new String[]{"regA", "regB", "regC"}, "1011", RSType.ADD);
	}

	@Override
	public void execute() {
		mem.setRegisterValue(regA, mem.getRegisterValue(regB) - mem.getRegisterValue(regC));
	}
	
	public int compute(int valA, int valB) {
		return valA - valB;
	}

	@Override
	public String getMachineCode() {
		return getOpcode() + Utilities.getBinaryNumber(getRegANum(), 3)
				+ Utilities.getBinaryNumber(getRegBNum(), 3) + "0000"
				+ Utilities.getBinaryNumber(getRegCNum(), 3);
	}

}
