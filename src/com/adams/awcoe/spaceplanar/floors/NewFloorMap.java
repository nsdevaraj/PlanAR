package com.adams.awcoe.spaceplanar.floors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.net.Uri;
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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.adams.awcoe.spaceplanar.app.R;
import com.adams.awcoe.spaceplanar.floors.NewFloorMap2DRenderer.Point;
import com.adams.awcoe.spaceplanar.floors.modeldata.FloorModelPointsCordinate;
import com.adams.awcoe.spaceplanar.room.ViewRoomMap;
import com.adams.awcoe.spaceplanar.room.ViewRoomMap2D;
import com.adams.awcoe.spaceplanar.room.mapdata.ReadAugmented3DPointsData;
import com.adams.awcoe.spaceplanar.utils.ListAdapter;

public class NewFloorMap extends Activity {
	// Android Variables
	Path path = new Path();
	//ActionBar actionBar;
	ListView roomListInFloor;
	ProgressDialog place_room_progress_dialog;
	ProgressDialog view_room_progress_dialog;
	ProgressDialog save_floor_progress_dialog;
	ProgressDialog export_floor_progress_dialog;
	//Menu menu;
	ImageButton floor_room_rotation;
	ImageButton floor_room_antirotation;
	MenuItem floor_newmap_roomdetails_menu;
	/*TextView floor_new_floorname;
	TextView floor_new_locationname;*/

	// Member Variables
	private static int LOCATIONFLOOR_CHOOSER_requestCode = 1890;
	NewFloorMap2DRenderer map2drenderer;
	ListAdapter adapter;
	private String CHOSEN_LOCATION_FLOOR_PATH = "";
	private List<String> roomEntries = new ArrayList<String>();
	private List<String> roomEntries_formatted = new ArrayList<String>();
	private File currentDirectory = new File("/");
	public static ArrayList<ArrayList<FloorModelPointsCordinate>> floor_model;
	public static ArrayList<ArrayList<String>> all_room_info;
	public static ArrayList<String> floor_names_list;
	public static int floor_map_index = -1;
	public static boolean map_clicked = false;
	public static boolean map_reposition_clicked = false;
	public static int map_index_reposition_clicked = -1;
	public int index_room_view = 0;
	public static String floor_map_name = "";
	public static String floor_name_to_save = "3dmodel";
	public static String floor_name = "";
	public static String location = "";
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		CHOSEN_LOCATION_FLOOR_PATH = "";
		roomEntries = null;
		roomEntries_formatted = null;
		floor_model = null;
		all_room_info  = null;
		floor_names_list = null;
		floor_map_index = -1;
		map_clicked = false;
		map_reposition_clicked = false;
		index_room_view = 0;
		floor_map_name = "";
		floor_name = "";
		location = "";
		finish();
	}

	private void browseTo(final File aDirectory) {
		location = aDirectory.getPath();
		floor_name = aDirectory.getParentFile().getParent();							
		floor_map_name = aDirectory.getName();
		floor_name_to_save = aDirectory.getParentFile().getParentFile().getName();
		if (aDirectory.isDirectory()) {
			this.currentDirectory = aDirectory;
			fill(aDirectory.listFiles());
		} else {
			String floorName = aDirectory.getName().substring(0, aDirectory.getName().length() - 4);
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewFloorMap.this);
			alertDialog.setTitle("Confirm");
			alertDialog.setMessage("Do you want to place \"" + floorName + "\" on Floor \"" + floor_name_to_save + "\"?");
			alertDialog.setIcon(R.drawable.ic_launcher);
			alertDialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					place_room_progress_dialog = ProgressDialog.show(NewFloorMap.this, "Room has been selected","Loading....", true, false);
					// create Thread to Load the Room from XML
					Thread place_room_background = new Thread(
							new Runnable() {
								public void run() {
									NewFloorRoomMapReadMapXML obj = new NewFloorRoomMapReadMapXML(location);
									obj.readMap();
									place_room_handler.sendEmptyMessage(0);
								}
							});
					place_room_background.start();
				}
			});
			alertDialog.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to invoke NO event
							map_clicked = false;
							dialog.cancel();
						}
					});

			// Showing Alert Message
			alertDialog.show();
		}
	}

	private void fill(File[] files) {
		this.roomEntries.clear();		

		int currentPathStringLength = this.currentDirectory.getAbsolutePath()
				.length();
		for (File file : files) {
			if(file.isFile()){
				String roomName = file.getAbsolutePath().substring(
						currentPathStringLength);
				this.roomEntries.add(roomName);
			}			
		}
		
		for (int i = 0; i < roomEntries.size(); i++) {
			roomEntries_formatted.add(roomEntries.get(i).substring(1,
					roomEntries.get(i).length() - 4));
		}
		adapter = new ListAdapter(NewFloorMap.this, roomEntries_formatted);
		roomListInFloor.setAdapter(adapter);
	}

	private void init() {
		floor_model = new ArrayList<ArrayList<FloorModelPointsCordinate>>();
		all_room_info = new ArrayList<ArrayList<String>>();
		floor_names_list = new ArrayList<String>();
		map2drenderer = (NewFloorMap2DRenderer) findViewById(R.id.floor_newfloor_2drenderer);
		if (map2drenderer == null)
			try {
				map2drenderer = new NewFloorMap2DRenderer(NewFloorMap.this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		floor_room_rotation=(ImageButton)findViewById(R.id.floor_room_rotation);
		floor_room_antirotation=(ImageButton)findViewById(R.id.floor_room_antirotation);
	}

	private void startListeners() {

		//Floor Room Rotations
		floor_room_rotation.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				ArrayList<FloorModelPointsCordinate> rotating_room=floor_model.get(index_room_view);
				for(int i=0;i<rotating_room.size();i++)
				{
					FloorModelPointsCordinate obj=new FloorModelPointsCordinate();
					obj.setXMark(rotating_room.get(i).getXMark());
					obj.setYMark(rotating_room.get(i).getYMark());
					obj.setZMark(rotating_room.get(i).getZMark());
					obj.setTex_Data(rotating_room.get(i).getTex_Data());
					FloorModelPointsCordinate obj_rotated=new FloorModelPointsCordinate();
					obj_rotated.setXMark((float) (obj.getXMark()*Math.cos(Math.toRadians(5))-obj.getYMark()*Math.sin(Math.toRadians(5))));
					obj_rotated.setYMark((float) (obj.getXMark()*Math.sin(Math.toRadians(5))+obj.getYMark()*Math.cos(Math.toRadians(5))));
					obj_rotated.setTex_Data(obj.getTex_Data());
					obj_rotated.setZMark(obj.getZMark());
					rotating_room.set(i, obj_rotated);
				}
				floor_model.set(index_room_view,rotating_room);
				map2drenderer.invalidate();
				
			}
		});
		
          floor_room_antirotation.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				ArrayList<FloorModelPointsCordinate> rotating_room=floor_model.get(index_room_view);
				for(int i=0;i<rotating_room.size();i++)
				{
					FloorModelPointsCordinate obj=new FloorModelPointsCordinate();
					obj.setXMark(rotating_room.get(i).getXMark());
					obj.setYMark(rotating_room.get(i).getYMark());
					obj.setZMark(rotating_room.get(i).getZMark());
					obj.setTex_Data(rotating_room.get(i).getTex_Data());
					FloorModelPointsCordinate obj_rotated=new FloorModelPointsCordinate();
					obj_rotated.setXMark((float) (obj.getXMark()*Math.cos(Math.toRadians(5))+obj.getYMark()*Math.sin(Math.toRadians(5))));
					obj_rotated.setYMark((float) (-obj.getXMark()*Math.sin(Math.toRadians(5))+obj.getYMark()*Math.cos(Math.toRadians(5))));
					obj_rotated.setTex_Data(obj.getTex_Data());
					obj_rotated.setZMark(obj.getZMark());
					rotating_room.set(i, obj_rotated);
				}
				floor_model.set(index_room_view,rotating_room);
				map2drenderer.invalidate();
				
			}
		});
		
		
		
		// List View Click Listeners for Placing Rooms
		roomListInFloor.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				int selectionRowID = position;
				File clickedFile = null;
				clickedFile = new File(currentDirectory.getAbsolutePath()
						+ roomEntries.get(selectionRowID));
				if (clickedFile != null)
					browseTo(clickedFile);
				//menu.getItem(0).setVisible(false);
				//floor_newmap_roomdetails_menu.setVisible(false);
			}
		});

		map2drenderer.setOnTouchListener(new OnTouchListener() {
			@SuppressWarnings({ "static-access" })
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getActionMasked();
				int pid = (event.getAction() & event.ACTION_POINTER_ID_MASK) >> event.ACTION_POINTER_ID_SHIFT;
				int index = (event.getAction() & event.ACTION_POINTER_INDEX_MASK) >> event.ACTION_POINTER_INDEX_SHIFT;

				switch (action) {

				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					//menu.getItem(0).setVisible(false);
					try{
						floor_newmap_roomdetails_menu.setVisible(false);
					} catch (Exception e){
						e.printStackTrace();
					}
					if (NewFloorMap.map_clicked) {
						Point point = new Point();
						point.isOk = true;
						point.pid = pid;
						point.x = event.getX(index);
						point.y = event.getY(index);
						NewFloorMap2DRenderer.mPoints.clear();
						NewFloorMap2DRenderer.mPoints.push(point);
						map2drenderer.invalidate();
					} else {
						float touch_X = event.getX();
						float touch_Y = event.getY();
						for (int i = 0; i < NewFloorMap2DRenderer.mapsMaxMinPoints
								.size(); i++) {
							if ((touch_X >= NewFloorMap2DRenderer.mapsMaxMinPoints
									.get(i).x_min && touch_X <= NewFloorMap2DRenderer.mapsMaxMinPoints
									.get(i).x_max)
									&& (touch_Y >= NewFloorMap2DRenderer.mapsMaxMinPoints
											.get(i).y_min && touch_Y <= NewFloorMap2DRenderer.mapsMaxMinPoints
											.get(i).y_max)) {
								// Toast.makeText(NewFloorMap.this,"Room Touched - "+
								// NewFloorMap.floor_names_list.get(i),Toast.LENGTH_SHORT).show();

								map_reposition_clicked = true;
								index_room_view = map_index_reposition_clicked = i;
								//menu.getItem(0).setVisible(true);
								try{
									floor_newmap_roomdetails_menu.setVisible(true);
								} catch (Exception e){
									e.printStackTrace();
								}								
								Point point = new Point();
								point.isOk = true;
								point.pid = pid;
								point.x = event.getX(index);
								point.y = event.getY(index);
								NewFloorMap2DRenderer.mPoints.clear();
								NewFloorMap2DRenderer.mPoints.push(point);
								map2drenderer.invalidate();
								break;
							}
						}

					}
					break;

				case MotionEvent.ACTION_MOVE:
					for (Point p : NewFloorMap2DRenderer.mPoints) {
						int pindex = event.findPointerIndex(p.pid);
						if (pindex != -1) {
							p.x = event.getX(pindex);
							p.y = event.getY(pindex);
						}
					}

					map2drenderer.invalidate();
					break;

				case MotionEvent.ACTION_POINTER_UP:
					if (NewFloorMap2DRenderer.mPoints.size() >= 1) {
						NewFloorMap2DRenderer.mPoints.pop();
					}
					map2drenderer.invalidate();
					break;

				case MotionEvent.ACTION_UP:
					if (NewFloorMap.map_clicked) {
						alert_messages("Do you want to place this Room on Floor?");
					} else if (map_reposition_clicked == true) {
						boolean flag_overlap = false;
						if (!flag_overlap) {
							NewFloorMap2DRenderer.mapfloorPoints.set(
									map_index_reposition_clicked,
									NewFloorMap2DRenderer.mPoints.get(0));
							NewFloorMap2DRenderer.mPoints.clear();
							NewFloorMap2DRenderer.mapsMaxMinPoints.clear();
							map_reposition_clicked = false;
							map_index_reposition_clicked = -1;
							map2drenderer.invalidate();
						} else {
							Toast.makeText(
									NewFloorMap.this,
									"You cannot place here,rooms have overlapped.",
									Toast.LENGTH_SHORT).show();
							NewFloorMap2DRenderer.mPoints.clear();
							NewFloorMap2DRenderer.mapsMaxMinPoints.clear();
							map_reposition_clicked = false;
							flag_overlap = false;
							map_index_reposition_clicked = -1;
							map2drenderer.invalidate();
						}

					}
					break;
				default:
					NewFloorMap2DRenderer.mPoints.clear();
					map2drenderer.invalidate();
					break;
				}

				return true;
			}
		});
	}
	
	private void alert_messages_note_wrong(String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(NewFloorMap.this).create();
		alertDialog.setTitle("Note");
		alertDialog.setMessage(message);
		alertDialog.setIcon(R.drawable.symbol_alert);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}

	private void alert_messages_savedFloor(String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(NewFloorMap.this).create();
		alertDialog.setTitle("Floor has been saved");
		alertDialog.setMessage(message);
		alertDialog.setIcon(R.drawable.symbol_alert);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		alertDialog.show();
	}
	
	private void alert_messages_exportFloor(String message) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewFloorMap.this);
		alertDialog.setTitle("Exported Floor Model");
		alertDialog.setMessage(message);
		alertDialog.setIcon(R.drawable.symbol_alert);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Uri uri = Uri.fromFile(new File(floor_name
								+ "/OBJModel/" + floor_name_to_save + ".zip"));
						Intent i = new Intent(Intent.ACTION_SEND);
						i.putExtra(Intent.EXTRA_SUBJECT, "Floor Model - SpacePlanAR");
						i.putExtra(Intent.EXTRA_TEXT, " Wavefront 3D OBJ Floor Model - Created with SpacePlanAR. \n Please find the same in Attachment." +
								"\n You can extract the " + floor_name_to_save + ".zip file and Import the content into 3D Modelling Engines like Blender,Maya,3DMax etc.");
						i.putExtra(Intent.EXTRA_STREAM, uri);
						i.setType("text/plain");
						startActivity(Intent.createChooser(i, "Send mail"));	
					}
				});
		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog.show();
	}

	// Dynamic YES NO Alert Messages for various on Room Selection
	private void alert_messages(String message) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewFloorMap.this);
		alertDialog.setTitle("Selected Room");
		alertDialog.setMessage(message);
		alertDialog.setIcon(R.drawable.ic_launcher);
		alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				map_clicked = true;
				NewFloorMap2DRenderer.mapfloorPoints.add(NewFloorMap2DRenderer.mPoints.get(0));
				NewFloorMap2DRenderer.mPoints.clear();
				NewFloorMap2DRenderer.mapsMaxMinPoints.clear();
				floor_names_list.add(floor_map_name.substring(0,floor_map_name.length() - 4));
				index_room_view=floor_model.size()-1;
				map2drenderer.invalidate();
				map_clicked = false;						
			}
		});
		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						map_clicked = false;
						NewFloorMap2DRenderer.mPoints.clear();
						NewFloorMap2DRenderer.mapsMaxMinPoints.clear();
						floor_model.remove(floor_map_index);
						all_room_info.remove(floor_map_index);
						floor_map_index--;
						map2drenderer.invalidate();
						dialog.cancel();
					}
				});
		alertDialog.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.floors_newfloor_draw);
		Intent i = new Intent(NewFloorMap.this, LocationFloorChooser.class);
		startActivityForResult(i, LOCATIONFLOOR_CHOOSER_requestCode);
		roomListInFloor = (ListView) findViewById(R.id.floor_roomsmapslist);
		/*floor_new_floorname = (TextView)findViewById(R.id.floor_new_floorname);
		floor_new_locationname = (TextView)findViewById(R.id.floor_new_locationname);*/
		init();
		startListeners();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			if (requestCode == LOCATIONFLOOR_CHOOSER_requestCode) {
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(NewFloorMap.this);
				CHOSEN_LOCATION_FLOOR_PATH = sharedPreferences.getString(
						"CHOSEN_LOCATION_FLOOR", "");
				browseTo(new File(CHOSEN_LOCATION_FLOOR_PATH));
				String CHOSEN_FLOOR = sharedPreferences.getString("CHOSEN_FLOOR", "");
				CHOSEN_FLOOR = CHOSEN_FLOOR.replace("/","");
				String CHOSEN_LOCATION = sharedPreferences.getString("CHOSEN_LOCATION", "");
				CHOSEN_LOCATION = CHOSEN_LOCATION.replace("/","");
				/*floor_new_floorname.setText("Floor : "+CHOSEN_FLOOR);
				floor_new_locationname.setText("Location : "+CHOSEN_LOCATION);*/
			}
		} else {
			finish();
		}
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

	// Options Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.floor_newfloor_2dview_menu, menu);
		//this.menu.getItem(0).setVisible(false);
		floor_newmap_roomdetails_menu = menu.getItem(0);
		floor_newmap_roomdetails_menu.setVisible(false);
		return true;
	}

	// Handlers for various Threads

	// Placing Room Handler
	Handler place_room_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			place_room_progress_dialog.dismiss();
			Toast.makeText(NewFloorMap.this, "Tap the Screen and position your Room by dragging.",Toast.LENGTH_SHORT).show();
			floor_map_index = floor_map_index + 1;
			map_clicked = true;
		}
	};	

	// Viewing Room Handler
	Handler view_room_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			view_room_progress_dialog.dismiss();
		}
	};

	// Saving Floor Handler
	Handler save_floor_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			save_floor_progress_dialog.dismiss();
			alert_messages_savedFloor("You can view Saved Floors from Main Menu.");
		}
	};

	// Exporting Floor Handler
	Handler export_floor_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			export_floor_progress_dialog.dismiss();
			alert_messages_exportFloor("Wavefront 3D OBJ saved in location \n '"
					+ NewFloorMap.floor_name
					+ "/OBJModel/" + floor_name_to_save + ".zip"
					+ "\n You can import floor model in 3D Modelling Engines like Blender,3DMax etc." +
							"\n Do you want to send the Floor Model by Email?");
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.floor_newfloor_viewRoomModel:
			view_room_progress_dialog = ProgressDialog.show(NewFloorMap.this,
					"View Room", "Loading Room Preview....", true, false);
			Log.v("Room Model", index_room_view+"");
			// create Thread to Load the Room for Viewing
			Thread view_room_background = new Thread(new Runnable() {
				public void run() {
					ArrayList<FloorModelPointsCordinate> room = floor_model.get(index_room_view);
					ViewRoomMap.readAugmented3DPointsCordinates = new ArrayList<ReadAugmented3DPointsData>();
					ViewRoomMap.readMapInfo = all_room_info.get(index_room_view);
					ViewRoomMap.textures_Base64_to_Image = new ArrayList<Bitmap>();
					for (int i = 0; i < room.size(); i++) {
						ReadAugmented3DPointsData obj = new ReadAugmented3DPointsData();
						obj.setXMark(room.get(i).getXMark());
						obj.setYMark(room.get(i).getYMark());
						obj.setZMark(room.get(i).getZMark());
						ViewRoomMap.readAugmented3DPointsCordinates.add(obj);
					}
					for (int i = 0; i < room.size(); i++) {
						Bitmap object = StringToBitMap(room.get(i).getTex_Data());
						ViewRoomMap.textures_Base64_to_Image.add(object);
					}
					view_room_handler.sendEmptyMessage(0);
				}
			});
			// start the Loading thread
			view_room_background.start();
			Intent i = new Intent(NewFloorMap.this, ViewRoomMap2D.class);
			startActivity(i);
			break;
		case R.id.floor_newfloor_saveobjformat:
			/*actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);*/
			if (floor_names_list.size() > 0) {
				export_floor_progress_dialog = ProgressDialog
						.show(NewFloorMap.this, "Exporting Floor", "Exporting OBJ Model....", true,false);
				// create Thread to Save Floor
				Thread export_floor_model = new Thread(new Runnable() {
					public void run() {
						FloorModelSaveXMLWriter floorModelSaveXMLWriter = new FloorModelSaveXMLWriter();
						// Save Floor Model in Wavefront OBJ format for Exporting
						floorModelSaveXMLWriter.exportFloorMapOBJ(NewFloorMap.this,map2drenderer.c_width/2,map2drenderer.c_height/2);
						export_floor_handler.sendEmptyMessage(0);
					}
				});
				// start the Loading thread
				export_floor_model.start();
			} else {
				alert_messages_note_wrong("Empty Floor cannot be exported. Place rooms to export Floor.");
			}
			break;
		case R.id.floor_newfloor_saveModel:
			/*actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);*/			
			if (floor_names_list.size() > 0) {
				save_floor_progress_dialog = ProgressDialog.show(
						NewFloorMap.this, "Floor", "Saving....", true, false);
				// create Thread to Save Floor
				Thread save_floor_background = new Thread(new Runnable() {
					public void run() {
						FloorModelSaveXMLWriter floorModelSaveXMLWriter = new FloorModelSaveXMLWriter();
						// Save Floor Model in Space Planar XML for 3D Rendering
						floorModelSaveXMLWriter.saveFloorMap();
						save_floor_handler.sendEmptyMessage(0);
					}
				});
				// start the Loading thread
				save_floor_background.start();
			} else {
				alert_messages_note_wrong("Empty Floor cannot be saved. Place rooms to save Floor.");
			}
			break;		
		}
		return true;
	}

}
