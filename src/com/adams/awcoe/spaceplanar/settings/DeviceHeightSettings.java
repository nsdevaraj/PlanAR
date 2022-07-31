package com.adams.awcoe.spaceplanar.settings;

import com.adams.awcoe.spaceplanar.app.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

public class DeviceHeightSettings extends Activity {
	SeekBar camValueSeekBar;
	TextView camValue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spaceplanar_settings_deviceheight);
		camValue=(TextView)findViewById(R.id.camHeightSettingsValue);
		camValueSeekBar=(SeekBar)findViewById(R.id.camHeightSettingsBar);
		AppSettings obj = new AppSettings();
		float height = Float.parseFloat(obj.getCamHeight(DeviceHeightSettings.this));
		camValue.setText(height + " fts");
		camValueSeekBar.setProgress((int) height*10);
		camValueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {			
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				float c=(float)(progress/10.0);
				camValue.setText(c+" fts");
				AppSettings obj=new AppSettings();
				obj.SetCameraSettings(DeviceHeightSettings.this,c);
			}
		});
		
	}	
}
