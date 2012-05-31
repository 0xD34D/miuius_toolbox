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
public class StatusbarPrefFragment extends PreferenceFragment {

	// Strings for retreiving settings using Settings.System.getXXXX
	public final static String CENTER_CLOCK = "center_clock";
	public final static String SINGLE_SIGNAL_BARS = "single_signal_bars";

	private SwitchPreference mCenterClock;
	private SwitchPreference mSingleBars;
	private ContentResolver mCR;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.statusbar_settings);

		mCR = getActivity().getContentResolver();
		mCenterClock = (SwitchPreference) findPreference("pref_key_centerclock");
		mSingleBars = (SwitchPreference) findPreference("pref_key_signalbars");

		// Try to read the CENTER_CLOCK setting and if we get a
		// SettingNotFoundException
		// we need to create it.
		try {
			mCenterClock
					.setChecked(Settings.System.getInt(mCR, CENTER_CLOCK) == 1);
		} catch (SettingNotFoundException e) {
			mCenterClock.setChecked(false);
			Settings.System.putInt(mCR, CENTER_CLOCK, 0);
		}

		// Try to read the SINGLE_SIGNAL_BARS setting and if we get a
		// SettingNotFoundException
		// we need to create it.
		try {
			mSingleBars.setChecked(Settings.System.getInt(mCR,
					SINGLE_SIGNAL_BARS) == 1);
		} catch (SettingNotFoundException e) {
			mSingleBars.setChecked(false);
			Settings.System.putInt(mCR, SINGLE_SIGNAL_BARS, 0);
		}

		// need to update system settings with the new value for CENTER_CLOCK
		mCenterClock
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						Settings.System
								.putInt(mCR, CENTER_CLOCK, (Boolean) newValue
										.equals(Boolean.TRUE) ? 1 : 0);
						restartSystemUI();
						return true;
					}
				});

		// need to update system settings with the new value for CENTER_CLOCK
		mSingleBars
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Settings.System
								.putInt(mCR,
										SINGLE_SIGNAL_BARS,
										(Boolean) newValue.equals(Boolean.TRUE) ? 1
												: 0);
						restartSystemUI();
						return true;
					}
				});

		// disable signal bar mod if device is not CDMA
		TelephonyManager tm = (TelephonyManager) getActivity()
				.getSystemService(Context.TELEPHONY_SERVICE);
		if(tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA)
			mSingleBars.setEnabled(false);
	}

	/**
	 * Method to find the PID for com.android.system ui and kill it. SystemUI
	 * will restart so this works out for us.
	 */
	private void restartSystemUI() {
		ActivityManager am = (ActivityManager) getActivity().getSystemService(
				Context.ACTIVITY_SERVICE);

		List<ActivityManager.RunningAppProcessInfo> apps = am
				.getRunningAppProcesses();
		for (RunningAppProcessInfo app : apps) {
			if (app.processName.equals("com.android.systemui")) {
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
