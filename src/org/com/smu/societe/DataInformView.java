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
* DataInformView Ŭ������ ī�޶� �������� ��  Marker �� Radar �� �����ϴ� View 
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
	 * DataInforView�� ������ �� �ִ� ���·� ����� �ִ� �Լ�
	 */
	public void doStart() {
		state.nextLStatus = ARState.NOT_STARTED;
	}

	/** 
	 * �ʱ�ȭ�� ���������� Ȯ���ϴ� �Լ�
	 * 
	 * @return �ʱ�ȭ ���¿� ���� boolean data
	 */
	public boolean isInited() {
		return isInit;
	}

	/**
	 * DataInformView ��ü �ʱ�ȭ
	 *
	 * @param widthInit 	�ʺ�
	 * @param heightInit	����
	 * @exception Camera��ü ������ �߻��ϴ� ����
	 */
	public void init(int widthInit, int heightInit) {
		try {
			width = widthInit;
			height = heightInit;

			//radar �� ������ ��ܿ� ��ġ
			rx = width - (RadarPoints.RADIUS*2 + 10);
			
			//Camera ��ü ����
			cam = new Camera(width, height, true);
			cam.setViewAngle(Camera.DEFAULT_VIEW_ANGLE);	//�̷ν� distance�� ������

			//Radar���� left line, right line�� ��������
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
	 * DataInforView�� ��ü�� �׷��ִ� �Լ�
	 * 
	 * @param dw	ȭ�鿡 �׷��� ��ü���� ����
	 */
	public void draw(PaintScreen dw) {
		ctx.getRM(cam.transform);
		curFix = ctx.getCurrentLocation();

		/* Radar�� ���� ������  bearing ��  pitch ���� ���� */
		state.calcPitchBearing(cam.transform);

		//jLayer = SingletonDB.createInstance();
		jLayer = SingletonFactory.create(MainSelectView.viewType);
		
		
		/* Update markers */
		for (int i = 0; i < jLayer.markers.size(); i++) {
			Marker ma = jLayer.markers.get(i);
			float[] dist = new float[1];
			dist[0] = 0;
			
			/* ���� ��ġ�� ��ü�� ���� ��ġ���� �Ÿ��� ���� */
			Location.distanceBetween(ma.mGeoLoc.getLatitude(), ma.mGeoLoc.getLongitude(), 
					ctx.getCurrentLocation().getLatitude(), ctx.getCurrentLocation().getLongitude(), dist);
			
			/* marker ���� �Ÿ��� radar �� ���� ���� ���� ��쿡�� marker �� ��������  */
			if (dist[0] / 1000f < radius) {
				if (!frozen) ma.update(curFix, System.currentTimeMillis());
				ma.calcPaint(cam, addX, addY);
				ma.draw(dw);
			}
		}

		//Draw Radar
		String	dirTxt = "";
		
		/* ������ ���¹����� ���������� ���� */
		int bearing = (int) state.getCurBearing(); 
		int range = (int) (state.getCurBearing() / (360f / 16f)); 
		
		//�����
		//Log1(bearing);
		//Log2((int)state.getCurPitch());
		
		/* ���� ������ ǥ���ϱ����� */
		if (range == 15 || range == 0) dirTxt = "N"; 
		else if (range == 1 || range == 2) dirTxt = "NE"; 
		else if (range == 3 || range == 4) dirTxt = "E"; 
		else if (range == 5 || range == 6) dirTxt = "SE";
		else if (range == 7 || range == 8) dirTxt= "S"; 
		else if (range == 9 || range == 10) dirTxt = "SW"; 
		else if (range == 11 || range == 12) dirTxt = "W"; 
		else if (range == 13 || range == 14) dirTxt = "NW";

		/* radar ��  view angle Line �� �׸� */
		radarPoints.view = this; 
		dw.paintObj(radarPoints, rx, ry, -state.getCurBearing(), 1); 
		dw.setFill(false);
		dw.setColor(Color.argb(150,225,225,225)); 
		dw.paintLine( lrl.x, lrl.y, rx+RadarPoints.RADIUS, ry+RadarPoints.RADIUS); 
		dw.paintLine( rrl.x, rrl.y, rx+RadarPoints.RADIUS, ry+RadarPoints.RADIUS); 
		dw.setColor(Color.rgb(255,255,255));
		dw.setFontSize(12);

		/* Radar �� ���� ǥ�� */
		radarText(dw, ARUtils.formatDist(radius * 1000), 
				rx + RadarPoints.RADIUS, ry + RadarPoints.RADIUS*2 -10, false);
		
		//���� ���� ����� ������ ǥ��
		//radarText(dw, "" + bearing + ((char) 176) + " " + dirTxt, rx + RadarPoints.RADIUS, ry - 5, true); 
			
	}

	/**
	 * Radar ��  ǥ����  Text �� �������ִ� �Լ�
	 * 
	 * @param dw			ȭ�鿡 �׷��� ��ü���� ����
	 * @param text			Radar �� ��Ÿ�� Text
	 * @param x				x ��ǥ
	 * @param y				y ��ǥ
	 * @param containBox	Box �� �������� ������ boolean data
	 */
	void radarText(PaintScreen dw, String text, float x, float y, boolean containBox) {
		float paddingw = 4, paddingh = 2;
		float w = dw.getTextWidth(text) + paddingw * 2;
		float h = dw.getTextAsc() + dw.getTextDesc() + paddingh * 2;
		
		/* ����� ������ ǥ�����ٶ� �ڽ� ���·� �����ֱ� ���� */
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
	 * Touch Event�� �߻��� ��� ȣ��Ǵ� �Լ�
	 * 
	 * @param x		X��ǥ
	 * @param y		Y��ǥ
	 */
	public void touchEvent(float x, float y){
		
		TouchEvent touchedPoint = new TouchEvent(x, y);		
		
		if (touchedPoint != null && touchedPoint.type == TouchEvent.TOUCH) {
			ARView.touched = handleTouchEvent(touchedPoint);
		}

	}

	/**
	 * ��ü�� ��ġ�� ��쿡 ����� ��ġ�Ͽ������� ���� ������ �ٷ�� �Լ�
	 * 
	 * @param 	evt			TouchEvent�� ���� ����
	 * @return	evtHandled	��ü�� ��ġ�Ǿ������� ���� ����
	 */
	boolean handleTouchEvent(TouchEvent evt) {
		boolean evtHandled = false;

		/* Handle event	*/
		for (int i = 0; i < jLayer.markers.size() && !evtHandled; i++) {
			Marker pm = jLayer.markers.get(i);
			
			/* marker �߿� � ���� ��ġ�ߴ��� üũ */
			pm.calcPaint(cam, addX, addY);
			evtHandled = pm.isClicked(evt.x, evt.y);
			
			/* touch �� ��ü�� �̸��� ���� */
			if(evtHandled){
				ARView.touchedId = pm.objectId;
				ARView.touchedName = pm.mText;
				
				/* ��ġ�� ��ü�� ��ġ ���� */
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
 * TouchEventŬ����. ��ġ�߻���  x, 
 * y ��ǥ�� ���� ������ �����ϴ� Ŭ����
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
	 * x, y��ǥ�� ���ڿ��� �������ִ� �Լ�
	 * 
	 * @return x, y��ǥ
	 */
	public String toString() {
			return "(" + x + "," + y + ")";	
	}
}