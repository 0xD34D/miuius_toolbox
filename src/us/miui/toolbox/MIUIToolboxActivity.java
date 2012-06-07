package us.miui.toolbox;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class MIUIToolboxActivity extends Activity {
	public static final String ACTION_ADB_WIFI_SETTINGS = "adb_wifi_setings";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (intent.getAction().equals(ACTION_ADB_WIFI_SETTINGS))
			getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new AdbWifiPrefFragment()).commit();
		else 
			// Display the fragment as the main content.
			getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new ToolboxPrefFragment()).commit();
	}
}