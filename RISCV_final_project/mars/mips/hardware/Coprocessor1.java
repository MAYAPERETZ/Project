package mars.mips.hardware;
import mars.util.*;
import mars.Globals;
import mars.ProgramStatement;
import mars.simulator.Exceptions;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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
  *  Represents Coprocessor 1, the Floating Point Unit (FPU)
  *   @author 	Pete Sanderson
  *   @version July 2005
  **/

// Adapted from RegisterFile class developed by Bumgarner et al in 2003.
// The FPU registers will be implemented by Register objects.  Such objects
// can only hold int values, but we can use Float.floatToIntBits() to translate
// a 32 bit float value into its equivalent 32-bit int representation, and
// Float.intBitsToFloat() to bring it back.  More importantly, there are 
// similar methods Double.doubleToLongBits() and Double.LongBitsToDouble()
// which can be used to extend a double value over 2 registers.  The resulting
// long is split into 2 int values (high order 32 bits, low order 32 bits) for
// storing into registers, and reassembled upon retrieval.

   
   
    public  class Coprocessor1 {
    	
    	// need to complete
    	protected static final float POSITIZE_0 = 0;
    	protected static final float NEGATIVE_0 = 0;
    	protected static final float qNan = 0;
    	protected static final float sNan = 0;
    	protected static final float NEGATIVE_NORMAL = 0;
    	protected static final float NEGATIVE_SUBNORMAL = 0;
    	protected static final float POSITIZE_NORMAL = 0;
    	protected static final float POSITIZE_SUBNORMAL = 0;
    	protected static final float NEGATIVE_INF = 0;
    	protected static final float POSITIZE_INF = 0;
    	
    	private static final int SIGN_MASK_32 = 0x80000000;
    	private static final int MANTISA_SIGN_MASK_32 = 0x00400000;
    	private static final int SIGN_MASK_64 = 0x80000000;
    	private static final int MANTISA_SIGN_MASK_64 = 0x00400000;
    	
    	private static final int RNE = 0;
    	private static final int RTZ = 1;
    	private static final int RDN = 2;
    	private static final int RUP = 3;
    	private static final int RMM = 4;
    	private static final int DYNAMIC = 7;
    	
    	// need to complete
    	
    	
    	static enum AccruedExceptionFlagEncoding{
    		NV,
    		DZ,
    		OF,
    		UF,
    		NZ
    	}

    	
    	 enum fclass{
        	NEGATIVE_INF,
        	NEGATIVE_NORMAL,
        	NEGATIVE_SUBNORMAL,
        	NEGATIVE_0,
        	POSITIZE_0,
        	POSITIZE_SUBNORMAL,
        	POSITIZE_NORMAL,
        	POSITIZE_INF,
        	sNan,
        	qNan
    	}
    	
      private static ArrayList<Register> registers = new ArrayList<Register>(
      		Arrays.asList(
           new Register("ft0", 0, 0),  new Register("ft1", 1, 0),
         	new Register("ft2", 2, 0),  new Register("ft3", 3, 0),
         	new Register("ft4", 4, 0),  new Register("ft5", 5, 0),
         	new Register("ft6", 6, 0),  new Register("ft7", 7, 0),
         	new Register("fs0", 8, 0),  new Register("fs1", 9, 0),
         	new Register("fa0", 10, 0),new Register("fa1", 11, 0), 
         	new Register("fa2", 12, 0),new Register("fa3", 13, 0),
         	new Register("fa4", 14, 0),new Register("fa5", 15, 0),
         	new Register("fa6", 16, 0),new Register("fa7", 17, 0),
         	new Register("fs2", 18, 0),new Register("fs3", 19, 0),
         	new Register("fs4", 20, 0),new Register("fs5", 21, 0),
         	new Register("fs6", 22, 0),new Register("fs7", 23, 0),
         	new Register("fs8", 24, 0),new Register("fs9", 25, 0),
         	new Register("fs10", 26, 0),new Register("fs11", 27, 0),
         	new Register("ft8", 28, 0),new Register("ft9", 29, 0),
         	new Register("ft10", 30, 0),new Register("ft11", 31, 0)
           ));
   	// The 8 condition flags will be stored in bits 0-7 for flags 0-7.
      private static Register fcsr = new Register("fcsr",32, 0);	 
    //  private static int numConditionFlags = 8;
   	
   	/**
   	  *  Method for displaying the register values for debugging.
   	  **/	 
   		 
    /*   public static void showRegisters(){
         for (int i=0; i< registers.size(); i++){
          
            System.out.println("Name: " + registers.get(i).getName());
            System.out.println("Number: " + registers.get(i).getNumber());
            System.out.println("Value: " + registers.get(i).getValue());	
            System.out.println(""); 
         }
      }
      */
   	/**
   	  *  Sets the value of the FPU register given to the value given.
   	  *   @param reg Register to set the value of.
   	  *   @param val The desired float value for the register.
   	  **/
   	
    //   public static void setRegisterToFloat(String reg, float val){
    //     setRegister(getRegisterNumber(reg), val);
     // }
   
   
   	/**
   	  *  Sets the value of the FPU register given to the value given.
   	  *   @param reg Register to set the value of.
   	  *   @param val The desired float value for the register.
   	  **/
      
       
   	  
       public static void setRegister(int reg, float val){
         if(reg >= 0 && reg < registers.size()) {
        	 long longVal =  0;
        	 longVal |= Float.floatToRawIntBits(val);
            registers.get(reg).setValue(longVal);
         }
      }
   
   	/**
   	  *  Sets the value of the FPU register given to the 32-bit
   	  *  pattern given by the int parameter.
   	  *   @param reg Register to set the value of.
   	  *   @param val The desired int bit pattern for the register.
   	  **/
   	
     //  public static void setRegisterToInt(String reg, int val){
     //    setRegisterToInt(getRegisterNumber(reg), val);
     // }
   
   
   	/**
   	  *  Sets the value of the FPU register given to the 32-bit
   	  *  pattern given by the int parameter.
   	  *   @param reg Register to set the value of.
   	  *   @param val The desired int bit pattern for the register.
   	  **/
   	  
      // public static void setRegisterToInt(int reg, long val){
      //   if(reg >= 0 && reg < registers.size()) {
      //      registers.get(reg).setValue(val);
      //   }
     // }
   
   
   	/**
   	  *  Sets the value of the FPU register given to the double value given.  The register
   	  *  must be even-numbered, and the low order 32 bits are placed in it.  The high order
   	  *  32 bits are placed in the (odd numbered) register that follows it.
   	  *   @param reg Register to set the value of.
   	  *   @param val The desired double value for the register.
   	  *   @throws InvalidRegisterAccessException if register ID is invalid or odd-numbered.
   	  **/
   	
       public static void setRegister(int reg, double val) 
                                    {
         //if (reg % 2 != 0) {
        //    throw new InvalidRegisterAccessException();
        // } 
         long bits = Double.doubleToRawLongBits(val); 
         registers.get(reg).setValue(bits);  // high order 32 bits
      }
   
   
   	/**
   	  *  Sets the value of the FPU register given to the double value given.  The register
   	  *  must be even-numbered, and the low order 32 bits are placed in it.  The high order
   	  *  32 bits are placed in the (odd numbered) register that follows it.
   	  *   @param reg Register to set the value of.
   	  *   @param val The desired double value for the register.
   	  *   @throws InvalidRegisterAccessException if register ID is invalid or odd-numbered.
   	  **/   
      // public static void setRegisterPairToDouble(String reg, double val) 
      //										throws InvalidRegisterAccessException {
       //  setRegisterToDouble(getRegisterNumber(reg), val);
     // }
   
   	
   	/**
   	  *  Sets the value of the FPU register pair given to the long value containing 64 bit pattern
   	  *  given.  The register
   	  *  must be even-numbered, and the low order 32 bits from the long are placed in it.  The high order
   	  *  32 bits from the long are placed in the (odd numbered) register that follows it.
   	  *   @param reg Register to set the value of.  Must be even register of even/odd pair.
   	  *   @param val The desired double value for the register.
   	  *   @throws InvalidRegisterAccessException if register ID is invalid or odd-numbered.
   	  **/
   	
       public static void setRegister(int reg, long val) 
                                    {
        // if (reg % 2 != 0) {
        //    throw new InvalidRegisterAccessException();
         //} 
       // registers.get(reg+1).setValue(Binary.highOrderLongToInt(val));  // high order 32 bits
        registers.get(reg).setValue(val); // low order 32 bits
      }
   
   
   	/**
   	  *  Sets the value of the FPU register pair given to the long value containing 64 bit pattern
   	  *  given.  The register
   	  *  must be even-numbered, and the low order 32 bits from the long are placed in it.  The high order
   	  *  32 bits from the long are placed in the (odd numbered) register that follows it.
   	  *   @param reg Register to set the value of.  Must be even register of even/odd pair.
   	  *   @param val The desired long value containing the 64 bits for the register pair.
   	  *   @throws InvalidRegisterAccessException if register ID is invalid or odd-numbered.
   	  **/   
      // public static void setRegisterPairToLong(String reg, long val) 
    //   										throws InvalidRegisterAccessException {
     //    setRegisterPairToLong(getRegisterNumber(reg), val);
     // }
   
   
   	
   	/**
   	  *  Gets the float value stored in the given FPU register.
   	  *   @param reg Register to get the value of.
   	  *   @return The  float value stored by that register.
   	  **/
   	
       public static float getFloatValue(int reg){
         float result = 0F;
         if(reg >= 0 && reg < registers.size()) {
            result = Float.intBitsToFloat((int) registers.get(reg).getValue());
         }
         return result;
      }
   	
   	
   	/**
   	  *  Gets the float value stored in the given FPU register.
   	  *   @param reg Register to get the value of.
   	  *   @return The  float value stored by that register.
   	  **/
   	  		
       public static float getFloatValue(String reg) {
         return getFloatValue(getRegisterNumber(reg));
      }
   
   
   	/**
   	  *  Gets the 32-bit int bit pattern stored in the given FPU register.
   	  *   @param reg Register to get the value of.
   	  *   @return The int bit pattern stored by that register.
   	  **/
   	
    /*   private static long getIntFromRegister(int reg){
         long result = 0;
         if(reg >= 0 && reg < registers.size()) {
            result =  registers.get(reg).getValue();
         }
         return result;
      }
   	*/
   	
   	/**
   	  *  Gets the 32-bit int bit pattern stored in the given FPU register.
   	  *   @param reg Register to get the value of.
   	  *   @return The int bit pattern stored by that register.
   	  **/
   	  		
    //    public static long getIntFromRegister(String reg) {
    //     return getIntFromRegister(getRegisterNumber(reg));
    //  }
   	
   
   	/**
   	  *  Gets the double value stored in the given FPU register.  The register
   	  *  must be even-numbered.
   	  *   @param reg Register to get the value of. Must be even number of even/odd pair.
   	  *   @throws InvalidRegisterAccessException if register ID is invalid or odd-numbered.
   	  **/
   	
       public static double getDoubleValue(int reg) 
                                    throws InvalidRegisterAccessException {
        // if (reg % 2 != 0) {
        //    throw new InvalidRegisterAccessException();
        // } 
       //  long bits = Binary.twoIntsToLong((int)registers.get(reg+1).getValue(),(int)registers.get(reg).getValue());
         return Double.longBitsToDouble(registers.get(reg).getValue().longValue());
      }
   
   
   	/**
   	  *  Gets the double value stored in the given FPU register.  The register
   	  *  must be even-numbered.
   	  *   @param reg Register to get the value of. Must be even number of even/odd pair.
   	  *   @throws InvalidRegisterAccessException if register ID is invalid or odd-numbered.
   	  **/
   	   
       public static double getDoubleValue(String reg)
                                    throws InvalidRegisterAccessException {
         return getDoubleValue(getRegisterNumber(reg));
      }   
      
   
   	/**
   	  *  Gets a long representing the double value stored in the given double
   	  *  precision FPU register. 
   	  *  The register must be even-numbered.
   	  *   @param reg Register to get the value of. Must be even number of even/odd pair.
   	  *   @throws InvalidRegisterAccessException if register ID is invalid or odd-numbered.
   	  **/
   	
       public static long getLongValue(long reg) 
                                     {
       //  if (reg % 2 != 0) {
       //     throw new InvalidRegisterAccessException();
       //  } 
         return registers.get((int)reg).getValue().longValue();
      }
   
   
   	/**
   	  *  Gets the double value stored in the given FPU register.  The register
   	  *  must be even-numbered.
   	  *   @param reg Register to get the value of. Must be even number of even/odd pair.
   	  *   @throws InvalidRegisterAccessException if register ID is invalid or odd-numbered.
   	  **/
   	   
       public static long getLongValue(String reg)
                                    {
         return getLongValue(getRegisterNumber(reg));
      }  
   
   
   	/**
   	  *  This method updates the FPU register value who's number is num.  Note the
   	  *  registers themselves hold an int value.  There are helper methods available
   	  *  to which you can give a float or double to store.
   	  *   @param num FPU register to set the value of.
   	  *   @param val The desired int value for the register.
   	  **/
   	  
       public static Number updateRegister(long num, long val) {
         Number old = 0;
         int num2 = (int)num;
        
         old = (Globals.getSettings().getBackSteppingEnabled())
                        ? Globals.program.getBackStepper().addCoprocessor1Restore(num2 , registers.get(num2).setValue(val).longValue())
                  		: registers.get(num2).setValue(val).longValue();

         return old;
      }
 
       public static long setFCSR(long value){
           long old = fcsr.getValue().longValue();
           fcsr.setValue(value);
           if (Globals.getSettings().getBackSteppingEnabled()) {
              Globals.program.getBackStepper().addFCSRRestore(old);
           } 
           return old;
        }
      
       public static Number updateRegisterWithExecptions(long num,  Number val, ProgramStatement statement) throws FloatingPointException {
           Number old = 0;
           int num2 = (int)num;
           float floatVal = Float.intBitsToFloat(val.intValue());        
           
           if(Coprocessor1.isUnderflow(floatVal, Coprocessor1.getFclass(floatVal))) {
          	 throw new FloatingPointException(statement,"Floating-point Arithmetic Underflow :", 
               		Exceptions.FLOATING_POINT_UNDERFLOW);
           }
           else if (Float.isInfinite(floatVal)) {
          	 throw new FloatingPointException(statement,"Floating-point Arithmetic Overflow :", 
          		Exceptions.FLOATING_POINT_OVERFLOW);
           }
           else if (Float.isNaN(floatVal)) {
        	   throw new FloatingPointException(statement,"Floating-point Invalid Operation :", 
                 		Exceptions.FLOATING_POINT_INVALID_OP);
           }
           // need to implemtent the inexact exception, don't know how...
           
           old = (Globals.getSettings().getBackSteppingEnabled())
                          ? Globals.program.getBackStepper().addCoprocessor1Restore(num2 , registers.get(num2).setValue(val).longValue())
                    		: registers.get(num2).setValue(val).longValue();

           return old;
        }
      /**
   	  *  Returns the value of the FPU register who's number is num.  Returns the
   	  *  raw int value actually stored there.  If you need a float, use
   	  *  Float.intBitsToFloat() to get the equivent float.
   	  *   @param num The FPU register number.
   	  *   @return The int value of the given register.
   	  **/
   	
       public static int getIntValue(long num){
         return (int) registers.get((int)num).getValue();
      }
      		
      	/**
   		  *  For getting the number representation of the FPU register.
   		  *   @param n The string formatted register name to look for.
   		  *   @return The number of the register represented by the string.
   		  **/	
      		
       public static int getRegisterNumber(String n){
         int j=-1;
         for (int i=0; i< registers.size(); i++){
            if(registers.get(i).getName().equals(n)) {
               j= registers.get(i).getNumber();
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
         return registers;
      }
      
   	/**
   	  *  Get register object corresponding to given name.  If no match, return null.
   	  *   @param rName The FPU register name, must be "$f0" through "$f31".
   	  *   @return The register object,or null if not found.
   	  **/
  
   
       public static Register getRegister(String rName) {
           Register reg = null;
           if (rName.charAt(0) == 'f' && Character.isDigit(rName.charAt(0))) {
          	
          	 try {
                     // check for register number 0-31.
                     reg = registers.get(Binary.stringToInt(rName.substring(1)));
              }
              catch (Exception e) {
                     // handles both NumberFormat and ArrayIndexOutOfBounds
                     // check for register mnemonic zero through ra
              	reg = null;
              }
          	
           }
           else
           {
          	 reg = null; // just to be sure
                     // just do linear search; there aren't that many registers
                    for (int i=0; i < registers.size(); i++) {
                       if (rName.equals(registers.get(i).getName())) {
                          reg = registers.get(i);
                          break;
                       }
                    }
           }
           return reg;
        }
   	
   	/**
   	  *  Method to reinitialize the values of the registers.
   	  **/
   	
       public static void resetRegisters(){
         for(int i=0; i < registers.size(); i++)
            registers.get(i).resetValue();
       //  clearConditionFlags();
         fcsr.resetValue();
      }
      
   
      /**
   	 *  Each individual register is a separate object and Observable.  This handy method
   	 *  will add the given Observer to each one.  
   	 */
       public static void addRegistersObserver(Observer observer) {
         for (int i=0; i<registers.size(); i++) {
            registers.get(i).addObserver(observer);
         }
         fcsr.addObserver(observer);
      }

   
      /**
   	 *  Each individual register is a separate object and Observable.  This handy method
   	 *  will delete the given Observer from each one.  
   	 */
       public static void deleteRegistersObserver(Observer observer) {
         for (int i=0; i<registers.size(); i++) 
            registers.get(i).deleteObserver(observer);
     
         fcsr.deleteObserver(observer);

      }
      
      public static int getFclass(Float value) {
    	 
    	  if(value == Float.NEGATIVE_INFINITY)
    		  return fclass.NEGATIVE_INF.ordinal();
    	  else if(value == Float.POSITIVE_INFINITY)
    		  return fclass.POSITIZE_INF.ordinal();
    	  else if(value == 0)
    		  return fclass.POSITIZE_0.ordinal();
    	  else if((Float.floatToIntBits(value)&(~SIGN_MASK_32)) == 0)
    		  return fclass.NEGATIVE_0.ordinal();
    	  else if(0 < value &&
    			  value < Float.MIN_NORMAL) 
    		  return fclass.POSITIZE_SUBNORMAL.ordinal();
    	  else if(value < 0 &&
    			  value > (-Float.MIN_NORMAL)) 
    		  return fclass.NEGATIVE_SUBNORMAL.ordinal();
    	  else if(Float.isNaN(value)) {
    		  return ((Float.floatToIntBits(value)&MANTISA_SIGN_MASK_32) == 1) ?
   				fclass.qNan.ordinal(): fclass.sNan.ordinal();
    	  }
    	  
    	  return ((Float.floatToIntBits(value)&SIGN_MASK_32) == 0) ?
    			  fclass.POSITIZE_NORMAL.ordinal() : fclass.NEGATIVE_NORMAL.ordinal();
    	  
      }
      
      public static int getFclass(Double value) {
     	 
    	  if(value == Double.NEGATIVE_INFINITY)
    		  return fclass.NEGATIVE_INF.ordinal();
    	  else if(value == Double.POSITIVE_INFINITY)
    		  return fclass.POSITIZE_INF.ordinal();
    	  else if(value == 0)
    		  return fclass.POSITIZE_0.ordinal();
    	  else if((Double.doubleToLongBits(value)&(~SIGN_MASK_64)) == 0)
    		  return fclass.NEGATIVE_0.ordinal();
    	  else if(0 < value &&
    			  value < Double.MIN_NORMAL) 
    		  return fclass.POSITIZE_SUBNORMAL.ordinal();
    	  else if(value < 0 &&
    			  value > (-Double.MIN_NORMAL)) 
    		  return fclass.NEGATIVE_SUBNORMAL.ordinal();
    	  else if(Double.isNaN(value)) {
    		  return ((Double.doubleToLongBits(value)&MANTISA_SIGN_MASK_64) == 1) ?
   				fclass.qNan.ordinal(): fclass.sNan.ordinal();
    	  }
    	  
    	  return ((Double.doubleToLongBits(value)&SIGN_MASK_64) == 0) ?
    			  fclass.POSITIZE_NORMAL.ordinal() : fclass.NEGATIVE_NORMAL.ordinal();
    	  
      }
      
      
   	
   	/**
   	 *  Set condition flag to 1 (true).
   	 *
   	 *  @param flag condition flag number (0-7)
   	 *  @return previous flag setting (0 or 1)
   	 */
    /*   public static int setConditionFlag(int flag) {
         int old = 0;
         if (flag >= 0 && flag < numConditionFlags) {
            old = getConditionFlag(flag);
            condition.setValue(Binary.setBit((int) condition.getValue(),flag));
            if (Globals.getSettings().getBackSteppingEnabled())
               if (old==0) {
                  Globals.program.getBackStepper().addConditionFlagClear(flag);
               } 
               else {
                  Globals.program.getBackStepper().addConditionFlagSet(flag);
               }
         }
         return old;
      }
     
     (/
   
   	/**
   	 *  Set condition flag to 0 (false).
   	 *
   	 *  @param flag condition flag number (0-7)
   	 *  @return previous flag setting (0 or 1)
   	 */
      /*
       public static int clearConditionFlag(int flag) {
         int old = 0;
         if (flag >= 0 && flag < numConditionFlags) {
            old = getConditionFlag(flag);
            condition.setValue(Binary.clearBit((int) condition.getValue(),flag));
            if (Globals.getSettings().getBackSteppingEnabled())
               if (old==0) {
                  Globals.program.getBackStepper().addConditionFlagClear(flag);
               } 
               else {
                  Globals.program.getBackStepper().addConditionFlagSet(flag);
               }
         }
         return old;
      }
   
   	*/
   	
   	/**
   	 *  Get value of specified condition flag (0-7).
   	 *
   	 *  @param flag condition flag number (0-7)
   	 *  @return 0 if condition is false, 1 if condition is true
   	 */
      
   /*   public static int getConditionFlag(int flag) {
         if (flag < 0 || flag >= numConditionFlags) 
            flag = 0;
         return Binary.bitValue(condition.getValue(), flag);
      }		
   	
   	*/
   	/**
   	 *  Get array of condition flags (0-7).
   	 *
   	 *  @return array of int condition flags
   	 */
     /*  public static int getConditionFlags() {
         return (int) condition.getValue();
      }	
   	*/
   	
   	/**
   	 *  Clear all condition flags (0-7).
   	 *
   	 */
     /*  public static void clearConditionFlags() {
         condition.setValue(0);  // sets all 32 bits to 0.
      }		
   	*/
      /**
   	 *  Set all condition flags (0-7).
     * @throws FloatingPointException 
   	 *
   	 */
     /*  public static void setConditionFlags() {
         condition.setValue(-1);  // sets all 32 bits to 1.
      }	
   	
      /**
   	 *  Get count of condition flags.
   	 *
   	 *  @return number of condition flags
   	 */
     /*  public static int getConditionFlagCount() {
         return numConditionFlags;        
      }	
     */  
      
      public synchronized static Register getFCSR() {
    	  return fcsr;
      }
      
      public synchronized static void setFCSRValue(int exceptionType) {
    	  setFCSR(((int)fcsr.getValue())|exceptionType);
      }
      
      
      private static int getRoudingMode() throws FloatingPointException {
  //  	  if((int)(fcsr.getValue()&0x000000e0) > 4 || (int)(fcsr.getValue()&0x000000e0) < 0)
 //   		  throw new FloatingPointException();
    	  return (fcsr.getValue().intValue()&0x000000e0);
      }
      
      public static boolean isUnderflow(float value, int classification) {
    	  return (Float.isFinite(value) && classification != fclass.NEGATIVE_NORMAL.ordinal() 
    			  && classification != fclass.POSITIZE_NORMAL.ordinal() && !Float.isNaN(value)
    			  && value != 0);
      }
      
      
      public static int round(float value) throws FloatingPointException {

    	  switch (mars.mips.instructions.R_type.WithRmFeild.rmMode) {
    	  case RNE:
    		  Float.floatToRawIntBits(value);
    	  case RTZ: 
    		  return truncate(value);
    	  case RDN:
    		  return floor(value);
    	  case RUP:
    		  return ceil(value);
    	  case RMM:
    		  return roundToNearestMM(value);
    	  case DYNAMIC:
    		  return getRoudingMode();
    		  
    	  }
		  return Float.floatToRawIntBits(value);
      }
      
      
      
      private static int floor(float floatValue) {
        //  float floatValue = Float.intBitsToFloat(Coprocessor1.getIntValue(regNum));	
    	 /* 
          int floor = (int) Math.floor(floatValue);

          if ( Float.isInfinite(floatValue)
               || floatValue < (float) Integer.MIN_VALUE 
          	  || floatValue > (float) Integer.MAX_VALUE ) {							
             floor = Integer.MAX_VALUE;
          }
          return floor;
         // updateRegister(regNum, floor);
          * 
          * 
          */
    	  
    	  BigDecimal round = new BigDecimal(Float.floatToRawIntBits(floatValue));
    	  round.setScale(round.scale(), RoundingMode.FLOOR);
    	  return round.intValueExact();
      }
      
      
      private static int ceil(float value) {
        /*  float floatValue = Float.intBitsToFloat(Coprocessor1.getIntValue(regNum));	
          int ceiling = (int) Math.ceil(floatValue);

          if (  Float.isInfinite(floatValue)
               || floatValue < (float) Integer.MIN_VALUE 
          	  || floatValue > (float) Integer.MAX_VALUE ) {							
             ceiling = Integer.MAX_VALUE;
          }
          updateRegister(regNum, ceiling);
      	*/
    	  
    	  BigDecimal round = new BigDecimal(Float.floatToRawIntBits(value));
    	  round.setScale(round.scale(), RoundingMode.CEILING);
    	  return round.intValueExact();
      }
      
      private static int roundToNearestMM(float value) {
    	  BigDecimal round = new BigDecimal(Float.floatToRawIntBits(value));
    	  round.setScale(round.scale(), RoundingMode.HALF_UP);
    	  return round.intValueExact();
      }
      
      private static int truncate(float floatValue) {
          
    	  /*
    	  int truncate = (int) floatValue;// Typecasting will round toward zero, the correct action
       
          if ( Float.isNaN(floatValue) 
               || Float.isInfinite(floatValue)
               || floatValue < (float) Integer.MIN_VALUE 
          	  || floatValue > (float) Integer.MAX_VALUE ) {							
             truncate = Integer.MAX_VALUE;
          }
          updateRegister(regNum, truncate);
          
          */

    	  BigDecimal round = new BigDecimal(Float.floatToRawIntBits(floatValue));
    	  round.setScale(round.scale(), RoundingMode.DOWN);
    	  return round.intValueExact();
    	  
      }
   }
