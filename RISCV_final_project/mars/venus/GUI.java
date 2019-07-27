/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.venus;

/**
 *
 * @author XXX
 */
import mars.*;
import mars.mips.dump.*;
import mars.venus.*;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.MatteBorder;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javafx.scene.control.Skin;
/*
Copyright (c) 2003-2013,  Pete Sanderson and Kenneth Vollmar

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
	  *  Top level container for Venus GUI.
	  *   @author Sanderson and Team JSpim
	  **/
	  
	  /* Heavily modified by Pete Sanderson, July 2004, to incorporate JSPIMMenu and JSPIMToolbar
	   * not as subclasses of JMenuBar and JToolBar, but as instances of them.  They are both
		* here primarily so both can share the Action objects.
		*/
	
 public class GUI extends JFrame{
   GUI mainUI;
   public JMenuBar menu;
   JToolBar toolbar;
   MainPane mainPane; 
   RegistersPane registersPane; 
   RegistersWindow registersTab;
   Coprocessor1Window coprocessor1Tab;
   Coprocessor0Window coprocessor0Tab;
   MessagesPane messagesPane;
   JSplitPane splitter, horizonSplitter;
   JPanel north;

   private int frameState; // see windowActivated() and windowDeactivated()
   private static int menuState = FileStatus.NO_FILE;
  	
	// PLEASE PUT THESE TWO (& THEIR METHODS) SOMEWHERE THEY BELONG, NOT HERE
   private static boolean reset= true; // registers/memory reset for execution
   private static boolean started = false;  // started execution
   Editor editor;
	
	// components of the menubar
   private JMenu file, run, window, help, edit, settings;
   private JMenuItem fileNew, fileOpen, fileClose, fileCloseAll, fileSave, fileSaveAs, fileSaveAll, fileDumpMemory, filePrint, fileExit;
   private JMenuItem editUndo, editRedo, editCut, editCopy, editPaste, editFindReplace, editSelectAll;
   private JMenuItem runGo, runStep, runBackstep, runReset, runAssemble, runStop, runPause, runClearBreakpoints, runToggleBreakpoints;
   private JCheckBoxMenuItem settingsLabel, settingsPopupInput, settingsValueDisplayBase, settingsAddressDisplayBase,
           settingsExtended, settingsAssembleOnOpen, settingsAssembleAll, settingsWarningsAreErrors, settingsStartAtMain,
   		  settingsDelayedBranching, settingsProgramArguments, settingsSelfModifyingCode;
   private JMenuItem settingsExceptionHandler, settingsEditor, settingsHighlighting, settingsMemoryConfiguration;
   private JMenuItem helpHelp, helpAbout;
      
   // components of the toolbar
   private ToolBarComponent Undo, Redo, Cut, Copy, Paste, FindReplace;
   private ToolBarComponent New, Open, Save, SaveAs, SaveAll, DumpMemory, Print;
   private ToolBarComponent Run, Assemble, Reset, Step, Backstep, Stop, Pause;
   private ToolBarComponent Help;
   private JButton SelectAll;

   // The "action" objects, which include action listeners.  One of each will be created then
	// shared between a menu item and its corresponding toolbar button.  This is a very cool
	// technique because it relates the button and menu item so closely
	
   private Action fileNewAction, fileOpenAction, fileCloseAction, fileCloseAllAction, fileSaveAction;
   private Action fileSaveAsAction, fileSaveAllAction, fileDumpMemoryAction, filePrintAction, fileExitAction;
   EditUndoAction editUndoAction;
   EditRedoAction editRedoAction;
   private Action editCutAction, editCopyAction, editPasteAction, editFindReplaceAction, editSelectAllAction;
   private Action runAssembleAction, runGoAction, runStepAction, runBackstepAction, runResetAction, 
                  runStopAction, runPauseAction, runClearBreakpointsAction, runToggleBreakpointsAction;
   private Action settingsLabelAction, settingsPopupInputAction, settingsValueDisplayBaseAction, settingsAddressDisplayBaseAction,
                  settingsExtendedAction, settingsAssembleOnOpenAction, settingsAssembleAllAction,
   					settingsWarningsAreErrorsAction, settingsStartAtMainAction, settingsProgramArgumentsAction,
   					settingsDelayedBranchingAction, settingsExceptionHandlerAction, settingsEditorAction,
   					settingsHighlightingAction, settingsMemoryConfigurationAction, settingsSelfModifyingCodeAction;    
   private Action helpHelpAction, helpAboutAction;
   private javax.swing.JPanel toolBarPanel, contentPane;
   NewObservable observable;
   private JToolBar toolBar;
   /**
   *  Constructor for the Class. Sets up a window object for the UI
	*   @param s Name of the window to be created.
	**/     

    public GUI(String s) {
      super(s);
      mainUI = this;
      Globals.setGui(this);
      this.editor = new Editor(this);
      contentPane = new javax.swing.JPanel();
      toolBarPanel = new javax.swing.JPanel();
      contentPane.setBackground(Color.BLACK);
      toolBarPanel.setBackground(new Color(111, 111, 111));
      toolBarPanel.setBorder(new MatteBorder(0, 0, 2, 0, (Color) new Color(51, 153, 255)));
      observable = new NewObservable();
      
      double screenWidth  = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
      double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
      // basically give up some screen space if running at 800 x 600
      double messageWidthPct = (screenWidth<1000.0)? 0.69 : 0.74;
      double messageHeightPct = (screenWidth<1000.0)? 0.17 : 0.15;
      double mainWidthPct = (screenWidth<1000.0)? 0.69 : 0.74;
      double mainHeightPct = (screenWidth<1000.0)? 0.60 : 0.58;
      double registersWidthPct = (screenWidth<1000.0)? 0.20 : 0.244;
      double registersHeightPct = (screenWidth<1000.0)? 0.95 : 0.88;
      
      // invoke only after toolBar has created and initialized
      
	
      Dimension messagesPanePreferredSize = new Dimension((int)(screenWidth*messageWidthPct),(int)(screenHeight*messageHeightPct)); 
      Dimension mainPanePreferredSize = new Dimension((int)(screenWidth*mainWidthPct),(int)(screenHeight*mainHeightPct));
      Dimension registersPanePreferredSize = new Dimension((int)(screenWidth*registersWidthPct)
    		  ,(int)(screenHeight*registersHeightPct));

      Globals.initialize(true);
      
      EventQueue.invokeLater(new Runnable() {
  		
			@Override
			public void run() {
			      double toolBarHeightPct = (screenWidth<1000.0)? 0.05 : toolBar.getHeight();
			      Dimension toolBarPanePreferredSize = new Dimension((int)(screenWidth*0.70),(int)(toolBarHeightPct));
			      toolBarPanel.setPreferredSize(toolBarPanePreferredSize);

			}
    });
   
   	//  image courtesy of NASA/JPL.  
     /* URL im = this.getClass().getResource(Globals.imagesPath+"RedMars16.gif");
      if (im == null) {
         System.out.println("Internal Error: images folder or file not found");
         System.exit(0);
      }				
    */	

      registersTab = new RegistersWindow();
      coprocessor1Tab = new Coprocessor1Window();
      coprocessor0Tab = new Coprocessor0Window();
      registersPane = new RegistersPane(mainUI, registersTab,coprocessor1Tab, coprocessor0Tab);
      registersPane.setPreferredSize(registersPanePreferredSize);
      mainPane = new MainPane(mainUI, editor, registersTab, coprocessor1Tab, coprocessor0Tab);   	
      mainPane.setPreferredSize(mainPanePreferredSize);
      messagesPane= new MessagesPane();
      messagesPane.setPreferredSize(messagesPanePreferredSize);
      
      // due to dependencies, do not set up menu/toolbar until now.
      this.createActionObjects();
      menu= this.setUpMenuBar();
      this.setJMenuBar(menu);
   	
      toolbar= this.setUpToolBar();
      javax.swing.GroupLayout gl_toolBar = new javax.swing.GroupLayout(toolBarPanel);
      toolBarPanel.setLayout(gl_toolBar);
      gl_toolBar.setHorizontalGroup(
          gl_toolBar.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(gl_toolBar.createSequentialGroup()
              .addComponent(toolbar, 0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addGap(4))
          .addGap(4)
      );
      gl_toolBar.setVerticalGroup(
          gl_toolBar.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(toolbar,javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
      );
     
   
      FileStatus.reset();
   	// The following has side effect of establishing menu state
      FileStatus.set(FileStatus.NO_FILE);  
   				
      // This is invoked when opening the app.  It will set the app to
      // appear at full screen size.
      this.addWindowListener(
             new WindowAdapter() {
                public void windowOpened(WindowEvent e) {
                  mainUI.setExtendedState(JFrame.MAXIMIZED_BOTH); 
               }
            });
   
     // This is invoked when exiting the app through the X icon.  It will in turn
     // check for unsaved edits before exiting.
      this.addWindowListener(
             new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                  if (mainUI.editor.closeAll()) {
                     System.exit(0);
                  } 
               }
            });
   			

     	// The following will handle the windowClosing event properly in the 
     	// situation where user Cancels out of "save edits?" dialog.  By default,
     	// the GUI frame will be hidden but I want it to do nothing.
        javax.swing.GroupLayout gl_contentPane = new javax.swing.GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
        	gl_contentPane.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_contentPane.createSequentialGroup()
        			.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
        				.addGroup(gl_contentPane.createSequentialGroup()
        					.addGap(4)
        					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
        						.addComponent(toolBarPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
        						.addGroup(gl_contentPane.createSequentialGroup()
        						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
        						.addComponent(mainPane,GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
        						.addComponent(messagesPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
        						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        						.addComponent(registersPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
        								Short.MAX_VALUE)
        						.addGap(4)))))));
        
        gl_contentPane.setVerticalGroup(
        	gl_contentPane.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_contentPane.createSequentialGroup()
        				.addComponent(toolBarPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addGap(4)
        			.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
        				.addGroup(gl_contentPane.createSequentialGroup()
        					.addComponent(mainPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
        					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        					.addComponent(messagesPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
        					.addComponent(registersPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
        					.addGap(4)
        ));
        contentPane.setLayout(gl_contentPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(contentPane, javax.swing.GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE,  Short.MAX_VALUE)
                .addGap(4)	
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(contentPane, javax.swing.GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
              )
      );
      
       getContentPane().setLayout(layout);
      
      
      this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      this.pack();
      
   }
    
    
    public void addIconifiedToToolBar(Component component) {
 	   	toolBar.add(component);
		toolBarPanel.validate();
		toolBarPanel.repaint();
	
 	   
    }
    
    public void removeIconifiedFromToolBar(Component component) {
 	   toolBar.remove(component);
 	   toolBarPanel.validate();
 	   toolBarPanel.repaint();
    }
	
 /*
  * Action objects are used instead of action listeners because one can be easily shared between
  * a menu item and a toolbar button.  Does nice things like disable both if the action is
  * disabled, etc.
  */
    private void createActionObjects() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Class cs = this.getClass(); 
        try {
           fileNewAction = new FileNewAction("New", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "icons8_file_25px_3.png"))),
                                           "Create a new file for editing", new Integer(KeyEvent.VK_N),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);		
           fileOpenAction = new FileOpenAction("Open ...", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "icons8_open_folder_25px.png"))),
              									  "Open a file for editing", new Integer(KeyEvent.VK_O),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);
              									  
           fileCloseAction = new FileCloseAction("Close", null,
                                           "Close the current file", new Integer(KeyEvent.VK_C),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);						
           fileCloseAllAction = new FileCloseAllAction("Close All", null,
                                           "Close all open files", new Integer(KeyEvent.VK_L),
              									  null, mainUI, observable);	
           fileSaveAction = new FileSaveAction("Save", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "icons8_save_25px.png"))),
              									  "Save the current file", new Integer(KeyEvent.VK_S),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);
           
           fileSaveAsAction = new FileSaveAsAction("Save as ...", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "icons8_save_as_25px.png"))),
              									  "Save current file with different name", new Integer(KeyEvent.VK_A),
              									  null, mainUI, observable);	
           fileSaveAllAction = new FileSaveAllAction("Save All", null,
                                           "Save all open files", new Integer(KeyEvent.VK_V),
              									  null, mainUI, observable);	
    //       fileDumpMemoryAction = new FileDumpMemoryAction("Dump Memory ...", 
      //                                     new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Dump22.png"))),
        //      									  "Dump machine code or data in an available format", new Integer(KeyEvent.VK_D),
         //     									  KeyStroke.getKeyStroke( KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
           //   									  mainUI);
              									  	
  /*         filePrintAction = new FilePrintAction("Print ...", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Print22.gif"))),
              									  "Print current file", new Integer(KeyEvent.VK_P),
              									  null, mainUI);
              									  */	
           fileExitAction = new FileExitAction("Exit", null,
              	                         "Exit", new Integer(KeyEvent.VK_X),
              									  null, mainUI, observable);	
           editUndoAction = new EditUndoAction("Undo", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "icons8_undo_25px.png"))),
              									  "Undo last edit", new Integer(KeyEvent.VK_U),
                                           KeyStroke.getKeyStroke( KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);	
           editRedoAction = new EditRedoAction("Redo", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "icons8_redo_25px.png"))),
              									  "Redo last edit", new Integer(KeyEvent.VK_R),
                                           KeyStroke.getKeyStroke( KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);			
           editCutAction = new EditCutAction("Cut", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "icons8_cut_25px.png"))),
              									  "Cut", new Integer(KeyEvent.VK_C),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);	
           editCopyAction = new EditCopyAction("Copy", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "icons8_copy_25px.png"))),
              									  "Copy", new Integer(KeyEvent.VK_O),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);	
           editPasteAction = new EditPasteAction("Paste", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "icons8_paste_25px.png"))),
              									  "Paste", new Integer(KeyEvent.VK_P),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);	
           editFindReplaceAction = new EditFindReplaceAction("Find/Replace", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "icons8_search_property_25px.png"))),
              									  "Find/Replace", new Integer(KeyEvent.VK_F),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);
           editSelectAllAction = new EditSelectAllAction("Select All", 
                                           null, //new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Find22.png"))),
              									  "Select All", new Integer(KeyEvent.VK_A),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);
           runAssembleAction = new RunAssembleAction("Assemble",  
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "icons8_puzzle_25px.png"))),
              									  "Assemble the current file and clear breakpoints", new Integer(KeyEvent.VK_A),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F3, 0), 
              									  mainUI, observable);			
           runGoAction = new RunGoAction("Go", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "play.png"))),
              									  "Run the current program", new Integer(KeyEvent.VK_G),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F5, 0),
              									  mainUI, observable);	
           runStepAction = new RunStepAction("Step", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "icons8_next_25px.png"))),
              									  "Run one step at a time", new Integer(KeyEvent.VK_T),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F7, 0),
              									  mainUI, observable);	
           runBackstepAction = new RunBackstepAction("Backstep", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "icons8_previous_25px.png"))),
              									  "Undo the last step", new Integer(KeyEvent.VK_B),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F8, 0), 
              									  mainUI, observable);	
           runPauseAction = new RunPauseAction("Pause", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "pause.png"))),
              									  "Pause the currently running program", new Integer(KeyEvent.VK_P),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F9, 0), 
              									  mainUI, observable);	
           runStopAction = new RunStopAction("Stop", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "icons8_stop_25px.png"))),
              									  "Stop the currently running program", new Integer(KeyEvent.VK_S),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F11, 0), 
              									  mainUI, observable);
           runResetAction = new RunResetAction("Reset", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "icons8_rewind_25px.png"))),
              									  "Reset MIPS memory and registers", new Integer(KeyEvent.VK_R),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F12,0),
              									  mainUI, observable);	
           runClearBreakpointsAction = new RunClearBreakpointsAction("Clear all breakpoints",
                                           null,
              									  "Clears all execution breakpoints set since the last assemble.",
              									  new Integer(KeyEvent.VK_K),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_K, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);  
           runToggleBreakpointsAction = new RunToggleBreakpointsAction("Toggle all breakpoints",
                                           null,
              									  "Disable/enable all breakpoints without clearing (can also click Bkpt column header)",
              									  new Integer(KeyEvent.VK_T),
              									  KeyStroke.getKeyStroke( KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);
              									  
           settingsLabelAction = new SettingsLabelAction("Show Labels Window (symbol table)",
                                           null,
              									  "Toggle visibility of Labels window (symbol table) in the Execute tab",
              									  null,null,
              									  mainUI);
           settingsPopupInputAction = new SettingsPopupInputAction("Popup dialog for input syscalls (5,6,7,8,12)",
                                           null,
              									  "If set, use popup dialog for input syscalls (5,6,7,8,12) instead of cursor in Run I/O window",
              									  null,null,
              									  mainUI);
        
           settingsValueDisplayBaseAction = new SettingsValueDisplayBaseAction("Values displayed in hexadecimal",
                                           null,
              									  "Toggle between hexadecimal and decimal display of memory/register values",
              									  null,null,
              									  mainUI);
           settingsAddressDisplayBaseAction = new SettingsAddressDisplayBaseAction("Addresses displayed in hexadecimal",
                                           null,
              									  "Toggle between hexadecimal and decimal display of memory addresses",
              									  null,null,
              									  mainUI);
           settingsExtendedAction  = new SettingsExtendedAction("Permit extended (pseudo) instructions and formats",
                                           null,
              									  "If set, MIPS extended (pseudo) instructions are formats are permitted.",
              									  null,null,
              									  mainUI);    
           settingsAssembleOnOpenAction    = new SettingsAssembleOnOpenAction("Assemble file upon opening",
                                           null,
              									  "If set, a file will be automatically assembled as soon as it is opened.  File Open dialog will show most recently opened file.",
              									  null,null,
              									  mainUI);
           settingsAssembleAllAction       = new SettingsAssembleAllAction("Assemble all files in directory",
                                           null,
              									  "If set, all files in current directory will be assembled when Assemble operation is selected.",
              									  null,null,
              									  mainUI);
           settingsWarningsAreErrorsAction = new SettingsWarningsAreErrorsAction("Assembler warnings are considered errors",
                                           null,
              									  "If set, assembler warnings will be interpreted as errors and prevent successful assembly.",
              									  null,null,
              									  mainUI);
           settingsStartAtMainAction       = new SettingsStartAtMainAction("Initialize Program Counter to global 'main' if defined",
                                           null,
              									  "If set, assembler will initialize Program Counter to text address globally labeled 'main', if defined.",
              									  null,null,
              									  mainUI);
           settingsProgramArgumentsAction = new SettingsProgramArgumentsAction("Program arguments provided to MIPS program",
                                           null,
              									  "If set, program arguments for MIPS program can be entered in border of Text Segment window.",
              									  null,null,
              									  mainUI);
           settingsDelayedBranchingAction  = new SettingsDelayedBranchingAction("Delayed branching",
                                           null,
              									  "If set, delayed branching will occur during MIPS execution.",
              									  null,null,
              									  mainUI, observable);
           settingsSelfModifyingCodeAction  = new SettingsSelfModifyingCodeAction("Self-modifying code",
                                           null,
              									  "If set, the MIPS program can write and branch to both text and data segments.",
              									  null,null,
              									  mainUI);
           settingsEditorAction          = new SettingsEditorAction("Editor...",
                                           null,
              									  "View and modify text editor settings.",
              									  null,null,
              									  mainUI);
           settingsHighlightingAction          = new SettingsHighlightingAction("Highlighting...",
                                           null,
              									  "View and modify Execute Tab highlighting colors",
              									  null,null,
              									  mainUI);
           settingsExceptionHandlerAction  = new SettingsExceptionHandlerAction("Exception Handler...",
                                           null,
              									  "If set, the specified exception handler file will be included in all Assemble operations.",
              									  null,null,
              									  mainUI);
           settingsMemoryConfigurationAction  = new SettingsMemoryConfigurationAction("Memory Configuration...",
                                           null,
              									  "View and modify memory segment base addresses for simulated MIPS.",
              									  null,null,
              									  mainUI, observable);
   /*        helpHelpAction = new HelpHelpAction("Help", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Help22.png"))),
              									  "Help", new Integer(KeyEvent.VK_H),
              									  KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
              									  mainUI);	
           helpAboutAction = new HelpAboutAction("About ...",null, 
                                           "Information about Mars", null,null, mainUI);
                                           */	
        } 
            catch (NullPointerException e) {
              System.out.println("Internal Error: images folder not found, or other null pointer exception while creating Action objects");
              e.printStackTrace();
              System.exit(0);
           }
     }

 /*
  * build the menus and connect them to action objects (which serve as action listeners
  * shared between menu item and corresponding toolbar icon).
  */
 
    private JMenuBar setUpMenuBar() {
   
      Toolkit tk = Toolkit.getDefaultToolkit();
      Class cs = this.getClass();
      JMenuBar menuBar = new JMenuBar();
      file=new JMenu("File");
      file.setMnemonic(KeyEvent.VK_F);
      edit = new JMenu("Edit");
      edit.setMnemonic(KeyEvent.VK_E);
      run=new JMenu("Run");
      run.setMnemonic(KeyEvent.VK_R);
      //window = new JMenu("Window");
      //window.setMnemonic(KeyEvent.VK_W);
      settings = new JMenu("Settings");
      settings.setMnemonic(KeyEvent.VK_S);
      help = new JMenu("Help");
      help.setMnemonic(KeyEvent.VK_H); 
   	// slight bug: user typing alt-H activates help menu item directly, not help menu
   
      fileNew = new JMenuItem(fileNewAction);
      fileNew.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"New16.png"))));
      fileOpen = new JMenuItem(fileOpenAction);
      fileOpen.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Open16.png"))));
      fileClose = new JMenuItem(fileCloseAction);
      fileClose.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"MyBlank16.gif"))));
      fileCloseAll = new JMenuItem(fileCloseAllAction);
      fileCloseAll.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"MyBlank16.gif"))));
      fileSave = new JMenuItem(fileSaveAction);
      fileSave.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Save16.png"))));
      fileSaveAs = new JMenuItem(fileSaveAsAction);
      fileSaveAs.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"SaveAs16.png"))));
      fileSaveAll = new JMenuItem(fileSaveAllAction);
      fileSaveAll.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"MyBlank16.gif"))));
    //  fileDumpMemory = new JMenuItem(fileDumpMemoryAction);
     // fileDumpMemory.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Dump16.png"))));
   //   filePrint = new JMenuItem(filePrintAction);
     // filePrint.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Print16.gif"))));
      fileExit = new JMenuItem(fileExitAction);
      fileExit.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"MyBlank16.gif"))));
      file.add(fileNew);
      file.add(fileOpen);
      file.add(fileClose);
      file.add(fileCloseAll);
      file.addSeparator();
      file.add(fileSave);
      file.add(fileSaveAs);
      file.add(fileSaveAll);
   //   if (new mars.mips.dump.DumpFormatLoader().loadDumpFormats().size() > 0) {
    //     file.add(fileDumpMemory);
    //  }
   //   file.addSeparator();
 //     file.add(filePrint);
      file.addSeparator();
      file.add(fileExit);
   	
      editUndo = new JMenuItem(editUndoAction);
      editUndo.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Undo16.png"))));//"Undo16.gif"))));
      editRedo = new JMenuItem(editRedoAction);
      editRedo.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Redo16.png"))));//"Redo16.gif"))));      
      editCut = new JMenuItem(editCutAction);
      editCut.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Cut16.gif"))));
      editCopy = new JMenuItem(editCopyAction);
      editCopy.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Copy16.png"))));//"Copy16.gif"))));
      editPaste = new JMenuItem(editPasteAction);
      editPaste.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Paste16.png"))));//"Paste16.gif"))));
      editFindReplace = new JMenuItem(editFindReplaceAction);
      editFindReplace.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Find16.png"))));//"Paste16.gif"))));
      editSelectAll = new JMenuItem(editSelectAllAction);
      editSelectAll.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"MyBlank16.gif"))));
      edit.add(editUndo);
      edit.add(editRedo);
      edit.addSeparator();
      edit.add(editCut);
      edit.add(editCopy);
      edit.add(editPaste);
      edit.addSeparator();
      edit.add(editFindReplace);
      edit.add(editSelectAll);
   
      runAssemble = new JMenuItem(runAssembleAction);
      runAssemble.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Assemble16.png"))));//"MyAssemble16.gif"))));
      runGo = new JMenuItem(runGoAction);
      runGo.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Play16.png"))));//"Play16.gif"))));
      runStep = new JMenuItem(runStepAction);
      runStep.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"StepForward16.png"))));//"MyStepForward16.gif"))));
      runBackstep = new JMenuItem(runBackstepAction);
      runBackstep.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"StepBack16.png"))));//"MyStepBack16.gif"))));
      runReset = new JMenuItem(runResetAction);
      runReset.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Reset16.png"))));//"MyReset16.gif"))));
      runStop = new JMenuItem(runStopAction);
    //  runStop.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Stop16.png"))));//"Stop16.gif"))));
      runPause = new JMenuItem(runPauseAction);
      runPause.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Pause16.png"))));//"Pause16.gif"))));
      runClearBreakpoints = new JMenuItem(runClearBreakpointsAction);
      runClearBreakpoints.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"MyBlank16.gif"))));
      runToggleBreakpoints = new JMenuItem(runToggleBreakpointsAction);
      runToggleBreakpoints.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"MyBlank16.gif"))));
   
      run.add(runAssemble);
      run.add(runGo);
      run.add(runStep);
      run.add(runBackstep);
      run.add(runPause);
      run.add(runStop);
      run.add(runReset);
      run.addSeparator();
      run.add(runClearBreakpoints);
      run.add(runToggleBreakpoints);
   	
      settingsLabel = new JCheckBoxMenuItem(settingsLabelAction);
      settingsLabel.setSelected(Globals.getSettings().getLabelWindowVisibility());
      settingsPopupInput = new JCheckBoxMenuItem(settingsPopupInputAction);
      settingsPopupInput.setSelected(Globals.getSettings().getBooleanSetting(Settings.POPUP_SYSCALL_INPUT));
      settingsValueDisplayBase = new JCheckBoxMenuItem(settingsValueDisplayBaseAction);
      settingsValueDisplayBase.setSelected(Globals.getSettings().getDisplayValuesInHex());//mainPane.getExecutePane().getValueDisplayBaseChooser().isSelected());
      // Tell the corresponding JCheckBox in the Execute Pane about me -- it has already been created.
     // mainPane.getExecutePane().getValueDisplayBaseChooser().setSettingsMenuItem(settingsValueDisplayBase);
      settingsAddressDisplayBase = new JCheckBoxMenuItem(settingsAddressDisplayBaseAction);
      settingsAddressDisplayBase.setSelected(Globals.getSettings().getDisplayAddressesInHex());//mainPane.getExecutePane().getValueDisplayBaseChooser().isSelected());
      // Tell the corresponding JCheckBox in the Execute Pane about me -- it has already been created.
      mainPane.getExecutePane().getAddressDisplayBaseChooser().setSettingsMenuItem(settingsAddressDisplayBase);
      settingsExtended = new JCheckBoxMenuItem(settingsExtendedAction);
      settingsExtended.setSelected(Globals.getSettings().getExtendedAssemblerEnabled());
      settingsDelayedBranching = new JCheckBoxMenuItem(settingsDelayedBranchingAction);
      settingsDelayedBranching.setSelected(Globals.getSettings().getDelayedBranchingEnabled());
      settingsSelfModifyingCode = new JCheckBoxMenuItem(settingsSelfModifyingCodeAction);
      settingsSelfModifyingCode.setSelected(Globals.getSettings().getBooleanSetting(Settings.SELF_MODIFYING_CODE_ENABLED));
      settingsAssembleOnOpen = new JCheckBoxMenuItem(settingsAssembleOnOpenAction);
      settingsAssembleOnOpen.setSelected(Globals.getSettings().getAssembleOnOpenEnabled());
      settingsAssembleAll = new JCheckBoxMenuItem(settingsAssembleAllAction);
      settingsAssembleAll.setSelected(Globals.getSettings().getAssembleAllEnabled());
      settingsWarningsAreErrors = new JCheckBoxMenuItem(settingsWarningsAreErrorsAction);
      settingsWarningsAreErrors.setSelected(Globals.getSettings().getWarningsAreErrors());
      settingsStartAtMain = new JCheckBoxMenuItem(settingsStartAtMainAction);
      settingsStartAtMain.setSelected(Globals.getSettings().getStartAtMain()); 
      settingsProgramArguments = new JCheckBoxMenuItem(settingsProgramArgumentsAction);
      settingsProgramArguments.setSelected(Globals.getSettings().getProgramArguments());
      settingsEditor = new JMenuItem(settingsEditorAction);
      settingsHighlighting = new JMenuItem(settingsHighlightingAction);
      settingsExceptionHandler = new JMenuItem(settingsExceptionHandlerAction);
      settingsMemoryConfiguration = new JMenuItem(settingsMemoryConfigurationAction);
   	
      settings.add(settingsLabel);
      settings.add(settingsProgramArguments);
      settings.add(settingsPopupInput);
      settings.add(settingsAddressDisplayBase);
      settings.add(settingsValueDisplayBase);
      settings.addSeparator();
      settings.add(settingsAssembleOnOpen);
      settings.add(settingsAssembleAll);
      settings.add(settingsWarningsAreErrors);
      settings.add(settingsStartAtMain);
      settings.addSeparator();
      settings.add(settingsExtended);
      settings.add(settingsDelayedBranching);
      settings.add(settingsSelfModifyingCode);
      settings.addSeparator();
      settings.add(settingsEditor);
      settings.add(settingsHighlighting);
      settings.add(settingsExceptionHandler);
      settings.add(settingsMemoryConfiguration);
   /*			
      helpHelp = new JMenuItem(helpHelpAction);
      helpHelp.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Help16.png"))));//"Help16.gif"))));
      helpAbout = new JMenuItem(helpAboutAction);
      helpAbout.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"MyBlank16.gif"))));
      help.add(helpHelp);
      help.addSeparator();
      help.add(helpAbout);
   */
      menuBar.add(file);
      menuBar.add(edit);
      menuBar.add(run);
      menuBar.add(settings);
 //     JMenu toolMenu = new ToolLoader().buildToolsMenu();
   //   if (toolMenu != null) menuBar.add(toolMenu);
    //  menuBar.add(help);
   	
   
   	
      return menuBar;
   }

 /*
  * build the toolbar and connect items to action objects (which serve as action listeners
  * shared between toolbar icon and corresponding menu item).
  */

    JToolBar setUpToolBar() {
       toolBar = new JToolBar();
      toolBar.setOpaque(false);
      toolBar.setBackground(new java.awt.Color(229, 229, 229));
      toolBar.setBorder(null);
      toolBar.setRollover(true);
      toolBar.setBorderPainted(false);
      New = new ToolBarComponent(fileNewAction);
      New.setText("");
      //New.setOpaque(false);
      New.setIconTextGap(0);
     
      Open = new ToolBarComponent(fileOpenAction);
      Open.setText("");
      Save = new ToolBarComponent(fileSaveAction);
      Save.setText("");
      SaveAs = new ToolBarComponent(fileSaveAsAction);
      SaveAs.setText("");
    //  DumpMemory = new ToolBarComponent(fileDumpMemoryAction);
    //  DumpMemory.setText("");
  //    Print= new ToolBarComponent(filePrintAction);
     // Print.setText("");
   
      Undo = new ToolBarComponent(editUndoAction);
      Undo.setText(""); 
      
      Redo = new ToolBarComponent(editRedoAction);
      Redo.setText("");
      
      Cut= new ToolBarComponent(editCutAction);
      Cut.setText("");
      
      Copy = new ToolBarComponent(editCopyAction);
      Copy.setText("");
      
      Paste= new ToolBarComponent(editPasteAction);
      Paste.setText("");
      
      FindReplace = new ToolBarComponent(editFindReplaceAction);
      FindReplace.setText("");
      
      SelectAll = new JButton(editSelectAllAction);
      SelectAll.setText("");
   	
      Run = new ToolBarComponent(runGoAction);
      Run.setText("");
      
      Assemble = new ToolBarComponent(runAssembleAction);
      Assemble.setText("");
      
      Step = new ToolBarComponent(runStepAction);
      Step.setText(""); 
      
      Backstep = new ToolBarComponent(runBackstepAction);
      Backstep.setText("");
      
      Reset = new ToolBarComponent(runResetAction);
      Reset.setText(""); 
      
      Stop = new ToolBarComponent(runStopAction);
      Stop.setText("");
      
      Pause = new ToolBarComponent(runPauseAction);
      Pause.setText("");   
      
     // Help= new JButton(helpHelpAction);
    //  Help.setText("");
      
      toolBar.add(New);
      toolBar.add(Open);
      toolBar.add(Save);
      toolBar.add(SaveAs);
  //    if (new mars.mips.dump.DumpFormatLoader().loadDumpFormats().size() > 0) {
   //      toolBar.add(DumpMemory);
   //   }
     // toolBar.add(Print);
    //  toolBar.add(new JToolBar.Separator());
      toolBar.add(Undo);
      toolBar.add(Redo);
      toolBar.add(Cut);
      toolBar.add(Copy);
      toolBar.add(Paste);
      toolBar.add(FindReplace);
      toolBar.add(new JToolBar.Separator());
      toolBar.add(Assemble);
      toolBar.add(Run);   
      toolBar.add(Step);
      toolBar.add(Backstep);
      toolBar.add(Pause);
      toolBar.add(Stop);
      toolBar.add(Reset);
    //  toolBar.add(new JToolBar.Separator());
     // toolBar.add(Help);
    //  toolBar.add(new JToolBar.Separator());
   	
      return toolBar;
   }
   
	
 /* Determine from FileStatus what the menu state (enabled/disabled)should 
  * be then call the appropriate method to set it.  Current states are:
  *
  * setMenuStateInitial: set upon startup and after File->Close
  * setMenuStateEditingNew: set upon File->New
  * setMenuStateEditing: set upon File->Open or File->Save or erroneous Run->Assemble
  * setMenuStateRunnable: set upon successful Run->Assemble
  * setMenuStateRunning: set upon Run->Go
  * setMenuStateTerminated: set upon completion of simulated execution
  */
   /* void setMenuState(int status) {
      menuState = status; 
      switch (status) {
         case FileStatus.NO_FILE:
            setMenuStateInitial();
            break;
         case FileStatus.NEW_NOT_EDITED:
            setMenuStateEditingNew();
            break;
         case FileStatus.NEW_EDITED:
            setMenuStateEditingNew();
            break;
         case FileStatus.NOT_EDITED:
            setMenuStateNotEdited(); // was MenuStateEditing. DPS 9-Aug-2011
            break;
         case FileStatus.EDITED:
            setMenuStateEditing();
            break;
         case FileStatus.RUNNABLE:
            setMenuStateRunnable();
            break;
         case FileStatus.RUNNING:
            setMenuStateRunning();
            break;
         case FileStatus.TERMINATED:
            setMenuStateTerminated();
            break;
         case FileStatus.OPENING:// This is a temporary state. DPS 9-Aug-2011
            break;
         default:
            System.out.println("Invalid File Status: "+status);
            break;
      }
   }
  
  
    void setMenuStateInitial() {
      fileNewAction.setEnabled(true);
      fileOpenAction.setEnabled(true);
      fileCloseAction.setEnabled(false);
      fileCloseAllAction.setEnabled(false);
      fileSaveAction.setEnabled(false);
      fileSaveAsAction.setEnabled(false);
      fileSaveAllAction.setEnabled(false);
   //   fileDumpMemoryAction.setEnabled(false);
    //  filePrintAction.setEnabled(false);
      fileExitAction.setEnabled(true);
      editUndoAction.setEnabled(false);
      editRedoAction.setEnabled(false);
      editCutAction.setEnabled(false);
      editCopyAction.setEnabled(false);
      editPasteAction.setEnabled(false);
      editFindReplaceAction.setEnabled(false);
      editSelectAllAction.setEnabled(false);
      settingsDelayedBranchingAction.setEnabled(true); // added 25 June 2007
      settingsMemoryConfigurationAction.setEnabled(true); // added 21 July 2009
      runAssembleAction.setEnabled(false);
      runGoAction.setEnabled(false);
      runStepAction.setEnabled(false);
      runBackstepAction.setEnabled(false);
      runResetAction.setEnabled(false);
      runStopAction.setEnabled(false);
      runPauseAction.setEnabled(false);
      runClearBreakpointsAction.setEnabled(false);
      runToggleBreakpointsAction.setEnabled(false);
//      helpHelpAction.setEnabled(true);
 //     helpAboutAction.setEnabled(true);
      editUndoAction.updateUndoState();
      editRedoAction.updateRedoState();
   }

   /* Added DPS 9-Aug-2011, for newly-opened files.  Retain
	   existing Run menu state (except Assemble, which is always true).
		Thus if there was a valid assembly it is retained. */
   // void setMenuStateNotEdited() {
   /* Note: undo and redo are handled separately by the undo manager*/  
    /*  fileNewAction.setEnabled(true);
      fileOpenAction.setEnabled(true);
      fileCloseAction.setEnabled(true);
      fileCloseAllAction.setEnabled(true);
      fileSaveAction.setEnabled(true);
      fileSaveAsAction.setEnabled(true);
      fileSaveAllAction.setEnabled(true);
     // fileDumpMemoryAction.setEnabled(false);
     // filePrintAction.setEnabled(true);
      fileExitAction.setEnabled(true);
      editCutAction.setEnabled(true);
      editCopyAction.setEnabled(true);
      editPasteAction.setEnabled(true);
      editFindReplaceAction.setEnabled(true);
      editSelectAllAction.setEnabled(true);
      settingsDelayedBranchingAction.setEnabled(true); 
      settingsMemoryConfigurationAction.setEnabled(true);
      runAssembleAction.setEnabled(true);
			// If assemble-all, allow previous Run menu settings to remain.
			// Otherwise, clear them out.  DPS 9-Aug-2011
      if (!Globals.getSettings().getBooleanSetting(mars.Settings.ASSEMBLE_ALL_ENABLED)) {
         runGoAction.setEnabled(false);
         runStepAction.setEnabled(false);
         runBackstepAction.setEnabled(false);
         runResetAction.setEnabled(false);
         runStopAction.setEnabled(false);
         runPauseAction.setEnabled(false);
         runClearBreakpointsAction.setEnabled(false);
         runToggleBreakpointsAction.setEnabled(false);
      } 
    //  helpHelpAction.setEnabled(true);
    //  helpAboutAction.setEnabled(true);
      editUndoAction.updateUndoState();
      editRedoAction.updateRedoState();
   }




    void setMenuStateEditing() {
   /* Note: undo and redo are handled separately by the undo manager*/  
 /*     fileNewAction.setEnabled(true);
      fileOpenAction.setEnabled(true);
      fileCloseAction.setEnabled(true);
      fileCloseAllAction.setEnabled(true);
      fileSaveAction.setEnabled(true);
      fileSaveAsAction.setEnabled(true);
      fileSaveAllAction.setEnabled(true);
   //   fileDumpMemoryAction.setEnabled(false);
     // filePrintAction.setEnabled(true);
      fileExitAction.setEnabled(true);
      editCutAction.setEnabled(true);
      editCopyAction.setEnabled(true);
      editPasteAction.setEnabled(true);
      editFindReplaceAction.setEnabled(true);
      editSelectAllAction.setEnabled(true);
      settingsDelayedBranchingAction.setEnabled(true); // added 25 June 2007
      settingsMemoryConfigurationAction.setEnabled(true); // added 21 July 2009
      runAssembleAction.setEnabled(true);
      runGoAction.setEnabled(false);
      runStepAction.setEnabled(false);
      runBackstepAction.setEnabled(false);
      runResetAction.setEnabled(false);
      runStopAction.setEnabled(false);
      runPauseAction.setEnabled(false);
      runClearBreakpointsAction.setEnabled(false);
      runToggleBreakpointsAction.setEnabled(false);
    //  helpHelpAction.setEnabled(true);
    //  helpAboutAction.setEnabled(true);
      editUndoAction.updateUndoState();
      editRedoAction.updateRedoState();
   }

  /* Use this when "File -> New" is used
   */
  /*  void setMenuStateEditingNew() {
   /* Note: undo and redo are handled separately by the undo manager*/  
    /*  fileNewAction.setEnabled(true);
      fileOpenAction.setEnabled(true);
      fileCloseAction.setEnabled(true);
      fileCloseAllAction.setEnabled(true);
      fileSaveAction.setEnabled(true);
      fileSaveAsAction.setEnabled(true);
      fileSaveAllAction.setEnabled(true);
   //   fileDumpMemoryAction.setEnabled(false);
  //    filePrintAction.setEnabled(true);
      fileExitAction.setEnabled(true);
      editCutAction.setEnabled(true);
      editCopyAction.setEnabled(true);
      editPasteAction.setEnabled(true);
      editFindReplaceAction.setEnabled(true);
      editSelectAllAction.setEnabled(true);
      settingsDelayedBranchingAction.setEnabled(true); // added 25 June 2007
      settingsMemoryConfigurationAction.setEnabled(true); // added 21 July 2009
      runAssembleAction.setEnabled(false);
      runGoAction.setEnabled(false);
      runStepAction.setEnabled(false);
      runBackstepAction.setEnabled(false);
      runResetAction.setEnabled(false);
      runStopAction.setEnabled(false);
      runPauseAction.setEnabled(false);
      runClearBreakpointsAction.setEnabled(false);
      runToggleBreakpointsAction.setEnabled(false);
 //     helpHelpAction.setEnabled(true);
 //     helpAboutAction.setEnabled(true);
      editUndoAction.updateUndoState();
      editRedoAction.updateRedoState();
   }
 	 
  /* Use this upon successful assemble or reset
   */
  /*  void setMenuStateRunnable() {
   /* Note: undo and redo are handled separately by the undo manager */  
    /*  fileNewAction.setEnabled(true);
      fileOpenAction.setEnabled(true);
      fileCloseAction.setEnabled(true);
      fileCloseAllAction.setEnabled(true);
      fileSaveAction.setEnabled(true);
      fileSaveAsAction.setEnabled(true);
      fileSaveAllAction.setEnabled(true);
  //    fileDumpMemoryAction.setEnabled(true);
    //  filePrintAction.setEnabled(true);
      fileExitAction.setEnabled(true);
      editCutAction.setEnabled(true);
      editCopyAction.setEnabled(true);
      editPasteAction.setEnabled(true);
      editFindReplaceAction.setEnabled(true);
      editSelectAllAction.setEnabled(true);
      settingsDelayedBranchingAction.setEnabled(true); // added 25 June 2007
      settingsMemoryConfigurationAction.setEnabled(true); // added 21 July 2009
      runAssembleAction.setEnabled(true);
      runGoAction.setEnabled(true);
      runStepAction.setEnabled(true);
      runBackstepAction.setEnabled(
         (Globals.getSettings().getBackSteppingEnabled()&& !Globals.program.getBackStepper().empty())
          ? true : false);
      runResetAction.setEnabled(true);
      runStopAction.setEnabled(false);
      runPauseAction.setEnabled(false);
      runToggleBreakpointsAction.setEnabled(true);
  //    helpHelpAction.setEnabled(true);
  //    helpAboutAction.setEnabled(true);
      editUndoAction.updateUndoState();
      editRedoAction.updateRedoState();
   }

  /* Use this while program is running
   */
   /* void setMenuStateRunning() {
   /* Note: undo and redo are handled separately by the undo manager */  
   /*   fileNewAction.setEnabled(false);
      fileOpenAction.setEnabled(false);
      fileCloseAction.setEnabled(false);
      fileCloseAllAction.setEnabled(false);
      fileSaveAction.setEnabled(false);
      fileSaveAsAction.setEnabled(false);
      fileSaveAllAction.setEnabled(false);
  //    fileDumpMemoryAction.setEnabled(false);
  //    filePrintAction.setEnabled(false);
      fileExitAction.setEnabled(false);
      editCutAction.setEnabled(false);
      editCopyAction.setEnabled(false);
      editPasteAction.setEnabled(false);
      editFindReplaceAction.setEnabled(false);
      editSelectAllAction.setEnabled(false);
      settingsDelayedBranchingAction.setEnabled(false); // added 25 June 2007
      settingsMemoryConfigurationAction.setEnabled(false); // added 21 July 2009
      runAssembleAction.setEnabled(false);
      runGoAction.setEnabled(false);
      runStepAction.setEnabled(false);
      runBackstepAction.setEnabled(false);
      runResetAction.setEnabled(false);
      runStopAction.setEnabled(true);
      runPauseAction.setEnabled(true);
      runToggleBreakpointsAction.setEnabled(false);
  //    helpHelpAction.setEnabled(true);
   //   helpAboutAction.setEnabled(true);
      editUndoAction.setEnabled(false);//updateUndoState(); // DPS 10 Jan 2008
      editRedoAction.setEnabled(false);//updateRedoState(); // DPS 10 Jan 2008
   }   
  /* Use this upon completion of execution
   */
  /*  void setMenuStateTerminated() {
   /* Note: undo and redo are handled separately by the undo manager */  
   /*   fileNewAction.setEnabled(true);
      fileOpenAction.setEnabled(true);
      fileCloseAction.setEnabled(true);
      fileCloseAllAction.setEnabled(true);
      fileSaveAction.setEnabled(true);
      fileSaveAsAction.setEnabled(true);
      fileSaveAllAction.setEnabled(true);
  //    fileDumpMemoryAction.setEnabled(true);
  //    filePrintAction.setEnabled(true);
      fileExitAction.setEnabled(true);
      editCutAction.setEnabled(true);
      editCopyAction.setEnabled(true);
      editPasteAction.setEnabled(true);
      editFindReplaceAction.setEnabled(true);
      editSelectAllAction.setEnabled(true);
      settingsDelayedBranchingAction.setEnabled(true); // added 25 June 2007
      settingsMemoryConfigurationAction.setEnabled(true); // added 21 July 2009
      runAssembleAction.setEnabled(true);
      runGoAction.setEnabled(false);
      runStepAction.setEnabled(false);
      runBackstepAction.setEnabled(
         (Globals.getSettings().getBackSteppingEnabled()&& !Globals.program.getBackStepper().empty())
          ? true : false);
      runResetAction.setEnabled(true);
      runStopAction.setEnabled(false);
      runPauseAction.setEnabled(false);
      runToggleBreakpointsAction.setEnabled(true);
    //  helpHelpAction.setEnabled(true);
    //  helpAboutAction.setEnabled(true);
      editUndoAction.updateUndoState();
      editRedoAction.updateRedoState();
   }

 */
 /**
  * Get current menu state.  State values are constants in FileStatus class.  DPS 23 July 2008
  * @return current menu state.
  **/
  
    public static int getMenuState() {
      return menuState;
   }
   
	/**
	  *  To set whether the register values are reset.
	  *   @param b Boolean true if the register values have been reset.
	  **/
	
    public  void setReset(boolean b){
      reset=b;
   }

	/**
	  *  To set whether MIPS program execution has started.
	  *   @param b true if the MIPS program execution has started.
	  **/
	
    public void setStarted(boolean b){ 
      started=b;
   }
   /**
	  *  To find out whether the register values are reset.
	  *   @return Boolean true if the register values have been reset.
	  **/
   
    public boolean getReset(){
      return reset;
   }
	
   /**
	  *  To find out whether MIPS program is currently executing.
	  *   @return  true if MIPS program is currently executing.
	  **/
    public boolean getStarted(){
      return started;
   }
	
   /**
	  *  Get reference to Editor object associated with this GUI.
	  *   @return Editor for the GUI.
	  **/
      	
    public Editor getEditor() {
      return editor;
   }		
	
   /**
	  *  Get reference to messages pane associated with this GUI.
	  *   @return MessagesPane object associated with the GUI.
	  **/
      	
    public MainPane getMainPane() {
      return mainPane;
   }      /**
	  *  Get reference to messages pane associated with this GUI.
	  *   @return MessagesPane object associated with the GUI.
	  **/
      	
    public MessagesPane getMessagesPane() {
      return messagesPane;
   }

   /**
	  *  Get reference to registers pane associated with this GUI.
	  *   @return RegistersPane object associated with the GUI.
	  **/
      	
    public RegistersPane getRegistersPane() {
      return registersPane;
   }   	

   /**
	  *  Get reference to settings menu item for display base of memory/register values.
	  *   @return the menu item
	  **/
      	
    public JCheckBoxMenuItem getValueDisplayBaseMenuItem() {
      return settingsValueDisplayBase;
   }   	     

   /**
	  *  Get reference to settings menu item for display base of memory/register values.
	  *   @return the menu item
	  **/
      	
    public JCheckBoxMenuItem getAddressDisplayBaseMenuItem() {
      return settingsAddressDisplayBase;
   }   	          
	
	/**
	 * Return reference tothe Run->Assemble item's action.  Needed by File->Open in case
	 * assemble-upon-open flag is set.
	 * @return the Action object for the Run->Assemble operation.
	 */
    public Action getRunAssembleAction() {
      return runAssembleAction;
   }
	
	/**
	 * Have the menu request keyboard focus.  DPS 5-4-10
	 */
    public void haveMenuRequestFocus() {
      this.menu.requestFocus();
   }
	
	/**
	 * Send keyboard event to menu for possible processing.  DPS 5-4-10
	 * @param evt KeyEvent for menu component to consider for processing.
	 */
    public void dispatchEventToMenu(KeyEvent evt) {
      this.menu.dispatchEvent(evt);
   }
  
  // pop up menu experiment 3 Aug 2006.  Keep for possible later revival.
    private void setupPopupMenu() {
      JPopupMenu popup; 
      popup = new JPopupMenu();
   	// cannot put the same menu item object on two different menus.
   	// If you want to duplicate functionality, need a different item.
   	// Should be able to share listeners, but if both menu items are
   	// JCheckBoxMenuItem, how to keep their checked status in synch?
   	// If you popup this menu and check the box, the right action occurs
   	// but its counterpart on the regular menu is not checked.
      popup.add(new JCheckBoxMenuItem(settingsLabelAction)); 
   //Add listener to components that can bring up popup menus. 
      MouseListener popupListener = new PopupListener(popup); 
      this.addMouseListener(popupListener); 
   }
  


}
