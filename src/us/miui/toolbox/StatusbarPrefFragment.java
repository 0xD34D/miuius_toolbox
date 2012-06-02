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
public class StatusbarPrefFragment extends PreferenceFragment {

	// Strings for retreiving settings using Settings.System.getXXXX
	private CheckBoxPreference mCenterClock;
	private CheckBoxPreference mSingleBars;
	private CheckBoxPreference mUseCustomCarrier;
	private EditTextPreference mCustomCarrierLabel;
	private ContentResolver mCR;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.statusbar_settings);

		mCR = getActivity().getContentResolver();
		mCenterClock = (CheckBoxPreference) findPreference("pref_key_centerclock");
		mSingleBars = (CheckBoxPreference) findPreference("pref_key_signalbars");
		mUseCustomCarrier = (CheckBoxPreference) findPreference("pref_key_use_custom_label");
		mCustomCarrierLabel = (EditTextPreference) findPreference("pref_key_custom_carrier_label");

		// Try to read the CENTER_CLOCK setting and if we get a
		// SettingNotFoundException
		// we need to create it.
		try {
			mCenterClock
					.setChecked(Settings.System.getInt(mCR, Toolbox.CENTER_CLOCK) == 1);
		} catch (SettingNotFoundException e) {
			mCenterClock.setChecked(false);
			Settings.System.putInt(mCR, Toolbox.CENTER_CLOCK, 0);
		}

		// Try to read the SINGLE_SIGNAL_BARS setting and if we get a
		// SettingNotFoundException
		// we need to create it.
		try {
			mSingleBars.setChecked(Settings.System.getInt(mCR,
					Toolbox.SINGLE_SIGNAL_BARS) == 1);
		} catch (SettingNotFoundException e) {
			mSingleBars.setChecked(false);
			Settings.System.putInt(mCR, Toolbox.SINGLE_SIGNAL_BARS, 0);
		}

		// Try to read the USE_CUSTOM_CARRIER setting and if we get a
		// SettingNotFoundException
		// we need to create it.
		try {
			mUseCustomCarrier.setChecked(Settings.System.getInt(mCR,
					Toolbox.USE_CUSTOM_CARRIER) == 1);
		} catch (SettingNotFoundException e) {
			mSingleBars.setChecked(false);
			Settings.System.putInt(mCR, Toolbox.USE_CUSTOM_CARRIER, 0);
		}
		
		String label = Settings.System.getString(mCR, Toolbox.CUSTOM_CARRIER_TEXT);
		if (label == null)
			label = "";
		mCustomCarrierLabel.setText(label);
		mCustomCarrierLabel.setSummary(label);
		mCustomCarrierLabel.setEnabled(mUseCustomCarrier.isChecked());
		
		// set the onPreferenceChangeListener for all preferences
		mCenterClock.setOnPreferenceChangeListener(mListener);
		mSingleBars.setOnPreferenceChangeListener(mListener);
		mUseCustomCarrier.setOnPreferenceChangeListener(mListener);
		mCustomCarrierLabel.setOnPreferenceChangeListener(mListener);

		// disable signal bar mod if device is not CDMA
		TelephonyManager tm = (TelephonyManager) getActivity()
				.getSystemService(Context.TELEPHONY_SERVICE);
		if(tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA)
			mSingleBars.setEnabled(false);
	}
	
	OnPreferenceChangeListener mListener = new OnPreferenceChangeListener() {
		
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if (preference == mCenterClock) {
				Settings.System
				.putInt(mCR, Toolbox.CENTER_CLOCK, (Boolean) newValue
						.equals(Boolean.TRUE) ? 1 : 0);
				restartSystemUI();
				return true;
			} else if (preference == mSingleBars) {
				Settings.System
				.putInt(mCR,
						Toolbox.SINGLE_SIGNAL_BARS,
						(Boolean) newValue.equals(Boolean.TRUE) ? 1
								: 0);
				restartSystemUI();
				return true;
			} else if (preference == mUseCustomCarrier) {
				boolean enabled = ((Boolean) newValue.equals(Boolean.TRUE));
				Settings.System
				.putInt(mCR,
						Toolbox.USE_CUSTOM_CARRIER,
						enabled ? 1 : 0);
				mCustomCarrierLabel.setEnabled(enabled);
				
				restartSystemUI();
				return true;
			} else if (preference == mCustomCarrierLabel) {
				Settings.System.putString(mCR, Toolbox.CUSTOM_CARRIER_TEXT, (String) newValue);
				mCustomCarrierLabel.setSummary((String) newValue);
				
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
