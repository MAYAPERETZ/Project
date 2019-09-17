package mars.mips.hardware;

import mars.ProcessingException;
import mars.simulator.Exceptions;

/**
 * Represents RISCV FloatingPointException. This is generated by the assembler
 * when a floating point exception occurs.
 *
 * @author Maya Peretz
 * @version September 2019
 */
public class FloatingPointException extends ProcessingException {

	public FloatingPointException(int cause) {
		Exceptions.setFCSRRegister(cause);
	}
}