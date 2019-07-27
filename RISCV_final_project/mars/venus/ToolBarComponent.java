package mars.venus;

import javax.swing.Action;
import javax.swing.JButton;

public class ToolBarComponent extends JButton{

	public ToolBarComponent() {
		super();
	   // this.setContentAreaFilled(false);
	    this.setOpaque(false);
		setFocusable(false);
        setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
	}
	
	public ToolBarComponent(Action action) {
		super(action);
		//this.setContentAreaFilled(false);
		this.setOpaque(false);
		setFocusable(false);
        setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
	}
}
