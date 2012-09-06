/**
 * 
 */
package us.miui.toolbox.fragment;

import us.miui.toolbox.R;
import us.miui.toolbox.R.xml;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

/**
 * @author Clark Scheff
 *
 */
public class CPUPrefFragment extends PreferenceFragment {
	private ListPreference mCpuGovernor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.cpu_settings);
		
		mCpuGovernor = (ListPreference) findPreference("");
	}

}
