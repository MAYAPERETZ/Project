package mars.riscv.instructions;

import mars.Globals;
import mars.riscv.hardware.MemoryConfigurations;
import mars.riscv.hardware.RVIRegisters;
import mars.simulator.DelayedBranch;
import mars.util.GenMath;

import java.util.function.BiFunction;

/**
 * This class represents the B-type instructions of RISCV.
 * @author Maya Peretz
 * @version September 2019
 */
public class B_type extends BasicInstruction.WithImmediateField{
	
	private final int SECOND_TO_FIFTH_BITS =  0x0000001e;
	private final int TWELVETH_BIT = 0x00000800;
	private final int SIXTH_TO_ELEVENTH_BITS = 0x000007e0;
	private final int THIRTEENTH_BIT = 0x00001000;
	
	public B_type() {}
	
	private B_type(String example, String description, String operMask,
				   SimulationCode simCode) {
		super(example, description, operMask, simCode);
	}

	/**
	 * {@inheritDoc}
	 * @param x a function receiving two parameters of type {@code Number} which
	 *          returns a {@code Number} value.
	 */
	B_type(String example, String description, String operMask,
		   BiFunction<Number, Number, Number> x) {
		this(example, description, operMask,
				statement -> {
					 Number[] operands = statement.getOperands();

					 if (x.apply(RVIRegisters.getValue(operands[0])
						, RVIRegisters.getValue(operands[1])).intValue() == 1)
					 {
						if(MemoryConfigurations.getCurrentComputingArchitecture() == 32)
							processBranch(operands[2].intValue());
						else
							processBranch(operands[2].longValue());
					 }
				});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int computeOperands(Number [] operands) {
		return (getOpcode()|InstCodeUtil.getFunct3(this)|InstCodeUtil.computeRs1(operands[0].intValue())|
				InstCodeUtil.computeRs2(operands[1].intValue())| computeImmFromInst(operands[2].intValue()));
	}
	
    private static void processBranch(Number displacement) {
		assert displacement != null;
		displacement = displacement.longValue();
        if (Globals.getSettings().getDelayedBranchingEnabled()) {
           // Register the branch target address (absolute Number address).
        	
           DelayedBranch.register(GenMath.add(RVIRegisters.getProgramCounter(),
        		   GenMath.sll(displacement, 2)));
        } 
        else {
           // Decrement needed because PC has already been incremented
           RVIRegisters.setProgramCounter(
        		   GenMath.add(RVIRegisters.getProgramCounter(),
                		   GenMath.sll(displacement, 2))); // - Instruction.INSTRUCTION_LENGTH);	 
        } 
     }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Number [] returnOperands(int instructionCode) {
		Number [] operands = new Number[3];
		operands[0] = InstCodeUtil.getRs1(instructionCode);
		operands[1] = InstCodeUtil.getRs2(instructionCode);
		operands[2] = computeImmFromOperand(instructionCode);
		return operands;
	}

	/**
	 * {@inheritDoc}
	 */
	int computeImmFromInst(int instructionCode) {
	return (((instructionCode&SECOND_TO_FIFTH_BITS)<<7)|((instructionCode&TWELVETH_BIT)>>>4)|
					((instructionCode&SIXTH_TO_ELEVENTH_BITS)<<20)|((instructionCode&0x00001000)<<19));
	}

	/**
	 * {@inheritDoc}
	 */
	int computeImmFromOperand(int operand) {
		return (((operand>>>7)&SECOND_TO_FIFTH_BITS)|((operand<<4)&TWELVETH_BIT)|
				((operand>>>20)&SIXTH_TO_ELEVENTH_BITS)|((operand>>>19)&THIRTEENTH_BIT));
	}

	/**
	 * Represents a subclass of B_type where the function given to the constructor is the
	 * negate function of the {@link B_type#B_type(String, String, String, BiFunction)}
	 */
	static class NegB_type extends B_type {
		
		NegB_type(String example, String description, String operMask, BiFunction<Number, Number, Number> x) {
			super(example, description, operMask, x.andThen(e->GenMath.xor(e, 1)));
		}
		
	}
}


