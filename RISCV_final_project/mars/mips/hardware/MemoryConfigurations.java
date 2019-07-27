   package mars.mips.hardware;
   import mars.Globals;

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
 * Models the collection of MIPS memory configurations.
 * The default configuration is based on SPIM.  Starting with MARS 3.7,
 * the configuration can be changed.  
 * 
 * @author Pete Sanderson 
 * @version August 2009
 */


    public class MemoryConfigurations {
    
      private static ArrayList configurations = null;
      private static MemoryConfiguration defaultConfiguration;
      private static MemoryConfiguration currentConfiguration;
      
   	// Be careful, these arrays are parallel and position-sensitive.
   	// The getters in this and in MemoryConfiguration depend on this
   	// sequence.  Should be refactored...  The order comes from the
   	// original listed order in Memory.java, where most of these were
   	// "final" until Mars 3.7 and changeable memory configurations.
      private static final String[] configurationItemNames = {
         ".text base address",
         "data segment base address",
         ".extern base address",
         "global pointer gp",
         ".data base address",
         "heap base address",
         "stack pointer sp",
         "stack base address",
         "user space high address",
         "kernel space base address",
         ".ktext base address",
         "exception handler address",
         ".kdata base address",
         "MMIO base address",
         "kernel space high address",
         "data segment limit address",
         "text limit address",
         "kernel data segment limit address",
         "kernel text limit address",
         "stack limit address",
         "memory map limit address"
         };
   	
      // Default configuration comes from SPIM
      private static Number[] configuration32 = { 
         0x00400000, // .text Base Address
         0x10000000, // Data Segment base address
         0x10000000, // .extern Base Address
         0x10008000, // Global Pointer gp)
         0x10010000, // .data base Address
         0x10040000, // heap base address
         0x7fffeffc, // stack pointer sp (from SPIM not MIPS)
         0x7ffffffc, // stack base address
         0x7fffffff, // highest address in user space
         0x80000000, // lowest address in kernel space
         0x80000000, // .ktext base address
         0x80000180, // exception handler address
         0x90000000, // .kdata base address
         0xffff0000, // MMIO base address
         0xffffffff, // highest address in kernel (and memory)
         0x7fffffff, // data segment limit address
         0x0ffffffc, // text limit address
         0xfffeffff, // kernel data segment limit address
         0x8ffffffc, // kernel text limit address
         0x10040000, // stack limit address
         0xffffffff  // memory map limit address
         };
      
     /* private static long[] defaultConfiguration64 = { 
    	 0x00000000000400000L, // .text Base Address
    	 0x00000000010000000L, // Data Segment base address
    	 0x00000000010000000L, // .extern Base Address
    	 0x00000000010008000L, // Global Pointer $gp)
    	 0x00000000010010000L, // .data base Address
    	 0x00000000010040000L, // heap base address
    	 0x0000000007fffeffcL, // stack pointer $sp (from SPIM not MIPS)
    	 0x0000000007ffffffcL, // stack base address
    	 0x0000000007fffffffL, // highest address in user space
    	 0x00000000080000000L, // lowest address in kernel space
    	 0x00000000080000000L, // .ktext base address
    	 0x00000000080000180L, // exception handler address
    	 0x00000000090000000L, // .kdata base address
    	 0x000000000ffff0000L, // MMIO base address
    	 0x000000000ffffffffL, // highest address in kernel (and memory)
    	 0x0000000007fffffffL, // data segment limit address
    	 0x0000000000ffffffcL, // text limit address
    	 0x000000000fffeffffL, // kernel data segment limit address
    	 0x0000000008ffffffcL, // kernel text limit address
    	 0x00000000010040000L, // stack limit address
    	 0x000000000ffffffffL  // memory map limit address
    	         };
    	*/      
      
      // Default configuration comes from SPIM
      private static Number[] configuration64 = { 
         0x0000000000400000L, // .text Base Address
         0x0000000010000000L, // Data Segment base address
         0x0000000010000000L, // .extern Base Address
         0x0000000010000000L, // Global Pointer $gp)
         0x0000000010010000L, // .data base Address
         0x0000000010040000L, // heap base address
         0x0000003fffffeff0L, // stack pointer $sp (from SPIM not MIPS)
         0x0000003ffffffff0L, // stack base address
         0x0000003fffffffffL, // highest address in user space
         0x0000040000000000L, // lowest address in kernel space
         0x0000040000000000L, // .ktext base address
         0x0000040000000180L, // exception handler address
         0x0000050000000000L, // .kdata base address
         0x00000ffff0000000L, // MMIO base address
         0xffffffffffffffffL, // highest address in kernel (and memory)
         0x0000003fffffffffL, // data segment limit address
         0x000000000fffffffL, // text limit address
         0xffffffefffffffffL, // kernel data segment limit address
         0x8ffffffffffffffcL, // kernel text limit address
         0x0000000010040000L, // stack limit address
         0xffffffffffffffffL  // memory map limit address
         };
   
      // Compact allows 16 bit addressing, data segment starts at 0
      private static Number[] dataBasedCompactConfigurationItemValues = { 
         0x00003000, // .text Base Address
         0x00000000, // Data Segment base address
         0x00001000, // .extern Base Address
         0x00001800, // Global Pointer $gp)
         0x00000000, // .data base Address
         0x00002000, // heap base address
         0x00002ffc, // stack pointer $sp 
         0x00002ffc, // stack base address
         0x00003fff, // highest address in user space
         0x00004000, // lowest address in kernel space
         0x00004000, // .ktext base address
         0x00004180, // exception handler address
         0x00005000, // .kdata base address
         0x00007f00, // MMIO base address
         0x00007fff, // highest address in kernel (and memory)
         0x00002fff, // data segment limit address
         0x00003ffc, // text limit address
         0x00007eff, // kernel data segment limit address
         0x00004ffc, // kernel text limit address
         0x00002000, // stack limit address
         0x00007fff  // memory map limit address
         };
   
      // Compact allows 16 bit addressing, text segment starts at 0
      private static Number[] textBasedCompactConfigurationItemValues = { 
         0x00000000, // .text Base Address
         0x00001000, // Data Segment base address
         0x00001000, // .extern Base Address
         0x00001800, // Global Pointer $gp)
         0x00002000, // .data base Address
         0x00003000, // heap base address
         0x00003ffc, // stack pointer $sp 
         0x00003ffc, // stack base address
         0x00003fff, // highest address in user space
         0x00004000, // lowest address in kernel space
         0x00004000, // .ktext base address
         0x00004180, // exception handler address
         0x00005000, // .kdata base address
         0x00007f00, // MMIO base address
         0x00007fff, // highest address in kernel (and memory)
         0x00003fff, // data segment limit address
         0x00000ffc, // text limit address
         0x00007eff, // kernel data segment limit address
         0x00004ffc, // kernel text limit address
         0x00003000, // stack limit address
         0x00007fff  // memory map limit address
         };
         
   
       public MemoryConfigurations() {
        
      }
   	
   
       public static void buildConfigurationCollection() {
         if (configurations == null) {
            configurations = new ArrayList();
            configurations.add(new MemoryConfiguration("32", "32-bit Architecture", configurationItemNames, configuration32));
            configurations.add(new MemoryConfiguration("64", "64-bit Architecture", configurationItemNames, configuration64));
         //   configurations.add(new MemoryConfiguration("CompactTextAtZero", "Compact, Text at Address 0", configurationItemNames, textBasedCompactConfigurationItemValues));
            defaultConfiguration = (MemoryConfiguration) configurations.get(0);
            currentConfiguration = defaultConfiguration;
         	// Get current config from settings
            setCurrentConfiguration(getConfigurationByName(Globals.getSettings().getMemoryConfiguration()));
         
         }
      }
   	
       public static Iterator getConfigurationsIterator() {
         if (configurations == null) {
            buildConfigurationCollection();
         }
         return configurations.iterator();
      
      }
   	
       public static MemoryConfiguration getConfigurationByName(String name) {
         Iterator configurationsIterator = getConfigurationsIterator();
         while (configurationsIterator.hasNext()) {
            MemoryConfiguration config = (MemoryConfiguration)configurationsIterator.next();
            if (name.equals(config.getConfigurationIdentifier())) {
               return config;
            }
         }		 
         return null;
      }
   	 
   	 
       public static MemoryConfiguration getDefaultConfiguration() {
         if (defaultConfiguration == null) {
            buildConfigurationCollection();
         }
         return defaultConfiguration;
      }
      
       public static MemoryConfiguration getCurrentConfiguration() {
         if (currentConfiguration == null) {
            buildConfigurationCollection();
         }
         return currentConfiguration;
      }
   	
       public static boolean setCurrentConfiguration(MemoryConfiguration config) {
         if (Globals.memory == null || config == null) 
            return false;
         if (config != currentConfiguration) {
            currentConfiguration = config;
            Globals.memory.clear();
            RV32IRegisters.getUserRegister("gp").changeResetValue(config.getGlobalPointer());
            RV32IRegisters.getUserRegister("sp").changeResetValue(config.getStackPointer());
            RV32IRegisters.getProgramCounterRegister().changeResetValue(config.getTextBaseAddress());
            RV32IRegisters.initializeProgramCounter(config.getTextBaseAddress());
            RV32IRegisters.resetRegisters();
            return true;
         }
         else {
            return false;
         }
      }
   	
       public static int getCurrentComputingArchitecture() {
    	   if (currentConfiguration == null) 
               buildConfigurationCollection();
    	   return Integer.parseInt(currentConfiguration.getConfigurationIdentifier());
       }
       

   ////  Use these to intialize Memory static variables at launch
   			
       public static Number getDefaultTextBaseAddress() {
         return configuration32[0];
      }
   	
       public static Number getDefaultDataSegmentBaseAddress() {
         return configuration32[1];
      }
   	
       public static Number getDefaultExternBaseAddress() {
         return configuration32[2];
      }
   
       public static Number getDefaultGlobalPointer() {
         return configuration32[3];
      }
   
       public static Number getDefaultDataBaseAddress() {
         return configuration32[4];
      }
   
       public static Number getDefaultHeapBaseAddress() {
         return configuration32[5];
      }
   
       public static Number getDefaultStackPointer() {
         return configuration32[6];
      }
   
       public static Number getDefaultStackBaseAddress() {
         return configuration32[7];
      }
   
       public static Number getDefaultUserHighAddress() {
         return configuration32[8];
      }
   
       public static Number getDefaultKernelBaseAddress() {
         return configuration32[9];
      }
   
       public static Number getDefaultKernelTextBaseAddress() {
         return configuration32[10];
      }
   
       public static Number getDefaultExceptionHandlerAddress() {
         return configuration32[11];
      }
   
       public static Number getDefaultKernelDataBaseAddress() {
         return configuration32[12];
      }
   
       public static Number getDefaultMemoryMapBaseAddress() {
         return configuration32[13];
      }
   
       public static Number getDefaultKernelHighAddress () {
         return configuration32[14];
      }   
      
       public Number getDefaultDataSegmentLimitAddress() {
         return configuration32[15];
      }
      
       public Number getDefaultTextLimitAddress() {
         return configuration32[16];
      }
      
       public Number getDefaultKernelDataSegmentLimitAddress() {
         return configuration32[17];
      }
      
       public Number getDefaultKernelTextLimitAddress() {
         return configuration32[18];
      }
      
       public Number getDefaultStackLimitAddress() {
         return configuration32[19];
      }
      
       public Number getMemoryMapLimitAddress() {
         return configuration32[20];
      } 
   
   }