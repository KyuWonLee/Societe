package org.com.smu.societe.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Building Table 에 있는 XML 파싱해서 리스트로 반환해주기
 * 위한 클래스임
 * 
 * @Project  Societe
 * @File  PullParsing.java
 * @Date  2011-05-03
 * @author ACE
 */
public class PullParsing{
	
	private static int index = -1;
	
	/**
	 * 리스트로 반환해주는 static 클래스
	 * 
	 * @return	ArrayList				
	 * @throws MalformedURLException
	 */
	public static ArrayList<BuildVO> getBuildingDataList(){
		ArrayList<BuildVO> buildList = new ArrayList<BuildVO>();
		
		URL url = null;
		try {
					
			url = new URL("http://210.118.74.133:8080/TestDB/SelectBuildingServlet.xml");
				
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
				
		boolean isitem = false;
		boolean isMarker = false;
		boolean isLatitude = false;
		boolean isLongitude = false;
		boolean isAltitude = false;
		boolean isPhone = false;
		boolean isPicture = false;
		boolean isComforts = false;
		boolean isDescript = false;
				
		String name = "";
		String marker = "";
		double latitude = 0.0d;
		double longitude = 0.0d;
		double altitude = 0.0d;
		String phone = "";
		String picture = "";
		String comfort = "";
		String descript = "";
				
		String tag = "";
				
		try {
					
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(url.openStream(), null);
					
					
			int eventType = parser.getEventType();
					
			while (eventType != XmlPullParser.END_DOCUMENT) {
						
				switch (eventType) {
						
				case XmlPullParser.START_DOCUMENT:
					break;
							
				case XmlPullParser.END_DOCUMENT:
					break;
							
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if (tag.equals("name")) {
						isitem = true;
					} else if(tag.equals("marker")){
						isMarker = true;
					} else if(tag.equals("latitude")){
						isLatitude = true;
					} else if(tag.equals("longitude")){
						isLongitude = true;
					} else if(tag.equals("Altitude")){
						isAltitude = true;
					} else if(tag.equals("phone")){
						isPhone = true;
					} else if(tag.equals("picture")){
						isPicture = true;
					} else if(tag.equals("comfort")){
						isComforts = true;
					} else if(tag.equals("descript")){
						isDescript = true;
					} else if(tag.equals("build")){
						index++;
					} 
					break;
							
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if(tag.equals("build")){
						buildList.add(new BuildVO(name , marker , 
									latitude ,longitude ,altitude,
									phone, picture, comfort, descript));
					}
					break;
							
				case XmlPullParser.TEXT:
					if (isitem) {
						name = parser.getText();
						isitem = false;
					} else if (isMarker) {
						marker = parser.getText();
						isMarker = false;
					} else if(isLatitude){
						latitude = Double.parseDouble(parser.getText());
						isLatitude = false;
					} else if(isLongitude){
						longitude = Double.parseDouble(parser.getText());
						isLongitude = false;
					} else if(isAltitude){
						altitude = Double.parseDouble(parser.getText());
						isAltitude = false;
					} else if(isPhone){
						phone = parser.getText();
						isPhone = false;
					} else if(isPicture){
						picture = parser.getText();
						isPicture = false;
					} else if(isComforts){
						comfort = parser.getText();
						isComforts = false;
					} else if(isDescript){
						descript = parser.getText();
						isDescript = false;
					} 
					break;
							
				}
						
				eventType = parser.next();
						
			}
					
		}
		catch (Exception e) {
			e.printStackTrace();
		}			

		return buildList;
		
	}
	
	/**
	 * 리스트로 반환해주는 static 클래스
	 * 
	 * @return	ArrayList				
	 * @throws MalformedURLException
	 */
	public static ArrayList<BusStopVO> getBusStopDataList(){
		ArrayList<BusStopVO> busStopList = new ArrayList<BusStopVO>();
						
		URL url = null;
		
		try {
					
			url = new URL("http://210.118.74.133:8080/TestDB/SelectBusStopServlet.xml");
				
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
				
		boolean isname = false;
		boolean isLatitude = false;
		boolean isLongitude = false;
				
		String name = "";
		double latitude = 0.0d;
		double longitude = 0.0d;
				
		String tag = "";
				
		try {
					
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(url.openStream(), null);			
					
			int eventType = parser.getEventType();
					
			while (eventType != XmlPullParser.END_DOCUMENT) {
						
				switch (eventType) {
						
				case XmlPullParser.START_DOCUMENT:
					break;
							
				case XmlPullParser.END_DOCUMENT:
					break;
							
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if (tag.equals("name")) {
						isname = true;
					} else if(tag.equals("latitude")){
						isLatitude = true;
					} else if(tag.equals("longitude")){
						isLongitude = true;
					} else if(tag.equals("bustop")){
						index++;
					} 
					break;
							
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if(tag.equals("bustop")){
						busStopList.add(new BusStopVO(name , latitude ,longitude ));
					}
					break;
							
				case XmlPullParser.TEXT:
					if (isname) {
						name = parser.getText();
						isname = false;
					} else if(isLatitude){
						latitude = Double.parseDouble(parser.getText());
						isLatitude = false;
					} else if(isLongitude){
						longitude = Double.parseDouble(parser.getText());
						isLongitude = false;
					} 
					break;
					
				}
						
				eventType = parser.next();
						
			}
					
		}
		catch (Exception e) {
			e.printStackTrace();
		}			
		
		return busStopList;
		
	}
	
}