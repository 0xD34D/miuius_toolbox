/**
 * 
 */
package us.miui.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import us.miui.Toolbox;
import us.miui.helpers.SystemHelper;
import us.miui.toolbox.FragmentTabsPager;
import us.miui.toolbox.R;
import us.miui.toolbox.RootUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;

/**
 * @author Clark Scheff
 *
 */
public class LogcatService extends Service {
	private static final String TAG = "LogcatService";
	public static final String ACTION_ENABLE = "logcat_enable";
	public static final String ACTION_DISABLE = "logcat_disable";
	public static final String ACTION_LOGCAT_STATE_CHANGED = "us.miui.toolbox.LOGCAT_STATE_CHANGED";
	public static final String PREF_KEY_ENABLE_LOGCAT = "pref_key_enable_adb_wifi";
	public static final String EXTRAS_ENABLED = "enabled";
	private static boolean mIsRunning = false;
	private static String mLogcatPID = "";
	private static String mCurrentLogFile = "";

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	LogcatService getService() {
            return LogcatService.this;
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent + ", " + intent.getAction());
    	String action = intent.getAction();
    	Intent i = new Intent();
    	i.setAction(ACTION_LOGCAT_STATE_CHANGED);
    	if (action.equals(ACTION_ENABLE)) {
        	startService(this);
        	savePreference(true);
        	i.putExtra(EXTRAS_ENABLED, true);
        	sendBroadcast(i);
        	return START_STICKY;
    	} else if (action.equals(ACTION_DISABLE)) {
    		stopService(this);
        	savePreference(false);
        	i.putExtra(EXTRAS_ENABLED, false);
        	sendBroadcast(i);
    		return START_NOT_STICKY;
    	}
    	
    	return START_NOT_STICKY;
    }
    
    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

	private void startService(Context ctx) {
		if (mIsRunning)
			return;
		mCurrentLogFile = "logcat_" + System.currentTimeMillis() + ".log";
		new Thread(mCaptureLog).start();
		mIsRunning = true;
		Log.i(TAG, String.format("Started service with pid=%s", mLogcatPID));
    	//registerReceiver(mWifiStateReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
		/*
		try {
			mCurrentLogFile = "logcat_" + System.currentTimeMillis() + ".log";
			mLogcatPID = RootUtils.executeWithResult("logcat > " + SystemHelper.FILES_DIR + "/" + 
					mCurrentLogFile + " & echo $!\n");
		} catch (IOException e) {
			return;
		}
		*/
		updateNotification(ctx);
	}
	
	private void stopService(Context ctx) {
		try {
			RootUtils.execute("kill " + mLogcatPID + ";logcat -c\n");
			removeNotification(ctx);
			mIsRunning = false;
			//RootUtils.execute("chmod 0766 " + SystemHelper.FILES_DIR + "/" + mCurrentLogFile +"\n");
    		//unregisterReceiver(mWifiStateReceiver);
		} catch (IOException e) {
		}
	}
	
	private static void updateNotification(Context ctx) {
		boolean alreadyEnabled = SystemHelper.isAdbWifiEnabled();
		NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		if (alreadyEnabled)
			nm.cancel(42);
		Notification.Builder nb = new Notification.Builder(ctx);
		nb.setContentTitle("Logcat service");
		nb.setContentText("Running in background");
		nb.setSmallIcon(R.drawable.stat_sys_adb);
		nb.setTicker("Logcat service enabled");
		nb.setAutoCancel(false);
		nb.setWhen(System.currentTimeMillis());
		nb.setOngoing(true);
		Intent intent = new Intent(ctx, FragmentTabsPager.class);
		intent.setAction(FragmentTabsPager.ACTION_ADB_WIFI_SETTINGS);
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, 0);
		nb.setContentIntent(contentIntent);
		nm.notify(42, nb.getNotification());
	}
	
	private static void removeNotification(Context ctx) {
		NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(42);
	}

	public static String getLocalIpAddress(Context context) {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> l = wm.getConfiguredNetworks();
		
		return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
	}
	
	private void savePreference(boolean enabled) {
		SharedPreferences prefs = this.getSharedPreferences(Toolbox.PREFS, 0);
		Editor e = prefs.edit();
		e.putBoolean(PREF_KEY_ENABLE_LOGCAT, enabled);
		e.commit();
	}
	
	private Runnable mCaptureLog = new Runnable() {

		@Override
		public void run() {
		    Process p;
		    Runtime r = Runtime.getRuntime();
			try {
				p = r.exec("su");
				DataOutputStream os = new DataOutputStream(p.getOutputStream());
				BufferedInputStream is = new BufferedInputStream(p.getInputStream());
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(SystemHelper.FILES_DIR + "/" + 
						mCurrentLogFile));
				byte[] buffer = new byte[1024];
				os.writeBytes("logcat\n");
				while(mIsRunning) {
					if (is.available() > 0) {
						is.read(buffer);
						out.write(buffer);
					}
					Thread.sleep(200);
				}
				if (is.available() > 0) {
					is.read(buffer);
					out.write(buffer);
				}
				is.close();
				out.close();
				os.writeBytes("exit\n");
				os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SystemHelper.copyFileToExternalData(SystemHelper.FILES_DIR + "/" + mCurrentLogFile);
		}
		
	};
}
