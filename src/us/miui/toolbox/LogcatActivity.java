package us.miui.toolbox;

import us.miui.Toolbox;
import us.miui.service.AdbWifiService;
import us.miui.service.LogcatService;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.support.v4.app.NavUtils;

public class LogcatActivity extends Activity {

	private Button mStartStop;
	private boolean mIsRunning = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logcat);
        mStartStop = (Button)findViewById(R.id.startStopButton);
        setInitialState();
        
        mStartStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!mIsRunning) {
					Intent i = new Intent(LogcatActivity.this, LogcatService.class);
					i.setAction(LogcatService.ACTION_ENABLE);
					startService(i);
				} else {
					Intent i = new Intent(LogcatActivity.this, LogcatService.class);
					i.setAction(LogcatService.ACTION_DISABLE);
					startService(i);
				}
			}
		});
    }
    
    private void setInitialState() {
		SharedPreferences prefs = this.getSharedPreferences(Toolbox.PREFS, 0);
		mIsRunning = prefs.getBoolean(LogcatService.PREF_KEY_ENABLE_LOGCAT, false);
		if (mIsRunning)
			mStartStop.setText("Stop service");
		else
			mStartStop.setText("Start service");
    }
    
    

    /* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(mLogcatServiceReceiver);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(LogcatService.ACTION_LOGCAT_STATE_CHANGED);
		registerReceiver(mLogcatServiceReceiver, filter);
		super.onResume();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_logcat, menu);
        return true;
    }

    private BroadcastReceiver mLogcatServiceReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			setInitialState();
		}
	};
}
