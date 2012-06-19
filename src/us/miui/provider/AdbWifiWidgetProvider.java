package us.miui.provider;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import us.miui.helpers.SystemHelper;
import us.miui.service.AdbWifiService;
import us.miui.toolbox.R;

public class AdbWifiWidgetProvider extends AppWidgetProvider {
	private static final String TAG = "AdbWifiWidgetProvider";
	public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
	public static AdbWifiWidgetProvider mWidget = null;
	public static Context mContext;
	public static AppWidgetManager mAWM;
	public static int mIDs[];

	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onDeleted(android.content.Context, int[])
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
	}

	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onDisabled(android.content.Context)
	 */
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
	}

	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onEnabled(android.content.Context)
	 */
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
	}

	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		final String action = intent.getAction();
		if (action.equals(ACTION_WIDGET_RECEIVER)) {
			if (!SystemHelper.isAdbWifiEnabled()) {
				SharedPreferences prefs = context.getSharedPreferences("us.miui.toolbox_preferences", 0);
				int port = Integer.parseInt(prefs.getString("pref_key_adb_port", "5555"));
				Intent i = new Intent(context, AdbWifiService.class);
				i.setAction(AdbWifiService.ACTION_ENABLE);
				i.putExtra("port_num", port);
				context.startService(i);
			} else {
				Intent i = new Intent(context, AdbWifiService.class);
				i.setAction(AdbWifiService.ACTION_DISABLE);
				context.startService(i);
			}
		} else if (action.equals(AdbWifiService.ACTION_ADB_WIFI_STATE_CHANGED)) {
			Log.d(TAG, "Received ADB_WIFI_STATE_CHANGED");
			mWidget.onUpdate(null, null, null);
		}
	}

	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onUpdate(android.content.Context, android.appwidget.AppWidgetManager, int[])
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		if(null == context) context = AdbWifiWidgetProvider.mContext;
		if(null == appWidgetManager) appWidgetManager = AdbWifiWidgetProvider.mAWM;
		if(null == appWidgetIds) appWidgetIds = AdbWifiWidgetProvider.mIDs;
		
		AdbWifiWidgetProvider.mWidget = this;
		AdbWifiWidgetProvider.mContext = context;
		AdbWifiWidgetProvider.mAWM = appWidgetManager;
		AdbWifiWidgetProvider.mIDs = appWidgetIds;
		
		// Get all ids
		ComponentName thisWidget = new ComponentName(context,
				AdbWifiWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_adb_wifi);
			Intent active = new Intent(context, AdbWifiWidgetProvider.class);
			active.setAction(ACTION_WIDGET_RECEIVER);
			
			PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
			remoteViews.setOnClickPendingIntent(R.id.status_icon, actionPendingIntent);

			setStatusIcon(remoteViews);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}
	
	private void setStatusIcon(RemoteViews remoteViews) {
		if (SystemHelper.isAdbWifiEnabled())
			remoteViews.setImageViewResource(R.id.status_icon, R.drawable.widget_adb_on);
		else
			remoteViews.setImageViewResource(R.id.status_icon, R.drawable.widget_adb_off);
	}

	private void setStatusIcon(RemoteViews remoteViews, boolean enabled) {
		if (enabled)
			remoteViews.setImageViewResource(R.id.status_icon, R.drawable.widget_adb_on);
		else
			remoteViews.setImageViewResource(R.id.status_icon, R.drawable.widget_adb_off);
	}

	private void setAlarm(Context context) {
		Intent update = new Intent(context, AdbWifiWidgetProvider.class);
		update.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		PendingIntent updatePendingIntent = PendingIntent.getBroadcast(context, 0, update, 0);
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		c.add(Calendar.SECOND, 2);
		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), updatePendingIntent);
	}
}
