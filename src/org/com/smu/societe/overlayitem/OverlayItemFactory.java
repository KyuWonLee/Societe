package org.com.smu.societe.overlayitem;

import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;

/** 
 * Fatory pattern 을 위한 클래스
 * 어떤 클래스인지 신경쓰지 않고 구현할 수 있도록 함
 * 
 * @Project  Societe
 * @File  OverlayItemFactory.java
 * @Date  2011-05-02
 * @author ACE
 *
 */
public class OverlayItemFactory{
	/**
	 * 어떤 클래스 인스턴스를 생성할 것인지 결정해주는 클래스
	 * 
	 * @param name			클래스 종류 구분
	 * @param d				오버레이될 이미지
	 * @param context		컨텍스트
	 * @return	instance	생성된 객체
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