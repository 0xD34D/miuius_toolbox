/**
 * 
 */
package us.miui.toolbox;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

/**
 * @author Clark Scheff
 * 
 */
public class ToolboxPrefFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.settings);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen prefScreen,
			Preference pref) {
		super.onPreferenceTreeClick(prefScreen, pref);
		if (pref.getKey().equals(
				getActivity().getResources().getString(
						R.string.statusbar_prefs_key))) {
			// Display the fragment as the main content.
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new StatusbarPrefFragment())
					.addToBackStack(null).commit();
			return true;
		}
		if (pref.getKey().equals(
				getActivity().getResources().getString(
						R.string.colors_prefs_key))) {
			// Display the fragment as the main content.
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new CustomColorsPrefFragment())
					.addToBackStack(null).commit();
			return true;
		}
		if (pref.getKey().equals(
				getActivity().getResources().getString(
						R.string.cpu_prefs_key))) {
			// Display the fragment as the main content.
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new CPUPrefFragment())
					.addToBackStack(null).commit();
			return true;
		}
		if (pref.getKey().equals(
				getActivity().getResources().getString(
						R.string.home_prefs_key))) {
			// Display the fragment as the main content.
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new MiuiHomePrefFragment())
					.addToBackStack(null).commit();
			return true;
		}
		return false;
	}
}
