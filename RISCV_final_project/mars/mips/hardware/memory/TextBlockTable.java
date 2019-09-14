package mars.mips.hardware.memory;

import mars.ProgramStatement;
import mars.mips.instructions.GenMath;
import static mars.mips.instructions.GenMath.*;
import static mars.util.Math2.*;

public class TextBlockTable extends AbstractBlockTable<ProgramStatement>{
	//private ProgramStatement [][] blockTable;

    public TextBlockTable(int tableLen , Number baseAddress) {
    	super(tableLen, baseAddress);
		blockTable = new ProgramStatement [tableLen][];
	}

	/**
	 * {@inheritDoc}
	 */
	void setLimitAddress(Number limitAddress) {
		this.limitAddress = min(limitAddress, defaultLimitAddress);
	}

	void reset() {
		int blockTableSize = blockTable.length;
		blockTable = new ProgramStatement [blockTableSize][];
	}
	
	///////////////////////////////////////////////////////////////////////   	
	// Stores a program statement at the given address.  Address has already been verified
	// as valid.  It may be either in user or kernel text segment.
	void storeProgramStatement(Number address, ProgramStatement statement) {

		Number relative = andThen(GenMath::sub, GenMath::srl, address, baseAddress, 2); // convert byte address to words
        int block = GenMath.div(relative, BLOCK_LENGTH_ADDRESS).intValue();
        int offset = GenMath.rem(relative, BLOCK_LENGTH_ADDRESS).intValue();
		if (block < blockTable.length) {
				if (blockTable[block] == null) {
					// No instructions are stored in this block, so allocate the block.
					blockTable[block] = new ProgramStatement[BLOCK_LENGTH_ADDRESS];
				}
				blockTable[block][offset] = statement;
		}
	}

	///////////////////////////////////////////////////////////////////////   	
	// Reads a program statement from the given address.  Address has already been verified
	// as valid.  It may be either in user or kernel text segment.  
	// Returns associated ProgramStatement or null if none. 
	ProgramStatement readProgramStatement(Number address) {
        Number relative = andThen(GenMath::sub, GenMath::srl, address, baseAddress, 2); // convert byte address to words
        int block = GenMath.div(relative, BLOCK_LENGTH_ADDRESS).intValue();
        int offset = GenMath.rem(relative, BLOCK_LENGTH_ADDRESS).intValue();
           if (block < blockTable.length && 
        		   blockTable[block] != null && blockTable[block][offset] != null) 
              return blockTable[block][offset];
        return null;
    }
}
