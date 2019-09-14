   package mars.mips.hardware;

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
 * Models the memory configuration for the simulated MIPS machine.
 * "configuration" refers to the starting memory addresses for
 * the various memory segments.
 * The default configuration is based on SPIM.  Starting with MARS 3.7,
 * the configuration can be changed.
 * @author Pete Sanderson 
 * @version August 2009
 */


    public class MemoryConfiguration {
	   // Identifier is used for saving setting; name is used for display
      private String configurationIdentifier, configurationName;
      private String[] configurationItemNames;
      private Number[] configurationItemValues;
   	
   
       public MemoryConfiguration(String ident, String name, String[] items) {
		   this.configurationIdentifier = ident;
         this.configurationName = name;
         this.configurationItemNames = items;
      }
       
  
       
       public MemoryConfiguration(String ident, String name, String[] items, Number[] values) {
		this(ident, name, items);
		configurationItemValues = values;
      }
   	
   	public String getConfigurationIdentifier() {
		   return configurationIdentifier;
	   }
		
       public String getConfigurationName() {
         return configurationName;
      }
   
      public Number[] getConfigurationItemValues() {
         return configurationItemValues;
      }
   	
       public String[] getConfigurationItemNames() {
         return configurationItemNames;
      }
   			
      public Number getTextBaseAddress() {
         return configurationItemValues[0];
      }
		
      public Number getDataSegmentBaseAddress() {
         return configurationItemValues[1];
      }
		
      public Number getExternBaseAddress() {
         return configurationItemValues[2];
      }
		   
      public Number getGlobalPointer() {
         return configurationItemValues[3];
      }
   
      public Number getDataBaseAddress() {
         return configurationItemValues[4];
      }
   
      public Number getHeapBaseAddress() {
         return configurationItemValues[5];
      }
   
      public Number getStackPointer() {
         return configurationItemValues[6];
      }
   
      public Number getStackBaseAddress() {
         return configurationItemValues[7];
      }
   
      public Number getUserHighAddress() {
         return configurationItemValues[8];
      }
   
      public Number getKernelBaseAddress() {
         return configurationItemValues[9];
      }
   
      public Number getKernelTextBaseAddress() {
         return configurationItemValues[10];
      }
   
      public Number getExceptionHandlerAddress() {
         return configurationItemValues[11];
      }
   
      public Number getKernelDataBaseAddress() {
         return configurationItemValues[12];
      }
   
      public Number getMemoryMapBaseAddress() {
         return configurationItemValues[13];
      }
   
      public Number getKernelHighAddress () {
         return configurationItemValues[14];
      }
      
      public Number getDataSegmentLimitAddress() {
         return configurationItemValues[15];
      }
      
      public Number getTextLimitAddress() {
         return configurationItemValues[16];
      }
      
      public Number getKernelDataSegmentLimitAddress() {
         return configurationItemValues[17];
      }
      
      public Number getKernelTextLimitAddress() {
         return configurationItemValues[18];
      }
      
      public Number getStackLimitAddress() {
         return configurationItemValues[19];
      }
      public Number getMemoryMapLimitAddress() {
         return configurationItemValues[20];
      }   
   
   }