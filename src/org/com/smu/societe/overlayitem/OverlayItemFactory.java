package org.com.smu.societe.overlayitem;

import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;

/** 
 * Fatory pattern �� ���� Ŭ����
 * � Ŭ�������� �Ű澲�� �ʰ� ������ �� �ֵ��� ��
 * 
 * @Project  Societe
 * @File  OverlayItemFactory.java
 * @Date  2011-05-02
 * @author ACE
 *
 */
public class OverlayItemFactory{
	/**
	 * � Ŭ���� �ν��Ͻ��� ������ ������ �������ִ� Ŭ����
	 * 
	 * @param name			Ŭ���� ���� ����
	 * @param d				�������̵� �̹���
	 * @param context		���ؽ�Ʈ
	 * @return	instance	������ ��ü
	 */
	public static MyOverlayItem create(String name,
			Drawable d, MapView mapView){
		
		if(name == null){
			throw new IllegalArgumentException("Error");
		}
		
		if(name.equals("FoodStore")){
			return new MyItemFromFoodStore(d, mapView);
		} else if(name.equals("ARView")){
			return new MyItemFromARView(d, mapView);
		} else if(name.equals("Twitter")){
			return new MyItemFromTwitter(d, mapView);	
		} else if(name.equals("BusView")){
			return new MyItemFromBusView(d, mapView);	
		} else{
			return null;
		}
	}	
	
}