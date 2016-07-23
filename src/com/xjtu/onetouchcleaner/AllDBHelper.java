package com.xjtu.onetouchcleaner;

import com.xjtu.onetouchcleaner.app.AppStorage;
import com.xjtu.onetouchcleaner.other.OtherStorage;
import com.xjtu.onetouchcleaner.sms.SMSStorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AllDBHelper extends SQLiteOpenHelper {

	private ContentValues contentValue;
	
	public AllDBHelper(Context context, String name, CursorFactory factory, int version) {
	      super(context, name, factory, version);
	      contentValue = new ContentValues();
	    }
		
		/* Behaviors **********************************************************/
		// Create db
		@Override
		public void onCreate(SQLiteDatabase db) {
			// Create needed database
			db.execSQL(AllDBAdapter.DATABASE_CREATE_APPLIST);
			db.execSQL(AllDBAdapter.DATABASE_CREATE_CONFIG);
			db.execSQL(AllDBAdapter.DATABASE_CREATE_FILTER);
			String strConfigDB = Constants.database.DATABASE_TABLE_CONFIG;
			// initiate database
			
			// initiate main config(MainStorage)
			// bSMSClean
			configInsert(db, "SMSClean", "0");
			// bCallClean
			configInsert(db, "CallClean", "0");
			// bAppClean
			configInsert(db, "AppClean", "0");
			// bOtherClean
			configInsert(db, "OtherClean", "0");

			// initiate sms config(SMSStorage)
			// bDaysBefore
			configInsert(db, "SMSDaysBefore", "0");
			// bDaysBetween
			configInsert(db, "SMSDaysBetween", "0");
			// bEnableFilter
			configInsert(db, "SMSEnableFilter", "0");
			// bStrangeSMS
			configInsert(db, "SMSStrange", "0");
			// daysBeforeNum
			configInsert(db, "SMSdaysBeforeNum", "-1");
			// cal1
			configInsert(db, "SMSCal1", "null");
			// cal2
			configInsert(db, "SMSCal2", "null");
			
			// initiate call config(CallStorage)
			// bDaysBefore
			configInsert(db, "CallDaysBefore", "0");
			// bDaysBetween
			configInsert(db, "CallDaysBetween", "0");
			// bEnableFilter
			configInsert(db, "CallEnableFilter", "0");
			// bStrangeCall
			configInsert(db, "CallStrange", "0");
			// daysBeforeNum
			configInsert(db, "CalldaysBeforeNum", "-1");
			// cal1
			configInsert(db, "CallCal1", "null");
			// cal2
			configInsert(db, "CallCal2", "null");
			
			// initiate app config(AppStorage)
			// clearCache
			configInsert(db, "AppClearCache", "0");
			// clearData
			configInsert(db, "AppClearData", "0");
			
			// initiate other config(OtherStorage)
			
			
			
		}
		// Update
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Log the version upgrade
			Log.w("AllDBAdapter", "Upgrading from version " + 
					oldVersion + " to " + 
					newVersion + ", which will destroy all old data");
			
			// 将现有的数据库升级到新版本
		    // 通过比较_oldVersion和_newVersion的值可以处理多个旧版本

		    // 最简单的情况就是删除旧表，创建新表.
			db.execSQL("DROP TABLE IF EXISTS " + Constants.database.DATABASE_TABLE_APPLIST);
			db.execSQL("DROP TABLE IF EXISTS " + Constants.database.DATABASE_TABLE_CONFIG);
			db.execSQL("DROP TABLE IF EXISTS " + Constants.database.DATABASE_TABLE_FILTER);
		    // Create a new one.
		    onCreate(db);
		}
		
		private long configInsert(SQLiteDatabase db, String name, String value) {
			contentValue.clear();
			contentValue.put("name", name);
			contentValue.put("value", value);
			return db.insert(Constants.database.DATABASE_TABLE_CONFIG, null, contentValue);
		}
}
