package mars.riscv.instructions.ecalls;

import mars.*;

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
 *  Interface for any RISCV ecall system service.  A qualifying service
 *   must be a class in the mars.riscv.instructions.ecalls package that
 *  implements the ecall interface, must be compiled into a .class file, 
 *  and its .class file must be in the same folder as ecall.class.
 *  Mars will detect a qualifying ecall upon startup, create an instance 
 *  using its no-argument constructor and add it to its ecall list.  
 *  When its service is invoked at runtime ("ecall" instruction
 *  with its service number stored in register a3), its simulate()
 *  method will be invoked.
 *
 */
public interface Ecall {

   /**
   *  Return a name you have chosen for this ecall.  This can be used by a MARS
   *  user to refer to the service when choosing to override its default service
   *  number in the configuration file.
   *  @return service name as a string
   */
   String getName();

   /**
   * Set the service number.  This is provided to allow MARS implementer or user
   * to override the default service number.
   * @param num specified service number to override the default.
   */
   void setNumber(int num);

   /**
   * Return the assigned service number.  This is the number the RISCV programmer
   * must store into a3 before issuing the ecall instruction.
   * @return assigned service number
   */
   int getNumber();

   /**
   * Performs ecall function.  It will be invoked when the service is invoked
   * at simulation time.  Service is identified by value stored in a3.
   * @param statement ProgramStatement for this ecall statement.
   */
   void simulate(ProgramStatement statement) throws ProcessingException;
}