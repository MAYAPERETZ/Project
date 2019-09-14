package mars.mips.hardware;

import mars.ProcessingException;
import mars.simulator.Exceptions;

public class FloatingPointException extends ProcessingException {

	public FloatingPointException(int cause) {
		Exceptions.setFCSRRegister(cause);
	}

}