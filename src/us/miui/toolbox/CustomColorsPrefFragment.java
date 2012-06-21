/**
 * 
 */
package us.miui.toolbox;

import java.io.IOException;
import java.util.List;

import us.miui.Toolbox;
import us.miui.helpers.SystemHelper;

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
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * @author Clark Scheff
 * 
 */
public class CustomColorsPrefFragment extends PreferenceFragment {
	private static final int LOAD_ID = 1984;
	private static final int SAVE_ID = 1976;

	private ColorPickerPreference mClockColor;
	private ColorPickerPreference mCarrierColor;
	private ColorPickerPreference mSignalColor;
	private ColorPickerPreference mNavbarColor;
	private ColorPickerPreference mBatteryColor;
	private ColorPickerPreference mWifiColor;
	private Preference mResetColors;
	//private ColorPickerPreference mBluetoothColor;
	//private ColorPickerPreference mGpsColor;
	private ContentResolver mCR;

	/* (non-Javadoc)
	 * @see android.preference.PreferenceFragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add(Menu.NONE, LOAD_ID, 0, "Load Preset").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(Menu.NONE, SAVE_ID, 0, "Save Preset").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case LOAD_ID:
			return true;
		case SAVE_ID:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

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
		mWifiColor = (ColorPickerPreference) findPreference("pref_key_wifi_color");
		mResetColors = (Preference) findPreference("pref_key_reset_colors");
		//mBluetoothColor = (ColorPickerPreference) findPreference("pref_key_bluetooth_color");
		//mGpsColor = (ColorPickerPreference) findPreference("pref_key_gps_color");
		setStoredColors();

		mCarrierColor.setAlphaSliderEnabled(true);
		mClockColor.setAlphaSliderEnabled(true);
		mSignalColor.setAlphaSliderEnabled(true);
		mNavbarColor.setAlphaSliderEnabled(true);
		mBatteryColor.setAlphaSliderEnabled(true);
		mWifiColor.setAlphaSliderEnabled(true);
		//mBluetoothColor.setAlphaSliderEnabled(true);
		//mGpsColor.setAlphaSliderEnabled(true);

		// set the onPreferenceChangeListener for all preferences
		mClockColor.setOnPreferenceChangeListener(mListener);
		mCarrierColor.setOnPreferenceChangeListener(mListener);
		mSignalColor.setOnPreferenceChangeListener(mListener);
		mNavbarColor.setOnPreferenceChangeListener(mListener);
		mBatteryColor.setOnPreferenceChangeListener(mListener);
		mWifiColor.setOnPreferenceChangeListener(mListener);
		
		//mBluetoothColor.setOnPreferenceChangeListener(mListener);
		//mGpsColor.setOnPreferenceChangeListener(mListener);
		
		// remove navigation bar color preference if there is no navbar
		if(!SystemHelper.hasNavigationBar(getActivity().getApplicationContext())) {
			getPreferenceScreen().removePreference(mNavbarColor);
		}
		
		mResetColors.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				resetColors();
				//restartSystemUI();
				return true;
			}
		});
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
			} else if (preference == mWifiColor) {
				Settings.System.putInt(mCR, Toolbox.CUSTOM_WIFI_COLOR, (Integer) newValue);
				
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
	
	private void setStoredColors() {
		try {
			mClockColor.onColorChanged(Settings.System.getInt(mCR, Toolbox.CUSTOM_CLOCK_COLOR));
		} catch (SettingNotFoundException e) {}
		try {
			mCarrierColor.onColorChanged(Settings.System.getInt(mCR, Toolbox.CUSTOM_CARRIER_COLOR));
		} catch (SettingNotFoundException e) {}
		try {
			mBatteryColor.onColorChanged(Settings.System.getInt(mCR, Toolbox.CUSTOM_BATTERY_COLOR));
		} catch (SettingNotFoundException e) {}
		try {
			mNavbarColor.onColorChanged(Settings.System.getInt(mCR, Toolbox.CUSTOM_NAVBAR_COLOR));
		} catch (SettingNotFoundException e) {}
		try {
			mSignalColor.onColorChanged(Settings.System.getInt(mCR, Toolbox.CUSTOM_SIGNAL_COLOR));
		} catch (SettingNotFoundException e) {}
		try {
			mWifiColor.onColorChanged(Settings.System.getInt(mCR, Toolbox.CUSTOM_WIFI_COLOR));
		} catch (SettingNotFoundException e) {}
	}
	
	private void resetColors() {
		mClockColor.onColorChanged(0xFF000000);
		Settings.System.putInt(mCR, Toolbox.CUSTOM_CLOCK_COLOR, 0);
		mCarrierColor.onColorChanged(0xFF000000);
		Settings.System.putInt(mCR, Toolbox.CUSTOM_CARRIER_COLOR, 0);
		mBatteryColor.onColorChanged(0xFF000000);
		Settings.System.putInt(mCR, Toolbox.CUSTOM_BATTERY_COLOR, 0);
		mNavbarColor.onColorChanged(0xFF000000);
		Settings.System.putInt(mCR, Toolbox.CUSTOM_NAVBAR_COLOR, 0);
		mSignalColor.onColorChanged(0xFF000000);
		Settings.System.putInt(mCR, Toolbox.CUSTOM_SIGNAL_COLOR, 0);
		mWifiColor.onColorChanged(0xFF000000);
		Settings.System.putInt(mCR, Toolbox.CUSTOM_WIFI_COLOR, 0);
	}
}
