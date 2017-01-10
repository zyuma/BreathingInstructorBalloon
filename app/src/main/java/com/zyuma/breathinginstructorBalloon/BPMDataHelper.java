package com.zyuma.breathinginstructorBalloon;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BPMDataHelper extends SQLiteOpenHelper {

	// database and table names definition
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "com.zyuma.breathinginstructorBalloon.sqlitedatabase";
	private static final String _table_bpm = "bpm_table";
	private static final String _id = "id";

	// for user info table
	private static final String _duration = "duration";
	private static final String _breathcount = "breath_count";
	private static final String _bpm = "bpm";
	private static final String _rating = "rating";
	private static final String _time = "time";

	// sqlite commands definition
	// Creating commands
	private static final String CREATE_BPM_TABLE = "CREATE TABLE " + _table_bpm
			+ "(" + _id + " INTEGER PRIMARY KEY," 
			+ _duration + " INT,"
			+ _breathcount + " INT," 
			+ _bpm + " REAL,"
			+ _rating + " INT,"
			+ _time + " TEXT"
			+ ")";

	// Upgrading commands
	private static final String DROP_BPM_TABLE = "DROP TABLE IF EXISTS "
			+ _table_bpm;

	public BPMDataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BPM_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_BPM_TABLE);
	}

	
	public void setBPMToDatabase(ArrayList<BreathingData> dataList) {
		SQLiteDatabase db = this.getWritableDatabase();
		// drop the old table
		db.execSQL(DROP_BPM_TABLE);

		// create the new table
		db.execSQL(CREATE_BPM_TABLE);

		for (BreathingData d : dataList) {
			ContentValues values = new ContentValues();
			values.put(_duration, d.getDuration());
			values.put(_breathcount, d.getBreathCount());
			values.put(_bpm, d.getBPM());
			values.put(_rating, d.getRating());
			values.put(_time, d.getTime());
			db.insert(_table_bpm, null, values);
		}

		db.close();
	}

	public ArrayList<BreathingData> getBPMFromDatabase() {
		ArrayList<BreathingData> result = new ArrayList<BreathingData>();

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ _table_bpm, null);

		if (cursor.moveToFirst()) {
			do {
				BreathingData d = new BreathingData();
				d.setDuration(cursor.getInt(1));
				d.setBreathCount(cursor.getInt(2));
				d.setBPM(cursor.getDouble(3));
				d.setRating(cursor.getInt(4));
				d.setTime(cursor.getString(5));
				result.add(d);
			} while (cursor.moveToNext());
		}

		cursor.close();

		db.close();

		return result;
	}
}
