package com.guest.cnbeta.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class BaseDB {

	private BaseDBHelper dbHelper;
	protected SQLiteDatabase db;

	public BaseDB(Context context) {
		
		if (dbHelper == null) {
			dbHelper = new BaseDBHelper(context, "db.db", null, 1);
		}
		
		if (db == null) {
			db = dbHelper.getWritableDatabase();
		}else {
			dbHelper.close();
		}
	}

	protected String toSafeString(String str) {

		try {
			str = str.replace("'", "''");
		} catch (NullPointerException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return str;
	}
	
}
