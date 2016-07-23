package com.xjtu.onetouchcleaner;

import com.xjtu.onetouchcleaner.app.AppStorage;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

public class OneKeyClearSettingsActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		((CheckBox)findViewById(R.id.setting_sms_cb)).setChecked(MainStorage.bSMSClean);
		((CheckBox)findViewById(R.id.setting_call_cb)).setChecked(MainStorage.bCallClean);
		((CheckBox)findViewById(R.id.setting_cache_cb)).setChecked(AppStorage.clearCache && MainStorage.bAppClean);
		((CheckBox)findViewById(R.id.setting_data_cb)).setChecked(AppStorage.clearData && MainStorage.bAppClean);
		((CheckBox)findViewById(R.id.setting_other_cb)).setChecked(MainStorage.bOtherClean);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	protected void onPause() {
		MainStorage.bSMSClean = ((CheckBox)findViewById(R.id.setting_sms_cb)).isChecked();
		MainStorage.bCallClean = ((CheckBox)findViewById(R.id.setting_call_cb)).isChecked();
		AppStorage.clearCache = ((CheckBox)findViewById(R.id.setting_cache_cb)).isChecked();
		AppStorage.clearData = ((CheckBox)findViewById(R.id.setting_data_cb)).isChecked();
		MainStorage.bAppClean = AppStorage.clearCache || AppStorage.clearData;
		MainStorage.bOtherClean = ((CheckBox)findViewById(R.id.setting_other_cb)).isChecked();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
//		case android.R.id.home:
//			
//			Intent intent = new Intent(this,MainActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
//			overridePendingTransition(R.animator.in_form_left,R.animator.out_to_right);
//			return true;
		
		case R.id.app_settings_about:
			//show the about box
			Intent intent2 = new Intent(this,AboutActivity.class);
			startActivity(intent2);
			overridePendingTransition(R.animator.in_form_right,R.animator.out_to_left);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
