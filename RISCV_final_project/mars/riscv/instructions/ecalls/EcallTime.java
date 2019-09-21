package mars.riscv.instructions.ecalls;
import mars.*;
import mars.util.*;
import mars.riscv.hardware.*;

/*
Copyright (c) 2003-2007,  Pete Sanderson and Kenneth Vollmar

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
* Service to read a character from input console into a0.
*/
public class EcallTime extends AbstractEcall {
   /**
    * Build an instance of the Read Char ecall.  Default service number
    * is 12 and name is "ReadChar".
    */
    public EcallTime() {
        super(30, "Time");
    }
      
    /**
    * Performs ecall function to place current system time into a0 (low order 32 bits)
    * and a1 (high order 32 bits) in 32 bits architecture; in 64 places only in a0.
    */
    public void simulate(ProgramStatement statement) throws ProcessingException {
        long value = new java.util.Date().getTime();
        if (MemoryConfigurations.getCurrentComputingArchitecture() == 32) {
            RVIRegisters.updateRegister(10, Binary.lowOrderLongToInt(value)); // a0
            RVIRegisters.updateRegister(11, Binary.highOrderLongToInt(value)); // a1
        }
        else RVIRegisters.updateRegister(10, value);

    }
   
}