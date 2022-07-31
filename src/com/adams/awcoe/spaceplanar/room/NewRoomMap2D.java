package com.adams.awcoe.spaceplanar.room;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adams.awcoe.spaceplanar.app.R;
import com.adams.awcoe.spaceplanar.room.mapdata.Augmented3DPointsData;
import com.adams.awcoe.spaceplanar.room.mapdata.AugmentedPointsData;
import com.adams.awcoe.spaceplanar.utils.CopyResources;
import com.adams.awcoe.spaceplanar.utils.WindowResolution;

public class NewRoomMap2D extends Activity implements LocationListener{
	//Android Variables
	LinearLayout room_newmap_walltex_container;
	ImageView room_newmap_walltex_image;
	TextView room_newmap_walltex_text;
	/*TextView room_newmap_noOfWalls;
	TextView room_newmap_heightofRoom;
	TextView room_newmap_area;
	TextView room_newmap_realWorldLocation;*/
	LocationManager locationManager;
	ImageButton room_newmap_rotation;
	ImageButton room_newmap_antirotation;
	Geocoder geocoder;
	Location location;
	//ActionBar actionBar;
	ImageButton room_newmap_regularize;
	MenuItem room_newmap_texture_menu;
	Bitmap content;
	
	// Room Properties	
	static String room_canvas_area, room_canvas_height, room_canvas_location;
	String room_location_calc;
	Boolean location_update_completed;
	CopyResources crObj;
	
	//Member Variables
	public final int NewRoomMapHeight_requestCode=10, NewRoomMapSave_requestCode=11, NewRoomMapEditTexture_requestCode=12;
	NewRoomMap2DRenderer map2drenderer;
	private boolean gps_enabled = false;
	private boolean network_enabled = false;
	public String map_location = "Location_Unknown";
	public static ArrayList<Bitmap> wall_textures_content;
	public static ArrayList<String> wall_textures_content_BASE64;
	private boolean edge_touched=false;
	public static int  current_wall_id=-1;
	public static Bitmap floor_bitmap;
	public static String room_details;
	public static AlertDialog alert_dialog;
	Boolean is_modify_texture_msg = false;
	
	@Override
	public void onBackPressed() {		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewRoomMap2D.this);
		alertDialog.setTitle("Map Not Saved");
		alertDialog.setMessage("Map can be saved from menu. Exit without saving?");
		alertDialog.setIcon(R.drawable.symbol_newmap_reset_alt);
		alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				NewRoomMap.points_augmented.removeAll(NewRoomMap.points_augmented);
					NewRoomMap.points3dCordinates_augmented.removeAll(NewRoomMap.points3dCordinates_augmented);
					AugmentedPointsData.index = -1;
					NewRoomMap.mark_start = false;
					room_canvas_height = "Not Set";
					room_canvas_area = "0 sq fts";
					room_canvas_location = "Not Set";
					location_update_completed = false;
					is_modify_texture_msg = false;
					try{
						content.recycle();						
					} catch(Exception e){}
					floor_bitmap.recycle();
					for (Bitmap bmp : wall_textures_content){
						bmp.recycle();
					}
					// Delete all temporary files from Temporary Folder
					crObj.deleteAllTempFiles();
					System.gc();
					finish();
				}
			});
		alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();				
			}
		});
		alertDialog.show();
	}
	
	// Dynamic OK Alert Messages
	private void alert_messages_ok(String message) {
		alert_dialog = new AlertDialog.Builder(NewRoomMap2D.this).create();
		alert_dialog.setTitle("Note");
		alert_dialog.setMessage(message);
		alert_dialog.setIcon(R.drawable.symbol_alert);
		alert_dialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alert_dialog.show();
	}
	
	// Dynamic OK Map Save Alert Messages
	private void alert_messages_finalsave(String message) {
		alert_dialog = new AlertDialog.Builder(NewRoomMap2D.this).create();
		alert_dialog.setTitle("Note");
		alert_dialog.setMessage(message);
		alert_dialog.setIcon(R.drawable.symbol_alert);
		alert_dialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
                   finish();
			}
		});
		alert_dialog.show();
	}
	
	// Methods for Detecting Touch is on Wall Edge
	private double distance_between(Augmented3DPointsData a,Augmented3DPointsData b){
	    return Math.sqrt(Math.pow((a.getXMark() - b.getXMark()),2) + Math.pow((a.getYMark() - b.getYMark()),2));
	}
	private boolean is_between(Augmented3DPointsData a, Augmented3DPointsData c,Augmented3DPointsData b) {
		double d3=Math.abs(distance_between(a,c) + distance_between(c,b)-distance_between(a,b));
		if(d3<=1.0) {
			return true;
		} else {
			//Toast.makeText(NewRoomMap2D.this,"Distace diff is NOT OK "+d3,Toast.LENGTH_SHORT).show();
			return false;
		}	   
	}
	
	// Pop Up for Texture View
	public void popUpTextureView_Show(int wall_no) {
		content = wall_textures_content.get(wall_no);
		room_newmap_walltex_image.setImageBitmap(content);
		room_newmap_walltex_text.setText("Texture on Wall "+(wall_no+1));
		room_newmap_walltex_container.setVisibility(View.VISIBLE);
	}
	//Hide Texture View Pop Up
	public void popUpTextureView_Hide() {
		room_newmap_texture_menu.setVisible(false);
		room_newmap_walltex_container.setVisibility(View.INVISIBLE);
	}
	
	//Showing Texturing Options
	public void showTexture_menu(final int wall_no){
		current_wall_id = wall_no;
		if(is_modify_texture_msg != true){
			is_modify_texture_msg = true;
			Toast.makeText(NewRoomMap2D.this,"Modify Wall Texture from Menu!",Toast.LENGTH_SHORT).show();
		}
		try{
			//Log.v("Test", room_newmap_texture_menu.isVisible()+"");
			room_newmap_texture_menu.setVisible(true);			
		} catch (Exception e){
			e.printStackTrace();
		}
		popUpTextureView_Show(wall_no);
		/*actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(NewRoomMap2D.this, R.array.texture_action_list,
		          android.R.layout.simple_spinner_dropdown_item);
		
		actionBar.setListNavigationCallbacks(
				mSpinnerAdapter,
	            new ActionBar.OnNavigationListener() {
	                public boolean onNavigationItemSelected(int position, long id) {
	                    if (position==0){
	                    	popUpTextureView_Show(wall_no);
	                    } else if (position==1){
	                    	popUpTextureView_Hide();
	                    	current_wall_id=wall_no;
	                    	Intent tex_i = new Intent(NewRoomMap2D.this, NewRoomMapTextures.class);
	                    	startActivity(tex_i);	                    	
	                	} else if (position==2){
	                    	popUpTextureView_Hide();
	                    	current_wall_id=wall_no;
	                    	Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.room_newmap_walltex_default);
	                    	wall_textures_content.set(current_wall_id, bmp);
	                    	
	                    	Bitmap bmp = null;
	                    	InputStream is = getResources().openRawResource(R.drawable.room_newmap_walltex_default);
	                    	try {
	                    		bmp = BitmapFactory.decodeStream(is);
	                    	} catch (Exception e) {
	                    		e.printStackTrace();
							} finally {
								try {
									is.close();
									is = null;
								} catch (IOException e) {
									e.printStackTrace();
								}			                    
							}
	                    	
	                    	wall_textures_content.set(current_wall_id, bmp);
	                    	wall_textures_content_BASE64.set(current_wall_id,BitMapToString(bmp));
	                    	popUpTextureView_Show(wall_no);
	                    }
	                    return true; 
	                }
	            });*/
	}

	public void startListeners() {
		//Regularize Room
		room_newmap_regularize.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				double distance_x=0;
				double distance_y=0;
				for(int i=0;i<NewRoomMap.points3dCordinates_augmented.size();i++) {
					if(i<NewRoomMap.points3dCordinates_augmented.size()-1) {
						distance_x =  Math.abs(NewRoomMap.points3dCordinates_augmented.get(i+1).getXMark()-NewRoomMap.points3dCordinates_augmented.get(i).getXMark());
						distance_y =  Math.abs(NewRoomMap.points3dCordinates_augmented.get(i+1).getYMark()-NewRoomMap.points3dCordinates_augmented.get(i).getYMark());
						if(distance_x<=2) {
							Augmented3DPointsData obj=new Augmented3DPointsData();
							obj.setXMark(NewRoomMap.points3dCordinates_augmented.get(i).getXMark());
							obj.setYMark(NewRoomMap.points3dCordinates_augmented.get(i+1).getYMark());
							NewRoomMap.points3dCordinates_augmented.set((i+1), obj);
						}
						if(distance_y<=2) {
							Augmented3DPointsData obj=new Augmented3DPointsData();
							obj.setXMark(NewRoomMap.points3dCordinates_augmented.get(i+1).getXMark());
							obj.setYMark(NewRoomMap.points3dCordinates_augmented.get(i).getYMark());
							NewRoomMap.points3dCordinates_augmented.set((i+1), obj);
						}
					}
					else if(i==NewRoomMap.points3dCordinates_augmented.size()-1) {
						distance_x =  Math.abs(NewRoomMap.points3dCordinates_augmented.get(0).getXMark()-NewRoomMap.points3dCordinates_augmented.get(i).getXMark());
						distance_y =  Math.abs(NewRoomMap.points3dCordinates_augmented.get(0).getYMark()-NewRoomMap.points3dCordinates_augmented.get(i).getYMark());
						if(distance_x<=2) {
							Augmented3DPointsData obj=new Augmented3DPointsData();
							obj.setXMark(NewRoomMap.points3dCordinates_augmented.get(0).getXMark());
							obj.setYMark(NewRoomMap.points3dCordinates_augmented.get(i).getYMark());
							NewRoomMap.points3dCordinates_augmented.set((i), obj);
						}
						if(distance_y<=2) {
							Augmented3DPointsData obj=new Augmented3DPointsData();
							obj.setXMark(NewRoomMap.points3dCordinates_augmented.get(i).getXMark());
							obj.setYMark(NewRoomMap.points3dCordinates_augmented.get(0).getYMark());
							NewRoomMap.points3dCordinates_augmented.set((i), obj);
					}
				}
			}
			map2drenderer.calc_area_map();
			map2drenderer.calc_distaces();
			map2drenderer.invalidate();
			}
		});
		
		// Define On Touch Events for Interaction with the 2D Map on Screen
          map2drenderer.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent ev) {
				final int action = ev.getAction();
		        switch (action & MotionEvent.ACTION_MASK) {
			        case MotionEvent.ACTION_DOWN: {
			        	if(edge_touched) {
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
			            for(int i=0;i<NewRoomMap.points3dCordinates_augmented.size();i++) {
			            	map2drenderer.x_cord=NewRoomMap.points3dCordinates_augmented.get(i).getXMark();
			            	map2drenderer.y_cord=NewRoomMap.points3dCordinates_augmented.get(i).getYMark();
			            	map2drenderer.x_pixel_cord=(map2drenderer.c_width_scale+(map2drenderer.x_cord*map2drenderer.x_scale));
			            	map2drenderer.y_pixel_cord=(map2drenderer.c_height_scale+(map2drenderer.y_cord*map2drenderer.y_scale));
			    			
				            if (
				            		((map2drenderer.x_pixel_cord-20) <= (map2drenderer.mLastTouchX) 
				            		&& map2drenderer.mLastTouchX <= (map2drenderer.x_pixel_cord+50))
				                    && 
				                    ((map2drenderer.y_pixel_cord-20) <= (map2drenderer.mLastTouchY) 
				                    && map2drenderer.mLastTouchY <= (map2drenderer.y_pixel_cord + 50))
				                ) {			            
				            	map2drenderer.mouse_down=true;
				            	map2drenderer.mouse_down_flag=i;
				            	break;
				            }
			    		}
	  
			            //Edge or Wall Touch Detection on Touch Down
			            if(!map2drenderer.mouse_down){
				            Augmented3DPointsData obj=new Augmented3DPointsData();
				            obj.setXMark((x-map2drenderer.c_width_scale)/map2drenderer.x_scale);
			                obj.setYMark((y-map2drenderer.c_height_scale)/map2drenderer.y_scale);
				            
				            for(int i=0;i<NewRoomMap.points3dCordinates_augmented.size();i++) {
				            	if(i<NewRoomMap.points3dCordinates_augmented.size()-1) {	
				            		if(is_between(NewRoomMap.points3dCordinates_augmented.get(i),obj,NewRoomMap.points3dCordinates_augmented.get(i+1))) {		            		
					            		map2drenderer.touched_edge_left=i;
					            		map2drenderer.touched_edge_right=i+1;
					            		edge_touched=true;					            		
					            		showTexture_menu(i);
					            		break;
				            	    }
				            	} else {
				            		if(is_between(NewRoomMap.points3dCordinates_augmented.get(i),obj,NewRoomMap.points3dCordinates_augmented.get(0))) {
				            		map2drenderer.touched_edge_left=0;
					            	map2drenderer.touched_edge_right=i;
					            	edge_touched=true;					            	
					            	showTexture_menu(i);
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
	
			            // Only mouse down is true meaning Flag is pressed.
			            if (map2drenderer.mouse_down) {		                
			            	Augmented3DPointsData p=new Augmented3DPointsData();
			                p.setXMark((x-map2drenderer.c_width_scale)/map2drenderer.x_scale);
			                p.setYMark((y-map2drenderer.c_height_scale)/map2drenderer.y_scale);
			                NewRoomMap.points3dCordinates_augmented.set(map2drenderer.mouse_down_flag,p);
			                map2drenderer.calc_distaces();
			                map2drenderer.calc_area_map();
			                room_canvas_area = roundDecimals(map2drenderer.area_sum) + " sq fts";
			                map2drenderer.invalidate();
			                //room_newmap_area.setText("Area of Room is : "+roundDecimals(map2drenderer.area_sum) +" sq fts");		                
			            }
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
		}); // map2renderer touch Listener ends
          
          room_newmap_rotation.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {				
				for(int i=0;i<NewRoomMap.points3dCordinates_augmented.size();i++) {
					Augmented3DPointsData obj=new Augmented3DPointsData();
					obj.setXMark(NewRoomMap.points3dCordinates_augmented.get(i).getXMark());
					obj.setYMark(NewRoomMap.points3dCordinates_augmented.get(i).getYMark());
					
					Augmented3DPointsData obj_rotated=new Augmented3DPointsData();
					
					Math.sin(Math.toRadians(10));
					obj_rotated.setXMark((float) (obj.getXMark()*Math.cos(Math.toRadians(5))-obj.getYMark()*Math.sin(Math.toRadians(5))));
					obj_rotated.setYMark((float) (obj.getXMark()*Math.sin(Math.toRadians(5))+obj.getYMark()*Math.cos(Math.toRadians(5))));
					NewRoomMap.points3dCordinates_augmented.set(i, obj_rotated);
				}				
				map2drenderer.invalidate();
			}
		});
          
          
          room_newmap_antirotation.setOnClickListener(new OnClickListener() {
  			public void onClick(View v) {  				
  				for(int i=0;i<NewRoomMap.points3dCordinates_augmented.size();i++) {
  					Augmented3DPointsData obj=new Augmented3DPointsData();
  					obj.setXMark(NewRoomMap.points3dCordinates_augmented.get(i).getXMark());
  					obj.setYMark(NewRoomMap.points3dCordinates_augmented.get(i).getYMark());
  					
  					Augmented3DPointsData obj_rotated=new Augmented3DPointsData();
  					
  					Math.sin(Math.toRadians(10));
  					obj_rotated.setXMark((float) (obj.getXMark()*Math.cos(Math.toRadians(5))+obj.getYMark()*Math.sin(Math.toRadians(5))));
  					obj_rotated.setYMark((float) (-obj.getXMark()*Math.sin(Math.toRadians(5))+obj.getYMark()*Math.cos(Math.toRadians(5))));
  					NewRoomMap.points3dCordinates_augmented.set(i, obj_rotated);
  				}
  				
  				map2drenderer.invalidate();
  			}
  		});

	}

	//Rounding up to 3dec
	private double roundDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("###.###");
		return Double.valueOf(twoDForm.format(d));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_newmap_generate2dview);
		map2drenderer =(NewRoomMap2DRenderer)findViewById(R.id.room_newmap_2drenderer);
		if(map2drenderer==null)
			map2drenderer = new NewRoomMap2DRenderer(NewRoomMap2D.this);
		room_newmap_regularize=(ImageButton)findViewById(R.id.room_newmap_regularize);
		room_newmap_rotation=(ImageButton)findViewById(R.id.room_newmap_rotation);
		room_newmap_antirotation=(ImageButton)findViewById(R.id.room_newmap_antirotation);
		room_newmap_walltex_container=(LinearLayout)findViewById(R.id.room_newmap_walltex_container);		
		room_newmap_walltex_text=(TextView)findViewById(R.id.room_newmap_walltex_text);
		room_newmap_walltex_image=(ImageView)findViewById(R.id.room_newmap_walltex_image);
		room_newmap_walltex_container.setVisibility(View.INVISIBLE);
		/*room_newmap_noOfWalls = (TextView)findViewById(R.id.room_newmap_noOfWalls);
		room_newmap_heightofRoom=(TextView)findViewById(R.id.room_newmap_heightofRoom);
		room_newmap_area = (TextView)findViewById(R.id.room_newmap_area);
		room_newmap_realWorldLocation=(TextView)findViewById(R.id.room_newmap_wordlocation);
		room_newmap_noOfWalls.setText("No of Walls in Room: "+NewRoomMap.points3dCordinates_augmented.size());
		room_newmap_heightofRoom.setText("Height of Room: Not Set");
		room_newmap_area.setText("Area of Room: "+roundDecimals(map2drenderer.area_sum) +" sq fts");*/
		room_canvas_height = "Not Set";
		room_canvas_area = roundDecimals(map2drenderer.area_sum) + " sq fts";
		room_canvas_location = "Not Set";
		room_location_calc = "";
		location_update_completed = false;
		wall_textures_content=new ArrayList<Bitmap>();
		wall_textures_content_BASE64=new ArrayList<String>();
		//room_newmap_edit_texture = (MenuItem)findViewById(R.id.room_newmap_edit_texture);

		//Initialize Default Textured Bitmaps
		Bitmap b = BitmapFactory.decodeResource(getResources(),R.drawable.room_newmap_walltex_default);
		for(int i=0;i<NewRoomMap.points3dCordinates_augmented.size();i++) {	
			wall_textures_content.add(b);
		}
		//Initialize Default BASE64 formats
		for(int i=0;i<NewRoomMap.points3dCordinates_augmented.size();i++) {
			wall_textures_content_BASE64.add(BitMapToString(b));
		}
		
		//Initialize Default Floor Bitmap
		floor_bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.room_newmap_floortex_default);
		crObj = new CopyResources(getResources().getAssets(), Environment.getExternalStorageDirectory() + File.separator + "SpacePlanARData" + File.separator + "TemporaryFiles");
		startListeners();
	}

	private String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
  }
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		super.onActivityResult(requestCode, resultCode, data);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewRoomMap2D.this);
		//For Storing Height Results
		if(requestCode==NewRoomMapHeight_requestCode){				
			room_canvas_height = sharedPreferences.getString("HEIGHT_MAP","") + " fts";
	        //room_newmap_heightofRoom.setText("Height of Room: " + room_height);
		} else if(requestCode==NewRoomMapSave_requestCode){
			room_canvas_height = sharedPreferences.getString("MAP_SAVED","");			    
		    if(room_canvas_height == "saved"){
		    	alert_messages_finalsave("Room Map has been saved! View your Maps from Main Menu.");
		    	// Delete all temporary files from Temporary Folder
				crObj.deleteAllTempFiles();
				System.gc();
		    } else if (room_canvas_height == "notsaved"){
		    	alert_messages_ok("Room Map has not been saved!");
		    }
		} else if(requestCode==NewRoomMapEditTexture_requestCode){
			popUpTextureView_Show(current_wall_id);
		}
	}

	// Location Listeners
	public void onLocationChanged(Location location) {		
		Toast.makeText(NewRoomMap2D.this,"Your Real World Location has been Retrieved",Toast.LENGTH_SHORT).show();
		if (location != null) {
			locationManager.removeUpdates(this);
		try {
			List<Address> addresses = geocoder.getFromLocation(
					location.getLatitude(), location.getLongitude(), 10);
			/*room_newmap_realWorldLocation.setText("");
			this.room_newmap_realWorldLocation.append(addresses.get(0).getCountryName() + "_");*/
			room_location_calc = "";
			room_location_calc = room_location_calc + addresses.get(0).getCountryName() + "_";

			if (addresses.get(0).getThoroughfare() != null) {
				//this.room_newmap_realWorldLocation.append(addresses.get(0).getThoroughfare()+ "_");
				room_location_calc = room_location_calc + addresses.get(0).getThoroughfare() + "_";
			} else if (addresses.get(0).getSubThoroughfare() != null) {
				//this.room_newmap_realWorldLocation.append(addresses.get(0).getSubThoroughfare()+ "_");
				room_location_calc = room_location_calc + addresses.get(0).getSubThoroughfare() + "_";
			} else if (addresses.get(0).getFeatureName() != null) {
				//this.room_newmap_realWorldLocation.append(addresses.get(0).getFeatureName()+ "_");
				room_location_calc = room_location_calc + addresses.get(0).getFeatureName() + "_";
			}
			if (addresses.get(0).getSubLocality() != null) {
				//this.room_newmap_realWorldLocation.append(addresses.get(0).getSubLocality()+ "_");
				room_location_calc = room_location_calc + addresses.get(0).getSubLocality() + "_";
			}
			if (addresses.get(0).getLocality() != null) {
				//this.room_newmap_realWorldLocation.append(addresses.get(0).getLocality() + "_");
				room_location_calc = room_location_calc + addresses.get(0).getLocality() + "_";
			}

			if (addresses.get(0).getAdminArea() != null) {
				//this.room_newmap_realWorldLocation.append(addresses.get(0).getAdminArea());
				room_location_calc = room_location_calc + addresses.get(0).getAdminArea();
			}

			/*map_location = room_newmap_realWorldLocation.getText().toString();
			map_location = map_location.replaceAll("\\s+", "_");*/
			//room_newmap_realWorldLocation.setText("Real World Location : "+map_location);
			room_location_calc = room_location_calc.replaceAll("\\s+", "_");
			room_canvas_location = room_location_calc;
			map2drenderer.invalidate();
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewRoomMap2D.this);
		    SharedPreferences.Editor editor = sharedPreferences.edit();
		    //editor.putString("LOCATION_MAP",map_location);
		    editor.putString("LOCATION_MAP",room_location_calc);
		    editor.commit();
		    location_update_completed = true;		    
		} catch (IOException e) {			
			e.printStackTrace();
		}
		}
		
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}	

	// Options Menu in Action Bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.room_newmap_2dview_menu, menu);
	    room_newmap_texture_menu = menu.getItem(4);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.room_newmap_modify_texture:
				popUpTextureView_Hide();            	
            	Intent tex_i = new Intent(NewRoomMap2D.this, NewRoomMapTextures.class);
            	startActivityForResult(tex_i, NewRoomMapEditTexture_requestCode);
				break;
			case R.id.room_newmap_reset_texture:
				popUpTextureView_Hide();
            	/*Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.room_newmap_walltex_default);
            	wall_textures_content.set(current_wall_id, bmp);*/            	
            	Bitmap bmp = null;
            	InputStream is = getResources().openRawResource(R.drawable.room_newmap_walltex_default);
            	try {
            		bmp = BitmapFactory.decodeStream(is);
            	} catch (Exception e) {
            		e.printStackTrace();
				} finally {
					try {
						is.close();
						is = null;
					} catch (IOException e) {
						e.printStackTrace();
					}			                    
				}
            	
            	wall_textures_content.set(current_wall_id, bmp);
            	wall_textures_content_BASE64.set(current_wall_id,BitMapToString(bmp));
            	popUpTextureView_Show(current_wall_id);
				break;
			case R.id.room_newmap_2d_details:
				room_details = 	"Area of Room: " +
									room_canvas_area + "\n" +
									"Height of Room: " +
									room_canvas_height + "\n" +
									"Real World Location: " +
									room_canvas_location + "\n" +
									"No of Walls in Room: " +
									NewRoomMap.points3dCordinates_augmented.size();
				alert_dialog = new AlertDialog.Builder(NewRoomMap2D.this).create();
				alert_dialog.setTitle("Room Details");
				alert_dialog.setMessage(room_details);
				alert_dialog.setIcon(R.drawable.symbol_alert);
				alert_dialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				alert_dialog.show();			
				break;		
			case R.id.room_newmap_retrivegpsloc:		
				/*actionBar = getActionBar();
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);*/
				
				//room_newmap_realWorldLocation.setText("Waiting for Location Updates...");
				room_canvas_location = "Waiting for Location Updates...";
				map2drenderer.invalidate();
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				geocoder = new Geocoder(NewRoomMap2D.this);
				try {
					gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
					Debug.startMethodTracing("trace");
					network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
					Debug.stopMethodTracing();
				} catch (Exception ex) {
				}
				Log.v("GPS", gps_enabled+"");
				Log.v("NW", network_enabled+"");
			
				//if (!gps_enabled && !network_enabled) {
				if (!gps_enabled) {
					String alert_message = "Sorry, location cannot determined. Location providers are not enabled.Location set to 'Location_Unknown' ";
					alert_messages_ok(alert_message);
					//room_newmap_realWorldLocation.setText("Real World Location : Location_Unknown");
					room_canvas_location = "Location_Unknown";
					map2drenderer.invalidate();
					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewRoomMap2D.this);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					//editor.putString("LOCATION_MAP",map_location);
					editor.putString("LOCATION_MAP","Location_Unknown");
					editor.commit();
					location_update_completed = true;
				}				  
				/*if (gps_enabled) {
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, NewRoomMap2D.this); 
				}
				if (network_enabled) {
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, NewRoomMap2D.this);
				}*/			
				break;			
			case R.id.room_newmap_2d_setHeight:
				/*actionBar = getActionBar();
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);*/
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewRoomMap2D.this);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				if(sharedPreferences.getString("HEIGHT_MAP","")!=""){
					editor.remove("HEIGHT_MAP");
					editor.commit();
				}
				Intent i = new Intent(NewRoomMap2D.this, NewRoomMapSetHeight.class);
				startActivityForResult(i, NewRoomMapHeight_requestCode); 
				break;	    
			case R.id.room_newmap_saveMap:
				/*actionBar = getActionBar();
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);*/
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NewRoomMap2D.this);			  
				SharedPreferences.Editor edit = preferences.edit();
				edit.putString("MAP_SAVED","notsaved");
				edit.commit();
				String saved_height=preferences.getString("HEIGHT_MAP", "");
				if(location_update_completed == false) {
					alert_messages_ok("Please retrive World Location of moom before you can save map"); 
				} else if(saved_height=="") {
					alert_messages_ok("Please set Height of Room before you can save map"); 
				} else if(location_update_completed == true && saved_height!=""){		    	  
					SharedPreferences area_sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewRoomMap2D.this);
					SharedPreferences.Editor area_editor = area_sharedPreferences.edit();
					area_editor.putString("AREA_MAP",roundDecimals(map2drenderer.area_sum) +" sq fts");
					area_editor.putString("NO_WALLS_MAP",NewRoomMap.points3dCordinates_augmented.size()+"");
					area_editor.commit();
					try {
						Intent save_i = new Intent(NewRoomMap2D.this, NewRoomMapSaveMap.class);
						startActivityForResult(save_i, NewRoomMapSave_requestCode);
					} catch(Exception e){
						e.printStackTrace();
						alert_dialog = new AlertDialog.Builder(NewRoomMap2D.this).create();
						alert_dialog.setTitle("Error");
						alert_dialog.setMessage("Error saving Room Map!");
						alert_dialog.setIcon(R.drawable.symbol_error);
						alert_dialog.setButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						alert_dialog.show();
					}
				}			  
				break;      
			default:
				break;
		}
	    return true;
	  }	
}
