package mars.venus;

import java.awt.Dimension;
import java.awt.SystemColor;
import java.util.Observable;
import java.util.Observer;

public class TabButton extends CustomButton implements Observer{

	public TabButton(String icon, ButtonListener action, String text) {
		super(icon, null, text);
		this.setPreferredSize(new Dimension(90, 32));
	}

	@Override
	public void update(Observable o, Object arg) {
		
		if(this == arg) {
			//System.out.println("if: "+ this.getMouseListeners().length);

			this.removeMouseListener(mouseListener);
	   		buttonPanel.setBackground(SystemColor.window);
		   	buttonPanel.validate();
	   		buttonPanel.repaint();
		}
		else {
			//System.out.println("else: "+ this.getMouseListeners().length);
		 	if(this.getMouseListeners().length == 1)
				this.addMouseListener(mouseListener);
	   		buttonPanel.setBackground(SystemColor.activeCaptionBorder);
	   		buttonPanel.validate();
   			buttonPanel.repaint();
		}
		
	}

	

}
