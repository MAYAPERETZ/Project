package mars.mips.hardware;

import mars.ProcessingException;
import mars.ProgramStatement;
import mars.simulator.Exceptions;

public class FloatingPointException extends ProcessingException {  

	   public FloatingPointException(ProgramStatement ps, String m) {
		   super(ps, m);
	   }

	   public FloatingPointException(ProgramStatement ps, String m, int cause) {
		  this(ps, m);
		  Exceptions.setFCSRRegister(cause);
	   }

}