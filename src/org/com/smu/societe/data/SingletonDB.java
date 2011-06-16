
package org.com.smu.societe.data;

import java.util.ArrayList;

import org.com.smu.societe.Marker;
import org.com.smu.societe.reality.RealLocation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/**
 * 각 건물들의 정보를 저장하는 객체- DB에서 데이터를 
 * 가져와서 Marker 객체에 할당
 * Singleton 패턴 적용함! DB관련 객체는 하나만 생성되어야 함!
 * 
 * @Project  Societe
 * @File  SingletonJson.java
 * @Date  2010-11-25
 * @author ACE
 *
 */
public class SingletonDB extends SingletonBase{  
	private static SingletonBase jsonInstance;	// 자신 객체를 static으로 만들어
												// 하나만 생성되도록 함
	//public ArrayList<Marker> markers = new ArrayList<Marker>();
	//private static boolean isComplete = false;
	
	/**
	 * new 연산자를 통해 인스턴스를 생성할 수 없도록 함!
	 */
	private SingletonDB() {
		super.createInstance();
	}
	
	/**
	 * 인스턴스 생성함수로, 현재 인스턴스를 생성하지 않은 상태라면
	 * 인스턴스를 생성하고, 데이터베이스에서 정보를 가져온다.
	 * 멤버변수로 선언한 단일 인스턴스를 리턴한다.
	 * 
	 * @return	JsonInstance	
	 */
	public static SingletonBase createInstance(){
		if(jsonInstance == null){
			jsonInstance = new SingletonDB();
			
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
	
	/**
	 * Server DB로부터 Data를 가져오는 함수 
	 */
	public static void getDataFromServerDB(){
		
		new Thread(){
			public void run(){
				
				ArrayList<BuildVO> buildList = PullParsing.getBuildingDataList();
				
				for(int i=0; i < buildList.size(); i++){
					BuildVO build = (BuildVO)buildList.get(i);
					
					/* Marker 생성 */
					createMarker(i, build.getName(), build.getMarker(), build.getLatitude(), 
								 build.getLongitude(), build.getAltitude(), build.getPhone(), 
								 build.getBuild_picture(), build.getComforts(), build.getDescription());
					
				}
				
				isComplete = true;
				
			}
		}.start();
		
	}

	/**
	 * Marker를 생성한다.
	 * 
	 * @param title			객체의 이름
	 * @param nickname		객체의 닉네임
	 * @param latitude		객체의 위도
	 * @param longitude		객체의 경도
	 * @param elevation		객체의 고도
	 */
	private static void createMarker(final int id, final String title, final String nickname ,final double latitude,
			final double longitude, final double elevation, String phone, String picture, String comforts, String descript) {
		
			RealLocation refpt = new RealLocation();
			Marker ma = new Marker();	
				
			Log.d("BITMAPPPPPPPPPP", nickname);
				
			Bitmap bit = BitmapFactory.decodeFile(
					Environment.getExternalStorageDirectory().getPath() + "/ARview/" + nickname + ".png");
				
			Log.d("BITMAPPPPPPPPPP", bit.toString());
				
			if(bit != null){
				bit = Bitmap.createScaledBitmap(bit, bit.getWidth() + 30, bit.getHeight() + 30, false);		
			}
				
			ma.objectId = id;
			ma.mText = title;
			refpt.setLatitude(latitude);
			refpt.setLongitude(longitude);
			refpt.setAltitude(elevation);
			ma.mGeoLoc.setTo(refpt);
			ma.bit = bit;
			
			ma.phone = phone;
			ma.build_picture = picture;
			ma.comforts = comforts;
			ma.descript = descript;
				
			jsonInstance.markers.add(ma);
		
		
	}
	
	
}