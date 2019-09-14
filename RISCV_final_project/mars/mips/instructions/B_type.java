package mars.mips.instructions;

import java.util.function.BiFunction;
import mars.Globals;
import mars.ProcessingException;
import mars.ProgramStatement;
import mars.mips.hardware.MemoryConfigurations;
import mars.mips.hardware.RVIRegisters;
import mars.simulator.DelayedBranch;

public class B_type extends BasicInstruction{
	
	private final int SECOND_TO_FIFTH_BITS =  0x0000001e;
	private final int TWELVETH_BIT = 0x00000800;
	private final int SIXTH_TO_ELEVENTH_BITS = 0x000007e0;
	private final int THIRTEENTH_BIT = 0x00001000;
	
	public B_type() {}
	
	public B_type(String example, String description, String operMask,
			SimulationCode simCode) {
		super(example, description, operMask, simCode);
	}
	
	public B_type(String example, String description, String operMask, 
			BiFunction< Number,  Number, Number> x) {
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
	
	
	@Override
	public int computeOperands(Number [] operands) {
		return (getOpcode()|InstCodeUtil.getFunct3(this)|InstCodeUtil.computeRs1(operands[0].intValue())|
				InstCodeUtil.computeRs2(operands[1].intValue())|computeImm(operands[2].intValue()));
	}
	
    private static void processBranch(Number displacement) {
    	displacement = (displacement instanceof Number) ? displacement.longValue() : displacement.intValue();
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

	@Override
	public Number [] returnOperands(int instructionCode) {
		Number [] operands = new Number[3];
		operands[0] = InstCodeUtil.getRs1(instructionCode);
		operands[1] = InstCodeUtil.getRs2(instructionCode);
		operands[2] = getImm(instructionCode);
		return operands;
	}

	
	private int computeImm(long args) {
	return (int) (((args&SECOND_TO_FIFTH_BITS)<<7)|((args&TWELVETH_BIT)>>>4)|
					((args&SIXTH_TO_ELEVENTH_BITS)<<20)|((args&0x00001000)<<19));
	}
	
	private int getImm(int args) {
		return (((args>>>7)&SECOND_TO_FIFTH_BITS)|((args<<4)&TWELVETH_BIT)|
				((args>>>20)&SIXTH_TO_ELEVENTH_BITS)|((args>>>19)&THIRTEENTH_BIT));
	}
	

	public static class NegB_type extends B_type {
		
		public NegB_type(String example, String description, String operMask, BiFunction<Number, Number, Number> x) {
			super(example, description, operMask, x.andThen(e->GenMath.xor(e, 1)));
		}
		
	}
}


