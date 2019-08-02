   package mars.venus;
   import mars.simulator.*;
import mars.*;
   import java.util.*;
   import java.awt.*;
   import java.awt.event.*;
   import javax.swing.*;
   import java.io.*;
	
	/*
Copyright (c) 2003-2008,  Pete Sanderson and Kenneth Vollmar

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
    * Action class for the Settings menu item to control whether or not
	 * program arguments can be entered and used.  If so, a text field
	 * will be displayed where they can be interactively entered.
    */
    public class SettingsProgramArgumentsAction extends GuiAction  {
   	
   
       public SettingsProgramArgumentsAction(String name, Icon icon, String descrip,
                             Integer mnemonic, KeyStroke accel, GUI mainUI) {
         super(name, icon, descrip, mnemonic, accel, mainUI);
      }
   		 
       public void actionPerformed(ActionEvent e) {
		   boolean selected = ((JCheckBoxMenuItem)e.getSource()).isSelected();
		   Globals.getSettings().setProgramArguments(selected);
			if (selected) {
			   Globals.getGui().getMainPane().getExecutePane().getTextSegmentWindow().addProgramArgumentsPanel();
			} else {
			   Globals.getGui().getMainPane().getExecutePane().getTextSegmentWindow().removeProgramArgumentsPanel();			
			}
      }
   	   	
   }