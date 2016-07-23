package com.xjtu.onetouchcleaner;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.xjtu.onetouchcleaner.app.AppStorage;
import com.xjtu.onetouchcleaner.call.CallStorage;
import com.xjtu.onetouchcleaner.sms.SMSStorage;

public class SplashActivity extends Activity {

	/* Attributes *****************************************************************/	
	private AllDBAdapter dbAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbAdapter = new AllDBAdapter(this);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_splash);
		
		//Date date1 = new Date();
		init();
		// Attempt to get root access
		// long duration = new Date().getTime() - date1.getTime();
		// duration = duration < Constants.splash.SPLASH_DISPLAY_LENGHT ? Constants.splash.SPLASH_DISPLAY_LENGHT - duration : 0L;
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent mainIntent = new Intent(SplashActivity.this,
						MainActivity.class);
				SplashActivity.this.startActivity(mainIntent);
				SplashActivity.this.finish();
			}
		}, 400);
		
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}
	
	
	// Help functions 
	private void init() {
		// open database
		dbAdapter.open();
		Cursor cur = dbAdapter.getConfigEntries();
		if(cur != null && cur.moveToFirst()) {
			// MainStorage
			MainStorage.bSMSClean = cur.getInt(1) == 1 ? true:false;
			cur.moveToNext();
			MainStorage.bCallClean = cur.getInt(1) == 1 ? true:false;
			cur.moveToNext();
			MainStorage.bAppClean = cur.getInt(1) == 1 ? true:false;
			cur.moveToNext();
			MainStorage.bOtherClean = cur.getInt(1) == 1 ? true:false;
			cur.moveToNext();
			
			// SMSStorage
			SMSStorage.bDaysBefore = cur.getInt(1) == 1 ? true:false;
			cur.moveToNext();
			SMSStorage.bDaysBetween = cur.getInt(1) == 1 ? true:false;
			cur.moveToNext();
			SMSStorage.bEnableFilter = cur.getInt(1) == 1 ? true:false;
			cur.moveToNext();
			SMSStorage.bStrangeSMS = cur.getInt(1) == 1? true:false;
			cur.moveToNext();
			SMSStorage.daysBeforeNum = cur.getInt(1);
			cur.moveToNext();
			if(cur.getString(1).equalsIgnoreCase("null"))
				SMSStorage.cal1 = null;
			else {
				SMSStorage.cal1 = Calendar.getInstance();
				SMSStorage.cal1.setTimeInMillis(cur.getLong(1));
			}
			cur.moveToNext();
			if(cur.getString(1).equalsIgnoreCase("null"))
				SMSStorage.cal2 = null;
			else {
				SMSStorage.cal2 = Calendar.getInstance();
				SMSStorage.cal2.setTimeInMillis(cur.getLong(1));
			}
			cur.moveToNext();
			
			// CallStorage
			CallStorage.bDaysBefore = cur.getInt(1) == 1 ? true:false;
			cur.moveToNext();
			CallStorage.bDaysBetween = cur.getInt(1) == 1 ? true:false;
			cur.moveToNext();
			CallStorage.bEnableFilter = cur.getInt(1) == 1 ? true:false;
			cur.moveToNext();
			CallStorage.bStrangeCall = cur.getInt(1) == 1? true:false;
			cur.moveToNext();
			CallStorage.daysBeforeNum = cur.getInt(1);
			cur.moveToNext();
			if(cur.getString(1).equalsIgnoreCase("null"))
				CallStorage.cal1 = null;
			else {
				CallStorage.cal1 = Calendar.getInstance();
				CallStorage.cal1.setTimeInMillis(cur.getLong(1));
			}
			cur.moveToNext();
			if(cur.getString(1).equalsIgnoreCase("null"))
				CallStorage.cal2 = null;
			else {
				CallStorage.cal2 = Calendar.getInstance();
				CallStorage.cal2.setTimeInMillis(cur.getLong(1));
			}
			cur.moveToNext();
			
			// AppStorage
			AppStorage.clearCache = cur.getInt(1) == 1? true:false;
			cur.moveToNext();
			AppStorage.clearData = cur.getInt(1) == 1? true:false;
			// Config End
		} 
		else {
			Log.e("InfoInit", "ConfigInitError");
		}
		
		// InitSMSReserveMap
		cur = dbAdapter.getFilterEntries("SMSR");
		if(cur != null && cur.moveToFirst()) {
			do {
				SMSStorage.reserveMap.put(cur.getString(0), cur.getString(1));
			} while (cur.moveToNext());
		}
		
		// InitSMSDeleteMap
		cur = dbAdapter.getFilterEntries("SMSD");
		if(cur != null && cur.moveToFirst()) {
			do {
				SMSStorage.deleteMap.put(cur.getString(0), cur.getString(1));
			} while (cur.moveToNext());
		}
		
		// InitCallReserveMap
		cur = dbAdapter.getFilterEntries("CallR");
		if(cur != null && cur.moveToFirst()) {
			do {
				CallStorage.reserveMap.put(cur.getString(0), cur.getString(1));
			} while (cur.moveToNext());
		}
		
		// InitCallDeleteMap
		cur = dbAdapter.getFilterEntries("CallD");
		if(cur != null && cur.moveToFirst()) {
			do {
				CallStorage.deleteMap.put(cur.getString(0), cur.getString(1));
			} while (cur.moveToNext());
		}
		
		// InitAppListMap
		cur = dbAdapter.getAppListEntries();
		if(cur != null && cur.moveToFirst()) {
			HashMap<String, Boolean> map1 = AppStorage.getCacheCleaned();
			HashMap<String, Boolean> map2 = AppStorage.getDataCleaned();
			do {
				map1.put(cur.getString(0), cur.getInt(1) == 1 ? true: false);
				map2.put(cur.getString(0), cur.getInt(2) == 1 ? true: false);
			} while (cur.moveToNext());
		}
		
		dbAdapter.close();
	}
}
