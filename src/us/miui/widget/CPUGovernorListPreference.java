/**
 * 
 */
package us.miui.widget;

import java.io.IOException;

import us.miui.helpers.CPUHelper;
import us.miui.toolbox.R;
import us.miui.toolbox.RootUtils;

import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.preference.Preference.OnPreferenceChangeListener;

/**
 * @author Clark Scheff
 *
 */
public class CPUGovernorListPreference extends ListPreference
	implements OnPreferenceChangeListener {
    private Context mContext;
    private TextView mCurrent;
	/**
	 * @param context
	 */
	public CPUGovernorListPreference(Context context) {
		super(context);
				
		try {
			setEntries(CPUHelper.getGovernors());
			setEntryValues(CPUHelper.getGovernors());
			setValue(CPUHelper.getGovernor());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mContext = context;
		this.setOnPreferenceChangeListener(this);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CPUGovernorListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		try {
			setEntries(CPUHelper.getGovernors());
			setEntryValues(CPUHelper.getGovernors());
			setValue(CPUHelper.getGovernor());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mContext = context;
		this.setOnPreferenceChangeListener(this);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		View v = super.onCreateView(parent);
        final LayoutInflater layoutInflater =
            (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        final View layout = layoutInflater.inflate(R.layout.clp_layout, parent, false); 
        ((TextView)layout.findViewById(android.R.id.title)).setText(((TextView)v.findViewById(android.R.id.title)).getText()); 
        ((TextView)layout.findViewById(android.R.id.summary)).setText(((TextView)v.findViewById(android.R.id.summary)).getText());
        LinearLayout wf = (LinearLayout)v.findViewById(android.R.id.widget_frame);
        LinearLayout wf_new = (LinearLayout)layout.findViewById(android.R.id.widget_frame);
        wf_new.setLayoutParams(wf.getLayoutParams());

        for(int i = 0; i < wf.getChildCount(); i++) {
        	View child = wf.getChildAt(i);
        	wf.removeView(child);
        	wf_new.addView(child);
        }
        return layout;
	}
	
	@Override
	protected void onBindView(View v) {
		super.onBindView(v);
		mCurrent = (TextView)v.findViewById(R.id.current);
		int index = this.findIndexOfValue(getValue());
		if ( index < 0 || index >= getEntries().length )
			index = 0;
		mCurrent.setText(this.getEntries()[index]);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		int index = this.findIndexOfValue((String)newValue);
		if ( index < 0 || index >= getEntries().length )
			index = 0;
		mCurrent.setText(this.getEntries()[index]);
		CPUHelper.setGovernor((String) newValue);
		return true;
	}
}
