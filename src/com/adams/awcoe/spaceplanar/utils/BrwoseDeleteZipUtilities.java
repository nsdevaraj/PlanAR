package com.adams.awcoe.spaceplanar.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BrwoseDeleteZipUtilities {
	File directory;
	String modelName;
	Boolean isRoom;
	ArrayList<File> fileListOBJ = new ArrayList<File>();
	
	public BrwoseDeleteZipUtilities(File directory, String modelName, Boolean isRoom) {
		super();
		this.directory = directory;
		this.modelName = modelName;
		this.isRoom = isRoom;
	}
	
	private void fill(File[] files) {
		for (File file : files) {
			fileListOBJ.add(file);
		}		
	}

	public void browseTo(File aDirectory) {
		if (aDirectory.isDirectory()) {
			fill(aDirectory.listFiles());
		}		
	}
	
	//Delete Singular Files after ZIPPING
	public void delete() {
		for (File file : fileListOBJ) {
			file.delete();
		}
		if(isRoom == true){
			directory.delete();
		}		
	}
	
	//ZIP Utility
	public void zip(){
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = null;
			if(isRoom == true){
				dest = new FileOutputStream(directory.getParent()+File.separator+modelName+".zip");
			} else {
				dest = new FileOutputStream(directory.getPath()+File.separator+modelName+".zip");
			}
			
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest)); 
			byte data[] = new byte[2048]; 
	 
			for(int i=0; i < fileListOBJ.size(); i++) { 
				//Log.v("Compress", "Adding: " + fileListOBJ.get(i));
				FileInputStream fi = new FileInputStream(fileListOBJ.get(i)); 
				origin = new BufferedInputStream(fi,2048); 
				ZipEntry entry = new ZipEntry(fileListOBJ.get(i).getName().substring(fileListOBJ.get(i).getName().lastIndexOf("/") + 1)); 
				out.putNextEntry(entry); 
				int count; 
				while ((count = origin.read(data, 0,2048)) != -1) { 
					out.write(data, 0, count); 
				} 
				origin.close(); 
			}
			out.close(); 
		} catch(Exception e) {
			e.printStackTrace(); 
		}
	}
}
