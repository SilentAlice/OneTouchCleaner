package com.xjtu.onetouchcleaner.other;

import com.xjtu.onetouchcleaner.Constants;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class OtherHandler extends Handler{

	private Context context;
	
	public OtherHandler(Context context) {
		super();
		this.context = context;
	}
	
	@Override
	public void handleMessage(Message msg) {
		switch(msg.arg1) {
		case Constants.other.RAM_CLEAR_FINISHED:
			((OtherActivity)context).updateRamTV();
		}
		super.handleMessage(msg);
	}
	
	
}
