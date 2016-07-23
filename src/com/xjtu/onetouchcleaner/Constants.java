package com.xjtu.onetouchcleaner;

public class Constants {
/* Constants *************************************************************************/
	
	// main's constants will begin with 3XXX
	final public static class main {
		public final static int MAIN_SAVE_FINISHED = 3000;
		public final static int MAIN_CONFIG_SAVE_FIN = 3001;
		public final static int MAIN_APPINFO_SAVE_FIN = 3002;
	}
	
	// apps' constants will begin with 4XXX
	final public static class app {
		public final static int APP_CLEANER = 4000;
		public final static int APP_CLEANER_FINISHED = 4001;
		public final static int APP_LIST_PREPARE = 4002;
		public final static int APP_LIST_PREPARE_FINISHED = 4003;
	}
	
	// call's constants begin with 3XXX
	final public static class call {
		public final static int RETRIVE_CONTACT_RESERVE = 3001;
		public final static int RETRIVE_CONTACT_DELETE = 3002;
		public final static int REMOVE_RESERVE_ITEM = 3003;
		public final static int REMOVE_DELETE_ITEM = 3004;
		public final static int CALL_CLEANER_FINISHED = 3005;
	}
	
	// other's constants will begin with 5XXX
	final public static class other {
		public final static int RAM_CLEAR_FINISHED = 5001;
	}
	
	// sms's constants will begin with 6XXX
	final public static class sms{
		public final static int RETRIVE_CONTACT_RESERVE = 6001;
		public final static int RETRIVE_CONTACT_DELETE  = 6002;
		public final static int REMOVE_RESERVE_ITEM = 6003;
		public final static int REMOVE_DELETE_ITEM = 6004;
		public final static int SMS_CLEANER_FINISHED = 6005;
	}
	
	// used to set display default display time;
	final public static class splash {
		public static final int SPLASH_DISPLAY_LENGHT = 2500;
	}
	
	// database information
	final public static class database {
		// Database
		public static final String DATABASE_NAME = "OneTouchCleaner.db";
		public static final String DATABASE_TABLE_CONFIG = "config";
		public static final String DATABASE_TABLE_APPLIST = "appList";
		public static final String DATABASE_TABLE_FILTER = "filter";
		public static final int DATABASE_VERSION = 1;
		
		// Config table
		public static final String CONFIG_ID = "_id";
		public static final String CONFIG_NAME = "name";
		public static final String CONFIG_VALUE = "value";
		
		// Filter table
		public static final String FILTER_ID = "_id";
		public static final String FILTER_TYPE = "type";
		public static final String FILTER_NAME = "name";
		public static final String FILTER_NUMBER = "number";
		
		// Applist table
		public static final String APPLIST_ID = "_id";
		public static final String APPLIST_PATH = "path";
		public static final String APPLIST_ISCACHE = "cache";
		public static final String APPLIST_ISDATA = "data";
	}

	
}
