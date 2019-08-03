 package mars.mips.hardware;

   import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observer;
import static mars.util.Math2.*;

   import mars.Globals;
   import mars.assembler.SymbolTable;
import mars.mips.hardware.memory.Memory;
import mars.mips.instructions.Instruction;
   import mars.util.Binary;

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
  *  Represents the collection of MIPS registers.
  *   @author Jason Bumgarner, Jason Shrewsbury
  *   @version June 2003
  **/

    public  class RV32IRegisters {
   
      public static final int GLOBAL_POINTER_REGISTER = 3;
      public static final int STACK_POINTER_REGISTER = 2;
   
      private static ArrayList<Register> regFile = new ArrayList<Register>(
    		Arrays.asList(
            new Register("zero", 0, 0), new Register("ra", 1, 0),
        	new Register("sp", STACK_POINTER_REGISTER, Memory.stackPointer),
        	new Register("gp", GLOBAL_POINTER_REGISTER, Memory.globalPointer),
         	new Register("tp", 4, 0),new Register("t0", 5, 0),
         	new Register("t1", 6, 0),new Register("t2", 7, 0),
         	new Register("s0", 8, 0),new Register("s1", 9, 0),
         	new Register("a0", 10, 0),new Register("a1", 11, 0),
         	new Register("a2", 12, 0),new Register("a3", 13, 0), 
         	new Register("a4", 14, 0),new Register("a5", 15, 0),
         	new Register("a6", 16, 0),new Register("a7", 17, 0),
         	new Register("s2", 18, 0),new Register("s3", 19, 0),
         	new Register("s4", 20, 0),new Register("s5", 21, 0),
         	new Register("s6", 22, 0),new Register("s7", 23, 0),
         	new Register("s8", 24, 0),new Register("s9", 25, 0),
         	new Register("s10", 26, 0),new Register("s11", 27, 0),
         	new Register("t3", 28, 0),new Register("t4", 29, 0),
         	new Register("t5", 30, 0),new Register("t6", 31, 0)
           )
    	 );
         												  
      private static Register programCounter= new Register("pc", 32, Memory.getInstance().getTextTable().getBaseAddress()); 

      
   	/**
   	  *  This method updates the register value who's Number is num.  Also handles the lo and hi registers
   	  *   @param num Register to set the value of.
   	  *   @param val The desired value for the register.
   	  **/
   	  
       public static Number updateRegister(long num, Number val){
           Number old = 0;
           int num2 = (int)num;
           if(num2 > 0 && num2 < 32) {
                    old = ((Globals.getSettings().getBackSteppingEnabled())
                          ? Globals.program.getBackStepper().addRegisterFileRestore(num2, regFile.get(num2).setValue(val))
                       	: regFile.get(num2).setValue(val));
           }

           return old;
        }
       
       
       
       
   	/**
   	  *  Sets the value of the register given to the value given.
   	  *   @param reg Name of register to set the value of.
   	  *   @param val The desired value for the register.
   	  **/
   	
       public static void updateRegister(String reg, int val){
         if(reg.equals("zero")){
         }
         else{
            for (int i=0; i< regFile.size(); i++){
               if(regFile.get(i).getName().equals(reg)) {
                  updateRegister(i,val);
                  break;
               }
            }}
      }
      
      /**
   	  *  Returns the value of the register who's Number is num.
   	  *   @param num The register number.
   	  *   @return The value of the given register.
   	  **/
   	
       public static Number getValue(Number num){
    	   return regFile.get(num.intValue()).getValue();
       }
       
       
       
      	/**
   		  *  For getting the Number representation of the register.
   		  *   @param n The string formatted register name to look for.
   		  *   @return The Number of the register represented by the string
   		  *   or -1 if no match.
   		  **/	
      		
       public static int getNumber(String n){
         int j=-1;
         for (int i=0; i< regFile.size(); i++){
            if(regFile.get(i).getName().equals(n)) {
               j = regFile.get(i).getNumber();
               break;
            }
         } 
         return j;     
      }
     
   	/**
   	  *  For returning the set of registers.
   	  *   @return The set of registers.
   	  **/
   	
       public static ArrayList<Register> getRegisters(){
         return regFile;
      }
      
   	/**
   	  *  Get register object corresponding to given name.  If no match, return null.
   	  *   @param Rname The register name, either in $0 or $zero format.
   	  *   @return The register object,or null if not found.
   	  **/
   	
       public static Register getUserRegister(String Rname) {
         Register reg = null;
         if (Rname.charAt(0) == 'x') {
        	
        	 try {
                   // check for register Number 0-31.
                   reg = regFile.get(Binary.stringToInt(Rname.substring(1)));
            }
            catch (Exception e) {
                   // handles both NumberFormat and ArrayIndexOutOfBounds
                   // check for register mnemonic zero thru ra
            	reg = null;
            }
        	
         }
         else
         {
        	 reg = null; // just to be sure
                   // just do linear search; there aren't that many registers
                  for (int i=0; i < regFile.size(); i++) {
                     if (Rname.equals(regFile.get(i).getName())) {
                        reg = regFile.get(i);
                        break;
                     }
                  }
         }
         return reg;
      }
   
   	/**
   	  *  For initializing the Program Counter.  Do not use this to implement jumps and
   	  *  branches, as it will NOT record a backstep entry with the restore value.
   	  *  If you need backstepping capability, use setProgramCounter instead.
   	  *   @param value The value to set the Program Counter to.
   	  **/
     
       public static void initializeProgramCounter(long value){
         programCounter.setValue(value);
      }
       
       public static void initializeProgramCounter(Number value){
           programCounter.setValue(value);
        }
   	
   	/**
   	 *  Will initialize the Program Counter to either the default reset value, or the address 
   	 *  associated with source program global label "main", if it exists as a text segment label
   	 *  and the global setting is set.
   	 *  @param startAtMain  If true, will set program counter to address of statement labeled
   	 *  'main' (or other defined start label) if defined.  If not defined, or if parameter false,
   	 *  will set program counter to default reset value.
   	 **/
   	 
       public static void initializeProgramCounter(boolean startAtMain) {  
         Number mainAddr = Globals.symbolTable.getAddress(SymbolTable.getStartLabel());
         if (startAtMain && !isEq(mainAddr, SymbolTable.NOT_FOUND) && 
        		 (Memory.getInstance().getTextTable().inSegment(mainAddr) 
        				 || Memory.getInstance().getKernelTextTable().inSegment(mainAddr))) {
            initializeProgramCounter(mainAddr);
         } 
         else {
            initializeProgramCounter( programCounter.getResetValue());
         }
      }
   	
   	/**
   	  *  For setting the Program Counter.  Note that ordinary PC update should be done using
   	  *  incrementPC() method. Use this only when processing jumps and branches.
   	  *   @param value The value to set the Program Counter to.
   	  *   @return previous PC value
   	  **/
     
       public static Number setProgramCounter(Number value){
         Number old = programCounter.getValue();
         programCounter.setValue(value);
         if (Globals.getSettings().getBackSteppingEnabled()) {
            Globals.program.getBackStepper().addPCRestore(old.longValue());
         } 
         return old;
      }
     
     /**
      *  For returning the program counters value.
   	  *  @return The program counters value as an int.
   	  **/
   	 
       public static Number getProgramCounter(){
         return programCounter.getValue();
      }
   
     /**
      *  Returns Register object for program counter.  Use with caution.
   	*  @return program counter's Register object.
   	*/
       public static Register getProgramCounterRegister() {
         return programCounter;
      }
   	
     /**
      *  For returning the program counter's initial (reset) value.
   	  *  @return The program counter's initial value
   	  **/
   	 
       public static int getInitialProgramCounter(){
         return (int) programCounter.getResetValue();
      }
   	
   	/**
   	  *  Method to reinitialize the values of the registers.
   	  *  <b>NOTE:</b> Should <i>not</i> be called from command-mode MARS because this
   	  *  this method uses global settings from the registry.  Command-mode must operate
   	  *  using only the command switches, not registry settings.  It can be called
   	  *  from tools running stand-alone, and this is done in 
   	  *  <code>AbstractMarsToolAndApplication</code>.
   	  **/
   	
       public static void resetRegisters(){
         for(int i=0; i< regFile.size(); i++){
            regFile.get(i).resetValue();
         }
         initializeProgramCounter(Globals .getSettings().getStartAtMain());// replaces "programCounter.resetValue()", DPS 3/3/09

      }
      
     /**
       *  Method to increment the Program counter in the general case (not a jump or branch).
   	 **/
   
       public static void incrementPC(){
    	   if(MemoryConfigurations.getCurrentConfiguration().getConfigurationIdentifier().equals("64"))
    		   programCounter.setValue(programCounter.getValue().longValue() + Instruction.INSTRUCTION_LENGTH);
    	   else
    		   programCounter.setValue(programCounter.getValue().intValue() + Instruction.INSTRUCTION_LENGTH);
       }
   
      /**
   	 *  Each individual register is a separate object and Observable.  This handy method
   	 *  will add the given Observer to each one.  Currently does not apply to Program
   	 *  Counter.
   	 */
       public static void addRegistersObserver(Observer observer) {
         for (int i=0; i<regFile.size(); i++) {
            regFile.get(i).addObserver(observer);
         }

      }
   	
      /**
   	 *  Each individual register is a separate object and Observable.  This handy method
   	 *  will delete the given Observer from each one.  Currently does not apply to Program
   	 *  Counter.
   	 */
       public static void deleteRegistersObserver(Observer observer) {
         for (int i=0; i<regFile.size(); i++) {
            regFile.get(i).deleteObserver(observer);
         }

      }
   }
