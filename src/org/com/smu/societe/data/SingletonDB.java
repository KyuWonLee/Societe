
package org.com.smu.societe.data;

import java.util.ArrayList;

import org.com.smu.societe.Marker;
import org.com.smu.societe.reality.RealLocation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/**
 * �� �ǹ����� ������ �����ϴ� ��ü- DB���� �����͸� 
 * �����ͼ� Marker ��ü�� �Ҵ�
 * Singleton ���� ������! DB���� ��ü�� �ϳ��� �����Ǿ�� ��!
 * 
 * @Project  Societe
 * @File  SingletonJson.java
 * @Date  2010-11-25
 * @author ACE
 *
 */
public class SingletonDB extends SingletonBase{  
	private static SingletonBase jsonInstance;	// �ڽ� ��ü�� static���� �����
												// �ϳ��� �����ǵ��� ��
	//public ArrayList<Marker> markers = new ArrayList<Marker>();
	//private static boolean isComplete = false;
	
	/**
	 * new �����ڸ� ���� �ν��Ͻ��� ������ �� ������ ��!
	 */
	private SingletonDB() {
		super.createInstance();
	}
	
	/**
	 * �ν��Ͻ� �����Լ���, ���� �ν��Ͻ��� �������� ���� ���¶��
	 * �ν��Ͻ��� �����ϰ�, �����ͺ��̽����� ������ �����´�.
	 * ��������� ������ ���� �ν��Ͻ��� �����Ѵ�.
	 * 
	 * @return	JsonInstance	
	 */
	public static SingletonBase createInstance(){
		if(jsonInstance == null){
			jsonInstance = new SingletonDB();
			
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
	
	/**
	 * Server DB�κ��� Data�� �������� �Լ� 
	 */
	public static void getDataFromServerDB(){
		
		new Thread(){
			public void run(){
				
				ArrayList<BuildVO> buildList = PullParsing.getBuildingDataList();
				
				for(int i=0; i < buildList.size(); i++){
					BuildVO build = (BuildVO)buildList.get(i);
					
					/* Marker ���� */
					createMarker(i, build.getName(), build.getMarker(), build.getLatitude(), 
								 build.getLongitude(), build.getAltitude(), build.getPhone(), 
								 build.getBuild_picture(), build.getComforts(), build.getDescription());
					
				}
				
				isComplete = true;
				
			}
		}.start();
		
	}

	/**
	 * Marker�� �����Ѵ�.
	 * 
	 * @param title			��ü�� �̸�
	 * @param nickname		��ü�� �г���
	 * @param latitude		��ü�� ����
	 * @param longitude		��ü�� �浵
	 * @param elevation		��ü�� ��
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