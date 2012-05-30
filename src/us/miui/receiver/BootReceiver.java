/**
 * 
 */
package us.miui.receiver;

import java.io.IOException;

import us.miui.helpers.CPUHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author Clark Scheff
 *
 */
public class BootReceiver extends BroadcastReceiver {
	private final static String TAG = "MIUIToolboxBoot";
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs = context.getSharedPreferences("us.miui.toolbox_preferences", 0);
		if (prefs.getBoolean("restore_cpu_onboot", false)) {
			Log.i(TAG, "Resotring CPU settings.");
			try {
				String[] freqs = CPUHelper.getFrequencies();
				String minFreq = freqs[prefs.getInt("min_cpu_freq", 0)];
				String maxFreq = freqs[prefs.getInt("max_cpu_freq", 0)];
				String gov = prefs.getString("pref_key_cpugovernor", "ondemand");
				Log.i(TAG, "Setting min frequency to " + minFreq);
				CPUHelper.setMinFrequency(minFreq);
				Log.i(TAG, "Setting max frequency to " + maxFreq);
				CPUHelper.setMaxFrequency(maxFreq);
				Log.i(TAG, "Setting governor to " + gov);
				CPUHelper.setGovernor(gov);
			} catch (IOException e) {
				Log.e(TAG, "Unable to restore CPU settings.");
			}
		}
	}

}
