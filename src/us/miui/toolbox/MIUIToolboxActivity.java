package us.miui.toolbox;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class MIUIToolboxActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new ToolboxPrefFragment()).commit();
	}
}