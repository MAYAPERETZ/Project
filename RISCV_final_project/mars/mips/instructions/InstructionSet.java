   package mars.mips.instructions;
   import mars.simulator.*;
   import mars.mips.hardware.*;
   import mars.mips.instructions.syscalls.*;
   import mars.*;
   import mars.util.*;
   import java.util.*;
   import java.io.*;
   import java.util.function.Function;

	/*
Copyright (c) 2003-2013,  Pete Sanderson and Kenneth Vollmar

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
 * The list of Instruction objects, each of which represents a MIPS instruction.
 * The instruction may either be basic (translates into binary machine code) or
 * extended (translates into sequence of one or more basic instructions).
 *
 * @author Pete Sanderson and Ken Vollmar
 * @version August 2003-5
 */

    public class InstructionSet
   {
      private ArrayList instructionList;
	  private ArrayList opcodeMatchMaps;
      private SyscallLoader syscallLoader;
    /**
     * Creates a new InstructionSet object.
     */
       public InstructionSet()
      {
         instructionList = new ArrayList();
      
      }
    /**
     * Retrieve the current instruction set.
     */
       public ArrayList getInstructionList()
      {
         return instructionList;
      
      }
    /**
     * Adds all instructions to the set.  A given extended instruction may have
     * more than one Instruction object, depending on how many formats it can have.
     * @see Instruction
     * @see BasicInstruction
     * @see ExtendedInstruction
     */
       public void populate()
      {
        /* Here is where the parade begins.  Every instruction is added to the set here.*/
      
        // ////////////////////////////////////   BASIC INSTRUCTIONS START HERE ////////////////////////////////
      
         instructionList.add(
                new BasicInstruction("nop",
            	 "Null operation : machine code is all zeroes",
                "000000 00000 00000 00000 00000 000000",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                  }
               }));
         
         // Adds the register x[rs2]  to register x[rs1] and writes the result to x[rd].
         // Arithmetic overflow is ignored.
         
         instructionList.add(
                new R_type.RVI("add t1,t2,t3",
            	 "Addition with overflow : set t1 to (t2 plus t3)",
                "0000000tttttsssss000fffff0110011",GenMath::add));
         
         // Subtracts the register x[rs2] from register x[rs1] and writes the result to x[rd].
         // Arithmetic overflow is ignored.
         
         instructionList.add(
                new R_type.RVI("sub t1,t2,t3",
            	 "Subtract. Subtracts the register t2 from register t3 and writes the result to t1",
                "0100000tttttsssss000fffff0110011", GenMath::sub));
         
       
         // Adds the sign-extended immediate to register x[rs1] and writes the result to x[rd].
         // Arithmetic overflow is ignored.
         
         instructionList.add(
                new I_type("addi t1,t2,-100",
            	 "Add Immediate. Adds the sign-extended(-100) to t2 and writes the result to t1.",
                "ttttttttttttsssss000fffff0010011",GenMath::add));

         instructionList.add(
                new R_M("mul t1,t2,t3",
            	 "Multiplication : Set hi to high-order 32 bits, lo to low-order 32 bits of the product of $t1 and $t2 (use mfhi to access hi, mflo to access lo)",
                "000", GenMath::mul));
         instructionList.add(
                 new R_M("mulh t1,t2,t3",
             	 "Multiplication : Set hi to high-order 32 bits, lo to low-order 32 bits of the product of $t1 and $t2 (use mfhi to access hi, mflo to access lo)",
                 "001", GenMath::mulh));
         
         instructionList.add(
                 new R_M("mulhsu t1,t2,t3",
             	 "Multiplication unsigned : Set HI to high-order 32 bits, LO to low-order 32 bits of the product of unsigned $t1 and $t2 (use mfhi to access HI, mflo to access LO)",
                 "010",GenMath::mulhsu));
         
         instructionList.add(
                new R_M("mulhu t1,t2,t3",
            	 "Multiplication unsigned : Set HI to high-order 32 bits, LO to low-order 32 bits of the product of unsigned $t1 and $t2 (use mfhi to access HI, mflo to access LO)",
                "011",GenMath::mulhu));

         instructionList.add(
                new R_M_DIV("div t1,t2,t3",
            	 "Division with overflow : Divide $t1 by $t2 then set LO to quotient and HI to remainder (use mfhi to access HI, mflo to access LO)",
                "100",GenMath::div));
         instructionList.add(
                new R_M_DIV("divu t1,t2,t3",
            	 "Division unsigned without overflow : Divide unsigned $t1 by $t2 then set LO to quotient and HI to remainder (use mfhi to access HI, mflo to access LO)",
                "101", GenMath::divu));
         
         instructionList.add(
                 new R_M_DIV("rem t1,t2,t3",
             	 "Division with overflow : Divide $t1 by $t2 then set LO to quotient and HI to remainder (use mfhi to access HI, mflo to access LO)",
                 "110", GenMath::rem));
         
         instructionList.add(
                 new R_M_DIV("remu t1,t2,t3",
             	 "Division unsigned without overflow : Divide unsigned $t1 by $t2 then set LO to quotient and HI to remainder (use mfhi to access HI, mflo to access LO)",
                 "111", (GenMath::remu)));

         instructionList.add(
                new R_type.RVI("and t1,t2,t3",
            	 "Bitwise AND : Set t1 to bitwise AND of t2 and t3",
                "0000000tttttsssss111fffff0110011", GenMath::and));
         
         instructionList.add(
                new R_type.RVI("or t1,t2,t3",
            	 "Bitwise OR : Set t1 to bitwise OR of t2 and t3",
                "0000000tttttsssss110fffff0110011", GenMath::or));
         
         instructionList.add(
                new I_type("andi t1,t2,100",
            	 "Bitwise AND immediate : Set t1 to bitwise AND of t2 and zero-extended 12-bit immediate",
                "ttttttttttttsssss111fffff0010011",GenMath::and));
         
         instructionList.add(
                new I_type("ori t1,t2,100",
            	 "Bitwise OR immediate : Set t1 to bitwise OR of t2 and zero-extended 12-bit immediate",
                "ttttttttttttsssss110fffff0010011",GenMath::or));

         instructionList.add(
                new R_type.RVI("xor t1,t2,t3",
            	 "Bitwise XOR (exclusive OR) : Set t1 to bitwise XOR of t2 and t3",
                "0000000tttttsssss100fffff0110011",GenMath::xor));
         
         instructionList.add(
                new I_type("xori t1,t2,100",
            	 "Exclusive-OR Immediate. Set t1 to bitwise XOR of t2 and zero-extended 12-bit immediate",            
                "ttttttttttttsssss100fffff0010011", GenMath::xor));		
         
         // Shifts register x[rs1] by x[rs2] bit positions. The vacated bits are filled with zeros and the result is written "
         // to x[rd]. The least-significant five bits of x[rs1](of six bits for RV64I) form the shift amount; the upper bits are ignored."
         
         instructionList.add(
                new R_type.RVI("sll t1,t2,t3",
                "Shift Left Logical Immediate. Shifts register t2 by t3 bit positions and the result is written "
                  + "\nto t1.",
                "0000000tttttsssss001fffff0110011",GenMath::sll));
         
         instructionList.add(
                 new I_typeShift("slli t1,t2,10",
             	 "Shift Left Logical Immediate. Shifts register t2 by 10 bit positions. The vacated bits are filled with zeros abd the result is written "
             	 + "\nto t1. For RV32I, the instruction is only legal when shmat[5] = 0",
                 "0000000tttttsssss001fffff0110011",GenMath::sll));
         
         // Shift Right Logical. Shifts register t2 right by t3 bits positions. 
         // The vacated bits are filled with zeros, and the result is written to x[rd].
         // The least-significant five bits of x[r2] (or six bits for RV64I) form the shift amount;
         // the upper bits are ignored.
         
         instructionList.add(
                new R_type.RVI("srl t1,t2,t3",
            	 "Shift Right Logical. Shifts register t2 right by t3 bits positions and writes the result to t1.",
                "0000000tttttsssss101fffff0110011",GenMath::srl));
         
         // Shift Right Logical. Shifts register t2 right by t3 bits positions. 
         // The vacated bits are filled with zeros, and the result is written to x[rd].
         // The least-significant five bits of x[r2] (or six bits for RV64I) form the shift amount;
         // the upper bits are ignored.
         
         instructionList.add(
                 new I_typeShift("srli t1,t2,offset",
             	 "Shift Right Logical Immediate. Shifts register t2 right by shmat bits positions and writes the result to t1.",
                 "0000000tttttsssss101fffff0010011",GenMath::srl));
                
         // Shifts register t2 right by t3 bit positions. 
         // The vacated bits are filled with copies of t2's most-significant bit, and the result is written to t1.
         // The least-significant five bits of t2 (or six in RV64I) form the shift amount; the upper bits are ignored.
      
         instructionList.add(
                new R_type.RVI("sra t1,t2,t3",
                "Shift Right Arithmetic. Shifts register t2 right by t3 bit positions.",	 
                "0100000tttttsssss101fffff0110011", GenMath::sra));
         
         // Shifts register t2 right by shmat bit positions. 
         // The vacated bits are filled with copies of t2's most-significant bit, and the result is written to t1.
         // The least-significant five bits of t2 (or six in RV64I) form the shift amount; the upper bits are ignored.
      
         
         instructionList.add(
                 new I_typeShift("srai t1,t2,10",
                 "Shift Right Arithmetic. Shifts register t2 right by 10 bit positions.",
                 "0100000tttttsssss101fffff0010011",GenMath::sra));


         // Loads four bytes from memory at address t1 + sign-extended(-100) and writes them to t2.
         // For RV64I, the result is sign-extended.
                
         instructionList.add(
                new I_type.LW_type("lw t2,-100(t1)",
            	 "Load Word. Loads four bytes from memory at address t1 + sign-extended(-100) and writes them to t2.",
                "ssssssssssssttttt010fffff0000011", Globals.memory::getWord));

         instructionList.add(
                new S_type("sw t1,-100(t2)",
                "Store Word. Store contents of t1 into effective memory word address",
                "tttttttsssssfffff010sssss0100011",Globals.memory::setWord, 0xffffffff));

         // Writes the sign-extended 20-bit immediate, left shifted by 12 bits, to x[rd], 
         // zeroing the lower 12 bits,
         
         instructionList.add(
                new U_type("lui t1, 100",
                "Load Upper Immediate. Writes the sign-extended 20-bit immediate, left shifted by 12 bits, to t1.",
                "ssssssssssssssssssssfffff0110111", 0));
         
         // Adds the sign-extended 20-bit immediate, left-shifted by 12 bits, to the pc, and writes the result to x[rd]
         
         instructionList.add(
                 new U_type("auipc t1, 1000",
                 "Add Upper Immediate to PC. Adds the sign-extended 20-bit immediate, left-shifted by 12 bits, to the pc, and writes the result to t1",
             	"ssssssssssssssssssssfffff0010111",RV32IRegisters.getProgramCounter()));
                
         instructionList.add(
                new B_type("beq t1, t2,offset",
                "Branch if equal : If register t1 equals register t2, set pc to the current pc plus the sign-extended offset",
                "tttttttsssssfffff000ttttt1100011", GenMath::eq));
         
         instructionList.add(new B_type.NegB_type("bne t1, t2, offset",
                "Branch if not equal : If register t1 does not equal register t2, set the pc to the current pc plus sign-extended offset",
             "tttttttsssssfffff001ttttt1100011", GenMath::eq));
         
         instructionList.add(
                new B_type.NegB_type("bge t1, t2,offset",
                "Branch If Greater Than or Equal : If register t1 is at least t2, treating the values as two's complement numbers, set the pc "
                + "\nto the current pc plus sign-extended offset",
                 "tttttttsssssfffff101ttttt1100011",GenMath::lt));
         instructionList.add(
                 new B_type.NegB_type("bgeu t1, t2,offset",
                 "Branch If Greater Than or Equal, Unsigned : If register t1 is at least t2, treating the values as unsigned numbers, set the pc "
                 + "\nto the current pc plus sign-extended offset",
                  "tttttttsssssfffff111ttttt1100011",GenMath::ltu));

         instructionList.add(
                new B_type("blt t1, t2,offset",
                "Branch If Less Than : if register t1 is laess than register t2, treating the values as two's complement numbers, set the pc to the current "
                + "\npc plus the sign-extended offset",
                 "tttttttsssssfffff100ttttt1100011", GenMath::lt));
         
         instructionList.add(
                 new B_type("bltu t1, t2,offset",
                 "Branch If Less Than, Unsigned : if register t1 is less than register t2, treating the values as unsigned numbers, set the pc to the current "
                 + "\npc plus the sign-extended offset",
                  "tttttttsssssfffff110ttttt1100011", GenMath::ltu));

         instructionList.add(
                 new R_type.RVI("slt t1,t2,t3",
                         "Set If Less Than. Compares t2 and t3 as two's complement numbers, and writes 1 to t1 if t2 is smaller, or 0 if not.",
                          "0000000tttttsssss010fffff0110011", GenMath::lt));
         instructionList.add(
                 new R_type.RVI("sltu t1,t2,t3",
                         "Set If Less Than, Unsigned. Compares t2 and t3 as unsigned numbers, and writes 1 to t1 if t2 is smaller, or 0 if not.",
                          "0000000tttttsssss011fffff0110011", GenMath::ltu));
           
         instructionList.add(
                new I_type("slti t1,t2,-100",
                "Set If Less Than Immediate. Compares t2 and the sign-extended immidiate as tow's complement numbers, and writes 1 to t1 if t2 is smaller, or 0 if not.",
                 "ttttttttttttsssss010fffff0110011", GenMath::lt));
         
         instructionList.add(
                 new I_type("sltiu t1,t2,-100",
                         "Set If Less Than Immediate, Unsigned. Compares t2 and the sign-extended immidiate as unsigned numbers, and writes 1 to t1 if t2 is smaller, or 0 if not.",
                          "ttttttttttttsssss011fffff0110011", GenMath::ltu));
         /*
         instructionList.add(
                new BasicInstruction("break 100", 
            	 "Break execution with code : Terminate program execution with specified exception code",
            	 
                "000000 ffffffffffffffffffff 001101",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {  // At this time I don't have exception processing or trap handlers
                     // so will just halt execution with a message.
                     int[] operands = statement.getOperands();
                     throw new ProcessingException(statement, "break instruction executed; code = "+
                          operands[0]+".", Exceptions.BREAKPOINT_EXCEPTION);
                  }
               }));	
         instructionList.add(
                new BasicInstruction("break", 
            	 "Break execution : Terminate program execution with exception",
            	 
                "000000 00000 00000 00000 00000 001101",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {  // At this time I don't have exception processing or trap handlers
                     // so will just halt execution with a message.
                     throw new ProcessingException(statement, "break instruction executed; no code given.",
                        Exceptions.BREAKPOINT_EXCEPTION);
                  }
               }));	
               */				
         instructionList.add(
                new BasicInstruction("syscall", 
            	 "Issue a system call : Execute the system call specified by value in $v0",
            	 
                "000000 00000 00000 00000 00000 001100",
                        statement -> findAndSimulateSyscall(RV32IRegisters.getValue(2).intValue(),statement)));

         instructionList.add(
                new J_type("jal t1, target",
                "Jump and link. Writes the address of the next instruction (pc + 4) to t1, then set the pc to the current pc plus the sign-extended offset."
                + "\nIf rd is omitted, x1 (ra) is assumed.",
                "ssssssssssssssssssssfffff1101111",
                        statement -> {
                           Number[] operands = statement.getOperands();
                           processReturnAddress(operands[0]);//RV32IRegisters.updateRegister(operands[0], RV32IRegisters.getProgramCounter());
                           processJump(GenMath.sub(GenMath.add(
                              RV32IRegisters.getProgramCounter(), operands[1])
                              ,Instruction.INSTRUCTION_LENGTH));
                        }));
         

         // Sets the pc to x[rs1] + sign-extend(offset), masking off the least-significant bit if the computed
         // address, then writes the previous pc+4 to x[rd]. If x[rd] is omitted, x1 (ra) is assumed.
         
         instructionList.add(
                new I_type.LW_type("jalr t1,-100(t2)",
                "Jump and link register. Sets the pc to t2 + sign-extend(4), masking off the least-significant bit of the computed"
                + "\naddress, then writes the previous pc+4 to t1. If rd is ommitted, x1 (ra) is assumed.",
                "ssssssssssssttttt000fffff1100111",
                        statement -> {
                           Number[] operands = statement.getOperands();
                           processReturnAddress(operands[0]);
                           processJump(
                                   GenMath.and(GenMath.add(RV32IRegisters.getValue(operands[2]),
                                   operands[1]), (~1)));
                        }));
       
         // Loads a byte from memory at address x[rs1] + sign-extend(offset) and writes it to x[rd], sign-extending the result.
         
         instructionList.add(
                new I_type.LW_type("lb t1,-100(t2)",
                "Load byte. Loads a byte from memory at address t2 + sign-extend(100) and writes it to t1.",
                "ttttttttttttsssss000fffff0000011",Globals.memory::getByte));
         
         instructionList.add(
                new I_type.LW_type("lh t1,-100(t2)",
                "Load Halfword. Loads two bytes from memory at address t2 + sign-extend(100) and writes them to t1, sign-extending the result.",
                 "ssssssssssssttttt001fffff0000011",Globals.memory::getHalf));
         instructionList.add(
                new I_type.LW_type("lhu t1,-100(t2)",
                	"Load Halfword, Unsigned. Loads two bytes from memory at address t2 + sign-extend(100) and writes them to t1, zero-extending the result.",
                        "ssssssssssssttttt101fffff0000011",Globals.memory::getHalf, 0x0ffff));
         
         instructionList.add(
                new I_type.LW_type("lbu t1,-100(t2)",
                "Load byte, Unsigned. Loads a byte from memory at address t2 + sign-extend(100) and writes them to t1, zero-extending the result.",
                 "ssssssssssssttttt100fffff0000011",Globals.memory::getByte, 0x0ff));
         
         instructionList.add(
                new S_type("sb t2,-100(t1)",
                "Store byte. Store the least-significant byte in register t2 to memory at address t1 + sign-extend(-100)",
                "tttttsssssfffff000ttttt0100011",Globals.memory::setByte, 0xff));
         instructionList.add(
                new S_type("sh t1,-100(t2)",
                "Store Halfword. Store the two least-significant bytes in register t2 to memory at address t1 + sign-extend(-100)",
            	 "tttttsssssfffff000ttttt0100011",Globals.memory::setHalf, 0xffff));


          /************************************************************************************************************
           *                            RV32/64F Instructions Start Here
           ***********************************************************************************************************/

         instructionList.add(
                new R_type.WithRmField("fadd.s ft1,ft2,ft3",
                "Floating-point Add, Single-Precision. Set ft1 to single-precision floating point value of ft2 plus ft3", 
                "0000000tttttsssssxxxfffff1010011",
                        statement -> {
                           Number[] operands = statement.getOperands();
                           float add1 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                           float add2 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[2]));
                           float sum = add1 + add2;
                           // overflow detected when sum is positive or negative infinity.
                           if (Float.isInfinite(sum)) {
                               Coprocessor1.updateRegister(operands[0], Coprocessor1.round(sum));

                               throw new FloatingPointException(statement,"Floating-point Arithmetic Overflow",
                                  Exceptions.FLOATING_POINT_OVERFLOW);
                           }

                           Coprocessor1.updateRegisterWithExecptions(operands[0], Coprocessor1.round(sum), statement);

                        }));
         
         instructionList.add(
                new R_type.WithRmField("fsub.s ft1,ft2,ft3",
                "Floating-point Subtract, Single-Precision. Set ft1 to single-precision floating point value of ft2  minus ft3",
                "0000100tttttsssssxxxfffff1010011",
                        statement -> {
                           Number[] operands = statement.getOperands();
                           float sub1 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                           float sub2 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[2]));
                           float diff = sub1 - sub2;
                           if(Coprocessor1.isUnderflow(diff, Coprocessor1.getFclass(diff))) {
                               throw new FloatingPointException(statement,"Floating-point Arithmetic Underflow",
                                       Exceptions.FLOATING_POINT_UNDERFLOW);
                           }
                           else
                               Coprocessor1.updateRegisterWithExecptions(operands[0].intValue(), Coprocessor1.round(diff), statement);

                        }));
         instructionList.add(
                new R_type.WithRmField("fmul.s ft1,ft2,ft3",
                "Floating-point Multiply, Single-Precision. Set ft1 to single-precision floating point value of ft2 times ft3",
                "0001100tttttsssssxxxfffff1010011",
                        statement -> {
                           Number[] operands = statement.getOperands();
                           float mul1 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                           float mul2 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[2]));
                           float prod = mul1 * mul2;
                           Coprocessor1.updateRegisterWithExecptions(operands[0], Coprocessor1.round(prod), statement);
                        }));
         instructionList.add(
                new R_type.WithRmField("fdiv.s ft1,ft2,ft3",
                "Floating-point Divide, Single-Precision. Set ft1 to single-precision floating point value of ft2 divided by ft3",
                "0001000tttttsssssxxxfffff1010011",
                        statement -> {
                           Number[] operands = statement.getOperands();
                           float div1 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                           float div2 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[2]));
                           if(div2 == 0){
                               throw new FloatingPointException(statement, "Divide by Zero Exception: ",
                                       Exceptions.DIVIDE_BY_ZERO_EXCEPTION);
                           }
                           else {
                               float quot = div1 / div2;
                               Coprocessor1.updateRegisterWithExecptions(operands[0], Coprocessor1.round(quot), statement);
                           }
                       }));
         instructionList.add(
                new BasicInstruction("fsqrt.s ft1,ft2",
            	 "Floating-point Square Root, Single-Precision. Set ft1 to single-precision floating point square root of ft2",
                 "010110000000sssssxxxfffff1010011",
                        statement -> {
                           Number[] operands = statement.getOperands();
                           float value = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                           int floatSqrt = 0;
                           if ((Coprocessor1.getIntValue(operands[1])&0x80000000) == 1 ) {
                               // doesn't check ifNaN as well, since if it is, it will raise an exception as it is
                              floatSqrt = Float.floatToIntBits( Float.NaN);
                              Coprocessor1.updateRegister(operands[0], Coprocessor1.round(floatSqrt));
                             // throw new ProcessingException(statement, "Invalid Operation: sqrt of negative number");
                           }
                           else {
                              floatSqrt = Float.floatToIntBits( (float) Math.sqrt(value));
                           }
                           Coprocessor1.updateRegisterWithExecptions(operands[0], Coprocessor1.round(floatSqrt), statement);
                         }));

         instructionList.add(
                new I_type.LW_type("flw f1,-100(t2)",
                "Load word into Coprocessor 1 (FPU) : Set f1 to 32-bit value from effective memory word address",
                "ssssssssssssttttt010fffff0000111",
                        statement -> {
                           Number[] operands = statement.getOperands();
                           try
                           {
                              Coprocessor1.updateRegister(operands[0],
                                  Globals.memory.getWord(
                                  RV32IRegisters.getValue(operands[2]).intValue() + operands[1].intValue()
                                  ).intValue());
                           }
                               catch (AddressErrorException e)
                              {
                                 throw new ProcessingException(statement, e);
                              }
                        }));
         
         instructionList.add(
                new S_type("fsw ft1,-100(t2)",
            	 "Floating-point store word. Stores the single-precision floating point number in register f[rs2] "
                		+ "to memory at address x[rs1]+sign-extend(offset)",
                 "tttttttsssssfffff010sssss01001 11",
                        statement -> {
                           Number[] operands = statement.getOperands();
                           try
                           {
                              Globals.memory.setWord(
                                  RV32IRegisters.getValue(operands[2]).intValue() + operands[1].intValue(),
                                  Coprocessor1.getIntValue(operands[0]));
                           }
                               catch (AddressErrorException e)
                              {
                                 throw new ProcessingException(statement, e);
                              }
                        }));
         instructionList.add(
                 new R_type("fsgnj.s f1,f2,f3",
                 "Floating-point Sign Inject, Single-Presicion.",
                 "0010001tttttsssss000fffff1010011",
                         statement -> {
                            Number[] operands = statement.getOperands();

                            int firstOperand = Coprocessor1.getIntValue(operands[1]);
                            int secondOperand = Coprocessor1.getIntValue(operands[2]);
                            int newspfp = ((firstOperand&0x80000000)|(secondOperand&0x7fffffff));
                            Coprocessor1.updateRegister(operands[0].intValue(), newspfp);

                         }));
         instructionList.add(
                 new R_type("fsgnjn.s ft1,ft2,ft3",
                 "Floating-point Sign Inject-Negate, Single-Presicion.",
                 "0010000tttttsssss001fffff1010011",
                         statement -> {
                            Number[] operands = statement.getOperands();

                            int firstOperand = Coprocessor1.getIntValue(operands[1]);
                            int secondOperand = Coprocessor1.getIntValue(operands[2]);
                            int newspfp = (((~firstOperand)&0x80000000)|(secondOperand&0x7fffffff));
                            Coprocessor1.updateRegister(operands[0].intValue(), newspfp);

                         }));
         instructionList.add(
                 new R_type("fsgnjx.s ft1,ft2,ft3",
                 "Floating-point Sign Inject-Xor, Single-Presicion.",
                 "0010000tttttsssss010fffff1010011",
                         statement -> {
                           Number[] operands = statement.getOperands();

                           int firstOperand = Coprocessor1.getIntValue(operands[1]);
                           int secondOperand = Coprocessor1.getIntValue(operands[2]);
                           int newspfp = (((firstOperand^secondOperand)&0x80000000)|(secondOperand&0x7fffffff));
                           Coprocessor1.updateRegister(operands[0], newspfp);

                        }));
         instructionList.add(
                 new R_type("fmin.s ft1,ft2,ft3",
                 "Floating-point Minimum, Single-Presicion.",
                 "0010100tttttsssss000fffff1010011",
                         statement -> {
                           Number[] operands = statement.getOperands();

                           Coprocessor1.updateRegister(operands[0],
                                   Integer.min(operands[1].intValue(), operands[2].intValue()));

                        }));
         instructionList.add(
                 new R_type("fmax.s ft1,ft2,ft3",
                 "Floating-point Maximum, Single-Presicion.",
                 "0010100tttttsssss001fffff1010011",
                         statement -> {
                           Number[] operands = statement.getOperands();

                           Coprocessor1.updateRegister(operands[0],
                                   Integer.max(operands[1].intValue(), operands[2].intValue()));

                        }));

         instructionList.add(
                 new R_type.WithTwoOperands("fmv.x.w t1,ft2",
                 "Floating-point Move Word to Integer, Single-Presicion.",
                 "111100000000sssss000fffff1010011",
                         e->e, Coprocessor1::getIntValue, RV32IRegisters::updateRegister));

         
         instructionList.add(
                 new R_type("feq.s t1,ft2,ft3",
                 "Floating-point Equals, Single-Presicion.",
                 "1010000tttttsssss010fffff1010011",
                         statement -> {
                           Number[] operands = statement.getOperands();

                           int equals = (Binary.highOrderLongToInt(operands[1]) ==
                                   Binary.highOrderLongToInt(operands[2]))? 1 : 0;
                               Coprocessor1.updateRegister(operands[0], equals);

                        }));

         instructionList.add(
                 new R_type("flt.s t1,ft2,ft3",
                 "Floating-point Less Than, Single-Presicion.",
                 "1010000tttttsssss001fffff1010011",
                         statement -> {
                           Number[] operands = statement.getOperands();

                           int equals = (Binary.highOrderLongToInt(operands[1])
                                   < Binary.highOrderLongToInt(operands[2]))? 1 : 0;
                               Coprocessor1.updateRegister(operands[0], equals);

                        }));

         instructionList.add(
                 new R_type("fle.s t1,ft2,ft3",
                 "Floating-point Less Than or Equal, Single-Presicion.",
                 "1010000tttttsssss000fffff1010011",
                         statement -> {
                           Number[] operands = statement.getOperands();

                           int equals = (Binary.highOrderLongToInt(operands[1]) <=
                                   Binary.highOrderLongToInt(operands[2]))? 1 : 0;
                               Coprocessor1.updateRegister(operands[0], equals);

                        }));
         
         instructionList.add(
                 new R_type("fclass.s t1,ft2",
                 "Floating-point Classify, Single-Precision. ", 
                 "111000000000sssss001fffff1010011",
                         statement -> {
                           Number[] operands = statement.getOperands();
                           float rs1 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                           Coprocessor1.updateRegister(operands[0], Coprocessor1.getFclass(rs1));
                        }));


                /*
                      TODO: FCVT.W.S, FCVT.WU.S, FCVT.S.W, FCVT.S.WU
                */

          /************************************************************************************************************
           *                                THE RV64F ONLY INSTRUCTIONS
           ***********************************************************************************************************/

                /*
                      TODO: FCVT.L.S, FCVT.LU.S, FCVT.S.L, FCVT.S.LU
                */


          /************************************************************************************************************
           *                                THE RV32/64D INSTRUCTIONS
           ***********************************************************************************************************/

         instructionList.add(// no printed reference, got opcode from SPIM
                new I_type.LW_type("fld ft1,-100(t1)",
            	 "Load double word Coprocessor 1 (FPU)) : Set ft1 to 64-bit value from effective memory doubleword address",
                "ttttttttttttsssss011fffff0000111",
                        statement -> {
                           Number[] operands = statement.getOperands();
                            // IF statement added by DPS 13-July-2011.
                           if (!Globals.memory.doublewordAligned(GenMath.add(RV32IRegisters.getValue(operands[2]),
                                   Binary.signExtend(operands[1], 12, 32)))) {
                              throw new ProcessingException(statement,
                                 new AddressErrorException("address not aligned on doubleword boundary ",
                                 Exceptions.ADDRESS_EXCEPTION_LOAD, GenMath.add(RV32IRegisters.getValue(operands[2]),
                                 Binary.signExtend(operands[1], 12, 64))));
                           }

                           try
                           {
                              Coprocessor1.updateRegister(operands[0],
                                  Globals.memory.getDoubleWord(
                                  (RV32IRegisters.getValue(GenMath.add(operands[2]
                                          , Binary.signExtend(operands[1], 12, 64).longValue())))));
                           }
                               catch (AddressErrorException e)
                              {
                                 throw new ProcessingException(statement, e);
                              }
                        }));
        	 
         
         instructionList.add( // no printed reference, got opcode from SPIM
                new S_type("fsd ft1,-100(t1)",
            	 "Store double word from Coprocessor 1 (FPU)) : Store 64 bit value in ft1 to effective memory doubleword address",
                 "tttttttsssssfffff011sssss0100111",
                        statement -> {
                           Number[] operands = statement.getOperands();
                            // IF statement added by DPS 13-July-2011.
                           if (!Globals.memory.doublewordAligned(GenMath.add(RV32IRegisters.getValue(operands[2]),
                                   Binary.signExtend(operands[1], 12, 64)))) {
                              throw new ProcessingException(statement,
                                 new AddressErrorException("address not aligned on doubleword boundary ",
                                 Exceptions.ADDRESS_EXCEPTION_STORE, GenMath.add(RV32IRegisters.getValue(operands[2]),
                                 Binary.signExtend(operands[1].longValue(), 12, 32))));
                           }
                           try
                           {
                              Globals.memory.setDoubleWord(
                                  (GenMath.add(RV32IRegisters.getValue(operands[2]),
                                          Binary.signExtend(operands[1], 12, 64))),
                                  Coprocessor1.getIntValue(operands[0]));
                           }
                               catch (AddressErrorException e)
                              {
                                 throw new ProcessingException(statement, e);
                              }
                        }));
         
         instructionList.add(
                 new R_type("fsgnj.d f1,f2,f3",
                 "Floating-point Sign Inject, Double-Presicion.",
                 "0010001tttttsssss000fffff1010011",
                         statement -> {
                            Number[] operands = statement.getOperands();
                            long firstOperand = Coprocessor1.getLongValue(operands[1]);
                            long secondOperand = Coprocessor1.getLongValue(operands[2]);
                            long newspfp = ((firstOperand&0x8000000000000000L)|(secondOperand&0x7fffffffffffffffL));
                            Coprocessor1.updateRegister(operands[0], newspfp);

                         }));

         instructionList.add(
                 new R_type("fsgnjn.d ft1,ft2,ft3",
                 "Floating-point Sign Inject-Negate, Double-Presicion.",
                 "0010001tttttsssss001fffff1010011",
                         statement -> {
                              Number[] operands = statement.getOperands();
                              long firstOperand = Coprocessor1.getLongValue(operands[1]);
                              long secondOperand = Coprocessor1.getLongValue(operands[2]);
                              long newspfp = ((~(firstOperand&0x8000000000000000L))|(secondOperand&0x7fffffffffffffffL));
                              Coprocessor1.updateRegister(operands[0], newspfp);
                         }));

         instructionList.add(
                 new R_type("fsgnjx.d ft1,ft2,ft3",
                 "Floating-point Sign Inject-Xor, Double-Presicion.",
                 "0010001tttttsssss010fffff1010011",
                         statement -> {
                            Number[] operands = statement.getOperands();
                            long firstOperand = Coprocessor1.getLongValue(operands[1]);
                            long secondOperand = Coprocessor1.getLongValue(operands[2]);
                            long newspfp = (((firstOperand^secondOperand)&0x8000000000000000L)|(secondOperand&0x7fffffffffffffffL));
                            Coprocessor1.updateRegister(operands[0], newspfp);

                         }));

         instructionList.add(
                 new R_type("fmin.d ft1,ft2,ft3",
                 "Floating-point Minimum, Double-Presicion.",
                 "0010101tttttsssss000fffff1010011",
                         statement -> {
                            Number[] operands = statement.getOperands();

                            Coprocessor1.updateRegister(operands[0],
                                    Long.min(operands[1].longValue(), operands[2].longValue()));

                         }));


         instructionList.add(
                 new R_type("fmax.d ft1,ft2,ft3",
                 "Floating-point Maximum, Double-Presicion.",
                 "0010101tttttsssss001fffff1010011",
                         statement -> {
                            Number[] operands = statement.getOperands();

                            Coprocessor1.updateRegister(operands[0],
                                    Long.max(operands[1].longValue(), operands[2].longValue()));

                         }));

         instructionList.add(
                 new R_type("feq.d t1,ft2,ft3",
                 "Floating-point Equals, Double-Presicion.",
                 "1010001tttttsssss010fffff1010011",
                         statement -> {
                            Number[] operands = statement.getOperands();

                            int equals = (operands[1].doubleValue() == operands[2].doubleValue())? 1 : 0;
                                Coprocessor1.updateRegister(operands[0], equals);

                         }));

         instructionList.add(
                 new R_type("flt.d t1,ft2,ft3",
                 "Floating-point Less Than, Double-Presicion.",
                 "1010001tttttsssss001fffff1010011",
                         statement -> {
                            Number[] operands = statement.getOperands();

                            int equals = (operands[1].doubleValue() < operands[2].doubleValue())? 1 : 0;
                                Coprocessor1.updateRegister(operands[0], equals);

                         }));

         instructionList.add(
                 new R_type("fle.d t1,ft2,ft3",
                 "Floating-point Less Than or Equal, Single-Presicion.",
                 "1010001tttttsssss000fffff1010011",
                         statement -> {
                            Number[] operands = statement.getOperands();

                            int equals = (operands[1].doubleValue() <= operands[2].doubleValue())? 1 : 0;
                                Coprocessor1.updateRegister(operands[0], equals);

                         }));
         
         instructionList.add(
                 new R_type("fclass.d ft1,ft2",
                 "Floating-point Classify, Double-Precision. ", 
                 "111000100000sssss001fffff1010011",
                         statement -> {
                            Number[] operands = statement.getOperands();
                            double rs1 = Double.longBitsToDouble(Coprocessor1.getIntValue(operands[1]));
                            Coprocessor1.updateRegister(operands[0], Coprocessor1.getFclass(rs1));
                         }));

          instructionList.add(
                  new R_type.WithRmField("fadd.d ft1,ft2,ft3",
                          "Floating-point Add, Single-Precision. Set ft1 to single-precision floating point value of ft2 plus ft3",
                          "0000001tttttsssssxxxfffff1010011",
                          statement -> {
                              Number[] operands = statement.getOperands();
                              double add1 = Double.longBitsToDouble(Coprocessor1.getLongValue(operands[1]));
                              double add2 = Double.longBitsToDouble(Coprocessor1.getLongValue(operands[2]));
                              double sum = add1 + add2;
                              // overflow detected when sum is positive or negative infinity.
                              if (Double.isInfinite(sum)) {
                                  Coprocessor1.updateRegister(operands[0], Coprocessor1.round(sum));
                                  throw new FloatingPointException(statement,"Floating-point Arithmetic Overflow",
                                          Exceptions.FLOATING_POINT_OVERFLOW);
                              }

                              Coprocessor1.updateRegisterWithExecptions(operands[0], Coprocessor1.round(sum), statement);
                          }));
                /*
                    TODO: FCVT.S.D, FCVT.D.S, FMV.W.D, FCVT.WU.D, FCVT.D.W, FMV.D.WU
                */

          /************************************************************************************************************
           *                            RV64D ONLY Instructions Start Here
           ***********************************************************************************************************/

              /*
                  TODO: FCVT.L.D, FCVT.LU.D, FMV.X.D, FCVT.D.L, FCVT.D.LU, FMV.D.X
                    instructionList.add(
                          new R_type.WithTwoOperands("fmv.x.d t1,ft2",
                                  "Floating-point Move Word to Integer, Single-Presicion.",
                                  "111100000000sssss000fffff1010011",
                                  e->e, Coprocessor1::getIntValue, RV32IRegisters::updateRegister));

              */





      	////////////////////////////  THE TRAP INSTRUCTIONS & ERET  ////////////////////////////
        /* 
         instructionList.add(
                new BasicInstruction("teq $t1,$t2",
                "Trap if equal : Trap if $t1 is equal to $t2",
            	 
                "000000 fffff sssss 00000 00000 110100",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[0]) == RV32IRegisters.getValue(operands[1]))
                     {
                        throw new ProcessingException(statement,
                            "trap",Exceptions.TRAP_EXCEPTION);
                     } 	                     
                  }
               }));
         
         
         instructionList.add(
                new BasicInstruction("teqi $t1,-100",
            	 "Trap if equal to immediate : Trap if $t1 is equal to sign-extended 16 bit immediate",
                
                "000001 fffff 01100 ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[0]) == (operands[1] << 16 >> 16)) 
                     {
                        throw new ProcessingException(statement,
                            "trap",Exceptions.TRAP_EXCEPTION);
                     }                
                  }
               }));
         instructionList.add(
                new BasicInstruction("tne $t1,$t2",
                "Trap if not equal : Trap if $t1 is not equal to $t2",
            	 
                "000000 fffff sssss 00000 00000 110110",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[0]) != RV32IRegisters.getValue(operands[1]))
                     {
                        throw new ProcessingException(statement,
                            "trap",Exceptions.TRAP_EXCEPTION);
                     }                      
                  }
               }));        
         instructionList.add(
                new BasicInstruction("tnei $t1,-100",
            	 "Trap if not equal to immediate : Trap if $t1 is not equal to sign-extended 16 bit immediate",
                
                "000001 fffff 01110 ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[0]) != (operands[1] << 16 >> 16)) 
                     {
                        throw new ProcessingException(statement,
                            "trap",Exceptions.TRAP_EXCEPTION);
                     }                     
                  }
               }));
         instructionList.add(
                new BasicInstruction("tge $t1,$t2",
                "Trap if greater or equal : Trap if $t1 is greater than or equal to $t2",
            	 
                "000000 fffff sssss 00000 00000 110000",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[0]) >= RV32IRegisters.getValue(operands[1]))
                     {
                        throw new ProcessingException(statement,
                            "trap",Exceptions.TRAP_EXCEPTION);
                     } 	                     
                  }
               }));
         instructionList.add(
                new BasicInstruction("tgeu $t1,$t2",
                "Trap if greater or equal unsigned : Trap if $t1 is greater than or equal to $t2 using unsigned comparision",
            	 
                "000000 fffff sssss 00000 00000 110001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     int first = RV32IRegisters.getValue(operands[0]);
                     int second = RV32IRegisters.getValue(operands[1]);
                  	// if signs same, do straight compare; if signs differ & first negative then first greater else second
                     if ((first >= 0 && second >= 0 || first < 0 && second < 0) ? (first >= second) : (first < 0) ) 
                     {
                        throw new ProcessingException(statement,
                            "trap",Exceptions.TRAP_EXCEPTION);
                     }                      
                  }
               }));
         instructionList.add(
                new BasicInstruction("tgei $t1,-100",
            	 "Trap if greater than or equal to immediate : Trap if $t1 greater than or equal to sign-extended 16 bit immediate",
                
                "000001 fffff 01000 ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[0]) >= (operands[1] << 16 >> 16)) 
                     {
                        throw new ProcessingException(statement,
                            "trap",Exceptions.TRAP_EXCEPTION);
                     }                    
                  }
               }));
         instructionList.add(
                new BasicInstruction("tgeiu $t1,-100",
                "Trap if greater or equal to immediate unsigned : Trap if $t1 greater than or equal to sign-extended 16 bit immediate, unsigned comparison",
            	 
                "000001 fffff 01001 ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int first = RV32IRegisters.getValue(operands[0]);
                     // 16 bit immediate value in operands[1] is sign-extended
                     int second = operands[1] << 16 >> 16;
                  	// if signs same, do straight compare; if signs differ & first negative then first greater else second
                     if ((first >= 0 && second >= 0 || first < 0 && second < 0) ? (first >= second) : (first < 0) ) 
                     {
                        throw new ProcessingException(statement,
                            "trap",Exceptions.TRAP_EXCEPTION);
                     }                
                  }
               }));
         instructionList.add(
                new BasicInstruction("tlt $t1,$t2",
                "Trap if less than: Trap if $t1 less than $t2",
            	 
                "000000 fffff sssss 00000 00000 110010",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[0]) < RV32IRegisters.getValue(operands[1]))
                     {
                        throw new ProcessingException(statement,
                            "trap",Exceptions.TRAP_EXCEPTION);
                     } 	                     
                  }
               }));
         instructionList.add(
                new BasicInstruction("tltu $t1,$t2",
                "Trap if less than unsigned : Trap if $t1 less than $t2, unsigned comparison",
            	 
                "000000 fffff sssss 00000 00000 110011",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     int first = RV32IRegisters.getValue(operands[0]);
                     int second = RV32IRegisters.getValue(operands[1]);
                  	// if signs same, do straight compare; if signs differ & first positive then first is less else second
                     if ((first >= 0 && second >= 0 || first < 0 && second < 0) ? (first < second) : (first >= 0) ) 
                     {
                        throw new ProcessingException(statement,
                            "trap",Exceptions.TRAP_EXCEPTION);
                     }                    
                  }
               }));
         instructionList.add(
                new BasicInstruction("tlti $t1,-100",
            	 "Trap if less than immediate : Trap if $t1 less than sign-extended 16-bit immediate",
                
                "000001 fffff 01010 ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[0]) < (operands[1] << 16 >> 16)) 
                     {
                        throw new ProcessingException(statement,
                            "trap",Exceptions.TRAP_EXCEPTION);
                     } 	                     
                  }
               }));
         instructionList.add(
                new BasicInstruction("tltiu $t1,-100",
                "Trap if less than immediate unsigned : Trap if $t1 less than sign-extended 16-bit immediate, unsigned comparison",
            	 
                "000001 fffff 01011 ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int first = RV32IRegisters.getValue(operands[0]);
                     // 16 bit immediate value in operands[1] is sign-extended
                     int second = operands[1] << 16 >> 16;
                  	// if signs same, do straight compare; if signs differ & first positive then first is less else second
                     if ((first >= 0 && second >= 0 || first < 0 && second < 0) ? (first < second) : (first >= 0) ) 
                     {
                        throw new ProcessingException(statement,
                            "trap",Exceptions.TRAP_EXCEPTION);
                     }                   
                  }
               }));
         instructionList.add(
                new BasicInstruction("eret", 
            	 "Exception return : Set Program Counter to Coprocessor 0 EPC register value, set Coprocessor Status register bit 1 (exception level) to zero",
            	 
                "010000 1 0000000000000000000 011000",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     // set EXL bit (bit 1) in Status register to 0 and set PC to EPC
                     Coprocessor0.updateRegister(Coprocessor0.STATUS, 
                                                 Binary.clearBit(Coprocessor0.getValue(Coprocessor0.STATUS), Coprocessor0.EXCEPTION_LEVEL));
                     RV32IRegisters.setProgramCounter(Coprocessor0.getValue(Coprocessor0.EPC));
                  }
               }));
      			
        */
        ////////////// READ PSEUDO-INSTRUCTION SPECS FROM DATA FILE AND ADD //////////////////////
         addPseudoInstructions();
      	
        ////////////// GET AND CREATE LIST OF SYSCALL FUNCTION OBJECTS ////////////////////
         syscallLoader = new SyscallLoader();
         syscallLoader.loadSyscalls();
      	
        // Initialization step.  Create token list for each instruction example.  This is
        // used by parser to determine user program correct syntax.
         for (int i = 0; i < instructionList.size(); i++)
         {
            Instruction inst = (Instruction) instructionList.get(i);
            inst.createExampleTokenList();
         }

		 HashMap maskMap = new HashMap();
		 ArrayList matchMaps = new ArrayList();
		 for (int i = 0; i < instructionList.size(); i++) {
		 	Object rawInstr = instructionList.get(i);
			if (rawInstr instanceof BasicInstruction) {
				BasicInstruction basic = (BasicInstruction) rawInstr;
				Integer mask = Integer.valueOf(basic.getOpcodeMask());
				Integer match = Integer.valueOf(basic.getOpcodeMatch());
				HashMap matchMap = (HashMap) maskMap.get(mask);
				if (matchMap == null) {
					matchMap = new HashMap();
					maskMap.put(mask, matchMap);
					matchMaps.add(new MatchMap(mask, matchMap));
				}
				matchMap.put(match, basic);
			}
		 }
		 Collections.sort(matchMaps);
		 this.opcodeMatchMaps = matchMaps;
      }

	public BasicInstruction findByBinaryCode(int binaryInstr) {
		ArrayList matchMaps = this.opcodeMatchMaps;
		for (int i = 0; i < matchMaps.size(); i++) {
			MatchMap map = (MatchMap) matchMaps.get(i);
			BasicInstruction ret = map.find(binaryInstr);
			if (ret != null) return ret;
		}
		return null;
	}
   	
    /*  METHOD TO ADD PSEUDO-INSTRUCTIONS
    */
   
       private void addPseudoInstructions()
      {
         InputStream is = null;
         BufferedReader in = null;
         try
         {
            // leading "/" prevents package name being prepended to filepath.
            is = this.getClass().getResourceAsStream("/PseudoOps.txt");
            in = new BufferedReader(new InputStreamReader(is));
         } 
             catch (NullPointerException e)
            {
               System.out.println(
                    "Error: MIPS pseudo-instruction file PseudoOps.txt not found.");
               System.exit(0);
            }
         try
         {
            String line, pseudoOp, template, firstTemplate, token;
            String description;
            StringTokenizer tokenizer;
            while ((line = in.readLine()) != null) {
                // skip over: comment lines, empty lines, lines starting with blank.
               if (!line.startsWith("#") && !line.startsWith(" ")
                        && line.length() > 0)  {  
                  description = "";
                  tokenizer = new StringTokenizer(line, "\t");
                  pseudoOp = tokenizer.nextToken();
                  template = "";
                  firstTemplate = null;
                  while (tokenizer.hasMoreTokens()) {
                     token = tokenizer.nextToken();
                     if (token.startsWith("#")) {  
                        // Optional description must be last token in the line.
                        description = token.substring(1);
                        break;
                     }
                     if (token.startsWith("COMPACT")) {
                        // has second template for Compact (16-bit) memory config -- added DPS 3 Aug 2009
                        firstTemplate = template;
                        template = "";
                        continue;
                     } 
                     template = template + token;
                     if (tokenizer.hasMoreTokens()) {
                        template = template + "\n";
                     }
                  }
                  ExtendedInstruction inst = (firstTemplate == null)
                         ? new ExtendedInstruction(pseudoOp, template, description)
                     	 : new ExtendedInstruction(pseudoOp, firstTemplate, template, description);
                  instructionList.add(inst);
               	//if (firstTemplate != null) System.out.println("\npseudoOp: "+pseudoOp+"\ndefault template:\n"+firstTemplate+"\ncompact template:\n"+template);
               }
            }
            in.close();
         } 
             catch (IOException ioe)
            {
               System.out.println(
                    "Internal Error: MIPS pseudo-instructions could not be loaded.");
               System.exit(0);
            } 
             catch (Exception ioe)
            {
               System.out.println(
                    "Error: Invalid MIPS pseudo-instruction specification.");
               System.exit(0);
            }
      
      }
   	
    /**
     *  Given an operator mnemonic, will return the corresponding Instruction object(s)
     *  from the instruction set.  Uses straight linear search technique.
     *  @param name operator mnemonic (e.g. addi, sw,...)
     *  @return list of corresponding Instruction object(s), or null if not found.
     */
       public ArrayList matchOperator(String name)
      {
         ArrayList matchingInstructions = null;
        // Linear search for now....
         for (int i = 0; i < instructionList.size(); i++)
         {
            if (((Instruction) instructionList.get(i)).getName().equalsIgnoreCase(name))
            {
               if (matchingInstructions == null) 
                  matchingInstructions = new ArrayList();
               matchingInstructions.add(instructionList.get(i));
            }
         }
         return matchingInstructions;
      }
   
   
    /**
     *  Given a string, will return the Instruction object(s) from the instruction
     *  set whose operator mnemonic prefix matches it.  Case-insensitive.  For example
     *  "s" will match "sw", "sh", "sb", etc.  Uses straight linear search technique.
     *  @param name a string
     *  @return list of matching Instruction object(s), or null if none match.
     */
       public ArrayList prefixMatchOperator(String name)
      {
         ArrayList matchingInstructions = null;
        // Linear search for now....
         if (name != null) {
            for (int i = 0; i < instructionList.size(); i++)
            {
               if (((Instruction) instructionList.get(i)).getName().toLowerCase().startsWith(name.toLowerCase()))
               {
                  if (matchingInstructions == null) 
                     matchingInstructions = new ArrayList();
                  matchingInstructions.add(instructionList.get(i));
               }
            }
         }
         return matchingInstructions;
      }
   	
   	/*
   	 * Method to find and invoke a syscall given its service number.  Each syscall
   	 * function is represented by an object in an array list.  Each object is of
   	 * a class that implements Syscall or extends AbstractSyscall.
   	 */
   	 
       private void findAndSimulateSyscall(int number, ProgramStatement statement) 
                                                        throws ProcessingException {
         Syscall service = syscallLoader.findSyscall(number);
         if (service != null) {
            service.simulate(statement);
            return;
         }
         throw new ProcessingException(statement,
              "invalid or unimplemented syscall service: " +
              number + " ", Exceptions.SYSCALL_EXCEPTION);
      }
   	
   	/*
   	 * Method to process a successful branch condition.  DO NOT USE WITH JUMP
   	 * INSTRUCTIONS!  The branch operand is a relative displacement in words
   	 * whereas the jump operand is an absolute address in bytes.

       /*
   	 * Method to process a jump.  DO NOT USE WITH BRANCH INSTRUCTIONS!  
   	 * The branch operand is a relative displacement in words
   	 * whereas the jump operand is an absolute address in bytes.
   	 *
   	 * The parameter is jump target absolute byte address.
   	 *
   	 * Handles delayed branching if that setting is enabled.
   	 */
   	 
       private void processJump(Number targetAddress) {
         if (Globals.getSettings().getDelayedBranchingEnabled()) {
            DelayedBranch.register(targetAddress);
         } 
         else {
            RV32IRegisters.setProgramCounter(targetAddress);
         }	 
      }
   
   	/*
   	 * Method to process storing of a return address in the given
   	 * register.  This is used only by the "and link"
   	 * instructions: jal, jalr, bltzal, bgezal.  If delayed branching
   	 * setting is off, the return address is the address of the
   	 * next instruction (e.g. the current PC value).  If on, the
   	 * return address is the instruction following that, to skip over
   	 * the delay slot.
   	 *
   	 * The parameter is register number to receive the return address.
   	 */
   	 
       private void processReturnAddress(Number register) {
         RV32IRegisters.updateRegister(register.intValue(), GenMath.add(RV32IRegisters.getProgramCounter(),
                 ((Globals.getSettings().getDelayedBranchingEnabled()) ? 
            	  Instruction.INSTRUCTION_LENGTH : 0) ));	 
      }

	  private static class MatchMap implements Comparable {
	  	private int mask;
		private int maskLength; // number of 1 bits in mask
		private HashMap matchMap;

		public MatchMap(int mask, HashMap matchMap) {
			this.mask = mask;
			this.matchMap = matchMap;

			int k = 0;
			int n = mask;
			while (n != 0) {
				k++;
				n &= n - 1;
			}
			this.maskLength = k;
		}

		public boolean equals(Object o) {
			return o instanceof MatchMap && mask == ((MatchMap) o).mask;
		}

		public int compareTo(Object other) {
			MatchMap o = (MatchMap) other;
			int d = o.maskLength - this.maskLength;
			if (d == 0) d = this.mask - o.mask;
			return d;
		}

		public BasicInstruction find(int instr) {
			int match = Integer.valueOf(instr & mask);
			return (BasicInstruction) matchMap.get(match);
		}
	}
   }

