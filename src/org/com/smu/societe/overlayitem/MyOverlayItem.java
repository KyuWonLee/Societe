package org.com.smu.societe.overlayitem;

import java.util.ArrayList;

import org.com.smu.societe.mapballoon.BalloonItemizedOverlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * 지도위에 객체들을 오버레이 해주는 클래스
 * 
 * @Project  Societe
 * @File  MyItemFromBuildInfo.java
 * @Date  2011-05-02
 * @author ACE
 *
 */
public abstract class MyOverlayItem extends 
					BalloonItemizedOverlay<OverlayItem> {

    private ArrayList<OverlayItem> mOverlays ;
    private Context context;
    
    public MyOverlayItem(Drawable defaultMarker, MapView mapview) {
        super(boundCenter(defaultMarker), mapview);

        this.mOverlays = new ArrayList<OverlayItem>();  
        this.context = mapview.getContext();
    }
    
    public ArrayList<OverlayItem> getOverlayList(){
    	return mOverlays;
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }

    protected void setMarkerDrawable(int index, Drawable drawable){
    	mOverlays.get(index).setMarker(boundCenter(drawable));
    	populate();
    }
   
    protected void addOverlay(OverlayItem overlay){
        mOverlays.add(overlay);
        populate();
    }
    
    protected void addOverlay(OverlayItem overlay, Drawable drawable){
    	if(drawable!=null)
    		overlay.setMarker(boundCenter(drawable));
    	addOverlay(overlay);
    }
    
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow){
    	super.draw(canvas, mapView, false);		//그림자 없앰
    }

    @Override
    public abstract boolean onBalloonTap(int index);
    
    /**
     * 건물, 트위터, 상점에 따라서 다른 데이터를 가져와
     * 추가해야 하기 때문에 추상메소드로 선언함
     */
    public abstract void addAllMarkers();
    
}