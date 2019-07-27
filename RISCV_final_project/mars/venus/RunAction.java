package mars.venus;

import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import mars.Globals;

/**
 * parent class for Action subclasses to be defined for every menu/toolbar
 * run option which its state is changeable
 * 
 * @author Maya Peretz
 * 
 */
public class RunAction extends ChangeableAction{

	protected boolean status = false;
	
	protected RunAction(String name, Icon icon, String descrip, Integer mnemonic,
			KeyStroke accel, GUI mainUI2, NewObservable newObservable) {
		super(name, icon, descrip, mnemonic, accel, mainUI2, newObservable);
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
	  switch ((int)arg1) {
      case FileStatus.NO_FILE:
      case FileStatus.NEW_NOT_EDITED:
      case FileStatus.NEW_EDITED:
      case FileStatus.EDITED:
      case FileStatus.RUNNING:
    	  this.setEnabled(status = false);
    	  break;
      case FileStatus.TERMINATED:
    	  if(!(this instanceof RunClearBreakpointsAction))
    		  this.setEnabled(status = false);
    	  break;
      case FileStatus.NOT_EDITED:
    	  if (!(Globals.getSettings().getBooleanSetting(
    			  mars.Settings.ASSEMBLE_ALL_ENABLED)))
        	  this.setEnabled(status = false);
    	  break;
      case FileStatus.RUNNABLE:
    	  if(!(this instanceof RunClearBreakpointsAction))
    		  this.setEnabled(status = true);
    	  break;
      case FileStatus.OPENING:// This is a temporary state. DPS 9-Aug-2011
    	  break;
      default:
    	  System.out.println("Invalid File Status: "+status);
    	  break;
	  }
  }
	
}
