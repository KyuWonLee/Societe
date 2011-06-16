package org.com.smu.societe.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
/**
 * PaintScreen 클래스는 DataInformView 클래스에서 Marker와 Radar를 
 * 그려주기 위한 클래스임.
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
	 * 체우기 속성 설정
	 * 
	 * @param fill 체울지 아닐지 여부의 boolean data
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
	 * 선그리기 함수
	 * 
	 * @param x1	시작 X좌표
	 * @param y1	시작 Y좌표
	 * @param x2	끝 X좌표
	 * @param y2	끝 Y좌표
	 */
	public void paintLine(float x1, float y1, float x2, float y2) {
		canvas.drawLine(x1, y1, x2, y2, paint);
	}

	/**
	 * 사각형 그리기
	 * 
	 * @param x	시작X좌표
	 * @param y 시작Y좌표
	 * @param width	너비
	 * @param height 높이
	 */
	public void paintRect(float x, float y, float width, float height) {
		canvas.drawRect(x, y, x + width, y + height, paint);
	}
	
	/**
	 * 원을 그리는 함수
	 * 
	 * @param x	시작X좌표
	 * @param y	시작Y좌표
	 * @param radius 반지름
	 */
	public void paintCircle(float x, float y, float radius) {
		canvas.drawCircle(x, y, radius, paint);
	}
	
	/**
	 * 텍스트 그리는 함수
	 * 
	 * @param x	시작X좌표
	 * @param y	시작Y좌표
	 * @param text	텍스트 내용
	 */
	public void paintText(float x, float y, String text) {
		canvas.drawText(text, x, y, paint);		
	}
	
	/**
	 * 이미지를 그리는 함수
	 * 
	 * @param x	시작X좌표
	 * @param y	시작Y좌표
	 */
	public void paintBitmap(Bitmap bit, float x, float y){
		canvas.drawBitmap(bit, x, y, null);
	}
	
	/**
	 * 객체를 그려주는 함수
	 * 
	 * @param obj	object
	 * @param x	시작X좌표
	 * @param y	시작Y좌표
	 * @param rotation	회전값
	 * @param scale	크기조절 값
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
	 * Text의 너비를 리턴하는 함수
	 * 
	 * @param txt
	 * @return	The width of text
	 */
	public float getTextWidth(String txt) {
		//문자열의 width를 측정하여 리턴
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
	 * 글시크기 설정
	 * 
	 * @param size	Size of text
	 */
	public void setFontSize(float size) {
		paint.setTextSize(size);
	}
}