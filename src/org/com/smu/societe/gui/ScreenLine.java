package org.com.smu.societe.gui;

/**
 * ScreenLine Ŭ������ Radar�� �þ߸� ��Ÿ���ִ� ���� �Ӽ��� �����ϴ� Ŭ������
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
	 * �׷��� ���� ����
	 * 
	 * @param Init_x	�ʱ� X��ǥ
	 * @param Init_y	�ʱ� Y��ǥ
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
	 * Line�� �̵����� �ִ� �Լ�
	 * 
	 * @param addx	������ X��ǥ
	 * @param addy	������ Y��ǥ
	 */
	public void add(float addx, float addy) {
		x += addx;
		y += addy;
	}
}
