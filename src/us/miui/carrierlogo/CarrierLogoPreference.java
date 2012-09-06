/**
 * 
 */
package us.miui.carrierlogo;

import java.io.IOException;

import us.miui.Toolbox;
import us.miui.helpers.SystemHelper;
import us.miui.toolbox.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * @author Clark Scheff
 *
 */
public class CarrierLogoPreference extends DialogPreference {
	private static final int RESULT_SELECT_IMAGE = 1;

	private ImageView mLogo;
	private Button mDefault;
	private Button mCustom;
	private Fragment mFragment;
	private String mLogoPath;
	private String mSrcPath;
	
	public CarrierLogoPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		setDialogLayoutResource(R.layout.dialog_carrier_logo);
	}

	public CarrierLogoPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.dialog_carrier_logo);
	}
	
	public void setFragment(Fragment fragment) {
		mFragment = fragment;
	}

	/* (non-Javadoc)
	 * @see android.preference.DialogPreference#onBindDialogView(android.view.View)
	 */
	@Override
	protected void onBindDialogView(View view) {
		mLogo = (ImageView) view.findViewById(R.id.preview);
		mDefault = (Button) view.findViewById(R.id.stock);
		mCustom = (Button) view.findViewById(R.id.choose);
		
		mDefault.setOnClickListener(mListener);
		mCustom.setOnClickListener(mListener);
		
		SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
		mLogoPath = getContext().getFilesDir() + "/" + 
				prefs.getString(getKey(), Toolbox.DEFAULT_CARRIER_LOGO_FILE);
		mSrcPath = mLogoPath;
		
		mLogo.setImageBitmap(BitmapFactory.decodeFile(mLogoPath));
		super.onBindDialogView(view);
	}

	/* (non-Javadoc)
	 * @see android.preference.DialogPreference#onDialogClosed(boolean)
	 */
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (!positiveResult)
			return;
		
		if (!mSrcPath.equals(mLogoPath)) {
			SystemHelper.removeFilesFromDir(getContext().getFilesDir().getAbsolutePath());
			try {
				CarrierLogoHelper.copyCustomCarrierLogo(mSrcPath);
			} catch (IOException e) {
			}
		}
		
		int mid = mLogoPath.lastIndexOf("/");
		String fname = mLogoPath.substring(mid+1, mLogoPath.length());
		SharedPreferences.Editor edit = getSharedPreferences().edit();
		edit.putString(getKey(), fname);
		edit.commit();
		
		getOnPreferenceChangeListener().onPreferenceChange(this, fname);
		super.onDialogClosed(positiveResult);
	}
	
	private OnClickListener mListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (v == mDefault) {
				try {
					CarrierLogoHelper.copyDefaultCarrierLogo(getContext());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//CarrierLogoHelper.makeCarrierLogoWorldReadable();
				mLogoPath = getContext().getFilesDir().getAbsolutePath() + "/" + Toolbox.DEFAULT_CARRIER_LOGO_FILE;
				mSrcPath = mLogoPath;
				mLogo.setImageBitmap(BitmapFactory.decodeFile(mLogoPath));
			} else if (v == mCustom) {
				Intent intent = new Intent(Intent.ACTION_PICK); 
                intent.setType("image/*");
                mFragment.startActivityForResult(intent, RESULT_SELECT_IMAGE);
			}
		}
	};
	
	public void setCustomLogoResult(String path) {
		try {
			//SystemHelper.removeFilesFromDir("/data/data/us.miui.toolbox/files");
			String dst = CarrierLogoHelper.copyCustomCarrierLogo(path);
			//SystemHelper.makeFileWorldReadable(dst);
			mLogoPath = dst;
			mSrcPath = path;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mLogo.setImageBitmap(BitmapFactory.decodeFile(mLogoPath));
	}
}
