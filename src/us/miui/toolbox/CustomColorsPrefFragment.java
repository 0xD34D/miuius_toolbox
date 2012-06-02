/**
 * 
 */
package us.miui.toolbox;

import java.io.IOException;
import java.util.List;

import us.miui.Toolbox;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

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
public class CustomColorsPrefFragment extends PreferenceFragment {

	private ColorPickerPreference mClockColor;
	private ColorPickerPreference mCarrierColor;
	private ColorPickerPreference mSignalColor;
	private ColorPickerPreference mNavbarColor;
	private ColorPickerPreference mBatteryColor;
	private ContentResolver mCR;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.colors_settings);

		mCR = getActivity().getContentResolver();
		mClockColor = (ColorPickerPreference) findPreference("pref_key_clock_color");
		mCarrierColor = (ColorPickerPreference) findPreference("pref_key_carrier_color");
		mSignalColor = (ColorPickerPreference) findPreference("pref_key_signal_color");
		mNavbarColor = (ColorPickerPreference) findPreference("pref_key_navbar_color");
		mBatteryColor = (ColorPickerPreference) findPreference("pref_key_battery_color");

		mCarrierColor.setAlphaSliderEnabled(true);
		mClockColor.setAlphaSliderEnabled(true);
		mSignalColor.setAlphaSliderEnabled(true);
		mNavbarColor.setAlphaSliderEnabled(true);
		mBatteryColor.setAlphaSliderEnabled(true);

		// set the onPreferenceChangeListener for all preferences
		mClockColor.setOnPreferenceChangeListener(mListener);
		mCarrierColor.setOnPreferenceChangeListener(mListener);
		mSignalColor.setOnPreferenceChangeListener(mListener);
		mNavbarColor.setOnPreferenceChangeListener(mListener);
		mBatteryColor.setOnPreferenceChangeListener(mListener);
	}
	
	OnPreferenceChangeListener mListener = new OnPreferenceChangeListener() {
		
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if (preference == mClockColor) {
				Settings.System.putInt(mCR, Toolbox.CUSTOM_CLOCK_COLOR, (Integer) newValue);
				
				restartSystemUI();
				return true;
			} else if (preference == mCarrierColor) {
				Settings.System.putInt(mCR, Toolbox.CUSTOM_CARRIER_COLOR, (Integer) newValue);
				
				restartSystemUI();
				return true;
			} else if (preference == mSignalColor) {
				Settings.System.putInt(mCR, Toolbox.CUSTOM_SIGNAL_COLOR, (Integer) newValue);
				
				restartSystemUI();
				return true;
			} else if (preference == mNavbarColor) {
				Settings.System.putInt(mCR, Toolbox.CUSTOM_NAVBAR_COLOR, (Integer) newValue);
				
				restartSystemUI();
				return true;
			} else if (preference == mBatteryColor) {
				Settings.System.putInt(mCR, Toolbox.CUSTOM_BATTERY_COLOR, (Integer) newValue);
				
				restartSystemUI();
				return true;
			}
			return false;
		}
	};

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
