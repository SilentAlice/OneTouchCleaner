package com.xjtu.onetouchcleaner.other.ram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.xjtu.onetouchcleaner.Constants;
import com.xjtu.onetouchcleaner.other.OtherActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class RAMCleaner implements Runnable{

	private Context context;
	private Handler handler;
	public RAMCleaner(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	public void run() {
		ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> processInfoList = activityManager.getRunningAppProcesses();
		if(processInfoList != null) {
			for(ActivityManager.RunningAppProcessInfo info:processInfoList) {
				if(info.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
					String[] pkgList = info.pkgList;
					for(String pkgName:pkgList)
						activityManager.killBackgroundProcesses(pkgName);
				}
			}
		}
		Message msg = new Message();
		msg.arg1 = Constants.other.RAM_CLEAR_FINISHED;
		handler.sendMessage(msg);
	}
	
/* Help Functions ****************************************************/	
	// Get available memory
	public static long getAvailMemory(Context context) 
    {
        // Get current available ram
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        //return Formatter.formatFileSize(context, mi.availMem);// format gotten ram size
        return mi.availMem/(1024*1024);
    }

	// get total memory
    public static long getTotalMemory(Context context) 
    {
        String memInfoPath = "/proc/meminfo";// memory info file
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(memInfoPath);
            BufferedReader localBufferedReader = new BufferedReader(
            localFileReader, 8192);
            str2 = localBufferedReader.readLine();// Read first line (total memory)

            // Split String with space
            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue();// KB 
            localBufferedReader.close();
        } catch (IOException e) {
        	return -1;
        }
        //return Formatter.formatFileSize(context, initial_memory);// convert it into MB
        return initial_memory/(1024);
    }
}
