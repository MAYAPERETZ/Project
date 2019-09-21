package mars.riscv.instructions;

import java.util.Observable;
import java.util.Observer;
import java.util.function.BiFunction;
import java.util.function.Function;
import mars.riscv.hardware.RVIRegisters;
import mars.util.Binary;

/**
 * This class represents the R-type instructions of RISCV.
 * @author Maya Peretz
 * @version September 2019
 */
public class R_type extends BasicInstruction{

	/**
	* {@inheritDoc}
	*/
	R_type(String example, String description, String operMask,
		   SimulationCode simCode) {
		super(example, description, operMask, simCode);
	}

	/**
	 * {@inheritDoc}
	 * @param x a function receiving two parameters of type {@code Number} which returns a
	 * 			{@code Number} value.
	 * @param y a function receiving a single parameter of type {@code Number} which
	 *          returns a {@code Number} value.
	 * @param z	a function receiving a single parameter of type {@code Number}
	 *          which returns a {@code Number} value.
	 * @param w	a function receiving two parameters of type {@code Number} which
	 *          returns a {@code Number} value.
	 */
	R_type(String example, String description, String operMask,
		   BiFunction<Number, Number, Number> x, Function<Number, Number> y,
		   Function<Number, Number> z, BiFunction<Number, Number, Number> w) {
		this(example, description, operMask,
				statement -> {
					 Number[] operands = statement.getOperands();
					 Number res;
					 if (x != null)
					 	res = x.apply(y.apply(operands[1]), z.apply(operands[2]));
					 else
					 	res = y.apply(z.apply(operands[1]));
					 w.apply(operands[0].intValue(), res);

				});
	}

	R_type(String example, String description, String operMask,
		   Function<Number, Number> y,
		   Function<Number, Number> z, BiFunction<Number, Number, Number> w) {
		this(example, description, operMask, null, y, z, w);
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public int computeOperands(Number [] operands) {
		return (getOpcode()|InstCodeUtil.computeRd((int)operands[0])|InstCodeUtil.getFunct3(this)|
				InstCodeUtil.computeRs1((int)operands[1])|
				InstCodeUtil.computeRs2(this, operands[2])
				| getFunct7());
	}

	/**
	* {@inheritDoc}
	*/
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

	/**
	 * This class represents the RVI instructions
	 */
	static class RVI extends R_type{

		RVI(String example, String description, String operMask,
			SimulationCode simCode){
			super(example, description, operMask, simCode);
		}

		RVI(String example, String description, String operMask, BiFunction<Number, Number, Number> x) {
			super(example, description, operMask, x, RVIRegisters::getValue, RVIRegisters::getValue,
					RVIRegisters::updateRegister);
		}
	}

	/**
	 * This class represents the R-type instructions of RISCV with rm field
	 */
	public static class WithRmField extends R_type implements Observer{

		public static int rmMode = 0; // rm register default value

		/**
		 * {@inheritDoc}
		 */
		public WithRmField(String example, String description, String operMask,
						   BiFunction< Number,  Number, Number> x, Function<Number, Number> y,
						   Function<Number, Number> z, BiFunction<Number, Number, Number>w) {
			super(example, description, operMask, x, y, z, w);
		}

		/**
		 * {@inheritDoc}
		 */
		WithRmField(String example, String description, String operMask, SimulationCode simCode){
			super(example, description, operMask, simCode);
		}

		@Override
		public void update(Observable o, Object arg) {
			rmMode = (int)arg;
		}
		
	}

	/**
	 * This class represents the RVM instructions of RISCV
	 */
	static class RVM extends R_type.RVI{

		/**
		 * {@inheritDoc}
		 * @param funct3 the funct3 field value
		 */
		RVM(String example, String description, String funct3,
			SimulationCode simCode) {
			super(example, description, "0000001tttttsssss"+ funct3+"fffff0110011", simCode);
		}

		/**
		 * {@inheritDoc}
		 * @param funct3 the funct3 field value
		 */
		RVM(String example, String description, String funct3,
			BiFunction<Number, Number, Number> x) {
			super(example, description, "0000001tttttsssss"+ funct3+"fffff0110011", x);
		}

	}

	/**
	* This class represents the division-related instructions of RISCV
	*/
	static class Div extends RVM {
		Div(String example, String description, String funct3,
			Function<Number, Number, Number> x) {
			super(example, description, funct3,
					statement -> {
						Number[] operands = statement.getOperands();
						if (RVIRegisters.getValue(operands[2]).equals((Number) 0)) {
							// Note: no exceptions, and undefined results for zero divide
							return;
						}
						Number qu = x.apply(RVIRegisters.getValue(operands[1]), RVIRegisters.getValue(operands[2]));

						RVIRegisters.updateRegister(operands[0].intValue(), qu);

					});
		}

		@FunctionalInterface
		interface Function<T extends Number, R extends Number, K extends Number> {
			K apply(T t, R r);
		}
	}
}


   

