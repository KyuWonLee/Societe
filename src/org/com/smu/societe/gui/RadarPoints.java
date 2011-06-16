/*
 * RadarPoints클래스는 카메라프리뷰에 Radar를 그려주기 위한 클래스임
 * Radar 상의 marker가 어느 위치에 있는지 나타내주는 draw함수도 포함됨
 */
package org.com.smu.societe.gui;

import org.com.smu.societe.DataInformView;
import org.com.smu.societe.Marker;

import android.graphics.Color;

/**
 * 
 * @Project  Campus AR 
 * @File  RadarPoints.java
 * @Date  2010-10-10
 * @author App+
 *
 */
public class RadarPoints implements ScreenObj {
	
	public DataInformView view;
	
	float range;
	
	public static float RADIUS = 70;
	
	static float originX = 0 , originY = 0;
	
	static int radarColor = Color.argb(200, 60, 130, 180);

	/**
	 * Radar 상에  Marker 가 위치하고 있는지를 그려주는 함수
	 * 
	 * @param dw	화면에 그려줄 객체들의 정보
	 */
	public void paint(PaintScreen dw) {
		
		range = view.radius * 1000;	//zoom level에서 1000을 1로 정했기 때문!!
		
		//draw Radar
		dw.setFill(true);
		dw.setColor(radarColor);
		dw.paintCircle(originX + RADIUS, originY + RADIUS, RADIUS);
		dw.setFill(false);
		dw.setColor(Color.WHITE);
		dw.paintCircle(originX + RADIUS, originY + RADIUS, RADIUS);

		
		float scale = range / RADIUS;

		//Marker 들을 Radar 에 표시
		for (int i = 0; i < view.jLayer.markers.size(); i++) {
			Marker pm = view.jLayer.markers.get(i);
			float x = pm.loc.x / scale;
			float y = pm.loc.z / scale;

			//marker 가 Radar 의 범위 안에 들어온다면 표시함
			if (x * x + y * y < RADIUS * RADIUS) {	//원
				dw.setFill(true);
				dw.setColor(Color.rgb(255, 255, 255));
				//dw.paintRect(x + RADIUS - 1, y + RADIUS - 1, 3, 3);
				dw.paintCircle(x + RADIUS - 1, y + RADIUS - 1, 2);
			}
		}
	}

	/**
	 * Get the Width
	 * 
	 * @return	The width of Radar
	 */
	public float getWidth() {
		return RADIUS * 2;
	}

	/**
	 * Get the height
	 * 
	 * @return The width of Radar
	 */
	public float getHeight() {
		return RADIUS * 2;
	}
}