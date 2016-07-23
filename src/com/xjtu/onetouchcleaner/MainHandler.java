package com.xjtu.onetouchcleaner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

public class MainHandler extends Handler{

	private MainActivity context;
	private String text;
	private TextView tv;
	
	public MainHandler(MainActivity context) {
		this.context = context;
		text = "";
		tv = (TextView)context.findViewById(R.id.main_status_tv);
	}
	@Override
	public void handleMessage(Message msg) {
		switch (msg.arg1) {
		case Constants.main.MAIN_SAVE_FINISHED:
			Toast.makeText(context, R.string.main_save_successfully, Toast.LENGTH_SHORT).show();
			addTextToTV(context.getString(R.string.main_save_successfully));
			break;
			
		case Constants.main.MAIN_CONFIG_SAVE_FIN:
			addTextToTV(context.getString(R.string.main_config_save_successfully));
			break;
			
		case Constants.sms.SMS_CLEANER_FINISHED:
			addTextToTV(String.valueOf(msg.arg2) + context.getString(R.string.main_sms_clean_fin));
			break;
			
		case Constants.call.CALL_CLEANER_FINISHED:
			addTextToTV(String.valueOf(msg.arg2) + context.getString(R.string.main_call_clean_fin));
			break;
			
		case Constants.app.APP_CLEANER_FINISHED:
			addTextToTV(context.getString(R.string.main_app_clean_fin));
			break;
			
		case Constants.other.RAM_CLEAR_FINISHED:
			addTextToTV(context.getString(R.string.main_ram_clean_fin));
			break;
		}
		super.handleMessage(msg);
	}
	
/* Help functions **************************************/
	private void addTextToTV(String newStr) {
		text += newStr + "\n";
		tv.setText(text);
	}

}
