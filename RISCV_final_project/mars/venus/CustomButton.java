package mars.venus;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIDefaults;

import com.sun.prism.Graphics;

import mars.Globals;
import mars.venus.CustomTabbedPane.TabButton;



public class CustomButton extends JButton{

	protected JPanel buttonPanel;
	protected final ButtonPanelMouseListener mouseListener = new ButtonPanelMouseListener();
	protected String text = "";
	private CustomButton customButton;
	
	public CustomButton(String icon, ButtonListener action, String text) {
		customButton = this;
		if(text != null)
			this.setText(this.text = text);
		else this.setText(this.text);
		this.setForeground(new Color(255, 255, 255));
		this.setFont(new Font("Arial", Font.CENTER_BASELINE, 11));
		this.setBorder(null);
		this.setBorderPainted(false);
	   	this.setContentAreaFilled(false);
	   	this.setFocusPainted(false);
	   
	   	buttonPanel = new JPanel();
		buttonPanel.setBackground(new Color(81, 81, 81));
	   	if(icon != null) {
	   		Toolkit tk = Toolkit.getDefaultToolkit();
	   		Class cs =  this.getClass(); 
	   		this.setIcon(
	   				new ImageIcon(
	   						tk.getImage(
	   								cs.getResource(
	   										Globals.imagesPath + icon))));
	   		
			buttonPanel.setMinimumSize(
					new Dimension(this.getIcon().getIconWidth(), this.getIcon().getIconHeight()));
	   	}
	   		
		
		if(action != null) {
			this.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					action.buttonListener();
			}
			});
		}
		
 	   	this.addMouseListener(mouseListener);
 	  
 	   	buttonPanel.setLayout(new BorderLayout(0, 0));
 	   	buttonPanel.add(this);
	}
	
	public CustomButton(String icon, ButtonListener action) {
		this(icon, action, null);
	}
	
	public CustomButton(ButtonListener action, String text) {
		this(null, action, text);
	}
	
	public CustomButton(String icon, String text) {
		this(icon, null, text);
	}
	
	
	JPanel getButtonPanel() {
		return buttonPanel;
	}
	
    public interface ButtonListener {
        void buttonListener();
    }
    
    class ButtonPanelMouseListener extends MouseAdapter{
    		private SystemColor color;
	   		@Override
	   		public void mouseEntered(MouseEvent e) {
	   			color = (!(e.getSource() instanceof TabButton)) ? 
	   					SystemColor.scrollbar : SystemColor.window;
	   			buttonPanel.setBackground(color);
	   			customButton.setForeground(new Color(81, 81, 81));
		   		buttonPanel.validate();
	   			buttonPanel.repaint();

	   		}
	   		@Override
	   		public void mouseExited(MouseEvent e) {
	   			buttonPanel.setBackground(new Color(81, 81, 81));
	   			customButton.setForeground(new Color(255, 255, 255));
	   			buttonPanel.validate();
	   			buttonPanel.repaint();
	   		}
    }
    
}
