package org.com.smu.societe;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.com.smu.societe.bus.BusAdapter;
import org.com.smu.societe.bus.BusInfo;
import org.com.smu.societe.data.BusStopVO;
import org.com.smu.societe.data.SingletonBusDB;
import org.com.smu.societe.networkstate.NetworkState;
import org.com.smu.societe.overlayitem.MyOverlayItem;
import org.com.smu.societe.overlayitem.OverlayItemFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

/**
 * 
 * 지도에 건물의 위치와 자신의 위치를 보여주기 위한 클래스
 * 
 * @Project Societe
 * @File 	MapOverlay.java
 * @Date 	2011-05-01
 * @author 	ACE
 *
 */
public class MapOverlay extends MapActivity implements LocationListener{
    private MapView map;
    private MapController controller;
    private MyLocationOverlay overlay;
    private LocationManager mLocationManager;
    private String provider;
    private FrameLayout viewSelectFrame;
    private FrameLayout refreshFrame;
    private MyOverlayItem overlayItem; 
   	private ListView list;
   	private ArrayList<BusInfo> arrayList;
   	private BusAdapter busAdapter;	
   	private ProgressBar progress;
   	private ImageButton refreshBtn;
   	private ConnectivityManager connectMgr;
   	private boolean isExit = false;
   	private String MapTag;
   
   	private final String busURL = "http://kimtree.net/dev/busapi/station.php?id=01278"; 
	
   	@Override
   	public void onCreate(Bundle savedInstanceState){
   		super.onCreate(savedInstanceState);
	    setContentView(R.layout.map); 
	    
	    map = (MapView)findViewById(R.id.mapView);	  
	    
	    /* Location Provider 를 선택해주는 Criteria 생성 */
	    Criteria c = new Criteria();
	    c.setAccuracy(Criteria.NO_REQUIREMENT);
		c.setCostAllowed(true);
		c.setBearingRequired(false);
		c.setAltitudeRequired(false);    
	    
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		provider = mLocationManager.getBestProvider(c, true);
		
		connectMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		/* MAPTAG를 받아옴 */
		Intent intent = getIntent();
		MapTag = intent.getStringExtra("MAPTAG");
		
		/* Initialize mapview */
	    initMapView(MapTag);
	    
	    
	    if(MapTag.equals("BusView")){		/* BusView */
	    	
			LayoutInflater inflater = (LayoutInflater)getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			viewSelectFrame = (FrameLayout)inflater.inflate(R.layout.bus_frame, null);
			viewSelectFrame.setPadding(0, 0, 12, 12);
			
	        list = (ListView)viewSelectFrame.findViewById(R.id.busListFrame);
	        progress = (ProgressBar)viewSelectFrame.findViewById(R.id.progress_small);
	        
	        addContentView(viewSelectFrame, new FrameLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
	        
	        /* Refresh Frame */
	        refreshFrame = (FrameLayout)inflater.inflate(R.layout.bus_refresh_btn, null);
			refreshFrame.setPadding(0, 12, 12, 0);
			
			/* Refresh Button */
			refreshBtn = (ImageButton)refreshFrame.findViewById(R.id.refreshBtn);
			refreshBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					/* Download bus information */
					DownloadBusInfoThread(MapTag);
				}
			});
			
			/* Add Refresh Frame */
			addContentView(refreshFrame, new FrameLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, Gravity.TOP));
			
	        arrayList = new ArrayList<BusInfo>();
  
	        /* Download bus information */
	        DownloadBusInfoThread(MapTag);      
	        
	    } else{
	    	//모든 마커를 추가
	    	addAllMarkers(MapTag);		
	    }
	    
   	}
   
   	/**
   	 * 모든 객체를 지도위에 오버레이해주기 위해 각각의 객체를 추가해주는 함수
   	 * 
   	 */
   	private void addAllMarkers(final String MapTag){
   		runOnUiThread(new Runnable(){
   			public void run(){		      
   				/* default marker image */
			    Bitmap bit = BitmapFactory.decodeFile("/sdcard/ARview/A.png");
			    Drawable d = (Drawable)(new BitmapDrawable(bit));
			    
			    /* Factory pattern 적용. 정확히 어떤 클래스인지 신경쓰지 않고 구현할 수 있기 때문에
			              훨씬 유연해짐!! */
			    overlayItem = OverlayItemFactory.create(MapTag, d, map);
			    
			    /* Add all markers */
			    overlayItem.addAllMarkers();
			    
			    map.getOverlays().add(overlayItem);
				
				map.invalidate();
   			}
   		});
	   	
   	}
   
   	/** 
   	 * Map view 를 초기화하는 함수
   	 * 
   	 */
   	private void initMapView(final String MapTag) {
		map.setSatellite(false); 			//위성사진 사용 안함
		map.setBuiltInZoomControls(true); 	//zoomControl 사용
		map.setPersistentDrawingCache(MapView.PERSISTENT_ALL_CACHES);
			      
		/* zoom 과 animatTo 는 MapController 클래스로 해야함 */
		controller = map.getController();
			    
		if(MapTag.equals("BusView")){
			controller.setZoom(14);
		} else{
			controller.setZoom(17);  
			    	
			/* MyLocationOverlay객체를 사용하여 현재 위치를 표시해주기 위함 */
			overlay = new MyLocationOverlay(MapOverlay.this, map);
			overlay.enableMyLocation();
			overlay.enableCompass();	//나침반 표시
		    overlay.runOnFirstFix(new Runnable(){
				public void run() {
					/* 현재 위치에 따라 마커 이동 */
					map.getController().animateTo(overlay.getMyLocation());
				}	
		    });
		    		
		    /* 현재 위치를 나타내는 마커 추가 */
		    map.getOverlays().add(overlay); 
		}

   	}  
   
   	/**
   	 * 리스트뷰 아이템 클릭 이벤트 리스너 
   	 */
	AdapterView.OnItemClickListener mItemClickListener = 
		new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			SingletonBusDB busDB = SingletonBusDB.createInstance();
			HashMap<String, Integer> busStopHash = busDB.getBusStopHash();
			
			overlayItem.onTap(busStopHash.get(
							arrayList.get(position).getName()));
		}
	};
   
    /**
	 * execute HTML downloading
	 */
	private void DownloadBusInfoThread(final String MapTag){
		if(NetworkState.isNetworkConnected(connectMgr)){	//Network 연결상태 확인
			/* Disabled refresh Button */
			refreshBtn.setEnabled(false);
			
			/* Remove all data */
			arrayList.removeAll(arrayList);
			
			if(busAdapter != null){			//새로고침 할 경우
				busAdapter.notifyDataSetChanged(); 
			}
			
			/* Execute Bus AcyncTask */
			new DownloadAsyncTask().execute(busURL);
			
		} else{
			Toast.makeText(this, "네트워크 상태가 좋지 않습니다. 다시 시도해 주세요.", 
					Toast.LENGTH_LONG).show();
		}
	}
   
   /** HTML parsing AsyncTask inner class */
   class DownloadAsyncTask extends AsyncTask<String, Integer, ArrayList<BusInfo>>{

   		@Override
   		protected void onPreExecute(){
   			viewSelectFrame.setVisibility(View.VISIBLE);
   			progress.setVisibility(View.VISIBLE);
   		}
   	
		@Override
		protected ArrayList<BusInfo> doInBackground(String... arg0) {
			ArrayList<BusInfo> busTimeList = getBusStopDataList();	
			
			if(busTimeList != null && busTimeList.size() == 2){	//막차 시간이 지나지 않은 경우
				return busTimeList;
			} else{
				isExit = true;	//ListView를 업데이트 하지 않음
			}
			return null;				
		}
		
		@Override
		protected void onPostExecute(ArrayList<BusInfo> busTimeList){
			progress.setVisibility(View.GONE);
			
			arrayList = busTimeList;
			
			if(!isExit && 		//막차시간이 지나지 않았을 경우만 업데이트
					arrayList != null && arrayList.size() == 2){	
				
				/* Change marker */
				Bitmap bit = BitmapFactory.decodeResource(getResources(), 
							R.drawable.bus_marker_blue);
					
						
				SingletonBusDB busDB = SingletonBusDB.createInstance();
				HashMap<String, Integer> busStopHash = 
									(HashMap<String, Integer>)busDB.getBusStopHash();
						
				/* Before marker image */
				Bitmap befMarker = busDB.markers.get(busStopHash.get(arrayList.get(0).getName())).bit;
						
				busDB.markers.get(busStopHash.get(arrayList.get(0).getName())).bit = bit;
				busDB.markers.get(busStopHash.get(arrayList.get(1).getName())).bit = bit;
					    
				/* Add all markers */
				addAllMarkers(MapTag);
				    	
				/* Recovery default marker */
				busDB.markers.get(busStopHash.get(arrayList.get(0).getName())).bit = befMarker;
				busDB.markers.get(busStopHash.get(arrayList.get(1).getName())).bit = befMarker;
				
				busAdapter = new BusAdapter(MapOverlay.this, R.layout.bustext, arrayList);
				list.setAdapter(busAdapter);
				list.setOnItemClickListener(mItemClickListener);
				
			} else{
			    /* 모든 마커를 추가 */
		    	addAllMarkers(MapTag);
		    	
		    	viewSelectFrame.setVisibility(View.GONE);
		    	
		    	isExit = false;
		    	
		    	Calendar calendar = Calendar.getInstance();
		    	int hour = calendar.get(Calendar.HOUR_OF_DAY);
		    	
		    	if((hour > 23) || (hour < 5)){	//실제 차가 끊길 때
		    		Toast.makeText(MapOverlay.this, "막차 시간이 지났습니다.",
							Toast.LENGTH_LONG).show();
		    	} else {		//연결 상태가 안좋을 때
		    		Toast.makeText(MapOverlay.this, "네트워크 상태가 좋지 않습니다. 다시 시도해 주세요.",
							Toast.LENGTH_LONG).show();
		    	}
				
			}
			/* Enable refresh Button */
			refreshBtn.setEnabled(true);
			
		}
		
		@Override 
		protected void onCancelled(){
			/* Gone progressBar */
			progress.setVisibility(View.GONE);
		}
		
		private ArrayList<BusInfo> getBusStopDataList(){
			ArrayList<BusInfo> busTimeList = new ArrayList<BusInfo>();
							
			URL url = null;
			try {		
				url = new URL("http://kimtree.net/dev/busapi/station.php?id=01278");
					
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			
			String tag = "";
					
			try {
						
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				XmlPullParser parser = factory.newPullParser();
				parser.setInput(url.openStream(), null);			
						
				int eventType = parser.getEventType();
						
				while(eventType != XmlPullParser.END_DOCUMENT) {
							
					switch (eventType) {
							
					case XmlPullParser.START_DOCUMENT:
						break;
								
					case XmlPullParser.END_DOCUMENT:
						break;
								
					case XmlPullParser.START_TAG:
						tag = parser.getName();
						if (tag.equals("now")) {
							busTimeList.add(new BusInfo("첫 번째 버스", 
									Integer.parseInt(parser.getAttributeValue(0)),
									Integer.parseInt(parser.getAttributeValue(1)),
									parser.getAttributeValue(2)));
						} else if(tag.equals("next")){
							busTimeList.add(new BusInfo("두 번째 버스", 
									Integer.parseInt(parser.getAttributeValue(0)),
									Integer.parseInt(parser.getAttributeValue(1)),
									parser.getAttributeValue(2)));
						}
						break;
								
					case XmlPullParser.END_TAG:
						break;
								
					case XmlPullParser.TEXT:
						break;	
					}		
					eventType = parser.next();	
					
				}			
			}
			catch (Exception e) {
				e.printStackTrace();
			}			
			
			return busTimeList;
			
		}
		
   }
   
   @Override
   protected boolean isRouteDisplayed() {
	   return false;
   }
   
   @Override
   protected void onResume(){
	   super.onResume();   
	   
	   /* 배터리 절약을 위해 프로그램이 foreground 인 경우에만 위치 업데이트 */
	   mLocationManager.requestLocationUpdates(provider, 1000, 1, this);     
   }
 
   @Override
   protected void onPause(){
	   super.onPause();
	   
	   /* 배터리 절약을 위해 프로그램이 background 인 경우에는 업데이트 중지  */
	   mLocationManager.removeUpdates(this);
   }
  
   @Override
	public void onBackPressed(){		
		finish();
		
		/* zoom out animation */
		overridePendingTransition(0, R.anim.zoom_out);
   }

   @Override
   public void onLocationChanged(Location location) {	   
	    if(!MapTag.equals("BusView")){		/* BusView가 아닐 경우 */
	    	map.invalidate();  
	    }
   }

   @Override
   public void onProviderDisabled(String provider) {	
   }

   @Override
   public void onProviderEnabled(String provider) {	
   }

   @Override
   public void onStatusChanged(String provider, int status, Bundle extras) {	
   }	

}