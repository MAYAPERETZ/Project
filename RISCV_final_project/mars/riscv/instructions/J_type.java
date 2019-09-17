package mars.riscv.instructions;

/**
 * This class represents the J-type instructions of RISCV.
 * @author Maya Peretz
 * @version September 2019
 */
public class J_type extends BasicInstruction.WithImmediateField{
	
	private final int THIRTEENTH_TO_TWENTIETH_BITS =  0x000ff000;
	private final int TWELVETH_BIT = 0x00000800;
	private final int SECOND_TO_ELEVENTH_BITS = 0x000007fe;
	private final int TWENTY_FIRST_BIT = 0x00100000;

	/**
	 * {@inheritDoc}
	 */
	J_type(String example, String description, String operMask,
		   SimulationCode simCode) {
		super(example, description, operMask, simCode);
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public int computeOperands( Number [] operands) {
		return (getOpcode()|InstCodeUtil.computeRd((int)operands[0])|
				computeImmFromOperand(operands[1].intValue()));
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public Number [] returnOperands(int instructionCode) {
		Number [] operands = new Number[3];
		operands[0] = InstCodeUtil.getRd(instructionCode);
		operands[1] = computeImmFromInst(instructionCode);
		return operands;
	}

	/**
	 * {@inheritDoc}
	 */
	int computeImmFromInst(int instructionCode) {
		return ((instructionCode&THIRTEENTH_TO_TWENTIETH_BITS)|((instructionCode>>9)&TWELVETH_BIT)|
				((instructionCode>>20)&SECOND_TO_ELEVENTH_BITS)|((instructionCode>>11)&TWENTY_FIRST_BIT));
	}

	/**
	 * {@inheritDoc}
	 */
	int computeImmFromOperand(int operand) {
		return (operand&THIRTEENTH_TO_TWENTIETH_BITS)|((operand&TWELVETH_BIT)<<9)|
					((operand&SECOND_TO_ELEVENTH_BITS)<<20)|((operand&TWENTY_FIRST_BIT)<<11);
	}
}
