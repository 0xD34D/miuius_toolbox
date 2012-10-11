/**
 * 
 */
package us.miui.toolbox.activity;

import us.miui.helpers.SystemHelper;
import us.miui.toolbox.R;
import us.miui.toolbox.R.id;
import us.miui.toolbox.R.layout;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Clark Scheff
 * 
 */
public class MountLogActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_log);

		((Button) findViewById(R.id.okButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});

		loadMountLog();
	}

	private void loadMountLog() {
		TextView tv = (TextView) findViewById(R.id.logText);
		StringBuilder sb = new StringBuilder("");
		File log = new File(SystemHelper.FILES_DIR + "/mount.log");
		if (log.exists()) {

			try {
				InputStreamReader isr = new InputStreamReader(new FileInputStream(log));
				int c;
				while ((c = isr.read()) != -1)
					sb.append((char) c);
				isr.close();

			} catch (IOException e) {
			}
		} else {
			sb.append("** NO LOG AVAILABLE **");
		}

		tv.setText(sb.toString());
	}
}
