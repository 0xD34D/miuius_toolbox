/* The following code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package us.miui.widget;

import java.io.IOException;
import java.math.BigInteger;

import us.miui.helpers.CPUHelper;
import us.miui.toolbox.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.preference.Preference;
import android.widget.SeekBar;
import android.widget.TextView;

public class CPUSeekBarPreference extends Preference implements
		SeekBar.OnSeekBarChangeListener {
	private static final String androidns = "http://schemas.android.com/apk/res/android";
	private static final String customns = "http://schemas.android.com/apk/res/us.miui.toolbox";

	private SeekBar mSeekBar;
	private TextView mSplashText, mValueText;
	private Context mContext;
	
	private String[] mFrequencies;
	private String mDialogMessage, mSuffix;
	private int mDefault, mMax, mValue = 0;
	private int mMin;
	private boolean mIsMax;
	private boolean mEnabled = true;

	public CPUSeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		mDialogMessage = attrs.getAttributeValue(androidns, "dialogMessage");
		mSuffix = attrs.getAttributeValue(androidns, "text");
		mDefault = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
		try {
			mFrequencies = CPUHelper.getFrequencies();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mMax = mFrequencies.length - 1;//attrs.getAttributeIntValue(androidns, "max", 100);
		mMin = 0;//attrs.getAttributeIntValue(customns, "min", 0);
		mEnabled = attrs.getAttributeBooleanValue(androidns, "enabled", true);
		mIsMax = attrs.getAttributeBooleanValue(customns, "isMax", false);
		String freq;
		if (mIsMax) {
			freq = CPUHelper.getMaxFrequency();
			mValue = getIndexOfFrequency(freq);
		} else {
			freq = CPUHelper.getMinFrequency();
			mValue = getIndexOfFrequency(freq);
		}
	}

	@Override
	protected View onCreateView(ViewGroup vg) {
		LayoutInflater li = LayoutInflater.from(mContext);
		View v = li.inflate(R.layout.sbp_layout, null);

		mSplashText = (TextView) v.findViewById(R.id.title);
		if (mDialogMessage != null)
			mSplashText.setText(mDialogMessage);

		mValueText = (TextView) v.findViewById(R.id.level);

		mSeekBar = (SeekBar) v.findViewById(R.id.seekbar);
		mSeekBar.setOnSeekBarChangeListener(this);
		mSeekBar.setEnabled(mEnabled);

		//if (shouldPersist())
			//mValue = getPersistedInt(mDefault);

		mSeekBar.setMax(mMax);
		mSeekBar.setProgress(mValue);
		return v;
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		//if (restore)
			//mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
		//else
			//mValue = (Integer) defaultValue;
	}

	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		int min = Integer.valueOf(CPUHelper.getMinFrequency());
		int max = Integer.valueOf(CPUHelper.getMaxFrequency());
		if (mIsMax) {
			if (Integer.valueOf(mFrequencies[value]) < min) {
				value = getIndexOfFrequency(Integer.toString(min));
				mSeekBar.setProgress(value);
			}
			CPUHelper.setMaxFrequency(mFrequencies[value]);
		} else {
			if (Integer.valueOf(mFrequencies[value]) > max) {
				value = getIndexOfFrequency(Integer.toString(max));
				mSeekBar.setProgress(value);
			}
			CPUHelper.setMinFrequency(mFrequencies[value]);
		}
		String t = Integer.toString(Integer.valueOf(mFrequencies[value])/1000);
		mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));
		if (shouldPersist())
			persistInt(value);
		callChangeListener(new Integer(value));
	}

	public void onStartTrackingTouch(SeekBar seek) {
	}

	public void onStopTrackingTouch(SeekBar seek) {
	}

	public void setMax(int max) {
		mMax = max;
	}

	public int getMax() {
		return mMax;
	}

	public void setProgress(int progress) {
		mValue = progress;
		if (mSeekBar != null)
			mSeekBar.setProgress(progress);
	}

	public int getProgress() {
		return mValue;// + mMin;
	}
	
	public int getIndexOfFrequency(String freq) {
		for (int i = 0; i < mFrequencies.length; i++) {
			if (freq.equals(mFrequencies[i]))
				return i;
		}
		
		return -1;
	}
}
