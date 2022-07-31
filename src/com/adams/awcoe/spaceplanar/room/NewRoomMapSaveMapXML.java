package com.adams.awcoe.spaceplanar.room;

import java.io.File;
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


public class NewRoomMapSaveMapXML {

	
	public void saveMap(String mapName,String ROOM_HEIGHT,String MAP_LOCATION,String AREA_MAP,String NO_WALLS_MAP,String location)
	{
		try {
			 
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("MAP");
			doc.appendChild(rootElement);
	 
			// point data elements
			Element pointdata= doc.createElement("Data");
			rootElement.appendChild(pointdata);
	 			
			Attr attr_mapname = doc.createAttribute("MID");
			attr_mapname.setValue(mapName);
			pointdata.setAttributeNode(attr_mapname);
			
			Attr attr_loc = doc.createAttribute("LOC");
			attr_loc.setValue(MAP_LOCATION);
			pointdata.setAttributeNode(attr_loc);
			
			Attr attr_area = doc.createAttribute("AREA");
			attr_area.setValue(AREA_MAP);
			pointdata.setAttributeNode(attr_area);
			
			Attr attr_no_walls = doc.createAttribute("NO_WALLS_MAP");
			attr_no_walls.setValue(NO_WALLS_MAP);
			pointdata.setAttributeNode(attr_no_walls);
			
			Attr attr_roomHeight = doc.createAttribute("ROOM_HEIGHT");
			attr_roomHeight.setValue(ROOM_HEIGHT);
			pointdata.setAttributeNode(attr_roomHeight);
	 			
			for(int i=0;i<NewRoomMap.points3dCordinates_augmented.size();i++)
			{
			
				Element point=doc.createElement("FlagPoint");
				pointdata.appendChild(point);
				
				Element Xpoint = doc.createElement("XCord3D");
				Xpoint.appendChild(doc.createTextNode(NewRoomMap.points3dCordinates_augmented.get(i).getXMark()+""));
				point.appendChild(Xpoint);
				
				Element Ypoint = doc.createElement("YCord3D");
				Ypoint.appendChild(doc.createTextNode(NewRoomMap.points3dCordinates_augmented.get(i).getYMark()+""));
				point.appendChild(Ypoint);
				
				Element Zpoint = doc.createElement("ZCord3D");
				Zpoint.appendChild(doc.createTextNode(ROOM_HEIGHT+""));
				point.appendChild(Zpoint);
				
				Element Texture_BASE64 = doc.createElement("Texture_BASE64");
				Texture_BASE64.appendChild(doc.createTextNode(NewRoomMap2D.wall_textures_content_BASE64.get(i)));
				point.appendChild(Texture_BASE64);

			}
	
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(
					 location, mapName+".xml"));
	 
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
	 
			transformer.transform(source, result);
	 
		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }
	}
	
	
}
