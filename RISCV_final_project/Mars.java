import javax.swing.UIManager;

import com.sun.xml.internal.ws.wsdl.parser.InaccessibleWSDLException;

import mars.mips.hardware.MemoryConfigurations;
import mars.util.Binary;
import mars.venus.GUI;

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
 * Portal to Mars
 * 
 * @author Pete Sanderson
 * @version March 2006
 **/

    public class Mars {
    //   public static void main(String[] args) {
    //     new mars.MarsLaunch(args);

      //}
    	
    	 public static void main(String args[]) {
    	        /* Set the Nimbus look and feel */
    	        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    	        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
    	         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
    	         */
    		 
    	       try {
    	            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    	        } catch (ClassNotFoundException ex) {
    	            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    	        } catch (InstantiationException ex) {
    	            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    	        } catch (IllegalAccessException ex) {
    	            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    	        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
    	            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    	        }
    	        //</editor-fold>

    	        /* Create and display the form */
    	        java.awt.EventQueue.invokeLater(new Runnable() {
    	            public void run() {
    	                new GUI("RISC-V Simulator").setVisible(true);
    	            }
    	        }); 	   
    		 
    	    }
   } 

