package mars.mips.instructions;

import java.util.Observable;
import java.util.Observer;
import java.util.function.BiFunction;

import mars.ProcessingException;
import mars.ProgramStatement;
import mars.mips.hardware.RV32IRegisters;
import mars.util.Binary;

public class R_type extends BasicInstruction{
	

	public R_type(String example, String description, String operMask,
			SimulationCode simCode) {
		super(example, description, operMask, simCode);
	}

	public R_type(String example, String description, String operMask, 
			BiFunction< Number,  Number, Number> x) {
		this(example, description, operMask, 
				new SimulationCode()
				{
					public void simulate(ProgramStatement statement) throws ProcessingException
					{
						 Number[] operands = statement.getOperands();
	                     Number product = x.apply(RV32IRegisters.getValue(operands[1]), RV32IRegisters.getValue(operands[2]));
	                     RV32IRegisters.updateRegister(operands[0].intValue(), product);
                       
					}
				});
	}

	@Override
	public int computeOperands(Number [] operands) {
		return (getOpcode()|InstCodeUtil.computeRd((int)operands[0])|InstCodeUtil.getFunct3(this)|
				InstCodeUtil.computeRs1((int)operands[1])|InstCodeUtil.computeRs2((int)operands[2])|getFunct7());
	}
	
	@Override
	public Number [] returnOperands(int instructionCode) {
		Number [] operands = new Number[3];
		operands[0] = InstCodeUtil.getRd(instructionCode);
		operands[1] = InstCodeUtil.getRs1(instructionCode);
		operands[2] = InstCodeUtil.getRs2(instructionCode);
		return operands;
	}
	
	private int getFunct7(){
		return InstCodeUtil.mask6 & (Binary.binaryStringToInt(this.getOperationMask().substring(0, 7))<<25);
	}
	
	
	public static class WithRmFeild extends R_type implements Observer{

		public static int rmMode = 0; // rm register default value
		
		public WithRmFeild(String example, String description, String operMask, SimulationCode simCode) {
			super(example, description, operMask, simCode);
		}

		@Override
		public void update(Observable o, Object arg) {
			rmMode = (int)arg;
		}
		
	}
}


   

