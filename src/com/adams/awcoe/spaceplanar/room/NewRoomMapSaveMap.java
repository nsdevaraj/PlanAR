package com.adams.awcoe.spaceplanar.room;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.adams.awcoe.spaceplanar.app.R;

public class NewRoomMapSaveMap extends Activity {
	//Android Variables
	EditText mapName;
	TextView mapLocation;
	Button saveMap;
	CheckBox checkAddToExisting;
	Spinner floor_names_list;
	EditText new_floor_name;
	List<String> floornamesindir = new ArrayList<String>();
	
	//Member Variables
	private String MAP_LOCATION;
	private String ROOM_HEIGHT;
	private String AREA_MAP;
	private String NO_WALLS;
	private File currentDirectory = new File("/");
	private enum DISPLAYMODE {
		ABSOLUTE, RELATIVE;
	}
	private final DISPLAYMODE displayMode = DISPLAYMODE.RELATIVE;
	
	private void buildFloorName(){		
		browseTo(new File(Environment.getExternalStorageDirectory() + "/SpacePlanARData/"+MAP_LOCATION));
	}
	private void browseTo(final File aDirectory) {
		if (aDirectory.isDirectory()) {
			this.currentDirectory = aDirectory;
			fill(aDirectory.listFiles());
		} 
		
	}
	private void fill(File[] files) {
		this.floornamesindir.clear();

		switch (this.displayMode) {
		case ABSOLUTE:
			for (File file : files) {
				this.floornamesindir.add(file.getPath());
			}
			break;
		case RELATIVE: 
			int currentPathStringLenght = this.currentDirectory
					.getAbsolutePath().length();
			for (File file : files) {
				if(!file.getName().contains("OBJ"))
					if(!file.getName().contains("xml"))
				this.floornamesindir.add(file.getAbsolutePath().substring(
						currentPathStringLenght));
			}
			break;
		}
	}
	
	
	private void init() {	
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NewRoomMapSaveMap.this);
	    MAP_LOCATION =  preferences.getString("LOCATION_MAP", "");
	    ROOM_HEIGHT=preferences.getString("HEIGHT_MAP", "");
	    AREA_MAP=preferences.getString("AREA_MAP","");
	    NO_WALLS=preferences.getString("NO_WALLS_MAP","");
		
		mapName=(EditText)findViewById(R.id.room_newmap_mapName);
		mapLocation=(TextView)findViewById(R.id.room_newmap_mapLocation);
		mapLocation.setText(MAP_LOCATION);
		checkAddToExisting=(CheckBox)findViewById(R.id.room_newmap_checkAddToExisting);
		new_floor_name=(EditText)findViewById(R.id.room_newmap_newfloorName);
		floor_names_list=(Spinner)findViewById(R.id.room_newmap_floorname_spinner);
		saveMap=(Button)findViewById(R.id.room_newmap_mapSave);
	}
	
	
	private void startListeners() {           
		//Toggle New Floor - Existing Floor Spinner Controls
		checkAddToExisting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(checkAddToExisting.isChecked()) {
					new_floor_name.setVisibility(View.INVISIBLE);
					floor_names_list.setVisibility(View.VISIBLE);					
				} else {
					new_floor_name.setVisibility(View.VISIBLE);
					floor_names_list.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		// Saving Map
         saveMap.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				File directory2 = new File(Environment.getExternalStorageDirectory()
						+ File.separator + "SpacePlanARData" + File.separator + MAP_LOCATION);
				directory2.mkdirs();
				String floor_directoryName;
				String map_Name = mapName.getText().toString().trim().replace(" ", "_");
				if(checkAddToExisting.isChecked()) {
					floor_directoryName = Environment.getExternalStorageDirectory() + File.separator + "SpacePlanARData" + File.separator +
						MAP_LOCATION + File.separator + floor_names_list.getSelectedItem().toString();
					File directory = new File(floor_directoryName + File.separator + "RoomMaps");
					directory.mkdirs();				
					
					NewRoomMapSaveMapXML obj = new NewRoomMapSaveMapXML();
					obj.saveMap(map_Name, ROOM_HEIGHT, MAP_LOCATION, AREA_MAP, NO_WALLS, 
							floor_directoryName + File.separator + "RoomMaps" + File.separator);
					finish();					
				} else {
					floor_directoryName = Environment.getExternalStorageDirectory() + File.separator + "SpacePlanARData" + File.separator +
						MAP_LOCATION + File.separator + new_floor_name.getText().toString().trim().replace(" ", "_");
					File directory = new File(floor_directoryName + File.separator + "RoomMaps");
					directory.mkdirs();
					NewRoomMapSaveMapXML obj = new NewRoomMapSaveMapXML();
					obj.saveMap(map_Name, ROOM_HEIGHT, MAP_LOCATION, AREA_MAP, NO_WALLS,
							floor_directoryName + File.separator + "RoomMaps" + File.separator);
					finish();					
				}				
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewRoomMapSaveMap.this);
			    SharedPreferences.Editor editor = sharedPreferences.edit();
			    editor.putString("MAP_SAVED","saved");
			    editor.commit();
			}
		});	
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_newmap_save);
		
		init();
		buildFloorName();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,floornamesindir);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		floor_names_list.setAdapter(adapter);
		startListeners();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
