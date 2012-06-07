package us.miui.toolbox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.util.Log;

public class RootUtils {
	public static boolean requestRoot() {
		boolean retval = false;
		Process p;
		Runtime r = Runtime.getRuntime();
		try {
			p = r.exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			DataInputStream osRes = new DataInputStream(p.getInputStream());

			if (null != os && null != osRes) {
				// Getting the id of the current user to check if this is root
				os.writeBytes("id\n");
				os.flush();

				String currUid = osRes.readLine();
				boolean exitSu = false;
				if (null == currUid) {
					retval = false;
					exitSu = false;
					Log.d("ROOT", "Can't get root access or denied by user");
				} else if (true == currUid.contains("uid=0")) {
					retval = true;
					exitSu = true;
					Log.d("ROOT", "Root access granted");
				} else {
					retval = false;
					exitSu = true;
					Log.d("ROOT", "Root access rejected: " + currUid);
				}

				if (exitSu) {
					os.writeBytes("exit\n");
					os.flush();
				}
			}
		} catch (Exception e) {
			// Can't get root !
			// Probably broken pipe exception on trying to write to output
			// stream after su failed, meaning that the device is not rooted

			retval = false;
			Log.d("ROOT", "Root access rejected [" + e.getClass().getName()
					+ "] : " + e.getMessage());
		}

		return retval;
	}
	
	public static void execute(String commands) 
		throws IOException {
	    Process p;
	    Runtime r = Runtime.getRuntime();
		p = r.exec("su");
		DataOutputStream os = new DataOutputStream(p.getOutputStream());
		os.writeBytes(commands);
		os.writeBytes("exit\n");
		os.flush();
	}

	public static String executeWithResult(String commands) 
		throws IOException {
		String result = null;
		    Process p;
		    Runtime r = Runtime.getRuntime();
			p = r.exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			DataInputStream is = new DataInputStream(p.getInputStream());
			os.writeBytes(commands);
			os.writeBytes("exit\n");
			os.flush();
			result = is.readLine();
			return result;
		}
}
