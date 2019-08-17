   package mars.mips.dump;

   import mars.Globals;
   import mars.mips.hardware.*;
   import mars.mips.hardware.memory.Memory;
   import static mars.mips.instructions.GenMath.*;

   import mars.util.Binary;
   import mars.util.Math2;

import java.io.*;

/**
 * Intel's Hex memory initialization format
 * @author Leo Alterman
 * @version July 2011
 */

    public class IntelHexDumpFormat extends AbstractDumpFormat {
   
   /**
   * Constructor.  File extention is "hex".
   */   
       public IntelHexDumpFormat() {
         super("Intel hex format", "HEX", "Written as Intel Hex Memory File", "hex");
      }
   
   /**
   *  Write MIPS memory contents according to the Memory Initialization File
   *  (MIF) specification. 
   *
   *  @param  file  File in which to store MIPS memory contents.  
   *  @param firstAddress first (lowest) memory address to dump.  In bytes but
   *  must be on word boundary.
   *  @param lastAddress last (highest) memory address to dump.  In bytes but
   *  must be on word boundary.  Will dump the word that starts at this address.
   *  @throws AddressErrorException if firstAddress is invalid or not on a word boundary.
   *  @throws IOException if error occurs during file output.
   */
       public void dumpMemoryRange(File file, Number firstAddress, Number lastAddress)
          throws AddressErrorException, IOException {
            PrintStream out = new PrintStream(new FileOutputStream(file));
            String string;
            try {
               for (Number address = firstAddress; !Math2.isLt(lastAddress, address);
                    address = add(address, Memory.WORD_LENGTH_BYTES)) {
                  Number temp = Globals.memory.getRawWordOrNull(address);
                  if (temp == null) 
                     break;
                  string = Long.toHexString(temp.intValue());
                  while (string.length() < 8) {
                     string = '0' + string;
                  }
                  String addr = Binary.currentNumToHexString(sub(address,firstAddress));
                  while (addr.length() < 4) {
                     addr = '0' + addr;
                  }
                  String chksum;
                  Number tmp_chksum = 0;
                  tmp_chksum = add(tmp_chksum,4);
                  tmp_chksum = add(tmp_chksum, (and(0xFF, sub(address, firstAddress))));
                  tmp_chksum = add(tmp_chksum, (and(0xFF, (sra(sub(address,firstAddress),8)))));
                  tmp_chksum = and(0xFF, temp);
                  tmp_chksum = add(tmp_chksum, (and(0xFF, (sra(sub(address,firstAddress),8)))));
                  tmp_chksum = add(tmp_chksum, (and(0xFF, (sra(sub(address,firstAddress),16)))));
                  tmp_chksum = add(tmp_chksum, (and(0xFF, (sra(sub(address,firstAddress),24)))));
                  tmp_chksum = rem(tmp_chksum, 256);
                  tmp_chksum = add(neg(tmp_chksum), 1);
                  chksum = Binary.currentNumToHexString(and(0xFF, tmp_chksum));
                  if(chksum.length()==1) chksum = '0' + chksum;
                  String finalstr = ":04"+addr+"00"+string+chksum;
                  out.println(finalstr.toUpperCase());
               }
               out.println(":00000001FF");
            } 
            finally { 
               out.close(); 
            }
            
      }
   }
