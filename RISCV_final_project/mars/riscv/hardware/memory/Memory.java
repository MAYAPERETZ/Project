package mars.riscv.hardware.memory;

import mars.Globals;
import mars.ProgramStatement;
import mars.Settings;
import mars.riscv.hardware.AccessNotice;
import mars.riscv.hardware.AddressErrorException;
import mars.riscv.hardware.MemoryConfigurations;
import mars.util.GenMath;
import mars.riscv.instructions.Instruction;
import mars.simulator.Exceptions;
import java.util.*;
import java.util.function.BiFunction;
import static mars.util.GenMath.*;
import static mars.util.Math2.*;
	
	/*
Copyright (c) 2003-2009,  Pete Sanderson and Kenneth Vollmar

Developed by Pete Sanderson (psanderson@otterbein.edu)
and Kenneth Vollmar (kenvollmar@missouristate.edu)

Permission is hereby granted, free of charge, to any person obtaining 
a copy of this software and associated documentation files (the 
"Software"), to deal in the Software without restriction, including 
without limitation the rights to use, copy, modify, merge, publish, 
distribute, sublicense, and/or sell copies of the Software, and to 
permit persons to whom the Software is furnished to do so, subject 
to the following conditions:

The above copyright notice and this permission notice shall be 
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR 
ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

(MIT license, http://www.opensource.org/licenses/mit-license.html)
 */

/**
 * Represents RISCV memory.  Different segments are represented by different data structs.
 *
 * Each Method was evolved and rearranged by Maya Peretz in September 2019. (Mainly in oreder to support 64
 * bit architecture)
 *
 * @author Pete Sanderson 
 * @version August 2003
 */

/////////////////////////////////////////////////////////////////////
// NOTE: This implementation is purely big-endian.  RISCV can handle either one.
/////////////////////////////////////////////////////////////////////

    public class Memory extends Observable  {

    /** base address for .extern directive **/
    public static Number externBaseAddress = MemoryConfigurations.getDefaultExternBaseAddress();

    /** base address for storing globals **/
    public static Number globalPointer = MemoryConfigurations.getDefaultGlobalPointer();

    /** base address for storage of non-global static data in data segment **/

    /** base address for heap **/
    public static Number heapBaseAddress = MemoryConfigurations.getDefaultHeapBaseAddress();
    /** starting address for stack **/

    /** base address for stack **/
    public static Number stackPointer = MemoryConfigurations.getDefaultStackPointer();

    /** highest address accessible in user (not kernel) mode. **/
    public static Number userHighAddress = MemoryConfigurations.getDefaultUserHighAddress();

    /** starting address for exception handlers **/
    public static Number exceptionHandlerAddress = MemoryConfigurations.getDefaultExceptionHandlerAddress();

    /** highest address accessible in kernel mode. **/
    public static Number kernelHighAddress = MemoryConfigurations.getDefaultKernelHighAddress();

    /** RISCV word length in bytes. **/
    // NOTE:  Much of the code is hardwired for 4 byte words.  Refactoring this is low priority.
    public static final int WORD_LENGTH_BYTES = 4;
    /** Constant representing byte order of each memory word.  Little-endian means lowest
    numbered byte is right most [3][2][1][0]. */
    private static final boolean LITTLE_ENDIAN = true;
    /** Constant representing byte order of each memory word.  Big-endian means lowest
    numbered byte is left most [0][1][2][3]. */
    public static final boolean BIG_ENDIAN = false;
    /** Current setting for endian (default LITTLE_ENDIAN) **/
    private static boolean byteOrder = LITTLE_ENDIAN;

    public static Number heapAddress;
   
    // Memory will maintain a collection of observables.  Each one is associated
    // with a specific memory address or address range, and each will have at least
    // one observer registered with it.  When memory access is made, make sure only
    // observables associated with that address send notices to their observers.
    // This assures that observers are not bombarded with notices from memory
    // addresses they do not care about.
    //
    // Would like a tree-like implementation, but that is complicated by this fact:
    // key for insertion into the tree would be based on Comparable using both low 
    // and high end of address range, but retrieval from the tree has to be based
    // on target address being ANYWHERE IN THE RANGE (not an exact key match).
      
    Collection observables = getNewMemoryObserversCollection();
   
    // The data segment is allocated in blocks of 1024 ints (4096 bytes).  Each block is
    // referenced by a "block table" entry, and the table has 1024 entries.  The capacity
    // is thus 1024 entries * 4096 bytes = 4 MB.  Should be enough to cover most
    // programs!!  Beyond that it would go to an "indirect" block (similar to Unix i-nodes),
    // which is not implemented.
    //
    // Although this scheme is an array of arrays, it is relatively space-efficient since
    // only the table is created initially. A 4096-byte block is not allocated until a value 
    // is written to an address within it.  Thus most small programs will use only 8K bytes 
    // of space (the table plus one block).  The index into both arrays is easily computed 
    // from the address; access time is constant.
    //
    // SPIM stores statically allocated data (following first .data directive) starting
    // at location 0x10010000.  This is the first Data Segment word beyond the reach of $gp
    // used in conjunction with signed 16 bit immediate offset.  $gp has value 0x10008000
    // and with the signed 16 bit offset can reach from 0x10008000 - 0xFFFF = 0x10000000 
    // (Data Segment base) to 0x10008000 + 0x7FFF = 0x1000FFFF (the byte preceding 0x10010000).
    //
    // Using my scheme, 0x10010000 falls at the beginning of the 17'th block -- table entry 16.
    // SPIM uses a heap base address of 0x10040000 which is not part of the MIPS specification.
    // (I don't have a reference for that offhand...)  Using my scheme, 0x10040000 falls at
    // the start of the 65'th block -- table entry 64.  That leaves (1024-64) * 4096 = 3,932,160
    // bytes of space available without going indirect.
    
    private static final int BLOCK_TABLE_LENGTH = 1024; // Each entry of table points to a block.
    private BlockTable kernelDataBlockTable;
    
    // The stack is modeled similarly to the data segment.  It cannot share the same
    // data structure because the stack base address is very large.  To store it in the
    // same data structure would require implementation of indirect blocks, which has not
    // been realized.  So the stack gets its own table of blocks using the same dimensions
    // and allocation scheme used for data segment.
    //
    // The other major difference is the stack grows DOWNWARD from its base address, not
    // upward.  I.e., the stack base is the largest stack address. This turns the whole
    // scheme for translating memory address to block-offset on its head!  The simplest
    // solution is to calculate relative address (offset from base) by subtracting the
    // desired address from the stack base address (rather than subtracting base address
    // from desired address).  Thus as the address gets smaller the offset gets larger.
    // Everything else works the same, so it shares some private helper methods with
    // data segment algorithms.

    //  private StackBlockTable stackBlockTable;

    // Memory mapped I/O is simulated with a separate table using the same structure and
    // logic as data segment.  Memory is allocated in 4K byte blocks.  But since MMIO
    // address range is limited to 0xffff0000 to 0xfffffffc, there are only 64K bytes
    // total.  Thus there will be a maximum of 16 blocks, and I suspect never more than
    // one since only the first few addresses are typically used.  The only exception
    // may be a rogue program generating such addresses in a loop.  Note that the
    // MMIO addresses are interpreted by Java as negative numbers since it does not
    // have unsigned types.  As long as the absolute address is correctly translated
    // into a table offset, this is of no concern.

    private static final int MMIO_TABLE_LENGTH = 16; // Each entry of table points to a 4K block.
   	    
    // I use a similar scheme for storing instructions.  MIPS text segment ranges from
    // 0x00400000 all the way to data segment (0x10000000) a range of about 250 MB!  So
    // I'll provide table of blocks with similar capacity.  This differs from data segment
    // somewhat in that the block entries do not contain int's, but instead contain
    // references to ProgramStatement objects.

    private static final int TEXT_BLOCK_TABLE_LENGTH = 1024; // Each entry of table points to a block.
    private TextBlockTable  textBlockTable;
    private TextBlockTable kernelTextBlockTable;
    private BlockTables tables;

   
    // This will be a Singleton class, only one instance is ever created.  Since I know the
    // Memory object is always needed, I'll go ahead and create it at the time of class loading.
    // (greedy rather than lazy instantiation).  The constructor is private and getInstance()
    // always returns this instance.

    private static Memory uniqueMemoryInstance = new Memory();

    enum DataTypes {
        Byte(1), halfword(2), word(4), doubleword(8);

        private final int len;
        DataTypes(int len) { this.len= len; }
        public int getValue() { return len; }
    }
      
    /*
    * Private constructor for Memory.  Separate data structures for text and data segments.
    **/
    private Memory() {
       initialize();
    }

    /**
    * Returns the unique Memory instance, which becomes in essence global.
    */
    public static Memory getInstance() {
     return uniqueMemoryInstance;
    }

    /**
    * Explicitly clear the contents of memory.  Typically done at start of assembly.
    */
    public void clear() {
       setConfiguration();
       initialize();
    }
   
    /**
    * Sets current memory configuration for simulated MIPS.  Configuration is
    * collection of memory segment addresses. e.g. text segment starting at
    * address 0x00400000.  Configuration can be modified starting with MARS 3.7.
    */
    public void setConfiguration() {
        externBaseAddress = MemoryConfigurations.getCurrentConfiguration().getExternBaseAddress(); //0x10000000;
        globalPointer = MemoryConfigurations.getCurrentConfiguration().getGlobalPointer(); //0x10008000;
        heapBaseAddress = MemoryConfigurations.getCurrentConfiguration().getHeapBaseAddress(); //0x10040000; // I think from SPIM not MIPS
        stackPointer = MemoryConfigurations.getCurrentConfiguration().getStackPointer(); //0x7fffeffc;
        userHighAddress = MemoryConfigurations.getCurrentConfiguration().getUserHighAddress(); //0x7fffffff;
        exceptionHandlerAddress = MemoryConfigurations.getCurrentConfiguration().getExceptionHandlerAddress(); //0x80000180;
        kernelHighAddress = MemoryConfigurations.getCurrentConfiguration().getKernelHighAddress(); //0xffffffff;

        //tables.get(0).setBaseAddress(MemoryConfigurations.getCurrentConfiguration().getDataSegmentBaseAddress());
        textBlockTable.setBaseAddress(MemoryConfigurations.getCurrentConfiguration().getTextBaseAddress());
        //tables.get(1).setBaseAddress(MemoryConfigurations.getCurrentConfiguration().getKernelDataBaseAddress());
        kernelTextBlockTable.setBaseAddress(MemoryConfigurations.getCurrentConfiguration().getKernelTextBaseAddress());
        //tables.get(2).setBaseAddress(MemoryConfigurations.getCurrentConfiguration().getStackBaseAddress());
        //tables.get(3).setBaseAddress(MemoryConfigurations.getCurrentConfiguration().getMemoryMapBaseAddress());

        tables.get(0).setLimitAddress(MemoryConfigurations.getCurrentConfiguration().getDataSegmentLimitAddress());
        textBlockTable.setLimitAddress(MemoryConfigurations.getCurrentConfiguration().getTextLimitAddress());
        tables.get(1).setLimitAddress(MemoryConfigurations.getCurrentConfiguration().getKernelDataSegmentLimitAddress());
        kernelTextBlockTable.setLimitAddress(MemoryConfigurations.getCurrentConfiguration().getKernelTextLimitAddress());
        tables.get(2).setLimitAddress(MemoryConfigurations.getCurrentConfiguration().getStackLimitAddress());
        tables.get(3).setLimitAddress(MemoryConfigurations.getCurrentConfiguration().getMemoryMapLimitAddress());

    }
       
    private void initialize() {
        heapAddress = heapBaseAddress;
        tables = new BlockTables();
        kernelTextBlockTable  = new TextBlockTable(TEXT_BLOCK_TABLE_LENGTH, MemoryConfigurations.getCurrentConfiguration().getKernelTextBaseAddress());
        textBlockTable  = new TextBlockTable(TEXT_BLOCK_TABLE_LENGTH, MemoryConfigurations.getCurrentConfiguration().getTextBaseAddress());
        System.gc(); // call garbage collector on any Table memory just deallocated.
    }
     
    /**
    * Returns the next available word-aligned heap address.  There is no recycling and
    * no heap management!  There is however nearly 4MB of heap space available in Mars.
    *
    * @param numBytes Number of bytes requested.  Should be multiple of 4, otherwise next higher multiple of 4 allocated.
    * @return address of allocated heap storage.
    * @throws IllegalArgumentException if number of requested bytes is negative or exceeds available heap storage
    */
    public Number allocateBytesFromHeap(int numBytes) throws IllegalArgumentException {
        Number result = heapAddress;
        if (numBytes < 0)
            throw new IllegalArgumentException("request ("+numBytes+") is negative heap amount");

        Number newHeapAddress = add(heapAddress, numBytes);
        if (!isEqz(rem(newHeapAddress, 4)) )
            newHeapAddress = add(newHeapAddress, sub(4, rem(newHeapAddress, 4))) ; // next higher multiple of 4

        if (!isLt(newHeapAddress,getDataTable().getBaseAddress()))
            throw new IllegalArgumentException("request ("+numBytes+") exceeds available heap storage");

        heapAddress = newHeapAddress;
        return result;
    }

    /**
    * Set byte order to either LITTLE_ENDIAN or BIG_ENDIAN.  Default is LITTLE_ENDIAN.
    * @param order either LITTLE_ENDIAN or BIG_ENDIAN
    */
    public void setByteOrder(boolean order) {
        byteOrder = order;
    }

    /**
    * Retrieve memory byte order.  Default is LITTLE_ENDIAN (like PCs).
    * @return either LITTLE_ENDIAN or BIG_ENDIAN
    */
    public boolean getByteOrder() {
        return byteOrder;
    }
   	
    /*  *******************************  THE SETTER METHODS  ******************************/

    ///////////////////////////////////////////////////////////////////////////////////////
    /** 
    *  Starting at the given address, write the given value over the given number of bytes.
    *  This one does not check for word boundaries, and copies one byte at a time.
    *  If length == 1, takes value from low order byte.  If 2, takes from low order half-word.
    *
    *  @param address Starting address of Memory address to be set.
    *  @param value Value to be stored starting at that address.
    *  @param length Number of bytes to be written.
    *  @return old value that was replaced by the set operation
    **/

    // Allocates blocks if necessary.
    public Number set(Number address, Number value, int length) throws AddressErrorException {
        Number oldValue = 0;

        if(tables.isInAnyBlockTable(address)) {
            for(BlockTable table : tables) {
                if(table.inSegment(address)) {
                    oldValue = table.storeOrFetchNumbersInTable(address, length, value, (byteOrder == LITTLE_ENDIAN),
                            BlockTable.op.STORE);
                    break;
                }
            }
        }
        else if (textBlockTable.inSegment(address)) {
            // Burch Mod (Jan 2013): replace throw with call to setStatement
            // DPS adaptation 5-Jul-2013: either throw or call, depending on setting

            if (Globals.getSettings().getBooleanSetting(Settings.SELF_MODIFYING_CODE_ENABLED)) {
               ProgramStatement oldStatement = getStatementNoNotify(address);
               if (oldStatement != null)
                  oldValue = oldStatement.getBinaryStatement();

               setStatement(address, new ProgramStatement(value.intValue(), address));
            }
            else
                throw new AddressErrorException("Cannot write directly to text segment!",
                        Exceptions.ADDRESS_EXCEPTION_STORE, address);
        }
        else if (kernelTextBlockTable.inSegment(address)) {
            // DEVELOPER: PLEASE USE setStatement() TO WRITE TO KERNEL TEXT SEGMENT...
            throw new AddressErrorException("DEVELOPER: You must use setStatement() to write to kernel text segment!",
               Exceptions.ADDRESS_EXCEPTION_STORE, address);
        }
        else
            // falls outside Mars addressing range
            throw new AddressErrorException("address out of range ", Exceptions.ADDRESS_EXCEPTION_STORE, address);
        notifyAnyObservers(AccessNotice.WRITE, address, length, value);
        return oldValue;
    }
   	
    ///////////////////////////////////////////////////////////////////////////////////////
    /** 
     *  Starting at the given address, write the given value over 8/4 bytes.
     *  It must be written as is, without adjusting for byte order (little vs big endian).
     *  Address must be word/double-word-aligned (depends on the value length and configuration).
     *
     *  @param address Starting address of Memory address to be set.
     *  @param value Value to be stored starting at that address.
     *  @return old value that was replaced by the set operation.
     *  @throws AddressErrorException If address is not on word boundary.
    **/
       private Number setRaw(Number address, Number value, final int shift) throws AddressErrorException {
         Number oldValue=0;
         int addressLength = (shift % 2)*WORD_LENGTH_BYTES;
         if (!isEqz(rem(address, (addressLength + WORD_LENGTH_BYTES)))) {
            throw new AddressErrorException("store address not aligned on word boundary ",
               Exceptions.ADDRESS_EXCEPTION_STORE, address);
         }
         if(tables.isInAnyBlockTable(address)) {
          	for(BlockTable table : tables) {
         	 	if(table.inSegment(address)) {
         		 	oldValue = table.storeWordInTable(address, value, shift);
         		 	break;
         	 	}
          	}
         }
         else if (textBlockTable.inSegment(address)) {	
           // Burch Mod (Jan 2013): replace throw with call to setStatement 
           // DPS adaptation 5-Jul-2013: either throw or call, depending on setting
            if (Globals.getSettings().getBooleanSetting(Settings.SELF_MODIFYING_CODE_ENABLED)) {
               ProgramStatement oldStatement = getStatementNoNotify(address);
               if (oldStatement != null) 
                  oldValue = oldStatement.getBinaryStatement();
               	  setStatement(address, new ProgramStatement(value.intValue(), address));
            } 
            else {
               throw new AddressErrorException(
                  "Cannot write directly to text segment!", 
                  Exceptions.ADDRESS_EXCEPTION_STORE, address);
            }
         } 
         else if (kernelTextBlockTable.inSegment(address)) {
           // DEVELOPER: PLEASE USE setStatement() TO WRITE TO KERNEL TEXT SEGMENT...
            throw new AddressErrorException(
               	"DEVELOPER: You must use setStatement() to write to kernel text segment!", 
               	Exceptions.ADDRESS_EXCEPTION_STORE, address);
         } 
         else {
           // falls outside Mars addressing range
            throw new AddressErrorException("store address out of range ",
               Exceptions.ADDRESS_EXCEPTION_STORE,	address);
         }
         notifyAnyObservers(AccessNotice.WRITE, address, WORD_LENGTH_BYTES, value);
         if (Globals.getSettings().getBackSteppingEnabled()) {
            Globals.program.getBackStepper().addMemoryRestoreRawWord(address,oldValue);
         }
         return oldValue;
      }
       
      
       public Number setRawWord(Number address, Number value) throws AddressErrorException {
    	   return setRaw(address, value, 2);
       }
       
       public Number setRawDoubleWord(Number address, Number value) throws AddressErrorException {
    	   return setRaw(address, value, 3);
       }
   	
    ///////////////////////////////////////////////////////////////////////////////////////
    /**
    *  Starting at the given word address, write the given value over 4 bytes (a word).
    *  The address must be word-aligned.
    *
    * @param address Starting address of Memory address to be set.
    * @param value Value to be stored starting at that address.
    * @return old value that was replaced by setWord operation.
    * @throws AddressErrorException If address is not on word boundary.
    */
    public Number setWord(Number address, Number value) throws AddressErrorException {
        return set(address, value, DataTypes.word, Globals.program.getBackStepper()::addMemoryRestoreWord);
    }
   

    public Number setDoubleWord(Number address, Number value) throws AddressErrorException {
        return set(address, value, DataTypes.doubleword, Globals.program.getBackStepper()::addMemoryRestoreDoubleWord);
    }
   
   
    ///////////////////////////////////////////////////////////////////////////////////////

    /**
    *  Starting at the given halfword address, write the lower 16 bits of given value
    *  into 2 bytes (a halfword).
    *
    * @param address Starting address of Memory address to be set.
    * @param value Value to be stored starting at that address.  Only low order 16 bits used.
    * @return old value that was replaced by setHalf operation.
    * @throws AddressErrorException If address is not on halfword boundary.
    */
    public Number setHalf(Number address, Number value) throws AddressErrorException {
        return set(address, value, DataTypes.halfword, Globals.program.getBackStepper()::addMemoryRestoreByte);
    }
    ///////////////////////////////////////////////////////////////////////////////////////

    /**
    * Writes low order 8 bits of given value into specified Memory byte.
    *
    * @param address Address of Memory byte to be set.
    * @param value Value to be stored at that address.  Only low order 8 bits used.
    * @return old value that was replaced by setByte operation.
    */
    public Number setByte(Number address, Number value) throws AddressErrorException {
        return set(address, value, DataTypes.Byte, Globals.program.getBackStepper()::addMemoryRestoreByte);
    }

   ////////////////////////////////////////////////////////////////////////////////

    /**
    * Stores ProgramStatement in Text Segment.
    * @param address Starting address of Memory address to be set.  Must be word boundary.
    * @param statement Machine code to be stored starting at that address -- for simulation
    * purposes, actually stores reference to ProgramStatement instead of 32-bit machine code.
    * @throws AddressErrorException If address is not on word boundary or is outside Text Segment.
    * @see ProgramStatement
    */
    public void setStatement(Number address, ProgramStatement statement) throws AddressErrorException {
        if (!isEqz(rem(address, 4)) || !textBlockTable.inSegment(address) ||
             kernelTextBlockTable.inSegment(address)) {
            throw new AddressErrorException(
               "store address to text segment out of range or not aligned to word boundary ",
               Exceptions.ADDRESS_EXCEPTION_STORE, address);
        }
        if (textBlockTable.inSegment(address)) textBlockTable.storeProgramStatement(address, statement);
        else kernelTextBlockTable.storeProgramStatement(address, statement);
    }

    private Number set(Number address, Number value, DataTypes type, BiFunction<Number,Number, Number> x)
            throws AddressErrorException {

        if (!isEqz(GenMath.rem(address,type.getValue()))) {
            throw new AddressErrorException("store address not aligned on "+ type +" boundary ",
                Exceptions.ADDRESS_EXCEPTION_STORE, address);
        }

        return (Globals.getSettings().getBackSteppingEnabled())
        ? x.apply(address ,set(address ,value ,type.getValue())) : set(address, value, type.getValue());

    }
   
 	
   /********************************  THE GETTER METHODS  ******************************/
       
    public TextBlockTable getTextTable() {
        return  textBlockTable;
    }

    public TextBlockTable getKernelTextTable() {
        return  kernelTextBlockTable;
    }
      
    public BlockTable getDataTable() {
        return tables.get(0);
    }
   	  
    public BlockTable getKernelDataTable() {
        return tables.get(1);
    }
   	  
    public BlockTable getStackTable() {
        return tables.get(2);
    }
   	  
    public BlockTable getMemoryMapTable() {
        return tables.get(3);
    }
   
   //////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Starting at the given word/double-word address, read the given number of bytes.
    * This one does not check for word boundaries, and copies one byte at a time.
    * If length == 1, puts value in low order byte.  If 2, puts into low order half-word.
    *
    * @param address Starting address of Memory address to be read.
    * @param length Number of bytes to be read.
    * @return  Value stored starting at that address.
    */
    public Number get(Number address, int length) throws AddressErrorException {
        return get(address, length, true);
    }
   	
    // Does the real work, but includes option to NOT notify observers.
    private Number get(Number address, int length, boolean notify) throws AddressErrorException {
        Number value = 0;
         
        if(tables.isInAnyBlockTable(address)) {
            for(BlockTable table : tables) {
                if(table.inSegment(address)) {
                    value = table.storeOrFetchNumbersInTable(
                            address, length, value, (byteOrder == LITTLE_ENDIAN), BlockTable.op.FETCH);
                    break;
                }
            }
        }
        else if (textBlockTable.inSegment(address)) {
            // Burch Mod (Jan 2013): replace throw with calls to getStatementNoNotify & getBinaryStatement
            // DPS adaptation 5-Jul-2013: either throw or call, depending on setting
            if (Globals.getSettings().getBooleanSetting(Settings.SELF_MODIFYING_CODE_ENABLED)) {
               ProgramStatement stmt = getStatementNoNotify(address);
               value = stmt == null ? 0 : stmt.getBinaryStatement();
            }
            else {
                throw new AddressErrorException(
                "Cannot read directly from text segment!",
                Exceptions.ADDRESS_EXCEPTION_LOAD, address);
            }
        }
        else if (kernelTextBlockTable.inSegment(address)) {
            // DEVELOPER: PLEASE USE getStatement() TO READ FROM KERNEL TEXT SEGMENT...
            throw new AddressErrorException(
               "DEVELOPER: You must use getStatement() to read from kernel text segment!",
               Exceptions.ADDRESS_EXCEPTION_LOAD, address);
        }
        else {
            // falls outside Mars addressing range
            throw new AddressErrorException("address out of range ",
            Exceptions.ADDRESS_EXCEPTION_LOAD, address);
        }
        if (notify) notifyAnyObservers(AccessNotice.READ, address, length, value);
            return value;
    }
   
   /////////////////////////////////////////////////////////////////////////
    /**
    *  Starting at the given word address, read a 4/8 byte word/double-word as an int/long.
    *  It transfers the 32/64 bit value "raw" as stored in memory, and does not adjust
    *  for byte order (big or little endian).  Address must be word/double-word-aligned.
    *
    *  @param address Starting address of word to be read.
    *  @return  Word/Double-word (4 or 8 byte value respectively) stored starting at that address.
    *  @throws AddressErrorException If address is not on word/double-word boundary.
    */

    // Note: the logic here is repeated in getRawWordOrNull() below.  Logic is
    // simplified by having this method just call getRawWordOrNull() then 
    // return either the int of its return value, or 0 if it returns null.
    // Doing so would be detrimental to simulation runtime performance, so
    // I decided to keep the duplicate logic.
    public Number getRaw(Number address , final int shift) throws AddressErrorException {
        Number value = 0;
        int addressLength = (shift % 2)*WORD_LENGTH_BYTES;
        if (!isEqz(rem(address, (addressLength + WORD_LENGTH_BYTES)))) {
            throw new AddressErrorException("address for fetch not aligned on word boundary",
               Exceptions.ADDRESS_EXCEPTION_LOAD, address);
        }

        if(tables.isInAnyBlockTable(address)) {
            for(BlockTable table : tables) {
                if(table.inSegment(address)) {
                    if (!(table instanceof BlockTable.DataBlockTable)) {
                        value = table.fetchWordFromTable(address, false, shift);
                        break;
                    } else{
                        // DEVELOPER: PLEASE USE getStatement() TO READ FROM KERNEL TEXT SEGMENT...
                        throw new AddressErrorException(
                                "DEVELOPER: You must use getStatement() to read from kernel text segment!",
                                Exceptions.ADDRESS_EXCEPTION_LOAD, address);
                    }
                }
            }
        }
        else if (textBlockTable.inSegment(address)) {
            // Burch Mod (Jan 2013): replace throw with calls to getStatementNoNotify & getBinaryStatement
            // DPS adaptation 5-Jul-2013: either throw or call, depending on setting
            if (Globals.getSettings().getBooleanSetting(Settings.SELF_MODIFYING_CODE_ENABLED)) {
               ProgramStatement stmt = getStatementNoNotify(address);
               value = stmt == null ? 0 : stmt.getBinaryStatement();
            }
            else {
               throw new AddressErrorException(
                  "Cannot read directly from text segment!",
                  Exceptions.ADDRESS_EXCEPTION_LOAD, address);
            }
        }
        else  // falls outside Mars addressing range
            throw new AddressErrorException("address out of range ",
               Exceptions.ADDRESS_EXCEPTION_LOAD, address);

        notifyAnyObservers(AccessNotice.READ, address, Memory.WORD_LENGTH_BYTES,value);
        return value;
    }
       
       
       public Number getRawWord(Number address) throws AddressErrorException {
    	   return getRaw(address, 2);
       }
       

       public Number getRawDoubleWord(Number address) throws AddressErrorException {
    	   return getRaw(address, 3);
       }
   
    /////////////////////////////////////////////////////////////////////////
    /**
    *  Starting at the given address, read a 4/8 byte value as an int/long and return Integer/Long.
    *  It transfers the 32/64 bit value "raw" as stored in memory, and does not adjust
    *  for byte order (big or little endian).  Address must be word-aligned.
    *
    *  Returns null if reading from text segment and there is no instruction at the
    *  requested address. Returns null if reading from data segment and this is the
    *  first reference to the MARS 4K memory allocation block (i.e., an array to
    *  hold the memory has not been allocated).
    *
    *  This method was developed by Greg Giberling of UC Berkeley to support the memory
    *  dump feature that he implemented in Fall 2007.
    *  @param address Starting address of word to be read.
    *  @return  Word/Double-word (4 or 8 byte value respectively) stored starting at that address as a/n Integer/Long.
    *  Conditions that cause return value null are described above.
    *  @throws AddressErrorException If address is not on word/double-word boundary.
    **/
   	 
    // See note above, with getRawWord(), concerning duplicated logic.
    public Number getRawOrNull(Number address, final int shift) throws AddressErrorException {
        Number value = null;
        int addressLength = (shift % 2);
        if (!isEqz(addressLength) &&!isEqz(rem(address, ((2 * addressLength)*WORD_LENGTH_BYTES))))
            throw new AddressErrorException("address for fetch not aligned on word boundary",
                  Exceptions.ADDRESS_EXCEPTION_LOAD, address);

        if(tables.isInAnyBlockTable(address)) {
            for(int i = 0; i < tables.size()-1; i++) { // all block tables except memory map
                if(tables.get(i).inSegment(address)) {
                    value = tables.get(i).fetchWordFromTable(address, false, shift);
                    break;
                }
            }
        }
        else if (textBlockTable.inSegment(address) || kernelDataBlockTable.inSegment(address)) {
             try {
               value = (getStatementNoNotify(address) == null)
                       ? null : (long) getStatementNoNotify(address).getBinaryStatement();
             }
             catch (AddressErrorException aee) {
                return value;
             }
        }
        else // falls outside Mars addressing range
            throw new AddressErrorException("address out of range ", Exceptions.ADDRESS_EXCEPTION_LOAD, address);
            // Do not notify observers.  This read operation is initiated by the
            // dump feature, not the executing MIPS program.
        return value;
    }

    public Number getRawWordOrNull(Number address) throws AddressErrorException {
       return getRawOrNull(address, 2);
    }

    public Number getRawDoubleWordOrNull(Number address) throws AddressErrorException {
       return getRawOrNull(address, 3);
    }

    /**
    *  Look for first "null" memory value in an address range.  For text segment (binary code), this
    *  represents a word that does not contain an instruction.  Normally use this to find the end of
    *  the program.  For data segment, this represents the first block of simulated memory (block length
    *  currently 4K words) that has not been referenced by an assembled/executing program.
    *
    *  @param baseAddress lowest MIPS address to be searched; the starting point
    *  @param limitAddress highest MIPS address to be searched
    *  @return lowest address within specified range that contains "null" value as described above.
    *  @throws AddressErrorException if the base address is not on a word boundary
    */
    public Number getAddressOfFirstNull(Number baseAddress, Number limitAddress) throws AddressErrorException {
         Number address = baseAddress;
         for (; isLt(address, limitAddress); address = add(address, Memory.WORD_LENGTH_BYTES)) {
            if (getRawWordOrNull(address) == null)
               break;
         }
         return address;
    }
   
    ///////////////////////////////////////////////////////////////////////////////////////
    /**
    *  Starting at the given word address, read a 4 byte word as an int.
    *  Does not use "get()"; we can do it faster here knowing we're working only
    *  with full words.
    *
    *  @param address Starting address of word to be read.
    *  @return  Word (4-byte value) stored starting at that address.
    *  @throws AddressErrorException If address is not on word boundary.
    */
    public Number getWord(Number address) throws AddressErrorException {
        if (GenMath.rem(address, WORD_LENGTH_BYTES).intValue() != 0)
            throw new AddressErrorException("fetch address not aligned on double word boundary ",
            Exceptions.ADDRESS_EXCEPTION_LOAD, address);

        if(MemoryConfigurations.getCurrentComputingArchitecture() == 32)
            return get(address.intValue(), 4);
        return get(address.longValue(), 4);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    /**
    *  Starting at the given word address, read a 8 byte word as an int.
    *  @param address Starting address of word to be read.
    *  @return  Double word (8-byte value) stored starting at that address.
    *  @throws AddressErrorException If address is not on word boundary.
    */
    public Number getDoubleWord(Number address) throws AddressErrorException {
        if (GenMath.rem(address,(2*WORD_LENGTH_BYTES)).intValue() != 0) {
            throw new AddressErrorException("fetch address not aligned on double word boundary ",
                Exceptions.ADDRESS_EXCEPTION_LOAD, address);
        }
        if(MemoryConfigurations.getCurrentComputingArchitecture() == 32)
            return get(address.intValue(), 8);
        return get(address.longValue(), 8);
    }
    ///////////////////////////////////////////////////////////////////////////////////////

    /**
    *  Starting at the given word address, read a 4 byte word as an int.
    *  Does not use "get()"; we can do it faster here knowing we're working only
    *  with full words.  Observers are NOT notified.
    *
    *  @param address Starting address of word to be read.
    *  @return  Word (4-byte value) stored starting at that address.
    *  @throws AddressErrorException If address is not on word boundary.
    */
    public Number getWordNoNotify(Number address) throws AddressErrorException {
        if (GenMath.rem(address,WORD_LENGTH_BYTES).intValue() != 0) {
            throw new AddressErrorException("fetch address not aligned on word boundary ",
                Exceptions.ADDRESS_EXCEPTION_LOAD, address);
        }
        if(MemoryConfigurations.getCurrentComputingArchitecture() == 32)
            return get(address.intValue(), 4, false);
        return get(address.longValue(), 4, false);
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    /**
    *  Starting at the given word address, read a 2 byte word into lower 16 bits of int.
    *  @param address Starting address of word to be read.
    *  @return  Halfword (2-byte value) stored starting at that address, stored in lower 16 bits.
    *  @throws AddressErrorException If address is not on halfword boundary.
    */
    public Number getHalf(Number address) throws AddressErrorException {
        if (GenMath.rem(address,2).intValue() != 0) {
            throw new AddressErrorException("fetch address not aligned on halfword boundary ",
                Exceptions.ADDRESS_EXCEPTION_LOAD, address);
        }
        if(MemoryConfigurations.getCurrentComputingArchitecture() == 32)
            return get(address.intValue(), 2);
        return get(address.longValue(), 2);
    }
 

    ///////////////////////////////////////////////////////////////////////////////////////

    /**
    *  Reads specified Memory byte into low order 8 bits of int.
    *  @param address Address of Memory byte to be read.
    *  @return Value stored at that address.  Only low order 8 bits used.
    */
    public Number getByte(Number address) throws AddressErrorException {
        if(MemoryConfigurations.getCurrentComputingArchitecture() == 32)
            return get(address.intValue(), 1);
        return get(address.longValue(), 1);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    /**
    *  Gets ProgramStatement from Text Segment.
    *  @param address Starting address of Memory address to be read.  Must be word boundary.
    *  @return reference to ProgramStatement object associated with that address, or null if none.
    *  @throws AddressErrorException If address is not on word boundary or is outside Text Segment.
    *  @see ProgramStatement
    */
    public ProgramStatement getStatement(Number address) throws AddressErrorException {
        return getStatement(address, true);
    }
   
   ////////////////////////////////////////////////////////////////////////////////
    /**
    *  Gets ProgramStatement from Text Segment without notifying observers.
    *  @param address Starting address of Memory address to be read.  Must be word boundary.
    *  @return reference to ProgramStatement object associated with that address, or null if none.
    *  @throws AddressErrorException If address is not on word boundary or is outside Text Segment.
    *  @see ProgramStatement
    */
    public ProgramStatement getStatementNoNotify(Number address) throws AddressErrorException {
        return getStatement(address, false);
    }
   
   ////////////////////////////////////////////////////////////////////////////////

    private ProgramStatement getStatement(Number address, boolean notify) throws AddressErrorException {
        if (!wordAligned(address)) {
            throw new AddressErrorException("fetch address for text segment not aligned to word boundary ",
                Exceptions.ADDRESS_EXCEPTION_LOAD, address);
        }
        if (!Globals.getSettings().getBooleanSetting(Settings.SELF_MODIFYING_CODE_ENABLED)
        && !(textBlockTable.inSegment(address) || kernelTextBlockTable.inSegment(address))) {
            throw new AddressErrorException("fetch address for text segment out of range ",
                Exceptions.ADDRESS_EXCEPTION_LOAD, address);
        }

        if (textBlockTable.inSegment(address))
            return readProgramStatement(textBlockTable, address, notify);
        else if (kernelTextBlockTable.inSegment(address))
            return readProgramStatement(kernelTextBlockTable, address, notify);

        return new ProgramStatement(get(address, WORD_LENGTH_BYTES).intValue(), address);
    }

   /*********************************  THE UTILITIES  *************************************/ 
   
   /**
    *  Utility to determine if given address is word-aligned.
    *  @param address the address to check
    *  @return true if address is word-aligned, false otherwise
    */
    public static boolean wordAligned(Number address) {
        return isEqz(GenMath.rem(address, WORD_LENGTH_BYTES));
    }
   
    /**
    *  Utility to determine if given address is doubleword-aligned.
    *  @param address the address to check
    *  @return true if address is doubleword-aligned, false otherwise
    */
    public static boolean doublewordAligned(Number address) {
        return isEqz(GenMath.rem(address,(2*WORD_LENGTH_BYTES)));
    }
   
    /**
    *  Utility method to align given address to next full word boundary, if not already
    *  aligned.
    *  @param address  a memory address (any long value is potentially valid)
    *  @return address aligned to next word boundary (divisible by 4)
    */
    public static Number alignToDoubleWordBoundary(Number address) {
        if (!wordAligned(address)) {
            if (isLt(0, address))
                address = add(address, compose(GenMath::sub, GenMath::rem, address, WORD_LENGTH_BYTES, 4));
            else
                address = sub(address, compose(GenMath::sub, GenMath::rem, address, WORD_LENGTH_BYTES, 4));
        }
        return address;
    }
   	
   ///////////////////////////////////////////////////////////////////////////
   //  ALL THE OBSERVABLE STUFF GOES HERE.  FOR COMPATIBILITY, Memory IS STILL 
   //  EXTENDING OBSERVABLE, BUT WILL NOT USE INHERITED METHODS.  WILL INSTEAD
   //  USE A COLLECTION OF MemoryObserver OBJECTS, EACH OF WHICH IS COMBINATION
   //  OF AN OBSERVER WITH AN ADDRESS RANGE.
   
   /**
    *  Method to accept registration from observer for any memory address.  Overrides
    *  inherited method.  Note to observers: this class delegates Observable operations
    *  so notices will come from the delegate, not the memory object.
    *  @param obs  the observer
    */
    public void addObserver(Observer obs) {
        try {  // split so start address always >= end address
            this.addObserver(obs, 0, getStackTable().getBaseAddress());
            this.addObserver(obs,kernelTextBlockTable.getBaseAddress(),  getStackTable().getBaseAddress());
        }
        catch (AddressErrorException aee) {
            System.out.println("Internal Error in Memory.addObserver: "+aee);
        }
    }
    
    /**
    *  Method to accept registration from observer for specific address.  This includes
    *  the memory word starting at the given address. Note to observers: this class delegates Observable operations
    *  so notices will come from the delegate, not the memory object.
    *  @param obs the observer
    *  @param addr the memory address which must be on word boundary
    */
    public void addObserver(Observer obs, Number addr) throws AddressErrorException {
        this.addObserver(obs, addr, addr);
    }
   
    /**
    *  Method to accept registration from observer for specific address range.  The
    *  last byte included in the address range is the last byte of the word specified
    *  by the ending address. Note to observers: this class delegates Observable operations
    *  so notices will come from the delegate, not the memory object.
    *
    *  @param obs  the observer
    *  @param startAddr the low end of memory address range, must be on word boundary
    *  @param endAddr the high end of memory address range, must be on word boundary
    */
    public void addObserver(Observer obs, Number startAddr, Number endAddr) throws AddressErrorException {
        if (!isEqz(GenMath.rem(startAddr, WORD_LENGTH_BYTES)))
            throw new AddressErrorException("address not aligned on word boundary ",
            Exceptions.ADDRESS_EXCEPTION_LOAD, startAddr);

        if (!isEq(endAddr, startAddr) && !isEqz(GenMath.rem(endAddr, WORD_LENGTH_BYTES)))
            throw new AddressErrorException("address not aligned on word boundary ",
            Exceptions.ADDRESS_EXCEPTION_LOAD, startAddr);

        // upper half of address space (above 0x7fffffff) has sign bit 1 thus is seen as
        // negative.
        if (!isLtz(startAddr) && isLtz(endAddr)) {
            throw new AddressErrorException("range cannot cross 0x8000000; please split it up",
                Exceptions.ADDRESS_EXCEPTION_LOAD, startAddr);
        }
        if (isLt(endAddr,startAddr)) {
            throw new AddressErrorException("end address of range < start address of range ",
                    Exceptions.ADDRESS_EXCEPTION_LOAD, startAddr);
        }
        observables.add(new MemoryObservable(obs, startAddr, endAddr));
    }

    /**
    * get the number of observers
    * @return the number of observers
    */
    public int countObservers() {
        return observables.size();
    }
   
    /**
    *  Remove specified memory observers
    *  @param obs  Observer to be removed
    */
    public void deleteObserver(Observer obs) {
        for (Object observable : observables) {
            ((MemoryObservable) observable).deleteObserver(obs);
        }
    }
   	
    /**
    *  Remove all memory observers
    */
    public void deleteObservers() {
         // just drop the collection
         observables = getNewMemoryObserversCollection();
    }
   	
    /**
    *  Overridden to be unavailable.  The notice that an Observer
    *  receives does not come from the memory object itself, but
    *  instead from a delegate.
    */
    public void notifyObservers() {
        throw new UnsupportedOperationException();
    }
   	
    /**
    *  Overridden to be unavailable.  The notice that an Observer
    *  receives does not come from the memory object itself, but
    *  instead from a delegate.
    *  @throws UnsupportedOperationException
    */
    public void notifyObservers(Object obj) {
        throw new UnsupportedOperationException();
    }


    private Collection getNewMemoryObserversCollection() {
     return new Vector();  // Vectors are thread-safe
    }
   		
    /////////////////////////////////////////////////////////////////////////
    // Private class whose objects will represent an observable-observer pair
    // for a given memory address or range.
    private class MemoryObservable extends Observable implements Comparable {
        private Number lowAddress, highAddress;

        public MemoryObservable(Observer obs, Number startAddr, Number endAddr) {
            lowAddress = startAddr;
            highAddress = endAddr;
            this.addObserver(obs);
        }

        public boolean match(Number address) {
            return !isLt(address, lowAddress) && !isEq(
                    GenMath.sub(highAddress, 1 + WORD_LENGTH_BYTES), address);
        }

        public void notifyObserver(MemoryAccessNotice notice) {
            this.setChanged();
            this.notifyObservers(notice);
        }

        // Useful to have for future refactoring, if it actually becomes worthwhile to sort
        // these or put 'em in a tree (rather than sequential search through list).
        public int compareTo(Object obj) {
            if (!(obj instanceof MemoryObservable))
                throw new ClassCastException();

            MemoryObservable mo = (MemoryObservable) obj;
            if (isLt(this.lowAddress, mo.lowAddress)
                    || isEq(this.lowAddress, mo.lowAddress) &&
                    isLt(this.highAddress, mo.highAddress))
                return -1;

            if (isLt(mo.lowAddress, this.lowAddress)
                    || isEq(this.lowAddress, mo.lowAddress) &&
                    isLt(mo.highAddress, this.highAddress))
                return -1;

            return 0;  // they have to be equal at this point.
        }
    }
   
   /*********************************  THE HELPERS  *************************************/

    ////////////////////////////////////////////////////////////////////////////////
    // Method to notify any observers of memory operation that has just occurred.
    // The "|| Globals.getGui()==null" is a hack added 19 July 2012 DPS.  IF MIPS simulation
    // is from command mode, Globals.program is null but still want ability to observe.
    private void notifyAnyObservers(int type, Number address, int length, Number value) {
        if ((Globals.program != null || Globals.getGui()==null) && this.observables.size() > 0) {
        Iterator it = this.observables.iterator();
        MemoryObservable mo;
            while (it.hasNext()) {
               mo = (MemoryObservable)it.next();
               if (mo.match(address))
                  mo.notifyObserver(new MemoryAccessNotice(type, address, length, value));
            }
        }
    }
       
   ///////////////////////////////////////////////////////////////////////   	
   // Read a program statement from the given address.  Address has already been verified
   // as valid.  It may be either in user or kernel text segment, as specified by arguments.  
   // Returns associated ProgramStatement or null if none. 
   // Last parameter controls whether or not observers will be notified.
       private ProgramStatement readProgramStatement(TextBlockTable textBlockTable, Number address, 
    		    boolean notify) {

    	   ProgramStatement statement = textBlockTable.readProgramStatement(address);
    	   if(statement != null) {
    		   if (notify) notifyAnyObservers(AccessNotice.READ, address, Instruction.INSTRUCTION_LENGTH,
            		   statement.getBinaryStatement());
    		   return statement;
    	   }
           if (notify) notifyAnyObservers(AccessNotice.READ, address, Instruction.INSTRUCTION_LENGTH,0);
           return null;
      }

      private class BlockTables extends ArrayList<BlockTable>{
    	      	  
    	  private BlockTables() {
    		  super(Arrays.asList(  
        				  (new BlockTable(BLOCK_TABLE_LENGTH,
        						  MemoryConfigurations.getCurrentConfiguration().getDataBaseAddress(),
                                  MemoryConfigurations.getCurrentConfiguration().getDataSegmentBaseAddress())), // data table
        				  (new BlockTable.DataBlockTable(BLOCK_TABLE_LENGTH,
        						  MemoryConfigurations.getCurrentConfiguration().getKernelDataBaseAddress())),   // kernel data table
        				  ( new StackBlockTable(-BLOCK_TABLE_LENGTH, 
        						  MemoryConfigurations.getCurrentConfiguration().getStackBaseAddress())), // stack table
        				  ( new BlockTable(MMIO_TABLE_LENGTH,
        						  MemoryConfigurations.getCurrentConfiguration().getMemoryMapBaseAddress())) // memory map table
        		));  
    	  }
    	  
    	  private boolean isInAnyBlockTable(Number address) {
    		  for(BlockTable table : this) {
    			  if(table.inSegment(address))
    		  		return true;
    		  }
    		  return false;
    	  }
      }
   }
