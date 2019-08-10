package mars.mips.instructions;
import java.util.function.BiFunction;


public class R_M  extends R_type.RVI{

	public R_M(String example, String description, String funct3,
			SimulationCode simCode) {
		super(example, description, "0000001tttttsssss"+ funct3+"fffff0110011", simCode);
	}

	public R_M(String example, String description, String funct3	,
			BiFunction<Number, Number, Number> x) {
		super(example, description, "0000001tttttsssss"+ funct3+"fffff0110011", x);
	}
	
}
