package org.com.smu.societe.gui;

/**
 * ScreenLine 클래스는 Radar상에 시야를 나타내주는 선의 속성을 지정하는 클래스임
 * 
 * @Project  Campus AR 
 * @File  ScreenLine.java
 * @Date  2010-10-10
 * @author App+
 *
 */
public class ScreenLine {
	public float x, y;

	public ScreenLine() {
		set(0, 0);
	}
	
	/**
	 * Set of line's x and y
	 * 
	 * @param x
	 * @param y
	 */
	public ScreenLine(float x, float y) {
		set(x, y);
	}

	/**
	 * 그려줄 지점 세팅
	 * 
	 * @param Init_x	초기 X좌표
	 * @param Init_y	초기 Y좌표
	 */
	public void set(float Init_x, float Init_y) {
		x = Init_x;
		y = Init_y;
	}

	/**
	 * Rotate line
	 * 
	 * @param angle	Rotation angle
	 */
	public void rotate(double angle) {
		float xp = (float) Math.cos(angle) * x - (float) Math.sin(angle) * y;
		float yp = (float) Math.sin(angle) * x + (float) Math.cos(angle) * y;

		x = xp;
		y = yp;
	}

	/**
	 * Line을 이동시켜 주는 함수
	 * 
	 * @param addx	더해줄 X좌표
	 * @param addy	더해줄 Y좌표
	 */
	public void add(float addx, float addy) {
		x += addx;
		y += addy;
	}
}
