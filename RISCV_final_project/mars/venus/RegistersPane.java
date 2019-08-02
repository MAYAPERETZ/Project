   package mars.venus;

import javax.swing.*;
import java.awt.Color;

/*
Copyright (c) 2003-2006,  Pete Sanderson and Kenneth Vollmar

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
	  *  Contains tabbed areas in the UI to display register contents
	  *   @author Sanderson 
	  *   @version August 2005
	  **/

    public class RegistersPane extends JInternalFrame {
      RegistersWindow regsTab;
	  Coprocessor1Window cop1Tab;
	  Coprocessor0Window cop0Tab;
	  private JTabbedPane jTabbedPane;
   	
      private GUI mainUI;

    /**
      *  Constructor for the RegistersPane class. 
   	**/
   	
       public RegistersPane(GUI mainUI2, RegistersWindow regs, Coprocessor1Window cop1,
		                      Coprocessor0Window cop0){
		   super("Data Segment", true, false, true, true);
		   setFrameIcon(null);
    	   this.mainUI = mainUI2;
    	   regsTab = regs;
    	   cop1Tab = cop1;
    	   cop0Tab = cop0;
    	   regsTab.setVisible(true);
    	   cop1Tab.setVisible(true);
    	   cop0Tab.setVisible(true);
    	   jTabbedPane = new JTabbedPane();
    	   jTabbedPane.addTab( "Registers",  regsTab);
    	   jTabbedPane.addTab("Coproc 1",  cop1Tab);
    	   jTabbedPane.addTab( "Coproc 0",  cop0Tab);
    	   add(jTabbedPane);
    	   setVisible(true);

      }
   	
		/**
		 * Return component containing integer register set.
		 *
		 * @return integer register window
		 */
       public RegistersWindow getRegistersWindow() {
         return regsTab;
      }
		/**
		 * Return component containing coprocessor 1 (floating point) register set.
		 *
		 * @return floating point register window
		 */
       public Coprocessor1Window getCoprocessor1Window() {
         return cop1Tab;
      }   	
		/**
		 * Return component containing coprocessor 0 (exceptions) register set.
		 *
		 * @return exceptions register window
		 */   
       public Coprocessor0Window getCoprocessor0Window() {
         return cop0Tab;
      }

       public void setCurrentTab(JComponent jComponent){
       		jTabbedPane.setSelectedComponent(jComponent);
	   }
}