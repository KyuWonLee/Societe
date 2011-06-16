package org.com.smu.societe.overlayitem;

import java.util.ArrayList;

import org.com.smu.societe.BuildInformView;
import org.com.smu.societe.data.SingletonBase;
import org.com.smu.societe.data.SingletonDB;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * �������� �信�� ��ü�� ��ġ���� �� �ǹ������� �����ִ� ��Ƽ��Ƽ��
 * ��ȯ�ǵ��� MyOverlayItem�� ��ӹް�, onTop �޼ҵ��� 
 * ��ü�� ������ ��
 * 
 * @Project  Societe
 * @File  MyItemFromBuildInfo.java
 * @Date  2011-05-02
 * @author ACE
 *
 */
public class MyItemFromARView extends MyOverlayItem{
	private Context context;
	
	/**
	 * �ν��Ͻ��� ����
	 * 
	 * @param defaultMarker
	 * @param context
	 */
	public MyItemFromARView(Drawable defaultMarker, MapView mapView) {
		super(defaultMarker, mapView);
		this.context = mapView.getContext();
	}

	@Override
	public void addAllMarkers() {
	    SingletonBase json = SingletonDB.createInstance();
	    
		for(int i=0; i < json.markers.size(); i++){
			Bitmap bit = json.markers.get(i).bit;    
			
			GeoPoint geo = new GeoPoint((int)(json.markers.get(i).mGeoLoc.getLatitude() * 1E6),
					(int)(json.markers.get(i).mGeoLoc.getLongitude() * 1E6));
			
			OverlayItem item = new OverlayItem(geo, json.markers.get(i).mText, "");
			
			this.addOverlay(item, (Drawable)(new BitmapDrawable(bit)));	//��Ŀ �߰�
			
		}	
		
	}

	@Override
	public boolean onBalloonTap(int index) {
		ArrayList<OverlayItem> mOverlays = getOverlayList();
		
		double[] objectLocation = new double[2];
	
        objectLocation[0] = mOverlays.get(index).getPoint().getLatitudeE6() / 1E6;
        objectLocation[1] = mOverlays.get(index).getPoint().getLatitudeE6() / 1E6;	
        
        Intent i = new Intent(context, BuildInformView.class);
		i.putExtra("Name", mOverlays.get(index).getTitle());
		i.putExtra("ARView", index + 1);
		i.putExtra("Location", objectLocation);
		context.startActivity(i);
        
		return true;
	}

}