package com.adams.awcoe.spaceplanar.room;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adams.awcoe.spaceplanar.app.R;
import com.adams.awcoe.spaceplanar.room.mapdata.Augmented3DPointsData;
import com.adams.awcoe.spaceplanar.room.mapdata.AugmentedPointsData;
import com.adams.awcoe.spaceplanar.settings.AppSettings;
import com.adams.awcoe.spaceplanar.utils.SensorCompass;

@SuppressWarnings("deprecation")
public class NewRoomMap extends Activity implements SurfaceHolder.Callback, SensorListener{
	// Activity Constants
	public final int NewRoomMap2D_requestCode = 2;	
	
	//Member Variables
	public static float cam_height;
	public static boolean mark_start = false;
	public static boolean mark_end = false;
	boolean previewing = false;
	static double current_projection;
	static double calculated_distance;
	public static float area_sum;
	float x, y, tilt;
	float y_tiltcorrection=0;
	//CharSequence cordinates;
	String CordPosition = "";
	public static ArrayList<Augmented3DPointsData> points3dCordinates_augmented;
	public static ArrayList<AugmentedPointsData> points_augmented;

	
	//Android Variables
	Camera camera;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	SensorManager mSensorManager = null;
	//TextView angleofView;
	TextView heightofDevice;
	SeekBar adjustedCameraHeight;
	TextView pointStore;
	ImageView createMarker;
	ImageView room_newmap_done;
	ImageView rooom_newmap_delete;
	ImageView room_newmap_delete_all;
	ImageView anim_mark;
	ImageView crosshair;
	Display display;
	TranslateAnimation slide;
	
	//Class Variables
	NewRoomMapMakersView new_room_map_makers_view;
	SensorCompass sensor_compass;
	
	
	// Member Methods
	private void init() {	
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.screenBrightness = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		getWindow().setAttributes(layoutParams);
		
		//Initializing Sensors
		// Initializing Sensors
		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		mSensorManager.registerListener(this, SensorManager.SENSOR_ORIENTATION,
				SensorManager.SENSOR_DELAY_GAME);
		
		// Setting Makers Points Classes
		points3dCordinates_augmented= new ArrayList<Augmented3DPointsData>();
		points_augmented=new ArrayList<AugmentedPointsData>();
		
		// Drawer on UI
		adjustedCameraHeight = (SeekBar)findViewById(R.id.adjustDeviceHeight);
		adjustedCameraHeight.setProgress((int) (cam_height*10));
		sensor_compass = (SensorCompass) findViewById(R.id.sensor_compass);
		//angleofView = (TextView) findViewById(R.id.viewAngle);
		heightofDevice=(TextView) findViewById(R.id.deviceHeight);
		heightofDevice.setText("Device Height : "+cam_height+" fts");
		createMarker=(ImageView)findViewById(R.id.room_newmap_createMarker);
		room_newmap_done = (ImageView) findViewById(R.id.room_newmap_done);
		rooom_newmap_delete = (ImageView) findViewById(R.id.room_newmap_delete);
		room_newmap_delete_all = (ImageView) findViewById(R.id.room_newmap_delete_all);
		anim_mark = (ImageView) findViewById(R.id.anim_mark);
		anim_mark.setVisibility(View.INVISIBLE);
		display = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();

		// Initializing Marker Animation
		slide = new TranslateAnimation(0, 0, 0, display.getHeight() / 2
				+ anim_mark.getHeight());
		slide.setDuration(1000);
		
		// Drawing Markers on UI
		LayoutParams lpmarks = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		new_room_map_makers_view = new NewRoomMapMakersView(this);
		addContentView(new_room_map_makers_view, lpmarks);		
	}
	
	private void startListeners() {		
		// View 2D Map Generation
		room_newmap_done.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (points_augmented.size()>2) {
					mark_end = true;
					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewRoomMap.this);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.remove("HEIGHT_MAP");
					editor.remove("LOCATION_MAP");
					editor.commit();
					Intent i = new Intent(NewRoomMap.this, NewRoomMap2D.class);
					startActivityForResult(i,NewRoomMap2D_requestCode);
				} else {
                    String alert_message="You should have atleast three Flag Points to generate the map.";
					alert_messages(alert_message);
				}
			}
		});
		
		// Delete Last Updated Marker
		rooom_newmap_delete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (points_augmented.size()>0) {
					Toast.makeText(NewRoomMap.this,
							"Previous Flag Point has been deleted.",
							Toast.LENGTH_SHORT).show();
					points_augmented.remove(AugmentedPointsData.index);
					points3dCordinates_augmented.remove(AugmentedPointsData.index);
					--AugmentedPointsData.index;
					//roommapView.draw_markers_inView(x, y);

				} else {
					String alert_message="There are no Flag Points to be deleted.";
					alert_messages(alert_message);

				}
			}
		});

		// Delete All Markers
		room_newmap_delete_all.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				if (points_augmented.size() > 0) {
					Toast.makeText(NewRoomMap.this,
							"All Flag Points have been deleted",
							Toast.LENGTH_SHORT).show();
					points_augmented.removeAll(points_augmented);
					points3dCordinates_augmented.removeAll(points3dCordinates_augmented);
					AugmentedPointsData.index = -1;
					mark_start = false;
				} else {
					String alert_message="No Flag Point to delete";
					alert_messages(alert_message);
					mark_start = false;
				}

			}
		});
		
		createMarker.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View arg0) {				
				if (y-y_tiltcorrection > 0 && y-y_tiltcorrection < 90) {
					anim_mark.startAnimation(slide);					
					//setLog();
				} else {					
					String alert_message="You are probably looking very far or high. Cannot place Flag Point here.";
					alert_messages(alert_message);					
				}				
			}
		});		

		// Define Animation Listener on Marking Point
		slide.setAnimationListener(new Animation.AnimationListener() {			
			public void onAnimationStart(Animation animation) {
				anim_mark.setVisibility(View.VISIBLE);
				Toast.makeText(NewRoomMap.this, "A Flag Point has been placed.",
						Toast.LENGTH_SHORT).show();
			}			
			public void onAnimationRepeat(Animation animation) {
			}			
			public void onAnimationEnd(Animation animation) {
				anim_mark.setVisibility(View.INVISIBLE);
				AugmentedPointsData point_aug=new AugmentedPointsData();
				point_aug.setProjection(current_projection);
				point_aug.setTilt(x);
				point_aug.setYAngle(y);
				if ((x >= 315 && x <= 359) || (x >= 0 && x <= 45))
					CordPosition = x + "' N";
				if ((x > 45 && x <= 135))
					CordPosition = x + "' E";
				if ((x > 135 && x <= 225))
					CordPosition = x + "' S";
				if ((x > 225 && x < 315))
					CordPosition = x + "' W";
				point_aug.setCordPosition(CordPosition);
				points_augmented.add(point_aug);
				buildAugmented3DPointsCordinates(point_aug.getIndex());
				mark_start = true;
				//roommapView.draw_markers_inView(x, y);
			}
		});		
		
		//Adjusted Device Height Seekbar
		adjustedCameraHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {		
    	   public void onStopTrackingTouch(SeekBar arg0) {		
    	   }
    	   public void onStartTrackingTouch(SeekBar arg0) {
    	   }
    	   public void onProgressChanged(SeekBar seekBar, int progress,
    			   boolean fromUser) {
    		   float c=(float) (progress/10.0);
    		   cam_height=c;
    		   heightofDevice.setText("Device Height : "+cam_height+"fts");
    		   AppSettings obj=new AppSettings();
    		   obj.SetCameraSettings(NewRoomMap.this,cam_height);			 	
    	   }
       });	
	}
	

	//Dynamic Alert Messages
	private void alert_messages(String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(NewRoomMap.this).create();
		alertDialog.setTitle("Note");
		alertDialog.setMessage(message);
		alertDialog.setIcon(R.drawable.symbol_alert);
		alertDialog.setButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
					}
				});
		alertDialog.show();
	}
	
	public void buildAugmented3DPointsCordinates(int i) {
		float stopX = 0, stopY = 0;
			try{
				if (points_augmented.get(i).getTilt() >= 0
						&& points_augmented.get(i).getTilt() <= 90) {
					stopY = (float) (points_augmented.get(i).getProjection() * Math
							.sin(Math.toRadians(points_augmented.get(i).getTilt())));
					stopX = (float) (points_augmented.get(i).getProjection() * Math
							.cos(Math.toRadians(points_augmented.get(i).getTilt())));
				} else if (points_augmented.get(i).getTilt() > 90
						&& points_augmented.get(i).getTilt() <= 180) {
	
					stopY = (float) (points_augmented.get(i).getProjection() * Math
							.sin(Math.toRadians(180 - points_augmented.get(i)
									.getTilt())));
					stopX = -(float) (points_augmented.get(i).getProjection() * Math
							.cos(Math.toRadians(180 - points_augmented.get(i)
									.getTilt())));
				} else if (points_augmented.get(i).getTilt() > 180
						&& points_augmented.get(i).getTilt() <= 270) {
	
					stopX = -(float) (points_augmented.get(i).getProjection() * Math
							.sin(Math.toRadians(270 - points_augmented.get(i)
									.getTilt())));
					stopY = -(float) (points_augmented.get(i).getProjection() * Math
							.cos(Math.toRadians(270 - points_augmented.get(i)
									.getTilt())));
				} else if (points_augmented.get(i).getTilt() > 270
						&& points_augmented.get(i).getTilt() <= 360) {
	
					stopX = (float) (points_augmented.get(i).getProjection() * Math
							.cos(Math.toRadians(360 - points_augmented.get(i)
									.getTilt())));
					stopY = -(float) (points_augmented.get(i).getProjection() * Math
							.sin(Math.toRadians(360 - points_augmented.get(i)
									.getTilt())));
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			Augmented3DPointsData pointCord3d=new Augmented3DPointsData();
			pointCord3d.setXMark((float) roundDecimals(stopX));
			pointCord3d.setYMark((float) roundDecimals(stopY));
			points3dCordinates_augmented.add(pointCord3d);
	}
	
	double roundDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("###.###");
		return Double.valueOf(twoDForm.format(d));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		AppSettings obj=new AppSettings();
		cam_height =Float.parseFloat(obj.getCamHeight(NewRoomMap.this));
		y_tiltcorrection=obj.getTiltCorrection(NewRoomMap.this);		
		setContentView(R.layout.room_newmap_view);
		init();		
		// Surface View for Camera
		getWindow().setFormat(PixelFormat.UNKNOWN);
		surfaceView = (SurfaceView) findViewById(R.id.preview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		Toast.makeText(this, "Tap Screen to Mark Position", Toast.LENGTH_LONG) .show();
		startListeners();
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		if(requestCode==NewRoomMap2D_requestCode) {
			points_augmented.removeAll(points_augmented);
			points3dCordinates_augmented.removeAll(points3dCordinates_augmented);
			AugmentedPointsData.index = -1;
			mark_start = false;
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewRoomMap.this);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.remove("HEIGHT_MAP");
			editor.remove("LOCATION_MAP");
			editor.remove("AREA_MAP");
			editor.commit();
			finish();			
		}
	}

	@Override
	public void onBackPressed() {
		points_augmented.removeAll(points_augmented);
		points3dCordinates_augmented.removeAll(points3dCordinates_augmented);
		AugmentedPointsData.index = -1;
		mark_start = false;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewRoomMap.this);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove("HEIGHT_MAP");
		editor.remove("LOCATION_MAP");
		editor.remove("AREA_MAP");
		editor.commit();
		finish();
	}

	@Override
	protected void onDestroy() {		
		super.onDestroy();
	}

	@Override
	protected void onStop() {		
		super.onStop();
	}
	
	// Sensor Listeners
	public void onAccuracyChanged(int sensor, int accuracy) {
	}

	public void onSensorChanged(int sensor, float[] values) {		
		if (sensor == SensorManager.SENSOR_ORIENTATION) {
			x = (values[0]);
			y = -(values[1]);
		}
		x = Math.round(x);
		//y = Math.round(y-y_tiltcorrection);		
		y = Math.round(y);
		//angleofView.setText("Angle of View: " + y);
		calculated_distance = Math.abs(cam_height * Math.tan(Math.toRadians(y)));
		//current_projection = calculated_distance + 1.3;
		current_projection = calculated_distance + 1.0;
		//current_projection = calculated_distance;
		sensor_compass.setDirection((int) x);
		new_room_map_makers_view.draw_markers_inView(x, y, y_tiltcorrection);
	}
	
	/*void setLog(){
		WindowResolution wr = new WindowResolution(NewRoomMap.this);
		Log.v("width height", wr.getDeviceWeidth() + "  " + wr.getDeviceHeight());
		
		Log.v("CAM HEIGHT", cam_height+"");
		Log.v("Tag","---- Without tilt in Consideration ----");
		Log.v("X and Y used for calcutaion", x + "  " + y);
		Log.v("Projection without +1.1fts", current_projection+"");
		Log.v("Projection with +1.3fts", current_projection+1.3+"");
		
		Log.v("Tag", "---- With tilt in Consideration ----");
		Log.v("y_tiltcorrection", y_tiltcorrection+"");
		Log.v("Y", y+"");
		Log.v("y with tilt correction", y-y_tiltcorrection+"");
		Log.v("X and Y used for calcutaion", x + "  " + (y-y_tiltcorrection));
		
		double distance = Math.abs(cam_height * Math.tan(Math.toRadians(y-y_tiltcorrection)));
		Log.v("Projection without +1.3fts", distance+"");
		Log.v("Projection with +1.3fts", distance+1.3+"");
	}*/

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (previewing) {
			camera.stopPreview();
			previewing = false;
		}
		if (camera != null) {
			try {
				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();
				previewing = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {		
		camera.stopPreview();
		camera.release();
		camera = null;
		previewing = false;
	}
}
