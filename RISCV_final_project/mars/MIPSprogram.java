   package mars;
	
   import mars.assembler.*;
   import mars.simulator.*;
   import mars.mips.hardware.*;
	
   import java.util.*;
   import java.io.*;
   import javax.swing.*;

/*
Copyright (c) 2003-2006,  Pete Sanderson and Kenneth Vollmar

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
 * Internal representations of MIPS program.  Connects source, tokens and machine code.  Having
 * all these structures available facilitates construction of good messages,
 * debugging, and easy simulation.
 * 
 * @author Pete Sanderson
 * @version August 2003
 **/

    public class MIPSprogram {
   
   // See explanation of method inSteppedExecution() below.
      private boolean steppedExecution = false;
   
      private String filename;
      private ArrayList sourceList;
      private ArrayList tokenList;
      private ArrayList parsedList;
      private ArrayList machineList;
      private BackStepper backStepper;
      private SymbolTable localSymbolTable;
      private MacroPool macroPool;
      private ArrayList<SourceLine> sourceLineList;
		private Tokenizer tokenizer;
   
   /**
    * Produces list of source statements that comprise the program.
    * @return ArrayList of String.  Each String is one line of MIPS source code.
    **/
    
       public ArrayList getSourceList() {
         return sourceList;
      }
   
   /**
    * Set list of source statements that comprise the program.
    * @param sourceLineList ArrayList of SourceLine.  
	 * Each SourceLine represents one line of MIPS source code.
    **/
   	
       public void setSourceLineList(ArrayList<SourceLine> sourceLineList) { 
         this.sourceLineList = sourceLineList; 
         sourceList = new ArrayList();
         for (SourceLine sl : sourceLineList) {
            sourceList.add(sl.getSource());
         } 
      }
   
   /**
    * Retrieve list of source statements that comprise the program.
    * @return ArrayList of SourceLine.  
	 * Each SourceLine represents one line of MIPS source cod
    **/
   	
       public ArrayList<SourceLine> getSourceLineList() {
         return this.sourceLineList;
      }
   
   /**
    * Produces name of associated source code file.
    * @return File name as String. 
    **/
    
       public String getFilename() {
         return filename;
      }
   
   /**
    * Produces list of tokens that comprise the program.
    * @return ArrayList of TokenList.  Each TokenList is list of tokens generated by
    * corresponding line of MIPS source code.
    * @see TokenList
    **/
    
       public ArrayList getTokenList() {
         return tokenList;
      }
   
   /**
    * Retrieves Tokenizer for this program
    * @return Tokenizer
    **/   
    
       public Tokenizer getTokenizer() {
         return tokenizer;
      }	
		
   /**
    * Produces new empty list to hold parsed source code statements.
    * @return ArrayList of ProgramStatement.  Each ProgramStatement represents a parsed
    * MIPS statement.
    * @see ProgramStatement
    **/
    
       public ArrayList createParsedList() {
         parsedList = new ArrayList();
         return parsedList;
      }
   
   /**
    * Produces existing list of parsed source code statements.
    * @return ArrayList of ProgramStatement.  Each ProgramStatement represents a parsed
    * MIPS statement.
    * @see ProgramStatement
    **/
    
       public ArrayList getParsedList() {
         return parsedList;
      }
   
   /**
    * Produces list of machine statements that are assembled from the program.
    * @return ArrayList of ProgramStatement.  Each ProgramStatement represents an assembled
    * basic MIPS instruction.
    * @see ProgramStatement
    **/
    
       public ArrayList getMachineList() {
         return machineList;
      }
   
   
   /**
    * Returns BackStepper associated with this program.  It is created upon successful assembly.
    * @return BackStepper object, null if there is none.
    **/
    
       public BackStepper getBackStepper() {
         return backStepper;
      }
   
   /**
    * Returns SymbolTable associated with this program.  It is created at assembly time,
    * and stores local labels (those not declared using .globl directive).
    **/
    
       public SymbolTable getLocalSymbolTable() {
         return localSymbolTable;
      }
   
   /**
    * Returns status of BackStepper associated with this program.  
    * @return true if enabled, false if disabled or non-existant.
    **/
    
       public boolean backSteppingEnabled() {
         return (backStepper!=null && backStepper.enabled());
      }
   
   /**
    * Produces specified line of MIPS source program.
    * @param i Line number of MIPS source program to get.  Line 1 is first line.
    * @return Returns specified line of MIPS source.  If outside the line range,
    * it returns null.  Line 1 is first line.
    **/
    
       public String getSourceLine(int i) {
         if ( (i >= 1) && (i <= sourceList.size()) )
            return (String) sourceList.get(i-1);
         else
            return null;
      }
   
   
   /**
    * Reads MIPS source code from file into structure.  Will always read from file.
    * It is GUI responsibility to assure that source edits are written to file
    * when user selects compile or run/step options.
    * 
    * @param file String containing name of MIPS source code file.
    * @throws ProcessingException Will throw exception if there is any problem reading the file.
    **/
   
       public void readSource(String file) throws ProcessingException {
         this.filename = file;
         this.sourceList = new ArrayList();
         ErrorList errors = null;
         BufferedReader inputFile;
         String line;
         int lengthSoFar = 0;
         try {
            inputFile = new BufferedReader(new FileReader(file));
            line = inputFile.readLine();
            while (line != null) {
               sourceList.add(line);
               line = inputFile.readLine();
            }
         } 
             catch (Exception e) {
               errors = new ErrorList();
               errors.add(new ErrorMessage((MIPSprogram)null,0,0,e.toString()));
               throw new ProcessingException(errors);
            }
         return;
      }
   
   /**
    * Tokenizes the MIPS source program. Program must have already been read from file.
    * @throws ProcessingException Will throw exception if errors occured while tokenizing.
    **/
   
       public void tokenize() throws ProcessingException {
         this.tokenizer = new Tokenizer();
         this.tokenList = tokenizer.tokenize(this);
         this.localSymbolTable = new SymbolTable(this.filename); // prepare for assembly
         return;
      }
   
   /**
    * Prepares the given list of files for assembly.  This involves
    * reading and tokenizing all the source files.  There may be only one.
    * @param filenames  ArrayList containing the source file name(s) in no particular order
    * @param leadFilename String containing name of source file that needs to go first and 
    * will be represented by "this" MIPSprogram object.
    * @param exceptionHandler String containing name of source file containing exception
    * handler.  This will be assembled first, even ahead of leadFilename, to allow it to
    * include "startup" instructions loaded beginning at 0x00400000.  Specify null or
    * empty String to indicate there is no such designated exception handler.
    * @return ArrayList containing one MIPSprogram object for each file to assemble.
    * objects for any additional files (send ArrayList to assembler)
    * @throws ProcessingException Will throw exception if errors occured while reading or tokenizing.
    **/
   
       public ArrayList prepareFilesForAssembly(ArrayList filenames, String leadFilename, String exceptionHandler) throws ProcessingException {
         ArrayList MIPSprogramsToAssemble = new ArrayList();
         int leadFilePosition = 0;
         if (exceptionHandler != null && exceptionHandler.length() > 0) {
            filenames.add(0, exceptionHandler);
            leadFilePosition = 1;
         }
         for (int i=0; i<filenames.size(); i++) {
            String filename = (String) filenames.get(i);  
            MIPSprogram preparee = (filename.equals(leadFilename)) ? this : new MIPSprogram();
            preparee.readSource(filename);
            preparee.tokenize();
         	// I want "this" MIPSprogram to be the first in the list...except for exception handler
            if (preparee == this && MIPSprogramsToAssemble.size()>0) {
               MIPSprogramsToAssemble.add(leadFilePosition,preparee);
            } 
            else {
               MIPSprogramsToAssemble.add(preparee);
            }
         }
         return MIPSprogramsToAssemble;
      }
   
   /**
    * Assembles the MIPS source program. All files comprising the program must have 
    * already been tokenized.  Assembler warnings are not considered errors.
    * @param MIPSprogramsToAssemble ArrayList of MIPSprogram objects, each representing a tokenized source file.
    * @param extendedAssemblerEnabled A boolean value - true means extended (pseudo) instructions
    * are permitted in source code and false means they are to be flagged as errors.
    * @throws ProcessingException Will throw exception if errors occured while assembling.
    * @return ErrorList containing nothing or only warnings (otherwise would have thrown exception).
    **/
   
       public ErrorList assemble(ArrayList MIPSprogramsToAssemble, boolean extendedAssemblerEnabled)
              throws ProcessingException {   
         return assemble(MIPSprogramsToAssemble, extendedAssemblerEnabled, false);
      }
   	  
   /**
    * Assembles the MIPS source program. All files comprising the program must have 
    * already been tokenized.
    * @param MIPSprogramsToAssemble ArrayList of MIPSprogram objects, each representing a tokenized source file.
    * @param extendedAssemblerEnabled A boolean value - true means extended (pseudo) instructions
    * are permitted in source code and false means they are to be flagged as errors
    * @param warningsAreErrors A boolean value - true means assembler warnings will be considered errors and terminate
      the assemble; false means the assembler will produce warning message but otherwise ignore warnings.
    * @throws ProcessingException Will throw exception if errors occured while assembling.
    * @return ErrorList containing nothing or only warnings (otherwise would have thrown exception).
    **/
    
       public ErrorList assemble(ArrayList MIPSprogramsToAssemble, boolean extendedAssemblerEnabled,
              boolean warningsAreErrors) throws ProcessingException {
         this.backStepper = null;
         Assembler asm = new Assembler();
         this.machineList = asm.assemble(MIPSprogramsToAssemble, extendedAssemblerEnabled, warningsAreErrors);
         this.backStepper = new BackStepper();
         return asm.getErrorList();
      }
   
   
   /**
    * Simulates execution of the MIPS program. Program must have already been assembled.
    * Begins simulation at beginning of text segment and continues to completion.
    * @param breakPoints int array of breakpoints (PC addresses).  Can be null.
    * @return true if execution completed and false otherwise
    * @throws ProcessingException Will throw exception if errors occured while simulating.
    **/
    
       public boolean simulate(Number[] breakPoints) throws ProcessingException {
         return this.simulateFromPC(breakPoints, -1, null);
      }
   
   
   /**
    * Simulates execution of the MIPS program. Program must have already been assembled.
    * Begins simulation at beginning of text segment and continues to completion or
    * until the specified maximum number of steps are simulated.
    * @param maxSteps  maximum number of steps to simulate.
    * @return true if execution completed and false otherwise
    * @throws ProcessingException Will throw exception if errors occured while simulating.
    **/
    
       public boolean simulate(int maxSteps) throws ProcessingException {
         return this.simulateFromPC(null, maxSteps, null);
      }	
   
   /**
    * Simulates execution of the MIPS program. Program must have already been assembled.
    * Begins simulation at current program counter address and continues until stopped,
    * paused, maximum steps exceeded, or exception occurs.
    * @param breakPoints int array of breakpoints (PC addresses).  Can be null.
    * @param maxSteps maximum number of instruction executions.  Default -1 means no maximum.
    * @param a the GUI component responsible for this call (GO normally).  set to null if none.
    * @return true if execution completed and false otherwise
    * @throws ProcessingException Will throw exception if errors occured while simulating.
    **/	
       public boolean simulateFromPC(Number[] breakPoints, Number maxSteps, AbstractAction a) throws ProcessingException {
         steppedExecution = false;
         Simulator sim = Simulator.getInstance();
         return sim.simulate(this, RVIRegisters.getProgramCounter(), maxSteps, breakPoints, a);
      }
   
   
   
   /**
    * Simulates execution of the MIPS program. Program must have already been assembled.
    * Begins simulation at current program counter address and executes one step.
    * @param a the GUI component responsible for this call (STEP normally). Set to null if none.
    * @return true if execution completed and false otherwise
    * @throws ProcessingException Will throw exception if errors occured while simulating.
    **/
       public boolean simulateStepAtPC(AbstractAction a) throws ProcessingException {
         steppedExecution = true;
         Simulator sim = Simulator.getInstance();
         boolean done = sim.simulate(this, RVIRegisters.getProgramCounter(), 1, null,a);
         return done;
      }
   
   /** Will be true only while in process of simulating a program statement
   * in step mode (e.g. returning to GUI after each step).  This is used to
   * prevent spurious AccessNotices from being sent from Memory and Register
   * to observers at other times (e.g. while updating the data and register
   * displays, while assembling program's data segment, etc).
   */
       public boolean inSteppedExecution() {
         return steppedExecution;
      }
   
   /**
    * Instantiates a new {@link MacroPool} and sends reference of this
    * {@link MIPSprogram} to it
    * 
    * @return instatiated MacroPool
    * @author M.H.Sekhavat <sekhavat17@gmail.com>
    */
       public MacroPool createMacroPool() {
         macroPool = new MacroPool(this);
         return macroPool;
      }
   
   /**
    * Gets local macro pool {@link MacroPool} for this program
    * @return MacroPool
    * @author M.H.Sekhavat <sekhavat17@gmail.com>
    */	
       public MacroPool getLocalMacroPool() {
         return macroPool;
      }
   
   /**
    * Sets local macro pool {@link MacroPool} for this program
    * @param macroPool reference to MacroPool
    * @author M.H.Sekhavat <sekhavat17@gmail.com>
    */   
       public void setLocalMacroPool(MacroPool macroPool) {
         this.macroPool = macroPool;
      }
    
   }  // MIPSprogram
