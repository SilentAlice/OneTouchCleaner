package com.xjtu.onetouchcleaner.call;

import java.util.Calendar;
import java.util.Iterator;

import com.xjtu.onetouchcleaner.Constants;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.CallLog.Calls;

public class CallCleaner implements Runnable{

	Handler handler;	// to send message to call activity
	private boolean bStrangeCall,bEnableFilter,bDaysBefore,bDaysBetween;
	private Calendar cal1;
	private Calendar cal2;
	private int daysBeforeNum = 0;
	private Context context;
	
	
	public CallCleaner(Handler handler, Context context) {
		this.handler = handler;
		this.context = context;
		// Get data from storage
		bStrangeCall = CallStorage.bStrangeCall;
		bEnableFilter = CallStorage.bEnableFilter;
		bDaysBefore = CallStorage.bDaysBefore;
		bDaysBetween = CallStorage.bDaysBetween;
		cal1 = CallStorage.cal1;
		cal2 = CallStorage.cal2;
		daysBeforeNum = CallStorage.daysBeforeNum;
	}
	

	@Override
	public void run() {
		int totalNum = 0;
		StringBuffer dateArgs = new StringBuffer();
		
		// Delete Strange Call
		if(bStrangeCall) {
			totalNum += deStrangeCalls();
		}
		
		// Enable Filter
		if(bEnableFilter) {
			// Delete blacklist
			if(CallStorage.deleteMap.size() > 0) {
				StringBuffer args = new StringBuffer();
				Iterator<String> iter = CallStorage.deleteMap.values().iterator();
				while(iter.hasNext()) {
					args.append(iter.next());
					args.append("/");
				}
				totalNum += deCallByNums(args.toString().split("/"));
			}
			if(CallStorage.reserveMap.size() > 0) {
				Iterator<String> iter = CallStorage.reserveMap.values().iterator();
				while(iter.hasNext()) {
					dateArgs.append(iter.next());
					dateArgs.append("/");
				}
			}
		}
		
		// delete days before
		if(bDaysBefore) {
			totalNum += deCallBeforeDays(daysBeforeNum, dateArgs);
		}
		
		// delete days between
		if(bDaysBetween) {
			totalNum += deCallBetweenDates(cal1, cal2, dateArgs);
		}
		
		Message msg = new Message();
		msg.arg1 = Constants.call.CALL_CLEANER_FINISHED;
		msg.arg2 = totalNum;
		handler.sendMessage(msg);
		
	}
	
/* Deletion functions **************************************************************/
	
	private int deCallByNums(String[] num) {
		String whereStr = Calls.NUMBER + "=?";
		for(int i=num.length-1; i>0; i--) {
			whereStr += " or " + Calls.NUMBER + "=?";
		}
		return context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, whereStr, num);
	}
	
	private int deStrangeCalls() {
		return context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, Calls.CACHED_NAME + " is null", null);
	}

	
	// @description delete call logs before days
	// @return the number of rows deleted 
	private int deCallBeforeDays(int days, StringBuffer conds) {
		// Get current time
		Calendar nowTime = Calendar.getInstance();
		// Calculate the deleteDays 
		long deleteDays = nowTime.getTimeInMillis() -((long)days)*86400*1000;
		// Execute delete query
		String whereStr = "";
			for(int i=CallStorage.reserveMap.size(); i>0; i--) {
				whereStr += Calls.NUMBER + "<>?" + " and ";
		}
		conds.append(String.valueOf(deleteDays));
		return context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, whereStr + Calls.DATE+"<=?", conds.toString().split("/"));
	}
	
	// @description delete call logs between two date
	// @return the number of rows deleted
	private int deCallBetweenDates(Calendar cal1, Calendar cal2, StringBuffer conds) {
		String cond1 = Calls.DATE + ">=?";
		String cond2 = Calls.DATE + "<=?";
		String whereStr = "";
			for(int i=CallStorage.reserveMap.size(); i>0; i--) {
				whereStr += Calls.NUMBER + "<>?" + " and ";
		}
		// set conditions and arguments
		if(cal1 == null) {
			cond1 = "";
			conds.append(String.valueOf(cal2.getTimeInMillis()));
		} else if(cal2 == null) {
			cond2 = "";
			conds.append(String.valueOf(cal1.getTimeInMillis()));
		} else {
			cond1 += " and ";
			conds.append(String.valueOf(cal1.getTimeInMillis()));
			conds.append("/");
			conds.append(String.valueOf(cal2.getTimeInMillis()));
		}
		return context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, whereStr + cond1 + cond2, conds.toString().split("/"));
	}

}
