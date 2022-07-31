package com.adams.awcoe.spaceplanar.room;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.adams.awcoe.spaceplanar.app.R;
import com.adams.awcoe.spaceplanar.room.mapdata.AugmentedHeightPointsData;
import com.adams.awcoe.spaceplanar.settings.AppSettings;
import com.adams.awcoe.spaceplanar.utils.SensorCompass;

@SuppressWarnings("deprecation")
public class NewRoomMapSetHeight extends Activity implements SurfaceHolder.Callback,
SensorListener{
	//Android Variables
	TextView setheight_currentHeight;
	//Button room_newmap_setheight_mark;
	ImageView room_newmap_setheight_mark;
	Camera camera = null;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	SensorManager sm = null;
	NewRoomMapHeightCrosshairView crosshairview;
	
	//Member Variables
	SensorCompass compass;
	public static ArrayList<AugmentedHeightPointsData> mp;
	boolean previewing = false;
	float x, y, z;
	double current_projection;
	float cam_height;
	// Dynamic Alert Messages
	private void alert_messages(String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(NewRoomMapSetHeight.this)
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
	
	private void alert_height_marked(String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(NewRoomMapSetHeight.this)
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
				camera.stopPreview();
	    		camera.release();
	    		camera = null;
	    		previewing = false;    
				finish();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}
	
	private void init() {
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.screenBrightness = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		getWindow().setAttributes(layoutParams);

		// Registering Sensors
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sm.registerListener(this, SensorManager.SENSOR_ORIENTATION,
				SensorManager.SENSOR_DELAY_FASTEST);

		//Initialize Class Data
		mp = new ArrayList<AugmentedHeightPointsData>();
		
		// Initialize Views
		compass = (SensorCompass) findViewById(R.id.sensor_compass);	
		//setheight_viewangle=(TextView)findViewById(R.id.setheight_viewangle);
		setheight_currentHeight=(TextView)findViewById(R.id.setheight_currentHeight);
		room_newmap_setheight_mark=(ImageView)findViewById(R.id.room_newmap_setheight_mark);
		//bottom_infobar = (LinearLayout)findViewById(R.id.bottom_infobar);
		
		LayoutParams lpmarks = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
				crosshairview = new NewRoomMapHeightCrosshairView(this);				
				addContentView(crosshairview, lpmarks);				
		//crosshairview.setBottomInfoBarHeight(bottom_infobar.getHeight());
		
		Toast.makeText(this,
				"Place a Flag Point above ground to set map height",
				Toast.LENGTH_SHORT).show();
	}
	
	private String generateMapHeight() {		
		String h = null;
		if(y<90) {
			float H = (float) ((mp.get(0)
					.getProjection() / Math.tan(Math.toRadians(Math.abs(mp
					.get(0).getYAngle())))));
			h = H + "";
		} else {
			float H = (float) (cam_height + (mp.get(0)
					.getProjection() * Math.tan(Math.toRadians(Math.abs(mp
					.get(0).getYAngle()-90)))));
			h = H + "";
		}
		return h;
	}
	
	private String generateDynamicMapHeight() {
		String h = null;
		if(y>0 && y<90) {
			float H = (float) ((current_projection / Math.tan(Math.toRadians(Math.abs(y)))));
			h = H + " fts";
		}
		else if(y>90 && y<179) {
			float H = (float) (cam_height + (current_projection * Math.tan(Math.toRadians(Math.abs(y - 90)))));
			h = H + " fts";
		} else if(y==0 || y==180) {
			h = "Infinity";
		} else {
			h = "Invalid";
		}
		
		return h;
	}
	
	private void initlisteners(){
		// Set Height
		room_newmap_setheight_mark.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(y>0 && y<180){
					AugmentedHeightPointsData hpoint = new AugmentedHeightPointsData();
					hpoint.setXAngle(x);
					hpoint.setYAngle(y);
					hpoint.setProjection(current_projection);
					mp=new ArrayList<AugmentedHeightPointsData>();
					mp.add(hpoint);
					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewRoomMapSetHeight.this);
				    SharedPreferences.Editor editor = sharedPreferences.edit();
				    String h=generateMapHeight();
				    editor.putString("HEIGHT_MAP",h);
				    editor.commit();
				    alert_height_marked("Room Height has been set to : "+ h+ " fts");
				} else {
					Toast.makeText(NewRoomMapSetHeight.this, "You have selected an Invalid Height. Please Try Again.", Toast.LENGTH_LONG).show();
				}
			}
		});		
	}
	
	
	double roundDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("###.###");
		return Double.valueOf(twoDForm.format(d));
	}

	float roundDecimalsFloat(float d) {
		DecimalFormat twoDForm = new DecimalFormat("###.###");
		return Float.valueOf(twoDForm.format(d));
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		AppSettings obj=new AppSettings();
		cam_height = Float.parseFloat(obj.getCamHeight(NewRoomMapSetHeight.this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_newmap_generate2dview_setheight);
		init();
		
		// Surface View for Camera
		getWindow().setFormat(PixelFormat.UNKNOWN);
		surfaceView = (SurfaceView) findViewById(R.id.room_newmap_setheight_surface);		
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		initlisteners();
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
	public void onBackPressed() {	
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewRoomMapSetHeight.this);
        String h=sharedPreferences.getString("HEIGHT_MAP","");
		if(h=="") {
        	alert_messages("Please set your Room Height.");        	
        } else {
        	camera.stopPreview();
    		camera.release();
    		camera = null;
    		previewing = false;
    		finish();
        }
	}
	

	public void onAccuracyChanged(int sensor, int accuracy) {		
	}

	public void onSensorChanged(int sensor, float[] values) {
		if (sensor == SensorManager.SENSOR_ORIENTATION) {
			x = (values[0]);
			y = -(values[1]);
		}
		x = Math.round(x);
		y = Math.round(y);
		current_projection = Math.abs(cam_height
				* Math.tan(Math.toRadians(y))) + 1.3;
		compass.setDirection((int) x);
		//setheight_viewangle.setText("View Angle : " + roundDecimalsFloat(y));
		setheight_currentHeight.setText("Projected Height: "+generateDynamicMapHeight());
		crosshairview.printYTilt(y);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
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
		//if(camera!= null){
			camera = Camera.open();
		//}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		/*camera.stopPreview();
		camera.release();
		camera = null;
		previewing = false;*/
	}	
}
