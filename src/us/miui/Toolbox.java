/**
 * This class will be added to framework.jar and used for convenience in
 * other packages as needed. 
 */
package us.miui;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * @author Clark Scheff
 *
 */
public class Toolbox {
	private final static String TAG = "Toolbox";
	public static final String PREFS = "us.miui.toolbox_preferences";
	public static final boolean DEBUG = false;
	public static final boolean ENABLE_DEVELOPMENT_OPTIONS = false;
    public static final boolean ENABLE_EXPERIMENTAL_OPTIONS = true;
	public static final String DEFAULT_CARRIER_LOGO_FILE = "carrier_logo.png";
	
	// Strings for retreiving settings using Settings.System.getXXXX
	public final static String CENTER_CLOCK = "center_clock";
	public final static String CLOCK_HIDE_AMPM = "hide_ampm";
	public final static String SINGLE_SIGNAL_BARS = "single_signal_bars";
	public final static String USE_CUSTOM_CARRIER = "use_custom_carrier";
	public final static String CUSTOM_CARRIER_TEXT = "custom_carrier_text";
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
	public final static String LOCKSCREEN_SHOW_HOME = "lockscreen_show_home";
	public final static String REMOVE_RECENTS = "remove_recents";
	public final static String USE_CARRIER_LOGO = "use_carrier_logo";
	public final static String CARRIER_LOGO_FILE = "carrier_logo_file";
	public final static String NAVBAR_HEIGHT = "navbar_height";
	
	public final static String SPOOFED_SCREEN_SIZE = "spoofed_screen_size";
	
	/* Launcher specific settings */
	public final static String HIDE_STATUS_BAR = "hide_status_bar";
	public final static String ALLOW_LAUNCHER_ROTATION = "allow_launcher_rotation";
	public final static String RESIZE_ANY_WIDGET = "resize_any_widget";
	
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
	
	public static String SCREEN_LAYOUT_WIDTH = "0";
	
	/**
	 * Retrieves the system setting for CENTER_CLOCK
	 * @param context context used to get a ContentResolver
	 * @return true if center clock is to be used, false otherwise
	 */
	public static boolean useCenterClock(Context context) {
		return Craftsman.useCenterClock(context);
	}

	/**
	 * Retrieves the system setting for CLOCK_HIDE_AMPM
	 * @param context context used to get a ContentResolver
	 * @return true if am/pm should be hidden, false otherwise
	 */
	public static boolean hideAMPM(Context context) {
		return Craftsman.hideAMPM(context);
	}

	/**
	 * Retrieves the system setting for SINGLE_SIGNAL_BARS
	 * @param context context used to get a ContentResolver
	 * @return true if single bars are to be used, false otherwise
	 */
	public static boolean useSingleBars(Context context) {
		return Craftsman.useSingleBars(context);
	}
	
	/**
	 * Retrieves the system setting for USE_CUSTOM_CARRIER
	 * @param context context used to get a ContentResolver
	 * @return true if custom carrier text is to be used, false otherwise
	 */
	public static boolean useCustomCarrierText(Context context) {
		return Craftsman.useCustomCarrierText(context);
	}
	
	/**
	 * Retrieves the system setting for USE_CARRIER_LOGO
	 * @param context context used to get a ContentResolver
	 * @return true if custom carrier text is to be used, false otherwise
	 */
	public static boolean useCarrierLogo(Context context) {
		return Craftsman.useCarrierLogo(context);
	}
	
	/**
	 * Retrieves the text to use for custom carrier from system settings
	 * @param context context used to get a ContentResolver
	 * @return the text to use for the carrier label
	 */
	public static String getCustomCarrierText(Context context) {
		return Craftsman.getCustomCarrierText(context);
	}
	
	public static Bitmap getCarrierLogo(Context context) {
		return Craftsman.getCarrierLogo(context);
	}

	/**
	 * Retrieves the system setting for HIDE_STATUS_BAR
	 * @param context context used to get a ContentResolver
	 * @return true if status bar is to be hidden in MiuiHome
	 */
	public static boolean hideStatusBar(Context context) {
		return SnapOn.hideStatusBar(context);
	}

	/**
	 * Retrieves the system setting for HIDE_SHORTCUT_TEXT
	 * @param context context used to get a ContentResolver
	 * @return true if shortcut text label is to be hidden in MiuiHome
	 */
	public static boolean hideShortcutText(Context context) {
		return SnapOn.hideShortcutText(context);
	}

	/**
	 * Retrieves the system setting for RESIZE_ANY_WIDGET
	 * @param context context used to get a ContentResolver
	 * @return true if all widgets can be resized
	 */
	public static boolean resizeAnyWidget(Context context) {
		return SnapOn.resizeAnyWidget(context);
	}

	/**
	 * Retrieves the system setting for ALLOW_LAUNCHER_ROTATION
	 * @param context context used to get a ContentResolver
	 * @return true if launcher should rotate into landscape orientation
	 */
	public static boolean rotateMiuiHome(Context context) {
		return SnapOn.rotateMiuiHome(context);
	}

	/**
	 * Retrieves the system setting for CUSTOM_CLOCK_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom clock color has been selected
	 */
	public static boolean useCustomClockColor(Context context) {
		return Craftsman.useCustomClockColor(context);
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_CLOCK_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomClockColor(Context context) {
		return Craftsman.getCustomClockColor(context);
	}

	/**
	 * Retrieves the system setting for CUSTOM_CARRIER_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom carrier color has been selected
	 */
	public static boolean useCustomCarrierColor(Context context) {
		return Craftsman.useCustomCarrierColor(context);
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_CARRIER_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomCarrierColor(Context context) {
		return Craftsman.getCustomCarrierColor(context);
	}

	/**
	 * Retrieves the system setting for CUSTOM_SIGNAL_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom signal color has been selected
	 */
	public static boolean useCustomSignalColor(Context context) {
		return Craftsman.useCustomSignalColor(context);
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_SIGNAL_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomSignalColor(Context context) {
		return Craftsman.getCustomSignalColor(context);
	}

	/**
	 * Retrieves the system setting for CUSTOM_NAVBAR_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom navbar color has been selected
	 */
	public static boolean useCustomNavbarColor(Context context) {
		return Craftsman.useCustomNavbarColor(context);
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_NAVBAR_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomNavbarColor(Context context) {
		return Craftsman.getCustomNavbarColor(context);
	}

	/**
	 * Retrieves the system setting for CUSTOM_BATTERY_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom battery color has been selected
	 */
	public static boolean useCustomBatteryColor(Context context) {
		return Craftsman.useCustomBatteryColor(context);
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_BATTERY_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomBatteryColor(Context context) {
		return Craftsman.getCustomBatteryColor(context);
	}

	/**
	 * Retrieves the system setting for CUSTOM_WIFI_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom wifi color has been selected
	 */
	public static boolean useCustomWifiColor(Context context) {
		return Craftsman.useCustomWifiColor(context);
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_WIFI_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomWifiColor(Context context) {
		return Craftsman.getCustomWifiColor(context);
	}

	/**
	 * Retrieves the system setting for CUSTOM_BLUETOOTH_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom bluetooth color has been selected
	 */
	public static boolean useCustomBluetoothColor(Context context) {
		return Craftsman.useCustomBluetoothColor(context);
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_BLUETOOTH_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomBluetoothColor(Context context) {
		return Craftsman.getCustomBluetoothColor(context);
	}

	/**
	 * Retrieves the system setting for CUSTOM_GPS_COLOR
	 * @param context context used to get a ContentResolver
	 * @return true if a custom gps color has been selected
	 */
	public static boolean useCustomGpsColor(Context context) {
		return Craftsman.useCustomGpsColor(context);
	}

	/**
	 * Retrieves the custom color selected for CUSTOM_GPS_COLOR
	 * @param context context used to get a ContentResolver
	 * @return the ARGB color selected by the user
	 */
	public static int getCustomGpsColor(Context context) {
		return Craftsman.getCustomGpsColor(context);
	}

	/**
	 * Retrieves the system setting for USE_NAVBAR_SEARCH
	 * @param context context used to get a ContentResolver
	 * @return true if search button should be shown in navbar
	 */
	public static boolean useNavbarSearch(Context context) {
		return SnapOn.useNavbarSearch(context);
	}

	/**
	 * Retrieves the system setting for ENABLE_NAVBAR
	 * @param context context used to get a ContentResolver
	 * @return true if navbar should be enabled
	 */
	public static boolean enableNavbar(Context context) {
		return SnapOn.enableNavbar(context);
	}

	/**
	 * Retrieves the system setting for LOCKSCREEN_SHOW_HOME
	 * @param context context used to get a ContentResolver
	 * @return true if home should be enabled on lockscreen
	 */
	public static boolean lockscreenShowHome(Context context) {
		return SnapOn.lockscreenShowHome(context);
	}
	
	private static String getScreenSize() {
		File f = new File(Environment.getExternalStorageDirectory() + "/.screen_size");
		if (!f.exists())
			return "0";
		DataInputStream is;
		try {
			is = new DataInputStream(new FileInputStream(f));
			String size = is.readLine();
			is.close();
			return size;
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
		}
		
		return "0";
	}
	
	public static Configuration getConfiguration(Configuration config_orig, DisplayMetrics dm) {
		Configuration config = new Configuration(config_orig);
		
		config.screenHeightDp = (int)((float)dm.heightPixels / dm.density);
		config.screenWidthDp = (int)((float)dm.widthPixels / dm.density);
		
		if (config.screenHeightDp < config.screenWidthDp)
			config.smallestScreenWidthDp = config.screenHeightDp;
		else
			config.smallestScreenWidthDp = config.screenWidthDp;
		
		int layout_long = config.screenLayout & Configuration.SCREENLAYOUT_LONG_MASK;
		
		if (dm.densityDpi > DisplayMetrics.DENSITY_HIGH)
			config.screenLayout = Configuration.SCREENLAYOUT_SIZE_XLARGE | layout_long;
		else if (dm.densityDpi >= DisplayMetrics.DENSITY_MEDIUM)
			config.screenLayout = Configuration.SCREENLAYOUT_SIZE_LARGE | layout_long;
		else if (dm.densityDpi >= DisplayMetrics.DENSITY_LOW)
			config.screenLayout = Configuration.SCREENLAYOUT_SIZE_NORMAL | layout_long;
		else
			config.screenLayout = Configuration.SCREENLAYOUT_SIZE_SMALL | layout_long;
		
		return config;
	}
	
	public static DisplayMetrics getDisplayMetrics(DisplayMetrics original) {
		DisplayMetrics dm = new DisplayMetrics();
		DisplayMetrics defaults = new DisplayMetrics();
		defaults.setToDefaults();
		dm.setTo(original);
		
		dm.density = defaults.density;
		dm.densityDpi = defaults.densityDpi;
		dm.scaledDensity = dm.density;
		dm.xdpi = dm.ydpi = dm.densityDpi;
		
		return dm;
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
	
	public static int getNavbarHeight(Context context) {
		return SnapOn.getNavbarHeight(context);
	}
	
	public static WindowManager.LayoutParams getNavbarLayoutParams(Context context, WindowManager.LayoutParams lp) {
		return SnapOn.getNavbarLayoutParams(context, lp);
	}
	
	private static class Craftsman {

        private static final boolean mIsAuthorized = getModVersion().contains("MIUIUS");
        private static final String UNAUTHORIZED = "Unauthorized use of MIUI.us Toolbox.";

        private static String getModVersion() {
            return SystemProperties.get("ro.modversion", "KANGED");
        }

        /**
		 * Retrieves the system setting for CENTER_CLOCK
		 * @param context context used to get a ContentResolver
		 * @return true if center clock is to be used, false otherwise
		 */
		public static boolean useCenterClock(Context context) {
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
		 * Retrieves the system setting for CLOCK_HIDE_AMPM
		 * @param context context used to get a ContentResolver
		 * @return true if am/pm should be hidden, false otherwise
		 */
		public static boolean hideAMPM(Context context) {
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
            boolean hide = false;
			ContentResolver cr = context.getContentResolver();
			
			try {
				hide = (Settings.System.getInt(cr,
						CLOCK_HIDE_AMPM) == 1);
			} catch (SettingNotFoundException e) {
				hide = false;
			}
			
			return hide;
		}

		/**
		 * Retrieves the system setting for SINGLE_SIGNAL_BARS
		 * @param context context used to get a ContentResolver
		 * @return true if single bars are to be used, false otherwise
		 */
		public static boolean useSingleBars(Context context) {
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
		 * Retrieves the system setting for USE_CARRIER_LOGO
		 * @param context context used to get a ContentResolver
		 * @return true if custom carrier text is to be used, false otherwise
		 */
		public static boolean useCarrierLogo(Context context) {
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
            boolean use = false;
			ContentResolver cr = context.getContentResolver();
			
			try {
				use = (Settings.System.getInt(cr,
						USE_CARRIER_LOGO) == 1);
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
            String text = "";
			ContentResolver cr = context.getContentResolver();
			
			text = Settings.System.getString(cr,
					CUSTOM_CARRIER_TEXT);
			if (text == null)
				text = "";
			
			return text;
		}
		
		public static Bitmap getCarrierLogo(Context context) {
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
            ContentResolver cr = context.getContentResolver();
			String fileName = Settings.System.getString(cr, CARRIER_LOGO_FILE);
			if (fileName == null)
				fileName = DEFAULT_CARRIER_LOGO_FILE;

			Uri logoUri = Uri.parse("content://us.miui.toolbox/" + fileName);
			ParcelFileDescriptor file = null;
			try {
				file = cr.openFileDescriptor(logoUri, "r");
				Bitmap bmp = BitmapFactory.decodeFileDescriptor(file.getFileDescriptor());
				file.close();
				return bmp;
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
			
			return null;
		}

		/**
		 * Retrieves the system setting for CUSTOM_CLOCK_COLOR
		 * @param context context used to get a ContentResolver
		 * @return true if a custom clock color has been selected
		 */
		public static boolean useCustomClockColor(Context context) {
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
	}

	private static class SnapOn {
        private static final boolean mIsAuthorized = getModVersion().contains("MIUIUS");
        private static final String UNAUTHORIZED = "Unauthorized use of MIUI.us Toolbox.";

        private static String getModVersion() {
            return SystemProperties.get("ro.modversion", "KANGED");
        }

        /**
		 * Retrieves the system setting for HIDE_STATUS_BAR
		 * @param context context used to get a ContentResolver
		 * @return true if status bar is to be hidden in MiuiHome
		 */
		public static boolean hideStatusBar(Context context) {
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
		 * Retrieves the system setting for RESIZE_ANY_WIDGET
		 * @param context context used to get a ContentResolver
		 * @return true if all widgets can be resized
		 */
		public static boolean resizeAnyWidget(Context context) {
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
            boolean resize = false;
			ContentResolver cr = context.getContentResolver();
			
			try {
				resize = (Settings.System.getInt(cr,
						RESIZE_ANY_WIDGET) == 1);
			} catch (SettingNotFoundException e) {
				resize = false;
			}
			
			return resize;
		}

		/**
		 * Retrieves the system setting for ALLOW_LAUNCHER_ROTATION
		 * @param context context used to get a ContentResolver
		 * @return true if launcher should rotate into landscape orientation
		 */
		public static boolean rotateMiuiHome(Context context) {
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
		 * Retrieves the system setting for USE_NAVBAR_SEARCH
		 * @param context context used to get a ContentResolver
		 * @return true if search button should be shown in navbar
		 */
		public static boolean useNavbarSearch(Context context) {
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
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
		 * Retrieves the system setting for LOCKSCREEN_SHOW_HOME
		 * @param context context used to get a ContentResolver
		 * @return true if home should be enabled on lockscreen
		 */
		public static boolean lockscreenShowHome(Context context) {
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
            boolean show = false;
			ContentResolver cr = context.getContentResolver();
			
			try {
				show = (Settings.System.getInt(cr,
						LOCKSCREEN_SHOW_HOME) == 1);
			} catch (SettingNotFoundException e) {
				show = false;
			}
			
			return show;
		}

		/**
		 * Retrieves the text to use for custom carrier from system settings
		 * @param context context used to get a ContentResolver
		 * @return the text to use for the carrier label
		 */
		public static int getNavbarHeight(Context context) {
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
            int dp = 48;
			ContentResolver cr = context.getContentResolver();
			
			String text = Settings.System.getString(cr,
					NAVBAR_HEIGHT);
			if (text == null)
				dp =  48;
			else
				dp = Integer.parseInt(text);
			
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			int px = (int)((float)dp * dm.density);
			Log.i(TAG, String.format("dp=%d, px=%d", dp, px));
			
			return px;
		}
		
		public static WindowManager.LayoutParams getNavbarLayoutParams(Context context, WindowManager.LayoutParams lp) {
            if (!mIsAuthorized) {
                throw new RuntimeException(UNAUTHORIZED);
            }
            lp.height = getNavbarHeight(context);
			return lp;
		}
	}
}
