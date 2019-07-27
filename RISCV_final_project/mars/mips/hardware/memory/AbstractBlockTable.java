package mars.mips.hardware.memory;
import mars.util.Math2;

abstract class AbstractBlockTable {

	protected Number baseAddress, limitAddress;
	protected static final int BLOCK_LENGTH_WORDS = 1024;  // allocated blocksize 1024 ints == 4K Numbers
	
  /**
    * Handy little utility to find out if given address is in a specified segment area 
    * starts at baseAddress, range baseAddress to limitAddress.
    * @param  memory address
    * @return true if that address is within MARS-defined memory map (MMIO) area,
    *  false otherwise.
   */
	public boolean inSegment(Number address) {
		return !Math2.isLt(address, baseAddress) && Math2.isLt(address, limitAddress);
	}
	
	void setBaseAddress(Number getCurrentConfBase) {
		limitAddress = getCurrentConfBase;
	}

	abstract void setLimitAddress(Number getCurrentConfLimit);
	
	public Number getLimitAddress() {
		return limitAddress;
	}
	
	public Number getBaseAddress() {
		return baseAddress;
	}
	


}
