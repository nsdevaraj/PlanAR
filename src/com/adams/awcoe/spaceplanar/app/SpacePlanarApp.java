package com.adams.awcoe.spaceplanar.app;

import java.io.File;

import android.app.Application;
import android.os.Environment;
public class SpacePlanarApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// SpacePlanAR main directory
		File directory = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "SpacePlanARData");
		directory.mkdirs();
		
		// Folder for Documentation pdf files and temporary Files
		directory = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "SpacePlanARData" + File.separator + "TemporaryFiles");
		directory.mkdirs();
	}

}
