package mars.riscv.instructions;

import mars.riscv.hardware.RVIRegisters;
import mars.util.GenMath;

/**
 * This class represents the U-type instructions of RISCV.
 * @author Maya Peretz
 * @version September 2019
 */
public class U_type extends BasicInstruction.WithImmediateField{

	/**
	 * {@inheritDoc}
	 */
	private U_type(String example, String description, String operMask,
				   SimulationCode simCode) {
		super(example, description, operMask, simCode);
	}
	
	U_type(String example, String description, String operMask, Number y) {
		this(example, description, operMask,
				statement -> {
					  Number[] operands = statement.getOperands();
					  Number sum = GenMath.aui(y, operands[1]);
					  RVIRegisters.updateRegister(operands[0].intValue(), sum);

				});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int computeOperands(Number [] operands) {
		return (getOpcode()|InstCodeUtil.computeRd((int)operands[0])| computeImmFromOperand(operands[1].intValue()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Number [] returnOperands(int instructionCode) {
		Number[] operands = new Number[4];
		operands[0] = InstCodeUtil.getRd(instructionCode);
		operands[1] = computeImmFromInst(instructionCode);
		return operands;
	}

	/**
	 * {@inheritDoc}
	 */
	int computeImmFromInst(int instructionCode) {
		return ((instructionCode& (InstCodeUtil.mask3|
						InstCodeUtil.mask4|
						InstCodeUtil.mask5|
						InstCodeUtil.mask6))>>12);
	}

	/**
	 * {@inheritDoc}
	 */
	int computeImmFromOperand(int operand) {
		return (operand<<12)& (InstCodeUtil.mask3|
							  InstCodeUtil.mask4|
							  InstCodeUtil.mask5|
							  InstCodeUtil.mask6);
	}
}


   
