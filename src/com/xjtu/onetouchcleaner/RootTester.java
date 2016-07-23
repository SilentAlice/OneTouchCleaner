package com.xjtu.onetouchcleaner;

import java.io.DataOutputStream;
import java.io.IOException;

public class RootTester {

	static private boolean isRooted = false;
	static private boolean isTested = false; 
/* Methods **********************************************************/
	static public boolean isRooted() {
		return isRooted;
	}
	
	static public void setRooted(boolean value) {
		isRooted = value;
	}
	
	static public boolean isTested() {
		return isTested;
	}
	
	public void testRoot() {
		Process p;
		try {
			// Perform su to get root privileges  
			p = Runtime.getRuntime().exec("su");
				
			// Attempt to write a file to a root-only  
			try {
				Thread.sleep(1700);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			DataOutputStream os = new DataOutputStream(p.getOutputStream());   
			os.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");
			// Close the terminal  
			os.writeBytes("exit\n");   
			os.flush();   
			try {   
			   p.waitFor();
			   if (p.exitValue() != 255) {   
				   RootTester.setRooted(true);
			   }   
			   else {   
				   RootTester.setRooted(false);
			   }   
			} catch (InterruptedException e) {   
				RootTester.setRooted(false);    
			}   
		} catch (IOException e) {   
			RootTester.setRooted(false);
		}
		
		isTested = true;
	}
}
