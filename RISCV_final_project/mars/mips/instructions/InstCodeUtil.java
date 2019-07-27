package mars.mips.instructions;

import mars.util.Binary;

public class InstCodeUtil {
	
	protected static final int mask1 = 0x0000007f;
	protected static final int mask2 = 0x00000f80;
	protected static final int mask3 = 0x00007000;
	protected static final int mask4 = 0x000f8000;
	protected static final int mask5 = 0x01f00000;
	protected static final int mask6 = 0xfe000000; 
	
	
	protected static int computeRd(int args) {
		return (mask2 & (args<<7));
	}
	
	protected static int getRd(int args) {
		return ((mask2 & args)>> 7);
	}
		
	protected static int computeRs1(int args) {
		return  (mask4 & (args << 15));
	}
	
	protected static int getRs1(int args) {
		return ((mask4 & args) >> 15);
	}
	
	protected static int computeRs2(int args) {
		return  (mask5 & (args << 20));
	}
	
	protected static int getRs2(int args) {
		return ((mask5 & args) >> 20);
	}
	
	protected static int getFunct3(BasicInstruction basicInst){
		if(basicInst instanceof R_type.WithRmFeild) {
			return mask3 & (R_type.WithRmFeild.rmMode<<12);
		}
		return mask3 & (Binary.binaryStringToInt(basicInst.getOperationMask().substring(17, 20))<<12);
	}
	
}
