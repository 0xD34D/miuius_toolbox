package us.miui.toolbox;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import us.miui.Toolbox;
import us.miui.widget.TouchListView;

import us.miui.toolbox.R;

public class NavbarOrderSelector extends ListActivity {
	private static String[] items;;
	private static int[] item_icons = {R.drawable.ic_sysbar_menu, R.drawable.ic_sysbar_home, 
		R.drawable.ic_sysbar_recent, R.drawable.ic_sysbar_back, R.drawable.ic_sysbar_search};
	private static HashMap<String, Integer> icon_mapping = new HashMap<String, Integer>();
	private IconicAdapter adapter=null;
	//private Pair<String, Integer> pair = new Pair<String, Integer>();
	private ArrayList<Pair<String, Integer>> array = new ArrayList<Pair<String, Integer>>();
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.navbar_order);
		
		loadOrder();
		
		Pair<String, Integer> p;
		for (int i = 0; i < items.length; i++) {
			p = new Pair<String, Integer>(items[i], icon_mapping.get(items[i]));
			array.add(p);
		}
		
		TouchListView tlv=(TouchListView)getListView();
		adapter=new IconicAdapter();
		setListAdapter(adapter);
		
		tlv.setDropListener(onDrop);
		tlv.setRemoveListener(onRemove);
	}
	
	private TouchListView.DropListener onDrop=new TouchListView.DropListener() {
		@Override
		public void drop(int from, int to) {
				Pair<String, Integer> item=adapter.getItem(from);
				
				adapter.remove(item);
				adapter.insert(item, to);
				
				storeOrder();
		}
	};
	
	private TouchListView.RemoveListener onRemove=new TouchListView.RemoveListener() {
		@Override
		public void remove(int which) {
				adapter.remove(adapter.getItem(which));
		}
	};
	
	private void loadOrder() {
		ContentResolver cr = this.getContentResolver();
		icon_mapping.put("menu", R.drawable.ic_sysbar_menu);
		icon_mapping.put("home", R.drawable.ic_sysbar_home);
		icon_mapping.put("back", R.drawable.ic_sysbar_back);
		icon_mapping.put("recents", R.drawable.ic_sysbar_recent);
		icon_mapping.put("search", R.drawable.ic_sysbar_search);

		String[] order = {"menu", "home", "recents", "back", "search"};
		String tmp = Settings.System.getString(cr, Toolbox.CUSTOM_NAVBAR_ORDER);
		if (tmp != null) {
			order = tmp.split(" ");
		}
		
		items = order;
	}
	
	private void storeOrder() {
		ContentResolver cr = this.getContentResolver();
		String order = "";
		for (int i = 0; i < adapter.getCount(); i++) {
			order += adapter.getItem(i).first;
			if (i < adapter.getCount() - 1)
				order += " ";
		}
		
		Settings.System.putString(cr, Toolbox.CUSTOM_NAVBAR_ORDER, order);
	}
	
	class IconicAdapter extends ArrayAdapter<Pair<String, Integer>> {
		IconicAdapter() {
			super(NavbarOrderSelector.this, R.layout.row2, array);
		}
		
		public View getView(int position, View convertView,
												ViewGroup parent) {
			View row=convertView;
			
			if (row==null) {													
				LayoutInflater inflater=getLayoutInflater();
				
				row=inflater.inflate(R.layout.row2, parent, false);
			}
			Pair<String, Integer> p = array.get(position);
			TextView label=(TextView)row.findViewById(R.id.label);
			
			label.setText(p.first);
			
			ImageView icon = (ImageView)row.findViewById(R.id.navbar_icon);
			icon.setImageResource(p.second);
			
			return(row);
		}
	}
}
