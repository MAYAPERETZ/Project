package mars.venus;

import javax.swing.*;

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
*/

/*
    FIXME: This class can be implemented in a more efficient way combining all
            RegisterWindow, Coprocessor0Window and Coprocessor1Window classes
            into a base class

 */
public class RegistersPane extends JInternalFrame {
	RegistersWindow regsTab;
	Coprocessor1Window cop1Tab;
	Coprocessor0Window cop0Tab;
	private JTabbedPane jTabbedPane;

	/**
	*  Constructor for the RegistersPane class.
	**/
	public RegistersPane(RegistersWindow regs, Coprocessor1Window cop1,
						 Coprocessor0Window cop0){
	   super("", true, false, false, false);
	   setFrameIcon(null);
		regsTab = regs;
	   cop1Tab = cop1;
	   cop0Tab = cop0;
	   regsTab.setVisible(true);
	   cop1Tab.setVisible(true);
	   cop0Tab.setVisible(true);
	   jTabbedPane = new JTabbedPane();
	   jTabbedPane.addTab( "Registers",  regsTab);
	   jTabbedPane.addTab("FP Registers",  cop1Tab);
	   jTabbedPane.addTab( "CSRs",  cop0Tab);
	   add(jTabbedPane);
	   setVisible(true);

	}

	/**
	* Return component containing integer register set.
	* @return integer register window
	*/
	public RegistersWindow getRegistersWindow() {
		return regsTab;
	}

	/**
	* Return component containing coprocessor 1 (floating point) register set.
	* @return floating point register window
	*/
	public Coprocessor1Window getCoprocessor1Window() {
		return cop1Tab;
	}

	/**
	* Return component containing coprocessor 0 (exceptions) register set.
	* @return exceptions register window
	*/
	public Coprocessor0Window getCoprocessor0Window() {
		return cop0Tab;
	}

	/**
	* Sets current tab of the register pane
	* @param jComponent the tab to be set as current
	*/
	public void setCurrentTab(JComponent jComponent){
		jTabbedPane.setSelectedComponent(jComponent);
	}

	/**
	* Return current tab of the register pane
	* @return current tab of the register pane
	*/
	public JComponent getCurrentTab(){
		return (JComponent)jTabbedPane.getSelectedComponent();
	}

}