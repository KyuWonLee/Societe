package org.com.smu.societe.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
/**
 * PaintScreen Ŭ������ DataInformView Ŭ�������� Marker�� Radar�� 
 * �׷��ֱ� ���� Ŭ������.
 * 
 * @Project  Campus AR 
 * @File  TwitterView.java
 * @Date  2010-10-10 
 * @author App+
 *
 */
public class PaintScreen {
	Canvas canvas;
	int width, height;
	Paint paint = new Paint();
	Paint bPaint = new Paint();

	
	public PaintScreen() {
		paint.setTextSize(25);
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
	}

	/**
	 * Get a canvas
	 * 
	 * @return canvas
	 */
	public Canvas getCanvas() {
		return canvas;
	}

	/**
	 * Set a canvas
	 * 
	 * @param canvas
	 */
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}
	
	/**
	 * Set a width
	 * 
	 * @param width		Screen's width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Set a height
	 * 
	 * @param height	Screen's height
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Get a Width
	 * 
	 * @return width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get a height
	 * 
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * ü��� �Ӽ� ����
	 * 
	 * @param fill ü���� �ƴ��� ������ boolean data
	 */
	public void setFill(boolean fill) {
		if (fill)
			paint.setStyle(Paint.Style.FILL);
		else
			paint.setStyle(Paint.Style.STROKE);
	}

	/**
	 * Set a Color
	 * 
	 * @param c	Color
	 */
	public void setColor(int c) {
		paint.setColor(c);
	}

	/**
	 * Set a StrokeWidth
	 * 
	 * @param w	StrokeWidth
	 */
	public void setStrokeWidth(float w) {
		paint.setStrokeWidth(w);
	}

	/**
	 * ���׸��� �Լ�
	 * 
	 * @param x1	���� X��ǥ
	 * @param y1	���� Y��ǥ
	 * @param x2	�� X��ǥ
	 * @param y2	�� Y��ǥ
	 */
	public void paintLine(float x1, float y1, float x2, float y2) {
		canvas.drawLine(x1, y1, x2, y2, paint);
	}

	/**
	 * �簢�� �׸���
	 * 
	 * @param x	����X��ǥ
	 * @param y ����Y��ǥ
	 * @param width	�ʺ�
	 * @param height ����
	 */
	public void paintRect(float x, float y, float width, float height) {
		canvas.drawRect(x, y, x + width, y + height, paint);
	}
	
	/**
	 * ���� �׸��� �Լ�
	 * 
	 * @param x	����X��ǥ
	 * @param y	����Y��ǥ
	 * @param radius ������
	 */
	public void paintCircle(float x, float y, float radius) {
		canvas.drawCircle(x, y, radius, paint);
	}
	
	/**
	 * �ؽ�Ʈ �׸��� �Լ�
	 * 
	 * @param x	����X��ǥ
	 * @param y	����Y��ǥ
	 * @param text	�ؽ�Ʈ ����
	 */
	public void paintText(float x, float y, String text) {
		canvas.drawText(text, x, y, paint);		
	}
	
	/**
	 * �̹����� �׸��� �Լ�
	 * 
	 * @param x	����X��ǥ
	 * @param y	����Y��ǥ
	 */
	public void paintBitmap(Bitmap bit, float x, float y){
		canvas.drawBitmap(bit, x, y, null);
	}
	
	/**
	 * ��ü�� �׷��ִ� �Լ�
	 * 
	 * @param obj	object
	 * @param x	����X��ǥ
	 * @param y	����Y��ǥ
	 * @param rotation	ȸ����
	 * @param scale	ũ������ ��
	 */
	public void paintObj(ScreenObj obj, float x, float y, float rotation,
			float scale) {
		canvas.save();
		canvas.translate(x + obj.getWidth() / 2, y + obj.getHeight() / 2);
		canvas.rotate(rotation);
		canvas.scale(scale, scale);
		canvas.translate(-(obj.getWidth() / 2), -(obj.getHeight() / 2));
		obj.paint(this);
		canvas.restore();
	}

	/**
	 * Text�� �ʺ� �����ϴ� �Լ�
	 * 
	 * @param txt
	 * @return	The width of text
	 */
	public float getTextWidth(String txt) {
		//���ڿ��� width�� �����Ͽ� ����
		return paint.measureText(txt);
	}
	
	/**
	 * Get TextAscent
	 * 
	 * @return ascent
	 */
	public float getTextAsc() {
		return -paint.ascent();
	}

	/**
	 * Get TextDescent
	 * 
	 * @return descent
	 */
	public float getTextDesc() {
		return paint.descent();
	}
	
	/**
	 * Get TextLead
	 * 
	 * @return	0
	 */
	public float getTextLead() {
		return 0;
	}

	/**
	 * �۽�ũ�� ����
	 * 
	 * @param size	Size of text
	 */
	public void setFontSize(float size) {
		paint.setTextSize(size);
	}
}