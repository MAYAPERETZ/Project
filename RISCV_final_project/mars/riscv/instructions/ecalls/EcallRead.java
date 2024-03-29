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
 * Service to read from file descriptor given in $a0.  $a1 specifies buffer
 * and $a2 specifies length.  Number of characters read is returned in $v0.
 * (this was changed from $a0 in MARS 3.7 for SPIM compatibility.  The table
 * in COD erroneously shows $a0). *
 */
 
    public class EcallRead extends AbstractEcall {
   /**
    * Build an instance of the Read file syscall.  Default service number
    * is 14 and name is "Read".
    */
       public EcallRead() {
         super(14, "Read");
      }
      
   /**
   * Performs syscall function to read from file descriptor given in a0.  a1 specifies buffer
   * and a2 specifies length.  Number of characters read is returned in v0 (starting MARS 3.7).
   */
       public void simulate(ProgramStatement statement) throws ProcessingException {
         Number byteAddress = RVIRegisters.getValue(5); // destination of characters read from file
           int index = 0;
         byte myBuffer[] = new byte[RVIRegisters.getValue(6).intValue()]; // specified length
         // Call to SystemIO.xxxx.read(xxx,xxx,xxx)  returns actual length
         int retLength = SystemIO.readFromFile(
                                 RVIRegisters.getValue(4).intValue(), // fd
                                 myBuffer, // buffer
                                 RVIRegisters.getValue(6).intValue()); // length
         RVIRegisters.updateRegister(2, retLength); // set returned value in register
           
         // copy bytes from returned buffer into MARS memory
         try
         {
            while (index < retLength)
            {
               Globals.memory.setByte(GenMath.add(byteAddress,1),
                                        myBuffer[index++]);
            }
         } 
             catch (AddressErrorException e)
            {
               throw new ProcessingException(statement, e);
            }
      }
   }