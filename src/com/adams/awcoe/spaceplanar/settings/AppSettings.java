package com.adams.awcoe.spaceplanar.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSettings {
    public String CAMERA_HEIGHT="4.7";    
    String cam_height_val;
    float tilt_correction_val;    
    
    public void SetDefaultCameraSettings(Context c) {    	
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CAMERA_HEIGHT",CAMERA_HEIGHT);
        editor.commit();        
    }
    public void SetCameraSettings(Context c,float data) {    	
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();        
        editor.putString("CAMERA_HEIGHT",data+"");
        editor.commit();        
    }    
    
    public void SetDefaultTiltSettings(Context c) {    	
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("TILT_CORRECTION",0);
        editor.commit();        
    }
    
    public void SetTiltSettings(Context c,float data) {    	
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("TILT_CORRECTION",data);
        editor.commit();
    }    
    
    public String getCamHeight(Context c) {
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        cam_height_val = sharedPreferences.getString("CAMERA_HEIGHT", "");
    	return cam_height_val;
    }
    
    public float getTiltCorrection(Context c) {
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
    	tilt_correction_val = sharedPreferences.getFloat("TILT_CORRECTION",0);
    	return tilt_correction_val;
    }   
}
