package com.adams.awcoe.spaceplanar.room;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

import com.adams.awcoe.spaceplanar.app.R;
import com.adams.awcoe.spaceplanar.room.mapdata.ReadAugmented3DPointsData;
import com.adams.awcoe.spaceplanar.utils.BrwoseDeleteZipUtilities;
import com.adams.awcoe.spaceplanar.utils.CopyResources;

public class ViewRoomMapOBJModelWriter {
	File main_directory, sub_directory;
	private String CHOSEN_LOCATION_FLOOR_PATH;

	void DeleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				DeleteRecursive(child);

		fileOrDirectory.delete();
	}
	
	//Saving Bitmap Files and creating corresponding MTL Texture file for OBJ File
	public void saveBitmapsTextures(Context c)
	{
		try {
			
			String store_location=main_directory.getPath()+File.separator;
			Bitmap bmp;
			ArrayList<Bitmap> textures=ViewRoomMap.textures_Base64_to_Image;
			for (int j = 0; j < textures.size(); j++) {
			bmp=(textures.get(j));
			FileOutputStream out = new FileOutputStream(store_location+"room_wall"+(j+1)+".JPEG");
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
				}
			// Saving Floor Tile
			bmp=BitmapFactory.decodeResource(c.getResources(),R.drawable.floor_tile);
			FileOutputStream out = new FileOutputStream(store_location+"room_floor"+".JPEG");
		    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
		    out.flush();
		    out.close();
			
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					new File(main_directory.getPath() + File.separator,
							"room_texture.mtl")));
			
			bufferedWriter.write("newmtl room_floor_tex");
			bufferedWriter.newLine();
			bufferedWriter.write("illum 4");
			bufferedWriter.newLine();
			bufferedWriter.write("Kd 1.00 1.00 1.00");
			bufferedWriter.newLine();
			bufferedWriter.write("Ka 1.00 1.00 1.00");
			bufferedWriter.newLine();
			bufferedWriter.write("Tf 1.00 1.00 1.00");
			bufferedWriter.newLine();
			bufferedWriter.write("map_Kd "+"room_floor.JPEG");
			bufferedWriter.newLine();
			bufferedWriter.write("Ni 1.00");
			bufferedWriter.newLine();
			bufferedWriter.newLine();
			bufferedWriter.newLine();
			

				ArrayList<ReadAugmented3DPointsData> singlemapModel = ViewRoomMap.readAugmented3DPointsCordinates;
				for (int j = 0; j < singlemapModel.size(); j++) {
					bufferedWriter.write("newmtl room_wall"+(j+1));
					bufferedWriter.newLine();
					bufferedWriter.write("illum 4");
					bufferedWriter.newLine();
					bufferedWriter.write("Kd 1.00 1.00 1.00");
					bufferedWriter.newLine();
					bufferedWriter.write("Ka 1.00 1.00 1.00");
					bufferedWriter.newLine();
					bufferedWriter.write("Tf 1.00 1.00 1.00");
					bufferedWriter.newLine();
					bufferedWriter.write("map_Kd room_wall"+(j+1)+".JPEG");
					bufferedWriter.newLine();
					bufferedWriter.write("Ni 1.00");
					bufferedWriter.newLine();
					bufferedWriter.newLine();
					bufferedWriter.newLine();
				}
			
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public void exportMapOBJ(Context c) {
		ArrayList<String> vertices = new ArrayList<String>();
		ArrayList<String> faces = new ArrayList<String>();
		ArrayList<String> tex_wall = new ArrayList<String>();
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		CHOSEN_LOCATION_FLOOR_PATH = sharedPreferences.getString(
				"CHOSEN_LOCATION_FLOOR", "");

		try {
			main_directory = new File(CHOSEN_LOCATION_FLOOR_PATH + "/OBJRoomMaps/"
					+ ViewRoomMap.readMapInfo.get(0));
			if (main_directory.exists()) {
				DeleteRecursive(main_directory);
				main_directory.mkdirs();				
			} else {
				main_directory.mkdirs();
			}
			/*sub_directory = new File(main_directory.getPath() + File.separator + "objWebViewer");
			sub_directory.mkdirs();*/
			
			saveBitmapsTextures(c);
			
			/*BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					new File(main_directory.getPath(), ViewRoomMap.readMapInfo
							.get(0) + ".obj")));*/
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					new File(main_directory.getPath(), "obj_model.obj")));
			bufferedWriter.write("# Room OBJ Model");
			bufferedWriter.newLine();
			bufferedWriter.newLine();
			bufferedWriter.write("# Materials Definition");
			bufferedWriter.newLine();
			bufferedWriter.write("mtllib room_texture.mtl");
			bufferedWriter.newLine();
			bufferedWriter.write("# Vertex Definition");
			bufferedWriter.newLine();
			bufferedWriter.newLine();

			ArrayList<ReadAugmented3DPointsData> singlemapModel = ViewRoomMap.readAugmented3DPointsCordinates;
			// Adding Floor Vertices
			for (int i = 0; i < singlemapModel.size(); i++) {
				vertices.add("v" + " " +
				(singlemapModel.get(i).getXMark()) + " "
						+ (singlemapModel.get(i).getYMark()) + " " + 0 + "");

			}
			// Writing Floor Vertices
			for (int k = 0; k < vertices.size(); k++) {
				bufferedWriter.write(vertices.get(k));
				bufferedWriter.newLine();
			}
			// Adding Floor Faces
			for (int k = 2; k < vertices.size(); k++) {
				faces.add("f" + " " + 1 + "/1" + " " + (k) + "/2" + " "
						+ (k + 1) + "/3");
			}
			int no_floor_faces = faces.size();
			int floor_vertex_count = vertices.size();
			// Adding Roof Vertices
			for (int i = 0; i < singlemapModel.size(); i++) {

				vertices.add("v" + " " +

				(singlemapModel.get(i).getXMark()) + " "
						+ (singlemapModel.get(i).getYMark()) + " "
						+ (singlemapModel.get(i).getZMark()) + "");

			}
			// Writing Roof Vertices
			for (int k = floor_vertex_count; k < vertices.size(); k++) {
				bufferedWriter.write(vertices.get(k));
				bufferedWriter.newLine();
			}
			// Adding Wall Faces
			for (int i = floor_vertex_count; i < vertices.size() - 1; i++) {
				faces.add("f" + " " + (i - floor_vertex_count + 1) + "/1 "
						+ (i + 1) + "/2 " + (i + 2) + "/3 "
						+ (i - floor_vertex_count + 2) + "/4");
			}
			// Adding Final Wall
			faces.add("f" + " " + (floor_vertex_count) + "/1 "
					+ (vertices.size()) + "/2 " + "1" + "/3 "
					+ (floor_vertex_count + 1) + "/4");

			bufferedWriter.newLine();
			bufferedWriter.newLine();
			bufferedWriter.write("# Wall Texture Cordinates");
			bufferedWriter.newLine();
			bufferedWriter.newLine();

			tex_wall.add("vt 0.0 0.0");
			tex_wall.add("vt 0.0 1.0");
			tex_wall.add("vt 1.0 1.0");
			tex_wall.add("vt 1.0 0.0");

			for (int l = 0; l < tex_wall.size(); l++) {
				bufferedWriter.write(faces.get(l));
				bufferedWriter.newLine();
			}

			int wall_no=1;
			bufferedWriter.newLine();
			bufferedWriter.newLine();
			bufferedWriter.write("# Faces Definition");
			bufferedWriter.newLine();
			bufferedWriter.newLine();
			bufferedWriter.write("usemtl room_floor_tex");
			bufferedWriter.newLine();
			for (int l = 0; l < faces.size(); l++) {
				if (l < no_floor_faces) {
					bufferedWriter.write(faces.get(l));
					bufferedWriter.newLine();
				} else {
					bufferedWriter.write("usemtl room_wall"
							+ wall_no);
					bufferedWriter.newLine();
					bufferedWriter.write(faces.get(l));
					bufferedWriter.newLine();
					wall_no++;
				}
			}
			bufferedWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Copy html from Assets/objWebViewer
		CopyResources copy_res_obj = new CopyResources(c.getResources().getAssets(), main_directory.getPath());
		copy_res_obj.copyObjWebViewerFiles();
		
		BrwoseDeleteZipUtilities utlis = new BrwoseDeleteZipUtilities(main_directory, ViewRoomMap.readMapInfo.get(0), true);
		//Browse to OBJModel Directory and ZIP the contents into single File
		utlis.browseTo(main_directory);
		utlis.zip();
		
		//Delete singular files after ZIPPING
		utlis.delete();

	}
}
