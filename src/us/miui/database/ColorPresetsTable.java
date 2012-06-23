/**
 * 
 */
package us.miui.database;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Clark Scheff
 *
 */
public class ColorPresetsTable {
	private static final String DATABASE_CREATE = " create table presets_tbl ("
			+ "_id integer primary key autoincrement,"
			+ "presetName varchar(64),"
			+ "presetColors varchar(120)"
			+ ");";
	public static void onCreate(SQLiteDatabase database) {
		try {
			database.execSQL(DATABASE_CREATE);
			addPreset(database, "Red", "#FFFF0000:#FFFF0000:#FFFF0000:#FFFF0000:#FFFF0000");
			addPreset(database, "Green", "#FF00FF00:#FF00FF00:#FF00FF00:#FF00FF00:#FF00FF00");
			addPreset(database, "Blue", "#FF0000FF:#FF0000FF:#FF0000FF:#FF0000FF:#FF0000FF");
		} catch (SQLException se) {
			
		}
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS presets_tbl");
		onCreate(database);
	}
	
	private static void addPreset(SQLiteDatabase database, String name, String colors) {
		database.execSQL(String.format("INSERT INTO presets_tbl (presetName. presetColors)\n" +
				"VALUES (%s, %s);", name, colors));
	}
}
