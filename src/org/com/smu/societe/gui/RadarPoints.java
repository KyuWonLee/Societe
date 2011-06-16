/*
 * RadarPointsŬ������ ī�޶������信 Radar�� �׷��ֱ� ���� Ŭ������
 * Radar ���� marker�� ��� ��ġ�� �ִ��� ��Ÿ���ִ� draw�Լ��� ���Ե�
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
	 * Radar ��  Marker �� ��ġ�ϰ� �ִ����� �׷��ִ� �Լ�
	 * 
	 * @param dw	ȭ�鿡 �׷��� ��ü���� ����
	 */
	public void paint(PaintScreen dw) {
		
		range = view.radius * 1000;	//zoom level���� 1000�� 1�� ���߱� ����!!
		
		//draw Radar
		dw.setFill(true);
		dw.setColor(radarColor);
		dw.paintCircle(originX + RADIUS, originY + RADIUS, RADIUS);
		dw.setFill(false);
		dw.setColor(Color.WHITE);
		dw.paintCircle(originX + RADIUS, originY + RADIUS, RADIUS);

		
		float scale = range / RADIUS;

		//Marker ���� Radar �� ǥ��
		for (int i = 0; i < view.jLayer.markers.size(); i++) {
			Marker pm = view.jLayer.markers.get(i);
			float x = pm.loc.x / scale;
			float y = pm.loc.z / scale;

			//marker �� Radar �� ���� �ȿ� ���´ٸ� ǥ����
			if (x * x + y * y < RADIUS * RADIUS) {	//��
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