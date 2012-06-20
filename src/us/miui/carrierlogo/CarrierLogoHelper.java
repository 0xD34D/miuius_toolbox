package us.miui.carrierlogo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import us.miui.Toolbox;
import us.miui.helpers.SystemHelper;

public class CarrierLogoHelper {
	private static final String FILES_DIR = "/data/data/us.miui.toolbox/files";
	
	public static void makeCarrierLogoWorldReadable() {
		SystemHelper.makeFileWorldReadable(Toolbox.DEFAULT_CARRIER_LOGO_FILE);
	}

    public static boolean carrierLogoExists() {
	    File f = new File(Toolbox.CARRIER_LOGO_FILE);
	    return f.exists();
    }

	public static void copyDefaultCarrierLogo(Context context) throws IOException{
		File filesDir = new File(FILES_DIR);
		if(!filesDir.exists())
			filesDir.mkdir();
		//Open your local db as the input stream
	    InputStream myInput = context.getAssets().open("carrier_logo.png");

	    // delete the file if the DB exists
	    File f = new File(Toolbox.DEFAULT_CARRIER_LOGO_FILE);
	    if(f.exists())
	    	f.delete();
	    //Open the empty db as the output stream
	    FileOutputStream myOutput = new FileOutputStream(Toolbox.DEFAULT_CARRIER_LOGO_FILE);

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
	}

	public static String copyCustomCarrierLogo(String src) throws IOException{
		File filesDir = new File(FILES_DIR);
		if(!filesDir.exists())
			filesDir.mkdir();
		//Open your local db as the input stream
	    FileInputStream myInput = new FileInputStream(src);

	    String dst;
	    int mid = src.lastIndexOf("/");
	    dst = FILES_DIR + "/" + src.substring(mid+1, src.length());
	    // delete the file if the DB exists
	    File f = new File(dst);
	    if(f.exists())
	    	f.delete();
	    //Open the empty db as the output stream
	    FileOutputStream myOutput = new FileOutputStream(dst);

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
	    
	    return dst;
	}
}
