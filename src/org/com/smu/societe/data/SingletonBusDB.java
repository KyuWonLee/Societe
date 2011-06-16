package org.com.smu.societe.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.com.smu.societe.MainSelectView;
import org.com.smu.societe.Marker;
import org.com.smu.societe.reality.RealLocation;

/**
 * 
 * @author YUNMINHO
 *
 */
public class SingletonBusDB extends SingletonBase{
    
	private static SingletonBusDB jsonInstance;	// 자신 객체를 static으로 만들어
												// 하나만 생성되도록 함
	private static HashMap<String, Integer> busStopHash = 
										new HashMap<String, Integer>();
	
	public ArrayList<Marker> markers = new ArrayList<Marker>();
	private static boolean isComplete = false;
	
	/**
	 * new 연산자를 통해 인스턴스를 생성할 수 없도록 함!
	 */
	private SingletonBusDB() {
		super.createInstance();
	}
	
	/**
	 * 인스턴스 생성함수로, 현재 인스턴스를 생성하지 않은 상태라면
	 * 인스턴스를 생성하고, 데이터베이스에서 정보를 가져온다.
	 * 멤버변수로 선언한 단일 인스턴스를 리턴한다.
	 * 
	 * @return	JsonInstance	
	 */
	public static SingletonBusDB createInstance(){
		if(jsonInstance == null){
		
			jsonInstance = new SingletonBusDB();
			
			// 서버 DB로부터 데이터를 가져오는 함수
			getDataFromServerDB();
			
		}
		
		return jsonInstance;	
		
	}
	
	/**
	 * 
	 * @return boolean 데이터 로딩 완료 여부
	 */
	public boolean isCompleteLoading(){
		return isComplete; 
	}
	
	public HashMap<String, Integer> getBusStopHash(){
		return busStopHash;
	}
	
	/**
	 * Server DB로부터 Data를 가져오는 함수 
	 */
	public static void getDataFromServerDB(){
		
		new Thread(){
			public void run(){
				
				ArrayList<BusStopVO> buildList = PullParsing.getBusStopDataList();
				
				for(int i=0; i < buildList.size(); i++){
					BusStopVO build = (BusStopVO)buildList.get(i);
					
					/* Marker 생성 */
					createMarker(i, build.getName(), build.getLatitude(), build.getLongitude());
					
					busStopHash.put(build.getName(), i);
				}
				
				isComplete = true;
				
			}
		}.start();
		
	}

	/**
	 * 
	 * 
	 * @param id
	 * @param title
	 * @param latitude
	 * @param longitude
	 */
	private static void createMarker(final int id, final String title, 
						final double latitude, final double longitude) {
		
			RealLocation refpt = new RealLocation();
			Marker ma = new Marker();	
		    
			ma.objectId = id;
			ma.mText = title;
			refpt.setLatitude(latitude);
			refpt.setLongitude(longitude);
			refpt.setAltitude(0);
			ma.mGeoLoc.setTo(refpt);

			if(id == 52){
				ma.bit = MainSelectView.greenBit;
			} else {
				ma.bit = MainSelectView.bit;
			}
			
			jsonInstance.markers.add(ma);	
		
	}
	
}