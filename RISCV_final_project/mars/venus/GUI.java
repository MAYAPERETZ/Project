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
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

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
    MessagesPane messagesPane;
    JPanel north;

    private static int menuState = FileStatus.NO_FILE;

    // PLEASE PUT THESE TWO (& THEIR METHODS) SOMEWHERE THEY BELONG, NOT HERE
    private static boolean reset= true; // registers/memory reset for execution
    private static boolean started = false;  // started execution
    Editor editor;

    private JMenu window;
    private JCheckBoxMenuItem settingsValueDisplayBase;
    private JCheckBoxMenuItem settingsAddressDisplayBase;
    private ToolBarComponent SaveAll;
    private ToolBarComponent DumpMemory;
    private ToolBarComponent Print;

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
    NewObservable observable;

        /**
    *  Constructor for the Class. Sets up a window object for the UI
    *   @param s Name of the window to be created.
    **/
    public GUI(String s) {
        super(s);
        mainUI = this;
        Globals.setGui(this);
        this.editor = new Editor(this);
        observable = new NewObservable();

        double screenWidth  = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        // basically give up some screen space if running at 800 x 600
        double messageWidthPct = (screenWidth<1000.0)? 0.67 : 0.73;
        double messageHeightPct = (screenWidth<1000.0)? 0.12 : 0.15;
        double mainWidthPct = (screenWidth<1000.0)? 0.67 : 0.73;
        double mainHeightPct = (screenWidth<1000.0)? 0.60 : 0.65;
        double registersWidthPct = (screenWidth<1000.0)? 0.18 : 0.22;
        double registersHeightPct = (screenWidth<1000.0)? 0.72 : 0.80;

        Dimension messagesPanePreferredSize = new Dimension((int)(screenWidth*messageWidthPct),(int)(screenHeight*messageHeightPct));
        Dimension mainPanePreferredSize = new Dimension((int)(screenWidth*mainWidthPct),(int)(screenHeight*mainHeightPct));
        Dimension registersPanePreferredSize = new Dimension((int)(screenWidth*registersWidthPct),(int)(screenHeight*registersHeightPct));

        // the "restore" size (window control button that toggles with maximize)
        // I want to keep it large, with enough room for user to get handles
        //this.setSize((int)(screenWidth*.8),(int)(screenHeight*.8));

        Globals.initialize(true);

        //  image courtesy of NASA/JPL.
        URL im = this.getClass().getResource(Globals.imagesPath+"RedMars16.gif");
        if (im == null) {
            System.out.println("Internal Error: images folder or file not found");
            System.exit(0);
        }
        Image mars = Toolkit.getDefaultToolkit().getImage(im);
        this.setIconImage(mars);
        // Everything in frame will be arranged on JPanel "center", which is only frame component.
        // "center" has BorderLayout and 2 major components:
        //   -- panel (jp) on North with 2 components
        //      1. toolbar
        //      2. run speed slider.
        //   -- split pane (horizonSplitter) in center with 2 components side-by-side
        //      1. split pane (splitter) with 2 components stacked
        //         a. main pane, with 2 tabs (edit, execute)
        //         b. messages pane with 2 tabs (mars, run I/O)
        //      2. registers pane with 3 tabs (register file, coproc 0, coproc 1)
        // I should probably run this breakdown out to full detail.  The components are created
        // roughly in bottom-up order; some are created in component constructors and thus are
        // not visible here.

        RegistersWindow registersTab = new RegistersWindow();
        Coprocessor1Window coprocessor1Tab = new Coprocessor1Window();
        Coprocessor0Window coprocessor0Tab = new Coprocessor0Window();
        registersPane = new RegistersPane(registersTab, coprocessor1Tab, coprocessor0Tab);
        registersPane.setPreferredSize(registersPanePreferredSize);

        mainPane = new MainPane(mainUI, editor, registersTab, coprocessor1Tab, coprocessor0Tab);
        mainPane.setPreferredSize(mainPanePreferredSize);
        messagesPane= new MessagesPane();
        messagesPane.setPreferredSize(messagesPanePreferredSize);
        JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainPane, messagesPane);
        splitter.setOneTouchExpandable(true);
        splitter.resetToPreferredSizes();
        JSplitPane horizonSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitter, registersPane);
        horizonSplitter.setOneTouchExpandable(true);
        horizonSplitter.resetToPreferredSizes();

        // due to dependencies, do not set up menu/toolbar until now.
        this.createActionObjects();
        menu= this.setUpMenuBar();
        this.setJMenuBar(menu);

        toolbar= this.setUpToolBar();

        JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jp.add(toolbar);
        JPanel center= new JPanel(new BorderLayout());
        center.add(jp, BorderLayout.NORTH);
        center.add(horizonSplitter);
        this.getContentPane().add(center);

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
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.pack();
        this.setVisible(true);
      
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
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "new_file.png"))),
                                           "Create a new file for editing", KeyEvent.VK_N,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);		
           fileOpenAction = new FileOpenAction("Open ...", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "open_document.png"))),
              									  "Open a file for editing", KeyEvent.VK_O,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);
              									  
           fileCloseAction = new FileCloseAction("Close", null,
                                           "Close the current file", KeyEvent.VK_C,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);						
           fileCloseAllAction = new FileCloseAllAction("Close All", null,
                                           "Close all open files", KeyEvent.VK_L,
              									  null, mainUI, observable);	
           fileSaveAction = new FileSaveAction("Save", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "save.png"))),
              									  "Save the current file", KeyEvent.VK_S,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);
           
           fileSaveAsAction = new FileSaveAsAction("Save as ...", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "save_as.png"))),
              									  "Save current file with different name", KeyEvent.VK_A,
              									  null, mainUI, observable);	
           fileSaveAllAction = new FileSaveAllAction("Save All", null,
                                           "Save all open files", KeyEvent.VK_V,
              									  null, mainUI, observable);	
           fileDumpMemoryAction = new FileDumpMemoryAction("Dump Memory ...",
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Dump22.png"))),
              									  "Dump machine code or data in an available format", KeyEvent.VK_D,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
             									  mainUI);
              									  	
           filePrintAction = new FilePrintAction("Print ...",
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Print22.gif"))),
              									  "Print current file", KeyEvent.VK_P,
              									  null, mainUI);

           fileExitAction = new FileExitAction("Exit", null,
              	                         "Exit", KeyEvent.VK_X,
              									  null, mainUI, observable);	
           editUndoAction = new EditUndoAction("Undo", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "undo.png"))),
              									  "Undo last edit", KeyEvent.VK_U,
                                           KeyStroke.getKeyStroke( KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);	
           editRedoAction = new EditRedoAction("Redo", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "redo.png"))),
              									  "Redo last edit", KeyEvent.VK_R,
                                           KeyStroke.getKeyStroke( KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);			
           editCutAction = new EditCutAction("Cut", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "cut.png"))),
              									  "Cut", KeyEvent.VK_C,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);	
           editCopyAction = new EditCopyAction("Copy", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "copy.png"))),
              									  "Copy", KeyEvent.VK_O,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);	
           editPasteAction = new EditPasteAction("Paste", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "paste.png"))),
              									  "Paste", KeyEvent.VK_P,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);	
           editFindReplaceAction = new EditFindReplaceAction("Find/Replace", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "find_and_replace.png"))),
              									  "Find/Replace", KeyEvent.VK_F,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);
           editSelectAllAction = new EditSelectAllAction("Select All", 
                                           null, //new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Find22.png"))),
              									  "Select All", KeyEvent.VK_A,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);
           runAssembleAction = new RunAssembleAction("Assemble",  
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "assemble.png"))),
              									  "Assemble the current file and clear breakpoints", KeyEvent.VK_A,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F3, 0), 
              									  mainUI, observable);			
           runGoAction = new RunGoAction("Go", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "play_button.png"))),
              									  "Run the current program", KeyEvent.VK_G,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F5, 0),
              									  mainUI, observable);	
           runStepAction = new RunStepAction("Step", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "resume_button.png"))),
              									  "Run one step at a time", KeyEvent.VK_T,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F7, 0),
              									  mainUI, observable);	
           runBackstepAction = new RunBackstepAction("Backstep", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "previous.png"))),
              									  "Undo the last step", KeyEvent.VK_B,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F8, 0), 
              									  mainUI, observable);	
           runPauseAction = new RunPauseAction("Pause", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "circled_pause.png"))),
              									  "Pause the currently running program", KeyEvent.VK_P,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F9, 0), 
              									  mainUI, observable);	
           runStopAction = new RunStopAction("Stop", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "stop_circled.png"))),
              									  "Stop the currently running program", KeyEvent.VK_S,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F11, 0), 
              									  mainUI, observable);
           runResetAction = new RunResetAction("Reset", 
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath + "rewind_button.png"))),
              									  "Reset MIPS memory and registers", KeyEvent.VK_R,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_F12,0),
              									  mainUI, observable);	
           runClearBreakpointsAction = new RunClearBreakpointsAction("Clear all breakpoints",
                                           null,
              									  "Clears all execution breakpoints set since the last assemble.", KeyEvent.VK_K,
              									  KeyStroke.getKeyStroke( KeyEvent.VK_K, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
              									  mainUI, observable);  
           runToggleBreakpointsAction = new RunToggleBreakpointsAction("Toggle all breakpoints",
                                           null,
              									  "Disable/enable all breakpoints without clearing (can also click Bkpt column header)", KeyEvent.VK_T,
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
           helpHelpAction = new HelpHelpAction("Help",
                                           new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"help.png"))),
              									  "Help", KeyEvent.VK_H,
              									  KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
              									  mainUI);	
           helpAboutAction = new HelpAboutAction("About ...",null, 
                                           "Information about Mars", null,null, mainUI);

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
        // components of the menubar
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        JMenu edit = new JMenu("Edit");
        edit.setMnemonic(KeyEvent.VK_E);
        JMenu run = new JMenu("Run");
        run.setMnemonic(KeyEvent.VK_R);
        //window = new JMenu("Window");
        //window.setMnemonic(KeyEvent.VK_W);
        JMenu settings = new JMenu("Settings");
        settings.setMnemonic(KeyEvent.VK_S);
        JMenu help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        // slight bug: user typing alt-H activates help menu item directly, not help menu

        JMenuItem fileNew = new JMenuItem(fileNewAction);
        fileNew.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"New16.png"))));
        JMenuItem fileOpen = new JMenuItem(fileOpenAction);
        fileOpen.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Open16.png"))));
        JMenuItem fileClose = new JMenuItem(fileCloseAction);
        fileClose.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"MyBlank16.gif"))));
        JMenuItem fileCloseAll = new JMenuItem(fileCloseAllAction);
        fileCloseAll.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"MyBlank16.gif"))));
        JMenuItem fileSave = new JMenuItem(fileSaveAction);
        fileSave.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Save16.png"))));
        JMenuItem fileSaveAs = new JMenuItem(fileSaveAsAction);
        fileSaveAs.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"SaveAs16.png"))));
        JMenuItem fileSaveAll = new JMenuItem(fileSaveAllAction);
        fileSaveAll.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"MyBlank16.gif"))));
        JMenuItem fileDumpMemory = new JMenuItem(fileDumpMemoryAction);
        fileDumpMemory.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Dump16.png"))));
        JMenuItem filePrint = new JMenuItem(filePrintAction);
        filePrint.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Print16.gif"))));
        JMenuItem fileExit = new JMenuItem(fileExitAction);
        fileExit.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"MyBlank16.gif"))));
        file.add(fileNew);
        file.add(fileOpen);
        file.add(fileClose);
        file.add(fileCloseAll);
        file.addSeparator();
        file.add(fileSave);
        file.add(fileSaveAs);
        file.add(fileSaveAll);
        if (new mars.riscv.dump.DumpFormatLoader().loadDumpFormats().size() > 0)
            file.add(fileDumpMemory);
        file.addSeparator();
        file.add(filePrint);
        file.addSeparator();
        file.add(fileExit);

        JMenuItem editUndo = new JMenuItem(editUndoAction);
        editUndo.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Undo16.png"))));//"Undo16.gif"))));
        JMenuItem editRedo = new JMenuItem(editRedoAction);
        editRedo.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Redo16.png"))));//"Redo16.gif"))));
        JMenuItem editCut = new JMenuItem(editCutAction);
        editCut.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Cut16.gif"))));
        JMenuItem editCopy = new JMenuItem(editCopyAction);
        editCopy.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Copy16.png"))));//"Copy16.gif"))));
        JMenuItem editPaste = new JMenuItem(editPasteAction);
        editPaste.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Paste16.png"))));//"Paste16.gif"))));
        JMenuItem editFindReplace = new JMenuItem(editFindReplaceAction);
        editFindReplace.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Find16.png"))));//"Paste16.gif"))));
        JMenuItem editSelectAll = new JMenuItem(editSelectAllAction);
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

        JMenuItem runAssemble = new JMenuItem(runAssembleAction);
        runAssemble.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Assemble16.png"))));//"MyAssemble16.gif"))));
        JMenuItem runGo = new JMenuItem(runGoAction);
        runGo.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Play16.png"))));//"Play16.gif"))));
        JMenuItem runStep = new JMenuItem(runStepAction);
        runStep.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"StepForward16.png"))));//"MyStepForward16.gif"))));
        JMenuItem runBackstep = new JMenuItem(runBackstepAction);
        runBackstep.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"StepBack16.png"))));//"MyStepBack16.gif"))));
        JMenuItem runReset = new JMenuItem(runResetAction);
        runReset.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Reset16.png"))));//"MyReset16.gif"))));
        JMenuItem runStop = new JMenuItem(runStopAction);
        //  runStop.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Stop16.png"))));//"Stop16.gif"))));
        JMenuItem runPause = new JMenuItem(runPauseAction);
        runPause.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Pause16.png"))));//"Pause16.gif"))));
        JMenuItem runClearBreakpoints = new JMenuItem(runClearBreakpointsAction);
        runClearBreakpoints.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"MyBlank16.gif"))));
        JMenuItem runToggleBreakpoints = new JMenuItem(runToggleBreakpointsAction);
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

        JCheckBoxMenuItem settingsLabel = new JCheckBoxMenuItem(settingsLabelAction);
        settingsLabel.setSelected(Globals.getSettings().getLabelWindowVisibility());
        JCheckBoxMenuItem settingsPopupInput = new JCheckBoxMenuItem(settingsPopupInputAction);
        settingsPopupInput.setSelected(Globals.getSettings().getBooleanSetting(Settings.POPUP_SYSCALL_INPUT));
        settingsValueDisplayBase = new JCheckBoxMenuItem(settingsValueDisplayBaseAction);
        settingsValueDisplayBase.setSelected(Globals.getSettings().getDisplayValuesInHex());//mainPane.getExecutePane().getValueDisplayBaseChooser().isSelected());
        // Tell the corresponding JCheckBox in the Execute Pane about me -- it has already been created.
        // mainPane.getExecutePane().getValueDisplayBaseChooser().setSettingsMenuItem(settingsValueDisplayBase);
        settingsAddressDisplayBase = new JCheckBoxMenuItem(settingsAddressDisplayBaseAction);
        settingsAddressDisplayBase.setSelected(Globals.getSettings().getDisplayAddressesInHex());//mainPane.getExecutePane().getValueDisplayBaseChooser().isSelected());
        // Tell the corresponding JCheckBox in the Execute Pane about me -- it has already been created.
        mainPane.getExecutePane().getAddressDisplayBaseChooser().setSettingsMenuItem();
        JCheckBoxMenuItem settingsExtended = new JCheckBoxMenuItem(settingsExtendedAction);
        settingsExtended.setSelected(Globals.getSettings().getExtendedAssemblerEnabled());
        JCheckBoxMenuItem settingsDelayedBranching = new JCheckBoxMenuItem(settingsDelayedBranchingAction);
        settingsDelayedBranching.setSelected(Globals.getSettings().getDelayedBranchingEnabled());
        JCheckBoxMenuItem settingsSelfModifyingCode = new JCheckBoxMenuItem(settingsSelfModifyingCodeAction);
        settingsSelfModifyingCode.setSelected(Globals.getSettings().getBooleanSetting(Settings.SELF_MODIFYING_CODE_ENABLED));
        JCheckBoxMenuItem settingsAssembleOnOpen = new JCheckBoxMenuItem(settingsAssembleOnOpenAction);
        settingsAssembleOnOpen.setSelected(Globals.getSettings().getAssembleOnOpenEnabled());
        JCheckBoxMenuItem settingsAssembleAll = new JCheckBoxMenuItem(settingsAssembleAllAction);
        settingsAssembleAll.setSelected(Globals.getSettings().getAssembleAllEnabled());
        JCheckBoxMenuItem settingsWarningsAreErrors = new JCheckBoxMenuItem(settingsWarningsAreErrorsAction);
        settingsWarningsAreErrors.setSelected(Globals.getSettings().getWarningsAreErrors());
        JCheckBoxMenuItem settingsStartAtMain = new JCheckBoxMenuItem(settingsStartAtMainAction);
        settingsStartAtMain.setSelected(Globals.getSettings().getStartAtMain());
        JCheckBoxMenuItem settingsProgramArguments = new JCheckBoxMenuItem(settingsProgramArgumentsAction);
        settingsProgramArguments.setSelected(Globals.getSettings().getProgramArguments());
        JMenuItem settingsEditor = new JMenuItem(settingsEditorAction);
        JMenuItem settingsHighlighting = new JMenuItem(settingsHighlightingAction);
        JMenuItem settingsExceptionHandler = new JMenuItem(settingsExceptionHandlerAction);
        JMenuItem settingsMemoryConfiguration = new JMenuItem(settingsMemoryConfigurationAction);

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

        JMenuItem helpHelp = new JMenuItem(helpHelpAction);
        helpHelp.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Help16.png"))));//"Help16.gif"))));
        JMenuItem helpAbout = new JMenuItem(helpAboutAction);
        helpAbout.setIcon(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"MyBlank16.gif"))));
        help.add(helpHelp);
        help.addSeparator();
        help.add(helpAbout);

        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(run);
        menuBar.add(settings);
        menuBar.add(help);

        return menuBar;
    }

 /*
  * build the toolbar and connect items to action objects (which serve as action listeners
  * shared between toolbar icon and corresponding menu item).
  */

    JToolBar setUpToolBar() {
        JToolBar toolBar = new JToolBar();
      toolBar.setOpaque(false);
      toolBar.setBackground(new java.awt.Color(229, 229, 229));
      toolBar.setBorder(null);
      toolBar.setRollover(true);
      toolBar.setBorderPainted(false);
        ToolBarComponent aNew = new ToolBarComponent(fileNewAction);
      aNew.setText("");
      //New.setOpaque(false);
      aNew.setIconTextGap(0);

        ToolBarComponent open = new ToolBarComponent(fileOpenAction);
      open.setText("");
        ToolBarComponent save = new ToolBarComponent(fileSaveAction);
      save.setText("");
        ToolBarComponent saveAs = new ToolBarComponent(fileSaveAsAction);
      saveAs.setText("");
    //  DumpMemory = new ToolBarComponent(fileDumpMemoryAction);
    //  DumpMemory.setText("");
    //    Print= new ToolBarComponent(filePrintAction);
    // Print.setText("");

        // components of the toolbar
        ToolBarComponent undo = new ToolBarComponent(editUndoAction);
      undo.setText("");

        ToolBarComponent redo = new ToolBarComponent(editRedoAction);
      redo.setText("");

        ToolBarComponent cut = new ToolBarComponent(editCutAction);
      cut.setText("");

        ToolBarComponent copy = new ToolBarComponent(editCopyAction);
      copy.setText("");

        ToolBarComponent paste = new ToolBarComponent(editPasteAction);
      paste.setText("");

        ToolBarComponent findReplace = new ToolBarComponent(editFindReplaceAction);
      findReplace.setText("");

        JButton selectAll = new JButton(editSelectAllAction);
      selectAll.setText("");

        ToolBarComponent run1 = new ToolBarComponent(runGoAction);
      run1.setText("");

        ToolBarComponent assemble = new ToolBarComponent(runAssembleAction);
      assemble.setText("");

        ToolBarComponent step = new ToolBarComponent(runStepAction);
      step.setText("");

        ToolBarComponent backstep = new ToolBarComponent(runBackstepAction);
      backstep.setText("");

        ToolBarComponent reset1 = new ToolBarComponent(runResetAction);
      reset1.setText("");

        ToolBarComponent stop = new ToolBarComponent(runStopAction);
      stop.setText("");

        ToolBarComponent pause = new ToolBarComponent(runPauseAction);
      pause.setText("");

        ToolBarComponent help1 = new ToolBarComponent(helpHelpAction);
      help1.setText("");
      
      toolBar.add(aNew);
      toolBar.add(open);
      toolBar.add(save);
      toolBar.add(saveAs);
        //    if (new mars.riscv.dump.DumpFormatLoader().loadDumpFormats().size() > 0) {
        //      toolBar.add(DumpMemory);
        //   }
        // toolBar.add(Print);
        //  toolBar.add(new JToolBar.Separator());
      toolBar.add(undo);
      toolBar.add(redo);
      toolBar.add(cut);
      toolBar.add(copy);
      toolBar.add(paste);
      toolBar.add(findReplace);
      toolBar.add(new JToolBar.Separator());
      toolBar.add(assemble);
      toolBar.add(run1);
      toolBar.add(step);
      toolBar.add(backstep);
      toolBar.add(pause);
      toolBar.add(stop);
      toolBar.add(reset1);
      toolBar.add(new JToolBar.Separator());
      toolBar.add(help1);
      toolBar.add(new JToolBar.Separator());
   	
      return toolBar;
   }
   

    /**
    * Get current menu state.  State values are constants in FileStatus class.  DPS 23 July 2008
    * @return current menu state.
    **/
    public static int getMenuState() {
        return menuState;
    }
   
    /**
    *  To set whether the register values are reset.
    *  @param b Boolean true if the register values have been reset.
    **/

    public  void setReset(boolean b){
        reset=b;
    }

    /**
    *  To set whether MIPS program execution has started.
    *  @param b true if the MIPS program execution has started.
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
    *  To find out whether RISCV program is currently executing.
    *  @return  true if RISCV program is currently executing.
    **/
    public boolean getStarted(){
        return started;
    }
	
    /**
    *  Get reference to Editor object associated with this GUI.
    *  @return Editor for the GUI.
    **/
    public Editor getEditor() {
        return editor;
    }
	
    /**
    *  Get reference to messages pane associated with this GUI.
    *  @return MessagesPane object associated with the GUI.
    **/
    public MainPane getMainPane() {
        return mainPane;
    }

    /**
    *  Get reference to messages pane associated with this GUI.
    *   @return MessagesPane object associated with the GUI.
    **/
    public MessagesPane getMessagesPane() {
        return messagesPane;
    }

    /**
    * Get reference to registers pane associated with this GUI.
    * @return RegistersPane object associated with the GUI.
    **/
    public RegistersPane getRegistersPane() {
        return registersPane;
    }

    /**
    * Get reference to settings menu item for display base of memory/register values.
    * @return the menu item
    **/
    public JCheckBoxMenuItem getValueDisplayBaseMenuItem() {
        return settingsValueDisplayBase;
    }

    /**
    * Get reference to settings menu item for display base of memory/register values.
    * @return the menu item
    **/
    public JCheckBoxMenuItem getAddressDisplayBaseMenuItem() {
        return settingsAddressDisplayBase;
    }
	
    /**
    * Return reference to the Run->Assemble item's action.  Needed by File->Open in case
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

    private class ToolBarComponent extends JButton{

        private ToolBarComponent(Action action) {
            super(action);
            this.setOpaque(false);
            setFocusable(false);
            setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        }
    }

 }
