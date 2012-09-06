/**
 * 
 */
package us.miui.toolbox.fragment;

import java.io.IOException;
import java.util.List;

import us.miui.Toolbox;
import us.miui.toolbox.R;
import us.miui.toolbox.RootUtils;
import us.miui.toolbox.R.string;
import us.miui.toolbox.R.xml;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;

/**
 * @author Clark Scheff
 * 
 */
public class MiuiHomePrefFragment extends PreferenceFragment {

	private CheckBoxPreference mHideStatusbar;
	private CheckBoxPreference mHideIconText;
	private ContentResolver mCR;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.miuihome_settings);
		Resources res = getActivity().getResources();

		mCR = getActivity().getContentResolver();
		mHideStatusbar = 
				(CheckBoxPreference) findPreference(res.getString(
						R.string.hide_statusbar_pref_key));
		mHideIconText = 
				(CheckBoxPreference) findPreference(res.getString(
						R.string.hide_shortcut_text_pref_key));
		// Try to read the HIDE_STATUS_BAR setting and if we get a
		// SettingNotFoundException
		// we need to create it.
		try {
			mHideStatusbar
					.setChecked(Settings.System.getInt(mCR, Toolbox.HIDE_STATUS_BAR) == 1);
		} catch (SettingNotFoundException e) {
			mHideStatusbar.setChecked(false);
			Settings.System.putInt(mCR, Toolbox.HIDE_STATUS_BAR, 0);
		}

		// Try to read the HIDE_SHORTCUT_TEXT setting and if we get a
		// SettingNotFoundException
		// we need to create it.
		try {
			mHideIconText
					.setChecked(Settings.System.getInt(mCR, Toolbox.HIDE_SHORTCUT_TEXT) == 1);
		} catch (SettingNotFoundException e) {
			mHideIconText.setChecked(false);
			Settings.System.putInt(mCR, Toolbox.HIDE_SHORTCUT_TEXT, 0);
		}

		mHideStatusbar.setOnPreferenceChangeListener(mListener);
		mHideIconText.setOnPreferenceChangeListener(mListener);
	}
	
	OnPreferenceChangeListener mListener = new OnPreferenceChangeListener() {
		
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if (preference == mHideStatusbar) {
				Settings.System
				.putInt(mCR, Toolbox.HIDE_STATUS_BAR, (Boolean) newValue
						.equals(Boolean.TRUE) ? 1 : 0);
				restartLauncher();
				return true;
			} else if (preference == mHideIconText) {
				Settings.System
				.putInt(mCR, Toolbox.HIDE_SHORTCUT_TEXT, (Boolean) newValue
						.equals(Boolean.TRUE) ? 1 : 0);
				restartLauncher();
				return true;
			}
			return false;
		}
	};

	/**
	 * Method to find the PID for com.miui.home ui and kill it. MiuiHome
	 * will restart so this works out for us.
	 */
	private void restartLauncher() {
		ActivityManager am = (ActivityManager) getActivity().getSystemService(
				Context.ACTIVITY_SERVICE);

		List<ActivityManager.RunningAppProcessInfo> apps = am
				.getRunningAppProcesses();
		for (RunningAppProcessInfo app : apps) {
			if (app.processName.equals("com.miui.home")) {
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
