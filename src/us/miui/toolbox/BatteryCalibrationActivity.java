/**
 * 
 */
package us.miui.toolbox;

import java.io.IOException;

import us.miui.toolbox.SimpleDialogs.OnYesNoResponse;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * @author Clark Scheff
 *
 */
public class BatteryCalibrationActivity extends Activity {
	private static final int STATE_STEP1 = 1;
	private static final int STATE_STEP2 = 2;
	private static final int STATE_STEP3 = 3;
	private static final int STATE_STEP4 = 4;
	private static final int STATE_COMPLETED = 5;
	
	private int mState = STATE_STEP1;
	private CheckBox mStep1;
	private CheckBox mStep2;
	private CheckBox mStep3;
	private CheckBox mStep4;
	private CheckBox mPlayAlarm;
	private TextView mCharge;
	private Button mCalibrate;
	private Button mOverride;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.battery_calibration_layout);
		
		mStep1 = (CheckBox) findViewById(R.id.step1);
		mStep2 = (CheckBox) findViewById(R.id.step2);
		mStep3 = (CheckBox) findViewById(R.id.step3);
		mStep4 = (CheckBox) findViewById(R.id.step4);
		mPlayAlarm = (CheckBox) findViewById(R.id.play_alarm);
		mStep1.setClickable(false);
		mStep2.setClickable(false);
		mStep3.setClickable(false);
		mStep4.setClickable(false);
		mCalibrate = (Button) findViewById(R.id.calibrate);
		mOverride = (Button) findViewById(R.id.override);
		mCharge = (TextView) findViewById(R.id.charge_info);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(mBatInfoReceiver, filter);
		
		mCalibrate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mState == STATE_STEP3) {
					try {
						RootUtils.execute("rm /data/system/batterystats.bin\n");
						mState = STATE_STEP4;
						mStep3.setChecked(true);
						mStep4.setEnabled(true);
						mCalibrate.setEnabled(false);
					} catch (IOException e) {
					}
				}
			}
		});

		mOverride.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mState == STATE_STEP2) {
					SimpleDialogs.displayYesNoDialog("YES", "NO", 
							"Are you sure?", 
							"Overriding this step may not yield the best results.\n\n" +
							"Are you sure you would like to overide step 2?", 
							BatteryCalibrationActivity.this, overrideConfirmation);
				}
			}
		});
	}
	
	OnYesNoResponse overrideConfirmation = new OnYesNoResponse() {
		
		@Override
		public void onYesNoResponse(boolean isYes) {
			if (isYes) {
				mState = STATE_STEP3;
				mStep2.setChecked(true);
				mStep3.setEnabled(true);
				mCalibrate.setEnabled(true);
			}
		}
	};

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
			int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
			int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
			
			mCharge.setText("" + level + "%, " + voltage + "mV");
			
			updateState(level, status, plugged);
		}
	};
	
	private void updateState(int level, int status, int plugged) {
		switch (mState) {
		case STATE_STEP1:
			if (plugged != 0) {
				if (level != 100) {
					mState = STATE_STEP2;
					mStep1.setChecked(true);
					mStep2.setEnabled(true);
					if (level >= 90)
						mOverride.setEnabled(true);
				} else {
					mState = STATE_STEP3;
					mStep1.setChecked(true);
					mStep2.setChecked(true);
					mStep2.setEnabled(true);
					mStep3.setEnabled(true);
					mCalibrate.setEnabled(true);
				}
			}
			break;
		case STATE_STEP2:
			if (status == BatteryManager.BATTERY_STATUS_FULL || level == 100) {
				mState = STATE_STEP3;
				mStep2.setChecked(true);
				mStep3.setEnabled(true);
				mCalibrate.setEnabled(true);
				if (mPlayAlarm.isChecked()) {
					Ringtone ringtone = RingtoneManager.getRingtone(this, 
							RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
					if (ringtone != null) {
						ringtone.play();
					}
				}
			} else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING ||
					status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
				mState = STATE_STEP1;
				mStep1.setChecked(false);
				mStep2.setEnabled(false);
				mOverride.setEnabled(false);
			} else if (level >= 90) {
				mOverride.setEnabled(true);
			}
			break;
		case STATE_STEP3:
			if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING ||
					status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
				mState = STATE_STEP1;
				mStep1.setChecked(false);
				mStep2.setChecked(false);
				mStep2.setEnabled(false);
				mStep3.setEnabled(false);
				mCalibrate.setEnabled(false);
				mOverride.setEnabled(false);
			}
			break;
		case STATE_STEP4:
			if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING ||
					status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
				SimpleDialogs.displayOkDialog("Finished", 
						"You should now allow your device to fully discharge and shut off on it's own.  Once the device " +
						"is powered down, please plug in the charger and allow it to charge to 100% before turning it back on.", this);
				mState = STATE_COMPLETED;
				mStep4.setChecked(true);
			}
			break;
		case STATE_COMPLETED:
			break;
		}
	}
}
