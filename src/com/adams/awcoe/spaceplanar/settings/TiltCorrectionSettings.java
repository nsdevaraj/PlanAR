package com.adams.awcoe.spaceplanar.settings;

import com.adams.awcoe.spaceplanar.app.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class TiltCorrectionSettings extends Activity implements SensorListener {

	
	TextView tiltCorrectionValue;
	TextView tiltCorrectionLabel;
    Button calibrateTilt;
    Button calibrateTiltDefault;
    Button devicedisplaysettings;
    float y;
    
    //Android Variables
    SensorManager sm = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Toast.makeText(TiltCorrectionSettings.this,"Place your device on flat surface or ground before calibrating.", Toast.LENGTH_LONG).show();
		setContentView(R.layout.spaceplanar_settings_tiltcorrection);
	    sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sm.registerListener(this, SensorManager.SENSOR_ORIENTATION,
				SensorManager.SENSOR_DELAY_NORMAL);
		
		calibrateTiltDefault=(Button)findViewById(R.id.calibrateTiltDefault);
		calibrateTilt=(Button)findViewById(R.id.calibrateTilt);
		tiltCorrectionValue=(TextView)findViewById(R.id.tiltCorrectionValue);
		devicedisplaysettings=(Button)findViewById(R.id.devicedisplaysettings);
		calibrateTilt.setOnClickListener(new OnClickListener() {			
			public void onClick(View arg0) {
				AppSettings obj=new AppSettings();
				obj.SetTiltSettings(TiltCorrectionSettings.this,y);
				finish();
			}
		});
		calibrateTiltDefault.setOnClickListener(new OnClickListener() {			
			public void onClick(View arg0) {
				AppSettings obj=new AppSettings();
				obj.SetDefaultTiltSettings(TiltCorrectionSettings.this);
				finish();
			}
		});
		devicedisplaysettings.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				Intent opensettingsIntent = new Intent(android.provider.Settings.ACTION_DISPLAY_SETTINGS);
				startActivity(opensettingsIntent);
			}
		});
	}

	public void onAccuracyChanged(int arg0, int arg1) {
		
		
	}

	public void onSensorChanged(int sensor, float[] values) {
		if (sensor == SensorManager.SENSOR_ORIENTATION) {
			y = -(values[1]);
		}
		y = Math.round(y);
		tiltCorrectionValue.setText(" "+y+" Degrees");
	}
	
	
	

}
