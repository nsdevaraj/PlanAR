package com.adams.awcoe.spaceplanar.room;

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


public class ViewRoomMapAdapter extends BaseAdapter {
	 
    private Activity activity;
    private List<String> dataDir;
    private static LayoutInflater inflater=null;
 
    public ViewRoomMapAdapter(Activity a,List<String> directoryEntries) {

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
            vi = inflater.inflate(R.layout.room_viewmaps_list_row, null);
 
        TextView category = (TextView)vi.findViewById(R.id.mapFolder); // title
        @SuppressWarnings("unused")
		ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        dataDir.get(position);
        // Setting all values in listview
        category.setText(dataDir.get(position));
		return vi;
	}
}
