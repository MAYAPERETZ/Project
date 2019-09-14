package mars.mips.hardware.memory;

import mars.mips.hardware.MemoryConfigurations;
import mars.util.Math2;

import static mars.mips.instructions.GenMath.add;


/**
 * Represents an abstract block table object in memory.
 * @author Maya Peretz
 * @version September 2019
 */
abstract class AbstractBlockTable<T> {

	protected Number baseAddress, limitAddress, baseSegmentAddress;
	protected static final int BLOCK_LENGTH_ADDRESS = 1024;  // allocated blocksize 1024 ints == 4K Numbers
	protected T [][] blockTable;
	final Number defaultLimitAddress;

	public AbstractBlockTable(int tableLen , Number baseAddress) {
		if(MemoryConfigurations.getCurrentComputingArchitecture() == 64)
			tableLen *= 2;
		this.baseAddress = baseAddress;
		limitAddress = add(baseAddress, BLOCK_LENGTH_ADDRESS *tableLen* Memory.WORD_LENGTH_BYTES);
		defaultLimitAddress = limitAddress;
	}

	/**
	 * Handy little utility to find out if given address is in a specified segment area
	 * starts at baseAddress, range baseAddress to limitAddress.
	 * @param address the address to be checked whether it is in the specified segment area
	 * @return true if that address is within RISCV-defined memory map (MMIO) area, false otherwise.
	 */
	public boolean inSegment(Number address) {
		return !Math2.isLt(address, baseAddress) && Math2.isLt(address, limitAddress);
	}

	/**
	 * sets the base address of the block table
	 * @param baseAddress the base address of the block table
	 */
	void setBaseAddress(Number baseAddress) {
		this.baseAddress = baseAddress;
	}

	/**
	 * sets the limit address of the block table
	 * @param limitAddress the limit address of the block table
	 */
	abstract void setLimitAddress(Number limitAddress);

	/**
	 * gets the limit address of the block table
	 * @return the limit address of the block table
	 */
	public Number getLimitAddress() {
		if(MemoryConfigurations.getCurrentComputingArchitecture() == 32)
			return limitAddress.intValue();
		return limitAddress.longValue();
	}

	/**
	 * gets the base address of the block table
	 * @return the base address of the block table
	 */
	public Number getBaseAddress() {
		if(MemoryConfigurations.getCurrentComputingArchitecture() == 32)
			return baseAddress.intValue();
		return baseAddress.longValue();
	}

}
