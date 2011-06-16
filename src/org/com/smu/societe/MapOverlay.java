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
 * ������ �ǹ��� ��ġ�� �ڽ��� ��ġ�� �����ֱ� ���� Ŭ����
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
	    
	    /* Location Provider �� �������ִ� Criteria ���� */
	    Criteria c = new Criteria();
	    c.setAccuracy(Criteria.NO_REQUIREMENT);
		c.setCostAllowed(true);
		c.setBearingRequired(false);
		c.setAltitudeRequired(false);    
	    
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		provider = mLocationManager.getBestProvider(c, true);
		
		connectMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		/* MAPTAG�� �޾ƿ� */
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
	    	//��� ��Ŀ�� �߰�
	    	addAllMarkers(MapTag);		
	    }
	    
   	}
   
   	/**
   	 * ��� ��ü�� �������� �����������ֱ� ���� ������ ��ü�� �߰����ִ� �Լ�
   	 * 
   	 */
   	private void addAllMarkers(final String MapTag){
   		runOnUiThread(new Runnable(){
   			public void run(){		      
   				/* default marker image */
			    Bitmap bit = BitmapFactory.decodeFile("/sdcard/ARview/A.png");
			    Drawable d = (Drawable)(new BitmapDrawable(bit));
			    
			    /* Factory pattern ����. ��Ȯ�� � Ŭ�������� �Ű澲�� �ʰ� ������ �� �ֱ� ������
			              �ξ� ��������!! */
			    overlayItem = OverlayItemFactory.create(MapTag, d, map);
			    
			    /* Add all markers */
			    overlayItem.addAllMarkers();
			    
			    map.getOverlays().add(overlayItem);
				
				map.invalidate();
   			}
   		});
	   	
   	}
   
   	/** 
   	 * Map view �� �ʱ�ȭ�ϴ� �Լ�
   	 * 
   	 */
   	private void initMapView(final String MapTag) {
		map.setSatellite(false); 			//�������� ��� ����
		map.setBuiltInZoomControls(true); 	//zoomControl ���
		map.setPersistentDrawingCache(MapView.PERSISTENT_ALL_CACHES);
			      
		/* zoom �� animatTo �� MapController Ŭ������ �ؾ��� */
		controller = map.getController();
			    
		if(MapTag.equals("BusView")){
			controller.setZoom(14);
		} else{
			controller.setZoom(17);  
			    	
			/* MyLocationOverlay��ü�� ����Ͽ� ���� ��ġ�� ǥ�����ֱ� ���� */
			overlay = new MyLocationOverlay(MapOverlay.this, map);
			overlay.enableMyLocation();
			overlay.enableCompass();	//��ħ�� ǥ��
		    overlay.runOnFirstFix(new Runnable(){
				public void run() {
					/* ���� ��ġ�� ���� ��Ŀ �̵� */
					map.getController().animateTo(overlay.getMyLocation());
				}	
		    });
		    		
		    /* ���� ��ġ�� ��Ÿ���� ��Ŀ �߰� */
		    map.getOverlays().add(overlay); 
		}

   	}  
   
   	/**
   	 * ����Ʈ�� ������ Ŭ�� �̺�Ʈ ������ 
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
		if(NetworkState.isNetworkConnected(connectMgr)){	//Network ������� Ȯ��
			/* Disabled refresh Button */
			refreshBtn.setEnabled(false);
			
			/* Remove all data */
			arrayList.removeAll(arrayList);
			
			if(busAdapter != null){			//���ΰ�ħ �� ���
				busAdapter.notifyDataSetChanged(); 
			}
			
			/* Execute Bus AcyncTask */
			new DownloadAsyncTask().execute(busURL);
			
		} else{
			Toast.makeText(this, "��Ʈ��ũ ���°� ���� �ʽ��ϴ�. �ٽ� �õ��� �ּ���.", 
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
			
			if(busTimeList != null && busTimeList.size() == 2){	//���� �ð��� ������ ���� ���
				return busTimeList;
			} else{
				isExit = true;	//ListView�� ������Ʈ ���� ����
			}
			return null;				
		}
		
		@Override
		protected void onPostExecute(ArrayList<BusInfo> busTimeList){
			progress.setVisibility(View.GONE);
			
			arrayList = busTimeList;
			
			if(!isExit && 		//�����ð��� ������ �ʾ��� ��츸 ������Ʈ
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
			    /* ��� ��Ŀ�� �߰� */
		    	addAllMarkers(MapTag);
		    	
		    	viewSelectFrame.setVisibility(View.GONE);
		    	
		    	isExit = false;
		    	
		    	Calendar calendar = Calendar.getInstance();
		    	int hour = calendar.get(Calendar.HOUR_OF_DAY);
		    	
		    	if((hour > 23) || (hour < 5)){	//���� ���� ���� ��
		    		Toast.makeText(MapOverlay.this, "���� �ð��� �������ϴ�.",
							Toast.LENGTH_LONG).show();
		    	} else {		//���� ���°� ������ ��
		    		Toast.makeText(MapOverlay.this, "��Ʈ��ũ ���°� ���� �ʽ��ϴ�. �ٽ� �õ��� �ּ���.",
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
							busTimeList.add(new BusInfo("ù ��° ����", 
									Integer.parseInt(parser.getAttributeValue(0)),
									Integer.parseInt(parser.getAttributeValue(1)),
									parser.getAttributeValue(2)));
						} else if(tag.equals("next")){
							busTimeList.add(new BusInfo("�� ��° ����", 
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
	   
	   /* ���͸� ������ ���� ���α׷��� foreground �� ��쿡�� ��ġ ������Ʈ */
	   mLocationManager.requestLocationUpdates(provider, 1000, 1, this);     
   }
 
   @Override
   protected void onPause(){
	   super.onPause();
	   
	   /* ���͸� ������ ���� ���α׷��� background �� ��쿡�� ������Ʈ ����  */
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
	    if(!MapTag.equals("BusView")){		/* BusView�� �ƴ� ��� */
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