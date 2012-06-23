/**
 * 
 */
package us.miui.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * @author lithium
 *
 */
public class ColorPresetsDatabaseAdapter {
	public static final String KEY_NAME = "presetName";
	public static final String KEY_COLORS = "presetColors";
	public static final String DB_TABLE = "presets_tbl";
	private Context mContext;
	private SQLiteDatabase mDb;
	private ColorPresetsDatabaseHelper mDbHelper;
	
	public ColorPresetsDatabaseAdapter(Context context) {
		mContext = context;
	}
	
	public ColorPresetsDatabaseAdapter open() throws SQLiteException {
		mDbHelper = new ColorPresetsDatabaseHelper(mContext);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		mDbHelper.close();
	}
	
	public long createPreset(String name, String colors) {
		ContentValues values = createContentValues(name, colors);
		
		return mDb.insert(DB_TABLE, null, values);
	}
	
	public Cursor fetchAllPresets() {
		return mDb.query(DB_TABLE, new String[] {KEY_NAME, KEY_COLORS}, 
				null, null, null, null, KEY_NAME);
	}
	
	public Cursor fetchPreset(String presetName) {
		Cursor cursor = mDb.query(true, DB_TABLE, new String[] {
				KEY_NAME, KEY_COLORS }, 
				KEY_NAME + "='" + presetName + "'",
				null, null, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	private ContentValues createContentValues(String name, String colors) {
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_COLORS, colors);
		
		return values;
	}
}
