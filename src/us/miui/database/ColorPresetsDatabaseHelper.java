package us.miui.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ColorPresetsDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "presets.db";
	public static final int DATABASE_VERSION = 8;
	public ColorPresetsDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ColorPresetsTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ColorPresetsTable.onUpgrade(db, oldVersion, newVersion);
	}

}
