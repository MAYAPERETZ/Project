package mars.venus;
import mars.*;
import mars.util.*;
import mars.simulator.*;
import mars.mips.hardware.*;
import mars.mips.hardware.memory.Memory;
import mars.mips.hardware.memory.MemoryAccessNotice;
import mars.mips.instructions.GenMath;
import static mars.util.Math2.*;
import static mars.mips.instructions.GenMath.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.table.*;
import javax.swing.event.*;

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
	  *  Represents the Data Segment window, which is a type of JInternalFrame.
	  *   @author Sanderson and Bumgarner
	  **/
    
   public class DataSegmentWindow extends JInternalFrame implements Observer {

       private static  Object[][] dataData;
   
      private static JTable dataTable;
      private JScrollPane dataTableScroller;
      private Container contentPane;
      private JPanel tablePanel;
      private JButton dataButton, nextButton, prevButton, stakButton, globButton, heapButton, kernButton, extnButton, mmioButton, textButton;
      private JCheckBox asciiDisplayCheckBox;
   	
      static final int VALUES_PER_ROW = 8;
      static final int NUMBER_OF_ROWS = 16;  // with 8 value columns, this shows 512 Numbers;  
      static final int NUMBER_OF_COLUMNS = VALUES_PER_ROW + 1;// 1 for address and 8 for values
      static final int NumberS_PER_VALUE = 4;
      static final int NumberS_PER_ROW = VALUES_PER_ROW * NumberS_PER_VALUE;
      static final int MEMORY_CHUNK_SIZE = NUMBER_OF_ROWS * NumberS_PER_ROW;
   	// PREV_NEXT_CHUNK_SIZE determines how many rows will be scrolled when Prev or Next buttons fire.
   	// MEMORY_CHUNK_SIZE/2 means scroll half a table up or down.  Easier to view series that flows off the edge.
   	// MEMORY_CHUNK_SIZE means scroll a full table's worth.  Scrolls through memory faster.  DPS 26-Jan-09
      static final int PREV_NEXT_CHUNK_SIZE = MEMORY_CHUNK_SIZE/2;
      static final int ADDRESS_COLUMN = 0;
      static final boolean USER_MODE = false;
      static final boolean KERNEL_MODE = true;
   
      private boolean addressHighlighting = false;
      private boolean asciiDisplay = false;
      private int addressRow, addressColumn;
      private Settings settings;
   	
      Number firstAddress = 0,addressRowFirstAddress = 0;
      Number homeAddress = 0;
      boolean userOrKernelMode;
   
   	// The combo box replaced the row of buttons when Number of buttons expanded to 7!
   	// We'll keep the button objects however and manually invoke their action listeners
   	// when the corresponding combo box item is selected.  DPS 22-Nov-2006
      JComboBox  baseAddressSelector;
   	
   	// The next bunch are initialized dynamically in initializeBaseAddressChoices()
      private String[] displayBaseAddressChoices;
       private int defaultBaseAddressIndex;
      private JPanel panel;
      private  JTabbedPane tabbedPane;
      JButton[] baseAddressButtons;
   
    /**
      *  Constructor for the Data Segment window.
   	*   @param choosers an array of objects used by user to select Number display base (10 or 16)
   	*/
   
      public DataSegmentWindow (NumberDisplayBaseChooser[] choosers){
         super("Data Segment", true, false, true, true);
         setToolTipText("");
         setVisible(true);
         setFont(UIManager.getFont("FileChooser.listFont"));
         setForeground(Color.WHITE);
         setFrameIcon(null);
      	
         Simulator.getInstance().addObserver(this);
         settings = Globals.getSettings();
         settings.addObserver(this);
      							
         homeAddress = Memory.getInstance().getDataTable().getBaseAddress();  // address for Home button
         firstAddress = homeAddress;  // first address to display at any given time
         userOrKernelMode = USER_MODE;
         addressHighlighting = false;
         contentPane = this.getContentPane();
         tablePanel = new JPanel(new GridLayout(1,2,10,0));
         Toolkit tk = Toolkit.getDefaultToolkit();
         Class cs = this.getClass(); 
         try {
            prevButton = new PrevButton(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Previous22.png"))));//"Back16.gif"))));//"Down16.gif"))));
            nextButton = new NextButton(new ImageIcon(tk.getImage(cs.getResource(Globals.imagesPath+"Next22.png"))));//"Forward16.gif")))); //"Up16.gif"))));
            //  This group of buttons was replaced by a combo box.  Keep the JButton objects for their action listeners.
            dataButton = new JButton();//".data");
            stakButton = new JButton();//"$sp");
            globButton = new JButton();//"$gp");
            heapButton = new JButton();//"heap");
            extnButton = new JButton();//".extern");
            mmioButton = new JButton();//"MMIO");
            textButton = new JButton();//".text");
            kernButton = new JButton();//".kdata");
         } 
            catch (NullPointerException e) {
               System.out.println("Internal Error: images folder not found");
               System.exit(0);
            }
      	
         initializeBaseAddressChoices();      	      	
      			
         
         
         tabbedPane = new JTabbedPane();
     
         contentPane.add(tabbedPane, BorderLayout.CENTER);
         
         panel = new JPanel(new BorderLayout(0, 0));
         tabbedPane.addTab("Data Segment", panel);
         JPanel features = new JPanel();
         panel.add(features, BorderLayout.SOUTH);
         baseAddressSelector = new JComboBox();
         baseAddressSelector.setModel(new CustomComboBoxModel(displayBaseAddressChoices));
         baseAddressSelector.setEditable(false);
         baseAddressSelector.setSelectedIndex(defaultBaseAddressIndex);
         baseAddressSelector.setToolTipText("Base address for data segment display");
         baseAddressSelector.addActionListener(
                 e -> {
                  // trigger action listener for associated invisible button.
                    baseAddressButtons[baseAddressSelector.getSelectedIndex()].getActionListeners()[0].actionPerformed(null);
                 });
         JPanel navButtons = new JPanel(new GridLayout(1,4));
         navButtons.add(prevButton);
         navButtons.add(nextButton);
         features.add(navButtons);
         features.add(baseAddressSelector);
         asciiDisplayCheckBox = new JCheckBox("ASCII", asciiDisplay);
         asciiDisplayCheckBox.setToolTipText("Display data segment values in ASCII (overrides Hexadecimal Values setting)");
         asciiDisplayCheckBox.addItemListener(
                 e -> {
                    asciiDisplay = (e.getStateChange() == ItemEvent.SELECTED);
                    DataSegmentWindow.this.updateValues();
                 });
         features.add(asciiDisplayCheckBox);
         addButtonActionListenersAndInitialize();
         for (int i=0; i<choosers.length; i++) {
            features.add(choosers[i]);
         }
      }  
   
   
      public void updateBaseAddressComboBox() {
         displayBaseAddressArray[EXTERN_BASE_ADDRESS_INDEX] = Memory.externBaseAddress;
         displayBaseAddressArray[GLOBAL_POINTER_ADDRESS_INDEX] = -1; /*Memory.globalPointer*/
         displayBaseAddressArray[DATA_BASE_ADDRESS_INDEX] =  Memory.getInstance().getDataTable().getBaseAddress(); 
         displayBaseAddressArray[HEAP_BASE_ADDRESS_INDEX] = Memory.heapBaseAddress; 
         displayBaseAddressArray[STACK_POINTER_BASE_ADDRESS_INDEX] = -1; /*Memory.stackPointer*/ 
         displayBaseAddressArray[KERNEL_DATA_BASE_ADDRESS_INDEX] = Memory.getInstance().getKernelDataTable().getBaseAddress(); 
         displayBaseAddressArray[MMIO_BASE_ADDRESS_INDEX] = Memory.getInstance().getMemoryMapTable().getBaseAddress();
         displayBaseAddressArray[TEXT_BASE_ADDRESS_INDEX] = Memory.getInstance().getTextTable().getBaseAddress();
      
			
         displayBaseAddressChoices = createBaseAddressLabelsArray(displayBaseAddressArray, descriptions);
         baseAddressSelector.setModel(new CustomComboBoxModel(displayBaseAddressChoices));
         baseAddressSelector.setSelectedIndex(defaultBaseAddressIndex);
      }
   	
     /**
      *  Scroll the viewport so the cell at the given data segment address
   	*  is visible, vertically centered if possible, and selected.  
   	*  Developed July 2007 for new feature that shows source code step where
   	*  label is defined when that label is clicked on in the Label Window.
   	*  Note there is a separate method to highlight the cell by setting
   	*  its background color to a highlighting color.  Thus one cell can be
   	*  highlighted while a different cell is selected at the same time.
   	*  @param address data segment address of word to be selected.
   	*/
      void selectCellForAddress(int address) {
         Point rowColumn = displayCellForAddress(address);
         if (rowColumn==null)
            return;

         Rectangle addressCell = dataTable.getCellRect(rowColumn.x, rowColumn.y, true);
         // Select the memory address cell by generating a fake Mouse Pressed event within its
      	// extent and explicitly invoking the table's mouse listener.
         MouseEvent fakeMouseEvent = new MouseEvent(dataTable, MouseEvent.MOUSE_PRESSED,
                                                    new Date().getTime(), MouseEvent.BUTTON1_MASK,
            													 (int)addressCell.getX()+1,
            													 (int)addressCell.getY()+1, 1, false);
         MouseListener[] mouseListeners = dataTable.getMouseListeners();
         for (int i=0; i<mouseListeners.length; i++)
            mouseListeners[i].mousePressed(fakeMouseEvent);

      }
   
     /**
      *  Scroll the viewport so the cell at the given data segment address
   	*  is visible, vertically centered if possible, and highlighted (but not selected).  
   	*  @param address data segment address of word to be selected.
   	*/   	 
      void highlightCellForAddress(Number address) {
         Point rowColumn = displayCellForAddress(address);
         if (rowColumn==null || rowColumn.x < 0 || rowColumn.y < 0)
            return;

         this.addressRow = rowColumn.x;
         this.addressColumn = rowColumn.y; 
         this.addressRowFirstAddress = Binary.stringToNumber(dataTable.getValueAt(this.addressRow,ADDRESS_COLUMN).toString());
      //System.out.println("Address "+Binary.intToHexString(address)+" becomes row "+ addressRow + " column "+addressColumn+
      //" starting addr "+dataTable.getValueAt(this.addressRow,ADDRESS_COLUMN));
         // Tell the system that table contents have changed.  This will trigger re-rendering 
      	// during which cell renderers are obtained.  The cell of interest (identified by 
      	// instance variables this.addressRow and this.addressColumn) will get a renderer
      	// with highlight background color and all others get renderer with default background. 
         dataTable.tableChanged(new TableModelEvent(dataTable.getModel(),0,dataData.length-1));
      }
   	 
   	 // Given address, will compute table cell location, adjusting table if necessary to
   	 // contain this cell, make sure that cell is visible, then return a Point containing
   	 // row and column position of cell in the table.  This private helper method is called
   	 // by selectCellForAddress() and highlightCellForAddress().
   	 // This is the kind of design I tell my students to avoid! The method both translates
   	 // address to table cell coordinates and adjusts the display to assure the cell is visible.
   	 // The two operations are related because the address may fall in within address space not
   	 // currently in the (display) table, including a different MIPS data segment (e.g. in
   	 // kernel instead of user data segment).
      private Point displayCellForAddress(Number address) {
         //////////////////////////////////////////////////////////
      	// This requires a 5-step process.  Each step is described
      	// just above the statements that implement it.
      	//////////////////////////////////////////////////////////
      	
         // STEP 1: Determine which data segment contains this address.  
         int desiredComboBoxIndex = getBaseAddressIndexForAddress(address);
         if (desiredComboBoxIndex < 0)
            // It is not a data segment address so good bye!
            return null;

         // STEP 2:  Set the combo box appropriately.  This will also display the 
      	// first chunk of addresses from that segment.
         baseAddressSelector.setSelectedIndex(desiredComboBoxIndex);


            ((CustomComboBoxModel) baseAddressSelector.getModel()).forceComboBoxUpdate(desiredComboBoxIndex);
         baseAddressButtons[desiredComboBoxIndex].getActionListeners()[0].actionPerformed(null);
      	// STEP 3:  Display memory chunk containing this address, which may be 
      	// different than the one just displayed.
         Number baseAddress = displayBaseAddressArray[desiredComboBoxIndex];
         if (isEq(baseAddress, -1)) {
            if (desiredComboBoxIndex == GLOBAL_POINTER_ADDRESS_INDEX) {
               baseAddress = sub(RVIRegisters.getValue(RVIRegisters.GLOBAL_POINTER_REGISTER),
                              rem(RVIRegisters.getValue(RVIRegisters.GLOBAL_POINTER_REGISTER), NumberS_PER_ROW));
            } 
            else if (desiredComboBoxIndex == STACK_POINTER_BASE_ADDRESS_INDEX) {
               baseAddress = sub(RVIRegisters.getValue(RVIRegisters.STACK_POINTER_REGISTER),
                       rem(RVIRegisters.getValue(RVIRegisters.STACK_POINTER_REGISTER), NumberS_PER_ROW));
               } 
            else {
               return null;// shouldn't happen since these are the only two
            }
         }
         Number NumberOffset  = sub(address , baseAddress);
         Number chunkOffset = div(NumberOffset, MEMORY_CHUNK_SIZE); 
         Number NumberOffsetIntoChunk = rem(NumberOffset, MEMORY_CHUNK_SIZE);
      	// Subtract 1 from chunkOffset because we're gonna call the "next" action
      	// listener to get the correct chunk loaded and displayed, and the first
      	// thing it does is increment firstAddress by MEMORY_CHUNK_SIZE.  Here
      	// we do an offsetting decrement in advance because we don't want the
      	// increment but we want the other actions that method provides.
         firstAddress = sub(GenMath.add(firstAddress, mul(chunkOffset, MEMORY_CHUNK_SIZE)), PREV_NEXT_CHUNK_SIZE);
         nextButton.getActionListeners()[0].actionPerformed(null);
      	// STEP 4:  Find cell containing this address.  Add 1 to column calculation
      	// because table column 0 displays address, not memory contents.  The 
      	// "convertColumnIndexToView()" is not necessary because the columns cannot be
      	// reordered, but I included it as a precautionary measure in case that changes.
         int addrRow    = div(NumberOffsetIntoChunk, NumberS_PER_ROW).intValue();
         int addrColumn = div(rem(NumberOffsetIntoChunk, NumberS_PER_ROW), NumberS_PER_VALUE).intValue() + 1;
         addrColumn = dataTable.convertColumnIndexToView(addrColumn); 
         Rectangle addressCell = dataTable.getCellRect(addrRow, addrColumn, true);
      	// STEP 5:  Center the row containing the cell of interest, to the extent possible.
         double cellHeight = addressCell.getHeight();
         double viewHeight = dataTableScroller.getViewport().getExtentSize().getHeight();
         int numberOfVisibleRows = (int) (viewHeight / cellHeight);
         int newViewPositionY = Math.max((int)((addrRow-(numberOfVisibleRows/2))*cellHeight), 0);
         dataTableScroller.getViewport().setViewPosition(new Point(0, newViewPositionY));
         return new Point(addrRow, addrColumn); 
      }
   
   
       ////////////////////////////////////////////////////////////////////////
       // Initalize arrays used with Base Address combo box chooser.
   	 // The combo box replaced the row of buttons when Number of buttons expanded to 7!
      private static final int EXTERN_BASE_ADDRESS_INDEX = 0;
      private static final int GLOBAL_POINTER_ADDRESS_INDEX = 3; //1;
      private static final int TEXT_BASE_ADDRESS_INDEX = 5; //2;
      private static final int DATA_BASE_ADDRESS_INDEX = 1; //3;
      private static final int HEAP_BASE_ADDRESS_INDEX = 2; //4;
      private static final int STACK_POINTER_BASE_ADDRESS_INDEX = 4; //5;
      private static final int KERNEL_DATA_BASE_ADDRESS_INDEX = 6;
      private static final int MMIO_BASE_ADDRESS_INDEX = 7;

      // Must agree with above in Number and order...
      private Number[] displayBaseAddressArray = {Memory.externBaseAddress, 
                                    Memory.getInstance().getDataTable().getBaseAddress(), Memory.heapBaseAddress,  -1 /*Memory.globalPointer*/,
               							-1 /*Memory.stackPointer*/, Memory.getInstance().getTextTable().getBaseAddress(),
               							Memory.getInstance().getKernelDataTable().getBaseAddress(), 
               							Memory.getInstance().getMemoryMapTable().getBaseAddress(), };
      	// Must agree with above in Number and order...
      String[] descriptions =         { " (.extern)", " (.data)", " (heap)", "current gp", 
                                           "current sp", " (.text)", " (.kdata)"," (MMIO)" };   
   												   
      private void initializeBaseAddressChoices() {
         // Also must agree in Number and order.  Upon combo box item selection, will invoke
      	// action listener for that item's button.  
         baseAddressButtons = new JButton[descriptions.length];
         baseAddressButtons[EXTERN_BASE_ADDRESS_INDEX]=extnButton; 
         baseAddressButtons[GLOBAL_POINTER_ADDRESS_INDEX]=globButton;
         baseAddressButtons[DATA_BASE_ADDRESS_INDEX]=dataButton;
         baseAddressButtons[HEAP_BASE_ADDRESS_INDEX]=heapButton; 
         baseAddressButtons[STACK_POINTER_BASE_ADDRESS_INDEX]=stakButton; 
         baseAddressButtons[KERNEL_DATA_BASE_ADDRESS_INDEX]=kernButton;
         baseAddressButtons[MMIO_BASE_ADDRESS_INDEX]=mmioButton;
         baseAddressButtons[TEXT_BASE_ADDRESS_INDEX]=textButton;
         displayBaseAddressChoices = createBaseAddressLabelsArray(displayBaseAddressArray, descriptions);
         defaultBaseAddressIndex = DATA_BASE_ADDRESS_INDEX;
      }
   
       // Create and fill String array containing labels for base address combo box.
      private String[] createBaseAddressLabelsArray(Number[] baseAddressArray, String[] descriptions) {
         String[] baseAddressChoices = new String[baseAddressArray.length];
         for (int i=0; i<baseAddressChoices.length; i++) {
            baseAddressChoices[i] =  (!isEq(baseAddressArray[i], -1)
                                            ? mars.util.Binary.currentNumToHexString(baseAddressArray[i])
               									  : "")
               									  +descriptions[i];
         }
         return baseAddressChoices;
      } 
   	
   	  
      // Given an address, determine which segment it is in and return the corresponding
   	// combo box index.  Note there is not a one-to-one correspondence between these
   	// indexes and the Memory tables.  For instance, the heap (0x10040000), the
   	// global (0x10008000) and the data segment base (0x10000000) are all stored in the 
   	// same table as the static (0x10010000) so all are "Memory.inDataSegment()". 
      private int getBaseAddressIndexForAddress(Number address) {
         int desiredComboBoxIndex = -1; // assume not a data address.
         if (Memory.getInstance().getKernelDataTable().inSegment(address))
            return KERNEL_DATA_BASE_ADDRESS_INDEX;
         else if (Memory.getInstance().getMemoryMapTable().inSegment(address))
            return MMIO_BASE_ADDRESS_INDEX;
         else if (Memory.getInstance().getTextTable().inSegment(address))  // DPS. 8-July-2013
            return TEXT_BASE_ADDRESS_INDEX;

         Number shortDistance = (MemoryConfigurations.getCurrentComputingArchitecture() == 32) ?  0x7fffffff : 0x7fffffffffffffffL;
         Number thisDistance;
      	// Check distance from .extern base.  Cannot be below it
         thisDistance = sub(address, Memory.externBaseAddress);
         if (!isLtz(thisDistance) && isLt(thisDistance, shortDistance)) {
            shortDistance = thisDistance;
            desiredComboBoxIndex = EXTERN_BASE_ADDRESS_INDEX;
         }
      	// Check distance from global pointer; can be either side of it...
         thisDistance = abs(sub(address, RVIRegisters.getValue(RVIRegisters.GLOBAL_POINTER_REGISTER))); // distance from global pointer
         if (isLt(thisDistance, shortDistance)) {
            shortDistance = thisDistance;
            desiredComboBoxIndex = GLOBAL_POINTER_ADDRESS_INDEX;
         }
      	// Check distance from .data base.  Cannot be below it
         thisDistance = sub(address, Memory.getInstance().getDataTable().getBaseAddress());
         if (!isLtz(thisDistance) && isLt(thisDistance, shortDistance)) {
            shortDistance = thisDistance;
            desiredComboBoxIndex = DATA_BASE_ADDRESS_INDEX;
         }
      	// Check distance from heap base.  Cannot be below it
         thisDistance = sub(address, Memory.heapBaseAddress);
         if (!isLtz(thisDistance) && isLt(thisDistance, shortDistance)) {
            shortDistance = thisDistance;
            desiredComboBoxIndex = HEAP_BASE_ADDRESS_INDEX;
         }      	
      	// Check distance from stack pointer.  Can be on either side of it...
         thisDistance = abs(sub(address, RVIRegisters.getValue(RVIRegisters.STACK_POINTER_REGISTER)));
         if (isLt(thisDistance, shortDistance)) {
            shortDistance = thisDistance;
            desiredComboBoxIndex = STACK_POINTER_BASE_ADDRESS_INDEX;
         }      	
         return desiredComboBoxIndex;
      }
   
   	
   	////////////////////////////////////////////////////////////////////////////////
   	//  Generates the Address/Data part of the Data Segment window.
   	//   Returns the JScrollPane for the Address/Data part of the Data Segment window.
      private JScrollPane generateDataPanel(){
         dataData = new Object[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
         int valueBase = Globals.getGui().getMainPane().getExecutePane().getValueDisplayBase();
         int addressBase = Globals.getGui().getMainPane().getExecutePane().getAddressDisplayBase();
         Number address = this.homeAddress;
         for(int row=0; row<NUMBER_OF_ROWS; row++){
            dataData[row][ADDRESS_COLUMN] = NumberDisplayBaseChooser.formatUnsignedNumber(address, addressBase);
            for (int column=1; column<NUMBER_OF_COLUMNS; column++) {
               try {
                  dataData[row][column] = NumberDisplayBaseChooser.formatNumber(Globals.memory.getRawWord(address), valueBase);
               } 
                  catch (AddressErrorException aee) {
                     dataData[row][column] = NumberDisplayBaseChooser.formatIntNumber(0, valueBase);
                  }
               address = GenMath.add(address, NumberS_PER_VALUE);
            }
         }
         String [] names = new String[NUMBER_OF_COLUMNS];
         for (int i=0; i<NUMBER_OF_COLUMNS; i++)
            names[i] = getHeaderStringForColumn(i, addressBase);

         dataTable= new MyTippedJTable(new DataTableModel(dataData, names));
      	// Do not allow user to re-order columns; column order corresponds to MIPS memory order
         dataTable.getTableHeader().setReorderingAllowed(false);
         dataTable.setRowSelectionAllowed(false);
      	// Addresses are column 0, render right-justified in mono font
         MonoRightCellRenderer monoRightCellRenderer = new MonoRightCellRenderer(); 
         dataTable.getColumnModel().getColumn(ADDRESS_COLUMN).setPreferredWidth(60);
         dataTable.getColumnModel().getColumn(ADDRESS_COLUMN).setCellRenderer( monoRightCellRenderer ); 
         // Data cells are columns 1 onward, render right-justitifed in mono font but highlightable.
         AddressCellRenderer addressCellRenderer = new AddressCellRenderer();
         for (int i=1; i<NUMBER_OF_COLUMNS; i++) {
            dataTable.getColumnModel().getColumn(i).setPreferredWidth(60);
            dataTable.getColumnModel().getColumn(i).setCellRenderer( addressCellRenderer );
         }
         dataTableScroller = new JScrollPane(dataTable,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
                                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
         return dataTableScroller;
      }
      
   	// Little helper.  Is called when headers set up and each time Number base changes.
      private String getHeaderStringForColumn(int i, int base) {
         return (i==ADDRESS_COLUMN)? "Address" : "Value (+"+Integer.toString((i-1)*NumberS_PER_VALUE, base)+")";
      }
   	
   	
   	/**
   	 * Generates and displays fresh table, typically done upon successful assembly.
   	 */   	
      public synchronized void setupTable(){
         tablePanel.removeAll();
         tablePanel.add(generateDataPanel()); 
         panel.add(tablePanel);
         enableAllButtons();
         notifyAll();
      }
      
   	/**
   	 * Removes the table from its frame, typically done when a file is closed.
   	 */
      public void clearWindow() {
         tablePanel.removeAll();
         disableAllButtons();
      }
      
   	/**
   	 * Clear highlight background color from any cell currently highlighted.
   	 */
      public void clearHighlighting() {
         addressHighlighting=false;
         dataTable.tableChanged(new TableModelEvent(dataTable.getModel(),0,dataData.length-1));
      	// The below addresses situation in which addressRow and addressColum hold their
      	// values across assemble operations.  Whereupon at the first step of the next
      	// run the last cells from the previous run are highlighted!  This method is called
      	// after each successful assemble (or reset, which just re-assembles).  The
      	// assignment below assures the highlighting condition column==addressColumn will be
      	// initially false since column>=0.  DPS 23 jan 2009
         addressColumn = -1; 
      }
   
       
      private int getValueDisplayFormat() {
         return (asciiDisplay) ? NumberDisplayBaseChooser.ASCII : 
            Globals.getGui().getMainPane().getExecutePane().getValueDisplayBase();
      }
   	
   	/**
   	 * Update table model with contents of new memory "chunk".  Mars supports megaNumbers of
   	 * data segment space so we only plug a "chunk" at a time into the table.
   	 * @param firstAddr the first address in the memory range to be placed in the model.
   	 */
   	 
      public void updateModelForMemoryRange(Number firstAddr) {
         if (tablePanel.getComponentCount() == 0) 
            return; // ignore if no content to change
         int valueBase = getValueDisplayFormat();
         int addressBase = Globals.getGui().getMainPane().getExecutePane().getAddressDisplayBase();
         Number address = firstAddr;
         TableModel dataModel = dataTable.getModel();
         for (int row=0; row<NUMBER_OF_ROWS; row++) {
            ((DataTableModel)dataModel).setDisplayAndModelValueAt(NumberDisplayBaseChooser.formatUnsignedNumber(address, addressBase),row,ADDRESS_COLUMN);
            for (int column=1; column<NUMBER_OF_COLUMNS; column++) {
               try {
                  ((DataTableModel)dataModel).setDisplayAndModelValueAt(NumberDisplayBaseChooser.formatUnsignedNumber(Globals.memory.getWordNoNotify(address), valueBase),row,column);
               } 
                  catch (AddressErrorException aee) {
                     // Bit of a hack here.  Memory will throw an exception if you try to read directly from text segment when the
                  	// self-modifying code setting is disabled.  This is a good thing if it is the executing MIPS program trying to
                  	// read.  But not a good thing if it is the DataSegmentDisplay trying to read.  I'll trick Memory by 
                  	// temporarily enabling the setting as "non persistent" so it won't write through to the registry.
                     if (Memory.getInstance().getTextTable().inSegment(address)) {
                        Number displayValue = 0;
                        if (!Globals.getSettings().getBooleanSetting(Settings.SELF_MODIFYING_CODE_ENABLED)) {
                           Globals.getSettings().setBooleanSettingNonPersistent(Settings.SELF_MODIFYING_CODE_ENABLED, true);
                           try {
                              displayValue = Globals.memory.getWordNoNotify(address);
                           } 
                              catch (AddressErrorException e) { 
                              // Still got an exception?  Doesn't seem possible but if we drop through it will write default value 0.
                              }
                           Globals.getSettings().setBooleanSettingNonPersistent(Settings.SELF_MODIFYING_CODE_ENABLED, false);
                        }
                        ((DataTableModel)dataModel).setDisplayAndModelValueAt(NumberDisplayBaseChooser.formatNumber(displayValue, valueBase),row,column);
                     } 
							// Bug Fix: the following line of code disappeared during the release 4.4 mods, but is essential to
							// display values of 0 for valid MIPS addresses that are outside the MARS simulated address space.  Such
							// addresses cause an AddressErrorException.  Prior to 4.4, they performed this line of code unconditionally.  
							// With 4.4, I added the above IF statement to work with the text segment but inadvertently removed this line!
							// Now it becomes the "else" part, executed when not in text segment.  DPS 8-July-2014.
                     else {
                        ((DataTableModel)dataModel).setDisplayAndModelValueAt(NumberDisplayBaseChooser.formatNumber((Number)0, valueBase),row,column);
                     }
                  }
               address = GenMath.add(address, NumberS_PER_VALUE);
            }
         }
      }
   	 
   	/**
   	 * Update data display to show this value (I'm not sure it is being called).
   	 */
   	
      public void updateCell(Number address, Number value) {
         Number offset = sub(address, this.firstAddress);
         if (isLtz(offset) || !isLt(MEMORY_CHUNK_SIZE, offset)) // out of range
            return;

         int row = div(offset,NumberS_PER_ROW).intValue();
         int column = GenMath.add(div(rem(offset, NumberS_PER_ROW),NumberS_PER_VALUE), 1).intValue(); // column 0 reserved for address
         int valueBase = Globals.getGui().getMainPane().getExecutePane().getValueDisplayBase();
         ((DataTableModel)dataTable.getModel()).setDisplayAndModelValueAt(NumberDisplayBaseChooser.formatNumber(value, valueBase),
                    row,column);
      }
   	
   	/**
   	 *  Redisplay the addresses.  This should only be done when address display base is
   	 *  modified (e.g. between base 16, hex, and base 10, dec).
   	 */
      public synchronized void updateDataAddresses() {
         while (tablePanel.getComponentCount() == 0)
         {
        	try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
         }
            // ignore if no content to change
         int addressBase = Globals.getGui().getMainPane().getExecutePane().getAddressDisplayBase();
         Number address = this.firstAddress;
         String formattedAddress;
         for(int i=0; i<NUMBER_OF_ROWS; i++) {
            formattedAddress = NumberDisplayBaseChooser.formatUnsignedNumber(address, addressBase);
            ((DataTableModel)dataTable.getModel()).setDisplayAndModelValueAt(formattedAddress, i, 0);
            address = GenMath.add(address, NumberS_PER_ROW);
         }
      	// column headers include address offsets, so translate them too
         for (int i=1; i<NUMBER_OF_COLUMNS; i++) {
            dataTable.getColumnModel().getColumn(i).setHeaderValue(getHeaderStringForColumn(i, addressBase));
         }
         dataTable.getTableHeader().repaint();
      }
   	
      /**
   	 * Update data display to show all values
   	 */
   	 
      public void updateValues(){
         updateModelForMemoryRange(this.firstAddress);
      }
   
      /**
   	 * Reset range of memory addresses to base address of currently selected segment and update display.
   	 */
   	 
      public void resetMemoryRange(){
         baseAddressSelector.getActionListeners()[0].actionPerformed(null); // previously dataButton
      }
   
      /**
   	 * Reset all data display values to 0
   	 */   
   	
      public void resetValues(){
         int valueBase = Globals.getGui().getMainPane().getExecutePane().getValueDisplayBase();
         TableModel dataModel = dataTable.getModel();
         for (int row=0; row<NUMBER_OF_ROWS; row++) {
            for (int column=1; column<NUMBER_OF_COLUMNS; column++)
               ((DataTableModel)dataModel).setDisplayAndModelValueAt(NumberDisplayBaseChooser.formatIntNumber(0, valueBase),row,column);

         }
         disableAllButtons();
      }
   
   	/*
   	 * Do this initially and upon reset.
   	 */
      private void disableAllButtons() {
         baseAddressSelector.setEnabled(false);
         globButton.setEnabled(false);
         stakButton.setEnabled(false);
         heapButton.setEnabled(false);
         extnButton.setEnabled(false);
         mmioButton.setEnabled(false);
         textButton.setEnabled(false);
         kernButton.setEnabled(false);
         prevButton.setEnabled(false);
         nextButton.setEnabled(false);
         dataButton.setEnabled(false);
      }
   
   	/*
   	 * Do this upon reset.
   	 */
      private void enableAllButtons() {
         baseAddressSelector.setEnabled(true);
         globButton.setEnabled(true);
         stakButton.setEnabled(true);
         heapButton.setEnabled(true);
         extnButton.setEnabled(true);
         mmioButton.setEnabled(true);
         textButton.setEnabled(settings.getBooleanSetting(Settings.SELF_MODIFYING_CODE_ENABLED));
         kernButton.setEnabled(true);
         prevButton.setEnabled(true);
         nextButton.setEnabled(true);
         dataButton.setEnabled(true);
      }
   	
   	/*
   	 * Establish action listeners for the data segment navigation buttons.
   	 */
   	
      private void addButtonActionListenersAndInitialize() {
         // set initial states
         disableAllButtons();
      	// add tool tips
      	// NOTE: For buttons that are now combo box items, the tool tips are not displayed w/o custom renderer.
         globButton.setToolTipText("View range around global pointer");
         stakButton.setToolTipText("View range around stack pointer");
         heapButton.setToolTipText("View range around heap base address "+
                         Binary.currentNumToHexString(Globals.memory.heapBaseAddress));
         kernButton.setToolTipText("View range around kernel data base address "+
                         Binary.currentNumToHexString(Globals.memory.getInstance().getKernelDataTable().getBaseAddress()));
         extnButton.setToolTipText("View range around static global base address "+
                         Binary.currentNumToHexString(Globals.memory.externBaseAddress));
         mmioButton.setToolTipText("View range around MMIO base address "+
                         Binary.currentNumToHexString(Globals.memory.getInstance().getMemoryMapTable().getBaseAddress()));
         textButton.setToolTipText("View range around program code "+
                         Binary.currentNumToHexString(Globals.memory.getInstance().getTextTable().getBaseAddress()));
         prevButton.setToolTipText("View next lower address range; hold down for rapid fire");
         nextButton.setToolTipText("View next higher address range; hold down for rapid fire");
         dataButton.setToolTipText("View range around static data segment base address "+
                         Binary.currentNumToHexString(Globals.memory.getInstance().getDataTable().getBaseAddress()));
      
      	// add the action listeners to maintain button state and table contents
      	// Currently there is no memory upper bound so next button always enabled.
      
         globButton.addActionListener( 
               new ActionListener() {
                  public void actionPerformed(ActionEvent ae) {
                     userOrKernelMode = USER_MODE;
                  	// get $gp global pointer, but guard against it having value below data segment
                     if(MemoryConfigurations.getCurrentComputingArchitecture() == 32)
                    	 firstAddress = max(Globals.memory.getInstance().getDataTable().getBaseAddress()
                    			 , RVIRegisters.getValue(RVIRegisters.GLOBAL_POINTER_REGISTER).intValue());
                     else
                    	 firstAddress = max(Globals.memory.getInstance().getDataTable().getBaseAddress()
                    			 , RVIRegisters.getValue(RVIRegisters.GLOBAL_POINTER_REGISTER).longValue());
                  	// updateModelForMemoryRange requires argument to be multiple of 4
                  	// but for cleaner display we'll make it multiple of 32 (last nibble is 0).  
                  	// This makes it easier to mentally calculate address from row address + column offset.
                     firstAddress = sub(firstAddress, rem(firstAddress, NumberS_PER_ROW));
                     homeAddress = firstAddress; 
                     firstAddress = setFirstAddressAndPrevNextButtonEnableStatus(firstAddress);
                     updateModelForMemoryRange(firstAddress);
                  }
               });
      	
         stakButton.addActionListener(
                 ae -> {
                    userOrKernelMode = USER_MODE;
                    // get $sp stack pointer, but guard against it having value below data segment
                    if(MemoryConfigurations.getCurrentComputingArchitecture() == 32)
                       firstAddress = max(Globals.memory.getInstance().getDataTable().getBaseAddress()
                                , RVIRegisters.getValue(RVIRegisters.STACK_POINTER_REGISTER).intValue());
                    else
                       firstAddress = max(Globals.memory.getInstance().getDataTable().getBaseAddress()
                                , RVIRegisters.getValue(RVIRegisters.STACK_POINTER_REGISTER).longValue());
                    // See comment above for gloButton...
                    firstAddress = sub(firstAddress, rem(firstAddress, NumberS_PER_ROW));
                    homeAddress = Globals.memory.getInstance().getStackTable().getBaseAddress();
                    firstAddress = setFirstAddressAndPrevNextButtonEnableStatus(firstAddress);
                    updateModelForMemoryRange(firstAddress);
                 });
      
         heapButton.addActionListener(
                 ae -> {
                    userOrKernelMode = USER_MODE;
                    homeAddress = Globals.memory.heapBaseAddress;
                    firstAddress = setFirstAddressAndPrevNextButtonEnableStatus(homeAddress);
                    updateModelForMemoryRange(firstAddress);
                 });
      
         extnButton.addActionListener(
                 ae -> {
                    userOrKernelMode = USER_MODE;
                    homeAddress = Globals.memory.externBaseAddress;
                    firstAddress = setFirstAddressAndPrevNextButtonEnableStatus(homeAddress);
                    updateModelForMemoryRange(firstAddress);
                 });
      			     			
         kernButton.addActionListener(
                 ae -> {
                    userOrKernelMode = KERNEL_MODE;
                    homeAddress = Globals.memory.getInstance().getKernelDataTable().getBaseAddress();
                    firstAddress = homeAddress;
                    firstAddress = setFirstAddressAndPrevNextButtonEnableStatus(firstAddress);
                    updateModelForMemoryRange(firstAddress);
                 });
      			     			
         mmioButton.addActionListener(
                 ae -> {
                    userOrKernelMode = KERNEL_MODE;
                    homeAddress = Globals.memory.getMemoryMapTable().getBaseAddress();
                    firstAddress = homeAddress;
                    firstAddress = setFirstAddressAndPrevNextButtonEnableStatus(firstAddress);
                    updateModelForMemoryRange(firstAddress);
                 });
      
         textButton.addActionListener(
                 ae -> {
                    userOrKernelMode = USER_MODE;
                    homeAddress = Globals.memory.getInstance().getTextTable().getBaseAddress();
                    firstAddress = homeAddress;
                    firstAddress = setFirstAddressAndPrevNextButtonEnableStatus(firstAddress);
                    updateModelForMemoryRange(firstAddress);
                 });
      	
         dataButton.addActionListener(
                 ae -> {
                    userOrKernelMode = USER_MODE;
                    homeAddress = Globals.memory.getInstance().getDataTable().getBaseAddress();
                    firstAddress = homeAddress;
                    firstAddress = setFirstAddressAndPrevNextButtonEnableStatus(firstAddress);
                    updateModelForMemoryRange(firstAddress);
                 });
      			
      	// NOTE: action listeners for prevButton and nextButton are now in their
      	//       specialized inner classes at the bottom of this listing.  DPS 20 July 2008  			
      
      }
   	
   	////////////////////////////////////////////////////////////////////////////////////
   	// This will assure that user cannot view memory locations outside the data segment
   	// for selected mode.  For user mode, this means no lower than data segment base,
   	// or higher than user memory boundary.  For kernel mode, this means no lower than
   	// kernel data segment base or higher than kernel memory.  It is called by the
   	// above action listeners.
   	//
   	// lowAddress is lowest desired address to view, it is adjusted if necessary
   	// and returned.
   	//
   	// PrevButton and NextButton are enabled/disabled appropriately.
   	//


      private Number setFirstAddressAndPrevNextButtonEnableStatus(Number lowAddress) {
         Number lowLimit = (userOrKernelMode==USER_MODE) ? min(min(Globals.memory.getInstance().getTextTable().getBaseAddress(),
            													 Globals.memory.getInstance().getDataTable().getBaseAddress()),
            																		Globals.memory.getInstance().getDataTable().getBaseAddress())
                                                      : Globals.memory.getInstance().getKernelDataTable().getBaseAddress();
         Number highLimit= (userOrKernelMode==USER_MODE) ? Globals.memory.userHighAddress
                                                      : Globals.memory.kernelHighAddress;

         if (!isLt(lowLimit, lowAddress)) {
            lowAddress = lowLimit;
            prevButton.setEnabled(false);
         } 
         else {
            prevButton.setEnabled(true);
         }
         if (!isLt(lowAddress, sub(highLimit, MEMORY_CHUNK_SIZE))) {
            lowAddress = sub(highLimit, MEMORY_CHUNK_SIZE + 1);
            nextButton.setEnabled(false);
         } 
         else {
            nextButton.setEnabled(true);
         }
         return lowAddress;
      }
   
   	
    	/** Required by Observer interface.  Called when notified by an Observable that we are registered with.
   	 * Observables include:
   	 *   The Simulator object, which lets us know when it starts and stops running
   	 *   A delegate of the Memory object, which lets us know of memory operations
   	 * The Simulator keeps us informed of when simulated MIPS execution is active.
   	 * This is the only time we care about memory operations.
   	 * @param observable The Observable object who is notifying us
   	 * @param obj Auxiliary object with additional information.
   	 */
   	
      public void update(Observable observable, Object obj) { 
         if (observable == mars.simulator.Simulator.getInstance()) {
            SimulatorNotice notice = (SimulatorNotice) obj;
            if (notice.getAction()==SimulatorNotice.SIMULATOR_START) {
            
               // Simulated MIPS execution starts.  Respond to memory changes if running in timed
            	// or stepped mode.
               if (notice.getRunSpeed() != RunSpeedPanel.UNLIMITED_SPEED || isEq(notice.getMaxSteps(),1)) {
                  Memory.getInstance().addObserver(this);
                  addressHighlighting = true;
               }
            } 
            else {
               // Simulated MIPS execution stops.  Stop responding.
               Memory.getInstance().deleteObserver(this);
            }
         } 
         else if (observable == settings) { 
            // Suspended work in progress. Intended to disable combobox item for text segment. DPS 9-July-2013.
            //baseAddressSelector.getModel().getElementAt(TEXT_BASE_ADDRESS_INDEX)
            //*.setEnabled(settings.getBooleanSetting(Settings.SELF_MODIFYING_CODE_ENABLED));
         }
         else if (obj instanceof MemoryAccessNotice) {          	// NOTE: observable != Memory.getInstance() because Memory class delegates notification duty.
            MemoryAccessNotice access = (MemoryAccessNotice) obj;
            if (access.getAccessType()==AccessNotice.WRITE) {
               Number address = access.getAddress();
            	// Use the same highlighting technique as for Text Segment -- see
            	// AddressCellRenderer class below.
               this.highlightCellForAddress(address);
            }
         }
      }
   	
   	
   ///////////////////////////////////////////////////////////////////////////////	   
   // Class defined to address apparent Javax.swing.JComboBox bug: when selection is
   // is set programmatically using setSelectedIndex() rather than by user-initiated 
   // event (such as mouse click), the text displayed in the JComboBox is not always 
   // updated correctly. Sometimes it is, sometimes updated to incorrect value.  
   // No pattern that I can detect.  Google search yielded many forums addressing
   // this problem. One suggested solution, a JComboBox superclass overriding 
   // setSelectedIndex to also call selectedItemChanged() did not help.  Only this 
   // solution to extend the model class to call the protected 
   // "fireContentsChanged()" method worked. DPS 25-Jan-2009
      private class CustomComboBoxModel extends DefaultComboBoxModel {
         public CustomComboBoxModel(Object[] list) {
            super(list);
         }
         private void forceComboBoxUpdate(int index) {
            super.fireContentsChanged(this,index,index);
         }
      }
      
   	
   	
   	////////////////////////////////////////////////////////////////////////
   	// Class representing memory data table data
   	
      class DataTableModel extends AbstractTableModel {
         String[] columnNames;
         Object[][] data;         
      	
         public DataTableModel(Object[][] d, String [] n){
            data=d;
            columnNames= n;
         }
      
         public int getColumnCount() {
            return columnNames.length;
         }
        
         public int getRowCount() {
            return data.length;
         }
      
         public String getColumnName(int col) {
            return columnNames[col];
         }
      
         public Object getValueAt(int row, int col) {
            return data[row][col];
         }
      
        /*
         * The cells in the Address column are not editable.  
      	* Value cells are editable except when displayed 
      	* in ASCII view - don't want to give the impression
      	* that ASCII text can be entered directly because
      	* it can't.  It is possible but not worth the
      	* effort to implement.
         */
         public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col != ADDRESS_COLUMN && !asciiDisplay)
               return true;
            else
               return false;

         }
      
      
      
        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  
         */
         public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
         }
        /*
         * Update cell contents in table model.  This method should be called
      	* only when user edits cell, so input validation has to be done.  If
      	* value is valid, MIPS memory is updated.
         */
         public void setValueAt(Object value, int row, int col) {
            int val=0;
            long address=0;
            try {
               val = Binary.stringToInt((String) value);
            }
               catch (NumberFormatException nfe) {
                  data[row][col] = "INVALID";
                  fireTableCellUpdated(row, col);
                  return;
               }
         
               // calculate address from row and column
            try {
               address = Binary.stringToInt((String)data[row][ADDRESS_COLUMN]) + (col-1)*NumberS_PER_VALUE;  // KENV 1/6/05
            }
               catch (NumberFormatException nfe) {
                  //  can't really happen since memory addresses are completely under
                  // the control of my software.
               }
         	//  Assures that if changed during MIPS program execution, the update will
         	//  occur only between MIPS instructions.
            synchronized (Globals.memoryAndRegistersLock) {
               try {
                  Globals.memory.setRawWord(address,val);
               } 
                // somehow, user was able to display out-of-range address.  Most likely to occur between
                // stack base and Kernel.  Also text segment with self-modifying-code setting off.
                  catch (AddressErrorException aee) {
                     return;
                  }
            }// end synchronized block
            int valueBase = Globals.getGui().getMainPane().getExecutePane().getValueDisplayBase();
            data[row][col] = NumberDisplayBaseChooser.formatIntNumber(val, valueBase); 
            fireTableCellUpdated(row, col);
            return;
         }
      
      
        /*
         * Update cell contents in table model.  Does not affect MIPS memory.
         */
         private void setDisplayAndModelValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
         }
      
       
      }  
      
   	
   	// Special renderer capable of highlighting cells by changing background color.
   	// Will set background to highlight color if certain conditions met.
   	
      class AddressCellRenderer extends DefaultTableCellRenderer { 
       
         public Component getTableCellRendererComponent(JTable table, Object value, 
                            boolean isSelected, boolean hasFocus, int row, int column) {									 
            JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, 
                                    isSelected, hasFocus, row, column);
         	
            cell.setHorizontalAlignment(SwingConstants.RIGHT);
            Number
                    rowFirstAddress = Binary.stringToNumber(table.getValueAt(row,ADDRESS_COLUMN).toString());
            if (settings.getDataSegmentHighlighting() && addressHighlighting  && isEq(rowFirstAddress,addressRowFirstAddress)
            		&& isEq(column, addressColumn)) {
               cell.setBackground( settings.getColorSettingByPosition(Settings.DATASEGMENT_HIGHLIGHT_BACKGROUND) );
               cell.setForeground( settings.getColorSettingByPosition(Settings.DATASEGMENT_HIGHLIGHT_FOREGROUND) );
               cell.setFont( settings.getFontByPosition(Settings.DATASEGMENT_HIGHLIGHT_FONT) );
            } 
            else if (row%2==0) {
               cell.setBackground( settings.getColorSettingByPosition(Settings.EVEN_ROW_BACKGROUND) );
               cell.setForeground( settings.getColorSettingByPosition(Settings.EVEN_ROW_FOREGROUND) );
               cell.setFont( settings.getFontByPosition(Settings.EVEN_ROW_FONT) );
            } 
            else {
               cell.setBackground( settings.getColorSettingByPosition(Settings.ODD_ROW_BACKGROUND) );
               cell.setForeground( settings.getColorSettingByPosition(Settings.ODD_ROW_FOREGROUND) );				
               cell.setFont( settings.getFontByPosition(Settings.ODD_ROW_FONT) );
            }
            return cell;
         }  
      
      }
       ///////////////////////////////////////////////////////////////////
   	 //
   	 // JTable subclass to provide custom tool tips for each of the
   	 // text table column headers. From Sun's JTable tutorial.
   	 // http://java.sun.com/docs/books/tutorial/uiswing/components/table.html
   	 //
      private class MyTippedJTable extends JTable {
         MyTippedJTable(DataTableModel m) {
            super(m);
         }       
         
         private String[] columnToolTips = {
               /* address  */ "Base MIPS memory address for this row of the table.",
               /* value +0 */ "32-bit value stored at base address for its row.",
            	/* value +n */ "32-bit value stored ",
            	/* value +n */ " Numbers beyond base address for its row."
               };
         	
          //Implement table header tool tips. 
         protected JTableHeader createDefaultTableHeader() {
            return 
               new JTableHeader(columnModel) {
                  public String getToolTipText(MouseEvent e) {
                     String tip = null;
                     java.awt.Point p = e.getPoint();
                     int index = columnModel.getColumnIndexAtX(p.x);
                     int realIndex = columnModel.getColumn(index).getModelIndex();
                     return (realIndex < 2) ?  columnToolTips[realIndex]
                                        : columnToolTips[2]+((realIndex-1)*4)+columnToolTips[3];
                  }
               };
         }
      }
   	
   	///////////////////////////////////////////////////////////////////////
   	//
   	//  The Prev button (left arrow) scrolls downward through the
   	//  selected address range.  It is a RepeatButton, which means
   	//  if the mouse is held down on the button, it will repeatedly
   	//  fire after an initial delay.  Allows rapid scrolling.
   	//  DPS 20 July 2008
      private class PrevButton extends RepeatButton {
         public PrevButton(Icon ico) {
            super(ico);
            this.setInitialDelay(500);  // 500 milliseconds hold-down before firing
            this.setDelay(60);          // every 60 milliseconds after that
            this.addActionListener(this);
         }
      	// This one will respond when either timer goes off or button lifted.
         public void actionPerformed(ActionEvent ae) {
            firstAddress = sub(firstAddress, PREV_NEXT_CHUNK_SIZE);
            firstAddress = setFirstAddressAndPrevNextButtonEnableStatus(firstAddress);
            updateModelForMemoryRange(firstAddress);
         }
      }//////////////////////////////////////////////////////////////////////
   	
   	///////////////////////////////////////////////////////////////////////
   	//
   	//  The Next button (right arrow) scrolls upward through the
   	//  selected address range.  It is a RepeatButton, which means
   	//  if the mouse is held down on the button, it will repeatedly
   	//  fire after an initial delay.  Allows rapid scrolling.
   	//  DPS 20 July 2008      	
      private class NextButton extends RepeatButton {
         public NextButton(Icon ico) {
            super(ico);
            this.setInitialDelay(500);  // 500 milliseconds hold-down before firing
            this.setDelay(60);          // every 60 milliseconds after that
            this.addActionListener(this);
         }
      	// This one will respond when either timer goes off or button lifted.
         public void actionPerformed(ActionEvent ae) {
            firstAddress = GenMath.add(firstAddress, PREV_NEXT_CHUNK_SIZE);
            firstAddress = setFirstAddressAndPrevNextButtonEnableStatus(firstAddress);
            updateModelForMemoryRange(firstAddress);
         }
      }//////////////////////////////////////////////////////////////////////
   	
		public String getThisTitle() {
			return getTitle();
		}
		public void setThisTitle(String title) {
			setTitle(title);
		}
   }
