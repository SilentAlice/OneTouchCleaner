package com.xjtu.onetouchcleaner.app;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.NavUtils;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.xjtu.onetouchcleaner.Constants;
import com.xjtu.onetouchcleaner.R;

public class AppActivity extends Activity implements OnItemClickListener, Runnable{

/* Attributes *****************************************************************************/	
	private ListView softlist = null;
	private PackageManager mPackageMange;
	private List<ResolveInfo> mAllApps;
	ProgressDialog pd;
	private AppHandler handler;
	private SoftwareAdapter sfAdapter;
	private HashMap<String, Boolean> isSelected;
	private GestureDetector gestureDetector;
	
	
/* Override functions ************************************************************************/
	// OnCreate()
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		setContentView(R.layout.activity_app);
		// Show the Up button in the action bar.
		setupActionBar();
		
	/* Initialization *******************************************************************/
		gestureDetector = new GestureDetector(new AppGestureDetector());
		isSelected = AppStorage.getCacheCleaned();
		((TextView)findViewById(R.id.app_software_tv)).setText(R.string.app_cache_clean);
		softlist = (ListView) findViewById(R.id.app_software_list);
		mPackageMange = this.getPackageManager();
		handler = new AppHandler(this);
		pd = prepareDialog(this, getString(R.string.app_dialog_collecting),
				getString(R.string.app_dialog_wait), false);
		
		Thread thread = new Thread(this);
		pd.show();
		thread.start();
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(gestureDetector.onTouchEvent(event))
			return true;
		else
			return false;
	}

	// Create options Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			super.onBackPressed();
			break;
		
		case R.id.app_menu_one_clear:
			AppStorage.clearCache = true;
			AppStorage.clearData = true;
			AppCleaner cleaner = new AppCleaner(handler, this);
			Thread tempThread = new Thread(cleaner);
			pd = prepareDialog(this, getString(R.string.app_dialog_clearing),
					getString(R.string.app_dialog_wait), false);
			pd.show();
			tempThread.start();
			break;
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// Run()
	@Override
	public void run() {
		bindMsg();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		Message msg = new Message();
		msg.arg1 = Constants.app.APP_LIST_PREPARE_FINISHED;
		handler.sendMessage(msg);
	}
	
	// onItemClick()
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		ResolveInfo res = mAllApps.get(position);
		String pkg = res.activityInfo.packageName;
		
		Intent intent = new Intent();
		// target device should be above API 14
		final int apiLevel = Build.VERSION.SDK_INT;
		if(apiLevel >= 9) {
			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts("package", pkg, null);
			intent.setData(uri);
		}
		startActivity(intent);
	}
	

/* ClickEvent **************************************************************/
	public void onSelectReverse(View v) {
		Iterator<Entry<String,Boolean>> iter = isSelected.entrySet().iterator();
		Entry<String, Boolean> tempEntry;
		while(iter.hasNext()) {
			tempEntry = iter.next();
			if(tempEntry.getValue())
				isSelected.put(tempEntry.getKey(), false);
			else
				isSelected.put(tempEntry.getKey(), true);
		}
		sfAdapter.notifyDataSetChanged();
	}
	
	public void onSelectAll(View v) {
		Iterator<String> iter = isSelected.keySet().iterator();
		while(iter.hasNext()) {
			isSelected.put(iter.next(),true);
		}
		sfAdapter.notifyDataSetChanged();
	}

	public void onSelectNone(View v) {
		Iterator<String> iter = isSelected.keySet().iterator();
		while(iter.hasNext()) {
			isSelected.put(iter.next(),false);
		}
		sfAdapter.notifyDataSetChanged();
	}
	
	public void onSelectMode(View v) {
		if(((TextView)findViewById(R.id.app_software_tv)).getText().equals(getString(R.string.app_cache_clean))) {
			((TextView)findViewById(R.id.app_software_tv)).setText(R.string.app_data_clean);
			isSelected = AppStorage.getDataCleaned();
			sfAdapter.setIsSelected(isSelected);
			sfAdapter.notifyDataSetChanged();
		} else {
			((TextView)findViewById(R.id.app_software_tv)).setText(R.string.app_cache_clean);
			isSelected = AppStorage.getCacheCleaned();
			sfAdapter.setIsSelected(isSelected);
			sfAdapter.notifyDataSetChanged();
		}
			
		
	}
/* Help Functions ************************************************************/
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	// Bind messages with list
	private void bindMsg() {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mAllApps = mPackageMange.queryIntentActivities(mainIntent, 0);
		sfAdapter = new SoftwareAdapter(this, mAllApps);
		softlist.setAdapter(sfAdapter);
		softlist.setOnItemClickListener(this);
	}
	
	// Prepare a simple dialog
	private ProgressDialog prepareDialog(Context context, CharSequence title, CharSequence message, boolean cancelable) {
		ProgressDialog pd = new ProgressDialog(context);
		pd.setCancelable(cancelable);
		pd.setTitle(title);
		pd.setMessage(message);
		return pd;
	}

/* Behaviors *********************************************************************/
	public HashMap<String, Boolean> getIsSelected() {
		return isSelected;
	}
	
	
/* Inner Class ******************************************************************/
	public class AppGestureDetector extends SimpleOnGestureListener {

		// Constants
			private static final int MIN_DISTANCE = 120;
			private static final int MAX_OFF_PATH = 250;
			private static final int THRESHOLD_VELOCITY = 200;
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				
				if(e1.getX() - e2.getX() > MIN_DISTANCE && Math.abs(velocityX)>THRESHOLD_VELOCITY) {
					// to left
					((TextView)findViewById(R.id.app_software_tv)).setText(R.string.app_cache_clean);
					isSelected = AppStorage.getCacheCleaned();
					sfAdapter.setIsSelected(isSelected);
					sfAdapter.notifyDataSetChanged();
					return super.onFling(e1, e2, velocityX, velocityY);
				}
				
				if(e2.getX()-e1.getX() > MIN_DISTANCE && Math.abs(velocityX)>THRESHOLD_VELOCITY) {
					// to right
					((TextView)findViewById(R.id.app_software_tv)).setText(R.string.app_data_clean);
					isSelected = AppStorage.getDataCleaned();
					sfAdapter.setIsSelected(isSelected);
					sfAdapter.notifyDataSetChanged();
					return super.onFling(e1, e2, velocityX, velocityY);
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
			
		}

}



