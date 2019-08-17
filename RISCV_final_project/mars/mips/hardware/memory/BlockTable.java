package mars.mips.hardware.memory;
import static mars.util.Math2.*;
import mars.Globals;
import mars.mips.hardware.MemoryConfigurations;
import mars.util.Math2;

import static mars.mips.instructions.GenMath.*;

public class BlockTable extends AbstractBlockTable{

	protected final Number defaultLimitAddress;
	protected Number [][] blockTable;
	public static enum op{
		STORE,
		FETCH
	}

	BlockTable(int tableLen , Number baseAddress) {
		blockTable = new Number [Math.abs(tableLen)][];
		this.baseAddress = baseAddress;
		
		limitAddress = add(baseAddress, BLOCK_LENGTH_WORDS*tableLen* Memory.WORD_LENGTH_BYTES);
		defaultLimitAddress = limitAddress;
	}
	
	// Set "top" address boundary to go with each "base" address.  This determines permissable
    // address range for user program.  Currently limit is 4MB, or 1024 * 1024 * 4 Numbers
	// (except memory mapped IO, limited to 64KB by range).
	void setLimitAddress(Number getCurrentConfLimit) {
		limitAddress = min(getCurrentConfLimit, defaultLimitAddress);
	}
	
	void reset() {
		int blockTableSize = blockTable.length;
		blockTable = new Number [blockTableSize][];
	}
	
	 synchronized Number storeOrFetchNumbersInTable(Number address, int length,
			  Number value, boolean isLittleEndian, op operation) {
		 
		
		Number relativeNumberAddress = (this instanceof StackBlockTable) ? sub(baseAddress, address) : 
				sub(address, baseAddress); 
		int block, offset, numOfBytes = MemoryConfigurations.getCurrentComputingArchitecture()/8;
		Number NumberPositionInMemory, 
			NumberPositionInValue;
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
		
		for (NumberPositionInValue = (numOfBytes-1); Math2.isLt(loopStopper, NumberPositionInValue); NumberPositionInValue = sub(NumberPositionInValue, 1)) {
			NumberPositionInMemory = rem(relativeNumberAddress, numOfBytes);
			relativeWordAddress = sra(relativeNumberAddress, 2);
			block = div(relativeWordAddress, BLOCK_LENGTH_WORDS).intValue();  // Block Number
			offset = rem(relativeWordAddress, BLOCK_LENGTH_WORDS).intValue(); // Word within that block
			if(Globals.debug) {
				System.out.println("relativeWordAddress: " + relativeWordAddress);
				System.out.println("address: " +address);
				System.out.println("block: " + block);
				System.out.println("offset: " +offset);
			}
			if (blockTable[block] == null) {
				if (operation == op.STORE) 
					blockTable[block] = new Number[BLOCK_LENGTH_WORDS];
				else return 0;
			}
			if (isLittleEndian) NumberPositionInMemory = sub(numOfBytes, NumberPositionInMemory);
			if (operation == op.STORE) {
				if(blockTable[block][offset] == null) blockTable[block][offset] = 0;
				oldValue = replaceNumber(blockTable[block][offset], NumberPositionInMemory,
											oldValue, NumberPositionInValue, numOfBytes);
				blockTable[block][offset] = replaceNumber(value, NumberPositionInValue, 
				                  blockTable[block][offset], NumberPositionInMemory, numOfBytes);
			} 
			else { // op == FETCH
				if(blockTable[block][offset] == null) blockTable[block][offset] = 0;
				value = replaceNumber(blockTable[block][offset], NumberPositionInMemory, 
			                                   value, NumberPositionInValue, numOfBytes);
			}
			relativeNumberAddress = add(relativeNumberAddress, 1);
		}
		return (operation == op.STORE)? oldValue : value;
		}	
	
	
	
	synchronized Number storeWordInTable(Number address, Number value, final int shift) {
		Number oldValue;
 		int block, offset;
 		
 		// Relative to segment start, in Numbers.
 		// In stack,relative Number address is calculated "backward" because stack addresses grow down from base.
 		Number relative = (this instanceof StackBlockTable) ? sub(baseAddress, address) : 
			sub(address, baseAddress); 
 		relative = sra(relative, shift);
 		
 		block = (int)div(relative, BLOCK_LENGTH_WORDS);
 		offset = (int)rem(relative, BLOCK_LENGTH_WORDS); 
 		if (blockTable[block] == null) 
 			// First time writing to this block, so allocate the space.
 			blockTable[block] = new Number[BLOCK_LENGTH_WORDS];

 		
 		oldValue = blockTable[block][offset];
 		blockTable[block][offset] = value;
 		return oldValue;
	}
	
	synchronized Number fetchWordFromTable(Number address, boolean getOrNull, final int shift) {
		Number relative = (this instanceof StackBlockTable) ? sub(baseAddress, address) : 
			sub(address, baseAddress);
 		relative = srl(relative, shift);
		Number value = (getOrNull) ? null : 0;
        int block, offset;
        block = rem(relative, BLOCK_LENGTH_WORDS).intValue();
        offset = div(relative, BLOCK_LENGTH_WORDS).intValue(); 
        if (blockTable[block] != null) { 
			if(blockTable[block][offset] == null) blockTable[block][offset] = 0;
           value = blockTable[block][offset];
        }
        return value;
    }     
	
	private synchronized Number replaceNumber(Number sourceValue, Number NumberPosInSource,
			Number destValue,Number NumberPosInDest, int numOfBytes) {
         
		return
            // Set source Number value into destination Number position; set other 24 bits to 0's...
             or(
            		 sll(and(srl(sourceValue, sub(numOfBytes-8, sll(NumberPosInSource, 3))), 0xFF) 
                                             ,sub(numOfBytes-8, sll(NumberPosInDest, 3))),
               // and bitwise-OR it with...
              // Set 8 bits in destination Number position to 0's, other 24 bits are unchanged.
            		 and(destValue, neg(sll(0xFF, sub(numOfBytes-8, sll(NumberPosInDest, 3))))));
    }

	static class DataBlockTable extends BlockTable{

		DataBlockTable(int tableLen, Number baseAddress) {
			super(tableLen, baseAddress);
		}
	}
 
}
