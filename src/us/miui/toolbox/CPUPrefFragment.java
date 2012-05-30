/**
 * 
 */
package us.miui.toolbox;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * @author Clark Scheff
 *
 */
public class CPUPrefFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.cpu_settings);
	}

}
