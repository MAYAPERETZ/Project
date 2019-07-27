package mars.mips.instructions;

import mars.ProcessingException;
import mars.ProgramStatement;
import mars.mips.hardware.RV32IRegisters;

public class R_M_DIV extends R_M {

	public R_M_DIV(String example, String description, String funct3, SimulationCode simCode) {
		super(example, description, funct3, simCode);
	}
	
	public R_M_DIV(String example, String description, String funct3, 
			Function< Number,  Number, Number> x) {
		this(example, description, funct3,
				new SimulationCode()
				{
					public void simulate(ProgramStatement statement) throws ProcessingException
					{
						Number[] operands = statement.getOperands();
						if (RV32IRegisters.getValue(operands[2]) == (Number)0)
						{
							// Note: no exceptions, and undefined results for zero divide
							return;
						}
						Number qu = x.apply(RV32IRegisters.getValue(operands[1]), RV32IRegisters.getValue(operands[2]));

						RV32IRegisters.updateRegister(operands[0].intValue(),qu);	
                       
					}
				});
	}

	@FunctionalInterface
	interface Function<T extends Number, R extends Number, K extends Number> {
	    public K apply(T t, R r);
	}
}
