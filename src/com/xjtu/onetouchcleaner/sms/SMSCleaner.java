package com.xjtu.onetouchcleaner.sms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import com.xjtu.onetouchcleaner.Constants;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

public class SMSCleaner implements Runnable{
	
	Handler handler;	// to send message to sms activity
	private boolean bStrangeSMS,bEnableFilter,bDaysBefore,bDaysBetween;
	private Calendar cal1;
	private Calendar cal2;
	private int daysBeforeNum;
	private Context context;
	
	public SMSCleaner(Handler handler, Context context) {
		this.handler = handler;
		this.context = context;
		// Get data from storage
		bStrangeSMS = SMSStorage.bStrangeSMS;
		bEnableFilter = SMSStorage.bEnableFilter;
		bDaysBefore = SMSStorage.bDaysBefore;
		bDaysBetween = SMSStorage.bDaysBetween;
		cal1 = SMSStorage.cal1;
		cal2 = SMSStorage.cal2;
		daysBeforeNum = SMSStorage.daysBeforeNum;
	}
	
	@Override
	public void run() {
		int totalNum = 0;
		StringBuffer dateArgs = new StringBuffer();
		
		// Delete Strange SMS
		if(bStrangeSMS) {
			totalNum += deleteStrangerMessage();
		}
		
		// Enable Filter
		if(bEnableFilter) {
			// Delete blacklist
			if(SMSStorage.deleteMap.size() > 0) {
				StringBuffer args = new StringBuffer();
				Iterator<String> iter = SMSStorage.deleteMap.values().iterator();
				while(iter.hasNext()) {
					args.append(iter.next());
					args.append("/");
				}
				totalNum += deleteSMSByNums(args.toString().split("/"));
			}
			// Store while list
			if(SMSStorage.reserveMap.size() > 0) {
				Iterator<String> iter = SMSStorage.reserveMap.values().iterator();
				while(iter.hasNext()) {
					dateArgs.append(iter.next());
					dateArgs.append("/");
				}
			}
		}
		
		// delete days before
		if(bDaysBefore) {
			totalNum += deleteMessageBeforeDays(daysBeforeNum, dateArgs);
		}
		
		// delete days between
		if(bDaysBetween) {
			totalNum += deleteBetweenDateMessge(cal1, cal2, dateArgs);
		}
		Message msg = new Message();
		msg.arg1 = Constants.sms.SMS_CLEANER_FINISHED;
		msg.arg2 = totalNum;
		handler.sendMessage(msg);
		
	}
	
/* Help Functions **************************************************************/ 
	private int deleteSMSByNums(String[] num) {
		int deleteNum = 0;
		Uri urisms = Uri.parse("content://sms");
		for(String address: num) {
			String thread_id = getThreadID(address);
			deleteNum += context.getContentResolver().delete(urisms,"thread_id=?",new String[]{thread_id});
		}
		return deleteNum;
	}
	
	private int deleteStrangerMessage(){
		
		Uri uriT = Uri.parse("content://mms-sms/conversations");
		Uri uriThreadSimple = uriT.buildUpon()
				.appendQueryParameter("simple", "true").build();
		StringBuffer deleteNumStr = new StringBuffer();
		
		// Get all threads
		Cursor cursorThread = context.getContentResolver().query(uriThreadSimple, new String[]{"_id","recipient_ids"}, null, null, null);
		if(cursorThread != null && cursorThread.moveToFirst()){
			// Travel each thread
			do{
				// Get canonical_id to get address
				String recipient_id = cursorThread.getString(1);
				Uri uriCanonical = Uri.parse("content://mms-sms/canonical-addresses");
				Cursor cursorAddress = context.getContentResolver().query(uriCanonical, new String[]{"_id","address"}, "_id=?", 
						new String[]{recipient_id}, null);
				if(cursorAddress != null && cursorAddress.moveToFirst()){
					// Get the address
					String address = cleanSpace(cursorAddress.getString(1));
					
					// Get address in term of address data4 is Phone.NORMALIZED_NUMBER
					Cursor cursorPhone = context.getContentResolver().query(Phone.CONTENT_URI, new String[] {Phone.DISPLAY_NAME},
							"data4=? or data4=?", new String[] {address, "+86"+address}, null);

					// Check if the contacts has this address
					if (cursorPhone != null && cursorPhone.moveToFirst()) {
						int contactIndex = cursorPhone.getColumnIndex(Phone.DISPLAY_NAME);
						String displayName = cursorPhone.getString(contactIndex);
						
						// This address included in contacts doesn't has a name (regarded as a strange number)
						if(displayName == null || displayName==""){
							deleteNumStr.append(address);
							deleteNumStr.append("/");
						}
					}else{
						// Can't find this address (It's truly a strange number)
						deleteNumStr.append(address);
						deleteNumStr.append("/");
					}

					// Close cursors
					if (cursorPhone != null && !cursorPhone.isClosed())
						cursorPhone.close();
					if(cursorAddress != null && !cursorAddress.isClosed())
						cursorAddress.close();
				}
			}while(cursorThread.moveToNext());
		}
		
		// Close thread cursor
		if(cursorThread != null && !cursorThread.isClosed())
			cursorThread.close();
		
		// Delete these short messages
		return deleteSMSByNums(deleteNumStr.toString().split("/"));
	}

	public int deleteBetweenDateMessge(Calendar cal1, Calendar cal2, StringBuffer dateArgs){
		String cond1 = "date >=?";
		String cond2 = "date <=?";
		StringBuffer conds = new StringBuffer();
		// Convert addresses to thread_ids
		String[] strDateArgs = dateArgs.toString().split("/");
		if(strDateArgs.length>0 && !(strDateArgs[0].equalsIgnoreCase(""))) {
			for(String address:strDateArgs) {
				conds.append(getThreadID(address));
				conds.append("/");
			}
		}
		
		String whereStr = "";
			for(int i=SMSStorage.reserveMap.size(); i>0; i--) {
				whereStr += "thread_id<>? and ";
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
		
		Uri uriSMS = Uri.parse("content://sms");
		int intDelete = context.getContentResolver().delete(uriSMS, whereStr + cond1 + cond2, conds.toString().split("/"));
		return intDelete;
	}
	
	public int deleteMessageBeforeDays(int days, StringBuffer dateArgs){
		// Get current time
		Calendar nowTime = Calendar.getInstance();
		// Calculate the deleteDays 
		long deleteDays = nowTime.getTimeInMillis() -((long)days)*86400*1000;
		// Execute delete query
		String whereStr = "";
			for(int i=SMSStorage.reserveMap.size(); i>0; i--) {
				whereStr += "thread_id<>? and ";
		}
			
		StringBuffer conds = new StringBuffer();
		String[] strDateArgs = dateArgs.toString().split("/");
		if(strDateArgs.length>0 && !(strDateArgs[0].equalsIgnoreCase(""))) {
			for(String address:strDateArgs) {
				conds.append(getThreadID(address));
				conds.append("/");
			}
		}
		
		conds.append(String.valueOf(deleteDays));
		Uri uriSMS = Uri.parse("content://sms");
		int intDelete = context.getContentResolver().delete(uriSMS, whereStr + "date <=?", conds.toString().split("/"));
		return intDelete;
	}
	
	// get the thread_id corresponding address
	private String getThreadID(String address){
		long thread_id = -1;
		Uri uri = Uri.parse("content://sms");
		Cursor cursor = context.getContentResolver().query(uri, new String[]{"thread_id"}, "address=? or address=?",
				new String[] {address, "+86"+address}, null);
		if(cursor != null && cursor.moveToFirst()){
			thread_id = cursor.getLong(0);
		}
		if(cursor != null && !cursor.isClosed()){
			cursor.close();
		}
		return String.valueOf(thread_id);
	}
	
	// Replace " " with ""
	private String cleanSpace(String str) {
		String temp = str.replace(" ", "");
		if(temp.startsWith("+")) {
			return temp.substring(3);
		} else
			return temp;
	}

	/*private void updateThread() {
		Uri uriSMS = Uri.parse("content://sms");
		ContentResolver resolver = context.getContentResolver();
		ArrayList<String> arrayDelThread = new ArrayList<String>();
		// Get all threads
		Uri uriT = Uri.parse("content://mms-sms/conversations");
		Uri uriThreadSimple = uriT.buildUpon()
				.appendQueryParameter("simple", "true").build();
		StringBuffer deleteNumStr = new StringBuffer();
		Cursor cursorThread = resolver.query(uriThreadSimple, new String[] {"_id","recipient_ids"}, null, null, null);
		
		
		
		if(cursorThread != null && cursorThread.moveToFirst()) {
			String temp1 = cursorThread.getString(0);
			String temp2 = cursorThread.getString(1);
			
			do {
				Cursor cursorSMS = resolver.query(uriSMS, new String[]{"thread_id","date","body"},
						"thread_id=?", new String[] {cursorThread.getString(0)}, "date desc");
				
				if(cursorSMS != null && cursorSMS.moveToFirst()) {
					ContentValues contentValue = new ContentValues();
					contentValue.put("message_count", cursorSMS.getCount());
					contentValue.put("date", cursorSMS.getLong(1));
					contentValue.put("snippet", cursorSMS.getString(2));
					resolver.update(uriThreadSimple, contentValue, "_id=?", new String[]{cursorThread.getString(0)});
				} else {
					// this thread should be deleted
					arrayDelThread.add(cursorThread.getString(0));
				}
				
				if(cursorSMS != null && !cursorSMS.isClosed())
					cursorSMS.close();
			} while (cursorThread.moveToNext());
			
			if(cursorThread != null && !cursorThread.isClosed())
				cursorThread.close();
				
			for(String deleteThread:arrayDelThread) {
				resolver.delete(uriThreadSimple, "_id=?", new String[]{deleteThread});
			}
		}
	}*/
}
