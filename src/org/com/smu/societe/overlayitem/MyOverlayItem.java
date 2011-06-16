package org.com.smu.societe.overlayitem;

import java.util.ArrayList;

import org.com.smu.societe.mapballoon.BalloonItemizedOverlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * �������� ��ü���� �������� ���ִ� Ŭ����
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
    	super.draw(canvas, mapView, false);		//�׸��� ����
    }

    @Override
    public abstract boolean onBalloonTap(int index);
    
    /**
     * �ǹ�, Ʈ����, ������ ���� �ٸ� �����͸� ������
     * �߰��ؾ� �ϱ� ������ �߻�޼ҵ�� ������
     */
    public abstract void addAllMarkers();
    
}