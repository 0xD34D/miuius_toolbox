/**
 * 
 */
package us.miui.receiver;

import java.io.IOException;

import us.miui.helpers.CPUHelper;
import us.miui.service.AdbWifiService;
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
		if (prefs.getBoolean("pref_key_enable_adb_wifi", false) &&
				prefs.getBoolean("pref_key_adb_wifi_on_boot", false)) {
			int port = Integer.parseInt(prefs.getString("pref_key_adb_port", "5555"));
			Intent i = new Intent(context, AdbWifiService.class);
			i.setAction(AdbWifiService.ACTION_ENABLE);
			i.putExtra("port_num", port);
			context.startService(i);
			Log.i(TAG, String.format("Resotring ADB via WiFi settings on port %d.", port));
		}
	}

}
