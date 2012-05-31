/**
 * 
 */
package us.miui.toolbox;

import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ContentResolver;
import android.content.Context;
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

	// Strings for retreiving settings using Settings.System.getXXXX
	public final static String HIDE_STATUS_BAR = "hide_status_bar";
	public final static String ALLOW_LAUNCHER_ROTATION = "allow_launcher_rotation";

	private CheckBoxPreference mHideStatusbar;
	private CheckBoxPreference mAllowRotation;
	private ContentResolver mCR;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.miuihome_settings);

		mCR = getActivity().getContentResolver();
		mHideStatusbar = (CheckBoxPreference) findPreference("pref_key_hidestatusbar");
		mAllowRotation = (CheckBoxPreference) findPreference("pref_key_allowrotation");

		// Try to read the HIDE_STATUS_BAR setting and if we get a
		// SettingNotFoundException
		// we need to create it.
		try {
			mHideStatusbar
					.setChecked(Settings.System.getInt(mCR, HIDE_STATUS_BAR) == 1);
		} catch (SettingNotFoundException e) {
			mHideStatusbar.setChecked(false);
			Settings.System.putInt(mCR, HIDE_STATUS_BAR, 0);
		}

		// Try to read the HIDE_STATUS_BAR setting and if we get a
		// SettingNotFoundException
		// we need to create it.
		try {
			mAllowRotation
					.setChecked(Settings.System.getInt(mCR, ALLOW_LAUNCHER_ROTATION) == 1);
		} catch (SettingNotFoundException e) {
			mAllowRotation.setChecked(false);
			Settings.System.putInt(mCR, ALLOW_LAUNCHER_ROTATION, 0);
		}

		// need to update system settings with the new value for HIDE_STATUS_BAR
		mHideStatusbar
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						Settings.System
								.putInt(mCR, HIDE_STATUS_BAR, (Boolean) newValue
										.equals(Boolean.TRUE) ? 1 : 0);
						restartLauncher();
						return true;
					}
				});
		
		// need to update system settings with the new value for ALLOW_LAUNCHER_ROTATION
		mAllowRotation
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						Settings.System
								.putInt(mCR, ALLOW_LAUNCHER_ROTATION, (Boolean) newValue
										.equals(Boolean.TRUE) ? 1 : 0);
						restartLauncher();
						return true;
					}
				});
	}

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
					RootUtils.execute("kill " + pid + "\n",
							Runtime.getRuntime());
					return;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
