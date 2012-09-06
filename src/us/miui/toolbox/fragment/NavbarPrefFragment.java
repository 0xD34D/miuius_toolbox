/**
 * 
 */
package us.miui.toolbox.fragment;

import us.miui.Toolbox;
import us.miui.toolbox.R;
import us.miui.toolbox.R.xml;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

/**
 * @author Clark Scheff
 * 
 */
public class NavbarPrefFragment extends PreferenceFragment {

//	private CheckBoxPreference mShowSearch;
	private CheckBoxPreference mLockscreenHome;
	private ContentResolver mCR;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.navbar_settings);

		mCR = getActivity().getContentResolver();
		mLockscreenHome = (CheckBoxPreference) findPreference("pref_key_lockscreen_show_home");
		

		// Try to read the LOCKSCREEN_SHOW_HOME setting and if we get a
		// SettingNotFoundException
		// we need to create it.
		try {
			mLockscreenHome
					.setChecked(Settings.System.getInt(mCR, Toolbox.LOCKSCREEN_SHOW_HOME) == 1);
		} catch (SettingNotFoundException e) {
			mLockscreenHome.setChecked(false);
			Settings.System.putInt(mCR, Toolbox.LOCKSCREEN_SHOW_HOME, 0);
		}

		// need to update system settings with the new value for LOCKSCREEN_SHOW_HOME
		mLockscreenHome
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						Settings.System
								.putInt(mCR, Toolbox.LOCKSCREEN_SHOW_HOME, (Boolean) newValue
										.equals(Boolean.TRUE) ? 1 : 0);
						//restartSystemUI();
						return true;
					}
				});
		
		// hide the navbar order preference until I figure out how to implement
		// this into MiuiSystemUI
		//getPreferenceScreen().removePreference(mNavbarOrder);
	}
}
