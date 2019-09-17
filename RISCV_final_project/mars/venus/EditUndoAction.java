package mars.venus;

import java.awt.event.*;
import java.util.Observable;
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
* Action  for the Edit -> Undo menu item
*/
public class EditUndoAction extends ChangeableAction{

    public EditUndoAction(String name, Icon icon, String descrip,
                     Integer mnemonic, KeyStroke accel, GUI mainUI, NewObservable observable) {
        super(name, icon, descrip, mnemonic, accel, mainUI, observable);
        setEnabled(false);
    }

    /**
    * Adapted from TextComponentDemo.java in the
    * Java Tutorial "Text Component Features"
    */
    public void actionPerformed(ActionEvent e) {
        EditPane editPane = mainUI.getMainPane().getEditPane();
        if (editPane != null) {
            editPane.undo();
            updateUndoState();
            mainUI.editRedoAction.updateRedoState();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Observable arg0, Object arg1) {
        updateUndoState();
    }

    void updateUndoState() {
        EditPane editPane = mainUI.getMainPane().getEditPane();
        setEnabled(editPane != null && editPane.getUndoManager().canUndo());
    }
}