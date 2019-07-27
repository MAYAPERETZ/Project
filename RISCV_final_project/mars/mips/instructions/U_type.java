package mars.mips.instructions;

import mars.ProcessingException;
import mars.ProgramStatement;
import mars.mips.hardware.RV32IRegisters;

public class U_type extends BasicInstruction{
	
	public U_type(String example, String description, String operMask,
			SimulationCode simCode) {
		super(example, description, operMask, simCode);
	}
	
	public U_type(String example, String description, String operMask, Number y) {
		this(example, description, operMask, 
				new SimulationCode()
				{
					public void simulate(ProgramStatement statement) throws ProcessingException
					{
	                      Number[] operands = statement.getOperands();
	                      Number sum = GenMath.aui(y, operands[1]);
	                      RV32IRegisters.updateRegister(operands[0].intValue(), sum);
                       
					}
				});
	}
	
	
	@Override
	public int computeOperands(Number [] operands) {
		return (getOpcode()|InstCodeUtil.computeRd((int)operands[0])|computeImm(operands[1].intValue()));
	}
	
	
	@Override
	public Number [] returnOperands(int instructionCode) {
		Number[] operands = new Number[4];
		operands[0] = InstCodeUtil.getRd(instructionCode);
		operands[1] = getImm(instructionCode);
		return operands;
	}
		
	private int getImm(int args) {
		return ((args& (InstCodeUtil.mask3|
						InstCodeUtil.mask4|
						InstCodeUtil.mask5|
						InstCodeUtil.mask6))>>12);
	}
	private int computeImm(int args) {
		return (int)((args<<12)& (InstCodeUtil.mask3|
							  InstCodeUtil.mask4|
							  InstCodeUtil.mask5|
							  InstCodeUtil.mask6));
	}
}


   
