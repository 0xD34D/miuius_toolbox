/**
 * 
 */
package us.miui.toolbox.fragment;

import us.miui.toolbox.R;
import us.miui.toolbox.R.id;
import us.miui.toolbox.R.layout;

import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Clark Scheff
 *
 */
public class ChangeLogFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}		

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.change_log, container, false);
		loadChangeLog(v);
		
		return v;
	}



	private void loadChangeLog(View v) {
		TextView tv = (TextView)v.findViewById(R.id.logText);
		StringBuilder sb = new StringBuilder("");
		
		try {
			InputStreamReader isr = new InputStreamReader(getActivity().getAssets().open("changelog.txt"));
			int c;
			while ( (c = isr.read()) != -1)
				sb.append((char)c);
			isr.close();
			
		} catch (IOException e) {
		}
		
		tv.setText(sb.toString());
	}
}
