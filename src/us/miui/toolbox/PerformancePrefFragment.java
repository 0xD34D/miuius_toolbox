/**
 * 
 */
package us.miui.toolbox;

import us.miui.helpers.SystemHelper;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

/**
 * @author Clark Scheff
 * 
 */
public class PerformancePrefFragment extends PreferenceFragment {
	private Preference mBatteryCalibration;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.performance_settings);
		
		mBatteryCalibration = findPreference("calibration_prefs_key");
		mBatteryCalibration.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(getActivity().getApplicationContext(), BatteryCalibrationActivity.class);
				startActivity(intent);
				return true;
			}
		});
		
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen prefScreen,
			Preference pref) {
		super.onPreferenceTreeClick(prefScreen, pref);
		if (pref.getKey().equals(
				getActivity().getResources().getString(
						R.string.cpu_prefs_key))) {
			// Display the fragment as the main content.
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new CPUPrefFragment())
					.addToBackStack(null).commit();
			return true;
		}
		return false;
	}
}
