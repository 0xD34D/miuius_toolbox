/* The following code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package us.miui.widget;

import us.miui.toolbox.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.preference.Preference;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarPreference extends Preference implements
		SeekBar.OnSeekBarChangeListener {
	private static final String androidns = "http://schemas.android.com/apk/res/android";
	private static final String customns = "http://schemas.android.com/apk/res/us.miui.toolbox";

	private SeekBar mSeekBar;
	private TextView mSplashText, mValueText;
	private Context mContext;

	private String mDialogMessage, mSuffix;
	private int mDefault, mMax, mValue = 0;
	private int mMin;
	private boolean mEnabled = true;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		mDialogMessage = attrs.getAttributeValue(androidns, "dialogMessage");
		mSuffix = attrs.getAttributeValue(androidns, "text");
		mDefault = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
		mMax = attrs.getAttributeIntValue(androidns, "max", 100);
		mMin = attrs.getAttributeIntValue(customns, "min", 0);
		mEnabled = attrs.getAttributeBooleanValue(androidns, "enabled", true);
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

		if (shouldPersist())
			mValue = getPersistedInt(mDefault);

		mSeekBar.setMax(mMax - mMin);
		mSeekBar.setProgress(mValue-mMin);
		return v;
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		if (restore)
			mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
		else
			mValue = (Integer) defaultValue;
	}

	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		String t = String.valueOf(value + mMin);
		mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));
		if (shouldPersist())
			persistInt(value+mMin);
		callChangeListener(new Integer(value+mMin));
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
		mValue = progress + mMin;
		if (mSeekBar != null)
			mSeekBar.setProgress(progress);
	}

	public int getProgress() {
		return mValue;// + mMin;
	}
}
