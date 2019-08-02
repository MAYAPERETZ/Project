   package mars.mips.instructions;
   import mars.simulator.*;
   import mars.mips.hardware.*;
   import mars.mips.instructions.syscalls.*;
   import mars.*;
   import mars.util.*;
   import java.util.*;


import java.io.*;
import java.math.BigInteger;
	
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
                new R_type("add t1,t2,t3",
            	 "Addition with overflow : set t1 to (t2 plus t3)",
                "0000000tttttsssss000fffff0110011",GenMath::add));
         
         // Subtracts the register x[rs2] from register x[rs1] and writes the result to x[rd].
         // Arithmetic overflow is ignored.
         
         instructionList.add(
                new R_type("sub t1,t2,t3",
            	 "Subtract. Subtracts the register t2 from register t3 and writes the result to t1",
                "0100000tttttsssss000fffff0110011", GenMath::sub));
         
       
         // Adds the sign-extended immediate to register x[rs1] and writes the result to x[rd].
         // Arithmetic overflow is ignored.
         
         instructionList.add(
                new I_type("addi t1,t2,-100",
            	 "Add Immediate. Adds the sign-extended(-100) to t2 and writes the result to t1.",
                "ttttttttttttsssss000fffff0010011",GenMath::add));
      /*   instructionList.add(
                new BasicInstruction("addu t1,t2,t3",
            	 "Addition unsigned without overflow : set t1 to (t2 plus t3), no overflow",

                "000000 sssss ttttt fffff 00000 100001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RV32IRegisters.updateRegister(operands[0],
                        RV32IRegisters.getValue(operands[1])
                        + RV32IRegisters.getValue(operands[2]));
                  }
               }));
        */
         /*instructionList.add(
                new BasicInstruction("subu $t1,$t2,$t3",
            	 "Subtraction unsigned without overflow : set $t1 to ($t2 minus $t3), no overflow",

                "000000 sssss ttttt fffff 00000 100011",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RV32IRegisters.updateRegister(operands[0],
                        RV32IRegisters.getValue(operands[1])
                        - RV32IRegisters.getValue(operands[2]));
                  }
               }));
         
         instructionList.add(
                new BasicInstruction("addiu $t1,$t2,-100",
            	 "Addition immediate unsigned without overflow : set $t1 to ($t2 plus signed 16-bit immediate), no overflow",
                
                "001001 sssss fffff tttttttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RV32IRegisters.updateRegister(operands[0],
                        RV32IRegisters.getValue(operands[1])
                        + (operands[2] << 16 >> 16));
                  }
               }));
               */
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

         /*instructionList.add(
                new BasicInstruction("madd t1,t2",
            	 "Multiply add : Multiply t1 by t2 then increment HI by high-order 32 bits of product, increment LO by low-order 32 bits of product (use mfhi to access HI, mflo to access LO)",

                "011100 fffff sssss 00000 00000 000000",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     long product = (long) RV32IRegisters.getValue(operands[0])
                        * (long) RV32IRegisters.getValue(operands[1]);
                     // Register 33 is HIGH and 34 is LOW. 
                     long contentsHiLo = Binary.twoIntsToLong(
                        RV32IRegisters.getValue(33), RV32IRegisters.getValue(34));
                     long sum = contentsHiLo + product;
                     RV32IRegisters.updateRegister(33, Binary.highOrderLongToInt(sum));
                     RV32IRegisters.updateRegister(34, Binary.lowOrderLongToInt(sum));
                  }
               }));
          */
         /*instructionList.add(
                new BasicInstruction("maddu $t1,$t2",
            	 "Multiply add unsigned : Multiply $t1 by $t2 then increment HI by high-order 32 bits of product, increment LO by low-order 32 bits of product, unsigned (use mfhi to access HI, mflo to access LO)",

                "011100 fffff sssss 00000 00000 000001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     long product = (((long) RV32IRegisters.getValue(operands[0]))<<32>>>32)
                        * (((long) RV32IRegisters.getValue(operands[1]))<<32>>>32);
                     // Register 33 is HIGH and 34 is LOW. 
                     long contentsHiLo = Binary.twoIntsToLong(
                        RV32IRegisters.getValue(33), RV32IRegisters.getValue(34));
                     long sum = contentsHiLo + product;
                     RV32IRegisters.updateRegister(33, Binary.highOrderLongToInt(sum));
                     RV32IRegisters.updateRegister(34, Binary.lowOrderLongToInt(sum));
                  }
               }));
               */
         /*instructionList.add(
                new BasicInstruction("msub $t1,$t2",
            	 "Multiply subtract : Multiply $t1 by $t2 then decrement HI by high-order 32 bits of product, decrement LO by low-order 32 bits of product (use mfhi to access HI, mflo to access LO)",

                "011100 fffff sssss 00000 00000 000100",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     long product = (long) RV32IRegisters.getValue(operands[0])
                        * (long) RV32IRegisters.getValue(operands[1]);
                     // Register 33 is HIGH and 34 is LOW. 
                     long contentsHiLo = Binary.twoIntsToLong(
                        RV32IRegisters.getValue(33), RV32IRegisters.getValue(34));
                     long diff = contentsHiLo - product;
                     RV32IRegisters.updateRegister(33, Binary.highOrderLongToInt(diff));
                     RV32IRegisters.updateRegister(34, Binary.lowOrderLongToInt(diff));
                  }
               }));
               */
         /*instructionList.add(
                new BasicInstruction("msubu $t1,$t2",
            	 "Multiply subtract unsigned : Multiply $t1 by $t2 then decrement HI by high-order 32 bits of product, decement LO by low-order 32 bits of product, unsigned (use mfhi to access HI, mflo to access LO)",

                "011100 fffff sssss 00000 00000 000101",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     long product = (((long) RV32IRegisters.getValue(operands[0]))<<32>>>32)
                        * (((long) RV32IRegisters.getValue(operands[1]))<<32>>>32);
                     // Register 33 is HIGH and 34 is LOW. 
                     long contentsHiLo = Binary.twoIntsToLong(
                        RV32IRegisters.getValue(33), RV32IRegisters.getValue(34));
                     long diff = contentsHiLo - product;
                     RV32IRegisters.updateRegister(33, Binary.highOrderLongToInt(diff));
                     RV32IRegisters.updateRegister(34, Binary.lowOrderLongToInt(diff));
                  }
               }));
               */
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
                 
        /* instructionList.add(
                new BasicInstruction("mfhi $t1", 
            	 "Move from HI register : Set $t1 to contents of HI (see multiply and divide operations)",
            	 
                "000000 00000 00000 fffff 00000 010000",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RV32IRegisters.updateRegister(operands[0],
                        RV32IRegisters.getValue(33));
                  }
               }));
               */
         /*instructionList.add(
                new BasicInstruction("mflo $t1", 
            	 "Move from LO register : Set $t1 to contents of LO (see multiply and divide operations)",
            	 
                "000000 00000 00000 fffff 00000 010010",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RV32IRegisters.updateRegister(operands[0],
                        RV32IRegisters.getValue(34));
                  }
               }));
               */
         /*instructionList.add(
                new BasicInstruction("mthi $t1", 
            	 "Move to HI registerr : Set HI to contents of $t1 (see multiply and divide operations)",
            	 
                "000000 fffff 00000 00000 00000 010001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RV32IRegisters.updateRegister(33,
                        RV32IRegisters.getValue(operands[0]));
                  }
               }));
               */
         /*instructionList.add(
                new BasicInstruction("mtlo $t1", 
            	 "Move to LO register : Set LO to contents of $t1 (see multiply and divide operations)",
            	 
                "000000 fffff 00000 00000 00000 010011",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RV32IRegisters.updateRegister(34,
                        RV32IRegisters.getValue(operands[0]));
                  }
               }));
          */
         instructionList.add(
                new R_type("and t1,t2,t3",
            	 "Bitwise AND : Set t1 to bitwise AND of t2 and t3",
                "0000000tttttsssss111fffff0110011", GenMath::and));
         
         instructionList.add(
                new R_type("or t1,t2,t3",
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
                  
     
         /*instructionList.add(
                new BasicInstruction("nor $t1,$t2,$t3",
            	 "Bitwise NOR : Set $t1 to bitwise NOR of $t2 and $t3",

                "000000 sssss ttttt fffff 00000 100111",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RV32IRegisters.updateRegister(operands[0],
                        ~(RV32IRegisters.getValue(operands[1])
                        | RV32IRegisters.getValue(operands[2])));
                  }
               }));
               */
         instructionList.add(
                new R_type("xor t1,t2,t3",
            	 "Bitwise XOR (exclusive OR) : Set t1 to bitwise XOR of t2 and t3",
                "0000000tttttsssss100fffff0110011",GenMath::xor));
         
         instructionList.add(
                new I_type("xori t1,t2,100",
            	 "Exclusive-OR Immediate. Set t1 to bitwise XOR of t2 and zero-extended 12-bit immediate",            
                "ttttttttttttsssss100fffff0010011", GenMath::xor));		
         
         // Shifts register x[rs1] by x[rs2] bit positions. The vacated bits are filled with zeros and the result is written "
         // to x[rd]. The least-significant five bits of x[rs1](of six bits for RV64I) form the shift amount; the upper bits are ignored."
         
         instructionList.add(
                new R_type("sll t1,t2,t3",
                "Shift Left Logical Immediate. Shifts register t2 by t3 bit positions and the result is written "
                  + "\nto t1.",
                "0000000tttttsssss001fffff0110011",GenMath::sll));
         
         instructionList.add(
                 new I_typeShift("slli t1,t2,10",
             	 "Shift Left Logical Immediate. Shifts register t2 by 10 bit positions. The vacated bits are filled with zeros abd the result is written "
             	 + "\nto t1. For RV32I, the instruction is only legal when shmat[5] = 0",
                 "0000000tttttsssss001fffff0110011",GenMath::sll));

         /*instructionList.add(
                new BasicInstruction("sllv $t1,$t2,$t3",
            	 "Shift left logical variable : Set $t1 to result of shifting $t2 left by number of bits specified by value in low-order 5 bits of $t3",

                "000000 ttttt sssss fffff 00000 000100",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  // Mask all but low 5 bits of register containing shamt.
                     RV32IRegisters.updateRegister(operands[0],
                        RV32IRegisters.getValue(operands[1]) << 
                        (RV32IRegisters.getValue(operands[2]) & 0x0000001F));
                  }
               }));
           */
         
         // Shift Right Logical. Shifts register t2 right by t3 bits positions. 
         // The vacated bits are filled with zeros, and the result is written to x[rd].
         // The least-significant five bits of x[r2] (or six bits for RV64I) form the shift amount;
         // the upper bits are ignored.
         
         instructionList.add(
                new R_type("srl t1,t2,t3",
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
                new R_type("sra t1,t2,t3",
                "Shift Right Arithmetic. Shifts register t2 right by t3 bit positions.",	 
                "0100000tttttsssss101fffff0110011", GenMath::sra));
         
         // Shifts register t2 right by shmat bit positions. 
         // The vacated bits are filled with copies of t2's most-significant bit, and the result is written to t1.
         // The least-significant five bits of t2 (or six in RV64I) form the shift amount; the upper bits are ignored.
      
         
         instructionList.add(
                 new I_typeShift("srai t1,t2,10",
                 "Shift Right Arithmetic. Shifts register t2 right by 10 bit positions.",
                 "0100000tttttsssss101fffff0010011",GenMath::sra));
        /* instructionList.add(
                new BasicInstruction("srav $t1,$t2,$t3",
            	 "Shift right arithmetic variable : Set $t1 to result of sign-extended shifting $t2 right by number of bits specified by value in low-order 5 bits of $t3",

                "000000 ttttt sssss fffff 00000 000111",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  // Mask all but low 5 bits of register containing shamt.Use ">>" to sign-fill.
                     RV32IRegisters.updateRegister(operands[0],
                        RV32IRegisters.getValue(operands[1]) >> 
                        (RV32IRegisters.getValue(operands[2]) & 0x0000001F));
                  }
               }));
         */
        /* instructionList.add(
                new BasicInstruction("srlv $t1,$t2,$t3",
            	 "Shift right logical variable : Set $t1 to result of shifting $t2 right by number of bits specified by value in low-order 5 bits of $t3",

                "000000 ttttt sssss fffff 00000 000110",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  // Mask all but low 5 bits of register containing shamt.Use ">>>" to zero-fill.
                     RV32IRegisters.updateRegister(operands[0],
                        RV32IRegisters.getValue(operands[1]) >>> 
                        (RV32IRegisters.getValue(operands[2]) & 0x0000001F));
                  }
               }));
               
         */
                
         // Loads four bytes from memory at address t1 + sign-extended(-100) and writes them to t2.
         // For RV64I, the result is sign-extended.
                
         instructionList.add(
                new I_type.LW_type("lw t2,-100(t1)",
            	 "Load Word. Loads four bytes from memory at address t1 + sign-extended(-100) and writes them to t2.",
                "ssssssssssssttttt010fffff0000011", Globals.memory::getWord));
         /*instructionList.add(
                new BasicInstruction("ll $t1,-100($t2)",
                "Load linked : Paired with Store Conditional (sc) to perform atomic read-modify-write.  Treated as equivalent to Load Word (lw) because MARS does not simulate multiple processors.",
            	 
                "110000 ttttt fffff ssssssssssssssss",
            	 // The ll (load link) command is supposed to be the front end of an atomic
            	 // operation completed by sc (store conditional), with success or failure
            	 // of the store depending on whether the memory block containing the
            	 // loaded word is modified in the meantime by a different processor.
            	 // Since MARS, like SPIM simulates only a single processor, the store
            	 // conditional will always succeed so there is no need to do anything
            	 // special here.  In that case, ll is same as lw.  And sc does the same
            	 // thing as sw except in addition it writes 1 into the source register.
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        RV32IRegisters.updateRegister(operands[0],
                            Globals.memory.getWord(
                            RV32IRegisters.getValue(operands[2]) + operands[1]));
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));
         /*instructionList.add(
                new BasicInstruction("lwl $t1,-100($t2)",
                "Load word left : Load from 1 to 4 bytes left-justified into $t1, starting with effective memory byte address and continuing through the low-order byte of its word",
            	 
                "100010 ttttt fffff ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        int address = RV32IRegisters.getValue(operands[2]) + operands[1];
                        int result = RV32IRegisters.getValue(operands[0]);
                        for (int i=0; i<=address % Globals.memory.WORD_LENGTH_BYTES; i++) {
                           result = Binary.setByte(result,3-i,Globals.memory.getByte(address-i));
                        }
                        RV32IRegisters.updateRegister(operands[0], result);
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));
         /*instructionList.add(
                new BasicInstruction("lwr $t1,-100($t2)",
                "Load word right : Load from 1 to 4 bytes right-justified into $t1, starting with effective memory byte address and continuing through the high-order byte of its word",
            	 
                "100110 ttttt fffff ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        int address = RV32IRegisters.getValue(operands[2]) + operands[1];
                        int result = RV32IRegisters.getValue(operands[0]);
                        for (int i=0; i<=3-(address % Globals.memory.WORD_LENGTH_BYTES); i++) {
                           result = Binary.setByte(result,i,Globals.memory.getByte(address+i));
                        }
                        RV32IRegisters.updateRegister(operands[0], result);
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));
         */
         instructionList.add(
                new S_type("sw t1,-100(t2)",
                "Store Word. Store contents of t1 into effective memory word address",
                "tttttttsssssfffff010sssss0100011",Globals.memory::setWord, 0xffffffff));
         /*instructionList.add(
                new BasicInstruction("sc $t1,-100($t2)",
                "Store conditional : Paired with Load Linked (ll) to perform atomic read-modify-write.  Stores $t1 value into effective address, then sets $t1 to 1 for success.  Always succeeds because MARS does not simulate multiple processors.",
            	 
                "111000 ttttt fffff ssssssssssssssss",
            	 // See comments with "ll" instruction above.  "sc" is implemented
            	 // like "sw", except that 1 is placed in the source register.
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        Globals.memory.setWord(
                            RV32IRegisters.getValue(operands[2]) + operands[1],
                            RV32IRegisters.getValue(operands[0]));
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                     RV32IRegisters.updateRegister(operands[0],1); // always succeeds
                  }
               }));
         /*instructionList.add(
                new BasicInstruction("swl $t1,-100($t2)",
                "Store word left : Store high-order 1 to 4 bytes of $t1 into memory, starting with effective byte address and continuing through the low-order byte of its word",
            	 
                "101010 ttttt fffff ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        int address = RV32IRegisters.getValue(operands[2]) + operands[1];
                        int source = RV32IRegisters.getValue(operands[0]);
                        for (int i=0; i<=address % Globals.memory.WORD_LENGTH_BYTES; i++) {
                           Globals.memory.setByte(address-i,Binary.getByte(source,3-i));
                        }
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));
         /*instructionList.add(
                new BasicInstruction("swr $t1,-100($t2)",
                "Store word right : Store low-order 1 to 4 bytes of $t1 into memory, starting with high-order byte of word containing effective byte address and continuing through that byte address",
            	 
                "101110 ttttt fffff ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        int address = RV32IRegisters.getValue(operands[2]) + operands[1];
                        int source = RV32IRegisters.getValue(operands[0]);
                        for (int i=0; i<=3-(address % Globals.memory.WORD_LENGTH_BYTES); i++) {
                           Globals.memory.setByte(address+i,Binary.getByte(source,i));
                        }
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));
         */
         
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
        /* instructionList.add(
                new BasicInstruction("bgezal $t1,label",
                "Branch if greater then or equal to zero and link : If $t1 is greater than or equal to zero, then set $ra to the Program Counter and branch to statement at label's address",
            	 BasicInstructionFormat.I_BRANCH_FORMAT,
                "000001 fffff 10001 ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     long[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[0]) >= 0)
                     {  // the "and link" part
                        processReturnAddress(31);//RV32IRegisters.updateRegister("$ra",RV32IRegisters.getProgramCounter());
                        processBranch(operands[1]);
                     }
                  } 
               }));
         instructionList.add(
                new BasicInstruction("bgtz $t1,label",
                "Branch if greater than zero : Branch to statement at label's address if $t1 is greater than zero",
            	 BasicInstructionFormat.I_BRANCH_FORMAT,
                "000111 fffff 00000 ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     long[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[0]) > 0)
                     {
                        processBranch(operands[1]);
                     }
                  }
               }));
         instructionList.add(
                new BasicInstruction("blez $t1,label",
                "Branch if less than or equal to zero : Branch to statement at label's address if $t1 is less than or equal to zero",
            	 BasicInstructionFormat.I_BRANCH_FORMAT,
                "000110 fffff 00000 ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     long[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[0]) <= 0)
                     {
                        processBranch(operands[1]);
                     }
                  }
               }));
               */
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
         /*
         instructionList.add(
                new BasicInstruction("bltzal $t1,label",
                "Branch if less than zero and link : If $t1 is less than or equal to zero, then set $ra to the Program Counter and branch to statement at label's address",
            	 BasicInstructionFormat.I_BRANCH_FORMAT,
                "000001 fffff 10000 ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     long[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[0]) < 0)
                     {  // the "and link" part
                        processReturnAddress(31);//RV32IRegisters.updateRegister("$ra",RV32IRegisters.getProgramCounter());
                        processBranch(operands[1]);
                     }
                  }
               }));
               */
         instructionList.add(
                 new R_type("slt t1,t2,t3",
                         "Set If Less Than. Compares t2 and t3 as two's complement numbers, and writes 1 to t1 if t2 is smaller, or 0 if not.",
                          "0000000tttttsssss010fffff0110011", GenMath::lt));
         instructionList.add(
                 new R_type("sltu t1,t2,t3",
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
                new BasicInstruction("movn $t1,$t2,$t3",
                "Move conditional not zero : Set $t1 to $t2 if $t3 is not zero",
            	 
                "000000 sssss ttttt fffff 00000 001011",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[2])!=0)
                        RV32IRegisters.updateRegister(operands[0], RV32IRegisters.getValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("movz $t1,$t2,$t3",
                "Move conditional zero : Set $t1 to $t2 if $t3 is zero",
            	 
                "000000 sssss ttttt fffff 00000 001010",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[2])==0)
                        RV32IRegisters.updateRegister(operands[0], RV32IRegisters.getValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("movf $t1,$t2",
                "Move if FP condition flag 0 false : Set $t1 to $t2 if FPU (Coprocessor 1) condition flag 0 is false (zero)",
            	 
                "000000 sssss 000 00 fffff 00000 000001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (Coprocessor1.getConditionFlag(0)==0)
                        RV32IRegisters.updateRegister(operands[0], RV32IRegisters.getValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("movf $t1,$t2,1",
                "Move if specified FP condition flag false : Set $t1 to $t2 if FPU (Coprocessor 1) condition flag specified by the immediate is false (zero)",
            	 
                "000000 sssss ttt 00 fffff 00000 000001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (Coprocessor1.getConditionFlag(operands[2])==0)
                        RV32IRegisters.updateRegister(operands[0], RV32IRegisters.getValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("movt $t1,$t2",
            	 "Move if FP condition flag 0 true : Set $t1 to $t2 if FPU (Coprocessor 1) condition flag 0 is true (one)",

                "000000 sssss 000 01 fffff 00000 000001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (Coprocessor1.getConditionFlag(0)==1)
                        RV32IRegisters.updateRegister(operands[0], RV32IRegisters.getValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("movt $t1,$t2,1",
            	 "Move if specfied FP condition flag true : Set $t1 to $t2 if FPU (Coprocessor 1) condition flag specified by the immediate is true (one)",

                "000000 sssss ttt 01 fffff 00000 000001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (Coprocessor1.getConditionFlag(operands[2])==1)
                        RV32IRegisters.updateRegister(operands[0], RV32IRegisters.getValue(operands[1]));
                  }
               }));
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
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     findAndSimulateSyscall(RV32IRegisters.getValue(2),statement);
                  }
               }));
         /*instructionList.add(
                new BasicInstruction("j target", 
            	 "Jump unconditionally : Jump to statement at target address",
            	 BasicInstructionFormat.J_type,
                "000010 ffffffffffffffffffffffffff",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     processJump(
                        ((RV32IRegisters.getProgramCounter() & 0xF0000000)
                                | (operands[0] << 2)));            
                  }
               }));
       /*  instructionList.add(
                new BasicInstruction("jr $t1", 
            	 "Jump register unconditionally : Jump to statement whose address is in $t1",
            	 
                "000000 fffff 00000 00000 00000 001000",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     processJump(RV32IRegisters.getValue(operands[0]));
                  }
               }));
               */
         instructionList.add(
                new J_type("jal t1, target",
                "Jump and link. Writes the address of the next instruction (pc + 4) to t1, then set the pc to the current pc plus the sign-extended offset."
                + "\nIf rd is omitted, x1 (ra) is assumed.",
                "ssssssssssssssssssssfffff1101111",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     Number[] operands = statement.getOperands();
                     processReturnAddress(operands[0]);//RV32IRegisters.updateRegister(operands[0], RV32IRegisters.getProgramCounter());
                     processJump(GenMath.sub(GenMath.add(
                    	RV32IRegisters.getProgramCounter(), operands[1])
                    	,Instruction.INSTRUCTION_LENGTH));
                  }
               }));
         

         // Sets the pc to x[rs1] + sign-extend(offset), masking off the least-significant bit if the computed
         // address, then writes the previous pc+4 to x[rd]. If x[rd] is omitted, x1 (ra) is assumed.
         
         instructionList.add(
                new I_type.LW_type("jalr t1,-100(t2)",
                "Jump and link register. Sets the pc to t2 + sign-extend(4), masking off the least-significant bit of the computed"
                + "\naddress, then writes the previous pc+4 to t1. If rd is ommitted, x1 (ra) is assumed.",
                "ssssssssssssttttt000fffff1100111",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     Number[] operands = statement.getOperands();
                     processReturnAddress(operands[0]);
                     processJump(
                    		 GenMath.and(GenMath.add(RV32IRegisters.getValue(operands[2]),
                    		 operands[1]), (~1)));
                  }
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
         /*
         instructionList.add(        
                new BasicInstruction("clo $t1,$t2", 
            	 "Count number of leading ones : Set $t1 to the count of leading one bits in $t2 starting at most significant bit position",
            	 
            	 // MIPS32 requires rd (first) operand to appear twice in machine code.
            	 // It has to be same as rt (third) operand in machine code, but the
            	 // source statement does not have or permit third operand.
            	 // In the machine code, rd and rt are adjacent, but my mask
            	 // substitution cannot handle adjacent placement of the same source
            	 // operand (e.g. "... sssss fffff fffff ...") because it would interpret
            	 // the mask to be the total length of both (10 bits).  I could code it
            	 // to have 3 operands then define a pseudo-instruction of two operands
            	 // to translate into this, but then both would show up in instruction set
            	 // list and I don't want that.  So I will use the convention of Computer
            	 // Organization and Design 3rd Edition, Appendix A, and code the rt bits
            	 // as 0's.  The generated code does not match SPIM and would not run 
            	 // on a real MIPS machine but since I am providing no means of storing
            	 // the binary code that is not really an issue.
                "011100 sssss 00000 fffff 00000 100001",
                new SimulationCode()
               {   
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int value = RV32IRegisters.getValue(operands[1]);
                     int leadingOnes = 0;
                     int bitPosition = 31;
                     while (Binary.bitValue(value,bitPosition)==1 && bitPosition>=0) {
                        leadingOnes++;
                        bitPosition--;
                     }
                     RV32IRegisters.updateRegister(operands[0], leadingOnes);
                  }
               }));	
         instructionList.add(        
                new BasicInstruction("clz $t1,$t2", 
            	 "Count number of leading zeroes : Set $t1 to the count of leading zero bits in $t2 starting at most significant bit positio",
            	 
            	 // See comments for "clo" instruction above.  They apply here too.
                "011100 sssss 00000 fffff 00000 100000",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int value = RV32IRegisters.getValue(operands[1]);
                     int leadingZeros = 0;
                     int bitPosition = 31;
                     while (Binary.bitValue(value,bitPosition)==0 && bitPosition>=0) {
                        leadingZeros++;
                        bitPosition--;
                     }
                     RV32IRegisters.updateRegister(operands[0], leadingZeros);
                  }
               }));				
         instructionList.add(        
                new BasicInstruction("mfc0 $t1,$8", 
            	 "Move from Coprocessor 0 : Set $t1 to the value stored in Coprocessor 0 register $8",
            	 
                "010000 00000 fffff sssss 00000 000000",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RV32IRegisters.updateRegister(operands[0],
                        Coprocessor0.getValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("mtc0 $t1,$8", 
            	 "Move to Coprocessor 0 : Set Coprocessor 0 register $8 to value stored in $t1",
            	 
                "010000 00100 fffff sssss 00000 000000",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     Coprocessor0.updateRegister(operands[1],
                        RV32IRegisters.getValue(operands[0]));
                  }
               }));
      			*/
        /////////////////////// Floating Point Instructions Start Here ////////////////
         instructionList.add(
                new R_type.WithRmFeild("fadd.s ft1,ft2,ft3",
                "Floating-point Add, Single-Precision. Set ft1 to single-precision floating point value of ft2 plus ft3", 
                "0000000tttttsssssxxxfffff1010011",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     long[] operands = statement.getOperands();
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

                  }  
               }));
         
         instructionList.add(
                new R_type.WithRmFeild("fsub.s ft1,ft2,ft3",
                "Floating-point Subtract, Single-Precision. Set ft1 to single-precision floating point value of ft2  minus ft3",
                "0000100tttttsssssxxxfffff1010011",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     Number[] operands = statement.getOperands();
                     float sub1 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                     float sub2 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[2]));
                     float diff = sub1 - sub2;
                     if(Coprocessor1.isUnderflow(diff, Coprocessor1.getFclass(diff))) {
                    	 throw new FloatingPointException(statement,"Floating-point Arithmetic Underflow", 
                         		Exceptions.FLOATING_POINT_UNDERFLOW);
                     }
                     else
                         Coprocessor1.updateRegisterWithExecptions(operands[0], Coprocessor1.round(diff), statement);

                  }
               }));
         instructionList.add(
                new R_type.WithRmFeild("fmul.s ft1,ft2,ft3",
                "Floating-point Multiply, Single-Precision. Set ft1 to single-precision floating point value of ft2 times ft3",
                "0001100tttttsssssxxxfffff1010011",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     long[] operands = statement.getOperands();
                     float mul1 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                     float mul2 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[2]));
                     float prod = mul1 * mul2;
                     Coprocessor1.updateRegisterWithExecptions(operands[0], Coprocessor1.round(prod), statement);
                  }
               }));
         instructionList.add(
                new R_type.WithRmFeild("fdiv.s ft1,ft2,ft3",
                "Floating-point Divide, Single-Precision. Set ft1 to single-precision floating point value of ft2 divided by ft3",
                "0001000tttttsssssxxxfffff1010011",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     long[] operands = statement.getOperands();
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
                 }
               }));
         instructionList.add(
                new BasicInstruction("fsqrt.s ft1,ft2",
            	 "Floating-point Square Root, Single-Precision. Set ft1 to single-precision floating point square root of ft2",
                 "010110000000sssssxxxfffff1010011",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     long[] operands = statement.getOperands();
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
                   }
               }));
       /*  instructionList.add(
                new BasicInstruction("floor.w.s $f0,$f1",
                "Floor single precision to word : Set $f0 to 32-bit integer floor of single-precision float in $f1",
            	 
                "010001 10000 00000 sssss fffff 001111",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     float floatValue = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));	
                     int floor = (int) Math.floor(floatValue);
                  	// DPS 28-July-2010: Since MARS does not simulate the FSCR, I will take the default
                  	// action of setting the result to 2^31-1, if the value is outside the 32 bit range.
                     if ( Float.isNaN(floatValue) 
                          || Float.isInfinite(floatValue)
                          || floatValue < (float) Integer.MIN_VALUE 
                     	  || floatValue > (float) Integer.MAX_VALUE ) {							
                        floor = Integer.MAX_VALUE;
                     }
                     Coprocessor1.updateRegister(operands[0], floor);
                  }
               }));
         instructionList.add(
                new BasicInstruction("ceil.w.s $f0,$f1",
            	 "Ceiling single precision to word : Set $f0 to 32-bit integer ceiling of single-precision float in $f1",

                "010001 10000 00000 sssss fffff 001110",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     float floatValue = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));	
                     int ceiling = (int) Math.ceil(floatValue);
                  	// DPS 28-July-2010: Since MARS does not simulate the FSCR, I will take the default
                  	// action of setting the result to 2^31-1, if the value is outside the 32 bit range.
                     if ( Float.isNaN(floatValue) 
                          || Float.isInfinite(floatValue)
                          || floatValue < (float) Integer.MIN_VALUE 
                     	  || floatValue > (float) Integer.MAX_VALUE ) {							
                        ceiling = Integer.MAX_VALUE;
                     }
                     Coprocessor1.updateRegister(operands[0], ceiling);
                  }
               }));
         instructionList.add(
                new BasicInstruction("round.w.s $f0,$f1",
                "Round single precision to word : Set $f0 to 32-bit integer round of single-precision float in $f1",
            	 
                "010001 10000 00000 sssss fffff 001100",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { // MIPS32 documentation (and IEEE 754) states that round rounds to the nearest but when
                    // both are equally near it rounds to the even one!  SPIM rounds -4.5, -5.5,
                    // 4.5 and 5.5 to (-4, -5, 5, 6).  Curiously, it rounds -5.1 to -4 and -5.6 to -5. 
                    // Until MARS 3.5, I used Math.round, which rounds to nearest but when both are
                    // equal it rounds toward positive infinity.  With Release 3.5, I painstakingly
                    // carry out the MIPS and IEEE 754 standard.
                     int[] operands = statement.getOperands();
                     float floatValue = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                     int below=0, above=0, round = Math.round(floatValue);
                  	// According to MIPS32 spec, if any of these conditions is true, set
                  	// Invalid Operation in the FCSR (Floating point Control/Status Register) and
                  	// set result to be 2^31-1.  MARS does not implement this register (as of release 3.4.1).
                  	// It also mentions the "Invalid Operation Enable bit" in FCSR, that, if set, results
                  	// in immediate exception instead of default value.  
                     if ( Float.isNaN(floatValue) 
                          || Float.isInfinite(floatValue)
                          || floatValue < (float) Integer.MIN_VALUE 
                     	  || floatValue > (float) Integer.MAX_VALUE ) {
                        round = Integer.MAX_VALUE;
                     } 
                     else {
                        Float floatObj = new Float(floatValue);
                        // If we are EXACTLY in the middle, then round to even!  To determine this,
                        // find next higher integer and next lower integer, then see if distances 
                        // are exactly equal.
                        if (floatValue < 0.0F) {
                           above = floatObj.intValue(); // truncates
                           below = above - 1;
                        } 
                        else {
                           below = floatObj.intValue(); // truncates
                           above = below + 1;
                        }
                        if (floatValue - below == above - floatValue) { // exactly in the middle?
                           round = (above%2 == 0) ? above : below;
                        }
                     }
                     Coprocessor1.updateRegister(operands[0], round);
                  }
               }));
         instructionList.add(
                new BasicInstruction("trunc.w.s $f0,$f1",
                "Truncate single precision to word : Set $f0 to 32-bit integer truncation of single-precision float in $f1",
            	 
                "010001 10000 00000 sssss fffff 001101",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     float floatValue = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));	
                     int truncate = (int) floatValue;// Typecasting will round toward zero, the correct action
                  	// DPS 28-July-2010: Since MARS does not simulate the FSCR, I will take the default
                  	// action of setting the result to 2^31-1, if the value is outside the 32 bit range.
                     if ( Float.isNaN(floatValue) 
                          || Float.isInfinite(floatValue)
                          || floatValue < (float) Integer.MIN_VALUE 
                     	  || floatValue > (float) Integer.MAX_VALUE ) {							
                        truncate = Integer.MAX_VALUE;
                     }
                     Coprocessor1.updateRegister(operands[0], truncate);
                  }
               }));
         instructionList.add(
                new BasicInstruction("add.d $f2,$f4,$f6",
            	 "Floating point addition double precision : Set $f2 to double-precision floating point value of $f4 plus $f6",
            	 
                "010001 10001 ttttt sssss fffff 000000",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1 || operands[2]%2==1) {
                        throw new ProcessingException(statement, "all registers must be even-numbered");
                     }
                     double add1 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                     double add2 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[2]+1),Coprocessor1.getIntValue(operands[2])));
                     double sum  = add1 + add2;
                     long longSum = Double.doubleToLongBits(sum);
                     Coprocessor1.updateRegister(operands[0]+1, Binary.highOrderLongToInt(longSum));
                     Coprocessor1.updateRegister(operands[0], Binary.lowOrderLongToInt(longSum));
                  }
               }));
         instructionList.add(
                new BasicInstruction("sub.d $f2,$f4,$f6",
            	 "Floating point subtraction double precision : Set $f2 to double-precision floating point value of $f4 minus $f6",

                "010001 10001 ttttt sssss fffff 000001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1 || operands[2]%2==1) {
                        throw new ProcessingException(statement, "all registers must be even-numbered");
                     }
                     double sub1 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                     double sub2 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[2]+1),Coprocessor1.getIntValue(operands[2])));
                     double diff = sub1 - sub2;
                     long longDiff = Double.doubleToLongBits(diff);
                     Coprocessor1.updateRegister(operands[0]+1, Binary.highOrderLongToInt(longDiff));
                     Coprocessor1.updateRegister(operands[0], Binary.lowOrderLongToInt(longDiff));
                  }
               }));
         instructionList.add(
                new BasicInstruction("mul.d $f2,$f4,$f6",
            	 "Floating point multiplication double precision : Set $f2 to double-precision floating point value of $f4 times $f6",

                "010001 10001 ttttt sssss fffff 000010",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1 || operands[2]%2==1) {
                        throw new ProcessingException(statement, "all registers must be even-numbered");
                     }
                     double mul1 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                     double mul2 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[2]+1),Coprocessor1.getIntValue(operands[2])));
                     double prod  = mul1 * mul2;
                     long longProd = Double.doubleToLongBits(prod);
                     Coprocessor1.updateRegister(operands[0]+1, Binary.highOrderLongToInt(longProd));
                     Coprocessor1.updateRegister(operands[0], Binary.lowOrderLongToInt(longProd));
                  }
               }));
         instructionList.add(
                new BasicInstruction("div.d $f2,$f4,$f6",
            	 "Floating point division double precision : Set $f2 to double-precision floating point value of $f4 divided by $f6",
                "010001 10001 ttttt sssss fffff 000011",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1 || operands[2]%2==1) {
                        throw new ProcessingException(statement, "all registers must be even-numbered");
                     }
                     double div1 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                     double div2 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[2]+1),Coprocessor1.getIntValue(operands[2])));
                     double quot  = div1 / div2;
                     long longQuot = Double.doubleToLongBits(quot);
                     Coprocessor1.updateRegister(operands[0]+1, Binary.highOrderLongToInt(longQuot));
                     Coprocessor1.updateRegister(operands[0], Binary.lowOrderLongToInt(longQuot));
                  }
               }));
         
         
         instructionList.add(
                new BasicInstruction("fsqrt.d f1, f2,f4",
            	 "Floating point Square Root, Double Precision. Computes the square root of the double-prcision floating-point number in register"
            	 + "\n and writes the ounded double-precision result to f1",

                "010001 10001 00000 sssss fffff 000100",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1 || operands[2]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                     double value = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                     long longSqrt = 0;              
                     if (value < 0.0) {
                        // This is subject to refinement later.  Release 4.0 defines floor, ceil, trunc, round
                     	// to act silently rather than raise Invalid Operation exception, so sqrt should do the
                     	// same.  An intermediate step would be to define a setting for FCSR Invalid Operation
                     	// flag, but the best solution is to simulate the FCSR register itself.
                     	// FCSR = Floating point unit Control and Status Register.  DPS 10-Aug-2010
                        longSqrt = Double.doubleToLongBits(Double.NaN);
                        //throw new ProcessingException(statement, "Invalid Operation: sqrt of negative number");
                     } 
                     else {
                        longSqrt = Double.doubleToLongBits(Math.sqrt(value));
                     }
                     Coprocessor1.updateRegister(operands[0]+1, Binary.highOrderLongToInt(longSqrt));
                     Coprocessor1.updateRegister(operands[0], Binary.lowOrderLongToInt(longSqrt));
                  }
               }));
         instructionList.add(
                new BasicInstruction("floor.w.d $f1,$f2",
            	 "Floor double precision to word : Set $f1 to 32-bit integer floor of double-precision float in $f2",

                "010001 10001 00000 sssss fffff 001111",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[1]%2==1) {
                        throw new ProcessingException(statement, "second register must be even-numbered");
                     }
                     double doubleValue = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                  	// DPS 27-July-2010: Since MARS does not simulate the FSCR, I will take the default
                  	// action of setting the result to 2^31-1, if the value is outside the 32 bit range.
                     int floor = (int) Math.floor(doubleValue);
                     if ( Double.isNaN(doubleValue) 
                          || Double.isInfinite(doubleValue)
                          || doubleValue < (double) Integer.MIN_VALUE 
                     	  || doubleValue > (double) Integer.MAX_VALUE ) {
                        floor = Integer.MAX_VALUE;
                     } 
                     Coprocessor1.updateRegister(operands[0], floor);
                  }
               }));
         instructionList.add(
                new BasicInstruction("ceil.w.d $f1,$f2",
            	 "Ceiling double precision to word : Set $f1 to 32-bit integer ceiling of double-precision float in $f2",

                "010001 10001 00000 sssss fffff 001110",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[1]%2==1) {
                        throw new ProcessingException(statement, "second register must be even-numbered");
                     }
                     double doubleValue = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                  	// DPS 27-July-2010: Since MARS does not simulate the FSCR, I will take the default
                  	// action of setting the result to 2^31-1, if the value is outside the 32 bit range.
                     int ceiling = (int) Math.ceil(doubleValue);
                     if ( Double.isNaN(doubleValue) 
                          || Double.isInfinite(doubleValue)
                          || doubleValue < (double) Integer.MIN_VALUE 
                     	  || doubleValue > (double) Integer.MAX_VALUE ) {
                        ceiling = Integer.MAX_VALUE;
                     } 
                     Coprocessor1.updateRegister(operands[0], ceiling);
                  }
               }));
         instructionList.add(
                new BasicInstruction("round.w.d $f1,$f2",
            	 "Round double precision to word : Set $f1 to 32-bit integer round of double-precision float in $f2",

                "010001 10001 00000 sssss fffff 001100",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { // See comments in round.w.s above, concerning MIPS and IEEE 754 standard. 
                    // Until MARS 3.5, I used Math.round, which rounds to nearest but when both are
                    // equal it rounds toward positive infinity.  With Release 3.5, I painstakingly
                    // carry out the MIPS and IEEE 754 standard (round to nearest/even).
                     int[] operands = statement.getOperands();
                     if (operands[1]%2==1) {
                        throw new ProcessingException(statement, "second register must be even-numbered");
                     }
                     double doubleValue = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                     int below=0, above=0; 
                     int round = (int) Math.round(doubleValue);
                  	// See comments in round.w.s above concerning FSCR...  
                     if ( Double.isNaN(doubleValue) 
                          || Double.isInfinite(doubleValue)
                          || doubleValue < (double) Integer.MIN_VALUE 
                     	  || doubleValue > (double) Integer.MAX_VALUE ) {
                        round = Integer.MAX_VALUE;
                     } 
                     else {
                        Double doubleObj = new Double(doubleValue);
                        // If we are EXACTLY in the middle, then round to even!  To determine this,
                        // find next higher integer and next lower integer, then see if distances 
                        // are exactly equal.
                        if (doubleValue < 0.0) {
                           above = doubleObj.intValue(); // truncates
                           below = above - 1;
                        } 
                        else {
                           below = doubleObj.intValue(); // truncates
                           above = below + 1;
                        }
                        if (doubleValue - below == above - doubleValue) { // exactly in the middle?
                           round = (above%2 == 0) ? above : below;
                        }
                     }
                     Coprocessor1.updateRegister(operands[0], round);
                  }
               }));
         instructionList.add(
                new BasicInstruction("trunc.w.d $f1,$f2",
            	 "Truncate double precision to word : Set $f1 to 32-bit integer truncation of double-precision float in $f2",

                "010001 10001 00000 sssss fffff 001101",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[1]%2==1) {
                        throw new ProcessingException(statement, "second register must be even-numbered");
                     }
                     double doubleValue = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                  	// DPS 27-July-2010: Since MARS does not simulate the FSCR, I will take the default
                  	// action of setting the result to 2^31-1, if the value is outside the 32 bit range.
                     int truncate = (int) doubleValue; // Typecasting will round toward zero, the correct action.
                     if ( Double.isNaN(doubleValue) 
                          || Double.isInfinite(doubleValue)
                          || doubleValue < (double) Integer.MIN_VALUE 
                     	  || doubleValue > (double) Integer.MAX_VALUE ) {
                        truncate = Integer.MAX_VALUE;
                     } 
                     Coprocessor1.updateRegister(operands[0], truncate);
                  }
               }));
         instructionList.add(
                new BasicInstruction("bc1t label",
            	 "Branch if FP condition flag 0 true (BC1T, not BCLT) : If Coprocessor 1 condition flag 0 is true (one) then branch to statement at label's address",
                "010001 01000 00001 ffffffffffffffff",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     if (Coprocessor1.getConditionFlag(0)==1)
                     {
                        processBranch(operands[0]);
                     }
                  }
               }));
         instructionList.add(
                new BasicInstruction("bc1t 1,label",
                "Branch if specified FP condition flag true (BC1T, not BCLT) : If Coprocessor 1 condition flag specified by immediate is true (one) then branch to statement at label's address",
                "010001 01000 fff 01 ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     if (Coprocessor1.getConditionFlag(operands[0])==1)
                     {
                        processBranch(operands[1]);
                     }
                  }
               }));
         instructionList.add(
                new BasicInstruction("bc1f label",
                "Branch if FP condition flag 0 false (BC1F, not BCLF) : If Coprocessor 1 condition flag 0 is false (zero) then branch to statement at label's address",
                "010001 01000 00000 ffffffffffffffff",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     if (Coprocessor1.getConditionFlag(0)==0)
                     {
                        processBranch(operands[0]);
                     }
                  
                  }
               }));
         instructionList.add(
                new BasicInstruction("bc1f 1,label",
                "Branch if specified FP condition flag false (BC1F, not BCLF) : If Coprocessor 1 condition flag specified by immediate is false (zero) then branch to statement at label's address",
                "010001 01000 fff 00 ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     if (Coprocessor1.getConditionFlag(operands[0])==0)
                     {
                        processBranch(operands[1]);
                     }
                  
                  }
               }));
         instructionList.add(
                new BasicInstruction("c.eq.s $f0,$f1",
                "Compare equal single precision : If $f0 is equal to $f1, set Coprocessor 1 condition flag 0 true else set it false",
            	 
                "010001 10000 sssss fffff 00000 110010",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     float op1 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[0]));
                     float op2 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                     if (op1 == op2) 
                        Coprocessor1.setConditionFlag(0);
                     else
                        Coprocessor1.clearConditionFlag(0);
                  }
               }));
         instructionList.add(
                new BasicInstruction("c.eq.s 1,$f0,$f1",
                 "Compare equal single precision : If $f0 is equal to $f1, set Coprocessor 1 condition flag specied by immediate to true else set it to false",
               
                "010001 10000 ttttt sssss fff 00 11 0010",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     float op1 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                     float op2 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[2]));
                     if (op1 == op2) 
                        Coprocessor1.setConditionFlag(operands[0]);
                     else
                        Coprocessor1.clearConditionFlag(operands[0]);
                  }
               }));
         instructionList.add(
                new BasicInstruction("c.le.s $f0,$f1",
            	 "Compare less or equal single precision : If $f0 is less than or equal to $f1, set Coprocessor 1 condition flag 0 true else set it false",

                "010001 10000 sssss fffff 00000 111110",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     float op1 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[0]));
                     float op2 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                     if (op1 <= op2) 
                        Coprocessor1.setConditionFlag(0);
                     else
                        Coprocessor1.clearConditionFlag(0);
                  }
               }));
         instructionList.add(
                new BasicInstruction("c.le.s 1,$f0,$f1",
            	 "Compare less or equal single precision : If $f0 is less than or equal to $f1, set Coprocessor 1 condition flag specified by immediate to true else set it to false",

                "010001 10000 ttttt sssss fff 00 111110",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     float op1 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                     float op2 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[2]));
                     if (op1 <= op2) 
                        Coprocessor1.setConditionFlag(operands[0]);
                     else
                        Coprocessor1.clearConditionFlag(operands[0]);
                  }
               }));
         instructionList.add(
                new BasicInstruction("c.lt.s $f0,$f1",
            	 "Compare less than single precision : If $f0 is less than $f1, set Coprocessor 1 condition flag 0 true else set it false",

                "010001 10000 sssss fffff 00000 111100",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     float op1 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[0]));
                     float op2 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                     if (op1 < op2) 
                        Coprocessor1.setConditionFlag(0);
                     else
                        Coprocessor1.clearConditionFlag(0);
                  }
               }));
         instructionList.add(
                new BasicInstruction("c.lt.s 1,$f0,$f1",
                "Compare less than single precision : If $f0 is less than $f1, set Coprocessor 1 condition flag specified by immediate to true else set it to false",
            	 
                "010001 10000 ttttt sssss fff 00 111100",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     float op1 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                     float op2 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[2]));
                     if (op1 < op2) 
                        Coprocessor1.setConditionFlag(operands[0]);
                     else
                        Coprocessor1.clearConditionFlag(operands[0]);
                  }
               }));
         instructionList.add(
                new BasicInstruction("c.eq.d $f2,$f4",
            	 "Compare equal double precision : If $f2 is equal to $f4 (double-precision), set Coprocessor 1 condition flag 0 true else set it false",

                "010001 10001 sssss fffff 00000 110010",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                     double op1 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[0]+1),Coprocessor1.getIntValue(operands[0])));
                     double op2 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                     if (op1 == op2) 
                        Coprocessor1.setConditionFlag(0);
                     else
                        Coprocessor1.clearConditionFlag(0);
                  }
               }));
         instructionList.add(
                new BasicInstruction("c.eq.d 1,$f2,$f4",
            	 "Compare equal double precision : If $f2 is equal to $f4 (double-precision), set Coprocessor 1 condition flag specified by immediate to true else set it to false",

                "010001 10001 ttttt sssss fff 00 110010",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[1]%2==1 || operands[2]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                     double op1 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                     double op2 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[2]+1),Coprocessor1.getIntValue(operands[2])));
                     if (op1 == op2) 
                        Coprocessor1.setConditionFlag(operands[0]);
                     else
                        Coprocessor1.clearConditionFlag(operands[0]);
                  }
               }));
         instructionList.add(
                new BasicInstruction("c.le.d $f2,$f4",
            	 "Compare less or equal double precision : If $f2 is less than or equal to $f4 (double-precision), set Coprocessor 1 condition flag 0 true else set it false",

                "010001 10001 sssss fffff 00000 111110",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                     double op1 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[0]+1),Coprocessor1.getIntValue(operands[0])));
                     double op2 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                     if (op1 <= op2) 
                        Coprocessor1.setConditionFlag(0);
                     else
                        Coprocessor1.clearConditionFlag(0);
                  }
               }));
         instructionList.add(
                new BasicInstruction("c.le.d 1,$f2,$f4",
            	 "Compare less or equal double precision : If $f2 is less than or equal to $f4 (double-precision), set Coprocessor 1 condition flag specfied by immediate true else set it false",

                "010001 10001 ttttt sssss fff 00 111110",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     if (operands[1]%2==1 || operands[2]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                     double op1 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                     double op2 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[2]+1),Coprocessor1.getIntValue(operands[2])));
                     if (op1 <= op2) 
                        Coprocessor1.setConditionFlag(operands[0]);
                     else
                        Coprocessor1.clearConditionFlag(operands[0]);
                  }
               }));
         instructionList.add(
                new BasicInstruction("c.lt.d $f2,$f4",
            	 "Compare less than double precision : If $f2 is less than $f4 (double-precision), set Coprocessor 1 condition flag 0 true else set it false",

                "010001 10001 sssss fffff 00000 111100",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                     double op1 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[0]+1),Coprocessor1.getIntValue(operands[0])));
                     double op2 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                     if (op1 < op2) 
                        Coprocessor1.setConditionFlag(0);
                     else
                        Coprocessor1.clearConditionFlag(0);
                  }
               }));
         instructionList.add(
                new BasicInstruction("c.lt.d 1,$f2,$f4",
            	 "Compare less than double precision : If $f2 is less than $f4 (double-precision), set Coprocessor 1 condition flag specified by immediate to true else set it to false",

                "010001 10001 ttttt sssss fff 00 111100",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[1]%2==1 || operands[2]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                     double op1 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                     double op2 = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[2]+1),Coprocessor1.getIntValue(operands[2])));
                     if (op1 < op2) 
                        Coprocessor1.setConditionFlag(operands[0]);
                     else
                        Coprocessor1.clearConditionFlag(operands[0]);
                  }
               }));
         instructionList.add(
                new BasicInstruction("abs.s $f0,$f1",
            	 "Floating point absolute value single precision : Set $f0 to absolute value of $f1, single precision",

                "010001 10000 00000 sssss fffff 000101",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                  	// I need only clear the high order bit!
                     Coprocessor1.updateRegister(operands[0], 
                                         Coprocessor1.getIntValue(operands[1]) & Integer.MAX_VALUE);
                  }
               }));
         instructionList.add(
                new BasicInstruction("abs.d $f2,$f4",
            	 "Floating point absolute value double precision : Set $f2 to absolute value of $f4, double precision",

                "010001 10001 00000 sssss fffff 000101",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                  	// I need only clear the high order bit of high word register!
                     Coprocessor1.updateRegister(operands[0]+1, 
                                         Coprocessor1.getIntValue(operands[1]+1) & Integer.MAX_VALUE);
                     Coprocessor1.updateRegister(operands[0], 
                                         Coprocessor1.getIntValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("cvt.d.s $f2,$f1",
            	 "Convert from single precision to double precision : Set $f2 to double precision equivalent of single precision value in $f1",

                "010001 10000 00000 sssss fffff 100001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1) {
                        throw new ProcessingException(statement, "first register must be even-numbered");
                     }
                  	// convert single precision in $f1 to double stored in $f2
                     long result = Double.doubleToLongBits(
                          (double)Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1])));
                     Coprocessor1.updateRegister(operands[0]+1, Binary.highOrderLongToInt(result));
                     Coprocessor1.updateRegister(operands[0], Binary.lowOrderLongToInt(result));
                  }
               }));
         instructionList.add(
                new BasicInstruction("cvt.d.w $f2,$f1",
            	 "Convert from word to double precision : Set $f2 to double precision equivalent of 32-bit integer value in $f1",

                "010001 10100 00000 sssss fffff 100001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1) {
                        throw new ProcessingException(statement, "first register must be even-numbered");
                     }
                  	// convert integer to double (interpret $f1 value as int?)
                     long result = Double.doubleToLongBits(
                          (double)Coprocessor1.getIntValue(operands[1]));
                     Coprocessor1.updateRegister(operands[0]+1, Binary.highOrderLongToInt(result));
                     Coprocessor1.updateRegister(operands[0], Binary.lowOrderLongToInt(result));
                  }
               }));
         instructionList.add(
                new BasicInstruction("cvt.s.d $f1,$f2",
                "Convert from double precision to single precision : Set $f1 to single precision equivalent of double precision value in $f2",
            	 
                "010001 10001 00000 sssss fffff 100000",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                  	// convert double precision in $f2 to single stored in $f1
                     if (operands[1]%2==1) {
                        throw new ProcessingException(statement, "second register must be even-numbered");
                     }
                     double val = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                     Coprocessor1.updateRegister(operands[0], Float.floatToIntBits((float)val));
                  }
               }));
         instructionList.add(
                new BasicInstruction("cvt.s.w $f0,$f1",
            	 "Convert from word to single precision : Set $f0 to single precision equivalent of 32-bit integer value in $f2",

                "010001 10100 00000 sssss fffff 100000",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                  	// convert integer to single (interpret $f1 value as int?)
                     Coprocessor1.updateRegister(operands[0], 
                         Float.floatToIntBits((float)Coprocessor1.getIntValue(operands[1])));
                  }
               }));
         instructionList.add(
                new BasicInstruction("cvt.w.d $f1,$f2",
            	 "Convert from double precision to word : Set $f1 to 32-bit integer equivalent of double precision value in $f2",

                "010001 10001 00000 sssss fffff 100100",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                  	// convert double precision in $f2 to integer stored in $f1
                     if (operands[1]%2==1) {
                        throw new ProcessingException(statement, "second register must be even-numbered");
                     }
                     double val = Double.longBitsToDouble(Binary.twoIntsToLong(
                              Coprocessor1.getIntValue(operands[1]+1),Coprocessor1.getIntValue(operands[1])));
                     Coprocessor1.updateRegister(operands[0], (int) val);
                  }
               }));
         instructionList.add(
                new BasicInstruction("cvt.w.s $f0,$f1",
            	 "Convert from single precision to word : Set $f0 to 32-bit integer equivalent of single precision value in $f1",

                "010001 10000 00000 sssss fffff 100100",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                  	// convert single precision in $f1 to integer stored in $f0
                     Coprocessor1.updateRegister(operands[0], 
                             (int)Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1])));
                  }
               }));
         instructionList.add(
                new BasicInstruction("mov.d $f2,$f4",
            	 "Move floating point double precision : Set double precision $f2 to double precision value in $f4",

                "010001 10001 00000 sssss fffff 000110",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                     Coprocessor1.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                     Coprocessor1.updateRegister(operands[0]+1, Coprocessor1.getIntValue(operands[1]+1));
                  }
               }));
         instructionList.add(
                new BasicInstruction("movf.d $f2,$f4",
            	 "Move floating point double precision : If condition flag 0 false, set double precision $f2 to double precision value in $f4",

                "010001 10001 000 00 sssss fffff 010001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                     if (Coprocessor1.getConditionFlag(0)==0) {
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                        Coprocessor1.updateRegister(operands[0]+1, Coprocessor1.getIntValue(operands[1]+1));
                     }
                  }
               }));
         instructionList.add(
                new BasicInstruction("movf.d $f2,$f4,1",
            	 "Move floating point double precision : If condition flag specified by immediate is false, set double precision $f2 to double precision value in $f4",

                "010001 10001 ttt 00 sssss fffff 010001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                     if (Coprocessor1.getConditionFlag(operands[2])==0) {
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                        Coprocessor1.updateRegister(operands[0]+1, Coprocessor1.getIntValue(operands[1]+1));
                     }
                  }
               }));
         instructionList.add(
                new BasicInstruction("movt.d $f2,$f4",
            	 "Move floating point double precision : If condition flag 0 true, set double precision $f2 to double precision value in $f4",

                "010001 10001 000 01 sssss fffff 010001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                     if (Coprocessor1.getConditionFlag(0)==1) {
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                        Coprocessor1.updateRegister(operands[0]+1, Coprocessor1.getIntValue(operands[1]+1));
                     }
                  }
               }));
         instructionList.add(
                new BasicInstruction("movt.d $f2,$f4,1",
            	 "Move floating point double precision : If condition flag specified by immediate is true, set double precision $f2 to double precision value in $f4e",

                "010001 10001 ttt 01 sssss fffff 010001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                     if (Coprocessor1.getConditionFlag(operands[2])==1) {
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                        Coprocessor1.updateRegister(operands[0]+1, Coprocessor1.getIntValue(operands[1]+1));
                     }
                  }
               }));
         instructionList.add(
                new BasicInstruction("movn.d $f2,$f4,$t3",
            	 "Move floating point double precision : If $t3 is not zero, set double precision $f2 to double precision value in $f4",

                "010001 10001 ttttt sssss fffff 010011",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                     if (RV32IRegisters.getValue(operands[2])!=0) {
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                        Coprocessor1.updateRegister(operands[0]+1, Coprocessor1.getIntValue(operands[1]+1));
                     }
                  }
               }));
         instructionList.add(
                new BasicInstruction("movz.d $f2,$f4,$t3",
            	 "Move floating point double precision : If $t3 is zero, set double precision $f2 to double precision value in $f4",

                "010001 10001 ttttt sssss fffff 010010",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                     if (RV32IRegisters.getValue(operands[2])==0) {
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                        Coprocessor1.updateRegister(operands[0]+1, Coprocessor1.getIntValue(operands[1]+1));
                     }
                  }
               }));
         instructionList.add(
                new BasicInstruction("mov.s $f0,$f1",
            	 "Move floating point single precision : Set single precision $f0 to single precision value in $f1",

                "010001 10000 00000 sssss fffff 000110",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     Coprocessor1.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("movf.s $f0,$f1",
            	 "Move floating point single precision : If condition flag 0 is false, set single precision $f0 to single precision value in $f1",

                "010001 10000 000 00 sssss fffff 010001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (Coprocessor1.getConditionFlag(0)==0)
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("movf.s $f0,$f1,1",
            	 "Move floating point single precision : If condition flag specified by immediate is false, set single precision $f0 to single precision value in $f1e",

                "010001 10000 ttt 00 sssss fffff 010001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (Coprocessor1.getConditionFlag(operands[2])==0)
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("movt.s $f0,$f1",
            	 "Move floating point single precision : If condition flag 0 is true, set single precision $f0 to single precision value in $f1e",

                "010001 10000 000 01 sssss fffff 010001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (Coprocessor1.getConditionFlag(0)==1)
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("movt.s $f0,$f1,1",
            	 "Move floating point single precision : If condition flag specified by immediate is true, set single precision $f0 to single precision value in $f1e",

                "010001 10000 ttt 01 sssss fffff 010001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (Coprocessor1.getConditionFlag(operands[2])==1)
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("movn.s $f0,$f1,$t3",
            	 "Move floating point single precision : If $t3 is not zero, set single precision $f0 to single precision value in $f1",

                "010001 10000 ttttt sssss fffff 010011",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[2])!=0)
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("movz.s $f0,$f1,$t3",
            	 "Move floating point single precision : If $t3 is zero, set single precision $f0 to single precision value in $f1",

                "010001 10000 ttttt sssss fffff 010010",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (RV32IRegisters.getValue(operands[2])==0)
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("mfc1 $t1,$f1",
                "Move from Coprocessor 1 (FPU) : Set $t1 to value in Coprocessor 1 register $f1",
            	 
                "010001 00000 fffff sssss 00000 000000", 
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     RV32IRegisters.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("mtc1 $t1,$f1",
                "Move to Coprocessor 1 (FPU) : Set Coprocessor 1 register $f1 to value in $t1",
            	 
                "010001 00100 fffff sssss 00000 000000",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     Coprocessor1.updateRegister(operands[1], RV32IRegisters.getValue(operands[0]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("neg.d $f2,$f4",
                "Floating point negate double precision : Set double precision $f2 to negation of double precision value in $f4",
            	 
                "010001 10001 00000 sssss fffff 000111",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     if (operands[0]%2==1 || operands[1]%2==1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                     }
                  	// flip the sign bit of the second register (high order word) of the pair
                     int value = Coprocessor1.getIntValue(operands[1]+1);
                     Coprocessor1.updateRegister(operands[0]+1, 
                          ((value < 0) ? (value & Integer.MAX_VALUE) : (value | Integer.MIN_VALUE)));
                     Coprocessor1.updateRegister(operands[0], Coprocessor1.getIntValue(operands[1]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("neg.s $f0,$f1",
                "Floating point negate single precision : Set single precision $f0 to negation of single precision value in $f1",
                "010001 10000 00000 sssss fffff 000111",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     int[] operands = statement.getOperands();
                     int value = Coprocessor1.getIntValue(operands[1]);
                  	// flip the sign bit
                     Coprocessor1.updateRegister(operands[0], 
                          ((value < 0) ? (value & Integer.MAX_VALUE) : (value | Integer.MIN_VALUE)));
                  }
               }));
               */
         instructionList.add(
                new I_type.LW_type("flw f1,-100(t2)",
                "Load word into Coprocessor 1 (FPU) : Set f1 to 32-bit value from effective memory word address",
                "ssssssssssssttttt010fffff0000111",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     long[] operands = statement.getOperands();
                     try
                     {
                        Coprocessor1.updateRegister((int)operands[0],
                            Globals.memory.getWord(
                            RV32IRegisters.getValue(((int)operands[2])) + operands[1]));
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));		 
         
         instructionList.add(
                new S_type("fsw ft1,-100(t2)",
            	 "Floating-point store word. Stores the single-precision floating point number in register f[rs2] "
                		+ "to memory at address x[rs1]+sign-extend(offset)",
                 "tttttttsssssfffff010sssss01001 11",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     long[] operands = statement.getOperands();
                     try
                     {
                        Globals.memory.setWord((int)
                        	RV32IRegisters.getValue(((int)operands[2])) + operands[1],
                            Coprocessor1.getIntValue(operands[0]));
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));
         instructionList.add(
                 new R_type("fsgnj.s f1,f2,f3",
                 "Floating-point Sign Inject, Single-Presicion.",
                 "0010001tttttsssss000fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                     
                      int firstOperand = Coprocessor1.getIntValue(operands[1]);
                      int secondOperand = Coprocessor1.getIntValue(operands[2]);
                      int newspfp = ((firstOperand&0x80000000)|(secondOperand&0x7fffffff));
                      Coprocessor1.updateRegister(operands[0], newspfp);
                        
                   }
                    
                }));	
         instructionList.add(
                 new R_type("fsgnjn.s ft1,ft2,ft3",
                 "Floating-point Sign Inject-Negate, Single-Presicion.",
                 "0010000tttttsssss001fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                     
                      int firstOperand = Coprocessor1.getIntValue(operands[1]);
                      int secondOperand = Coprocessor1.getIntValue(operands[2]);
                      int newspfp = (((~firstOperand)&0x80000000)|(secondOperand&0x7fffffff));
                      Coprocessor1.updateRegister(operands[0], newspfp);
                        
                   }
                    
                }));	
         instructionList.add(
                 new R_type("fsgnjx.s ft1,ft2,ft3",
                 "Floating-point Sign Inject-Xor, Single-Presicion.",
                 "0010000tttttsssss010fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                     
                      int firstOperand = Coprocessor1.getIntValue(operands[1]);
                      int secondOperand = Coprocessor1.getIntValue(operands[2]);
                      int newspfp = (((firstOperand^secondOperand)&0x80000000)|(secondOperand&0x7fffffff));
                      Coprocessor1.updateRegister(operands[0], newspfp);
                        
                   }
                    
                }));
         instructionList.add(
                 new R_type("fmin.s ft1,ft2,ft3",
                 "Floating-point Minimum, Single-Presicion.",
                 "0010100tttttsssss000fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                     
                      Coprocessor1.updateRegister(operands[0], 
                    		  Integer.min((int)operands[1], (int)operands[2]));
                        
                   }
                    
                }));	
         instructionList.add(
                 new R_type("fmax.s ft1,ft2,ft3",
                 "Floating-point Maximum, Single-Presicion.",
                 "0010100tttttsssss001fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                     
                      Coprocessor1.updateRegister(operands[0], 
                    		  Integer.max((int)operands[1], (int)operands[2]));
                        
                   }
                    
                }));
         instructionList.add(
                 new R_type("fmv.x.w t1,ft2",
                 "Floating-point Move Word to Integer, Single-Presicion.",
                 "111100000000sssss000fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                     
                      Coprocessor1.updateRegister(operands[0], operands[1]);
                        
                   }
                    
                }));
         
         instructionList.add(
                 new R_type("feq.s t1,ft2,ft3",
                 "Floating-point Equals, Single-Presicion.",
                 "1010000tttttsssss010fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                     
                      int equals = (Binary.highOrderLongToInt(operands[1]) == 
                    		  Binary.highOrderLongToInt(operands[2]))? 1 : 0;
                    	  Coprocessor1.updateRegister(operands[0], equals);
                        
                   }
                    
                }));
         instructionList.add(
                 new R_type("flt.s t1,ft2,ft3",
                 "Floating-point Less Than, Single-Presicion.",
                 "1010000tttttsssss001fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                     
                      int equals = (Binary.highOrderLongToInt(operands[1])
                    		  < Binary.highOrderLongToInt(operands[2]))? 1 : 0;
                    	  Coprocessor1.updateRegister(operands[0], equals);
                        
                   }
                    
                }));
         instructionList.add(
                 new R_type("fle.s t1,ft2,ft3",
                 "Floating-point Less Than or Equal, Single-Presicion.",
                 "1010000tttttsssss000fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                     
                      int equals = (Binary.highOrderLongToInt(operands[1]) <=
                    		  Binary.highOrderLongToInt(operands[2]))? 1 : 0;
                    	  Coprocessor1.updateRegister(operands[0], equals);
                        
                   }
                    
                }));
         
         instructionList.add(
                 new R_type("fclass.s t1,ft2",
                 "Floating-point Classify, Single-Precision. ", 
                 "111000000000sssss001fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                      float rs1 = Float.intBitsToFloat(Coprocessor1.getIntValue(operands[1]));
                  
                      Coprocessor1.updateRegister(operands[0], Coprocessor1.getFclass(rs1));
                   }
                }));
         
       	////////////////////////////  THE RV32D INSTRUCTIONS  ////////////////////////////
         
         instructionList.add(// no printed reference, got opcode from SPIM
                new I_type.LW_type("fld ft1,-100(t1)",
            	 "Load double word Coprocessor 1 (FPU)) : Set ft1 to 64-bit value from effective memory doubleword address",
                "ttttttttttttsssss011fffff0000111",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     long[] operands = statement.getOperands();
                  //   if (operands[0]%2==1) {
                   //     throw new ProcessingException(statement, "first register must be even-numbered");
                 //  }
                  	// IF statement added by DPS 13-July-2011.
                     if (!Globals.memory.doublewordAligned(RV32IRegisters.getValue(operands[2])
                    		 + Binary.signExtend(operands[1], 12, 32))) {
                        throw new ProcessingException(statement,
                           new AddressErrorException("address not aligned on doubleword boundary ",
                           Exceptions.ADDRESS_EXCEPTION_LOAD, RV32IRegisters.getValue(operands[2]) +
                           Binary.signExtend(operands[1], 12, 32)));
                     }
                                    
                     try
                     {
                        Coprocessor1.updateRegister(operands[0],
                            Globals.memory.getDoubleWord((int)
                            (RV32IRegisters.getValue(operands[2]) + Binary.signExtend(operands[1], 12, 32))));
                      //  Coprocessor1.updateRegister(operands[0]+1,
                      //      Globals.memory.getWord(
                      //      RV32IRegisters.getValue(operands[2]) + operands[1] + 4));
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));
        	 
         
         instructionList.add( // no printed reference, got opcode from SPIM
                new S_type("fsd ft1,-100(t1)",
            	 "Store double word from Coprocessor 1 (FPU)) : Store 64 bit value in ft1 to effective memory doubleword address",
                 "tttttttsssssfffff011sssss0100111",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  { 
                     long[] operands = statement.getOperands();
                   //  if (operands[0]%2==1) {
                   //     throw new ProcessingException(statement, "first register must be even-numbered");
                   //  }
                  	// IF statement added by DPS 13-July-2011.
                     if (!Globals.memory.doublewordAligned(RV32IRegisters.getValue(operands[2]) + 
                    		 Binary.signExtend(operands[1], 12, 32))) {
                        throw new ProcessingException(statement,
                           new AddressErrorException("address not aligned on doubleword boundary ",
                           Exceptions.ADDRESS_EXCEPTION_STORE, RV32IRegisters.getValue(operands[2]) + 
                           Binary.signExtend(operands[1], 12, 32)));
                     }
                     try
                     {
                        Globals.memory.setDoubleWord((int)
                            (RV32IRegisters.getValue(operands[2]) + Binary.signExtend(operands[1], 12, 32)),
                            Coprocessor1.getIntValue(operands[0]));
                     //   Globals.memory.setWord(
                     //       RV32IRegisters.getValue(operands[2]) + operands[1] + 4,
                     //       Coprocessor1.getIntValue(operands[0]+1));
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));
         
         instructionList.add(
                 new R_type("fsgnj.d f1,f2,f3",
                 "Floating-point Sign Inject, Double-Presicion.",
                 "0010001tttttsssss000fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();     
                      long firstOperand = Coprocessor1.getLongValue(operands[1]);
                      long secondOperand = Coprocessor1.getLongValue(operands[2]);
                      long newspfp = ((firstOperand&0x8000000000000000L)|(secondOperand&0x7fffffffffffffffL));
                      Coprocessor1.updateRegister(operands[0], newspfp);
                        
                   }
                    
                }));	
         instructionList.add(
                 new R_type("fsgnjn.d ft1,ft2,ft3",
                 "Floating-point Sign Inject-Negate, Double-Presicion.",
                 "0010001tttttsssss001fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                        long[] operands = statement.getOperands();     
                        long firstOperand = Coprocessor1.getLongValue(operands[1]);
                        long secondOperand = Coprocessor1.getLongValue(operands[2]);
                        long newspfp = ((~(firstOperand&0x8000000000000000L))|(secondOperand&0x7fffffffffffffffL));
                        Coprocessor1.updateRegister(operands[0], newspfp);
                          
                        
                   }
                    
                }));	
         instructionList.add(
                 new R_type("fsgnjx.d ft1,ft2,ft3",
                 "Floating-point Sign Inject-Xor, Double-Presicion.",
                 "0010001tttttsssss010fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                      long firstOperand = Coprocessor1.getLongValue(operands[1]);
                      long secondOperand = Coprocessor1.getLongValue(operands[2]);
                      long newspfp = (((firstOperand^secondOperand)&0x8000000000000000L)|(secondOperand&0x7fffffffffffffffL));
                      Coprocessor1.updateRegister(operands[0], newspfp);
                        
                   }
                    
                }));
         instructionList.add(
                 new R_type("fmin.d ft1,ft2,ft3",
                 "Floating-point Minimum, Double-Presicion.",
                 "0010101tttttsssss000fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                     
                      Coprocessor1.updateRegister(operands[0], 
                    		  Long.min(operands[1], operands[2]));
                        
                   }
                    
                }));	
         instructionList.add(
                 new R_type("fmax.d ft1,ft2,ft3",
                 "Floating-point Maximum, Single-Presicion.",
                 "0010101tttttsssss001fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                     
                      Coprocessor1.updateRegister(operands[0], 
                    		  Long.max(operands[1], operands[2]));
                        
                   }
                    
                }));
        /* instructionList.add(
                 new R_type("fmv.x.w t1,ft2",
                 "Floating-point Move Word to Integer, Single-Presicion.",
                 "001010000000sssss000fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                     
                      Coprocessor1.updateRegister(operands[0], operands[1]);
                        
                   }
                    
                }));
         */
         instructionList.add(
                 new R_type("feq.d t1,ft2,ft3",
                 "Floating-point Equals, Double-Presicion.",
                 "1010001tttttsssss010fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                     
                      int equals = (operands[1] == operands[2])? 1 : 0;
                    	  Coprocessor1.updateRegister(operands[0], equals);
                        
                   }
                    
                }));
         instructionList.add(
                 new R_type("flt.d t1,ft2,ft3",
                 "Floating-point Less Than, Double-Presicion.",
                 "1010001tttttsssss001fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                     
                      int equals = (operands[1] < operands[2])? 1 : 0;
                    	  Coprocessor1.updateRegister(operands[0], equals);
                        
                   }
                    
                }));
         instructionList.add(
                 new R_type("fle.d t1,ft2,ft3",
                 "Floating-point Less Than or Equal, Single-Presicion.",
                 "1010001tttttsssss000fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                     
                      int equals = (operands[1] <= operands[2])? 1 : 0;
                    	  Coprocessor1.updateRegister(operands[0], equals);
                        
                   }
                    
                }));
         
         instructionList.add(
                 new R_type("fclass.d ft1,ft2",
                 "Floating-point Classify, Double-Precision. ", 
                 "111000100000sssss001fffff1010011",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   { 
                      long[] operands = statement.getOperands();
                      double rs1 = Double.longBitsToDouble(Coprocessor1.getIntValue(operands[1]));
                  
                      Coprocessor1.updateRegister(operands[0], Coprocessor1.getFclass(rs1));
                   }
                }));
         
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
   	 *
   	 * The parameter is displacement operand from instruction.
   	 *
   	 * Handles delayed branching if that setting is enabled.
   	 */
   	 // 4 January 2008 DPS:  The subtraction of 4 bytes (instruction length) after
   	 // the shift has been removed.  It is left in as commented-out code below.
   	 // This has the effect of always branching as if delayed branching is enabled, 
   	 // even if it isn't.  This mod must work in conjunction with
   	 // ProgramStatement.java, buildBasicStatementFromBasicInstruction() method near
   	 // the bottom (currently line 194, heavily commented).
   	 
       private void processBranch(long displacement) {
         if (Globals.getSettings().getDelayedBranchingEnabled()) {
            // Register the branch target address (absolute byte address).
            DelayedBranch.register(GenMath.add(RV32IRegisters.getProgramCounter(), (displacement << 2)));
         } 
         else {
            // Decrement needed because PC has already been incremented
            RV32IRegisters.setProgramCounter(GenMath.add(
                RV32IRegisters.getProgramCounter()
                , (displacement << 2))); // - Instruction.INSTRUCTION_LENGTH);	 
         }
         
      }
   
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

