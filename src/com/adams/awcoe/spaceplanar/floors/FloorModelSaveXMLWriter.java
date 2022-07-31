package com.adams.awcoe.spaceplanar.floors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.adams.awcoe.spaceplanar.app.R;
import com.adams.awcoe.spaceplanar.floors.modeldata.FloorModelPointsCordinate;
import com.adams.awcoe.spaceplanar.utils.BrwoseDeleteZipUtilities;

public class FloorModelSaveXMLWriter {
	File directory;
	String modelName;
	
	public Bitmap StringToBitMap(String encodedString){
		try {
			byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
			Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
			return bitmap;
		} catch(Exception e) {
			e.getMessage();
			return null;
		}
	}
	
	
	public void saveFloorMap() {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();		
		try {
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("FLOORMAP");
			doc.appendChild(rootElement);
			for (int j = 0; j < NewFloorMap2DRenderer.mapfloorPoints.size(); j++) {
				ArrayList<FloorModelPointsCordinate> singlemapModel = NewFloorMap.floor_model
				.get(j);
				ArrayList<String> singleroom_info=NewFloorMap.all_room_info
				.get(j);
				// point data elements
				Element pointdata = doc.createElement("Data");
				rootElement.appendChild(pointdata);
				
				Attr attr_mapname = doc.createAttribute("MID");
				attr_mapname.setValue(singleroom_info.get(0));
				pointdata.setAttributeNode(attr_mapname);
				
				Attr attr_loc = doc.createAttribute("LOC");
				attr_loc.setValue(singleroom_info.get(1));
				pointdata.setAttributeNode(attr_loc);
				
				Attr attr_area = doc.createAttribute("AREA");
				attr_area.setValue(singleroom_info.get(2));
				pointdata.setAttributeNode(attr_area);
				
				Attr attr_no_walls = doc.createAttribute("NO_WALLS_MAP");
				attr_no_walls.setValue(singleroom_info.get(3));
				pointdata.setAttributeNode(attr_no_walls);
				
				Attr attr_roomHeight = doc.createAttribute("ROOM_HEIGHT");
				attr_roomHeight.setValue(singleroom_info.get(4));
				pointdata.setAttributeNode(attr_roomHeight);
				

				for (int i = 0; i < singlemapModel.size(); i++) {
					Element point = doc.createElement("FlagPoint");
					pointdata.appendChild(point);
					Element Xpoint = doc.createElement("XCord3D");
					Xpoint.appendChild(doc
							.createTextNode(((NewFloorMap2DRenderer.mapfloorPoints
									.get(j).x / 5) + singlemapModel.get(i)
									.getXMark())
									+ ""));
					point.appendChild(Xpoint);

					Element Ypoint = doc.createElement("YCord3D");
					Ypoint.appendChild(doc
							.createTextNode(((NewFloorMap2DRenderer.mapfloorPoints
									.get(j).y / 5) + singlemapModel.get(i)
									.getYMark())
									+ ""));
					point.appendChild(Ypoint);

					Element Zpoint = doc.createElement("ZCord3D");
					Zpoint.appendChild(doc.createTextNode(singlemapModel.get(i)
							.getZMark() + ""));
					point.appendChild(Zpoint);
					
					Element Texture_BASE64 = doc.createElement("Texture_BASE64");
					Texture_BASE64.appendChild(doc.createTextNode(singlemapModel.get(i).getTex_Data()));
					point.appendChild(Texture_BASE64);

				}
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
			File directory = new File(NewFloorMap.floor_name);
			directory.mkdirs();
			
			StreamResult result = new StreamResult(new File(
					NewFloorMap.floor_name + File.separator,
					"floorMap.xml"));

			transformer.transform(source, result);

		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}
	
	//Saving Bitmap Files and creating corresponding MTL Texture file for OBJ File
	public void saveBitmapsTextures(Context c) {
		try {
			
			String store_location=NewFloorMap.floor_name+"/OBJModel" + File.separator;
			Bitmap bmp;
			
			for(int i=0;i<NewFloorMap2DRenderer.mapfloorPoints.size();i++)
			{
				ArrayList<FloorModelPointsCordinate> singlemapModel = NewFloorMap.floor_model
				.get(i);
				for (int j = 0; j < singlemapModel.size(); j++) {
					bmp=StringToBitMap(singlemapModel.get(j).getTex_Data());
					FileOutputStream out = new FileOutputStream(store_location+(i+1)+"room_wall"+(j+1)+".JPEG");
				    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
				    out.flush();
				    out.close();
				}
			}
			// Saving Floor Tile
			bmp=BitmapFactory.decodeResource(c.getResources(),R.drawable.floor_tile);
			FileOutputStream out = new FileOutputStream(store_location+"room_floor"+".JPEG");
		    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
		    out.flush();
		    out.close();
			
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					new File(NewFloorMap.floor_name+"/OBJModel" + File.separator,
							"floor_texture.mtl")));
			
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
			
			for(int i=0;i<NewFloorMap2DRenderer.mapfloorPoints.size();i++)
			{
				ArrayList<FloorModelPointsCordinate> singlemapModel = NewFloorMap.floor_model
				.get(i);
				for (int j = 0; j < singlemapModel.size(); j++) {
					bufferedWriter.write("newmtl "+(i+1)+"room_wall"+(j+1));
					bufferedWriter.newLine();
					bufferedWriter.write("illum 4");
					bufferedWriter.newLine();
					bufferedWriter.write("Kd 1.00 1.00 1.00");
					bufferedWriter.newLine();
					bufferedWriter.write("Ka 1.00 1.00 1.00");
					bufferedWriter.newLine();
					bufferedWriter.write("Tf 1.00 1.00 1.00");
					bufferedWriter.newLine();
					bufferedWriter.write("map_Kd "+(i+1)+"room_wall"+(j+1)+".JPEG");
					bufferedWriter.newLine();
					bufferedWriter.write("Ni 1.00");
					bufferedWriter.newLine();
					bufferedWriter.newLine();
					bufferedWriter.newLine();
				}
			}
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void DeleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            DeleteRecursive(child);

	    fileOrDirectory.delete();
	}

	public void exportFloorMapOBJ(Context c,double translate_x,double translate_y)
	{
		try {
			ArrayList<String> vertices = new ArrayList<String>();
			ArrayList<String> faces = new ArrayList<String>();
			ArrayList<String> fourthwall_vertices=new ArrayList<String>();
			ArrayList<String> tex_wall=new ArrayList<String>();
 			ArrayList<Integer> room_vertex_count=new ArrayList<Integer>();
 			ArrayList<Integer> vertices_count=new ArrayList<Integer>();
			int room_vertex_incr=0;
			int index_1 = 0;
			int index_2 = 2;
			
			directory = new File(NewFloorMap.floor_name+"/OBJModel");
			if(directory.exists())
			{
				DeleteRecursive(directory);
				directory.mkdirs();
			}
			else
			{
				directory.mkdirs();
			}
			
			saveBitmapsTextures(c);
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					new File(NewFloorMap.floor_name+"/OBJModel" + File.separator,
							"floorMap3D.obj")));
			
			bufferedWriter.write("# Floor OBJ Model");
			bufferedWriter.newLine();
			bufferedWriter.newLine();
			bufferedWriter.write("# Materials Definition");
			bufferedWriter.newLine();
			bufferedWriter.write("mtllib floor_texture.mtl");
			bufferedWriter.newLine();
			bufferedWriter.write("# Vertex Definition");
			bufferedWriter.newLine();
			bufferedWriter.newLine();
			
			for (int j = 0; j < NewFloorMap2DRenderer.mapfloorPoints.size(); j++) {

				ArrayList<FloorModelPointsCordinate> singlemapModel = NewFloorMap.floor_model
						.get(j);
				vertices_count.add(singlemapModel.size());
				room_vertex_incr=room_vertex_incr+singlemapModel.size();
                room_vertex_count.add(room_vertex_incr);
                
				for (int i = 0; i < singlemapModel.size(); i++) {					
					if(i==0)
					{	fourthwall_vertices.add("v"
							+ " "
							+

							(-(NewFloorMap2DRenderer.mapfloorPoints.get(j).x / 5) + singlemapModel
									.get(i).getXMark())
							+ " "
							+ ((NewFloorMap2DRenderer.mapfloorPoints.get(j).y / 5) + singlemapModel
									.get(i).getYMark()) + " "
							+ 0 + "");
					
					fourthwall_vertices.add("v"
							+ " "
							+

							(-(NewFloorMap2DRenderer.mapfloorPoints.get(j).x / 5) + singlemapModel
									.get(i).getXMark())
							+ " "
							+ ((NewFloorMap2DRenderer.mapfloorPoints.get(j).y / 5) + singlemapModel
									.get(i).getYMark()) + " "
							+ singlemapModel
							.get(i).getZMark() + "");
					
				   }
					
					if(i==singlemapModel.size()-1)
					{	
						fourthwall_vertices.add("v"
								+ " "
								+

								(-(NewFloorMap2DRenderer.mapfloorPoints.get(j).x / 5) + singlemapModel
										.get(i).getXMark())
								+ " "
								+ ((NewFloorMap2DRenderer.mapfloorPoints.get(j).y / 5) + singlemapModel
										.get(i).getYMark()) + " "
								+ singlemapModel
								.get(i).getZMark() + "");
						
						
						fourthwall_vertices.add("v"
								+ " "
								+

								(-(NewFloorMap2DRenderer.mapfloorPoints.get(j).x / 5) + singlemapModel
										.get(i).getXMark())
								+ " "
								+ ((NewFloorMap2DRenderer.mapfloorPoints.get(j).y / 5) + singlemapModel
										.get(i).getYMark()) + " "
								+ 0 + "");
					
					
					
					}
					
					vertices.add("v"
							+ " "
							+

							(-(NewFloorMap2DRenderer.mapfloorPoints.get(j).x / 5) + singlemapModel
									.get(i).getXMark())
							+ " "
							+ ((NewFloorMap2DRenderer.mapfloorPoints.get(j).y / 5) + singlemapModel
									.get(i).getYMark()) + " "
							+ 0 + "");
					
				}

				for (int k = index_1; k < vertices.size(); k++) {
					bufferedWriter.write(vertices.get(k));
					bufferedWriter.newLine();
				}
				
				index_1 = vertices.size();
				int index_3 = index_2 - 1;
				for (int k = index_2; k < vertices.size(); k++) {
					faces.add("f" + " " + index_3 +"/1"+ " " + (k) +"/2"+ " " + (k + 1)+"/3");
				}
				index_2 = vertices.size() + 2;

			}
			
			int floor_vertices_size=vertices.size();
			
			for (int j = 0; j < NewFloorMap2DRenderer.mapfloorPoints.size(); j++) 
			{

				ArrayList<FloorModelPointsCordinate> singlemapModel = NewFloorMap.floor_model
						.get(j);

				for (int i = 0; i < singlemapModel.size(); i++) {
					
					vertices.add("v"
							+ " "
							+

							(-(NewFloorMap2DRenderer.mapfloorPoints.get(j).x / 5) + singlemapModel
									.get(i).getXMark())
							+ " "
							+ ((NewFloorMap2DRenderer.mapfloorPoints.get(j).y / 5) + singlemapModel
									.get(i).getYMark()) + " "
							+ singlemapModel.get(i).getZMark() + "");
					
					}
				}
			
			for(int i=floor_vertices_size;i<vertices.size();i++)
			{
				bufferedWriter.write(vertices.get(i));
				bufferedWriter.newLine();
			}
			
			
		
			for(int i=floor_vertices_size;i<vertices.size()-1;i++)
			{
				if(!room_vertex_count.contains(i-floor_vertices_size+1))
				faces.add("f" + " " + (i-floor_vertices_size+1) + "/1 " + (i+1) + "/2 " + (i+2)+ "/3 " + (i-floor_vertices_size+2)+"/4");
	
			}
			
			for(int i=3;i<fourthwall_vertices.size();i=i+4)
			{
				
				vertices.add(fourthwall_vertices.get(i));
				vertices.add(fourthwall_vertices.get(i-1));
				vertices.add(fourthwall_vertices.get(i-2));
				vertices.add(fourthwall_vertices.get(i-3));
				
				bufferedWriter.write(fourthwall_vertices.get(i));
				bufferedWriter.newLine();
				bufferedWriter.write(fourthwall_vertices.get(i-1));
				bufferedWriter.newLine();
				bufferedWriter.write(fourthwall_vertices.get(i-2));
				bufferedWriter.newLine();
				bufferedWriter.write(fourthwall_vertices.get(i-3));
				bufferedWriter.newLine();
				
				faces.add("f" + " " + (vertices.size()) + "/4 " + (vertices.size()-1) + "/3 " + (vertices.size()-2) + "/2 " + (vertices.size()-3)+"/1");
			}
			
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
			
				bufferedWriter.write(tex_wall.get(l));
				bufferedWriter.newLine();
			}
			
			
			bufferedWriter.newLine();
			bufferedWriter.newLine();
			bufferedWriter.write("# Faces Definition");
			bufferedWriter.newLine();
			bufferedWriter.newLine();
			
			int face_count_floor=0;
			for(int i=0;i<vertices_count.size();i++)
			{
				face_count_floor=face_count_floor+(vertices_count.get(i)-2);
			}
			
			int wall_count=0;
			int room_no=1;
			int index=0;
			int counter=vertices_count.get(index);
			int room_vertex_counter=1;
			
            
			bufferedWriter.write("g "+room_no);
			bufferedWriter.newLine();
			bufferedWriter.write("usemtl room_floor_tex");
			bufferedWriter.newLine();
			for (int l = 0; l < faces.size()-vertices_count.size(); l++) {
				if(l<face_count_floor)
				{
				bufferedWriter.write(faces.get(l));
				bufferedWriter.newLine();
				}
				else
				{
					if(room_vertex_counter<=(counter-1))
					{
						wall_count++;
						room_vertex_counter++;
					}
					else
					{
						
						if(index<vertices_count.size()-1)
						{
						
						wall_count=1;
						room_no++;
						room_vertex_counter=1;	
						index=index+1;
						counter=vertices_count.get(index)-1;
						}
					}
					bufferedWriter.write("g "+room_no);
					bufferedWriter.newLine();
					bufferedWriter.write("usemtl "+room_no+"room_wall"+wall_count);
					bufferedWriter.newLine();
					bufferedWriter.write(faces.get(l));
					bufferedWriter.newLine();
					
				}
			}
			for(int l=faces.size()-vertices_count.size(),j=0;l<faces.size();l++,j++)
			{
				bufferedWriter.write("g "+(j+1));
				bufferedWriter.newLine();
				bufferedWriter.write("usemtl "+(j+1)+"room_wall"+vertices_count.get(j));
				bufferedWriter.newLine();
				bufferedWriter.write(faces.get(l));
				bufferedWriter.newLine();
				
			}
			
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BrwoseDeleteZipUtilities utlis = new BrwoseDeleteZipUtilities(directory, NewFloorMap.floor_name_to_save, false);
		//Browse to OBJModel Directory and ZIP the contents into single File
		utlis.browseTo(directory);
		utlis.zip();
		
		//Delete singular files after ZIPPING
		utlis.delete();
	}
}
