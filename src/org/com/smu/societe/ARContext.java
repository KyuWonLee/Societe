package org.com.smu.societe;

import java.util.Date;

import org.com.smu.societe.render.Matrix;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
/**
*
* 현재 위치와 기울어짐, 회전행렬을 얻을 수 있는 클래스임
* GPS의 이용여부와 실제위치인지 확인할 수 있다.  
*
* @Project  Societe
* @File  	ARContext.java
* @Date  	2010-10-10
* @Author  	ACE
*
*/
public class ARContext {
	public ARView mixView;
	Context context;
	Location curLoc;
	Matrix rotationM = new Matrix();

	float declination = 0f;
	private boolean actualLocation=false;

	public ARContext(Context appCtx) {
		this.mixView = (ARView) appCtx;
		this.context = appCtx.getApplicationContext();

		rotationM.toIdentity();

		try {
			LocationManager locationMgr = (LocationManager) appCtx.getSystemService(Context.LOCATION_SERVICE);
			Location lastFix = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			
			Date date = new Date();
			long actualTime= date.getTime();
			long lastFixTime = lastFix.getTime();
			long timeDifference = actualTime-lastFixTime;	//현재 시간과 마지막으로 업데이트된 위치정보의
															//시간을 구하여 
			
			Date lastFixDate = new Date(lastFixTime);
			
			ARTextViews.GPS_LONGITUDE = lastFix.getLongitude();
			ARTextViews.GPS_LATITUDE = lastFix.getLatitude();
			ARTextViews.GPS_ACURRACY = lastFix.getAccuracy();
			ARTextViews.GPS_SPEED = lastFix.getSpeed();
			ARTextViews.GPS_ALTITUDE = lastFix.getAltitude();
			ARTextViews.GPS_LAST_FIX = lastFixDate.toString();
			ARTextViews.GPS_ALL = lastFix.toString();
			
			if(timeDifference > 60000){//300000 milliseconds = 5 min
				actualLocation=false;
			}
			actualLocation=true;
			if (lastFix == null){
				lastFix = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				actualLocation = false;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	  * GPS사용여부 확인
	  *
	  * @return boolean data. GPS가 사용가능한지 여부
	  */
	public boolean isGpsEnabled() {
		return mixView.isGpsEnabled;
	}
	
	/**
	  * 실제 위치인지 확인
	  *
	  * @return boolean data. 실제 위치인지 여부
	  */
	public boolean isActualLocation(){
		return actualLocation;
	}

	/**
	  * 회전 행렬을 세팅하는 함수
	  *
	  * @param dest	 Rotation matrix
	  */
	public void getRM(Matrix dest) {
		synchronized (rotationM) {
			dest.set(rotationM);
		}
	}

	/**
	  * 현재 위치를 리턴하는 함수
	  *
	  * @return Current location
	  */
	public Location getCurrentLocation() {
		synchronized (curLoc) {
			return curLoc;
		}
	}
}