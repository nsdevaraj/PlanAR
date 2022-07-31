package com.adams.awcoe.spaceplanar.room;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.adams.awcoe.spaceplanar.app.R;
import com.adams.awcoe.spaceplanar.floors.LocationFloorChooser;
import com.adams.awcoe.spaceplanar.room.mapdata.ReadAugmented3DPointsData;

public class ViewRoomMap extends Activity{

	private static int LOCATIONFLOOR_CHOOSER_requestCode=1860;
	private String CHOSEN_LOCATION_FLOOR_PATH="";
	public static ArrayList<ReadAugmented3DPointsData> readAugmented3DPointsCordinates;
	public static ArrayList<String> readMapInfo;
	public static ArrayList<Bitmap> textures_Base64_to_Image;
	ListView mapdirstructure;
	ViewRoomMapAdapter adapter;
	private List<String> roomEntries = new ArrayList<String>();
	private List<String> roomEntries_formatted=new ArrayList<String>();
	private File currentDirectory = new File("/");

	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		Intent i=new Intent(ViewRoomMap.this,LocationFloorChooser.class);
		startActivityForResult(i,LOCATIONFLOOR_CHOOSER_requestCode);
		setContentView(R.layout.room_viewmaps_list);
		mapdirstructure=(ListView)findViewById(R.id.mapdirstructure);
		mapdirstructure.setOnItemClickListener(new OnItemClickListener() {

			
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {				
				int selectionRowID = position;
				File clickedFile = null;
				clickedFile = new File(currentDirectory.getAbsolutePath()
								+ roomEntries.get(selectionRowID));				
					if (clickedFile != null)
						browseTo(clickedFile);
				
			}
		});		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == Activity.RESULT_OK){
			if (requestCode == LOCATIONFLOOR_CHOOSER_requestCode) {
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ViewRoomMap.this);
				Boolean map_exists = sharedPreferences.getBoolean("MAP_EXISTS", false);
				if(map_exists == true){
			        CHOSEN_LOCATION_FLOOR_PATH = sharedPreferences.getString("CHOSEN_LOCATION_FLOOR", "");
			        //Log.v("File", new File(CHOSEN_LOCATION_FLOOR_PATH).getName());
					browseTo(new File(CHOSEN_LOCATION_FLOOR_PATH));
				} else {					
					Toast.makeText(ViewRoomMap.this, "No Map Exists! Please create room map first.", Toast.LENGTH_LONG).show();
				}
			}
		} else {
			finish();
		}
	}
	
	/**
	 * This function browses to the root-directory of CHOSEN_LOCATION_FLOOR_PATH.
	 */
	/*private void browseToRoot() {
		browseTo(new File(CHOSEN_LOCATION_FLOOR_PATH));
	}*/

	private void browseTo(final File aDirectory) {
		if (aDirectory.isDirectory()) {
			this.currentDirectory = aDirectory;
			fill(aDirectory.listFiles());
		} else {		
			String roomName = aDirectory.getName().substring(0, aDirectory.getName().length() - 4);
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewRoomMap.this);
			// Setting Dialog Title
			alertDialog.setTitle("Confirm");

			// Setting Dialog Message
			alertDialog.setMessage("Do you want to Open the Room Map \"" + roomName + "\"?");

			// Setting Icon to Dialog
			alertDialog.setIcon(R.drawable.ic_launcher);

			// Setting Positive "Yes" Button
			alertDialog.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						String location=aDirectory.getPath();	
						ViewRoomMapReadMapXML obj=new ViewRoomMapReadMapXML(location);
						obj.readMap();
						Intent i=new Intent(ViewRoomMap.this,ViewRoomMap2D.class);
						startActivity(i);
						
						}
					});

			// Setting Negative "NO" Button
			alertDialog.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to invoke NO event

							dialog.cancel();
						}
					});

			// Showing Alert Message
			alertDialog.show();

		}
	}

		
	private void fill(File[] files) {
		this.roomEntries.clear();
			int currentPathStringLength = this.currentDirectory
					.getAbsolutePath().length();
			for (File file : files) {
				if(file.isFile()){
					this.roomEntries.add(file.getAbsolutePath().substring(
							currentPathStringLength));
				}				
			}
		for(int i=0;i<roomEntries.size();i++)
		{
			roomEntries_formatted.add(roomEntries.get(i).substring(1,roomEntries.get(i).length()-4));
		}
		adapter=new ViewRoomMapAdapter(ViewRoomMap.this,roomEntries_formatted);
		mapdirstructure.setAdapter(adapter);
	}

	@Override
	public void finish() {
		
		super.finish();
		ViewRoomMap.super.finish();
	}



	@Override
	public void onBackPressed() {		
		super.onBackPressed();
		finish();
	}
	
	
	
}

