package mars.venus;

import mars.simulator.Simulator;
import java.util.Observable;

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
* Action class for the Run -> Stop menu item (and toolbar icon)
*/
public class RunStopAction extends RunAction  {

    public RunStopAction(String name, javax.swing.Icon icon, String descrip,
                         Integer mnemonic, javax.swing.KeyStroke accel,
                         GUI mainUI, NewObservable observable) {
        super(name, icon, descrip, mnemonic, accel, mainUI, observable);
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {

       Simulator.getInstance().stopExecution(this);
        // RunGoAction's "stopped" method will take care of the cleanup.
    }

    /**
     * @see ChangeableAction#update(Observable, Object)
     */
    @Override
    public void update(java.util.Observable arg0, Object arg1) {
          super.update(arg0, arg1);
          switch ((int)arg1) {
          case FileStatus.RUNNABLE:
             this.setEnabled(status = false);
             break;
          case FileStatus.RUNNING:
             this.setEnabled(status = true);
             break;
          }
     }
   	   	
}