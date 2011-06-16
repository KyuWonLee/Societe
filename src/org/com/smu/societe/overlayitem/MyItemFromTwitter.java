package org.com.smu.societe.overlayitem;

import org.com.smu.societe.data.SingletonBase;
import org.com.smu.societe.data.SingletonFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyItemFromTwitter extends MyOverlayItem{
	private Context context;
	
	public MyItemFromTwitter(Drawable defaultMarker, MapView mapview) {
		super(defaultMarker, mapview);
		this.context = mapview.getContext();
	}

	@Override
	public void addAllMarkers() {
		SingletonBase json = SingletonFactory.create("Twitter");
	    
		for(int i=0; i < json.markers.size(); i++){
			Bitmap bit = json.markers.get(i).bit;    
			
			GeoPoint geo = new GeoPoint((int)(json.markers.get(i).mGeoLoc.getLatitude() * 1E6),
					(int)(json.markers.get(i).mGeoLoc.getLongitude() * 1E6));
			
			OverlayItem item = new OverlayItem(geo, json.markers.get(i).mText, "");
			
			this.addOverlay(item, (Drawable)(new BitmapDrawable(bit)));	//마커 추가
			
		}
	}

	@Override
	public boolean onBalloonTap(int index) {
		return true;
	}
	
}