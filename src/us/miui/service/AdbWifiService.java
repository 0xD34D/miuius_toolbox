/**
 * 
 */
package us.miui.service;

import java.io.IOException;
import java.util.List;

import us.miui.Toolbox;
import us.miui.helpers.SystemHelper;
import us.miui.toolbox.R;
import us.miui.toolbox.RootUtils;
import us.miui.toolbox.activity.ToolboxTabPagerActivity;
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
public class AdbWifiService extends Service {
	private static final String TAG = "ADB_WIFI_SERVICE";
	public static final String ACTION_ENABLE = "adb_wifi_enable";
	public static final String ACTION_DISABLE = "adb_wifi_disable";
	public static final String ACTION_ADB_WIFI_STATE_CHANGED = "us.miui.toolbox.ADB_WIFI_STATE_CHANGED";
	public static final String PREF_KEY_ENABLE_ADB_WIFI = "pref_key_enable_adb_wifi";
	public static final String PREF_KEY_ADB_PORT = "pref_key_adb_port";
	public static final String EXTRAS_PORT_NUMBER = "port_num";
	public static final String EXTRAS_IP_ADDR = "ip_addr";
	public static final String EXTRAS_ENABLED = "enabled";

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	AdbWifiService getService() {
            return AdbWifiService.this;
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent + ", " + intent.getAction());
    	String action = intent.getAction();
    	Intent i = new Intent();
    	i.setAction(ACTION_ADB_WIFI_STATE_CHANGED);
    	if (action.equals(ACTION_ENABLE)) {
        	int port = intent.getIntExtra("port_num", 5555);
        	enableAdbWifi(this, port);
        	savePreference(true);
        	i.putExtra(EXTRAS_PORT_NUMBER, port);
        	i.putExtra(EXTRAS_IP_ADDR, getLocalIpAddress(this));
        	i.putExtra(EXTRAS_ENABLED, true);
        	sendBroadcast(i);
        	return START_STICKY;
    	} else if (action.equals(ACTION_DISABLE)) {
    		disableAdbWifi(this);
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

	private void enableAdbWifi(Context ctx, int port) {
    	//registerReceiver(mWifiStateReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
		try {
			RootUtils.execute("setprop service.adb.tcp.port " + port + "\n" +
				"stop adbd\nstart adbd\n");
		} catch (IOException e) {
			return;
		}
		
		updateNotification(ctx, port);
	}
	
	private void disableAdbWifi(Context ctx) {
		try {
			RootUtils.execute("setprop service.adb.tcp.port -1\n" +
				"stop adbd\nstart adbd\n");
			removeNotification(ctx);
    		//unregisterReceiver(mWifiStateReceiver);
		} catch (IOException e) {
		}
	}
	
	private static void updateNotification(Context ctx, int port) {
		boolean alreadyEnabled = SystemHelper.isAdbWifiEnabled();
		String connection = getLocalIpAddress(ctx) + ":" + port;
		NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		if (alreadyEnabled)
			nm.cancel(42);
		Notification.Builder nb = new Notification.Builder(ctx);
		nb.setContentTitle("ADB via WiFi enabled");
		nb.setContentText("Connect to " + connection);
		nb.setSmallIcon(R.drawable.stat_sys_adb);
		nb.setTicker("ADB via WiFi enabled on " + connection);
		nb.setAutoCancel(false);
		nb.setWhen(System.currentTimeMillis());
		nb.setOngoing(true);
		Intent intent = new Intent(ctx, ToolboxTabPagerActivity.class);
		intent.setAction(ToolboxTabPagerActivity.ACTION_ADB_WIFI_SETTINGS);
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
	
	public static String getPort(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(Toolbox.PREFS, 0);
		return prefs.getString(PREF_KEY_ADB_PORT, "5555");
	}
	
	private void savePreference(boolean enabled) {
		SharedPreferences prefs = this.getSharedPreferences(Toolbox.PREFS, 0);
		Editor e = prefs.edit();
		e.putBoolean(PREF_KEY_ENABLE_ADB_WIFI, enabled);
		e.commit();
	}
}
