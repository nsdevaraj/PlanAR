package com.adams.awcoe.spaceplanar.room;
import java.io.File;
import java.util.ArrayList;

//import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adams.awcoe.spaceplanar.app.R;
import com.adams.awcoe.spaceplanar.room.mapdata.ReadAugmented3DPointsData;

public class ViewRoomMap2D extends Activity {	
   //Android Variables
	LinearLayout room_viewmap_walltex_container;
	ImageView room_viewmap_walltex_image;
	TextView room_viewmap_walltex_text;
	/*TextView room_viewmap_noOfWalls;
	TextView room_viewmap_roomName;
	TextView room_viewmap_heightofRoom;
	TextView room_viewmap_area;
	TextView room_viewmap_realWorldLocation;*/
	//ActionBar actionBar;
	public static Bitmap floor_bitmap;
	ProgressDialog export_room_progress_dialog;
	
	// Member Variables
	ViewRoomMap2DRenderer map2drenderer;
	public static ArrayList<Bitmap> wall_textures_content;
	private boolean edge_touched=false;
	String CHOSEN_LOCATION_FLOOR_PATH;
	
	// Room Properties
	static String room_canvas_name, room_canvas_area, room_no_of_walls, room_canvas_height, room_canvas_location;
	
	// Dynamic OK Alert Messages
		@SuppressWarnings("unused")
		private void alert_messages_ok(String message) {
			AlertDialog alertDialog = new AlertDialog.Builder(ViewRoomMap2D.this)
					.create();

			// Setting Dialog Title
			alertDialog.setTitle("Note");

			// Setting Dialog Message
			alertDialog.setMessage(message);

			// Setting Icon to Dialog
			alertDialog.setIcon(R.drawable.symbol_alert);

			// Setting OK Button
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});

			// Showing Alert Message
			alertDialog.show();
		}
	
	
	// Methods for Detecting Touch is on Wall Edge
		private double distance_between(ReadAugmented3DPointsData a,ReadAugmented3DPointsData b) {
		    return Math.sqrt(Math.pow((a.getXMark() - b.getXMark()),2) + Math.pow((a.getYMark() - b.getYMark()),2));
		}
		private boolean is_between(ReadAugmented3DPointsData a, ReadAugmented3DPointsData c,ReadAugmented3DPointsData b) {
			double d3=Math.abs(distance_between(a,c) + distance_between(c,b)-distance_between(a,b));
			if(d3<=1.0) {
				return true;				
			} else {
				return false;
			}		   
		}
		
		// Pop Up for Texture View
		public void popUpTextureView_Show(int wall_no) {
			
			Bitmap content=wall_textures_content.get(wall_no);
			room_viewmap_walltex_image.setImageBitmap(content);
			room_viewmap_walltex_text.setText("Texture on Wall "+(wall_no+1));
			room_viewmap_walltex_container.setVisibility(View.VISIBLE);
		}
		//Hide Texture View Pop Up
		public void popUpTextureView_Hide() {
			room_viewmap_walltex_container.setVisibility(View.INVISIBLE);
		}
		
		
		public void startListeners() {
			// Define On Touch Events for Interaction with the 2D Map on Screen
	          map2drenderer.setOnTouchListener(new View.OnTouchListener() {
				
				public boolean onTouch(View v, MotionEvent ev) {
					
					final int action = ev.getAction();
			        switch (action & MotionEvent.ACTION_MASK) {
			        
			        case MotionEvent.ACTION_DOWN: {			        	
			        	if(edge_touched)
			            	{
			        		popUpTextureView_Hide();
			        		edge_touched=false;
			            	}
			        	//Reset Action Bar
			        	/*actionBar = getActionBar();
						actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);*/
						
						final float x = ev.getX();
			            final float y = ev.getY();
			            map2drenderer.mLastTouchX = x;
			            map2drenderer.mLastTouchY = y;
			            
			            
			            
			            // Node or Marker Detection on Touch Down
			            for(int i=0;i<ViewRoomMap.readAugmented3DPointsCordinates.size();i++)
			    		{
			            	map2drenderer.x_cord=ViewRoomMap.readAugmented3DPointsCordinates.get(i).getXMark();
			            	map2drenderer.y_cord=ViewRoomMap.readAugmented3DPointsCordinates.get(i).getYMark();
			            	map2drenderer.x_pixel_cord=(map2drenderer.c_width_scale+(map2drenderer.x_cord*map2drenderer.x_scale));
			            	map2drenderer.y_pixel_cord=(map2drenderer.c_height_scale+(map2drenderer.y_cord*map2drenderer.y_scale));
			    			
			            if (
			            		((map2drenderer.x_pixel_cord-20) <= (map2drenderer.mLastTouchX) 
			            		&& map2drenderer.mLastTouchX <= (map2drenderer.x_pixel_cord+50))
			                    && 
			                    ((map2drenderer.y_pixel_cord-20) <= (map2drenderer.mLastTouchY) 
			                    && map2drenderer.mLastTouchY <= (map2drenderer.y_pixel_cord + 50))
			                ) 
			            {
			            
			            	map2drenderer.mouse_down=true;
			            	map2drenderer.mouse_down_flag=i;
			            	break;
			            }
			            
			    		}
	  
			            //Edge or Wall Touch Detection on Touch Down
			            if(!map2drenderer.mouse_down){
			            	
			            
			            ReadAugmented3DPointsData obj=new ReadAugmented3DPointsData();
			            obj.setXMark((x-map2drenderer.c_width_scale)/map2drenderer.x_scale);
		                obj.setYMark((y-map2drenderer.c_height_scale)/map2drenderer.y_scale);
			            
			            for(int i=0;i<ViewRoomMap.readAugmented3DPointsCordinates.size();i++)
			            {
			            	if(i<ViewRoomMap.readAugmented3DPointsCordinates.size()-1)
			            	
			            	{	
			            		if(is_between(ViewRoomMap.readAugmented3DPointsCordinates.get(i),obj,ViewRoomMap.readAugmented3DPointsCordinates.get(i+1)))
			            	    {
			            		
			            		map2drenderer.touched_edge_left=i;
			            		map2drenderer.touched_edge_right=i+1;
			            		edge_touched=true;
			            		popUpTextureView_Show(i);
			            		//Toast.makeText(NewRoomMap2D.this,"Wall "+i+"-"+(i+1)+"has been Touched",Toast.LENGTH_SHORT).show();
			            		
			            		break;
			            	    }
			            	}
			            	else
			            	{
			            		if(is_between(ViewRoomMap.readAugmented3DPointsCordinates.get(i),obj,ViewRoomMap.readAugmented3DPointsCordinates.get(0)))
			            	    {
			            		map2drenderer.touched_edge_left=0;
				            	map2drenderer.touched_edge_right=i;
				            	edge_touched=true;
				            	popUpTextureView_Show(i);
			            		//Toast.makeText(NewRoomMap2D.this,"Wall "+0+"-"+i+"has been Touched",Toast.LENGTH_SHORT).show();
			            	    }
			            	}
			            }
			        }    
			            map2drenderer.invalidate();
			            map2drenderer.mActivePointerId = ev.getPointerId(0);
			           
			            break;
			        }

			        case MotionEvent.ACTION_MOVE: {
			            final int pointerIndex = ev.findPointerIndex(map2drenderer.mActivePointerId);
			            final float x = ev.getX(pointerIndex);
			            final float y = ev.getY(pointerIndex);
			            map2drenderer.mLastTouchX = x;
			            map2drenderer.mLastTouchY = y;
			            break;
			        }

			        case MotionEvent.ACTION_UP: {
			        	map2drenderer.mActivePointerId = NewRoomMap2DRenderer.INVALID_POINTER_ID;
			        	map2drenderer.mouse_down=false;
			            break;
			        }

			        case MotionEvent.ACTION_CANCEL: {
			        	map2drenderer.mActivePointerId = NewRoomMap2DRenderer.INVALID_POINTER_ID;
			            break;
			        }

			        case MotionEvent.ACTION_POINTER_UP: {
			            final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) 
			                    >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			            final int pointerId = ev.getPointerId(pointerIndex);
			            if (pointerId == map2drenderer.mActivePointerId) {
			                final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			                map2drenderer.mLastTouchX = ev.getX(newPointerIndex);
			                map2drenderer.mLastTouchY = ev.getY(newPointerIndex);
			                map2drenderer.mActivePointerId = ev.getPointerId(newPointerIndex);
			            }
			            break;
			        }
			        }
			        return true;
				}
			});
		}


		@Override
		protected void onCreate(Bundle savedInstanceState) {	
			super.onCreate(savedInstanceState);			
			setContentView(R.layout.room_viewmaps_generate2dview);
			map2drenderer =(ViewRoomMap2DRenderer)findViewById(R.id.room_viewmap_2drenderer);
			if(map2drenderer==null)
				map2drenderer =new ViewRoomMap2DRenderer(ViewRoomMap2D.this);
			room_viewmap_walltex_container=(LinearLayout)findViewById(R.id.room_viewmap_walltex_container);
			room_viewmap_walltex_text=(TextView)findViewById(R.id.room_viewmap_walltex_text);
			room_viewmap_walltex_image=(ImageView)findViewById(R.id.room_viewmap_walltex_image);
			room_viewmap_walltex_container.setVisibility(View.INVISIBLE);
			/*room_viewmap_noOfWalls = (TextView)findViewById(R.id.room_viewmap_noOfWalls);
			room_viewmap_heightofRoom=(TextView)findViewById(R.id.room_viewmap_heightofRoom);
			room_viewmap_area = (TextView)findViewById(R.id.room_viewmap_area);
			room_viewmap_realWorldLocation=(TextView)findViewById(R.id.room_viewmap_wordlocation);
			room_viewmap_roomName=(TextView)findViewById(R.id.room_viewmap_roomName);*/
			
			for(int i=0;i<ViewRoomMap.readMapInfo.size();i++) {
				switch(i) {
				case 0:
					//room_viewmap_roomName.setText("Room Name : "+ViewRoomMap.readMapInfo.get(i));
					room_canvas_name = ViewRoomMap.readMapInfo.get(i);
					break;
				case 1:
					//room_viewmap_realWorldLocation.setText("World Location : "+ViewRoomMap.readMapInfo.get(i));
					room_canvas_location = ViewRoomMap.readMapInfo.get(i);
					break;
				case 2:
					//room_viewmap_area.setText("Area of Room is : "+ViewRoomMap.readMapInfo.get(i));
					room_canvas_area = ViewRoomMap.readMapInfo.get(i);
					break;
				case 3:
					//room_viewmap_noOfWalls.setText("No of Walls in Room: "+ViewRoomMap.readMapInfo.get(i));
					room_no_of_walls = ViewRoomMap.readMapInfo.get(i);
					break;
				case 4:
					//room_viewmap_heightofRoom.setText("Height of Room : "+ViewRoomMap.readMapInfo.get(i) + " fts");
					room_canvas_height = ViewRoomMap.readMapInfo.get(i);
					break;
				}
			}
			//Initialize Wall Textured Bitmaps
			wall_textures_content=new ArrayList<Bitmap>();
			wall_textures_content=ViewRoomMap.textures_Base64_to_Image;
			//Initialize Default Floor Bitmap
			floor_bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.room_newmap_floortex_default);
			startListeners();
		}
		
		
		@Override
		public void onBackPressed() {
			super.onBackPressed();
			room_canvas_height = "Height Not Set";
			room_canvas_area = "0 sq fts";
			room_canvas_location = "Location Not Set";
			room_no_of_walls = "0";
			for (Bitmap bmp : wall_textures_content){
				bmp.recycle();
			}
			floor_bitmap.recycle();
		}

		// Options Menu in Action Bar
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			
			MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.room_viewmap_menu, menu);
		    return true;
		}
		
		// Exporting Floor Handler
		Handler export_room_handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ViewRoomMap2D.this);
				CHOSEN_LOCATION_FLOOR_PATH = sharedPreferences.getString("CHOSEN_LOCATION_FLOOR", "");
				export_room_progress_dialog.dismiss();
				alert_messages_exportRoom("Wavefront 3D OBJ saved in location \n '"
						+ CHOSEN_LOCATION_FLOOR_PATH + "/OBJRoomMaps/"
						+ ViewRoomMap.readMapInfo.get(0) + ".zip"
						+ "\n You can import room model in 3D Modelling Engines like Blender,3DMax etc."
						+ "\n Do you want to send the Room Model by Email?");
			}
		};
		
		private void alert_messages_exportRoom(String message){
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewRoomMap2D.this);
			// Setting Dialog Title
			alertDialog.setTitle("Exported Room Model");
			// Setting Dialog Message
			alertDialog.setMessage(message);
			// Setting Icon to Dialog
			alertDialog.setIcon(R.drawable.symbol_alert);
			// Setting Positive "Yes" Button
			alertDialog.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							Uri uri = Uri.fromFile(new File(CHOSEN_LOCATION_FLOOR_PATH + "/OBJRoomMaps/"
									+ ViewRoomMap.readMapInfo.get(0) + ".zip"));
							Intent i = new Intent(Intent.ACTION_SEND);
							i.putExtra(Intent.EXTRA_SUBJECT, "Room Model - SpacePlanAR");
							i.putExtra(Intent.EXTRA_TEXT, " Wavefront 3D OBJ Room Model - Created with SpacePlanAR."
									+ "\n Please find the attachment."
									+ "\n You can extract the " + ViewRoomMap.readMapInfo.get(0) + ".zip file and Import the content into 3D Modelling "
									+ "Engines like Blender,Maya,3DMax etc.");
							i.putExtra(Intent.EXTRA_STREAM, uri);
							i.setType("text/plain");
							startActivity(Intent.createChooser(i, "Send mail"));
							
						}
					});

			// Setting Negative "NO" Button
			alertDialog.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});

			// Showing Alert Message
			alertDialog.show();
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.room_newmap_2d_details:
				String details =	"Room Map Name: " +
									room_canvas_name + "\n" +
									"Area of Room: " +
									room_canvas_area + "\n" +
									"Height of Room: " +
									room_canvas_height + "\n" +
									"Real World Location: " +
									room_canvas_location + "\n" +
									"No of Walls in Room: " +
									room_no_of_walls;
									
				
				AlertDialog room_details_dlg = new AlertDialog.Builder(ViewRoomMap2D.this).create();
				room_details_dlg.setTitle("Room Details");
				room_details_dlg.setMessage(details);
				room_details_dlg.setIcon(R.drawable.symbol_alert);
				room_details_dlg.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				room_details_dlg.show();			
				break;
			case R.id.room_view_export_roommodel:
				export_room_progress_dialog = ProgressDialog.show(ViewRoomMap2D.this, "Room", "Exporting OBJ Model....", true, false);				
				Thread export_room_model = new Thread(new Runnable() {					
					public void run() {
						ViewRoomMapOBJModelWriter obj=new ViewRoomMapOBJModelWriter();
						obj.exportMapOBJ(ViewRoomMap2D.this);
						export_room_handler.sendEmptyMessage(0);
					}
				});				
				export_room_model.start();
				break;				
			case R.id.room_view_3d_roommodel:				
				Intent i=new Intent(ViewRoomMap2D.this,ViewRoomMap3D.class);
				startActivity(i);				
				break;				
			}
			return true;
		}
}
