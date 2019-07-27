package mars.mips.hardware.memory;
import static mars.util.Math2.isLt;
import static mars.mips.instructions.GenMath.max;

class StackBlockTable extends BlockTable{

	StackBlockTable(int tableLen, Number baseAddress) {
		super(tableLen, baseAddress);
	}

	@Override
	public boolean inSegment(Number address) {
		return  isLt(limitAddress, address) && !isLt(baseAddress, address);
	}

	@Override
	void setBaseAddress(Number getCurrentConfLimit) {
		limitAddress = max(getCurrentConfLimit, defaultLimitAddress);
	}
}
