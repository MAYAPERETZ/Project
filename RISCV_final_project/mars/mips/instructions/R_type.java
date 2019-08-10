package mars.mips.instructions;

import java.util.Observable;
import java.util.Observer;
import java.util.function.BiFunction;
import java.util.function.Function;
import mars.mips.hardware.RV32IRegisters;
import mars.util.Binary;

public class R_type extends BasicInstruction{
	

	public R_type(String example, String description, String operMask,
			SimulationCode simCode) {
		super(example, description, operMask, simCode);
	}

	public R_type(String example, String description, String operMask,
				  BiFunction< Number,  Number, Number> x, Function<Number,Number> y,
				  Function<Number, Number> z, BiFunction<Number, Number, Number>w) {
		this(example, description, operMask,
				statement -> {
					 Number[] operands = statement.getOperands();
					 Number res = x.apply(y.apply(operands[1]), z.apply(operands[2]));
					//Number res = x.apply(RV32IRegisters.getValue(operands[1]), RV32IRegisters.getValue(operands[2]));
					 w.apply(operands[0].intValue(), res);

				});
	}

	@Override
	public int computeOperands(Number [] operands) {
		return (getOpcode()|InstCodeUtil.computeRd((int)operands[0])|InstCodeUtil.getFunct3(this)|
				InstCodeUtil.computeRs1((int)operands[1])|
				InstCodeUtil.computeRs2((operands[2] != null)? operands[2].intValue():0)
				| getFunct7());
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

	public static class RVI extends R_type{

		public RVI(String example, String description, String operMask,
				   SimulationCode simCode){
			super(example, description, operMask, simCode);
		}

		public RVI(String example, String description, String operMask, BiFunction<Number, Number, Number> x) {
			super(example, description, operMask, x, RV32IRegisters::getValue, RV32IRegisters::getValue,
					RV32IRegisters::updateRegister);
		}
	}
	
	
	public static class WithRmField extends R_type implements Observer{

		public static int rmMode = 0; // rm register default value

		public WithRmField(String example, String description, String operMask,
						   BiFunction< Number,  Number, Number> x, Function<Number, Number> y,
						   Function<Number, Number> z, BiFunction<Number, Number, Number>w) {
			super(example, description, operMask, x, y, z, w);
		}

		public WithRmField(String example, String description, String operMask, SimulationCode simCode){
			super(example, description, operMask, simCode);
		}


		@Override
		public void update(Observable o, Object arg) {
			rmMode = (int)arg;
		}
		
	}

	public static class WithTwoOperands extends R_type{

		public  WithTwoOperands(String example, String description, String operMask, SimulationCode simulationCode){
			super(example,description,operMask,simulationCode);
		}

		public WithTwoOperands(String example, String description, String operMask, Function<Number, Number> y,
							   Function< Number, Number> z, BiFunction<Number, Number, Number>w) {
			super(example, description, operMask,
					statement -> {
						Number[] operands = statement.getOperands();
						Number res = y.apply(z.apply(operands[1]));
						w.apply(operands[0].intValue(), res);

					});
		}
	}

	public static class WithTwoOperandsAndRmField extends WithTwoOperands implements Observer{
		public static int rmMode = 0; // rm register default value

		public WithTwoOperandsAndRmField(String example, String description, String operMask, Function<Number,
					Number> y, Function<Number, Number> z, BiFunction<Number, Number, Number>w) {
			super(example, description, operMask, y, z, w);
		}

		@Override
		public void update(Observable o, Object arg) {
			rmMode = (int)arg;
		}
	}



}


   

