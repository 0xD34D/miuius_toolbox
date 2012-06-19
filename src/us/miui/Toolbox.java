/**
 * This class will be added to framework.jar and used for convenience in
 * other packages as needed. 
 */
package us.miui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

/**
 * @author Clark Scheff
 *
 */
public class Toolbox {
	private final static String TAG = "Toolbox";
	public static final String PREFS = "us.miui.toolbox_preferences";
	
	// Strings for retreiving settings using Settings.System.getXXXX
	public final static String CENTER_CLOCK = "center_clock";
	public final static String SINGLE_SIGNAL_BARS = "single_signal_bars";
	public final static String USE_CUSTOM_CARRIER = "use_custom_carrier";
	public final static String CUSTOM_CARRIER_TEXT = "custom_carrier_text";
	public final static String HIDE_STATUS_BAR = "hide_status_bar";
	public final static String ALLOW_LAUNCHER_ROTATION = "allow_launcher_rotation";
	public final static String HIDE_SHORTCUT_TEXT = "hide_shortcut_text";
	public final static String CUSTOM_CLOCK_COLOR = "custom_clock_color";
	public final static String CUSTOM_CARRIER_COLOR = "custom_carrier_color";
	public final static String CUSTOM_SIGNAL_COLOR = "custom_signal_color";
	public final static String CUSTOM_NAVBAR_COLOR = "custom_navbar_color";
	public final static String CUSTOM_BATTERY_COLOR = "custom_battery_color";
	public final static String CUSTOM_WIFI_COLOR = "custom_wifi_color";
	public final static String CUSTOM_BLUETOOTH_COLOR = "custom_bluetooth_color";
	public final static String CUSTOM_GPS_COLOR = "custom_bluetooth_color";
	public final static String USE_NAVBAR_SEARCH = "use_navbar_search";
	public final static String CUSTOM_NAVBAR_ORDER = "custom_navbar_order";
	public final static String ENABLE_NAVBAR = "enable_navbar";
	
	// names for navbar buttons
	public final static String HOME = "home";
	public final static String BACK = "back";
	public final static String RECENTS = "recents";
	public final static String MENU = "menu";
	public final static String SEARCH = "search";
	
	// resource IDs for navbar button images
	public final static int IC_SYSBAR_MENU = 0x7f02003d;
	public final static int IC_SYSBAR_MENU_LAND = 0x7f02003e;
	public final static int IC_SYSBAR_HOME = 0x7f020036;
	public final static int IC_SYSBAR_HOME_LAND = 0x7f020037;
	public final static int IC_SYSBAR_BACK = 0x7f02002e;
	public final static int IC_SYSBAR_BACK_LAND = 0x7f020032;
	public final static int IC_SYSBAR_RECENTS = 0x7f020040;
	public final static int IC_SYSBAR_RECENTS_LAND = 0x7f020041;
	public final static int IC_SYSBAR_SEARCH = 0x7f02019c;
	public final static int IC_SYSBAR_SEARCH_LAND = 0x7f02019d;
	
	// keycodes for the navbar buttons
	public final static int KEYCODE_SYSBAR_MENU = 82;
	public final static int KEYCODE_SYSBAR_HOME = 3;
	public final static int KEYCODE_SYSBAR_BACK = 4;
	public final static int KEYCODE_SYSBAR_RECENTS = 0;
	public final static int KEYCODE_SYSBAR_SEARCH = 84;
	
	// resource IDs for the navbar button views
	public final static int SYSBAR_SLOT1 = 0x7f100015; // menu
	public final static int SYSBAR_SLOT2 = 0x7f100016; // home
	public final static int SYSBAR_SLOT3 = 0x7f100017; // recents
	public final static int SYSBAR_SLOT4 = 0x7f100018; // back
	public final static int SYSBAR_SLOT5 = 0x7f1000c8; // search
	public final static int SYSBAR_SLOT1_LAND = 0x7f1000cc; // menu
	public final static int SYSBAR_SLOT2_LAND = 0x7f1000cb; // home
	public final static int SYSBAR_SLOT3_LAND = 0x7f1000ca; // recents
	public final static int SYSBAR_SLOT4_LAND = 0x7f1000c9; // back
	public final static int SYSBAR_SLOT5_LAND = 0x7f1000cd; // search
	
	
	// NxN grid size to use in MiuiHome based on device type
	public final static int HOME_TABLET_ROWS = 8;
	public final static int HOME_PHONE_ROWS = 4;
	public final static int HOME_TABLET_COLS = 6;
	public final static int HOME_PHONE_COLS = 4;

	/**
	 * Retrieves the system setting for CENTER_CLOCK
	 * @param context context used to get a ContentResolver
	 * @return true if center clock is to be used, false otherwise
	 */
	public static boolean useCenterClock(Context context) {
		boolean use = false;
		ContentResolver cr = context.getContentResolver();
		
		try {
			use = (Settings.System.getInt(cr,
					CENTER_CLOCK) == 1);
		} catch (SettingNotFoundException e) {
			use = false;
		}
		
		return use;
	}

	/**
	 * Retrieves the system setting for SINGLE_SIGNAL_BARS
	 * @param context context used to get a ContentResolver
	 * @return true if single bars are to be used, false otherwise
	 */
	public static boolean useSingleBars(Context context) {
		boolean use = false;
		ContentResolver cr = context.getContentResolver();
		
		try {
			use = (Settings.System.getInt(cr,
					SINGLE_SIGNAL_BARS) == 1);
		} catch (SettingNotFoundException e) {
			use = false;
		}
		
		return use;
	}
	
	/**
	 * Retrieves the system setting for USE_CUSTOM_CARRIER
	 * @param context context used to get a ContentResolver
	 * @return true if custom carrier text is to be used, false otherwise
	 */
	public static boolean useCustomCarrierText(Context context) {
		boolean use = false;
		ContentResolver cr = context.getContentResolver();
		
		try {
			use = (Settings.System.getInt(cr,
					USE_CUSTOM_CARRIER) == 1);
		} catch (SettingNotFoundException e) {
			use = false;
		}
		
		return use;
	}
	
	/**
	 * Retrieves the text to use for custom carrier from system settings
	 * @param context context used to get a ContentResolver
	 * @return the text to use for the carrier label
	 */
	public static String getCustomCarrierText(Context context) {
		String text = "";
		ContentResolver cr = context.getContentResolver();
		
		text = Settings.System.getString(cr,
				CUSTOM_CARRIER_TEXT);
		if (text == null)
			text = "";
		
		return text;
	}

	/**
	 * Retrieves the system setting for HIDE_STATUS_BAR
	 * @param context context used to get a ContentResolver
	 * @return true if status bar is to be hidden in MiuiHome
	 */
	public static boolean hideStatusBar(Context context) {
		boolean hide = false;
		ContentResolver cr = context.getContentResolver();
		
		try {
			hide = (Settings.System.getInt(cr,
					HIDE_STATUS_BAR) == 1);
		} catch (SettingNotFoundException e) {
			hide = false;
		}
		
		return hide;
	}

	/**
	 * Retrieves the system setting for HIDE_SHORTCUT_TEXT
	 * @param context context used to get a ContentResolver
	 * @return true if shortcut text label is to be hidden in MiuiHome
	 */
	public static boolean hideShortcutText(Context context) {
		boolean hide = false;
		ContentResolver cr = context.getContentResolver();
		
		try {
			hide = (Settings.System.getInt(cr,
					HIDE_SHORTCUT_TEXT) == 1);
		} catch (SettingNotFoundException e) {
			hide = false;
		}
		
		return hide;
	}

	/**
	 * Retrieves the system setting for ALLOW_LAUNCHER_ROTATION
	 * @param context context used to get a ContentResolver
	 * @return true if launcher should rotate into landscape orientation
	 */
	public static boolean rotateMiuiHome(Context context) {
		boolean hide = false;
		ContentResolver cr = context.getContentResolver();
		
		try {
			hide = (Settings.System.getInt(cr,
					ALLOW_LAUNCHER_ROTATION) == 1);
		} catch (SettingNotFoundException e) {
			hide = false;
		}
		
		return hide;
	}

	/**
	 * Retrieves the system setting for CUSTOM_CLOCK_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom clock color has been selected
	 */
	public static boolean useCustomClockColor(Context context) {
		boolean use = false;
		ContentResolver cr = context.getContentResolver();
		
		try {
			int color = Settings.System.getInt(cr,
					CUSTOM_CLOCK_COLOR);
			use = (color != 0);
		} catch (SettingNotFoundException e) {
			use = false;
		}
		
		return use;
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_CLOCK_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomClockColor(Context context) {
		int color = 0xFFFFFFFF;
		ContentResolver cr = context.getContentResolver();
		
		try {
			color = Settings.System.getInt(cr,
					CUSTOM_CLOCK_COLOR);
		} catch (SettingNotFoundException e) {
			color = 0xFFFFFFFF;
		}
		
		return color;
	}

	/**
	 * Retrieves the system setting for CUSTOM_CARRIER_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom carrier color has been selected
	 */
	public static boolean useCustomCarrierColor(Context context) {
		boolean use = false;
		ContentResolver cr = context.getContentResolver();
		try {
			int color = Settings.System.getInt(cr,
					CUSTOM_CARRIER_COLOR);
			use = (color != 0);
		} catch (SettingNotFoundException e) {
			use = false;
		}
		
		return use;
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_CARRIER_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomCarrierColor(Context context) {
		int color = 0xFFFFFFFF;
		ContentResolver cr = context.getContentResolver();
		
		try {
			color = Settings.System.getInt(cr,
					CUSTOM_CARRIER_COLOR);
		} catch (SettingNotFoundException e) {
			color = 0xFFFFFFFF;
		}
		
		return color;
	}

	/**
	 * Retrieves the system setting for CUSTOM_SIGNAL_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom signal color has been selected
	 */
	public static boolean useCustomSignalColor(Context context) {
		boolean use = false;
		ContentResolver cr = context.getContentResolver();
		
		try {
			Settings.System.getInt(cr,
					CUSTOM_SIGNAL_COLOR);
			use = true;
		} catch (SettingNotFoundException e) {
			use = false;
		}
		
		return use;
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_SIGNAL_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomSignalColor(Context context) {
		int color = 0xFFFFFFFF;
		ContentResolver cr = context.getContentResolver();
		
		try {
			color = Settings.System.getInt(cr,
					CUSTOM_SIGNAL_COLOR);
		} catch (SettingNotFoundException e) {
			color = 0xFFFFFFFF;
		}
		
		return color;
	}

	/**
	 * Retrieves the system setting for CUSTOM_NAVBAR_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom navbar color has been selected
	 */
	public static boolean useCustomNavbarColor(Context context) {
		boolean use = false;
		ContentResolver cr = context.getContentResolver();
		
		try {
			int color = Settings.System.getInt(cr,
					CUSTOM_NAVBAR_COLOR);
			use = (color != 0);
		} catch (SettingNotFoundException e) {
			use = false;
		}
		
		return use;
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_NAVBAR_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomNavbarColor(Context context) {
		int color = 0xFFFFFFFF;
		ContentResolver cr = context.getContentResolver();
		
		try {
			color = Settings.System.getInt(cr,
					CUSTOM_NAVBAR_COLOR);
		} catch (SettingNotFoundException e) {
			color = 0xFFFFFFFF;
		}
		
		return color;
	}

	/**
	 * Retrieves the system setting for CUSTOM_BATTERY_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom battery color has been selected
	 */
	public static boolean useCustomBatteryColor(Context context) {
		boolean use = false;
		ContentResolver cr = context.getContentResolver();
		
		try {
			int color =Settings.System.getInt(cr,
					CUSTOM_BATTERY_COLOR);
			use = (color != 0);
		} catch (SettingNotFoundException e) {
			use = false;
		}
		
		return use;
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_BATTERY_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomBatteryColor(Context context) {
		int color = 0xFFFFFFFF;
		ContentResolver cr = context.getContentResolver();
		
		try {
			color = Settings.System.getInt(cr,
					CUSTOM_BATTERY_COLOR);
		} catch (SettingNotFoundException e) {
			color = 0xFFFFFFFF;
		}
		
		return color;
	}

	/**
	 * Retrieves the system setting for CUSTOM_WIFI_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom wifi color has been selected
	 */
	public static boolean useCustomWifiColor(Context context) {
		boolean use = false;
		ContentResolver cr = context.getContentResolver();
		
		try {
			int color = Settings.System.getInt(cr,
					CUSTOM_WIFI_COLOR);
			use = (color != 0);
		} catch (SettingNotFoundException e) {
			use = false;
		}
		
		return use;
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_WIFI_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomWifiColor(Context context) {
		int color = 0xFFFFFFFF;
		ContentResolver cr = context.getContentResolver();
		
		try {
			color = Settings.System.getInt(cr,
					CUSTOM_WIFI_COLOR);
		} catch (SettingNotFoundException e) {
			color = 0xFFFFFFFF;
		}
		
		return color;
	}

	/**
	 * Retrieves the system setting for CUSTOM_BLUETOOTH_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom bluetooth color has been selected
	 */
	public static boolean useCustomBluetoothColor(Context context) {
		boolean use = false;
		ContentResolver cr = context.getContentResolver();
		
		try {
			int color = Settings.System.getInt(cr,
					CUSTOM_BLUETOOTH_COLOR);
			use = (color != 0);
		} catch (SettingNotFoundException e) {
			use = false;
		}
		
		return use;
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_BLUETOOTH_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomBluetoothColor(Context context) {
		int color = 0xFFFFFFFF;
		ContentResolver cr = context.getContentResolver();
		
		try {
			color = Settings.System.getInt(cr,
					CUSTOM_BLUETOOTH_COLOR);
		} catch (SettingNotFoundException e) {
			color = 0xFFFFFFFF;
		}
		
		return color;
	}

	/**
	 * Retrieves the system setting for CUSTOM_GPS_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom gps color has been selected
	 */
	public static boolean useCustomGpsColor(Context context) {
		boolean use = false;
		ContentResolver cr = context.getContentResolver();
		
		try {
			int color = Settings.System.getInt(cr,
					CUSTOM_GPS_COLOR);
			use = (color != 0);
		} catch (SettingNotFoundException e) {
			use = false;
		}
		
		return use;
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_GPS_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomGpsColor(Context context) {
		int color = 0xFFFFFFFF;
		ContentResolver cr = context.getContentResolver();
		
		try {
			color = Settings.System.getInt(cr,
					CUSTOM_GPS_COLOR);
		} catch (SettingNotFoundException e) {
			color = 0xFFFFFFFF;
		}
		
		return color;
	}

	/**
	 * Retrieves the system setting for USE_NAVBAR_SEARCH
	 * @param context context used to get a ContentResolver
	 * @return true if search button should be shown in navbar
	 */
	public static boolean useNavbarSearch(Context context) {
		boolean use = false;
		ContentResolver cr = context.getContentResolver();
		
		try {
			use = (Settings.System.getInt(cr,
					USE_NAVBAR_SEARCH) == 1);
		} catch (SettingNotFoundException e) {
			use = false;
		}
		
		return use;
	}

	/**
	 * Retrieves the system setting for ENABLE_NAVBAR
	 * @param context context used to get a ContentResolver
	 * @return true if navbar should be enabled
	 */
	public static boolean enableNavbar(Context context) {
		boolean use = false;
		ContentResolver cr = context.getContentResolver();
		
		try {
			use = (Settings.System.getInt(cr,
					ENABLE_NAVBAR) == 1);
		} catch (SettingNotFoundException e) {
			use = false;
		}
		
		return use;
	}

	/**
	 * Retrieves an array with the button order for the navbar
	 * @param context context used to get a ContentResolver
	 * @return an array with the order of the navbar buttons
	 */
	public static String[] getNavbarOrder(Context context) {
		ContentResolver cr = context.getContentResolver();
		String[] order = {"menu", "home", "recents", "back", "search"};
		String tmp = Settings.System.getString(cr, CUSTOM_NAVBAR_ORDER);
		if (tmp != null) {
			order = tmp.split(" ");
		}
		
		Log.i(TAG, "getNavbarOrder()=" + order[0] + order[1] + order[2] + order[3] + order[4]);
		return order;
	}
	
	/**
	 * Get the resource ID for a navbar button based on its name
	 * @param context context used to get a ContentResolver
	 * @param name the name of the button
	 * @return the resource ID for the drawable to use with this button
	 */
	public static int getNavbarButtonResID(boolean isPortrait, String name) {
		int resID = 0;
		if (name.equals(HOME))
			resID = (isPortrait ? IC_SYSBAR_HOME : IC_SYSBAR_HOME_LAND);
		else if (name.equals(MENU))
			resID = (isPortrait ? IC_SYSBAR_MENU : IC_SYSBAR_MENU_LAND);
		else if (name.equals(BACK))
			resID = (isPortrait ? IC_SYSBAR_BACK : IC_SYSBAR_BACK_LAND);
		else if (name.equals(SEARCH))
			resID = (isPortrait ? IC_SYSBAR_SEARCH : IC_SYSBAR_SEARCH_LAND);
		else if (name.equals(RECENTS))
			resID = (isPortrait ? IC_SYSBAR_RECENTS : IC_SYSBAR_RECENTS_LAND);
		
		Log.i(TAG, "getNavbarButtonResID(" + name + ", " + isPortrait + ")=" + resID);
		return resID;
	}
	
	/**
	 * Get the keycode for a navbar button based on its name
	 * @param name the name of the button
	 * @return the keycode associated with this button
	 */
	public static int getNavbarButtonKeycode(String name) {
		if (name.equals(HOME))
			return KEYCODE_SYSBAR_HOME;
		else if (name.equals(MENU))
			return KEYCODE_SYSBAR_MENU;
		else if (name.equals(BACK))
			return KEYCODE_SYSBAR_BACK;
		else if (name.equals(SEARCH))
			return KEYCODE_SYSBAR_SEARCH;
		else if (name.equals(RECENTS))
			return KEYCODE_SYSBAR_RECENTS;
		return 0;
	}
	
	public static String getButtonName(Context context, int id) {
		String[] order = getNavbarOrder(context);
		String name = "";
		switch(id) {
		case SYSBAR_SLOT1:
		case SYSBAR_SLOT1_LAND:
			name = order[0];
			break;
		case SYSBAR_SLOT2:
		case SYSBAR_SLOT2_LAND:
			name = order[1];
			break;
		case SYSBAR_SLOT3:
		case SYSBAR_SLOT3_LAND:
			name = order[2];
			break;
		case SYSBAR_SLOT4:
		case SYSBAR_SLOT4_LAND:
			name = order[3];
			break;
		case SYSBAR_SLOT5:
		case SYSBAR_SLOT5_LAND:
			name = order[4];
			break;
		}
		Log.i(TAG, "getButtonName(" + id + ")=" + name);
		return name;
	}
	
	public static int getNavbarButtonResID(Context context, boolean isPortrait, int id) {
		int newID = getNavbarButtonResID(isPortrait, getButtonName(context, id));
		
		Log.i(TAG, "getNavbarButtonResID(" + id + ")=" + newID);
		return newID;
	}
	
	public static int getNavbarButtonKeycode(Context context, int id) {
		return getNavbarButtonKeycode(getButtonName(context, id));
	}
	
	public static Drawable getNavbarButtonDrawable(Context context, boolean isPortrait, int id) {
		Resources res = context.getResources();
		return res.getDrawable(getNavbarButtonResID(context, isPortrait, id));
	}
	
	public static int getNewNavbarButtonID(Context context, int id) {
		String name = getButtonName(context, id);
		if (name.equals(MENU))
			return SYSBAR_SLOT1;
		if (name.equals(HOME))
			return SYSBAR_SLOT2;
		if (name.equals(RECENTS))
			return SYSBAR_SLOT3;
		if (name.equals(BACK))
			return SYSBAR_SLOT4;
		if (name.equals(SEARCH))
			return SYSBAR_SLOT5;
		
		return id;
	}
	
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration()
				.smallestScreenWidthDp >= 600);
	}
	
	public static int getMiuiHomeRows(Context context) {
		return (isTablet(context) ? HOME_TABLET_ROWS : HOME_PHONE_ROWS);
	}

	public static int getMiuiHomeCols(Context context) {
		return (isTablet(context) ? HOME_TABLET_COLS : HOME_PHONE_COLS);
	}
}
