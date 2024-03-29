package mars.riscv.dump;

import mars.Globals;
import mars.riscv.hardware.*;
import mars.riscv.hardware.memory.Memory;
import mars.util.Binary;
import mars.util.Math2;
import static mars.util.GenMath.*;
import java.io.*;
/*
Copyright (c) 2003-2008,  Pete Sanderson and Kenneth Vollmar

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
 * Class that represents the "binary" memory dump format.  The output 
 * is a binary file containing the memory words as a byte stream.  Output
 * is produced using PrintStream's write() method.
 * @author Pete Sanderson 
 * @version December 2007
 */

/*
    FIXME: have not checked this class. Might work as it is,
            But have not made any meaningful changes but fixing unresolved files.
            Need to check and implement if necessary.
 */
public class BinaryDumpFormat extends AbstractDumpFormat {

    /**
    *  Constructor.  There is no standard file extension for this format.
    */
    public BinaryDumpFormat() {
 super("Binary", "Binary", "Written as byte stream to binary file", null);
}

   
    /**
    *  Write RISCV memory contents in pure binary format.  One byte at a time
    *  using PrintStream's write() method.  Adapted by Pete Sanderson from
    *  code written by Greg Gibeling.
    *
    *  @param  file  File in which to store RISCV memory contents.
    *  @param firstAddress first (lowest) memory address to dump.  In bytes but
    *  must be on word boundary.
    *  @param lastAddress last (highest) memory address to dump.  In bytes but
    *  must be on word boundary.  Will dump the word that starts at this address.
    *  @throws AddressErrorException if firstAddress is invalid or not on a word boundary.
    *  @throws IOException if error occurs during file output.
    */
       
    @Override
    public void dumpMemoryRange(File file, Number firstAddress, Number lastAddress)
       throws AddressErrorException, IOException {
        PrintStream out = new PrintStream(new FileOutputStream(file));
        try {
            for (Number address = firstAddress; !Math2.isLt(lastAddress, address); address = add(address, Memory.WORD_LENGTH_BYTES)) {
               Number temp = Globals.memory.getRawWordOrNull(address);
               if (temp == null)
                  break;
                Number word = temp;
                for (int i = 0; i < Binary.sizeof(firstAddress); i++)
                  out.write(and(srl(word, sll(i, (Binary.sizeof(firstAddress)/8))), 0xFF).intValue());
            }
        }
        finally {
            out.close();
        }
    }

}