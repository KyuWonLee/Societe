package org.com.smu.societe.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TwitAccTokenDB extends SQLiteOpenHelper{

	private static TwitAccTokenDB instance = null;
	private TwitAccTokenDB(Context context) {
		super(context, "Twitter.db", null, 1);
	}
	
	public static TwitAccTokenDB getDBInstance(Context context){
		if(instance == null){
			instance = new TwitAccTokenDB(context);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE dic( _id INTEGER PRIMARY KEY AUTOINCREMENT," + 
				"Token TEXT, Secret TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS dic");
		onCreate(db);
	}
	
}