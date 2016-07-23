package com.xjtu.onetouchcleaner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class AllDBAdapter {

	/* Creation Sql Sequence ***************************************************************/
		static final String DATABASE_CREATE_CONFIG = "create table " + Constants.database.DATABASE_TABLE_CONFIG 
				+ "(" + Constants.database.CONFIG_ID + " integer primary key autoincrement, " + Constants.database.CONFIG_NAME
				+ " text not null, " + Constants.database.CONFIG_VALUE + " text not null);";
		
		static final String DATABASE_CREATE_FILTER = "create table " + Constants.database.DATABASE_TABLE_FILTER 
				+ "(" + Constants.database.FILTER_ID + " integer primary key autoincrement, " + Constants.database.FILTER_NUMBER
				+ " text not null, " + Constants.database.FILTER_NAME + " text not null, " + Constants.database.FILTER_TYPE + " text not null);";
		
		static final String DATABASE_CREATE_APPLIST= "create table " + Constants.database.DATABASE_TABLE_APPLIST 
				+ "(" + Constants.database.APPLIST_ID + " integer primary key autoincrement, " + Constants.database.APPLIST_PATH
				+ " text not null, " + Constants.database.APPLIST_ISCACHE + " text, " + Constants.database.APPLIST_ISDATA + " text);";
		
	/* Attributes ***********************************************************************/

		// the context use this adapter
		private final Context context;
		// the datebase we use
		private SQLiteDatabase db;
		// Database Helper
		private AllDBHelper dbHelper;
		
	/* Constructors **********************************************************************/
		
		public AllDBAdapter(Context context) {
			super();
			this.context = context;
			dbHelper = new AllDBHelper(context, Constants.database.DATABASE_NAME, null, Constants.database.DATABASE_VERSION);
		}
		
	/* Behaviors ***************************************************************************/
		// open db
		public AllDBAdapter open() throws SQLException {
			try {
				db = dbHelper.getWritableDatabase();
			}
			catch(SQLException ex) {
				db = dbHelper.getReadableDatabase();
			}
			return this;
		}
		
		// close db
		public void close() {
			db.close();
		}
		
		// insert item
		public long insertValue(String tableName, ContentValues contentValue) {
			return db.insert(tableName,null,contentValue);
		}
		
		// remove table
		public long cleanTable(String tableName) {
			return db.delete(tableName,null,null);
		}
		
		// remove entry
		public boolean removeEntry(String tableName, long rowIndex) {
			return db.delete(tableName, "_id =" + rowIndex, null) > 0;
		}
		
		// remove all tables
		public void cleanAllTables() {
			db.delete(Constants.database.DATABASE_TABLE_CONFIG, null, null);
			db.delete(Constants.database.DATABASE_TABLE_FILTER, null, null);
			db.delete(Constants.database.DATABASE_TABLE_APPLIST, null, null);
		}
		
		// get config entries
		public Cursor getConfigEntries() {
			return db.query(Constants.database.DATABASE_TABLE_CONFIG, new String[] {Constants.database.CONFIG_NAME, 
					Constants.database.CONFIG_VALUE}, null, null, null, null, null);
		}
		
		// get filter entries
		public Cursor getFilterEntries(String type) {
			return db.query(Constants.database.DATABASE_TABLE_FILTER,
					new String[] {Constants.database.FILTER_NAME, Constants.database.FILTER_NUMBER},
					Constants.database.FILTER_TYPE + "=?", new String[] {type}, null, null, null);
		}
		
		// get applist entries
		public Cursor getAppListEntries() {
			return db.query(Constants.database.DATABASE_TABLE_APPLIST,
					new String[] {Constants.database.APPLIST_PATH, Constants.database.APPLIST_ISCACHE, Constants.database.APPLIST_ISDATA},
					null, null, null, null, null);
		}
		
		// Query Sql sequence
		public Cursor query (String table, String[] columns, String selection, 
				String[] selectionArgs, String groupBy, String having, String orderBy) {
			return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		}

		
		// update entry
		public boolean updateEntry(String tableName, long rowIndex, ContentValues contentValue) {
			return db.update(tableName, contentValue, "_id=" + rowIndex, null) > 0;
		}

}
