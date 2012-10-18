/**
 * 
 */
package us.miui.toolbox.fragment;

import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.*;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import us.miui.Toolbox;
import us.miui.helpers.DisplayHelper;
import us.miui.service.FanNavService;
import us.miui.toolbox.R.xml;

/**
 * @author Clark Scheff
 * 
 */
public class ExperimentalPrefFragment extends PreferenceFragment {

    FanNavService mService;
    boolean mBound = false;
    ListPreference mTriggerHeight;
    ListPreference mAutoHideTime;
    CheckBoxPreference mAutoHide;
    CheckBoxPreference mHideOnPress;
    ColorPickerPreference mTriggerColor;
    int mWidth = 20;
    int mHeight = 20;
    CheckBoxPreference mServiceEnabler;
    int mLayout = 0;
    int mColor;
    private ListPreference mNavbarHeight;
	private ContentResolver mCR;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(xml.experimental_settings);

		mCR = getActivity().getContentResolver();
		mNavbarHeight = (ListPreference) findPreference("pref_key_navbar_height");

        mTriggerHeight = (ListPreference)findPreference("pref_key_trigger_height");
        mTriggerHeight.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mHeight = DisplayHelper.getPixelSize(getActivity(), Integer.parseInt((String) newValue));
                if (mService.serviceEnabled()) {
                    mService.disableOverlay();
                    mService.enableOverlay(mHeight, mColor);
                }
                return true;
            }
        });

        mAutoHideTime = (ListPreference)findPreference("pref_key_autohide_time");

        mAutoHide = (CheckBoxPreference)findPreference("pref_key_autohide");
        mAutoHide.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mAutoHideTime.setEnabled((Boolean)newValue);
                return true;
            }
        });

        mHideOnPress = (CheckBoxPreference)findPreference("pref_key_hide_on_keypress");
        mHideOnPress.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean enabled = (Boolean)newValue;
                mAutoHide.setEnabled(!enabled);
                mAutoHideTime.setEnabled(!enabled);
                if (!enabled)
                    mAutoHideTime.setEnabled(mAutoHide.isChecked());
                return true;
            }
        });

        mServiceEnabler = (CheckBoxPreference)findPreference("pref_key_fannav_service");
        mServiceEnabler.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (mBound) {
                    boolean checked = (Boolean)newValue;
                    if (checked) {
                        if (mService.serviceEnabled())
                            mService.disableOverlay();
                        mService.enableOverlay(mHeight, mColor);
                        String navbarHeight = Settings.System.getString(mCR, Toolbox.NAVBAR_HEIGHT);
                        SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
                        editor.putString("pref_key_navbarheight_backup", navbarHeight);
                        editor.commit();
                        Settings.System
                                .putString(mCR, Toolbox.NAVBAR_HEIGHT, "0");
                        //SystemHelper.restartSystemUI(getActivity());
                        return true;
                    } else {
                        if (mService.serviceEnabled())
                            mService.disableOverlay();
                        String navbarHeight = getPreferenceManager().getSharedPreferences().getString("pref_key_navbarheight_backup", "48");
                        Settings.System
                                .putString(mCR, Toolbox.NAVBAR_HEIGHT, navbarHeight);
                    }

                    if (((Boolean)newValue).equals(Boolean.valueOf(mService.serviceEnabled())))
                        return true;
                }
                return false;
            }
        });

        mTriggerColor = (ColorPickerPreference)findPreference("pref_key_trigger_color");
        mTriggerColor.setAlphaSliderEnabled(true);
        mTriggerColor.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mColor = (Integer)newValue;
                if (mService.serviceEnabled()) {
                    mService.disableOverlay();
                    mService.enableOverlay(mHeight, mColor);
                }
                return false;
            }
        });

        getInitialValues();
    }

    private void getInitialValues() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mAutoHideTime.setEnabled(prefs.getBoolean("pref_key_autohide", false));
        mHeight = DisplayHelper.getPixelSize(getActivity(), Integer.parseInt(prefs.getString("pref_key_trigger_height", "20")));
        mColor = prefs.getInt("pref_key_trigger_color", 0xFFFFFFFF);
        if (mBound) {
            mServiceEnabler.setChecked(mService.serviceEnabled());
        }
    }

    @Override
    public void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        Intent intent = new Intent(getActivity(), FanNavService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FanNavService.FanNavServiceBinder binder = (FanNavService.FanNavServiceBinder) service;
            mService = binder.getService();
            mBound = true;
            mServiceEnabler.setChecked(mService.serviceEnabled());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mServiceEnabler.setChecked(false);
        }
    };
}
