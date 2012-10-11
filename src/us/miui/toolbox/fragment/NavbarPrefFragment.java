/**
 * 
 */
package us.miui.toolbox.fragment;

import us.miui.Toolbox;
import us.miui.helpers.SystemHelper;
import us.miui.toolbox.R;
import us.miui.toolbox.R.xml;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
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
	//private CheckBoxPreference mLockscreenHome;
	private ListPreference mNavbarHeight;
	private ContentResolver mCR;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.navbar_settings);

		mCR = getActivity().getContentResolver();
		mNavbarHeight = (ListPreference) findPreference("pref_key_navbar_height");
		
		String height = Settings.System.getString(mCR, Toolbox.NAVBAR_HEIGHT);
		if (height == null)
			height = "48";
		mNavbarHeight.setValue(height);

		// need to update system settings with the new value for LOCKSCREEN_SHOW_HOME
		mNavbarHeight
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						Settings.System
								.putString(mCR, Toolbox.NAVBAR_HEIGHT, (String) newValue);
						//SystemHelper.restartSystemUI(getActivity());
						return true;
					}
				});
		
		// hide the navbar order preference until I figure out how to implement
		// this into MiuiSystemUI
		//getPreferenceScreen().removePreference(mNavbarOrder);
	}
}
