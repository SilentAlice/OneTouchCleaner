package com.xjtu.onetouchcleaner.app;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.xjtu.onetouchcleaner.Constants;
import com.xjtu.onetouchcleaner.R;

public class AppHandler extends Handler{

	AppActivity context;
	public AppHandler(AppActivity context) {
		super();
		this.context =context; 
	}
	@Override
	public void handleMessage(Message msg) {
		// Process messages
		switch(msg.arg1) {
		case Constants.app.APP_CLEANER_FINISHED:
			context.pd.dismiss();
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(context.getApplicationContext(),context.getString(R.string.app_toast_clearfi), Toast.LENGTH_SHORT).show();
				}
			});
			break;

		case Constants.app.APP_LIST_PREPARE_FINISHED:
			context.pd.dismiss();
			break;
		}
		super.handleMessage(msg);
	}

}
