package org.com.smu.societe;

import static android.hardware.SensorManager.SENSOR_DELAY_GAME;

import java.util.List;
import java.util.Stack;

import org.com.smu.societe.camerapreview.CameraSurface;
import org.com.smu.societe.data.SingletonBase;
import org.com.smu.societe.data.SingletonFactory;
import org.com.smu.societe.gui.PaintScreen;
import org.com.smu.societe.render.Matrix;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
/**
*
* @Project  Societe
* @File  	ARView.java
* @Date  	2011-04-10
* @Author  	ACE
*/
public class ARView extends Activity implements SensorEventListener, LocationListener {

	private CameraSurface camScreen;
	private ARScene augScreen;
	
	static ARContext ctx;
	static PaintScreen dWindow;
	static DataInformView view;

	float RTmp[] = new float[9];
	float CR[] = new float[9];
	float I[] = new float[9];
	float grav[] = new float[3];
	float mag[] = new float[3];

	private SensorManager sensorMgr;
	private List<Sensor> sensors;
	private Sensor sensorGrav, sensorMag;
	private LocationManager locationMgr;
	boolean isGpsEnabled = false;

	int rHistIdx = 0;
	private Matrix tempR = new Matrix();
	private Matrix finalR = new Matrix();
	private Matrix smoothR = new Matrix();
	private Matrix histR[] = new Matrix[60];
	private Matrix m1 = new Matrix();
	private Matrix m2 = new Matrix();
	private Matrix m3 = new Matrix();
	private Matrix m4 = new Matrix();

	private boolean fError = false;
	
	public static boolean isInit = false;
	public static boolean isClickWifi = false;
	public static boolean touched = false;
	public static int touchedId;
	public static String touchedName;
	public static Location touchedLocation = new Location("location");
	
	private FrameLayout viewSelectFrame;
	
	private Stack<String> stack = new Stack<String>();
	
	/**
	  * 
	  *
	  * @param ex1	
	  * @exception Exception 
	  */	
	public void doError(Exception ex1) {
		if (!fError) {
			fError = true;
			//String fErrorTxt = ex1.toString();
			//Exception fExeption = ex1;
			
			ex1.printStackTrace();
			try {
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
		}

		try {
			augScreen.invalidate();
		} catch (Exception e) { }
	}
	
	/**
	  * 
	  *
	  * @exception Exception
	  */
	public void killOnError() throws Exception {
		if (fError)
			throw new Exception();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {	
			
			//파란색 바
			FrameLayout fl = new FrameLayout(this);
			ImageView img = new ImageView(this);
			img.setImageResource(R.drawable.arview_bar);
			fl.addView(img);
			fl.setPadding(20, 0, 30, 20);
			
			
			//홈버튼과 뷰버튼
			FrameLayout fl2 = new FrameLayout(this);
			RelativeLayout rl = new RelativeLayout(this);
			
			
			/* Select View  */
			LayoutInflater inflater = (LayoutInflater)getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			viewSelectFrame = (FrameLayout)inflater.inflate(R.layout.select_view, null);
			viewSelectFrame.setVisibility(View.GONE);
			
			/* Map Button */
			ImageButton mapBtn = (ImageButton)viewSelectFrame.findViewById(R.id.map);
			mapBtn.setImageResource(R.drawable.select_map_clicked);
			mapBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ARView.this, MapOverlay.class);
					intent.putExtra("MAPTAG", MainSelectView.viewType);
					startActivity(intent);
					
					/* zoom animation */
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				}
			});
			
			/* List Button */
			ImageButton listBtn = (ImageButton)viewSelectFrame.findViewById(R.id.List);
			listBtn.setImageResource(R.drawable.select_list_clicked);
			listBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ARView.this, ListBuildView.class);
					intent.putExtra("Location", new double[]{touchedLocation.getLatitude(), 
							touchedLocation.getLongitude()});
					startActivity(intent);
					
					/* zoom animation */
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
					
				}
			});
			
			/* Home Image Button */
			ImageButton homeBtn = new ImageButton(this);
			homeBtn.setBackgroundResource(R.drawable.arview_home_clicked);
			homeBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
					
					overridePendingTransition(0, R.anim.zoom_out);
				}
			});
			
			/* View Image Button */
			ImageButton viewBtn = new ImageButton(this);
			viewBtn.setBackgroundResource(R.drawable.arview_view_clicked);
			viewBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					if(viewSelectFrame.isShown()){
						viewSelectFrame.setVisibility(View.GONE);
					} else{
						viewSelectFrame.setVisibility(View.VISIBLE);
					}
					
				}
			});
			
			/* Home button 에 대한 정렬 */
			RelativeLayout.LayoutParams homeParam = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			homeParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			
			homeBtn.setLayoutParams(homeParam);		//home 버튼에 적용 	
			
			/* View button 에 대한 정렬 */
			RelativeLayout.LayoutParams viewParam = new RelativeLayout.LayoutParams(
					100, 80);
			viewParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			viewParam.addRule(RelativeLayout.CENTER_VERTICAL);
			
			viewBtn.setLayoutParams(viewParam);		//view 버튼에 적용 

			rl.addView(homeBtn);					//RelativeLayout 에 추가
			rl.addView(viewBtn);					//RelativeLayout 에 추가
			
			fl2.addView(rl);						//FrameLayout에 홈버튼과 뷰버튼 추가
			fl2.setPadding(7, 0, 7, 0);		
			
			
			camScreen = new CameraSurface(this);
			augScreen = new ARScene(this);			
			
			setContentView(camScreen);
			
			//증강현실 화면 추가
			addContentView(augScreen, new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));	
			
			//오버레이 될 버튼들에 대한 화면 추가
			addContentView(fl, new FrameLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					Gravity.BOTTOM));
			
			addContentView(fl2, new FrameLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					Gravity.BOTTOM));
			
			addContentView(viewSelectFrame, new FrameLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));	
			
			if(!isInit){
				ctx = new ARContext(this);			
				 
				dWindow = new PaintScreen();
				view = new DataInformView(ctx);
				
				SetZoomLevel(); 
				isInit = true;	
			}
			
			if(ctx.isActualLocation() == false){
		    	setGPSDialog();
			}
			
		} catch (Exception e) {
			doError(e);
		}
	}
	
	public void setGPSDialog(){
		
	}

	
	@Override
	/**
	  * 
	  * 
	  * @exception Exception 
	  */
	protected void onPause() {
		super.onPause();

		try {
			
			try {
				sensorMgr.unregisterListener(this, sensorGrav);
			} catch (Exception ignore) {
			}
			try {
				sensorMgr.unregisterListener(this, sensorMag);
			} catch (Exception ignore) {
			}
			sensorMgr = null;

			try {
				locationMgr.removeUpdates(this);
			} catch (Exception ignore) {
			}
			try {
				locationMgr = null;
			} catch (Exception ignore) {
			}

			if (fError) {
				finish();
			}
		} catch (Exception e) {
			doError(e);
		}
	}

	@Override
	/**
	  
	  * 
	  * @exception Exception 
	  */
	protected void onResume() {
		super.onResume();

		try {
			
			killOnError();
			ctx.mixView = this;
			view.doStart();

			double angleX, angleY;

			
			angleX = Math.toRadians(-90);
			m1.set(1f, 0f, 0f, 0f, (float) Math.cos(angleX), (float) -Math
					.sin(angleX), 0f, (float) Math.sin(angleX), (float) Math
					.cos(angleX));

			//angleX = Math.toRadians(90);
			angleY = Math.toRadians(-90);
					
			m2.set(1f, 0f, 0f, 0f, (float) Math.cos(angleX), (float) -Math
					.sin(angleX), 0f, (float) Math.sin(angleX), (float) Math
					.cos(angleX));
					
			m3.set((float) Math.cos(angleY), 0f, (float) Math.sin(angleY),
					0f, 1f, 0f, (float) -Math.sin(angleY), 0f, (float) Math
					.cos(angleY));

			m4.toIdentity();

			for (int i = 0; i < histR.length; i++) {
				histR[i] = new Matrix();
			}

			sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

			sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
			if (sensors.size() > 0) {
				sensorGrav = sensors.get(0);
			}

			sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
			if (sensors.size() > 0) {
				sensorMag = sensors.get(0);
			}

					
			sensorMgr.registerListener(this, sensorGrav, SENSOR_DELAY_GAME);
			sensorMgr.registerListener(this, sensorMag, SENSOR_DELAY_GAME);

			try {
				
				Criteria c = new Criteria();

				c.setPowerRequirement(Criteria.POWER_LOW);
				c.setAccuracy(Criteria.ACCURACY_FINE);
				
				locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				
				if(!locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)){
					alertCheckGPS();
				}
				
				String bestP = locationMgr.getBestProvider(c, true); 
				
				if(locationMgr.isProviderEnabled(bestP)) {
					
					locationMgr.requestLocationUpdates(bestP, 100, 1, this);
				}
				
				
				Location hardFix = new Location("reverseGeocoded");
				hardFix.setLatitude(37.60192850146395);
				hardFix.setLongitude(126.95474624633789);
				hardFix.setAltitude(20);

				try {			
					Location curLoc = locationMgr.getLastKnownLocation(bestP);		
					
					ctx.curLoc = new Location(hardFix);
					
				} catch (Exception ex2) {
					ctx.curLoc = new Location(hardFix);
				}

				GeomagneticField gmf = new GeomagneticField((float) ctx.curLoc
						.getLatitude(), (float) ctx.curLoc.getLongitude(),
						(float) ctx.curLoc.getAltitude(), System
						.currentTimeMillis());
 
				angleY = Math.toRadians(-gmf.getDeclination());
				m4.set((float) Math.cos(angleY), 0f,
						(float) Math.sin(angleY), 0f, 1f, 0f, (float) -Math
						.sin(angleY), 0f, (float) Math.cos(angleY));
				ctx.declination = gmf.getDeclination();		
						
			} catch (Exception e) { 
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			doError(e);

			try {
				if (sensorMgr != null) {
					sensorMgr.unregisterListener(this, sensorGrav);
					sensorMgr.unregisterListener(this, sensorMag);
					sensorMgr = null;
				}

				if (locationMgr != null) {
					locationMgr.removeUpdates(this);
					locationMgr = null;
				}
				
			} catch (Exception ex) { 
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 */
	private void alertCheckGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS가 비활성화되어있습니다.연결하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveConfigGPS();
                            }
                    })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                    });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     *  
     */
    private void moveConfigGPS() {
        Intent gpsOptionsIntent = new Intent(
        		android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
        
        /* zoom animation */
		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		
    }
    
	/**
	  * 
	  */
	private void SetZoomLevel() {
		//TODO improve zoomlevel algorithm	
		float myout = 0.5f;	
		
		view.radius = myout;		
		view.doStart();
		
	};

	/**
	 * 
	 * @param evt	SensorEvent data
	 */
	public void onSensorChanged(SensorEvent evt) {
		try {
			
			killOnError();

			if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				grav[0] = evt.values[0];
				grav[1] = evt.values[1];
				grav[2] = evt.values[2];

				
				augScreen.postInvalidate();
				
			} else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				mag[0] = evt.values[0];
				mag[1] = evt.values[1];
				mag[2] = evt.values[2];

				
				augScreen.postInvalidate();
			}

			 
			SensorManager.getRotationMatrix(RTmp, I, grav, mag);
			
			SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, CR);
			
			tempR.set(CR[0], CR[1], CR[2], CR[3], CR[4], CR[5], CR[6], CR[7], CR[8]);

			
			finalR.toIdentity();		
			finalR.prod(m4);
			finalR.prod(m1);	
			finalR.prod(tempR); 			
			finalR.prod(m3);	
			finalR.prod(m2);	
			finalR.invert(); 
			
			
			histR[rHistIdx].set(finalR);
			rHistIdx++;
			if (rHistIdx >= histR.length)
				rHistIdx = 0;
			
			smoothR.set(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
			for (int i = 0; i < histR.length; i++) {
				smoothR.add(histR[i]);
			}
			smoothR.mult(1 / (float) histR.length);

			synchronized (ctx.rotationM) {
				ctx.rotationM.set(smoothR);
			}		
			
		} catch (Exception e) {
			doError(e);
		}
	}

	/**
	 * 
	 * 
	 * @param me	MotionEvent 
	 */
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		try {
			
			killOnError();	
			
			float xPress = me.getX();
			float yPress = me.getY();
			
			if (me.getAction() == MotionEvent.ACTION_UP) {
				
				view.touchEvent(xPress, yPress);
					
				if(touched){
					if(MainSelectView.viewType.equals("ARView")){
						double[] location = new double[2];
						location[0] = touchedLocation.getLatitude();
						location[1] = touchedLocation.getLongitude();
						
						Intent intent = new Intent(this, BuildInformView.class);
						intent.putExtra("ARView", touchedId);
						intent.putExtra("Name", touchedName);
						intent.putExtra("Location", location);				
						startActivity(intent);
						
						/* zoom animation */
						overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
						
					} else{
						SingletonBase instance = SingletonFactory.create("Twitter");
						Marker m = instance.markers.get(touchedId);
						
						//이 부분에 커스텀 다이얼로그로 보여주면 됨.
						Toast.makeText(ARView.this, m.tweet, 
								Toast.LENGTH_LONG).show();
					}
					
				}
					
			}
			return true;
			
		} catch (Exception e) {
			doError(e);

			return super.onTouchEvent(me);
		}
	}


	public void onProviderDisabled(String provider) {
		isGpsEnabled = locationMgr
		.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	public void onProviderEnabled(String provider) {
		isGpsEnabled = locationMgr
		.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	/**
	  * 
	  * 
	  * @param Location	
	  */
	public void onLocationChanged(Location location) {
		try {
			killOnError();

			if (LocationManager.GPS_PROVIDER.equals(location.getProvider())) {
				
				synchronized (ctx.curLoc) {
					ctx.curLoc = location;
				}
				isGpsEnabled = true;
			}

		} catch (Exception e) {
			doError(e);
		}
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
	@Override
	public void onBackPressed(){
		if(viewSelectFrame.isShown()){
			viewSelectFrame.setVisibility(View.GONE);
			
		} else{
			finish();
			
			overridePendingTransition(0, R.anim.zoom_out);
		}
	}

}