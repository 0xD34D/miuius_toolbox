package us.miui.widget;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import us.miui.database.ColorPresetsDatabaseAdapter;

import android.content.Context;
import android.database.Cursor;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class ColorProfilesListPreference extends ListPreference {

	public ColorProfilesListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		ArrayList<String> entries = new ArrayList<String>();
		ArrayList<String> entryValues = new ArrayList<String>();
		ColorPresetsDatabaseAdapter db = new ColorPresetsDatabaseAdapter(context);
		db.open();
		Cursor cursor = db.fetchAllPresets();
		
		while (cursor.moveToNext()) {
			entries.add(cursor.getString(1));
			entryValues.add(cursor.getString(2));
		}
		cursor.close();
		db.close();
		
		String[] tmp = new String[entries.size()];
		setEntries(entries.toArray(tmp));
		setEntryValues(entryValues.toArray(tmp));
	}

	public ColorProfilesListPreference(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

}
