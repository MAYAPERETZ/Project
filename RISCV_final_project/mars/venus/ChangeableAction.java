package mars.venus;

import java.util.Observable;
import java.util.Observer;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * parent class for Action subclasses to be defined for every menu/toolbar
 * option which its state is changeable
 * @author Maya Peretz
 * @version September 2019
 *
 */
public class ChangeableAction extends GuiAction implements Observer{

	private NewObservable newObservable;
	protected ChangeableAction(String name, Icon icon, String descrip,
			Integer mnemonic, KeyStroke accel, GUI mainUI2) {
		super(name, icon, descrip, mnemonic, accel, mainUI2);
	}
	
	protected ChangeableAction(String name, Icon icon, String descrip, Integer mnemonic,
			KeyStroke accel, GUI mainUI2, NewObservable newObservable) {
		this(name, icon, descrip, mnemonic, accel, mainUI2);
		this.newObservable = newObservable;
		newObservable.addObserver(this);
	}
	
	/**
	*  Determines from FileStatus what the menu state (enabled/disabled)should be.
	*/
    @Override
	public void update(Observable arg0, Object arg1) {
  	  	boolean status;
  	  	switch ((int)arg1) {
        case FileStatus.NO_FILE:
           status = (this instanceof FileNewAction
          		 || this instanceof FileOpenAction
          		 || this instanceof SettingsDelayedBranchingAction
          		 || this instanceof SettingsMemoryConfigurationAction
          		 || this instanceof FileExitAction);
     	   this.setEnabled(status);
           break;
        case FileStatus.NEW_NOT_EDITED:
        case FileStatus.NEW_EDITED:
        case FileStatus.NOT_EDITED:
        case FileStatus.EDITED:
        case FileStatus.TERMINATED:	
        case FileStatus.RUNNABLE:
      	   this.setEnabled(true);
      	   break; 
        case FileStatus.RUNNING:
       	   this.setEnabled(false);
       	   break;
        case FileStatus.OPENING:// This is a temporary state. DPS 9-Aug-2011
           break;
        default:
           System.out.println("Invalid File Status");
           break;
  	  	}
}
	
}
