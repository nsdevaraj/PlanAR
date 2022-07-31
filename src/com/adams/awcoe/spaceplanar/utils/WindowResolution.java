package com.adams.awcoe.spaceplanar.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

public class WindowResolution {
	DisplayMetrics display_metrics;
	
	public WindowResolution(Activity caller_activity) {
		display_metrics = caller_activity.getResources().getDisplayMetrics();
	}
	
	public int getDeviceHeight() {
		return display_metrics.heightPixels;
	}
	
	public int getDeviceWidth() {
		return display_metrics.widthPixels;
	}
	
}
