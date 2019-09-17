package mars.venus;

import mars.Globals;
import mars.riscv.hardware.Coprocessor0;
import mars.riscv.hardware.Coprocessor1;
import mars.riscv.hardware.RVIRegisters;
import mars.riscv.hardware.memory.Memory;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Observable;

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
* Action  for the Run -> Backstep menu item
*/
public class RunBackstepAction extends RunAction {

    String name;
    ExecutePane executePane;
    public RunBackstepAction(String name, Icon icon, String descrip,
                     Integer mnemonic, KeyStroke accel, GUI mainUI, NewObservable observable) {
        super(name, icon, descrip, mnemonic, accel, mainUI, observable);
    }
    /**
     * perform next simulated instruction step.
     */
    public void actionPerformed(ActionEvent e){
        name = this.getValue(Action.NAME).toString();
        executePane = mainUI.getMainPane().getExecutePane();
        if(!FileStatus.isAssembled()){
                 // note: this should never occur since backstepping is only enabled after successful assembly.
            JOptionPane.showMessageDialog(mainUI,"The program must be assembled before it can be run.");
            return;
        }
         mainUI.setStarted(true);
         mainUI.messagesPane.selectRunMessageTab();
         executePane.getTextSegmentWindow().setCodeHighlighting(true);

        if (Globals.getSettings().getBackSteppingEnabled()) {
            boolean inDelaySlot = Globals.program.getBackStepper().inDelaySlot(); // Added 25 June 2007
            Memory.getInstance().addObserver(executePane.getDataSegmentWindow());
            RVIRegisters.addRegistersObserver(executePane.getRegistersWindow());
            Coprocessor0.addRegistersObserver(executePane.getCoprocessor0Window());
            Coprocessor1.addRegistersObserver(executePane.getCoprocessor1Window());
            Globals.program.getBackStepper().backStep();
            Memory.getInstance().deleteObserver(executePane.getDataSegmentWindow());
            RVIRegisters.deleteRegistersObserver(executePane.getRegistersWindow());
            executePane.getRegistersWindow().updateRegisters();
            executePane.getCoprocessor1Window().updateRegisters();
            executePane.getCoprocessor0Window().updateRegisters();
            executePane.getDataSegmentWindow().updateValues();
            executePane.getTextSegmentWindow().highlightStepAtPC(inDelaySlot); // Argument aded 25 June 2007
            FileStatus.set(FileStatus.RUNNABLE);
            mainUI.setReset(false);
        }
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        super.update(arg0, arg1);
        if ((int)arg1 == FileStatus.RUNNABLE ||
          (int)arg1 == FileStatus.TERMINATED) {
            status = (Globals.getSettings().getBackSteppingEnabled() &&
                      !Globals.program.getBackStepper().empty());
            this.setEnabled(status);
        }
    }

}