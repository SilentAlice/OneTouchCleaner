package com.xjtu.onetouchcleaner.app;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xjtu.onetouchcleaner.Constants;

public class AppCleaner implements Runnable {

/* Attributes ***************************************************************************/	
	private HashMap<String, Boolean> cacheCleaned;
	private HashMap<String, Boolean> dataCleaned;
	private boolean clearCache;
	private boolean clearData;
	private Handler handler;
	private Context context;
	
/* Constructors ***************************************************************************/	
	
	// Must confirm the type(cache or data) Constructor
	public AppCleaner(Handler handler, Context context) {
		cacheCleaned = AppStorage.getCacheCleaned();
		dataCleaned = AppStorage.getDataCleaned();
		this.clearCache = AppStorage.clearCache;
		this.clearData = AppStorage.clearData;
		this.handler = handler;
		this.context = context;
	}
	
/* Public Methods **********************************************************************************/
	@Override
	public void run() {
		if(clearCache)
			clearCache();
		if(clearData) {
			clearData();
		}
		
		Message msg = new Message();
		msg.arg1 = Constants.app.APP_CLEANER_FINISHED;
		handler.sendMessage(msg);
	}
	
/* Help Functions ********************************************************************************/
	private void clearCache() {
		// File[] subFiles;
		File fToBeCleared;
		String tmpStr;
		// Prepare to traverse the hashmap
		Iterator<Entry<String, Boolean>> iter = cacheCleaned.entrySet().iterator();
		Map.Entry<String, Boolean> entry = null;
		
		// traverse the hashmap
		while(iter.hasNext()) {
			// get each entry
			entry = iter.next();
			// this app is selected to be cleaned
			if(entry.getValue() == true) {
				fToBeCleared = new File(entry.getKey() + "/cache");
				// Judge if the file exists. Should always be true!
				if(fToBeCleared.exists()) {
					suDeleteAll(fToBeCleared.getAbsolutePath());
				}
				
				fToBeCleared = new File("/mnt/sdcard/Android/" + entry.getKey().substring(5)+"/cache");
				if(fToBeCleared.exists()) {
					suDeleteAll(fToBeCleared.getAbsolutePath());
				}
			}
		}
	
	}
	
	private void clearData() {
		String[] subFileNames;
		File fToBeCleared;
		// Prepare to traverse the hashmap
		Iterator<Entry<String, Boolean>> iter = dataCleaned.entrySet().iterator();
		Map.Entry<String, Boolean> entry = null;
		
		// traverse the hashmap
		while(iter.hasNext()) {
			// get each entry
			entry = iter.next();
			// this app is selected to be cleaned
			if(entry.getValue() == true) {
				fToBeCleared = new File(entry.getKey());
				if(fToBeCleared.exists()) {
					subFileNames = getSubFileNames(fToBeCleared.getAbsolutePath());
					if (subFileNames != null)
						for(String filename:subFileNames)
							if(!(filename.equalsIgnoreCase("lib") || filename.equals("")))
								suDeleteAll(fToBeCleared.getAbsolutePath() + "/" + filename);
				}
			
				fToBeCleared = new File("/mnt/sdcard/Android/" + entry.getKey().substring(5));
				if(fToBeCleared.exists()) {
					suDeleteAll(fToBeCleared.getAbsolutePath());
				}
			}
		}
	}
	
	/* This Function will delete all the subfiles and subdirectories
	 * (INCLUDING ITSELF!!)
	 * Can't used in directory/file that need root access */
	private static void deleteAll(File file) {
		   
		if(file.isFile() || file.list().length ==0) {
			// empty directory or it's a file
			file.delete();  
		} else {
			// this directory has subdirectory or subfiles
			File[] subfiles = file.listFiles();
			for (int i = 0; i < subfiles.length; i++) {  
				// delete all subfiles and empty subdirectories
				deleteAll(subfiles[i]);
			}
			// delete itself
			file.delete();
		}
			
	}  

	// delete the give file with root access
	private static boolean suDeleteAll(String path) {
		try {
			// Perform su to get root privileges  
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("rm -r "+ path +"\n");
            os.writeBytes("exit\n");
            /*String info = (new BufferedReader(new InputStreamReader(process.getInputStream()))).readLine();
            Log.e("InputStream", info);
            String out = new String();
            new BufferedWriter(new OutputStreamWriter(process.getOutputStream())).write(out);
            Log.e("OutputStream", out);*/
            String err = (new BufferedReader(new InputStreamReader(process.getErrorStream()))).readLine();
            os.flush();

            try{
	            if(process.waitFor() != 0 || (!"".equals(err) && null != err))
	            {
	                Log.e("suDeleteAll", err);
	                return false;
	            }
            } catch (InterruptedException e) {
            	Log.e("suDeleteAll", "not rooted");
            	return false;
            }
            // delete Successfully
			return true;
		} catch (IOException e) {
			Log.e("suDeleteAll", e.toString());
			return false;
		}
	}
	
	// get sub files names of the given path
	private String[] getSubFileNames(String path) {
		try {
			// Perform su to get root privileges
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			
			// to get the subfiles list
			os.writeBytes("ls "+ path +"\n");
            os.writeBytes("exit\n");
            
            // to store subFileNames
            StringBuffer inputStrBuff = new StringBuffer();
            String tempStr;
            BufferedReader is = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            while(true) {
            	tempStr = is.readLine();
            	if(tempStr == null)
            		break;
            	else {
            		inputStrBuff.append(tempStr);
            		inputStrBuff.append("/");	// '/' is used in division
            	}
            }
            /*
            String out = new String();
            new BufferedWriter(new OutputStreamWriter(process.getOutputStream())).write(out);
            Log.e("OutputStream", out);*/
            String err = (new BufferedReader(new InputStreamReader(process.getErrorStream()))).readLine();
            os.flush();
            try{
	            if(process.waitFor() != 0 || (!"".equals(err) && null != err))
	            {
	                Log.e("getSubFiles", err);
	                return null;
	            }
            } catch (InterruptedException e) {
            	Log.e("getSubFiles", "not rooted");
            	return null;
            }
            // get subfile names Successfully
			return inputStrBuff.toString().split("/");
			
		} catch (IOException e) {
			Log.e("getSubFiles", e.toString());
			return null;
		}
	}


}
