package us.miui.toolbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import us.miui.Toolbox;
import us.miui.carrierlogo.CarrierLogoHelper;
import us.miui.helpers.SystemHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Display;

public class MIUIToolboxActivity extends Activity {
	private static final String FILES_DIR = "/data/data/us.miui.toolbox/files";
	private static final String TAG = "Toolbox";
	private static final int RESULT_SELECT_IMAGE = 1;
	public static final String ACTION_ADB_WIFI_SETTINGS = "adb_wifi_setings";
	private Context mContext;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		Intent intent = getIntent();
		if (intent.getAction().equals(ACTION_ADB_WIFI_SETTINGS))
			getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new AdbWifiPrefFragment()).commit();
		else {
			if (!CarrierLogoHelper.carrierLogoExists()) {
				try {
					CarrierLogoHelper.copyDefaultCarrierLogo(mContext);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			CarrierLogoHelper.makeCarrierLogoWorldReadable();
			// Display the fragment as the main content.
			getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new ToolboxPrefFragment()).commit();
		}
	}

    @Override 
    public void onActivityResult(int requestCode, int resultCode, Intent data) { 
    	super.onActivityResult(requestCode, resultCode, data); 
    	if (requestCode == RESULT_SELECT_IMAGE) {
    		if (resultCode == RESULT_OK) {
    			if (data == null) {
    				Log.w(TAG, "Null data, but RESULT_OK, from image picker!");
    				return;
    			}
				String RealPath;
				Uri selectedImage = data.getData();
   				RealPath = getRealPathFromURI(selectedImage);
   				StatusbarPrefFragment spf = (StatusbarPrefFragment) getFragmentManager()
   						.findFragmentByTag("status_bar");
   				spf.setCustomLogoResult(RealPath);
    			Log.d(TAG, "User selected an image: " + RealPath);
    		}
    	}
    }

    public String getRealPathFromURI(Uri contentUri) {         
    	String [] proj={MediaColumns.DATA}; 
    	Cursor cursor = managedQuery( contentUri, 
    			proj, // Which columns to return 
    			null,       // WHERE clause; which rows to return (all rows) 
    			null,       // WHERE clause selection arguments (none) 
    			null); // Order-by clause (ascending by name) 
    	int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA); 
    	cursor.moveToFirst(); 
    	return cursor.getString(column_index);
    }

}