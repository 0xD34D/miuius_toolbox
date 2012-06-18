/**
 * 
 */
package us.miui.helpers;

import java.io.IOException;
import java.util.List;

import us.miui.toolbox.RootUtils;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

/**
 * @author Clark Scheff
 *
 */
public class SystemHelper {
	/**
	 * Resource ID for the boolean value stored in framework-res for MIUI
	 */
	public static final int CONFIG_SHOW_NAVIGATION = 0x0111002e;
	
	public static boolean hasNavigationBar(Context context) {
		return (context.getResources()
				.getBoolean(CONFIG_SHOW_NAVIGATION) ||
				isTablet(context));
	}
	
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration()
				.smallestScreenWidthDp >= 600);
	}

	public static void restartApp(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context.getSystemService(
				Context.ACTIVITY_SERVICE);

		List<ActivityManager.RunningAppProcessInfo> apps = am
				.getRunningAppProcesses();
		for (RunningAppProcessInfo app : apps) {
			if (app.processName.equals(packageName)) {
				int pid = app.pid;
				try {
					RootUtils.execute("kill " + pid + "\n");
					return;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void restartSystemUI(Context context) {
		restartApp(context, "com.android.systemui");
	}

	public static void restartMiuiHome(Context context) {
		restartApp(context, "com.miui.home");
	}

	public static boolean isAdbWifiEnabled() {
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
}
