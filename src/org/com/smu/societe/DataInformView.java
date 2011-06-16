package org.com.smu.societe;

import org.com.smu.societe.data.SingletonBase;
import org.com.smu.societe.data.SingletonFactory;
import org.com.smu.societe.gui.PaintScreen;
import org.com.smu.societe.gui.RadarPoints;
import org.com.smu.societe.gui.ScreenLine;
import org.com.smu.societe.render.Camera;

import android.graphics.Color;
import android.location.Location;
import android.util.Log;

/**
* DataInformView 클래스는 카메라에 오버레이 될  Marker 와 Radar 를 포함하는 View 
*
* @Project  Societe
* @File  	DataInformView.java
* @Date  	2011-04-10 
* @Author  	ACE
*/
public class DataInformView {

	private ARContext ctx;
	private Camera cam;
	
	boolean isInit = false;
	int width, height;	
	boolean frozen = false;
	int retry = 0;
	
	public ARState state = new ARState();	
	private Location curFix;
	
	public float screenWidth, screenHeight;	
	
	public SingletonBase jLayer;	
	
	public float radius = 20;	
	public boolean isLauncherStarted=false;	
	
	private RadarPoints radarPoints = new RadarPoints();
	private ScreenLine lrl = new ScreenLine();
	private ScreenLine rrl = new ScreenLine();
	float rx = 0, ry = 20;
	public float addX = 0, addY = 0;
	
	private String type;
	
	public DataInformView(ARContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * DataInforView를 시작할 수 있는 상태로 만들어 주는 함수
	 */
	public void doStart() {
		state.nextLStatus = ARState.NOT_STARTED;
	}

	/** 
	 * 초기화된 상태인지를 확인하는 함수
	 * 
	 * @return 초기화 상태에 관한 boolean data
	 */
	public boolean isInited() {
		return isInit;
	}

	/**
	 * DataInformView 객체 초기화
	 *
	 * @param widthInit 	너비
	 * @param heightInit	높이
	 * @exception Camera객체 생성시 발생하는 예외
	 */
	public void init(int widthInit, int heightInit) {
		try {
			width = widthInit;
			height = heightInit;

			//radar 를 오른쪽 상단에 위치
			rx = width - (RadarPoints.RADIUS*2 + 10);
			
			//Camera 객체 생성
			cam = new Camera(width, height, true);
			cam.setViewAngle(Camera.DEFAULT_VIEW_ANGLE);	//이로써 distance가 구해짐

			//Radar에서 left line, right line을 지정해줌
			lrl.set(0, -RadarPoints.RADIUS);
			lrl.rotate(Camera.DEFAULT_VIEW_ANGLE / 2);
			lrl.add(rx + RadarPoints.RADIUS, ry + RadarPoints.RADIUS);
			rrl.set(0, -RadarPoints.RADIUS);
			rrl.rotate(-Camera.DEFAULT_VIEW_ANGLE / 2);
			rrl.add(rx + RadarPoints.RADIUS, ry + RadarPoints.RADIUS);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		frozen = false;
		isInit = true;
	}

	/**
	 * DataInforView에 객체를 그려주는 함수
	 * 
	 * @param dw	화면에 그려줄 객체들의 정보
	 */
	public void draw(PaintScreen dw) {
		ctx.getRM(cam.transform);
		curFix = ctx.getCurrentLocation();

		/* Radar를 위해 현재의  bearing 과  pitch 값을 얻어옴 */
		state.calcPitchBearing(cam.transform);

		//jLayer = SingletonDB.createInstance();
		jLayer = SingletonFactory.create(MainSelectView.viewType);
		
		
		/* Update markers */
		for (int i = 0; i < jLayer.markers.size(); i++) {
			Marker ma = jLayer.markers.get(i);
			float[] dist = new float[1];
			dist[0] = 0;
			
			/* 현재 위치와 물체의 실제 위치간의 거리를 구함 */
			Location.distanceBetween(ma.mGeoLoc.getLatitude(), ma.mGeoLoc.getLongitude(), 
					ctx.getCurrentLocation().getLatitude(), ctx.getCurrentLocation().getLongitude(), dist);
			
			/* marker 와의 거리가 radar 의 범위 내에 들어올 경우에만 marker 를 오버레이  */
			if (dist[0] / 1000f < radius) {
				if (!frozen) ma.update(curFix, System.currentTimeMillis());
				ma.calcPaint(cam, addX, addY);
				ma.draw(dw);
			}
		}

		//Draw Radar
		String	dirTxt = "";
		
		/* 현재의 보는방향을 각도값으로 얻음 */
		int bearing = (int) state.getCurBearing(); 
		int range = (int) (state.getCurBearing() / (360f / 16f)); 
		
		//디버깅
		//Log1(bearing);
		//Log2((int)state.getCurPitch());
		
		/* 보는 방향을 표시하기위함 */
		if (range == 15 || range == 0) dirTxt = "N"; 
		else if (range == 1 || range == 2) dirTxt = "NE"; 
		else if (range == 3 || range == 4) dirTxt = "E"; 
		else if (range == 5 || range == 6) dirTxt = "SE";
		else if (range == 7 || range == 8) dirTxt= "S"; 
		else if (range == 9 || range == 10) dirTxt = "SW"; 
		else if (range == 11 || range == 12) dirTxt = "W"; 
		else if (range == 13 || range == 14) dirTxt = "NW";

		/* radar 와  view angle Line 을 그림 */
		radarPoints.view = this; 
		dw.paintObj(radarPoints, rx, ry, -state.getCurBearing(), 1); 
		dw.setFill(false);
		dw.setColor(Color.argb(150,225,225,225)); 
		dw.paintLine( lrl.x, lrl.y, rx+RadarPoints.RADIUS, ry+RadarPoints.RADIUS); 
		dw.paintLine( rrl.x, rrl.y, rx+RadarPoints.RADIUS, ry+RadarPoints.RADIUS); 
		dw.setColor(Color.rgb(255,255,255));
		dw.setFontSize(12);

		/* Radar 의 범위 표시 */
		radarText(dw, ARUtils.formatDist(radius * 1000), 
				rx + RadarPoints.RADIUS, ry + RadarPoints.RADIUS*2 -10, false);
		
		//현재 보는 방향과 각도를 표시
		//radarText(dw, "" + bearing + ((char) 176) + " " + dirTxt, rx + RadarPoints.RADIUS, ry - 5, true); 
			
	}

	/**
	 * Radar 에  표시할  Text 를 설정해주는 함수
	 * 
	 * @param dw			화면에 그려줄 객체들의 정보
	 * @param text			Radar 에 나타낼 Text
	 * @param x				x 좌표
	 * @param y				y 좌표
	 * @param containBox	Box 를 포함할지 여부의 boolean data
	 */
	void radarText(PaintScreen dw, String text, float x, float y, boolean containBox) {
		float paddingw = 4, paddingh = 2;
		float w = dw.getTextWidth(text) + paddingw * 2;
		float h = dw.getTextAsc() + dw.getTextDesc() + paddingh * 2;
		
		/* 방향과 각도를 표시해줄때 박스 형태로 보여주기 위함 */
		if (containBox) {
			 dw.setColor(Color.rgb(0, 0, 0));
			 dw.setFill(true);
			 dw.paintRect(x - w / 2, y - h / 2, w, h);
			 dw.setColor(Color.rgb(255, 255, 255));
			 dw.setFill(false);
			 dw.paintRect(x - w / 2, y - h / 2, w, h);
		}
		dw.paintText(paddingw + x - w / 2, paddingh + dw.getTextAsc() + y - h / 2, text);
	}
	
	/**
	 * Touch Event가 발생할 경우 호출되는 함수
	 * 
	 * @param x		X좌표
	 * @param y		Y좌표
	 */
	public void touchEvent(float x, float y){
		
		TouchEvent touchedPoint = new TouchEvent(x, y);		
		
		if (touchedPoint != null && touchedPoint.type == TouchEvent.TOUCH) {
			ARView.touched = handleTouchEvent(touchedPoint);
		}

	}

	/**
	 * 객체를 터치할 경우에 어떤것을 터치하였는지에 대한 정보를 다루는 함수
	 * 
	 * @param 	evt			TouchEvent에 대한 정보
	 * @return	evtHandled	객체가 터치되었는지에 대한 여부
	 */
	boolean handleTouchEvent(TouchEvent evt) {
		boolean evtHandled = false;

		/* Handle event	*/
		for (int i = 0; i < jLayer.markers.size() && !evtHandled; i++) {
			Marker pm = jLayer.markers.get(i);
			
			/* marker 중에 어떤 것을 터치했는지 체크 */
			pm.calcPaint(cam, addX, addY);
			evtHandled = pm.isClicked(evt.x, evt.y);
			
			/* touch 된 객체의 이름을 저장 */
			if(evtHandled){
				ARView.touchedId = pm.objectId;
				ARView.touchedName = pm.mText;
				
				/* 터치된 객체의 위치 저장 */
				ARView.touchedLocation.setLatitude(pm.mGeoLoc.getLatitude());
				ARView.touchedLocation.setLongitude(pm.mGeoLoc.getLongitude());
				
			}
			
		}	
		return evtHandled;
		
	}
	
	public void Log1(int bear){
		Log.d("Angle", bear + " ");
	}
	
	public void Log2(int pitch){
		Log.d("Pitch", pitch + " ");
	}
	
}

/**
 * TouchEvent클래스. 터치발생시  x, 
 * y 좌표에 관한 정보를 관리하는 클래스
 * 
 * @author ACE
 *
 */
class TouchEvent{
	public float x, y;
	public int type;
	public static int TOUCH = 0;
	
	public TouchEvent(float x, float y) {
		this.type = TOUCH;
		this.x = x;
		this.y = y;
	}

	@Override
	/**
	 * x, y좌표를 문자열로 리턴해주는 함수
	 * 
	 * @return x, y좌표
	 */
	public String toString() {
			return "(" + x + "," + y + ")";	
	}
}