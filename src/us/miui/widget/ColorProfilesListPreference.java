package us.miui.widget;

import java.util.ArrayList;

import us.miui.database.ColorPresetsDatabaseAdapter;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

public class ColorProfilesListPreference extends ListPreference {

	CharSequence[] entries;
	CharSequence[] entryValues;
	ArrayList<RadioButton> rButtonList;
	SharedPreferences mPrefs;
	SharedPreferences.Editor mEditor;
	LayoutInflater mInflater;
    private int mClickedDialogEntryIndex;
	
	public ColorProfilesListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		rButtonList = new ArrayList<RadioButton>();
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		mEditor = mPrefs.edit();
		mInflater = LayoutInflater.from(context);
		
		ArrayList<String> entries = new ArrayList<String>();
		ArrayList<String> entryValues = new ArrayList<String>();
		ColorPresetsDatabaseAdapter db = new ColorPresetsDatabaseAdapter(context);
		db.open();
		Cursor cursor = db.fetchAllPresets();
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			entries.add(cursor.getString(0));
			entryValues.add(cursor.getString(1));
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		
		this.entries = new CharSequence[entries.size()];
		this.entryValues = new CharSequence[entryValues.size()];
		setEntries(entries.toArray(this.entries));
		setEntryValues(entryValues.toArray(this.entryValues));
	}

	public ColorProfilesListPreference(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		ColorProfilesListPreferenceAdapter cplpa = new ColorProfilesListPreferenceAdapter();
		mClickedDialogEntryIndex = findIndexOfValue(getValue());
		builder.setAdapter(cplpa, mDialogClickListener);
	}
	
	private DialogInterface.OnClickListener mDialogClickListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			mClickedDialogEntryIndex = which;
			ColorProfilesListPreference.this.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
			dialog.dismiss();
		}
	};

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        
        if (positiveResult && mClickedDialogEntryIndex >= 0 && getEntryValues() != null) {
            String value = getEntryValues()[mClickedDialogEntryIndex].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }

	private class ColorProfilesListPreferenceAdapter implements ListAdapter {

		public ColorProfilesListPreferenceAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return entries.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return entries[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			CheckedTextView row = (CheckedTextView) convertView;
			
			if (row == null)
				row = (CheckedTextView) mInflater.inflate(android.R.layout.simple_list_item_single_choice, parent, false);

			row.setText(entries[position]);
			if (position == findIndexOfValue(getValue()))
				row.setChecked(true);
			else
				row.setChecked(false);
			row.setClickable(true);
			
			row.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					Toast.makeText(getContext(), "FUCK YEAH!!!!!", Toast.LENGTH_SHORT).show();
					return false;
				}
			});
			
			//row.setClickable(true);
			
			return row;
		}
		
		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean areAllItemsEnabled() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			return true;
		}
	}
}
