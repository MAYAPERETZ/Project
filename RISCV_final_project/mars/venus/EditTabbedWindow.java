package mars.venus;

import javax.swing.JInternalFrame;

public class EditTabbedWindow extends JInternalFrame{

	private EditTabbedPane editTabbedPane;
	
	public EditTabbedWindow(GUI appFrame, Editor editor, MainPane mainPane) {
		
		editTabbedPane = new EditTabbedPane(appFrame, editor, mainPane);
		add(editTabbedPane);
		
	}
	
	public EditTabbedPane getEditTabbedPane() {
		return editTabbedPane;
	}

}
