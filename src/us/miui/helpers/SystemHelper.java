/**
 * 
 */
package us.miui.helpers;

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
}
