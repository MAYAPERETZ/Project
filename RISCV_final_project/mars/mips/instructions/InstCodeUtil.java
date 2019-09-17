package mars.mips.instructions;

import mars.util.Binary;

public class InstCodeUtil {
	
	static final int mask1 = 0x0000007f;
	private static final int mask2 = 0x00000f80;
	static final int mask3 = 0x00007000;
	static final int mask4 = 0x000f8000;
	static final int mask5 = 0x01f00000;
	static final int mask6 = 0xfe000000;
	
	
	static int computeRd(int args) {
		return (mask2 & (args<<7));
	}
	
	static int getRd(int args) {
		return ((mask2 & args)>> 7);
	}
		
	static int computeRs1(int args) {
		return  (mask4 & (args << 15));
	}
	
	static int getRs1(int args) {
		return ((mask4 & args) >> 15);
	}
	
	static int computeRs2(BasicInstruction basicInstruction, Number number) {
		int s;
		try {
			s = Binary.binaryStringToInt(basicInstruction.getOperationMask().substring(7, 12));
		}catch (NumberFormatException nfe)
		{
			return (mask5 & (number.intValue()<<20));
		}
		return(mask5 & (s << 20));
	}

	static int computeRs2(Number args) {
		return(mask5 & (args.intValue()<<20));
	}

	static int getRs2(int args) {
		return ((mask5 & args) >> 20);
	}
	
	static int getFunct3(BasicInstruction basicInst){
		String res = basicInst.getOperationMask().substring(17, 20);
		if (res.equals("xxx"))
			return mask3 & (R_type.WithRmField.rmMode<<12);
		return mask3 & (Binary.binaryStringToInt(res)<<12);
	}
	
}
