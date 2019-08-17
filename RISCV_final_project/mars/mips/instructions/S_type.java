package mars.mips.instructions;

import java.util.function.Function;

import mars.Globals;
import mars.ProcessingException;
import mars.ProgramStatement;
import mars.mips.hardware.AddressErrorException;
import mars.mips.hardware.RV32IRegisters;
import mars.util.Binary;

public class S_type extends BasicInstruction{

	private final int FIRST_TO_FIFTH_BITS =  0x0000001f;
	private final int SIXTH_TO_THIRTEENTH_BITS = 0x00000fe0;
	
	public S_type(String example, String description, String operMask,
			SimulationCode simCode) {
		super(example, description, operMask, simCode);
	}

	public S_type(String example, String description, String operMask, 
			NewFunction< Number, Number, Number> x, Number mask) {
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
								 x.compose(e->GenMath.and(e, mask)).apply(GenMath.add(
								  RV32IRegisters.getValue(operands[2]), operands[1]), RV32IRegisters.getValue(operands[0]));
					  else
						  x.apply(GenMath.add(
										   RV32IRegisters.getValue(operands[2]), operands[1])
							  ,RV32IRegisters.getValue(operands[0]));

				   }
				   catch (AddressErrorException e)
				   {
					   throw new ProcessingException(statement, e);
				   }
				});
	}

	public S_type(String example, String description, String operMask, 
			NewFunction< Number, Number, Number> x) {
		this(example, description, operMask, x, null);
	}
		
	@Override
	public int computeOperands(Number [] operands) {
		return (getOpcode()|computeImm(operands[1].intValue())|InstCodeUtil.getFunct3(this)|
				InstCodeUtil.computeRs1((int)operands[2])|InstCodeUtil.computeRs2(this, operands[0]));
	}
	
	
	@Override
	public Number [] returnOperands(int instructionCode) {
		Number [] operands = new Number[3];
		operands[0] = InstCodeUtil.getRs1(instructionCode);
		operands[1] = InstCodeUtil.getRs2(instructionCode);
		operands[2] = getImm(instructionCode);
		return operands;
	}
	 
	private int getImm(int args) {
		return  (((args & FIRST_TO_FIFTH_BITS)<<7) |((args & SIXTH_TO_THIRTEENTH_BITS)<<15));
	}

	private int computeImm(int args) {
		return  (int)(((args>>7) & FIRST_TO_FIFTH_BITS) |((args>>15)& SIXTH_TO_THIRTEENTH_BITS));

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
