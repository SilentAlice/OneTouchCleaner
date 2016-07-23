package com.xjtu.onetouchcleaner;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.xjtu.onetouchcleaner.app.AppActivity;
import com.xjtu.onetouchcleaner.app.AppCleaner;
import com.xjtu.onetouchcleaner.call.CallActivity;
import com.xjtu.onetouchcleaner.call.CallCleaner;
import com.xjtu.onetouchcleaner.other.OtherActivity;
import com.xjtu.onetouchcleaner.other.ram.RAMCleaner;
import com.xjtu.onetouchcleaner.sms.SMSActivity;
import com.xjtu.onetouchcleaner.sms.SMSCleaner;

public class MainActivity extends Activity {

/* Override Functions ******************************************************/
    
	private MainHandler handler;
	private Message msg;
	long duration = 0;
	Date originalDate = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        msg = new Message();
        handler = new MainHandler(this);
        // Test if rooted
        	RootTester rootTester = new RootTester();
	        if(!RootTester.isTested()) {
	        	rootTester.testRoot();
		        if(RootTester.isRooted()) {
		        	Toast.makeText(this, R.string.rooted, Toast.LENGTH_SHORT).show();
		        }
		        else {
		        	Toast.makeText(this, R.string.not_rooted, Toast.LENGTH_SHORT).show();
		        	findViewById(R.id.btnAppClear).setEnabled(false);
		        }
        	}
    }

	@Override
	protected void onStart() {
		if(!RootTester.isRooted()) {
			findViewById(R.id.btnAppClear).setEnabled(false);
		}
		super.onStart();
	}

	// Create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case R.id.action_config:
			Intent intent = new Intent(MainActivity.this, OneKeyClearSettingsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.animator.in_form_right,R.animator.out_to_left);
			return super.onOptionsItemSelected(item);
			
		case R.id.action_save:
			new Thread(new InfoSave(this, handler)).start();
			return super.onOptionsItemSelected(item);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
/* ClickEvent ****************************************************************************/
	
	@Override
	public void onBackPressed() {
		Date tmpDate = new Date();
		if(originalDate == null) {
			originalDate = new Date();
			Toast.makeText(this, R.string.main_press_back, Toast.LENGTH_SHORT).show();
			return;
		}

		if(tmpDate.getTime() - originalDate.getTime() < 2000L) {
			super.onBackPressed();
		} else {
			Toast.makeText(this, R.string.main_press_back, Toast.LENGTH_SHORT).show();
			originalDate = tmpDate;
		}
		
	}

	// One Key Clear Button
	public void onBtnOneKeyClearClicked(View v) {
		if(MainStorage.bSMSClean) {
			new Thread(new SMSCleaner(handler, this)).start();
		}
		if(MainStorage.bCallClean)
			new Thread(new CallCleaner(handler, this)).start();
		if(MainStorage.bOtherClean)
			new Thread(new RAMCleaner(this, handler)).start();
		if(MainStorage.bAppClean) {
			new Thread(new AppCleaner(handler, this)).start();
		}
	}
	
	// App Clear Button
	public void onBtnAppClearClicked(View v) {
		Intent intent = new Intent(this, AppActivity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.in_form_right,R.animator.out_to_left);
		
	}
	
	// Call Clear Button
	public void onBtnCallClearClicked(View v) {
		Intent intent = new Intent(this, CallActivity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.in_form_right,R.animator.out_to_left);
	}
	
	// SMS Clear Button
	public void onBtnSMSClearClicked(View v) {
		Intent intent = new Intent(this, SMSActivity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.in_form_right,R.animator.out_to_left);
	}
    
	// Other Clear Button
    public void onBtnOtherClearClicked(View v) {
    	Intent intent = new Intent(this, OtherActivity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.in_form_right,R.animator.out_to_left);
    }
   
/* Other functions ***********************************************************************/
   public Handler getHandler() {
	   return handler;
   }
    
    
}
