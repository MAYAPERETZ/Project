   package mars.riscv.dump;

   import mars.Globals;
   import mars.ProgramStatement;
   import mars.riscv.hardware.AddressErrorException;
   import mars.riscv.hardware.memory.Memory;
   import mars.util.GenMath;
   import mars.util.Binary;
   import mars.util.Math2;

   import java.io.File;
   import java.io.FileOutputStream;
   import java.io.IOException;
   import java.io.PrintStream;

   import static mars.util.GenMath.add;
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
 *
 * Dump RISCV memory contents in Segment Window format.  Each line of
 * text output resembles the Text Segment Window or Data Segment Window
 * depending on which segment is selected for the dump.  Written
 * using PrintStream's println() method.  Each line of Text Segment
 * Window represents one word of text segment memory.  The line
 * includes (1) address, (2) machine code in hex, (3) basic instruction,
 * (4) source line.  Each line of Data Segment Window represents 8
 * words of data segment memory.  The line includes address of first
 * word for that line followed by 8 32-bit values.
 *
 * In either case, addresses and values are displayed in decimal or
 * hexadecimal representation according to the corresponding settings.
 *
 * @author Pete Sanderson 
 * @version January 2008
 */

/*
    FIXME: have not checked this class. Might work as it is,
            But have not made any meaningful changes but fixing unresolved files.
            Need to check and implement if necessary.
*/
public class SegmentWindowDumpFormat extends AbstractDumpFormat {
   
    /**
    *  Constructor.  There is no standard file extension for this format.
    */
    public SegmentWindowDumpFormat() {
        super("Text/Data Segment Window", "SegmentWindow", " Text Segment Window or Data Segment Window format to text file", null);
    }

   
    /**
    *  Write RISCV memory contents in Segment Window format.  Each line of
    *  text output resembles the Text Segment Window or Data Segment Window
    *  depending on which segment is selected for the dump.  Written
    *  using PrintStream's println() method.
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

        // If address in data segment, print in same format as Data Segment Window

        // If address in text segment, print in same format as Text Segment Window
        //           12345678901234567890123456789012345678901234567890
        //                    1         2         3         4         5
        try (out) {
            boolean hexAddresses = Globals.getSettings().getDisplayAddressesInHex();
            if (Memory.getInstance().getDataTable().inSegment(firstAddress)) {
                boolean hexValues = Globals.getSettings().getDisplayValuesInHex();
                Number offset = 0;
                String string = "";
                try {
                    for (Number address = firstAddress; !Math2.isLt(lastAddress, address);
                         address = add(address, Memory.WORD_LENGTH_BYTES)) {
                        if (Math2.isEqz(GenMath.rem(offset, Binary.sizeof(address))))
                            string = ((hexAddresses) ? Binary.currentNumToHexString(address) : Binary.toUnsignedNumber(address)) + "    ";

                        add(offset, 1);
                        Number temp = Globals.memory.getRawWordOrNull(address);
                        if (temp == null)
                            break;
                        string += ((hexValues) ? Binary.intToHexString(temp.intValue())
                                : ("           " + temp).substring(temp.toString().length())) + " ";
                        if (Math2.isEqz(GenMath.rem(offset, Binary.sizeof(address)))) {
                            out.println(string);
                            string = "";
                        }
                    }
                } finally {
                    out.close();
                }
                return;
            }
            if (!Memory.getInstance().getTextTable().inSegment(firstAddress))
                return;
            out.println(" Address    Code        Basic                     Source");
            out.println();
            String string;
            for (Number address = firstAddress; !Math2.isLt(lastAddress, address);
                 address = add(address, Memory.WORD_LENGTH_BYTES)) {
                string = ((hexAddresses) ? Binary.currentNumToHexString(address) : Binary.toUnsignedNumber(address)) + "  ";
                Number temp = Globals.memory.getRawWordOrNull(address);
                if (temp == null)
                    break;
                string += Binary.intToHexString(temp.intValue()) + "  ";
                try {
                    ProgramStatement ps = Globals.memory.getStatement(address);
                    string += (ps.getPrintableBasicAssemblyStatement() + "                      ").substring(0, 22);
                    string += (((ps.getSource().equals("")) ? "" : Integer.toString(ps.getSourceLine())) + "     ").substring(0, 5);
                    string += ps.getSource();
                } catch (AddressErrorException ignored) {
                }
                out.println(string);
            }
        }
    }


}