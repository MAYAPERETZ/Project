package mars.util;

import java.util.List;

/**
 * Some util class to make easier operations on an {@code Number} object.
 * @author Maya Peretz
 * @version September 2019
 */
public class Math2 {

	public static boolean isLtz(Number x) {
		return GenMath.lt(x, 0).intValue() == 1;
	}
	
	public static boolean isLt(Number x, Number y) {
		return GenMath.lt(x, y).intValue() == 1;
	}
	
	public static boolean isEqz(Number x) {
		return GenMath.eq(x, 0).intValue() == 1;
	}
	
	public static boolean isEq(Number x, Number y) {
		return GenMath.eq(x, y).intValue() == 1;
	}
	
	public static Number andThen( Function<Number, Number, 
			Number> x, Function<Number, Number, Number> y, Number a, Number b, Number c) {
		return x.andThen(y, c).apply(a, b);
	}
	
	public static Number chainedAndThen(List<Number> a,Function<Number, Number, 
			Number> x) {
		Number temp = 1;
		if(a.size() <= 1)
			return temp;
		
		Number y = a.remove(0);
		Number z = a.remove(0);
		a.add(0, GenMath.mul(y, z));
		return chainedAndThen(a, x);
	}
	
	public static Number compose( Function<Number, Number, 
			Number> x, Function<Number, Number, Number> y, Number a, Number b, Number c) {
		return x.compose(y, c).apply(a, b);
	}
	
	@FunctionalInterface
	public interface Function<A, B, C> {
	     C apply(A one, B b);
	     default <D extends Number> Function<A, B, D> andThen(Function<? super A, ? super C, ? extends D> after
	    		 , C c){	      
		    	return (A d, B b) -> after.apply((A) apply(d, b ), c);
		     }
	     default <D extends Number> Function<D, B, C> compose(Function<? super D, ? super B, ? extends B> before, C c){	      
	    	return (D d, B b) -> apply((A) c, before.apply(d, b ));
	     }
	}

}
