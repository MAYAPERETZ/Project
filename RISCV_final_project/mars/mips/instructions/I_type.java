package mars.mips.instructions;

import mars.ProcessingException;
import mars.ProgramStatement;
import mars.mips.hardware.AddressErrorException;
import mars.mips.hardware.RV32IRegisters;

public class I_type extends BasicInstruction{

	public I_type(String example, String description, String operMask,
			SimulationCode simCode) {
		super(example, description, operMask, simCode);
	}

	public I_type(String example, String description, String operMask, 
			java.util.function.BiFunction< Number,  Number, Number> x) {
		this(example, description, operMask,
				statement -> {
					 Number[] operands = statement.getOperands();
					 Number product = x.apply(RV32IRegisters.getValue(operands[1]), operands[2]);
					 RV32IRegisters.updateRegister(operands[0].intValue(), product);

				});
	}
	
	@Override
	public int computeOperands(Number [] operands) {
		int rs1 = 1, imm = 2;
		if(this instanceof LW_type) {
			rs1 = 2;
			imm = 1;
		}
		return (getOpcode()|InstCodeUtil.computeRd((int)operands[0])|
				InstCodeUtil.getFunct3(this)|InstCodeUtil.computeRs1((int)operands[rs1])|computeImm(operands[imm].intValue()));
	}
	
	
	@Override
	public Number [] returnOperands(int instructionCode) {
		Number [] operands = new Number[4];
		operands[0] = InstCodeUtil.getRd(instructionCode);
		operands[1] = InstCodeUtil.getRs1(instructionCode);
		operands[2] = getImm(instructionCode);
		operands[3] = 0;
		return operands;
	}

	private int getImm(int args) {
		return ((args & (InstCodeUtil.mask5|InstCodeUtil.mask6))>>20);
	}
	private int computeImm(int args) {
		return (int)((args<<20)& (InstCodeUtil.mask5|InstCodeUtil.mask6));
	}
	
	public static class LW_type extends I_type{

		public LW_type(String example, String description, String operMask, SimulationCode simCode) {
			super(example, description, operMask, simCode);
		}

		public LW_type(String example, String description, String operMask, 
				Function< Number, Number> x, Number mask) {
			this(example, description, operMask, 
					 new SimulationCode()
            {
                public void simulate(ProgramStatement statement) throws ProcessingException
               {
                  Number[] operands = statement.getOperands();
                  try
                  {
                	 if(mask != null)
                		 RV32IRegisters.updateRegister(operands[0].intValue(),
                				 x.compose(e->GenMath.and(e, mask)).apply(GenMath.add(
                         		RV32IRegisters.getValue(operands[2]), operands[1])));
                	 else
                		 RV32IRegisters.updateRegister(operands[0].intValue(),
						          x.apply(GenMath.add(
						          		RV32IRegisters.getValue(operands[2]), operands[1])));
                  } 
                  catch (AddressErrorException e)
                  {
                      throw new ProcessingException(statement, e);
                  }
               }
            });
		}

		public LW_type(String example, String description, String operMask, 
				Function< Number, Number> x) {
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
}
