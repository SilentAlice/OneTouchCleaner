package com.xjtu.onetouchcleaner.sms;

import java.util.Calendar;
import java.util.HashMap;

import android.annotation.SuppressLint;

public class SMSStorage {

	@SuppressLint("UseSparseArrays")
	public static HashMap<String,String> reserveMap = new HashMap<String,String>();
	@SuppressLint("UseSparseArrays")
	public static HashMap<String,String> deleteMap = new HashMap<String,String>();
	public static boolean bStrangeSMS,bEnableFilter,bDaysBefore,bDaysBetween = false;
	public static int daysBeforeNum = -1;
	public static Calendar cal1,cal2 = null;

}
