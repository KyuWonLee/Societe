package org.com.smu.societe.overlayitem;

import org.com.smu.societe.data.SingletonBusDB;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyItemFromBusView extends MyOverlayItem{
	private Context context;

	public MyItemFromBusView(Drawable defaultMarker, MapView mapView) {
		super(defaultMarker, mapView);
		this.context = mapView.getContext();
	}

	@Override
	public void addAllMarkers() {
		SingletonBusDB dbInstance = SingletonBusDB.createInstance();
		
		for(int i=0; i < dbInstance.markers.size(); i++){
			
			GeoPoint geo = new GeoPoint(
								(int)(dbInstance.markers.get(i).mGeoLoc.getLatitude() * 1E6),
								(int)(dbInstance.markers.get(i).mGeoLoc.getLongitude() * 1E6));
			
			OverlayItem item = new OverlayItem(geo, dbInstance.markers.get(i).mText, "");
			
			this.addOverlay(item, 
					(Drawable)(new BitmapDrawable(dbInstance.markers.get(i).bit)));	//아이템 추가
			
		}
	}

	@Override
	public boolean onBalloonTap(int index) {
		Toast.makeText(context, this.createItem(index).getTitle() + "", 
				Toast.LENGTH_SHORT).show();
		return true;
	}
	
}