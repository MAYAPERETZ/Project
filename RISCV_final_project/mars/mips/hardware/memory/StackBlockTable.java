package mars.mips.hardware.memory;
import static mars.util.Math2.isLt;
import static mars.mips.instructions.GenMath.max;

/**
 * Represents the stack block table in memory.
 * @author Maya Peretz
 * @version September 2019
 */

class StackBlockTable extends BlockTable{

	StackBlockTable(int tableLen, Number baseAddress) {
		super(tableLen, baseAddress);
	}

	/***
	* {@inheritDoc}
	*/
	@Override
	public boolean inSegment(Number address) {
		return  isLt(limitAddress, address) && !isLt(baseAddress, address);
	}

	/***
	* {@inheritDoc}
	*/
	@Override
	void setBaseAddress(Number getCurrentConfLimit) {
		limitAddress = max(getCurrentConfLimit, defaultLimitAddress);
	}
}
