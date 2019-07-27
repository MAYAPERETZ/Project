package mars.venus;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import mars.Globals;
import mars.venus.CustomButton.ButtonListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.awt.EventQueue;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import javax.swing.border.MatteBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.BevelBorder;
import javax.swing.JTable;


/**
 *
 *@author Maya
 */
public class CustomTabbedPane extends JPanel{

	private CustomTabbedPane customTabbedPane;
	private JPanel windowControlButtons;
	private CustomButton minimize;
	private JButton maximize;
	NewObservable observable;
	protected JPanel mainPanel;
	private IconifiedWindow iconified;
	private HashMap<JComponent, TabButton> tabMap;
	private JToolBar toolBar;

	public CustomTabbedPane() {
		
		setBorder(new LineBorder(new Color(51, 153, 255), 1));
		setLayout(new BorderLayout(0, 0));
	
		mainPanel = new JPanel();
		mainPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		mainPanel.setAutoscrolls(true);
		observable = new NewObservable();
		customTabbedPane = this;
		iconified = new IconifiedWindow();
		
		mainPanel.setBackground(new Color(51, 51, 51));
		add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));		
		windowControlButtons = new JPanel();
		JPanel titleBar = new JPanel();
		add(titleBar, BorderLayout.NORTH);
		titleBar.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(51, 153, 255)));
		titleBar.setBackground(new Color(81, 81, 81));
		titleBar.setLayout(new BorderLayout(0, 0));
		
		windowControlButtons.setAlignmentX(Component.RIGHT_ALIGNMENT);
		windowControlButtons.setBackground(new Color(71, 71, 71));
		titleBar.add(windowControlButtons, BorderLayout.EAST);
		windowControlButtons.setLayout(new BoxLayout(windowControlButtons, BoxLayout.X_AXIS));
		
		toolBar = new JToolBar();
		toolBar.setForeground(Color.WHITE);
		toolBar.setBackground(new Color(81, 81, 81));
		toolBar.setBorderPainted(false);
		toolBar.setBorder(null);
		titleBar.add(toolBar, BorderLayout.WEST);
		
		ButtonListener cont = new ButtonListener() {
		
			@Override
			public void buttonListener() {
				customTabbedPane.setVisible(false);
				customTabbedPane.validate();
				customTabbedPane.repaint();
				Globals.getGui().addIconifiedToToolBar(iconified);

			}
		};
		
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				minimize = new CustomButton(
         			   "minimize_window.png"
         			   , cont);
				minimize.setBackground(new Color(255, 255, 255));
	
				insertControlButton(minimize);				
			}
		});

		
	}
	
	void insertControlButton(CustomButton contButt) {
		
  	   	windowControlButtons.add(contButt.getButtonPanel());
		windowControlButtons.validate();
		windowControlButtons.repaint();

	}
	
	void insertTab(String icon, String text, javax.swing.JComponent panel) {

			TabButton newButtTab = new TabButton(icon, text);
			observable.addObserver(newButtTab);
			iconified.insertTabLbl(icon);
        	newButtTab.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		observable.notifyObserversOfSelectedTab(evt.getSource());
        		setCurrentPanel(panel);
        	

            }});
        	
        	toolBar.add(newButtTab.getButtonPanel());
  
        	if(toolBar.getComponentCount() == 1)
        		newButtTab.doClick();
        	if(tabMap == null) tabMap = new HashMap<JComponent, CustomTabbedPane.TabButton>();
        	tabMap.put(panel, newButtTab);
			mainPanel.validate();
			mainPanel.repaint();
		
	}
	
	void setCurrentTab(javax.swing.JComponent currentTab) {
		if(currentTab != this.getCurrentTab())
			tabMap.get(currentTab).doClick();		
	}
	
	private void setCurrentPanel(javax.swing.JComponent currentTab) {
		
		mainPanel.removeAll();
		currentTab.setBackground(new Color(51, 51, 51));
		mainPanel.add(currentTab);
		mainPanel.validate();
		mainPanel.repaint();
	}
	
	
	public javax.swing.JComponent getCurrentTab() {
		return (javax.swing.JComponent)mainPanel.getComponent(0);
	}
	
   
    class TabButton extends CustomButton implements Observer{

    	public TabButton( String icon, String text) {
    		super(icon, text);
    		this.setPreferredSize(new Dimension(90, 32));
    	}

    	@Override
    	public void update(Observable o, Object arg) {
    		
    		if(this == arg) {
    			this.removeMouseListener(mouseListener);
    	   		buttonPanel.setBackground(new Color(51, 153, 255));
    	   		this.setForeground(new Color(255, 255, 255));
    		   	buttonPanel.validate();
    	   		buttonPanel.repaint();
    		}
    		else {
    		 	if(this.getMouseListeners().length == 1)
    				this.addMouseListener(mouseListener);
	   			buttonPanel.setBackground(new Color(81, 81, 81));
	   			this.setForeground(new Color(255, 255, 255));
    	   		buttonPanel.validate();
       			buttonPanel.repaint();

    		}
    		
    	}	

    }
    
    private class IconifiedWindow extends JToolBar{
		
		private IconifiedWindow  iconifiedWindow = this;
		private IconifiedWindow(){
		setBackground(new Color(111, 111, 111));
		this.addSeparator();

		 setBorder(null);
		    setOpaque(false);
			EventQueue.invokeLater(
	                  new Runnable() {
	                     public void run() {
	                    	 maximize = new ToolBarComponent(
	                    		new AbstractAction() {
										
								@Override
								public void actionPerformed(ActionEvent e) {
									customTabbedPane.setIgnoreRepaint(false);
									customTabbedPane.setVisible(true);
                					customTabbedPane.validate();
                					customTabbedPane.repaint();
                					Globals.getGui().removeIconifiedFromToolBar(iconifiedWindow);
									customTabbedPane.setIgnoreRepaint(true);
											
								}});
	             	   		Toolkit tk = Toolkit.getDefaultToolkit();
	            	   		Class cs =  this.getClass(); 
	            	   		maximize.setIcon(
	            	   				new ImageIcon(tk.getImage(
	            	   						cs.getResource(Globals.imagesPath
	            	   								+ "icons8_maximize_window_32px_1.png"))));
	                    	 iconifiedWindow.add(maximize);
	                     }
	                  });
		}
		
		private void insertTabLbl(String icon) {
			if(icon != null) {
		   		Toolkit tk = Toolkit.getDefaultToolkit();
		   		Class cs =  this.getClass(); 
		   		this.add(
		   				new JLabel(
		   				new ImageIcon(
		   						tk.getImage(
		   								cs.getResource(
		   										Globals.imagesPath + icon)))));
		   		this.addSeparator(new Dimension(5, 0));
		   	
		   	}
		}
	}

}