package mars.riscv.instructions;

import mars.riscv.instructions.ecalls.*;
import mars.*;
import mars.util.*;
import java.util.*;

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


/****************************************************************************/
/* This class provides functionality to bring external Ecall definitions
 * into MARS.  This permits anyone with knowledge of the Mars public interfaces,
 * in particular of the Memory and Register classes, to write custom MIPS syscall
 * functions. This is adapted from the ToolLoader class, which is in turn adapted
 * from Bret Barker's GameServer class from the book "Developing Games In Java".
 */

class EcallLoader {

    private static final String CLASS_PREFIX = "mars.riscv.instructions.ecalls.";
    private static final String SYSCALLS_DIRECTORY_PATH = "mars/riscv/instructions/ecalls";
    private static final String SYSCALL_INTERFACE = "Ecall.class";
    private static final String SYSCALL_ABSTRACT = "AbstractEcall.class";
    private static final String CLASS_EXTENSION = "class";

    private ArrayList ecallList;

    /*
    *  Dynamically loads Syscalls into an ArrayList.  This method is adapted from
    *  the loadGameControllers() method in Bret Barker's GameServer class.
    *  Barker (bret@hypefiend.com) is co-author of the book "Developing Games
    *  in Java".  Also see the "loadMarsTools()" method from ToolLoader class.
    */
    void loadEcalls() {
        ecallList = new ArrayList();
        // grab all class files in the same directory as Ecall
        ArrayList candidates = FilenameFinder.getFilenameList(this.getClass( ).getClassLoader(),
                                          SYSCALLS_DIRECTORY_PATH, CLASS_EXTENSION);
        HashMap ecalls = new HashMap();
        for (Object candidate : candidates) {
           String file = (String) candidate;
           // Do not add class if already encountered (happens if run in MARS development directory)
           if (ecalls.containsKey(file))
               continue;
           else ecalls.put(file, file);

           if ((!file.equals(SYSCALL_INTERFACE)) && (!file.equals(SYSCALL_ABSTRACT))) {
               try {
                   // grab the class, make sure it implements Ecall, instantiate, add to list
                   String syscallClassName = CLASS_PREFIX + file.substring(0, file.indexOf(CLASS_EXTENSION) - 1);
                   Class clas = Class.forName(syscallClassName);
                   if (!Ecall.class.isAssignableFrom(clas))
                       continue;
                   Ecall ecall = (Ecall) clas.newInstance();
                   if (findEcall(ecall.getNumber()) == null) ecallList.add(ecall);
                   else
                       throw new Exception("Duplicate service number: " + ecall.getNumber() +
                               " already registered to " +
                               findEcall(ecall.getNumber()).getName());

               } catch (Exception e) {
                   System.out.println("Error instantiating Ecall from file " + file + ": " + e);
                   System.exit(0);
               }
           }
        }
        ecallList = processEcallNumberOverrides(ecallList);
    }

    // Will get any syscall number override specifications from MARS config file and
    // process them.  This will alter ecallList entry for affected names.
    private ArrayList processEcallNumberOverrides(ArrayList syscallList) {
        ArrayList overrides = new Globals().getSyscallOverrides();
        SyscallNumberOverride override;
        Ecall ecall;
        for (Object o : overrides) {
           override = (SyscallNumberOverride) o;
           boolean match = false;
           for (Object value : syscallList) {
               ecall = (Ecall) value;
               if (override.getName().equals(ecall.getName())) {
                   // we have a match to service name, assign new number
                   ecall.setNumber(override.getNumber());
                   match = true;
               }
           }
           if (!match) {
               System.out.println("Error: ecall name '" + override.getName() +
                       "' in config file does not match any name in ecall list");
               System.exit(0);
           }
        }
        // Wait until end to check for duplicate numbers.  To do so earlier
        // would disallow for instance the exchange of numbers between two
        // services.  This is N-squared operation but N is small.
        // This will also detect duplicates that accidently occur from addition
        // of a new Ecall subclass to the collection, even if the config file
        // does not contain any overrides.
         Ecall ecallA, ecallB;
         boolean duplicates = false;
            for (int i = 0; i < syscallList.size(); i++) {
                ecallA = (Ecall)syscallList.get(i);
                for (int j = i+1; j < syscallList.size(); j++) {
                   ecallB = (Ecall)syscallList.get(j);
                   if ( ecallA.getNumber() == ecallB.getNumber()) {
                      System.out.println("Error: ecalls "+ ecallA.getName()+" and "+
                            ecallB.getName()+" are both assigned same number "+ ecallA.getNumber());
                      duplicates = true;
                   }
                }
            }
         if (duplicates)
            System.exit(0);

         return syscallList;
    }

    /*
    * Method to find Ecall object associated with given service number.
    * Returns null if no associated object found.
    */
    Ecall findEcall(int number) {
        // linear search is OK since number of ecalls is small.
        Ecall service, match = null;
        if (ecallList ==null)
            loadEcalls();

        for (Object o : ecallList) {
           service = (Ecall) o;
           if (service.getNumber() == number)
               match = service;
        }
         return match;
    }
}
