/**
 * 
 */
package us.miui.toolbox;

import java.io.IOException;
import java.util.List;

import us.miui.Toolbox;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;

/**
 * @author Clark Scheff
 * 
 */
public class NavbarPrefFragment extends PreferenceFragment {

	private CheckBoxPreference mShowSearch;
	private Preference mNavbarOrder;
	private ContentResolver mCR;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.navbar_settings);

		mCR = getActivity().getContentResolver();
		mShowSearch = (CheckBoxPreference) findPreference("pref_key_showsearch");
		

		// Try to read the HIDE_STATUS_BAR setting and if we get a
		// SettingNotFoundException
		// we need to create it.
		try {
			mShowSearch
					.setChecked(Settings.System.getInt(mCR, Toolbox.USE_NAVBAR_SEARCH) == 1);
		} catch (SettingNotFoundException e) {
			mShowSearch.setChecked(false);
			Settings.System.putInt(mCR, Toolbox.USE_NAVBAR_SEARCH, 0);
		}

		// need to update system settings with the new value for HIDE_STATUS_BAR
		mShowSearch
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						Settings.System
								.putInt(mCR, Toolbox.USE_NAVBAR_SEARCH, (Boolean) newValue
										.equals(Boolean.TRUE) ? 1 : 0);
						restartSystemUI();
						return true;
					}
				});
		
		mNavbarOrder = findPreference("pref_key_navbar_order");
		mNavbarOrder.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(getActivity().getApplicationContext(), NavbarOrderSelector.class);
				startActivity(intent);
				return true;
			}
		});
		
		// hide the navbar order preference until I figure out how to implement
		// this into MiuiSystemUI
		//getPreferenceScreen().removePreference(mNavbarOrder);
	}

	private void restartSystemUI() {
		ActivityManager am = (ActivityManager) getActivity().getSystemService(
				Context.ACTIVITY_SERVICE);

		List<ActivityManager.RunningAppProcessInfo> apps = am
				.getRunningAppProcesses();
		for (RunningAppProcessInfo app : apps) {
			if (app.processName.equals("com.android.systemui")) {
				int pid = app.pid;
				try {
					RootUtils.execute("kill " + pid + "\n");
					return;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
