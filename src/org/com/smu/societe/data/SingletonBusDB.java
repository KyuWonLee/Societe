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
    
	private static SingletonBusDB jsonInstance;	// �ڽ� ��ü�� static���� �����
												// �ϳ��� �����ǵ��� ��
	private static HashMap<String, Integer> busStopHash = 
										new HashMap<String, Integer>();
	
	public ArrayList<Marker> markers = new ArrayList<Marker>();
	private static boolean isComplete = false;
	
	/**
	 * new �����ڸ� ���� �ν��Ͻ��� ������ �� ������ ��!
	 */
	private SingletonBusDB() {
		super.createInstance();
	}
	
	/**
	 * �ν��Ͻ� �����Լ���, ���� �ν��Ͻ��� �������� ���� ���¶��
	 * �ν��Ͻ��� �����ϰ�, �����ͺ��̽����� ������ �����´�.
	 * ��������� ������ ���� �ν��Ͻ��� �����Ѵ�.
	 * 
	 * @return	JsonInstance	
	 */
	public static SingletonBusDB createInstance(){
		if(jsonInstance == null){
		
			jsonInstance = new SingletonBusDB();
			
			// ���� DB�κ��� �����͸� �������� �Լ�
			getDataFromServerDB();
			
		}
		
		return jsonInstance;	
		
	}
	
	/**
	 * 
	 * @return boolean ������ �ε� �Ϸ� ����
	 */
	public boolean isCompleteLoading(){
		return isComplete; 
	}
	
	public HashMap<String, Integer> getBusStopHash(){
		return busStopHash;
	}
	
	/**
	 * Server DB�κ��� Data�� �������� �Լ� 
	 */
	public static void getDataFromServerDB(){
		
		new Thread(){
			public void run(){
				
				ArrayList<BusStopVO> buildList = PullParsing.getBusStopDataList();
				
				for(int i=0; i < buildList.size(); i++){
					BusStopVO build = (BusStopVO)buildList.get(i);
					
					/* Marker ���� */
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