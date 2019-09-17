package mars.riscv.instructions;

import mars.ProcessingException;
import mars.riscv.hardware.AddressErrorException;
import mars.riscv.hardware.RVIRegisters;
import mars.simulator.Exceptions;
import mars.util.GenMath;

/**
 * This class represents the I-type instructions of RISCV.
 * @author Maya Peretz
 * @version September 2019
 */
public class I_type extends BasicInstruction.WithImmediateField{

	/**
	 *	{@inheritDoc}
	 */
	I_type(String example, String description, String operMask,
		   SimulationCode simCode) {
		super(example, description, operMask, simCode);
	}

	/**
	 * {@inheritDoc}
	 * @param x a function receiving two parameters of type {@code Number} which
	 *          returns a {@code Number} value.
	 */
	I_type(String example, String description, String operMask,
		   java.util.function.BiFunction<Number, Number, Number> x) {
		this(example, description, operMask,
				statement -> {
					 Number[] operands = statement.getOperands();
					 Number product = x.apply(RVIRegisters.getValue(operands[1]), operands[2]);
					 RVIRegisters.updateRegister(operands[0].intValue(), product);

				});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int computeOperands(Number [] operands) {
		int rs1 = 1, imm = 2;
		if(this instanceof LW_type) {
			rs1 = 2;
			imm = 1;
		}
		return (getOpcode()|InstCodeUtil.computeRd((int)operands[0])|
				InstCodeUtil.getFunct3(this)|InstCodeUtil.computeRs1((int)operands[rs1])| computeImmFromOperand(operands[imm].intValue()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Number [] returnOperands(int instructionCode) {
		Number [] operands = new Number[4];
		operands[0] = InstCodeUtil.getRd(instructionCode);
		operands[1] = InstCodeUtil.getRs1(instructionCode);
		operands[2] = computeImmFromInst(instructionCode);
		operands[3] = 0;
		return operands;
	}

	/**
	 * {@inheritDoc}
	 */
	int computeImmFromInst(int instructionCode) {
		return ((instructionCode & (InstCodeUtil.mask5|InstCodeUtil.mask6))>>20);
	}

	/**
	 * {@inheritDoc}
	 */
	int computeImmFromOperand(int operand) {
		return (operand<<20)& (InstCodeUtil.mask5|InstCodeUtil.mask6);
	}

	/**
	 * This class represents the loading instructions (like lw).
	 * In loading instructions, the order of the operands in the instruction is not as in regular
	 * I-type instruction; so this class was created to overcome this issue.
	 */
	static class LW_type extends I_type{

		/**
		 * {@inheritDoc}
		 */
		LW_type(String example, String description, String operMask, SimulationCode simCode) {
			super(example, description, operMask, simCode);
		}

		/**
		 * {@inheritDoc}
		 * @param mask a mask of a number
		 */
		LW_type(String example, String description, String operMask,
				Function<Number, Number> x, Number mask) {
			this(example, description, operMask,
					statement -> {
					   Number[] operands = statement.getOperands();
					   try
					   {
						  if(mask != null)
							  RVIRegisters.updateRegister(operands[0].intValue(),
									  x.compose(e-> GenMath.and(e, mask)).apply(GenMath.add(
									  RVIRegisters.getValue(operands[2]), operands[1])));
						  else
							  RVIRegisters.updateRegister(operands[0].intValue(),
									   x.apply(GenMath.add(
											   RVIRegisters.getValue(operands[2]), operands[1])));
					   }
					   catch (AddressErrorException e)
					   {
						   throw new ProcessingException(statement, e);
					   }
					});
		}

		/**
		 * {@inheritDoc}
		 */
		LW_type(String example, String description, String operMask,
				Function<Number, Number> x) {
			this(example, description, operMask, x, null);
		}

	}

	@FunctionalInterface
	interface Function<T extends Number, R extends Number>{
		R apply(T t) throws AddressErrorException;
		default <V extends Number> Function<V, R> compose(Function<? super V, ? extends T> before) {
			java.util.Objects.requireNonNull(before);
			return (V v) -> apply(before.apply(v));
		}
	}

	/**
	 * Represents the shift instructions of I-type
	 */
	static class I_typeShift extends I_type{

		/**
		 * {@inheritDoc}
		 */
		private I_typeShift(String example, String description, String operMask,
							SimulationCode simCode) {
			super(example, description, operMask, simCode);
		}

		/**
		 * {@inheritDoc}
		 * @param x a function receiving two parameters of type {@code Number} which return a
		 * 			{@code Number} value.
		 */
		I_typeShift(String example, String description, String operMask,
					Function<Number, Number, Number> x) {
			this(example, description, operMask,
					statement -> {
						Number[] operands = statement.getOperands();
						Number y = x.compose(GenMath::shiftImm).apply(RVIRegisters.getValue(operands[1]), operands[2]);
						if(y != null)
							RVIRegisters.updateRegister(operands[0].intValue(), y);
						else throw new ProcessingException(statement, "Illegal Instruction: shmat[5] = 1)"
								, Exceptions.ILLEGAL_INSTRUCTION_EXCEPTION);

					});
		}

		@FunctionalInterface
		interface Function<A, B, C> {
			C apply(A one, B b);
			default <D extends Number> Function<D, B, C> compose(Function<? super D, ? super B, ? extends A> before){
				return (D d, B b) -> apply(before.apply(d, b ), b);
			}
		}


	}
}
