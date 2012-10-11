/**
 * 
 */
package us.miui.receiver;

import java.io.IOException;

import us.miui.helpers.CPUHelper;
import us.miui.helpers.SystemHelper;
import us.miui.service.AdbWifiService;
import us.miui.toolbox.RootUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
		
		int count = prefs.getInt("pref_key_boot_counter", 0) + 1;
		Editor editor = prefs.edit();
		editor.putInt("pref_key_boot_counter", count);
		editor.commit();
		
		String screenSize = prefs.getString("screen_size_prefs_key", "0");
		try {
			//RootUtils.execute(String.format("setprop persist.screen_size_spoof %s\n", screenSize));
			System.setProperty("persist.screen_size_spoof", screenSize);
			us.miui.Toolbox.SCREEN_LAYOUT_WIDTH = screenSize;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
		
		// in case the user updated we should copy the correct ota binary to /system/xbin
		// default will be to use cwmota in case that setting is not set yet.
		String recovery = prefs.getString("recovery_selection_prefs_key", "cwmota");
		try {
			SystemHelper.copyRecoveryOTA(context, recovery);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
