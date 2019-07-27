package mars.mips.instructions;

public class J_type extends BasicInstruction{
	
	private final int THIRTEENTH_TO_TWENTIETH_BITS =  0x000ff000;
	private final int TWELVETH_BIT = 0x00000800;
	private final int SECOND_TO_ELEVENTH_BITS = 0x000007fe;
	private final int TWENTY_FIRST_BIT = 0x00100000;
	
	public J_type(String example, String description, String operMask,
			SimulationCode simCode) {
		super(example, description, operMask, simCode);
	}
	
	@Override
	public int computeOperands( Number [] operands) {
		return (getOpcode()|InstCodeUtil.computeRd((int)operands[0])|computeImm(operands[1].intValue()));
	}
	
/*	@Override
	public long [] returnOperands(int instructionCode) {
		long [] operands = new long[3];
		operands[0] = InstCodeUtil.getRd(instructionCode);
		operands[1] = getImm(instructionCode);
		return operands;
	}
*/	
	
	@Override
	public Number [] returnOperands(int instructionCode) {
		Number [] operands = new Number[3];
		operands[0] = InstCodeUtil.getRd(instructionCode);
		operands[1] = getImm(instructionCode);
		return operands;
	}
	
	private int getImm(int args) {
		return ((args&THIRTEENTH_TO_TWENTIETH_BITS)|((args>>9)&TWELVETH_BIT)|
				((args>>20)&SECOND_TO_ELEVENTH_BITS)|((args>>11)&TWENTY_FIRST_BIT));
	}
	private int computeImm(long args) {
		return (int)((args&THIRTEENTH_TO_TWENTIETH_BITS)|((args&TWELVETH_BIT)<<9)|
					((args&SECOND_TO_ELEVENTH_BITS)<<20)|((args&TWENTY_FIRST_BIT)<<11));
	}
}
