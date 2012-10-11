/**
 * 
 */
package us.miui.toolbox.fragment;

import java.io.File;

import us.miui.helpers.SystemHelper;
import us.miui.toolbox.R;
import us.miui.toolbox.activity.MountLogActivity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

/**
 * @author lithium
 *
 */
public class DevelopmentOptionsFragment extends PreferenceFragment {
	Preference mShowLog;
	Preference mDeleteLog;
	Preference mBootCount;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.development_settings);
		mShowLog = findPreference("pref_key_view_mount_log");
		mDeleteLog = findPreference("pref_key_delete_mount_log");
		mBootCount = findPreference("pref_key_boot_counter");
		int count = getPreferenceManager().getSharedPreferences().getInt("pref_key_boot_counter", 0);
		mBootCount.setTitle("Boot count: " + count);
		mShowLog.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(getActivity(), MountLogActivity.class);
				getActivity().startActivity(intent);
				return true;
			}
		});

		mDeleteLog.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				File log = new File(SystemHelper.FILES_DIR + "/mount.log");
				if (log.exists())
					log.delete();
						
				return true;
			}
		});
		
		mBootCount.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Editor editor = getPreferenceManager().getSharedPreferences().edit();
				editor.putInt("pref_key_boot_counter", 0);
				editor.commit();
				mBootCount.setTitle("Boot count: 0");
				return true;
			}
		});
	}

}
