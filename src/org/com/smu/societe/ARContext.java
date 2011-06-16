package org.com.smu.societe;

import java.util.Date;

import org.com.smu.societe.render.Matrix;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
/**
*
* ���� ��ġ�� ������, ȸ������� ���� �� �ִ� Ŭ������
* GPS�� �̿뿩�ο� ������ġ���� Ȯ���� �� �ִ�.  
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
			long timeDifference = actualTime-lastFixTime;	//���� �ð��� ���������� ������Ʈ�� ��ġ������
															//�ð��� ���Ͽ� 
			
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
	  * GPS��뿩�� Ȯ��
	  *
	  * @return boolean data. GPS�� ��밡������ ����
	  */
	public boolean isGpsEnabled() {
		return mixView.isGpsEnabled;
	}
	
	/**
	  * ���� ��ġ���� Ȯ��
	  *
	  * @return boolean data. ���� ��ġ���� ����
	  */
	public boolean isActualLocation(){
		return actualLocation;
	}

	/**
	  * ȸ�� ����� �����ϴ� �Լ�
	  *
	  * @param dest	 Rotation matrix
	  */
	public void getRM(Matrix dest) {
		synchronized (rotationM) {
			dest.set(rotationM);
		}
	}

	/**
	  * ���� ��ġ�� �����ϴ� �Լ�
	  *
	  * @return Current location
	  */
	public Location getCurrentLocation() {
		synchronized (curLoc) {
			return curLoc;
		}
	}
}