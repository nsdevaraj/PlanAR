package com.adams.awcoe.spaceplanar.floors;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.adams.awcoe.spaceplanar.floors.modeldata.FloorModelPointsCordinate;

public class ViewFloorMapXMLReader {
	public String file_location;
	public ArrayList<String> individual_room_info;
	public ArrayList<FloorModelPointsCordinate> modelpointsCordinates;
	public ViewFloorMapXMLReader(String location) {
		file_location = location;
	}
	
	@SuppressWarnings("static-access")
	public void readXML() {
		File fXmlFile = new File(file_location);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = null;
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();			
			NodeList n = doc.getElementsByTagName("Data");
			for (int t = 0; t < n.getLength(); t++) {
				Node Node = n.item(t);
				NodeList nList= Node.getChildNodes();
				modelpointsCordinates=new ArrayList<FloorModelPointsCordinate>();
				individual_room_info=new ArrayList<String>();
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						FloorModelPointsCordinate point=new FloorModelPointsCordinate();
						//X
					    point.setXMark((Float.parseFloat(getTagValue("XCord3D", eElement))));						
					    //Y
						point.setYMark((Float.parseFloat(getTagValue("YCord3D", eElement))));						
						//Z
						point.setZMark(Float.parseFloat(getTagValue("ZCord3D", eElement)));
						// Texture Data
						point.setTex_Data(getTagValue("Texture_BASE64",eElement));						
						modelpointsCordinates.add(point);
					}
					NamedNodeMap map= Node.getAttributes();
					individual_room_info.add(map.getNamedItem("MID").getNodeValue());
					individual_room_info.add(map.getNamedItem("LOC").getNodeValue());
					individual_room_info.add(map.getNamedItem("AREA").getNodeValue());
					individual_room_info.add(map.getNamedItem("NO_WALLS_MAP").getNodeValue());
					individual_room_info.add(map.getNamedItem("ROOM_HEIGHT").getNodeValue());

				}
				ViewFloorMap.floor_model.add(modelpointsCordinates);
                ViewFloorMap.all_room_info.add(individual_room_info);
			}
			
		}catch (Exception e) {

		}

	}
	
	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
				.getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}

}
