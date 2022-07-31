package com.adams.awcoe.spaceplanar.floors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.adams.awcoe.spaceplanar.app.R;


public class LocationFloorChooser extends Activity {
	//Android Variables
	Spinner location_chooser_spiner;
	Spinner location_floor_chooser_spinner;
	TextView location_floor_label;
	Button choosefloorOk;
	SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
	
	//Members Variables
	String chosen_location;
	Boolean map_exists = false;
	private File currentDirectory = new File("/");
	private File currentDirectory2 = new File("/");
	List<String> locationnamesindir = new ArrayList<String>();
	List<String> floornamesindir = new ArrayList<String>();
	
	private void init() {
		buildLocationName();
		if(locationnamesindir.isEmpty() == true){
			this.locationnamesindir.add("-- No Existing Location --");
			this.floornamesindir.add("-- No Existing Floor --");			
			location_chooser_spiner.setEnabled(false);
	    	location_floor_chooser_spinner.setEnabled(false);
	    } else {
	    	map_exists = true;
	    }
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,locationnamesindir);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		location_chooser_spiner.setAdapter(adapter);
		ArrayAdapter<String> adapter_floor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,floornamesindir);
		adapter_floor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		location_floor_chooser_spinner.setAdapter(adapter_floor);
	}
	
	private void startListeners() {
		location_chooser_spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {				
				chosen_location = location_chooser_spiner.getSelectedItem().toString();
                buildFLoorFromLocationName();
				ArrayAdapter<String> adapter_floor = new ArrayAdapter<String>(LocationFloorChooser.this, android.R.layout.simple_spinner_item,floornamesindir);
				adapter_floor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				location_floor_chooser_spinner.setAdapter(adapter_floor);				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});		
		
		choosefloorOk.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View arg0) {				
				if(map_exists == true){
					String chosen_location_floor=Environment.getExternalStorageDirectory() + "/SpacePlanARData/"+location_chooser_spiner.getSelectedItem().toString()+
							"/"+location_floor_chooser_spinner.getSelectedItem().toString()+"/"+"RoomMaps";
					String chosen_location_floorxml=Environment.getExternalStorageDirectory() + "/SpacePlanARData/"+location_chooser_spiner.getSelectedItem().toString()+
					"/"+location_floor_chooser_spinner.getSelectedItem().toString()+"/"+"floorMap.xml";
			        editor.putString("CHOSEN_LOCATION_FLOOR",chosen_location_floor);
			        editor.putString("CHOSEN_FLOOR",location_floor_chooser_spinner.getSelectedItem().toString());
			        editor.putString("CHOSEN_LOCATION",location_chooser_spiner.getSelectedItem().toString());
			        editor.putString("CHOSEN_LOCATION_FLOORXML",chosen_location_floorxml);
			        editor.putBoolean("MAP_EXISTS",true);
			        editor.commit();
			        Intent resultIntent = LocationFloorChooser.this.getIntent();
			        LocationFloorChooser.this.setResult(Activity.RESULT_OK, resultIntent);
			        finish();
				} else {
					editor.putBoolean("MAP_EXISTS",false);
					editor.commit();
					AlertDialog alertDialog = new AlertDialog.Builder(LocationFloorChooser.this).create();
					alertDialog.setTitle("Note");
					alertDialog.setMessage("No Map Exists! Create room map from main menu.");
					alertDialog.setIcon(R.drawable.symbol_error);
					alertDialog.setButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							});
					alertDialog.show();
				}
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		Intent resultIntent = LocationFloorChooser.this.getIntent();
        LocationFloorChooser.this.setResult(Activity.RESULT_FIRST_USER , resultIntent);
        finish();
	}

	private void buildLocationName() {		
		browseTo(new File(Environment.getExternalStorageDirectory() + "/SpacePlanARData"));
	}
	private void buildFLoorFromLocationName() {
		browseToLocation(new File(Environment.getExternalStorageDirectory() + "/SpacePlanARData"+chosen_location));
	}
	
	private void browseTo(final File aDirectory) {
		if (aDirectory.isDirectory()) {
			this.currentDirectory = aDirectory;
			fill(aDirectory.listFiles());
		} 
		
	}
	
	private void browseToLocation(final File aDirectory) {
		if (aDirectory.isDirectory()) {
			this.currentDirectory2 = aDirectory;
			fill_floor(aDirectory.listFiles());
		} 
		
	}
	
	private void fill(File[] files) {
		this.locationnamesindir.clear();
			int currentPathStringLenght = this.currentDirectory
					.getAbsolutePath().length();
			for (File file : files) {								
				if(!file.getName().contains("Temporary"))
					if(!file.getName().contains("OBJ"))
						if(!file.getName().contains("xml"))
					this.locationnamesindir.add(file.getAbsolutePath().substring(currentPathStringLenght));
			}
		
	}
	
	
	private void fill_floor(File[] files) {
		this.floornamesindir.clear(); 
			int currentPathStringLenght = this.currentDirectory2
					.getAbsolutePath().length();
			for (File file : files) {
				if(!file.getName().contains("OBJ"))
					if(!file.getName().contains("xml"))
				this.floornamesindir.add(file.getAbsolutePath().substring(
						currentPathStringLenght));
			}

	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Rect dialogBounds = new Rect();
		getWindow().getDecorView().getHitRect(dialogBounds);
		
		if(!dialogBounds.contains((int)ev.getX(), (int)ev.getY())){
			return false;
		} else {
			return super.dispatchTouchEvent(ev);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_chooser_spinner);
		location_chooser_spiner=(Spinner)findViewById(R.id.location_chooser_spinner);
		location_floor_chooser_spinner=(Spinner)findViewById(R.id.location_floor_chooser_spinner);
		location_floor_label=(TextView)findViewById(R.id.location_floor_label);
		choosefloorOk=(Button)findViewById(R.id.choosefloorOk);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocationFloorChooser.this);
	    editor = sharedPreferences.edit();
		init();
		startListeners();
	}
	
	

}
