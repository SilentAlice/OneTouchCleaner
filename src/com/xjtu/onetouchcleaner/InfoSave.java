package com.xjtu.onetouchcleaner;

import java.util.Iterator;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.xjtu.onetouchcleaner.app.AppStorage;
import com.xjtu.onetouchcleaner.call.CallStorage;
import com.xjtu.onetouchcleaner.sms.SMSStorage;

public class InfoSave implements Runnable {

	private AllDBAdapter dbAdapter;
	private ContentValues contentValue;
	private MainActivity context;
	private Handler handler;
	public InfoSave(MainActivity context, Handler handler) {
		this.context = context;
		dbAdapter = new AllDBAdapter(context);
		contentValue = new ContentValues();
		this.handler = handler;
	}

	@Override
	public void run() {
		// open database
		dbAdapter.open();
		dbAdapter.cleanAllTables();
		
	/* Begin Saving Configurations **************************************/
		// Save config(MainStorage)
		configInsert("SMSClean", MainStorage.bSMSClean);
		configInsert("CallClean", MainStorage.bCallClean);
		configInsert("AppClean", MainStorage.bAppClean);
		configInsert("OtherClean", MainStorage.bOtherClean);

		// Save config(SMSStorage)
		configInsert("SMSDaysBefore", SMSStorage.bDaysBefore);
		configInsert("SMSDaysBetween", SMSStorage.bDaysBetween);
		configInsert("SMSEnableFilter", SMSStorage.bEnableFilter);
		configInsert("SMSStrange", SMSStorage.bStrangeSMS);
		configInsert("SMSdaysBeforeNum", String.valueOf(SMSStorage.daysBeforeNum));
		if(SMSStorage.cal1 == null)
			configInsert("SMSCal1", "null");
		else
			configInsert("SMSCal1", String.valueOf(SMSStorage.cal1.getTimeInMillis()));
		if(SMSStorage.cal2 == null)
			configInsert("SMSCal2", "null");
		else
			configInsert("SMSCal2", String.valueOf(SMSStorage.cal2.getTimeInMillis()));
		
		// Save call config(CallStorage)
		configInsert("CallDaysBefore", CallStorage.bDaysBefore);
		configInsert("CallDaysBetween", CallStorage.bDaysBetween);
		configInsert("CallEnableFilter", CallStorage.bEnableFilter);
		configInsert("CallStrange", CallStorage.bStrangeCall);
		configInsert("CalldaysBeforeNum", String.valueOf(CallStorage.daysBeforeNum));
		if(CallStorage.cal1 == null)
			configInsert("CallCal1", "null");
		else
			configInsert("CallCal1", String.valueOf(CallStorage.cal1.getTimeInMillis()));
		if(CallStorage.cal2 == null)
			configInsert("CallCal2", "null");
		else
			configInsert("CallCal2", String.valueOf(CallStorage.cal2.getTimeInMillis()));
		
		// clearCache
		configInsert("AppClearCache", AppStorage.clearCache);
		// clearData
		configInsert("AppClearData", AppStorage.clearData);
//		msg.arg1 = Constants.main.MAIN_CONFIG_SAVE_FIN;
//		handler.sendMessage(msg);
		// Save other config(OtherStorage)	
		// Finish saving config 
			
	/* Begin Saving Maps **************************************************/
		// Save SMSReserveMap
		Iterator<Entry<String, String>> iter = SMSStorage.reserveMap.entrySet().iterator();
		Entry<String, String> entry;
		while (iter.hasNext()) {
			entry = iter.next();
			contentValue.clear();
			contentValue.put(Constants.database.FILTER_NAME, entry.getKey());
			contentValue.put(Constants.database.FILTER_NUMBER, entry.getValue());
			contentValue.put(Constants.database.FILTER_TYPE, "SMSR");
			dbAdapter.insertValue(Constants.database.DATABASE_TABLE_FILTER, contentValue);
		}
		
		// Save SMSDeleteMap
		iter = SMSStorage.deleteMap.entrySet().iterator();
		while (iter.hasNext()) {
			entry = iter.next();
			contentValue.clear();
			contentValue.put(Constants.database.FILTER_NAME, entry.getKey());
			contentValue.put(Constants.database.FILTER_NUMBER, entry.getValue());
			contentValue.put(Constants.database.FILTER_TYPE, "SMSD");
			dbAdapter.insertValue(Constants.database.DATABASE_TABLE_FILTER, contentValue);
		}
		
		// Save CallReserveMap
		iter = CallStorage.reserveMap.entrySet().iterator();
		while (iter.hasNext()) {
			entry = iter.next();
			contentValue.clear();
			contentValue.put(Constants.database.FILTER_NAME, entry.getKey());
			contentValue.put(Constants.database.FILTER_NUMBER, entry.getValue());
			contentValue.put(Constants.database.FILTER_TYPE, "CallR");
			dbAdapter.insertValue(Constants.database.DATABASE_TABLE_FILTER, contentValue);
		}
		
		// Save CallDeleteMap
		iter = CallStorage.deleteMap.entrySet().iterator();
		while (iter.hasNext()) {
			entry = iter.next();
			contentValue.clear();
			contentValue.put(Constants.database.FILTER_NAME, entry.getKey());
			contentValue.put(Constants.database.FILTER_NUMBER, entry.getValue());
			contentValue.put(Constants.database.FILTER_TYPE, "CallD");
			dbAdapter.insertValue(Constants.database.DATABASE_TABLE_FILTER, contentValue);
		}
		
		// Save App Info
		Iterator<Entry<String, Boolean>> cacheIter;
		Iterator<Entry<String, Boolean>> dataIter;
		Entry<String, Boolean> cacheEntry;
		Entry<String, Boolean> dataEntry;
		
		// Save AppMap
		cacheIter = AppStorage.getCacheCleaned().entrySet().iterator();
		dataIter = AppStorage.getDataCleaned().entrySet().iterator();
		while(cacheIter.hasNext()) {
			cacheEntry = cacheIter.next();
			dataEntry = dataIter.next();
			if(cacheEntry.getValue() || dataEntry.getValue()) {
				contentValue.clear();
				contentValue.put(Constants.database.APPLIST_PATH, cacheEntry.getKey());
				contentValue.put(Constants.database.APPLIST_ISCACHE, cacheEntry.getValue());
				contentValue.put(Constants.database.APPLIST_ISDATA, dataEntry.getValue());
				dbAdapter.insertValue(Constants.database.DATABASE_TABLE_APPLIST, contentValue);
			}
		}
		
		dbAdapter.close();
		Message msg = new Message();
		msg.arg1 = Constants.main.MAIN_SAVE_FINISHED;
		handler.sendMessage(msg);

	}

	private long configInsert(String name, String value) {
		contentValue.clear();
		contentValue.put("name", name);
		contentValue.put("value", value);
		return dbAdapter.insertValue(Constants.database.DATABASE_TABLE_CONFIG, contentValue);
	}
	
	private long configInsert(String name, boolean value) {
		contentValue.clear();
		contentValue.put("name", name);
		contentValue.put("value", value);
		return dbAdapter.insertValue(Constants.database.DATABASE_TABLE_CONFIG, contentValue);
	}

}
