/**
 * 
 */
package us.miui.toolbox.fragment;

import java.io.IOException;

import us.miui.Toolbox;
import us.miui.helpers.SystemHelper;
import us.miui.toolbox.LogcatActivity;
import us.miui.toolbox.R;
import us.miui.toolbox.RootUtils;
import us.miui.toolbox.activity.AndroidIDToolActivity;
import us.miui.toolbox.activity.BatteryCalibrationActivity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.widget.Toast;

/**
 * @author Clark Scheff
 * 
 */
public class SystemSettingsPrefFragment extends PreferenceFragment
		implements MediaScannerConnectionClient {
	private Preference mBatteryCalibration;
	private Preference mAndroidID;
	private Preference mMediaScanner;
	private Preference mLogcat;
	private EditTextPreference mDensity;
	private CheckBoxPreference mEnableNavbar;
	private ListPreference mRecoveryType;
	private MediaScannerConnection mMsc;
	private ContentResolver mCR;
	private String mCurrentDPI = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Resources res = this.getResources();
		mCR = getActivity().getContentResolver();

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.performance_settings);
		
		mBatteryCalibration = findPreference(res.getString(R.string.calibration_prefs_key));
		mBatteryCalibration.setOnPreferenceClickListener(mListener);

		mAndroidID = findPreference(res.getString(R.string.android_id_prefs_key));
		mAndroidID.setOnPreferenceClickListener(mListener);
		
		mMediaScanner = findPreference(res.getString(R.string.media_scanner_prefs_key));
		mMediaScanner.setOnPreferenceClickListener(mListener);
		
		mLogcat = findPreference(res.getString(R.string.logcat_prefs_key));
		mLogcat.setOnPreferenceClickListener(mListener);
		
		mRecoveryType = (ListPreference)findPreference(res.getString(R.string.recovery_selection_prefs_key));
		mRecoveryType.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String type = (String) newValue;
				try {
					SystemHelper.copyRecoveryOTA(getActivity(), type);
					return true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
		});
		
		mEnableNavbar = (CheckBoxPreference) findPreference(res.getString(R.string.enable_navbar_prefs_key));
		// Try to read the ENABLE_NAVBAR setting and if we get a
		// SettingNotFoundException we need to create it.
		try {
			mEnableNavbar
					.setChecked(Settings.System.getInt(mCR, Toolbox.ENABLE_NAVBAR) == 1);
		} catch (SettingNotFoundException e) {
			mEnableNavbar.setChecked(false);
			Settings.System.putInt(mCR, Toolbox.ENABLE_NAVBAR, 0);
		}
		mEnableNavbar.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Settings.System
				.putInt(mCR, Toolbox.ENABLE_NAVBAR, (Boolean) newValue
						.equals(Boolean.TRUE) ? 1 : 0);
				SystemHelper.restartSystemUI(getActivity().getApplicationContext());
				return true;
			}
		});

		// hide the Enable navbar preference until I find a way to make it work
		// or decide it is not worth the trouble :)
		getPreferenceScreen().removePreference(mEnableNavbar);
        if (!Toolbox.ENABLE_DEVELOPMENT_OPTIONS)
        	getPreferenceScreen().removePreference(mLogcat);
        
        mDensity = (EditTextPreference)findPreference(res.getString(R.string.lcd_density_prefs_key));
        try {
			mCurrentDPI = RootUtils.executeWithResult("cat /system/build.prop | busybox grep \"ro.sf.lcd_density\" | sed 's/ro.sf.lcd_density=//g'\n");
			mDensity.setPersistent(false);
			mDensity.setText(mCurrentDPI);
			mDensity.setDefaultValue(mCurrentDPI);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        mDensity.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String newDpi = (String) newValue;
				if (newDpi.equals(mCurrentDPI))
					return true;
				try {
					int dpiNumber = Integer.parseInt(newDpi);
					if (dpiNumber < 100 || dpiNumber > 480)
						return false;
				} catch (NumberFormatException nfe) {
					return false;
				}
				SystemHelper.mountSystemRW();
				try {
					Thread.sleep(250);
					RootUtils.execute(String.format("busybox sed -i 's/ro.sf.lcd_density=%s/ro.sf.lcd_density=%s/g' /system/build.prop\n",
							mCurrentDPI, newDpi));
					String result = RootUtils.executeWithResult("cat /system/build.prop | busybox grep \"ro.sf.lcd_density\"\n");
					if (result.contains(newDpi)) {
						mCurrentDPI = newDpi;
						suggestReboot();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SystemHelper.mountSystemRO();
				return true;
			}
		});

	}
	
	private OnPreferenceClickListener mListener = new OnPreferenceClickListener() {
		
		@Override
		public boolean onPreferenceClick(Preference preference) {
			if (preference == mBatteryCalibration) {
				Intent intent = new Intent(getActivity().getApplicationContext(), BatteryCalibrationActivity.class);
				startActivity(intent);
				return true;
			} else if (preference == mAndroidID) {
				Intent intent = new Intent(getActivity().getApplicationContext(), AndroidIDToolActivity.class);
				startActivity(intent);
				return true;
			} else if (preference == mLogcat) {
				Intent intent = new Intent(getActivity().getApplicationContext(), LogcatActivity.class);
				startActivity(intent);
				return true;
			} else if (preference == mMediaScanner) {
				Context ctx = getActivity().getApplicationContext();
				ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, 
						Uri.parse("file://" + Environment.getExternalStorageDirectory())));
				Toast.makeText(ctx, "The media scanner has been started.", Toast.LENGTH_LONG).show();
				return true;
			}
			return false;
		}
	};

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
		if (pref.getKey().equals(
				getActivity().getResources().getString(
						R.string.adb_wifi_prefs_key))) {
			// Display the fragment as the main content.
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new AdbWifiPrefFragment())
					.addToBackStack(null).commit();
			return true;
		}
		return false;
	}

	@Override
	public void onMediaScannerConnected() {
		Context ctx = getActivity().getApplicationContext();
		
		mMsc.scanFile(Environment.getExternalStorageDirectory().getPath(), null);
		
		NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder nb = new Notification.Builder(ctx);
		nb.setContentTitle("MIUI.us Toolbox");
		nb.setContentText("Media scanner started");
		nb.setSmallIcon(R.drawable.icon);
		nb.setTicker("Media scanner has been started.");
		nb.setAutoCancel(true);
		nb.setWhen(System.currentTimeMillis());
		nb.setOngoing(true);
		nm.notify(42, nb.getNotification());
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		Context ctx = getActivity().getApplicationContext();
		NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder nb = new Notification.Builder(ctx);
		nb.setContentTitle("MIUI.us Toolbox");
		nb.setContentText("Media scanner completed");
		nb.setSmallIcon(R.drawable.icon);
		nb.setTicker("Media scanner has completed.");
		nb.setAutoCancel(true);
		nb.setWhen(System.currentTimeMillis());
		nb.setOngoing(false);
		nm.notify(42, nb.getNotification());
	}

	public void suggestReboot() {
		new AlertDialog.Builder(getActivity())
		.setMessage("In order for changes to take effect you must reboot your device.\n" +
				"Would you like to reboot now?")
		.setTitle("Reboot?")
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				RootUtils.reboot();
				dialog.dismiss();
			}
			
		})
		.setNeutralButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.show();
	}
}
