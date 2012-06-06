/**
 * 
 */
package us.miui.toolbox;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

/**
 * @author Clark Scheff
 * 
 */
public class PerformancePrefFragment extends PreferenceFragment
		implements MediaScannerConnectionClient {
	private Preference mBatteryCalibration;
	private Preference mAndroidID;
	private Preference mMediaScanner;
	private MediaScannerConnection mMsc;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Resources res = this.getResources();
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.performance_settings);
		
		mBatteryCalibration = findPreference(res.getString(R.string.calibration_prefs_key));
		mBatteryCalibration.setOnPreferenceClickListener(mListener);

		mAndroidID = findPreference(res.getString(R.string.android_id_prefs_key));
		mAndroidID.setOnPreferenceClickListener(mListener);
		
		mMediaScanner = findPreference(res.getString(R.string.media_scanner_prefs_key));
		mMediaScanner.setOnPreferenceClickListener(mListener);
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
			} else if (preference == mMediaScanner) {
				Context ctx = getActivity().getApplicationContext();
				ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, 
						Uri.parse("file://" + Environment.getExternalStorageDirectory())));
				Toast.makeText(ctx, "The edia scanner has been started.", Toast.LENGTH_LONG).show();
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
}
