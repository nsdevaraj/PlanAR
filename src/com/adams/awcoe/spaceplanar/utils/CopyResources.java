package com.adams.awcoe.spaceplanar.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.res.AssetManager;
import android.util.Log;

public class CopyResources {
	private static File fileInCard;
	private static String newFileName;
	private static String dest_dir_Path;
	private static InputStream inputStream = null;
	private static OutputStream outputStream = null;
	private static AssetManager assetManager;
	
	public CopyResources(AssetManager assetMgr, String destination_dir){
		assetManager = assetMgr;
		dest_dir_Path = destination_dir;
	}
	
	public void copyObjWebViewerFiles(){
		String[] files = null;
	    try {
	        files = assetManager.list("objWebViewer");
	        for(String file_name : files) {
	            inputStream = null;
	            outputStream = null;
	            try {
	            	inputStream = assetManager.open("objWebViewer" + File.separator + file_name);
	            	outputStream = new FileOutputStream(dest_dir_Path + File.separator + file_name);
	            	copyFile(inputStream, outputStream);
	            	inputStream.close();
	            	inputStream = null;
	            	outputStream.flush();
	            	outputStream.close();
	            	outputStream = null;
	            } catch(IOException e) {
	                Log.e("tag", "Failed to copy asset file: " + file_name, e);
	            }       
	        }
	    } catch (IOException e) {
	        Log.e("tag", "Failed to get asset file list.", e);
	    }
	}
	
	private void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
	
	public File copyHelpFiles(String fileName){
		fileInCard = new File(dest_dir_Path + File.separator + fileName);
		
		if(fileInCard.exists()){
			return fileInCard;
		} else {
			try {
				inputStream = assetManager.open("helpfiles" + File.separator + fileName);
				newFileName = dest_dir_Path + File.separator + fileName;
				outputStream = new FileOutputStream(newFileName);
				copyFile(inputStream, outputStream);
            	inputStream.close();
            	inputStream = null;
            	outputStream.flush();
            	outputStream.close();
            	outputStream = null;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			} finally {
				if(inputStream != null){
					try {
						inputStream.close();
					} catch (IOException e){
					}
				}
				if(outputStream != null){
					try {
						outputStream.close();
					} catch (IOException e){
					}
				}
			}
			return new File(dest_dir_Path + File.separator + fileName);
		}		
	}
	
	public void deleteTempFile(String fileName){
		fileInCard = new File(dest_dir_Path + File.separator + fileName);
		if(fileInCard.exists()){
			fileInCard.delete();
		}	
	}
	
	public void deleteAllTempFiles(){
		for (File file : new File(dest_dir_Path).listFiles()){
			file.delete();
		}
	}
}