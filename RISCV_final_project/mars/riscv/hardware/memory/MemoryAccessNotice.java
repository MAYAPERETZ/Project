package mars.riscv.hardware.memory;

import mars.riscv.hardware.AccessNotice;

/*
Copyright (c) 2003-2006,  Pete Sanderson and Kenneth Vollmar

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
 * Object provided to Observers of runtime access to MIPS memory.
 * Observer can get the access type (R/W), address and length in bytes (4,2,1).
 * @author Pete Sanderson 
 * @version July 2005
 */
 
public class MemoryAccessNotice extends AccessNotice {
    private Number address;
    private int length;
    private Number value;

    /** Constructor will be called only within this package, so assume
    *  address and length are in valid ranges.
     * @param type
     * @param address
     * @param length
     * @param value
    */
    MemoryAccessNotice(int type, Number address, int length, Number value) {
        super(type);
        this.address = address;
        this.length = length;
        this.value = value;
    }
    /** Constructor will be called only within this package, so assume
    *  address is in valid range.
    */
    public  MemoryAccessNotice(int type, Number address, Number value) {
        super(type);
        this.address = address;
        this.length = Memory.WORD_LENGTH_BYTES;
        this.value = value;
    }
    /** Fetch the memory address that was accessed. */
    public Number getAddress() {
        return address;
    }

    /** Fetch the value of the access operation (the value read or written). */
    public Number getValue() {
        return value;
    }

    /** String representation indicates access type, address and length in bytes */
    public String toString() {
        return ((this.getAccessType()==AccessNotice.READ) ? "R " : "W ") +
            "Mem " + address + " " + length + "B = "+value;
    }
}