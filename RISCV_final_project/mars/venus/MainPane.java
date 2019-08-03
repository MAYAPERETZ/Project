package mars.venus;
import javax.swing.*;
import java.awt.*;


	
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
	  *  Creates the tabbed areas in the UI and also created the internal windows that 
	  *  exist in them.
	  *   @author Maya Peretz
	  **/

    public class MainPane extends JInternalFrame{
      EditPane editTab;
      ExecutePane executeTab;
      EditTabbedPane editTabbedPane;
      private GUI mainUI;
      private JTabbedPane jTabbedPane;
    /**
      *  Constructor for the MainPane class. 
   	**/
   	
       public MainPane(GUI mainUI2, Editor editor, RegistersWindow regs,
                       Coprocessor1Window cop1Regs,Coprocessor0Window cop0Regs){
           super("Data Segment", true, false, false, false);
           setFrameIcon(null);
         this.mainUI = mainUI2;
         editTabbedPane = new EditTabbedPane(mainUI2, editor, this);
         executeTab = new ExecutePane(mainUI2, regs, cop1Regs, cop0Regs);
         jTabbedPane = new JTabbedPane();
         add(jTabbedPane);
         jTabbedPane.addTab("Editor", editTabbedPane);
         jTabbedPane.addTab("Execute", executeTab);
         setPreferredSize(new Dimension(700, 500));
         setMaximumSize(new Dimension(700, 500));
         setMinimumSize(new Dimension(700, 500));
         setMaximizable(true);
         setVisible(true);
         
      }
      
       
   	/**
   	 * Returns current edit pane.  Implementation changed for MARS 4.0 support
   	 * for multiple panes, but specification is same.
   	 *
   	 * @return the editor pane
   	 */
    
       public EditPane getEditPane() {
         return editTabbedPane.getCurrentEditTab();
      }
   
   	/**
   	 * Returns component containing editor display  
   	 *
   	 * @return the editor tabbed pane
   	 */
       public JComponent getEditTabbedPane() {
         return editTabbedPane;
      }
   
   	/**
   	 * returns component containing execution-time display
   	 *
   	 * @return the execute pane
   	 */   	
       public ExecutePane getExecutePane() {
         return executeTab;
      }

      public void setCurrentTab(JComponent jComponent){
           jTabbedPane.setSelectedComponent(jComponent);
      }

   }