package us.miui.toolbox.activity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import us.miui.toolbox.R.drawable;
import us.miui.toolbox.R.id;
import us.miui.toolbox.R.layout;
import us.miui.toolbox.R.string;
import us.miui.toolbox.SimpleDialogs.OnYesNoResponse;
import us.miui.toolbox.R;
import us.miui.toolbox.RootUtils;
import us.miui.toolbox.SimpleDialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class AndroidIDToolActivity extends Activity {
	private static final String FILES_DIR = "/data/data/us.miui.toolbox/files";
	private static final String SETTINGS_DB = "/data/data/com.android.providers.settings/databases/settings.db";
	private static final String SQL_SELECT_QUERY = "sqlite3 " + SETTINGS_DB + " 'select value from secure where name=\"android_id\"'";
	private static final String BACKUP_ID = Environment.getExternalStorageDirectory().toString() + "/android_id.txt";
	Context mContext;
	Runtime mRuntime;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        
        if (!RootUtils.requestRoot()) {
        	SimpleDialogs.displayOkDialog(R.string.dialog_root_required_title, 
        			R.string.dialog_root_required_body, this);
        }
        
        if (!binaryExists())
			try {
				copySqlite3Binary();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Toast.makeText(this, R.string.error_copying_binary, Toast.LENGTH_LONG).show();
			}
        
        setContentView(R.layout.android_id_layout);
        
        TextView tv = (TextView)findViewById(R.id.current_id);
        tv.setText(getAndroidID());

        updateBackupInfo();
        Button  b = (Button)findViewById(R.id.backup_id_button);
        b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(backupExists()) {
					SimpleDialogs.displayYesNoDialog("Yes", "No", 
							"Backup exists", "A backup already exists. Do you wish to continue and overwrite the current backup?", 
							mContext, overwriteBackupCallback);
				} else {
					try {
						createBackupID();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
        
        b = (Button)findViewById(R.id.restore_id_button);
        b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!backupExists()) {
					SimpleDialogs.displayOkDialog("No Backup", 
							"There is no backed up ID to restore.", 
							mContext);
					return;
				}
				String backup = "";
				try {
					backup = getBackupID();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (getAndroidID().equals(backup)) {
					SimpleDialogs.displayOkDialog("Same ID", 
							"Both the current and the backed up ID are the same. No need to perform this action.", 
							mContext);
					return;
				}
				restoreID(backup);
			}
		});
    }
    
    OnYesNoResponse overwriteBackupCallback = new OnYesNoResponse() {
		
		@Override
		public void onYesNoResponse(boolean isYes) {
			if (isYes) {
				try {
					createBackupID();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
    
    private boolean binaryExists() {
	    File f = new File(Environment.getDataDirectory().toString() + "/files/sqlite3");
	    return f.exists();
    }
    
    private boolean backupExists() {
	    File f = new File(BACKUP_ID);
	    return f.exists();
    }
    
    private String getBackupID() throws IOException {
	    File f = new File(BACKUP_ID);
	    String id = null;
	    if (!f.exists())
	    	return null;
	    
	    FileInputStream fis = new FileInputStream(f);
	    DataInputStream dis = new DataInputStream(fis);
	    id = dis.readLine();
	    dis.close();
	    fis.close();
	    
	    return id;
    }
    
    private void createBackupID() throws IOException {
	    File f = new File(BACKUP_ID);
	    if(f.exists())
	    	f.delete();
	    if(f.createNewFile()) {
	    	DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
	    	dos.writeBytes(getAndroidID());
	    	dos.close();
	    	if(backupExists())
		    	SimpleDialogs.displayOkDialogIcon("Backup created", "Your ID has been backed up successfully.", 
		    			mContext, R.drawable.checkmark);
	    } else {
	    	SimpleDialogs.displayOkDialogIcon("Backup failed", "Unable to create backup.", 
	    			mContext, R.drawable.warning);
	    }
	    
	    updateBackupInfo();
    }
    
    private void restoreID(String id) {
		changeAndroidID(id);
        TextView tv = (TextView)findViewById(R.id.current_id);
        tv.setText(readID());
        if (readID().equals(id)) {
        	//SimpleDialogs.displayOkDialogIcon("ID Restored", 
        			//"The saved android ID was successfully restored.", mContext, R.drawable.checkmark);
        	suggestReboot();
        } else {
        	SimpleDialogs.displayOkDialogIcon("Restore failed", 
        			"The saved android ID was not restored.", mContext, R.drawable.warning);
        }
    }
    
    private void updateBackupInfo() {
        if (backupExists()) {
        	try {
				String id = getBackupID();
				if (id != null) {
					findViewById(R.id.backup_view).setVisibility(View.VISIBLE);
			        TextView tv = (TextView)findViewById(R.id.backup_id);
			        tv.setText(id);
		        	findViewById(R.id.no_backup_view).setVisibility(View.GONE);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else {
			findViewById(R.id.backup_view).setVisibility(View.GONE);
        	findViewById(R.id.no_backup_view).setVisibility(View.VISIBLE);
        }
    }
    
	private void copySqlite3Binary() throws IOException{
		File filesDir = new File(FILES_DIR);
		if(!filesDir.exists())
			filesDir.mkdir();
		//Open your local db as the input stream
	    InputStream myInput = mContext.getAssets().open("sqlite3");

	    // delete the file if the DB exists
	    File f = new File(FILES_DIR + "/sqlite3");
	    if(f.exists())
	    	f.delete();
	    //Open the empty db as the output stream
	    FileOutputStream myOutput = new FileOutputStream(FILES_DIR + "/sqlite3");

	    //transfer bytes from the inputfile to the outputfile
	    byte[] buffer = new byte[1024];
	    int length;
	    while ((length = myInput.read(buffer))>0){
	        myOutput.write(buffer, 0, length);
	    }

	    //Close the streams
	    myOutput.flush();
	    myOutput.close();
	    myInput.close();

		// make the binary executable
		try {
			RootUtils.execute("chmod 0755 " + FILES_DIR + "/sqlite3\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getAndroidID() {
		return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
	}
	
	private String readID() {
		String id = "";
	    Process p;
		try {
			p = mRuntime.exec("su");
			String cmd = FILES_DIR + "/" + SQL_SELECT_QUERY;
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			DataInputStream is = new DataInputStream(p.getInputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			id = is.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return id;
	}
	
	private void changeAndroidID(String id) {
		StringBuilder cmd = new StringBuilder();
		cmd.append(FILES_DIR); cmd.append("/");
		cmd.append("sqlite3 "); cmd.append(SETTINGS_DB); cmd.append(" ");
		cmd.append("'UPDATE secure SET value=\"");
		cmd.append(id); cmd.append("\" ");
		cmd.append("WHERE name=\"android_id\"'\n");
		
		try {
			RootUtils.execute(cmd.toString());
		} catch (IOException e) {
			SimpleDialogs.displayOkDialog(R.string.dialog_update_id_failure_title,
					R.string.dialog_update_id_failure_body, mContext);
		}
	}

	private void reboot() {
		try {
			RootUtils.execute("reboot\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void suggestReboot() {
		new AlertDialog.Builder(mContext)
		.setMessage("Device ID was restored successfully. In order for changes to take effect you must reboot your device.\n\n" +
				"Would you like to reboot now?")
		.setTitle("Reboot?")
		.setIcon(R.drawable.restart)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				reboot();
				dialog.dismiss();
			}
			
		})
		.setNeutralButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.show();
	}

}