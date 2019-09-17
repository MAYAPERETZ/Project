package mars.util;
import java.math.BigInteger;
import mars.riscv.hardware.MemoryConfigurations;

/**
* This class contain arithmetic functions for abstract numbers (i.e, 32/64 bit long number).
* Unfortunately, Java doesn't support operands overload, so this class was needed to make this abstraction.
* Note that each function's name is identical to its relative command in RISCV.
* @author Maya Peretz
* @version September 2019
*/
public class GenMath {

	public static<T extends Number> T add(T x, T y){

	    if (x == null || y == null) 
	        return null;

	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
	        return (T) Long.valueOf(x.longValue() + y.longValue());
	     else
	        return (T) Integer.valueOf(x.intValue() + y.intValue());
	  
	 }
	
	public static<T extends Number> T sub(T x, T y){

	    if (x == null || y == null) 
	        return null;

	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
	        return (T) Long.valueOf(x.longValue() - y.longValue());
	     else
	        return (T) Integer.valueOf(x.intValue() - y.intValue());
	 }
	
	public static<T extends Number> T mul(T x, T y){

	    if (x == null || y == null) 
	        return null;

	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
	        return (T) Long.valueOf(x.longValue() * y.longValue());
	     else return (T) Integer.valueOf(x.intValue() * y.intValue());
	        
	 }
	
	public static<T extends Number> T mulh(T x, T y){

	    if (x == null || y == null) 
	        return null;

	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) {
	        return (T) Long.valueOf(new BigInteger(Long.toString(x.longValue())).multiply(new BigInteger(Long.toString(y.longValue()))).shiftRight(64).longValueExact());
	    }else
	        return (T) Integer.valueOf(new BigInteger(Long.toString(x.intValue())).multiply(new BigInteger(Long.toString(y.intValue()))).shiftRight(32).intValueExact());
	  
	 }
	
	public static<T extends Number> T mulhsu(T x, T y){

	    if (x == null || y == null) 
	        return null;

	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64)
	        return (T) Long.valueOf(new BigInteger(Long.toString(x.longValue())).multiply(
					new BigInteger(Long.toUnsignedString(y.longValue()))).shiftRight(64).longValueExact());
	     else
	        return (T) (T) Integer.valueOf(new BigInteger(Integer.toString(x.intValue())).multiply(
					new BigInteger(Integer.toUnsignedString(y.intValue()))).shiftRight(32).intValueExact());
	 }
	
	public static<T extends Number> T mulhu(T x, T y){

	    if (x == null || y == null) 
	        return null;
	   
	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
	        return (T) Long.valueOf(new BigInteger(Long.toUnsignedString(x.longValue())).multiply(
					new BigInteger(Long.toUnsignedString(y.longValue()))).shiftRight(64).longValueExact());
	     else
	        return (T) (T) Integer.valueOf(new BigInteger(Integer.toUnsignedString(x.intValue())).multiply(
					new BigInteger(Integer.toUnsignedString(y.intValue()))).shiftRight(32).intValueExact());
	    
	 }
	
	public static<T extends Number> T div(T x, T y){

	    if (x == null || y == null) {
	        return null;
	    }
	  	    	
	    
	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) {
	        return (T) Long.valueOf(x.longValue() / y.longValue());
	    } else {
	        return (T) Integer.valueOf(x.intValue() / y.intValue());
	    }
	 }
	
	
	public static<T extends Number> T divu(T x, T y){

	    if (x == null || y == null) 
	        return null;
	  	    	   
	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
	        return (T) Long.valueOf(Long.divideUnsigned(x.longValue(), y.intValue()));
	     else return (T) Integer.valueOf(Integer.divideUnsigned(x.intValue(), y.intValue()));
	    
	 }
	
	public static<T extends Number> T rem(T x, T y){

	    if (x == null || y == null) 
	        return null;    	
	    
	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
	        return (T) Long.valueOf(x.longValue() % y.longValue());
	     else return (T) Integer.valueOf(x.intValue() % y.intValue());
	    
	 }
	
	public static<T extends Number> T remu(T x, T y){

	    if (x == null || y == null) 
	        return null;  
	  	    	   
	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
	        return (T) Long.valueOf(Long.remainderUnsigned(x.longValue(), y.intValue()));
	     else
	        return (T) Integer.valueOf(Integer.remainderUnsigned(x.intValue(), y.intValue()));
	    
	 }
	
	public static<T extends Number> T and(T x, T y){

	    if (x == null || y == null) 
	        return null;   	
	    
	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
	        return (T) Long.valueOf(x.longValue() & y.longValue());
	      else return (T) Integer.valueOf(x.intValue() & y.intValue());
	   
	 }
	
	public static<T extends Number> T or(T x, T y){

	    if (x == null || y == null) 
	        return null;  	
	    
	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
	        return (T) Long.valueOf(x.longValue() | y.longValue());
	     else return (T) Integer.valueOf(x.intValue() | y.intValue());
	   
	 }
	
	public static<T extends Number> T xor(T x, T y){

	    if (x == null || y == null) 
	        return null;
	        	
	    
	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
	        return (T) Long.valueOf(x.longValue() ^ y.longValue());
	     else return (T) Integer.valueOf(x.intValue() ^ y.intValue());
	    
	 }
	
	public static<T extends Number> T sll(T x, T y){

	    if (x == null || y == null) 
	        return null;
	        	 
	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
	        return (T) Long.valueOf(x.longValue() << (y.longValue() & 0x3F));
	     else return (T) Integer.valueOf(x.intValue() << (y.intValue() & 0x1F));
	    
	 }

	// shift right logical
	public static<T extends Number> T srl(T x, T y){

	    if (x == null || y == null) 
	        return null;
	    
	    
	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
	        return (T) Long.valueOf(x.longValue() >>> (y.longValue() & 0x3F));
	     else return (T) Integer.valueOf(x.intValue() >>> (y.intValue() & 0x1F));
	    
	 }
	
	// shift right arithmetic
	public static<T extends Number> T sra(T x, T y){

	    if (x == null || y == null) 
	        return null;
	     	
	    
	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
	        return (T) Long.valueOf(x.longValue() >> (y.longValue() & 0x3F));
	     else return (T) Integer.valueOf(x.intValue() >> (y.intValue() & 0x1F));
	     
	}
	
	// less than
	public static<T extends Number> T lt(T x, T y){

	    if (x == null || y == null) 
	        return null;
	       	
	    
	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
	        return (T) ((x.longValue() < y.longValue()) ? Long.valueOf(1) : Long.valueOf(0));
	     else return (T) ((x.intValue() < y.intValue()) ? (Integer)1 : (Integer)0);
	    
	}
	
	// set less than unsigned
	public static<T extends Number> T ltu(T x, T y){

	    if (x == null || y == null) 
	        return null;
	        	
	    
	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
            return (T) ((Long.compareUnsigned(x.longValue(), y.longValue()) < 0) ? Long.valueOf(1) : Long.valueOf(0)) ;
	     else return (T) ((Integer.compareUnsigned(x.intValue(), y.intValue()) < 0) ?  (Integer)1 : (Integer)0) ;
	    
	}
	
	
	public static<T extends Number> T shiftImm(T x, T y){
		
		if (x == null || y == null)
		        return null;
		        
		if (x != null && y != null && x instanceof Integer && (y.intValue()&0x10) == 1)
		   return null;
		
		return (T) ((Number)1);
	 }
	
	// equals
	public static<T extends Number> T eq(T x, T y){

	    if (x == null || y == null) 
	        return null;
	    
	    if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
	        return  (T) ((x.longValue() == y.longValue()) ? Long.valueOf(1) : Long.valueOf(0));
	     else return  (T) ((x.intValue() == y.intValue()) ? (Integer)1 : (Integer)0);
	    
	}
		
	// equals
	public static<T extends Number> T min(T x, T y){

		if (x == null || y == null) 
			return null;
		     	

		if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
		    return  (T) (Long.valueOf(Math.min(x.longValue(), y.longValue())));
		else return  (T) (Integer.valueOf(Math.min(x.intValue(), y.intValue())));
		    
	}
		
		// equals
	public static<T extends Number> T max(T x, T y){

		if (x == null || y == null)
			return null;
			   	
		if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
			return  (T) (Long.valueOf(Math.max(x.longValue(), y.longValue())));
		else return  (T) (Integer.valueOf(Math.max(x.intValue(), y.intValue())));
			
	}
		
	// equals
	public static<T extends Number> T abs(T x){

		if (x == null ) return null;
					   	
		if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
			return  (T) (Long.valueOf(Math.abs(x.longValue())));
		else return  (T) (Integer.valueOf(Math.abs(x.intValue())));
			
	}
				
	public static<T extends Number> T neg(T x){

		if (x == null ) return null;
					   	
		if (MemoryConfigurations.getCurrentComputingArchitecture() == 64) 
			return  (T) (Long.valueOf(~x.longValue()));
		else return  (T) (Integer.valueOf(~x.intValue()));
			
	}
	
	// equals
	public static<T extends Number> T aui(T x, T y){

		if (x == null || y == null) 
			return null;   	
				    
		return (T) GenMath.add(x, GenMath.sll(
					GenMath.and(y, 0xfffff000), 12));
	}
}

