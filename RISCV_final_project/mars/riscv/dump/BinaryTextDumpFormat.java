package mars.riscv.dump;

import mars.Globals;
import mars.riscv.hardware.*;
import mars.riscv.hardware.memory.Memory;
import mars.util.GenMath;
import mars.util.Math2;
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
 * Class that represents the "binary text" memory dump format.  The output 
 * is a text file with one word of MIPS memory per line.  The word is formatted
 * using '0' and '1' characters, e.g. 01110101110000011111110101010011.
 * @author Pete Sanderson 
 * @version December 2007
 */

/*
    FIXME: have not checked this class. Might work as it is,
            But have not made any meaningful changes but fixing unresolved files.
            Need to check and implement if necessary.
 */
public class BinaryTextDumpFormat extends AbstractDumpFormat {
   
    /**
    *  Constructor.  There is no standard file extension for this format.
    */
    public BinaryTextDumpFormat() {
        super("Binary Text", "BinaryText", "Written as '0' and '1' characters to text file", null);
    }
   
    /**
    *  Write MIPS memory contents in binary text format.  Each line of
    *  text contains one memory word written as 32 '0' and '1' characters.  Written
    *  using PrintStream's println() method.
    *  Adapted by Pete Sanderson from code written by Greg Gibeling.
    *
    *  @param  file  File in which to store MIPS memory contents.
    *  @param firstAddress first (lowest) memory address to dump.  In bytes but
    *  must be on word boundary.
    *  @param lastAddress last (highest) memory address to dump.  In bytes but
    *  must be on word boundary.  Will dump the word that starts at this address.
    *  @throws AddressErrorException if firstAddress is invalid or not on a word boundary.
    *  @throws IOException if error occurs during file output.
    */
    public void dumpMemoryRange(File file, Number firstAddress, Number lastAddress)
    throws AddressErrorException, IOException {
        try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
            String string;
            for (Number address = firstAddress; !Math2.isLt(lastAddress, address); address = GenMath.add(address, Memory.WORD_LENGTH_BYTES)) {
                Number temp = Globals.memory.getRawWordOrNull(address);
                if (temp == null)
                    break;
                string = Integer.toBinaryString(temp.intValue());
                while (string.length() < 32)
                    string = '0' + string;
                out.println(string);
            }
        }
    }

}