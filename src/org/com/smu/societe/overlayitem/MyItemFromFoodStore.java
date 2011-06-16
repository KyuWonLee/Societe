package org.com.smu.societe.overlayitem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.MapView;

/**
 * 건물정보뷰에서 객체를 터치했을 때의 토스트를 띄어주도록
 * 하는 클래스. MyOvelayItem클래스를 상속받아서 onTap메소드의
 * 실체를 구현해줌
 * 
 * @Project  Societe
 * @File  MyItemFromBuildInfo.java
 * @Date  2011-05-02
 * @author ACE
 *
 */
public class MyItemFromFoodStore extends MyOverlayItem{

	private Context context;
	/**
	 * 인스턴스를 생성
	 * 
	 * @param defaultMarker
	 * @param context
	 */
	public MyItemFromFoodStore(Drawable defaultMarker, MapView mapView) {
		super(defaultMarker, mapView);

		this.context = mapView.getContext();
	}

	@Override
	public void addAllMarkers() {
		
	}

	@Override
	public boolean onBalloonTap(int index) {
		Toast.makeText(context, "...", Toast.LENGTH_LONG).show();
		return false;
	}

}