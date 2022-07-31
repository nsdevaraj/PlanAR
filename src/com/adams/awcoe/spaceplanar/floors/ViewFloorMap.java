package com.adams.awcoe.spaceplanar.floors;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.adams.awcoe.spaceplanar.app.R;
import com.adams.awcoe.spaceplanar.floors.modeldata.FloorModelPointsCordinate;
import com.adams.awcoe.spaceplanar.room.ViewRoomMap;
import com.adams.awcoe.spaceplanar.room.ViewRoomMap2D;
import com.adams.awcoe.spaceplanar.room.mapdata.ReadAugmented3DPointsData;

public class ViewFloorMap extends Activity {
	class PointMaxMin {
		float x_max;
		float x_min;
		float y_max;
		float y_min;
		String RoomName;
		String No_of_Walls;
		String Area;
		String Location;
		String Height;
	}

	private static int LOCATIONFLOOR_CHOOSER_XMLrequestCode = 1891;
	private String CHOSEN_LOCATION_FLOORXML_PATH;
	private String CHOSEN_LOCATION;
	private String CHOSEN_FLOOR;
	private String SELECTED_ROOM_NAME, SELECTED_ROOM_AREA, SELECTED_ROOM_NOOFWALLS, SELECTED_ROOM_HEIGHT;
	public static ArrayList<ArrayList<FloorModelPointsCordinate>> floor_model;
	public static ArrayList<ArrayList<String>> all_room_info;
	ViewFloorMap2DRenderer map2drenderer;
	static public ArrayList<PointMaxMin> mapsMaxMinPoints;
	
	
	//Android Variables
	AlertDialog alertDialog = null;
	/*TextView floor_view_locationname;
	TextView floor_view_floorname;
	TextView floor_view_room_roomName; 
	TextView floor_view_room_noOfWalls;
	TextView floor_view_room_heightofRoom;
	TextView floor_view_room_area;
	TextView floor_view_room_wordlocation;*/
	ProgressDialog load_floor_progress_dialog;
	MenuItem floor_newmap_roomdetails_menu;
	static public float floor_x_max;
	static public float floor_x_min;
	static public float floor_y_max;
	static public float floor_y_min;
	static public boolean room_touched_flag = false;
	static public int room_touched_index;
	
	@Override
	public void onBackPressed() {
		/*for(PointMaxMin mpn : mapsMaxMinPoints){
			mpn = null;
		}*/
		CHOSEN_LOCATION_FLOORXML_PATH = null;
		CHOSEN_LOCATION = null;
		CHOSEN_FLOOR = null;
		SELECTED_ROOM_NAME = null;
		SELECTED_ROOM_AREA = null;
		SELECTED_ROOM_NOOFWALLS = null;
		SELECTED_ROOM_HEIGHT = null;
		room_touched_flag = false;
		room_touched_index = -1;
		/*for(ArrayList<FloorModelPointsCordinate> fmdl : floor_model){
			fmdl = null;
		}
		map2drenderer = null;*/
		finish();		
	}
	
	private void init_draw() {
		ViewFloorMapXMLReader obj = new ViewFloorMapXMLReader(CHOSEN_LOCATION_FLOORXML_PATH);		
		obj.readXML();
		floor_x_max=floor_x_min=floor_y_max=floor_y_min=0;
		findMaxMininRoom();
	}

	private void findMaxMininRoom() {	
		for (int index = 0; index < floor_model.size(); index++) {			
			PointMaxMin p=new PointMaxMin();
			ArrayList<FloorModelPointsCordinate> singlemapModel = floor_model
					.get(index);
			ArrayList<String> info=ViewFloorMap.all_room_info.get(index);
			float map_x_max = singlemapModel.get(0).getXMark();
			float map_x_min = singlemapModel.get(0).getXMark();
			float map_y_max = singlemapModel.get(0).getYMark();
			float map_y_min = singlemapModel.get(0).getYMark();
            for(int i=1;i<singlemapModel.size();i++) {
            	if(map_x_max<singlemapModel.get(i).getXMark())
            		map_x_max=singlemapModel.get(i).getXMark();
            	if(map_x_min>singlemapModel.get(i).getXMark())
            		map_x_min=singlemapModel.get(i).getXMark();
            	if(map_y_max<singlemapModel.get(i).getYMark())
            		map_y_max=singlemapModel.get(i).getYMark();
            	if(map_y_min>singlemapModel.get(i).getYMark())
            		map_y_min=singlemapModel.get(i).getYMark();
            }
           if(floor_x_max<map_x_max) 
        	   floor_x_max=map_x_max;
           if(floor_x_min>map_x_min) 
        	   floor_x_min=map_x_min;
           if(floor_y_max<map_y_max) 
        	   floor_y_max=map_y_max;
           if(floor_y_min>map_y_min) 
        	   floor_y_min=map_x_min;
           p.x_max=map_x_max*5;
           p.y_max=map_y_max*5;
           p.x_min=map_x_min*5;
           p.y_min=map_y_min*5;
           p.RoomName=info.get(0);
           p.Location=info.get(1);
           p.Area=info.get(2);
           p.No_of_Walls=info.get(3);
           p.Height=info.get(4);
           mapsMaxMinPoints.add(p);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		floor_model = new ArrayList<ArrayList<FloorModelPointsCordinate>>();
		all_room_info = new ArrayList<ArrayList<String>>();
		mapsMaxMinPoints = new ArrayList<ViewFloorMap.PointMaxMin>();
		Intent i = new Intent(ViewFloorMap.this, LocationFloorChooser.class);
		startActivityForResult(i, LOCATIONFLOOR_CHOOSER_XMLrequestCode);
		setContentView(R.layout.floors_mapfloor_view);
		map2drenderer = (ViewFloorMap2DRenderer) findViewById(R.id.floor_viewfloor_2drenderer);
		if (map2drenderer == null)
			map2drenderer = new ViewFloorMap2DRenderer(ViewFloorMap.this);
		/*floor_view_floorname=(TextView)findViewById(R.id.floor_view_floorname);
		floor_view_locationname=(TextView)findViewById(R.id.floor_view_locationname);
		floor_view_room_roomName=(TextView)findViewById(R.id.floor_view_room_roomName);
		floor_view_room_heightofRoom=(TextView)findViewById(R.id.floor_view_room_heightofRoom);
		floor_view_room_noOfWalls=(TextView)findViewById(R.id.floor_view_room_noOfWalls);
		floor_view_room_area=(TextView)findViewById(R.id.floor_view_room_area);
		floor_view_room_wordlocation=(TextView)findViewById(R.id.floor_view_room_wordlocation);*/
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);		
		if(resultCode == Activity.RESULT_OK){
			if (requestCode == LOCATIONFLOOR_CHOOSER_XMLrequestCode) {
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(ViewFloorMap.this);
				CHOSEN_LOCATION_FLOORXML_PATH = sharedPreferences.getString(
						"CHOSEN_LOCATION_FLOORXML", "");
				CHOSEN_LOCATION = sharedPreferences
						.getString("CHOSEN_LOCATION", "");
				CHOSEN_FLOOR = sharedPreferences.getString("CHOSEN_FLOOR", "");
				CHOSEN_FLOOR=CHOSEN_FLOOR.replace("/","");
				CHOSEN_LOCATION=CHOSEN_LOCATION.replace("/","");
				
				/*floor_view_floorname.setText("Floor : "+CHOSEN_FLOOR);
				floor_view_locationname.setText("Location : "+CHOSEN_LOCATION);*/
				
				File chosenFile = new File(CHOSEN_LOCATION_FLOORXML_PATH);
				if(chosenFile.exists()){
					load_floor_progress_dialog = ProgressDialog.show(ViewFloorMap.this, "Floor", "Loading Floor....", true, false);
					// create Thread to Save Floor
					Thread load_floor_background = new Thread(new Runnable() {
						public void run() {
							init_draw();
							load_floor_handler.sendEmptyMessage(0);
						}
					});
					// start the Loading thread
					load_floor_background.start();
				} else {
					alertDialog = new AlertDialog.Builder(ViewFloorMap.this).create();
					alertDialog.setTitle("Note");
					alertDialog.setMessage("Floor Map does not Exist. Please create the map first!");
					alertDialog.setIcon(R.drawable.symbol_error);
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					alertDialog.show();
				}
			}
		} else {
			finish();
		}
	}
	
	// Loading Floor Handler
	Handler load_floor_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			load_floor_progress_dialog.dismiss();
			map2drenderer.invalidate();
			startListeners();
		}
	};
	
	
	private void startListeners() {
		map2drenderer.setOnTouchListener(new OnTouchListener() {
			@SuppressWarnings({ "static-access", "unused" })
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getActionMasked();
				int pid = (event.getAction() & event.ACTION_POINTER_ID_MASK) >> event.ACTION_POINTER_ID_SHIFT;
				int index = (event.getAction() & event.ACTION_POINTER_INDEX_MASK) >> event.ACTION_POINTER_INDEX_SHIFT;
				switch (action) {
				
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:					
					float touch_X = event.getX();
					float touch_Y = event.getY();
					for (int i = 0; i < mapsMaxMinPoints
							.size(); i++) {
						if ((touch_X >= mapsMaxMinPoints
								.get(i).x_min && touch_X <= mapsMaxMinPoints
								.get(i).x_max)
								&& (touch_Y >= mapsMaxMinPoints
										.get(i).y_min && touch_Y <= mapsMaxMinPoints
										.get(i).y_max)) {
							room_touched_flag=true;
							room_touched_index=i;
							
							try{
								floor_newmap_roomdetails_menu.setVisible(true);
							} catch (Exception e){
								e.printStackTrace();
							}
							SELECTED_ROOM_NAME = all_room_info.get(i).get(0).toString();
							SELECTED_ROOM_AREA = all_room_info.get(i).get(2).toString();
							SELECTED_ROOM_NOOFWALLS = all_room_info.get(i).get(3).toString();
							SELECTED_ROOM_HEIGHT = all_room_info.get(i).get(4).toString();
							/*floor_view_room_roomName.setText("Room Name : "+all_room_info.get(i).get(0).toString());
							floor_view_room_wordlocation.setText("Room Location : "+all_room_info.get(i).get(1).toString());
							floor_view_room_area.setText("Area of Room : "+all_room_info.get(i).get(2).toString() + "sq fts");
							floor_view_room_noOfWalls.setText("No of Walls in Room: "+all_room_info.get(i).get(3).toString());
							floor_view_room_heightofRoom.setText("Height of Room : "+all_room_info.get(i).get(4).toString()+" fts");*/
							break;
						} else {
							/*floor_view_room_roomName.setText("Room Name : ");
							floor_view_room_wordlocation.setText("Room Location : ");
							floor_view_room_area.setText("Area of Room : ");
							floor_view_room_noOfWalls.setText("No of Walls in Room: ");
							floor_view_room_heightofRoom.setText("Height of Room : ");*/
							try{
								floor_newmap_roomdetails_menu.setVisible(false);
							} catch (Exception e){
								e.printStackTrace();
							}
						}
						
						room_touched_flag=false;
						room_touched_index=-1;						
					}					
					map2drenderer.invalidate();
				break;
				}
				return true;
			}
		});
	}
	
	// Options Menu in Action Bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.floor_viewfloor_2dview_menu, menu);
	    floor_newmap_roomdetails_menu = menu.getItem(0);
		floor_newmap_roomdetails_menu.setVisible(false);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		switch (item.getItemId()) {
			case R.id.floor_viewfloor_viewRoomDetails:
				alertDialog = new AlertDialog.Builder(ViewFloorMap.this).create();
				alertDialog.setTitle("Room Details");
				alertDialog.setMessage(
						"Room Name: " + SELECTED_ROOM_NAME + "\n" +
						"Area of Room: " + SELECTED_ROOM_AREA + "\n" +
						"No of Walls: " + SELECTED_ROOM_NOOFWALLS  + "\n" +
						"Height of Room: " + SELECTED_ROOM_HEIGHT + " fts" + "\n" +
						"Floor: " + CHOSEN_FLOOR + "\n" +
						"Location of Room: " + CHOSEN_LOCATION
				);
				alertDialog.setIcon(R.drawable.ic_launcher);
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				alertDialog.show();
				break;
			case R.id.floor_viewfloor_floordetails:
				alertDialog = new AlertDialog.Builder(ViewFloorMap.this).create();
				alertDialog.setTitle("Floor Details");
				alertDialog.setMessage("Floor: " + CHOSEN_FLOOR + "\n" + "Location: " + CHOSEN_LOCATION);
				alertDialog.setIcon(R.drawable.ic_launcher);
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				alertDialog.show();
				break;
		}
		return true;
	}
	
	public Bitmap StringToBitMap(String encodedString) {
		try {
			byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
					encodeByte.length);
			return bitmap;
		} catch (Exception e) {
			e.getMessage();
			return null;
		}
	}
}


