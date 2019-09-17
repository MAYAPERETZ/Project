package mars.mips.instructions;

import java.util.function.Function;

import mars.Globals;
import mars.ProcessingException;
import mars.mips.hardware.AddressErrorException;
import mars.mips.hardware.RVIRegisters;
import mars.util.GenMath;

/**
 * This class represents the S-type instructions of RISCV.
 * @author Maya Peretz
 * @version September 2019
 */
public class S_type extends BasicInstruction.WithImmediateField{

	private final int FIRST_TO_FIFTH_BITS =  0x0000001f;
	private final int SIXTH_TO_THIRTEENTH_BITS = 0x00000fe0;

	/**
	 * {@inheritDoc}
	 */
	S_type(String example, String description, String operMask,
		   SimulationCode simCode) {
		super(example, description, operMask, simCode);
	}

	/**
	* {@inheritDoc}
	* @param x a function receiving two parameters of type {@code Number} which returns
	 *       a {@code Number} value.
	* @param mask a mask of a number
	*/
	S_type(String example, String description, String operMask,
		   NewFunction<Number, Number, Number> x, Number mask) {
		this(example, description, operMask,
				statement -> {
				   Number[] operands = statement.getOperands();
					if(Globals.debug) {
						System.out.println("operands[0]: " + operands[0]);
						System.out.println("operands[1]: " + operands[1]);
						System.out.println("operands[2]: " + operands[2]);
					}
				   try
				   {

					  if(mask != null)
								 x.compose(e-> GenMath.and(e, mask)).apply(GenMath.add(
								  RVIRegisters.getValue(operands[2]), operands[1]), RVIRegisters.getValue(operands[0]));
					  else
						  x.apply(GenMath.add(
										   RVIRegisters.getValue(operands[2]), operands[1])
							  , RVIRegisters.getValue(operands[0]));

				   }
				   catch (AddressErrorException e)
				   {
					   throw new ProcessingException(statement, e);
				   }
				});
	}

	/**
	* {@inheritDoc}
	* @param x function receiving two parameters of type Number which return a Number value.
	*/
	public S_type(String example, String description, String operMask, 
			NewFunction< Number, Number, Number> x) {
		this(example, description, operMask, x, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int computeOperands(Number [] operands) {
		return (getOpcode()| computeImmFromOperand(operands[1].intValue())|InstCodeUtil.getFunct3(this)|
				InstCodeUtil.computeRs1((int)operands[2])|InstCodeUtil.computeRs2(this, operands[0]));
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public Number [] returnOperands(int instructionCode) {
		Number [] operands = new Number[3];
		operands[0] = InstCodeUtil.getRs1(instructionCode);
		operands[1] = InstCodeUtil.getRs2(instructionCode);
		operands[2] = computeImmFromInst(instructionCode);
		return operands;
	}

	/**
	 * {@inheritDoc}
	 */
	int computeImmFromInst(int instructionCode) {
		return  (((instructionCode & FIRST_TO_FIFTH_BITS)<<7) |((instructionCode & SIXTH_TO_THIRTEENTH_BITS)<<15));
	}

	/**
	 * {@inheritDoc}
	 */
	int computeImmFromOperand(int operand) {
		return ((operand>>7) & FIRST_TO_FIFTH_BITS) |((operand>>15)& SIXTH_TO_THIRTEENTH_BITS);
	}
	
	@FunctionalInterface
	interface NewFunction<T extends Number, U extends Number, R extends Number>{
		R apply(T num1, U num2) throws AddressErrorException;
		default <V extends Number> NewFunction<V,U,R> compose(Function<? super V, ? extends T> before) {
			java.util.Objects.requireNonNull(before);
			return (V v, U u) -> apply(before.apply(v), u);
		}
	}
}
