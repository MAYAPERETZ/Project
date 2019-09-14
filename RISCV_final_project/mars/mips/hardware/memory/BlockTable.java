package mars.mips.hardware.memory;

import static mars.util.Math2.*;
import mars.Globals;
import mars.mips.hardware.MemoryConfigurations;
import static mars.mips.instructions.GenMath.*;

/**
 * Represents a block table object in memory.
 * @author Maya Peretz
 * @version September 2019
 */

public class BlockTable extends AbstractBlockTable<Number>{

	private Number [][] blockTable;

	public enum op{
		STORE,
		FETCH
	}

	BlockTable(int tableLen , Number baseAddress, Number baseSegmentAddress) {
		super(tableLen, baseAddress);
		blockTable = new Number [Math.abs(tableLen)][];
		this.baseSegmentAddress = baseSegmentAddress;
	}

	BlockTable(int tableLen , Number baseAddress) {
		this(tableLen, baseAddress, baseAddress);
	}
	
	// Set "top" address boundary to go with each "base" address.  This determines permissable
    // address range for user program.  Currently limit is 4MB, or 1024 * 1024 * 4 bytes
	// (8MB, or 1024 * 1024 * 4 * 2 bytes for 64 bit architecture))
	// (except memory mapped IO, limited to 64KB by range).
	void setLimitAddress(Number limitAddress) {
		this.limitAddress = min(limitAddress, defaultLimitAddress);
	}
	
	void reset() {
		int blockTableSize = blockTable.length;
		blockTable = new Number [blockTableSize][];
	}

	/*
	* The helper's helper.  Works for either storing or fetching, little or big endian.
	* When storing/fetching bytes, most of the work is calculating the correct array element(s)
	* and element byte(s).  This method performs either store or fetch, as directed by its
	* client using STORE or FETCH in last arg.
	* Modified 29 Dec 2005 to return old value of replaced bytes, for STORE.
	* Modified 14 Sep 2019 to generic function in order to support 64 bit architecture as well
	* @author Pete Sanderson
	*/
	 synchronized Number storeOrFetchNumbersInTable(Number address, int length,
			  Number value, boolean isLittleEndian, op operation) {
		 
		
		Number relativeNumberAddress = (this instanceof StackBlockTable) ? sub(baseSegmentAddress, address) :
				sub(address, baseSegmentAddress);
		int block, offset, numOfBytes = MemoryConfigurations.getCurrentComputingArchitecture()/8;
		Number NumberPositionInMemory, 
			numberPositionInValue;
		Number oldValue = 0, relativeWordAddress; // for STORE, return old values of replaced Numbers
		int loopStopper = numOfBytes-1-length;
		// IF added DPS 22-Dec-2008. NOTE: has NOT been tested with Big-Endian.
		// Fix provided by Saul Spatz; comments that follow are his.
		// If address in stack segment is 4k + m, with 0 < m < 4, then the
		// relativeNumberAddress we want is stackBaseAddress - 4k + m, but the
		// address actually passed in is stackBaseAddress - (4k + m), so we
		// need to add 2m.  Because of the change in sign, we get the
		// expression 4-delta below in place of m.
		if (this instanceof StackBlockTable) {
			Number delta = rem(relativeNumberAddress, numOfBytes);
			if (!isEqz(delta)) 
				relativeNumberAddress = add(
				relativeNumberAddress, sll(sub(numOfBytes, delta), 1));
			
		}
		if(Globals.debug) {
			System.out.println("-------------------------------------------------------------------------------------");
			System.out.println("relativeNumberAddress: " + relativeNumberAddress);
			System.out.println("address: " +address);
		}
		
		for (numberPositionInValue = (numOfBytes-1); isLt(loopStopper, numberPositionInValue);
			 numberPositionInValue = sub(numberPositionInValue, 1)) {
			NumberPositionInMemory = rem(relativeNumberAddress, numOfBytes);
			relativeWordAddress = sra(relativeNumberAddress, 2);
			block = div(relativeWordAddress, BLOCK_LENGTH_ADDRESS).intValue();  // Block Number
			offset = rem(relativeWordAddress, BLOCK_LENGTH_ADDRESS).intValue(); // Word within that block
			if(Globals.debug) {
				System.out.println("numberPositionInValue: " + numberPositionInValue);
				System.out.println("relativeWordAddress: " + relativeWordAddress);
				System.out.println("address: " +address);
				System.out.println("block: " + block);
				System.out.println("offset: " +offset);
			}
			if (blockTable[block] == null) {
				if (operation == op.STORE) 
					blockTable[block] = new Number[BLOCK_LENGTH_ADDRESS];
				else return 0;
			}
			if (isLittleEndian) NumberPositionInMemory = sub(numOfBytes-1, NumberPositionInMemory);
			if (operation == op.STORE) {
				if(blockTable[block][offset] == null) blockTable[block][offset] = 0;
				oldValue = replaceByte(blockTable[block][offset], NumberPositionInMemory,
											oldValue, numberPositionInValue, numOfBytes);
				blockTable[block][offset] = replaceByte(value, numberPositionInValue,
				                  blockTable[block][offset], NumberPositionInMemory, numOfBytes*8-8);
			} 
			else { // op == FETCH
				if(blockTable[block][offset] == null) blockTable[block][offset] = 0;
				value = replaceByte(blockTable[block][offset], NumberPositionInMemory,
			                                   value, numberPositionInValue, numOfBytes*8-8);
			}
			relativeNumberAddress = add(relativeNumberAddress, 1);
		}
		return (operation == op.STORE)? oldValue : value;
		}
	/*
	 * Helper method to store 4 byte value in table that represents MIPS memory.
	 * Originally used just for data segment, but now also used for stack.
	 * Both use different tables but same storage method and same table size
	 * and block size.  Assumes address is word aligned, no endian processing.
	 * Modified 29 Dec 2005 to return overwritten value.
	 * Modified 14 Sep 2019 to generic function in order to support 64 bit architecture as well
	 * @author Pete Sanderson
	 */
	synchronized Number storeWordInTable(Number address, Number value, final int shift) {
		Number oldValue;
 		int block, offset;
 		
 		// Relative to segment start, in Numbers.
 		// In stack,relative Number address is calculated "backward" because stack addresses grow down from base.
 		Number relative = (this instanceof StackBlockTable) ? sub(baseAddress, address) : 
			sub(address, baseAddress); 
 		relative = sra(relative, shift);
 		
 		block = (int)div(relative, BLOCK_LENGTH_ADDRESS);
 		offset = (int)rem(relative, BLOCK_LENGTH_ADDRESS);
 		if (blockTable[block] == null) 
 			// First time writing to this block, so allocate the space.
 			blockTable[block] = new Number[BLOCK_LENGTH_ADDRESS];

 		
 		oldValue = blockTable[block][offset];
 		blockTable[block][offset] = value;
 		return oldValue;
	}

	/*
	 * Helper method to fetch 4 byte value from table that represents RISCV memory.
	 * Originally used just for data segment, but now also used for stack.
	 * Both use different tables but same storage method and same table size
	 * and block size.  Assumes word alignment, no endian processing.
	 */
	synchronized Number fetchWordFromTable(Number address, boolean getOrNull, final int shift) {
		Number relative = (this instanceof StackBlockTable) ? sub(baseAddress, address) : 
			sub(address, baseAddress);
 		relative = srl(relative, shift);
		Number value = (getOrNull) ? null : 0;
        int block, offset;
        block = rem(relative, BLOCK_LENGTH_ADDRESS).intValue();
        offset = div(relative, BLOCK_LENGTH_ADDRESS).intValue();
        if (blockTable[block] != null) { 
			if(blockTable[block][offset] == null) blockTable[block][offset] = 0;
           value = blockTable[block][offset];
        }
        return value;
    }     
	/*
	 * Returns result of substituting specified byte of source value into specified byte
	 * of destination value. Byte positions are 0-1-2-3, listed from most to least
	 * significant.  No endian issues.  This is a private helper method used by get() & set().
	 */
	private synchronized Number replaceByte(Number sourceValue, Number NumberPosInSource,
											Number destValue, Number NumberPosInDest, int restOfTheBytes) {

		return
            // Set source Number value into destination Number position; set other 24 bits to 0's...
             or(
            		 sll(srl(sourceValue, and((sub(restOfTheBytes, sll(NumberPosInSource, 3))), 0xFF))
                                             ,sub(restOfTheBytes, sll(NumberPosInDest, 3))),
               // and bitwise-OR it with...
              // Set 3 bits in destination Number position to 0's, the rest of the bits remain unchanged.
            		 and(destValue, neg(sll(0xFF, sub(restOfTheBytes, sll(NumberPosInDest, 3))))));
    }

	static class DataBlockTable extends BlockTable{

		DataBlockTable(int tableLen, Number baseAddress) {
			super(tableLen, baseAddress);
		}
	}
 
}
