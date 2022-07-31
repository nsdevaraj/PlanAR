package com.adams.awcoe.spaceplanar.utils;

import java.util.List;

import com.adams.awcoe.spaceplanar.app.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {
	 
    private Activity activity;
    private List<String> dataDir;
    private static LayoutInflater inflater=null;
 
    public ListAdapter(Activity a,List<String> directoryEntries) {

    	activity = a;
    	dataDir=directoryEntries;
    	inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
        return dataDir.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }

	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.floor_newfloor_roomlist_row, null);
 
        TextView category = (TextView)vi.findViewById(R.id.mapFolder);
        @SuppressWarnings("unused")
		ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image);
        dataDir.get(position);
        category.setText(dataDir.get(position));
		return vi;
	}
}
