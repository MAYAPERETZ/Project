package mars.riscv.instructions.ecalls;

import mars.util.GenMath;
import mars.util.*;
import mars.riscv.hardware.*;
import mars.*;

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
* Service to write to file descriptor given in a0.  a1 specifies buffer
* and a2 specifies length.  Number of characters written is returned in a3
*
*/
 
public class EcallWrite extends AbstractEcall {

    /**
    * Build an instance of the Write file syscall.  Default service number
    * is 15 and name is "Write".
    */
    public EcallWrite() {
        super(15, "Write");
    }
      
    /**
    * Performs syscall function to write to file descriptor given in a0.  a1 specifies buffer
    * and a2 specifies length.  Number of characters written is returned in a3 tarting in MARS 3.7.
    */
    public void simulate(ProgramStatement statement) throws ProcessingException {
         Number byteAddress = RVIRegisters.getValue(5); // source of characters to write to file
         byte b;
         int reqLength = RVIRegisters.getValue(6).intValue(); // user-requested length
         int index = 0;
         byte myBuffer[] = new byte[RVIRegisters.getValue(6).intValue() + 1]; // specified length plus null termination
         try {
            b = (byte) Globals.memory.getByte(byteAddress);                            
            while (index < reqLength) // Stop at requested length. Null bytes are included.
                                 // while (index < reqLength && b != 0) // Stop at requested length OR null byte
            {
               myBuffer[index++] = b;
               GenMath.add(byteAddress, 1);
               b = (byte) Globals.memory.getByte(byteAddress);
            }
                              
            myBuffer[index] = 0; // Add string termination
         } // end try
             catch (AddressErrorException e)
            {
               throw new ProcessingException(statement, e);
            }
         int retValue = SystemIO.writeToFile(
                                 RVIRegisters.getValue(4).intValue(), // fd
                                 myBuffer, // buffer
                                 RVIRegisters.getValue(6).intValue()); // length
         RVIRegisters.updateRegister(13, retValue); // set returned value in register

         // Getting rid of processing exception.  It is the responsibility of the
			// user program to check the syscall's return value.  MARS should not
			// re-emptively terminate MIPS execution because of it.  Thanks to
			// UCLA student Duy Truong for pointing this out.  DPS 28-July-2009
         /*
         if (retValue < 0) // some error in opening file
         {
            throw new ProcessingException(statement,
                                    SystemIO.getFileErrorMessage());
         }
			*/
    }
}