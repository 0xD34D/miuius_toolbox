/**
 * 
 */
package us.miui.receiver;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import us.miui.Toolbox;
import us.miui.helpers.SystemHelper;
import us.miui.toolbox.RootUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author lithium
 *
 */
public class MountReceiver extends BroadcastReceiver {
	private static final String TAG = "MountReciever";
	/**
	 * 
	 */
	public MountReceiver() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs = context.getSharedPreferences(Toolbox.PREFS, 0);
		if (prefs.getBoolean("pref_key_enable_mount_log", false) == false)
			return;
		
		String action = intent.getAction();
		StringBuilder sb = new StringBuilder("[");
		if (action.equals(Intent.ACTION_MEDIA_EJECT))
			sb.append("EJECT");
		else if (action.equals(Intent.ACTION_MEDIA_MOUNTED))
			sb.append("MOUNTED");
		else if (action.equals(Intent.ACTION_MEDIA_REMOVED))
			sb.append("REMOVED");
		else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED))
			sb.append("UNMOUNTED");
		sb.append("]  ")
		.append(Calendar.getInstance().getTime().toLocaleString())
		.append("\n");
		if (Toolbox.DEBUG)
			Log.d(TAG, sb.toString());
		addLogEntry(sb.toString());
	}

	private void addLogEntry(String header) {
		try {
			File log = new File(SystemHelper.FILES_DIR + "/mount.log");
			if (!log.exists())
				log.createNewFile();
			RootUtils.execute("echo \"" + header + "\" >>" + SystemHelper.FILES_DIR + "/mount.log\n");
			RootUtils.execute("logcat -t 20 >>" + SystemHelper.FILES_DIR + "/mount.log\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
