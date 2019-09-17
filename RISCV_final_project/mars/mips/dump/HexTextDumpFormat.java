package mars.mips.dump;

import mars.Globals;
import mars.mips.hardware.AddressErrorException;
import mars.mips.hardware.memory.Memory;
import mars.util.GenMath;
import mars.util.Math2;
import mars.venus.NumberDisplayBaseChooser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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
* Class that represents the "hexadecimal text" memory dump format.  The output
* is a text file with one word of MIPS memory per line.  The word is formatted
* using hexadecimal characters, e.g. 3F205A39.
* @author Pete Sanderson
* @version December 2007
*/
/*
    FIXME: have not checked this class. Might work as it is,
            But have not made any meaningful changes but fixing unresolved files.
            Need to check and implement if necessary.
 */
public class HexTextDumpFormat extends AbstractDumpFormat {
   
    /**
    *  Constructor.  There is no standard file extension for this format.
    */
    public HexTextDumpFormat() {
        super("Hexadecimal Text", "HexText", "Written as hex characters to text file", null);
    }
   
    /**
    *  Write RISCV memory contents in hexadecimal text format.  Each line of
    *  text contains one memory word written in hexadecimal characters.  Written
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
        PrintStream out = new PrintStream(new FileOutputStream(file));
        String string;
        try {
            for (Number address = firstAddress; !Math2.isLt(lastAddress, address); address = GenMath.add(address, Memory.WORD_LENGTH_BYTES)) {
                Number temp = Globals.memory.getRawWordOrNull(address);
                if (temp == null)
                    break;
                string = NumberDisplayBaseChooser.formatNumber(temp, 16);
                int i = 0;
                while (i++ < string.length())
                    string = '0' + string;
                out.println(string);
            }
        }
        finally {
            out.close();
        }
    }

}