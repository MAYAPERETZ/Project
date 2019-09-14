package mars.mips.instructions;
import mars.ProcessingException;
import mars.ProgramStatement;
import mars.mips.hardware.RVIRegisters;
import mars.simulator.Exceptions;

public class I_typeShift extends I_type{

	public I_typeShift(String example, String description, String operMask,
			SimulationCode simCode) {
		super(example, description, operMask, simCode);
	}

	public I_typeShift(String example, String description, String operMask, 
			Function< Number,  Number, Number> x) {
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
