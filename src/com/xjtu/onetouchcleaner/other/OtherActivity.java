package com.xjtu.onetouchcleaner.other;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.xjtu.onetouchcleaner.R;
import com.xjtu.onetouchcleaner.other.ram.RAMCleaner;

public class OtherActivity extends Activity{
	
/* Attributes *************************************************************************/	
	private TextView ramTV;

	private OtherHandler handler; 
/* Constructors **********************************************************************/
	public OtherActivity() {
		handler = new OtherHandler(this);
	}

/* Override Functions *******************************************************************/	
	// onCreate()
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_other);
		ramTV = (TextView)findViewById(R.id.other_ram_textview);
		init();
		super.onCreate(savedInstanceState);
	}

	// Options Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.other, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home)
			super.onBackPressed();
		return super.onOptionsItemSelected(item);
	}

/* Click Events ***************************************************************************/
	public void ramClear(View v) {
		Thread tRamCleaner = new Thread(new RAMCleaner(this, handler));
		tRamCleaner.start();
	}
	
/* Help Functions ***********************************************************************/	
	// This function is used to initialize activity 
	private void init() {
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// update the ram textview
		updateRamTV();
	}
	
/* public Functions *********************************************************************/
	public TextView getRamTV() {
		return ramTV;
	}
	
	// update ram textview
	public void updateRamTV() {
		ramTV.setText(RAMCleaner.getAvailMemory(this) + " MB " + 
				getString(R.string.other_ram_available_ram)	+ " / " + RAMCleaner.getTotalMemory(this) + " MB" );
	}
	
}
