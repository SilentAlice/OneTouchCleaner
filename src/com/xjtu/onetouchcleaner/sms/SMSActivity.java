package com.xjtu.onetouchcleaner.sms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xjtu.onetouchcleaner.Constants;
import com.xjtu.onetouchcleaner.ContactsAdapter;
import com.xjtu.onetouchcleaner.R;

public class SMSActivity extends Activity{

/* Attributes **************************************************************************/
	private boolean bDaysBefore;
	private boolean bDaysBetween;
	private boolean bStrangeSMS;
	private boolean	bEnableFilter;
	private int daysBeforeNum;
	private EditText mDaysBeforeET;
	private EditText mDaysBetweenET1;
	private EditText mDaysBetweenET2;
	private ListView mReserveList;
	private ListView mDeleteList;
	private ArrayList<String> mReserveNameList;
	private ArrayList<String> mDeleteNameList;
	private ContactsAdapter reserveAdapter;
	private ContactsAdapter deleteAdapter;
	private TextView mAlertTV;
	private Calendar cal1;
	private Calendar cal2;
	private UIHandler handler;


/* Override Functions ******************************************************************/	
	
	// onCreate()
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_sms);
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
	/* Initialization ***********************************************/
		
		// Get views
		mDaysBeforeET = (EditText)findViewById(R.id.sms_days_before_et);
		mDaysBetweenET1 = (EditText)findViewById(R.id.sms_days_between_et1);
		mDaysBetweenET2 = (EditText)findViewById(R.id.sms_days_between_et2);
		mReserveList = (ListView)findViewById(R.id.sms_reserve_list);
		mDeleteList = (ListView)findViewById(R.id.sms_delete_list);
		mAlertTV = (TextView)findViewById(R.id.sms_alert_tv);
		
	/* get data from storage ******************************/
		bDaysBefore = SMSStorage.bDaysBefore;
		daysBeforeNum = SMSStorage.daysBeforeNum;
		bDaysBetween = SMSStorage.bDaysBetween;
		bStrangeSMS = SMSStorage.bStrangeSMS;
		bEnableFilter = SMSStorage.bEnableFilter;
		cal1 = SMSStorage.cal1;
		cal2 = SMSStorage.cal2;
		// namelist
		mReserveNameList = new ArrayList<String>();
		mDeleteNameList = new ArrayList<String>();
		Iterator<String> iter = SMSStorage.reserveMap.keySet().iterator();
		while(iter.hasNext()) {
			mReserveNameList.add(iter.next());
		}
		iter = SMSStorage.deleteMap.keySet().iterator();
		while(iter.hasNext()) {
			mDeleteNameList.add(iter.next());
		}
		
	/* Show data on views ********************************/
		
		// set checkboxes
		CheckBox tempCB;
		tempCB = (CheckBox)findViewById(R.id.sms_days_before_cb);
		tempCB.setChecked(bDaysBefore);
		if(bDaysBefore) {
			onCBClick(tempCB);
			mDaysBeforeET.setText(String.valueOf(daysBeforeNum));
			((EditText)findViewById(R.id.sms_days_before_et)).setTextColor(getResources().getColor(R.color.black));
		}
			
		tempCB = (CheckBox)findViewById(R.id.sms_days_between_cb);
		tempCB.setChecked(bDaysBetween);
		if(bDaysBetween) {
			onCBClick(tempCB);
			if(cal1 != null) {
				updateET(mDaysBetweenET1, cal1);
				((EditText)findViewById(R.id.sms_days_between_et1)).setTextColor(getResources().getColor(R.color.black));
			}
			if(cal2 != null) {
				updateET(mDaysBetweenET2, cal2);
				((EditText)findViewById(R.id.sms_days_between_et2)).setTextColor(getResources().getColor(R.color.black));
			}
		}
		
		tempCB = (CheckBox)findViewById(R.id.sms_delete_strange_sms_cb);
		tempCB.setChecked(bStrangeSMS);
		
		tempCB = (CheckBox)findViewById(R.id.sms_filter_cb);
		tempCB.setChecked(bEnableFilter);
		if(bEnableFilter) {
			onCBClick(tempCB);
		}
		
		// present name list
		reserveAdapter = new ContactsAdapter(this, mReserveNameList);
		mReserveList.setAdapter(reserveAdapter);
		reserveAdapter.notifyDataSetChanged();
		deleteAdapter = new ContactsAdapter(this, mDeleteNameList);
		mDeleteList.setAdapter(deleteAdapter);
		deleteAdapter.notifyDataSetChanged();
		
		// initialize handler
		handler = new UIHandler();
		
	/* Register context menu for views ******************************/
		registerForContextMenu(mReserveList);
		registerForContextMenu(mDeleteList);
		
	/* Set Listener for views ***************************************/
		// When view is focused/Clicked, open a time picker
		mDaysBeforeET.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(v.isFocused()) {
					((EditText)v).setText("");
					((EditText)v).setTextColor(getResources().getColor(R.color.black));
				} else {
					if(((EditText)v).getText().toString().equals("")) {
						((EditText)v).setText(R.string.sms_input_number);
						((EditText)v).setTextColor(getResources().getColor(R.color.d9d9d9));
					}
				}
			}
		});
		
		mDaysBeforeET.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(v.isFocused()) {
					((EditText)v).setTextColor(getResources().getColor(R.color.black));
					((EditText)v).setText("");
				}
			}
		});
		
		
		mDaysBetweenET1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v.isFocused()) {
					((EditText)v).setTextColor(getResources().getColor(R.color.black));
					((EditText)v).setText("");
					
					if(cal1 == null)
						cal1 = Calendar.getInstance();
					new DatePickerDialog(SMSActivity.this, new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								cal1.set(year, monthOfYear, dayOfMonth);
								updateET(mDaysBetweenET1, cal1);
							}
						}, cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), 
						cal1.get(Calendar.DAY_OF_MONTH)).show();
				} 
			}
		});
		mDaysBetweenET1.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					((EditText)v).setTextColor(getResources().getColor(R.color.black));
					((EditText)v).setText("");
					if(cal1 == null)
						cal1 = Calendar.getInstance();
					new DatePickerDialog(SMSActivity.this, new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								cal1.set(year, monthOfYear, dayOfMonth);
								updateET(mDaysBetweenET1, cal1);
							}
						}, cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), 
						cal1.get(Calendar.DAY_OF_MONTH)).show();
				} 
			}
		});
		mDaysBetweenET2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v.isFocused()) {
					((EditText)v).setTextColor(getResources().getColor(R.color.black));
					((EditText)v).setText("");
					if(cal2 == null)
						cal2 = Calendar.getInstance();
					new DatePickerDialog(SMSActivity.this, new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								cal2.set(year, monthOfYear, dayOfMonth);
								updateET(mDaysBetweenET2, cal2);
							}
						}, cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), 
						cal2.get(Calendar.DAY_OF_MONTH)).show();
				} 
			}
		});
		mDaysBetweenET2.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					((EditText)v).setTextColor(getResources().getColor(R.color.black));
					((EditText)v).setText("");
					if(cal2 == null)
						cal2 = Calendar.getInstance();
					new DatePickerDialog(SMSActivity.this, new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								cal2.set(year, monthOfYear, dayOfMonth);
								updateET(mDaysBetweenET2, cal2);
							}
						}, cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), 
						cal2.get(Calendar.DAY_OF_MONTH)).show();
				} 
			}
		});
		super.onCreate(savedInstanceState);
	}

	// Options Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sms, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Suppose all inputs are right
		mAlertTV.setText("");
		switch(item.getItemId()) {
		// Begin one touch cleaning
		
		case android.R.id.home:
			super.onBackPressed();
			break;
		
		case R.id.sms_menu_one_clear:
			SMSStorage.bEnableFilter = bEnableFilter;
			SMSStorage.bStrangeSMS = bStrangeSMS;
			// Delete Days before
			if(bDaysBefore) {
				try {
					daysBeforeNum = Integer.valueOf(mDaysBeforeET.getText().toString()).intValue();
					if(daysBeforeNum < 0 || daysBeforeNum > 15000) { 
						mAlertTV.setText(R.string.sms_alert_num_range);
						return true;
					}
						SMSStorage.daysBeforeNum = daysBeforeNum;
						SMSStorage.bDaysBefore = true;
				} catch (NumberFormatException e) {
					mAlertTV.setText(R.string.sms_alert_input_num);
					return true;
				}
			}
			
			// Delete Days between
			if(bDaysBetween) {
				if(cal1 == null && cal2 == null) {
					mAlertTV.setText(R.string.sms_alert_select_date);
					return true;
				}
				if(cal1 != null && cal2 != null)
					if(cal1.getTimeInMillis() > cal2.getTimeInMillis()) {
						mAlertTV.setText(R.string.sms_alert_select_date_value);
						return true;
					}
				SMSStorage.cal1 = cal1;
				SMSStorage.cal2 = cal2;
				SMSStorage.bDaysBetween = true;
			}
			Runnable cleaner = new SMSCleaner(handler, this);
			new Thread(cleaner).start();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}
	
	// Context Menu
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		AdapterView.AdapterContextMenuInfo menuInfo;
		menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int idx = -1;
		switch(item.getItemId()) {
		case Constants.sms.REMOVE_RESERVE_ITEM:
			idx = menuInfo.position;
			SMSStorage.reserveMap.remove(mReserveNameList.remove(idx));
			reserveAdapter.notifyDataSetChanged();
			return true;
		case Constants.sms.REMOVE_DELETE_ITEM:
			idx = menuInfo.position;
			SMSStorage.deleteMap.remove(mDeleteNameList.remove(idx));
			deleteAdapter.notifyDataSetChanged();
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("");
		switch(v.getId()) {
		case R.id.sms_reserve_list:
			menu.add(0,Constants.sms.REMOVE_RESERVE_ITEM,Menu.NONE,R.string.sms_context_menu_list);
			break;
		case R.id.sms_delete_list:
			menu.add(0,Constants.sms.REMOVE_DELETE_ITEM,Menu.NONE,R.string.sms_context_menu_list);
			break;
		default:
			break;
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

@Override
	protected void onPause() {
		SMSStorage.bDaysBefore = bDaysBefore;
		try {
		SMSStorage.daysBeforeNum = Integer.valueOf(mDaysBeforeET.getText().toString()).intValue();
		} catch(Exception e) {
			
		}
		SMSStorage.bDaysBetween = bDaysBetween;
		SMSStorage.bStrangeSMS = bStrangeSMS;
		SMSStorage.bEnableFilter = bEnableFilter;
		SMSStorage.cal1 = cal1;
		SMSStorage.cal2 = cal2;
		super.onPause();
	}

	/* Events *********************************************************************************/
	// CheckBox Event
	public void onCBClick(View v) {
		CheckBox cb = (CheckBox)v;
		switch(v.getId()) {
		case R.id.sms_days_before_cb:
			if(cb.isChecked()) {
				bDaysBefore = true;
				mDaysBeforeET.setEnabled(true);
			} else {
				bDaysBefore = false;
				mDaysBeforeET.setEnabled(false);
				mDaysBeforeET.setText(R.string.sms_input_number);
				mDaysBeforeET.setTextColor(getResources().getColor(R.color.d9d9d9));
			}
			break;
		case R.id.sms_days_between_cb:
			if(cb.isChecked()) {
				bDaysBetween = true;
				mDaysBetweenET1.setEnabled(true);
				mDaysBetweenET2.setEnabled(true);
			} else {
				bDaysBetween = false;
				mDaysBetweenET1.setEnabled(false);
				mDaysBetweenET2.setEnabled(false);
				mDaysBetweenET1.setText(R.string.sms_select_date);
				mDaysBetweenET2.setText(R.string.sms_select_date);
				mDaysBetweenET1.setTextColor(getResources().getColor(R.color.d9d9d9));
				mDaysBetweenET2.setTextColor(getResources().getColor(R.color.d9d9d9));
				cal1 = null;
				cal2 = null;
			}
			break;
		case R.id.sms_delete_strange_sms_cb:
			if(cb.isChecked()) {
				bStrangeSMS = true;
			}
			else
				bStrangeSMS = false;
			break;
		case R.id.sms_filter_cb:
			if(cb.isChecked()) {
				bEnableFilter = true;
				TextView tv = (TextView)findViewById(R.id.sms_reserve_tv);
				tv.setEnabled(true);
				tv = (TextView)findViewById(R.id.sms_delete_tv);
				tv.setEnabled(true);
				findViewById(R.id.sms_delete_list).setEnabled(true);
				findViewById(R.id.sms_reserve_list).setEnabled(true);
			} else {
				bEnableFilter = false;
				TextView tv = (TextView)findViewById(R.id.sms_reserve_tv);
				tv.setEnabled(false);
				tv = (TextView)findViewById(R.id.sms_delete_tv);
				tv.setEnabled(false);
				findViewById(R.id.sms_delete_list).setEnabled(false);
				findViewById(R.id.sms_reserve_list).setEnabled(false);
			}
			break;
		default:
			break;
		}
	}
	
	// onClick Event 
	// ReserveList TextView ClickEvent
	public void addReserveList(View v) {
		Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(contactIntent, Constants.sms.RETRIVE_CONTACT_RESERVE);
	}
	
	// DeleteList TextView ClickEvent 
	public void addDeleteList(View v) {
		Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(contactIntent, Constants.sms.RETRIVE_CONTACT_DELETE);
	}
	
/* Help Functions ***********************************************************************/
	
	// Use a date picker dialog to get the set date
	private void updateET(EditText et, Calendar cal) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		et.setText(dateFormat.format(cal.getTime()));
	}
	
	private String cleanSpace(String str) {
		String temp = str.replace(" ", "");
		if(temp.startsWith("+")) {
			return temp.substring(3);
		} else
			return temp;
	}
	
	// onActivityResult
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
				if(data == null)
					return ;
				@SuppressWarnings("deprecation")
				Cursor cursor = managedQuery(data.getData(), null, null, null, null);
				cursor.moveToFirst();
				int contactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID, null, null);
				phones.moveToFirst();
				String numphone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				numphone = cleanSpace(numphone);
				String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				if (requestCode == Constants.sms.RETRIVE_CONTACT_RESERVE) { 
					if(!SMSStorage.reserveMap.containsKey(name)) {
					SMSStorage.reserveMap.put(name, numphone);
					mReserveNameList.add(name);
					reserveAdapter.notifyDataSetChanged();
					}
					return;
				}
				if (requestCode == Constants.sms.RETRIVE_CONTACT_DELETE) { 
					if(!SMSStorage.deleteMap.containsKey(name)) {
						SMSStorage.deleteMap.put(name, numphone);
						mDeleteNameList.add(name);
					deleteAdapter.notifyDataSetChanged();
					return;
					}
				}
			}
	
	class  UIHandler extends Handler{
		public UIHandler() {
			super();
		}
		@Override
		public void handleMessage(Message msg) {
			// Process messages
			final int i = msg.arg2;
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(SMSActivity.this, i+getString(R.string.sms_toast_delete_num), Toast.LENGTH_SHORT).show();
				}
			});
			
			super.handleMessage(msg);
		}
	}
	

}
