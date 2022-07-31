package com.adams.awcoe.spaceplanar.room;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.adams.awcoe.spaceplanar.room.mapdata.ReadAugmented3DPointsData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ViewRoomMapReadMapXML {
	String file_location;

	public ViewRoomMapReadMapXML(String location) {
		this.file_location = location;
		ViewRoomMap.readAugmented3DPointsCordinates = new ArrayList<ReadAugmented3DPointsData>();
		ViewRoomMap.readMapInfo = new ArrayList<String>();
		ViewRoomMap.textures_Base64_to_Image=new ArrayList<Bitmap>();		
	}

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
	
	public void readMap() {
		File fXmlFile = new File(file_location);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = null;
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();			
			NodeList nList = doc.getElementsByTagName("FlagPoint");			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					ReadAugmented3DPointsData point=new ReadAugmented3DPointsData();					
					//X 
				    point.setXMark(Float.parseFloat(getTagValue("XCord3D", eElement)));					
				    //Y					
					point.setYMark(Float.parseFloat(getTagValue("YCord3D", eElement)));					
					//Z					
					point.setZMark(Float.parseFloat(getTagValue("ZCord3D", eElement)));					
					String BASE64_data=getTagValue("Texture_BASE64",eElement);					
					ViewRoomMap.readAugmented3DPointsCordinates.add(point);
					ViewRoomMap.textures_Base64_to_Image.add(StringToBitMap(BASE64_data));
				}
			}
			
			
			// Getting Map Information - Map Name, Location, Area, No of Walls
			NodeList InfoElement = doc.getElementsByTagName("Data");
			Node infoNode = InfoElement.item(0);	
			NamedNodeMap map= infoNode.getAttributes();
			ViewRoomMap.readMapInfo.add(map.getNamedItem("MID").getNodeValue());
			ViewRoomMap.readMapInfo.add(map.getNamedItem("LOC").getNodeValue());
			ViewRoomMap.readMapInfo.add(map.getNamedItem("AREA").getNodeValue());
			ViewRoomMap.readMapInfo.add(map.getNamedItem("NO_WALLS_MAP").getNodeValue());
			ViewRoomMap.readMapInfo.add(map.getNamedItem("ROOM_HEIGHT").getNodeValue());

		} catch (Exception e) {

		}
	}

	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
				.getChildNodes();

		Node nValue = (Node) nlList.item(0);

		return nValue.getNodeValue();
	}
}
