/**
 * 
 */
package us.miui.toolbox;

import java.io.IOException;
import java.util.List;

import us.miui.service.AdbWifiService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.text.InputType;
import android.text.format.Formatter;
import android.widget.EditText;

/**
 * @author Clark Scheff
 * 
 */
public class AdbWifiPrefFragment extends PreferenceFragment {
	private CheckBoxPreference mEnableAdbWifi;
	private EditTextPreference mPortNumber;
	private Preference mHowTo;
	private int mCurrentPort;
	private Resources res;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		res = this.getResources();

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.wifi_adb_settings);
		
		mEnableAdbWifi = (CheckBoxPreference) findPreference(res.getString(R.string.pref_key_enable_adb_wifi));
		mEnableAdbWifi.setChecked(isEnabled());
		mEnableAdbWifi.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				boolean enabled = ((Boolean)newValue.equals(Boolean.TRUE));
				if (enabled) {
					Intent i = new Intent(getActivity().getApplicationContext(), AdbWifiService.class);
					i.setAction(AdbWifiService.ACTION_ENABLE);
					i.putExtra("port_num", mCurrentPort);
					getActivity().startService(i);
					//enableAdbWifi();
				} else {
					Intent i = new Intent(getActivity().getApplicationContext(), AdbWifiService.class);
					i.setAction(AdbWifiService.ACTION_DISABLE);
					getActivity().startService(i);
					disableAdbWifi();
				}
				return true;
			}
		});
		
		mPortNumber = (EditTextPreference) findPreference(res.getString(R.string.pref_key_adb_port));
		EditText et = mPortNumber.getEditText();
		et.setInputType(InputType.TYPE_CLASS_NUMBER);
		mCurrentPort = Integer.parseInt(mPortNumber.getText());
		mPortNumber.setSummary(res.getString(R.string.pref_adb_port_summary) + "\n" + mPortNumber.getText());
		mPortNumber.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				int port = 0;
				try {
					port = Integer.parseInt((String) newValue);
				} catch (NumberFormatException e) {
					port = mCurrentPort;
					mPortNumber.setText(Integer.toString(mCurrentPort));
				}
				if (port <= 0 || port > 65535) {
					port = mCurrentPort;
					mPortNumber.setText(Integer.toString(mCurrentPort));
				}
				if (mCurrentPort != port && isEnabled()) {
					mCurrentPort = port;
					Intent i = new Intent(getActivity().getApplicationContext(), AdbWifiService.class);
					i.setAction(AdbWifiService.ACTION_ENABLE);
					i.putExtra("port_num", mCurrentPort);
					getActivity().startService(i);
					//enableAdbWifi();
				}
				mPortNumber.setText(Integer.toString(mCurrentPort));
				mPortNumber.setSummary(res.getString(R.string.pref_adb_port_summary) + "\n" + mCurrentPort);
				mHowTo.setSummary(String.format(res.getString(R.string.pref_adb_instructions_summary),
						getLocalIpAddress(), mCurrentPort));
				
				return true;
			}
		});
		
		mHowTo = findPreference(res.getString(R.string.pref_key_instructions));
		mHowTo.setSummary(String.format(res.getString(R.string.pref_adb_instructions_summary),
				getLocalIpAddress(), mCurrentPort));
	}
	
	private boolean isEnabled() {
		String prop = null;
		try {
			prop = RootUtils.executeWithResult("getprop service.adb.tcp.port\n");
		} catch (IOException e) {
			prop = "";
		}
		if (prop == null) {
			return false;
		}
		return !(prop.equals("") || prop.equals("-1"));
	}
	
	private void enableAdbWifi() {
		int port = mCurrentPort;
		boolean alreadyEnabled = isEnabled();
		try {
			RootUtils.execute("setprop service.adb.tcp.port " + port + "\n" +
				"stop adbd\nstart adbd\n");
		} catch (IOException e) {
			return;
		}
		
		String connection = getLocalIpAddress() + ":" + port;
		Context ctx = getActivity().getApplicationContext();
		NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		if (alreadyEnabled)
			nm.cancel(42);
		Notification.Builder nb = new Notification.Builder(ctx);
		nb.setContentTitle("ADB via WiFi enabled");
		nb.setContentText("Connect to " + connection);
		nb.setSmallIcon(R.drawable.stat_sys_adb);
//		if (alreadyEnabled) 
//			nb.setTicker("ADB via WiFi re-enabled on " + connection);
//		else
			nb.setTicker("ADB via WiFi enabled on " + connection);
		nb.setAutoCancel(false);
		nb.setWhen(System.currentTimeMillis());
		nb.setOngoing(true);
		Intent intent = new Intent(ctx, MIUIToolboxActivity.class);
		intent.setAction(MIUIToolboxActivity.ACTION_ADB_WIFI_SETTINGS);
		PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);
		nb.setContentIntent(contentIntent);
		nm.notify(42, nb.getNotification());
	}
	
	private void disableAdbWifi() {
		try {
			RootUtils.execute("setprop service.adb.tcp.port -1\n" +
				"stop adbd\nstart adbd\n");
			Context ctx = getActivity().getApplicationContext();
			NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(42);
		} catch (IOException e) {
		}
	}

	public String getLocalIpAddress() {
		Context ctx = getActivity().getApplicationContext();
		WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> l = wm.getConfiguredNetworks();
		
		return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
	}
}
