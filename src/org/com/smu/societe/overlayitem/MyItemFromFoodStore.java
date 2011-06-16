package org.com.smu.societe.overlayitem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.MapView;

/**
 * �ǹ������信�� ��ü�� ��ġ���� ���� �佺Ʈ�� ����ֵ���
 * �ϴ� Ŭ����. MyOvelayItemŬ������ ��ӹ޾Ƽ� onTap�޼ҵ���
 * ��ü�� ��������
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
	 * �ν��Ͻ��� ����
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