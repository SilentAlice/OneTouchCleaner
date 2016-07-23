package com.xjtu.onetouchcleaner.app;

import java.util.HashMap;

// This class is used to store app selection state
public class AppStorage{
	
	// TODO get data from database...
	private static HashMap<String, Boolean> cacheCleaned = new HashMap<String, Boolean>();
	private static HashMap<String, Boolean>	dataCleaned = new HashMap<String, Boolean>();
	public static boolean clearCache, clearData = false; 

/* Public Methods *************************************************************/
	public static HashMap<String, Boolean> getCacheCleaned() {
		return cacheCleaned;
	}
	
	public static HashMap<String, Boolean> getDataCleaned() {
		return dataCleaned;
	}

	public static void setCacheCleaned(HashMap<String, Boolean> cacheCleaned) {
		AppStorage.cacheCleaned = cacheCleaned;
	}
	
	public static void setDataCleaned(HashMap<String, Boolean> dataCleaned) {
		AppStorage.dataCleaned = dataCleaned;
	}
	
	public static Boolean cachePut(String key, Boolean value) {
		return cacheCleaned.put(key, value);
	}
	
	public static Boolean dataPut(String key, Boolean value) {
		return dataCleaned.put(key, value);
	}

}
