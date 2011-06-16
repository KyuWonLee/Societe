package org.com.smu.societe;

import org.com.smu.societe.gui.PaintScreen;
import org.com.smu.societe.gui.ScreenObj;
import org.com.smu.societe.gui.TextObj;
import org.com.smu.societe.reality.RealLocation;
import org.com.smu.societe.render.Camera;
import org.com.smu.societe.render.MixVector;

import android.graphics.Bitmap;
import android.location.Location;
/**
 * Marker 클래스는 실제 오버레이를 해주는 클래스임
 * 실제 marker 의 카메라상의 투영된 좌표를 계산하고, 카메라뷰의 영역안에 들어오는지를 계산하여
 * marker 를 카메라에 오버레이 해줌
 * 
 * @Project Societe
 * @File  	Marker.java
 * @Date  	2010-10-10
 * @author 	ACE
 */
public class Marker {
	
	public String mOnPress;
	public Bitmap bit;
	public RealLocation mGeoLoc = new RealLocation();
	public int objectId;
	public String phone;
	public String build_picture;
	public String comforts;
	public String descript;
	public String tweet;

	boolean isVisible, isNear;

	public static MixVector signMarker = new MixVector();
	MixVector fixMarker = new MixVector();

	//Label txtLab = new Label();
	
	MixVector tmpa = new MixVector();
	MixVector tmpb = new MixVector();
	MixVector tmpc = new MixVector();
	
	public MixVector loc = new MixVector(0,0,0);
	MixVector origin = new MixVector(0, 0, 0);
	MixVector upV = new MixVector(0, 1, 0);

	/**
	 * 카메라에 투영된 marker 의 좌표 계산
	 * 
	 * @param originalPoint	
	 * @param viewCam	Camera
	 * @param addX	이동할 X 값
	 * @param addY	이동할 Y 값
	 */
	void cCMarker(MixVector originalPoint, Camera viewCam, float addX,
			float addY) {
		tmpa.set(originalPoint); 
		tmpa.add(loc); //현재 위치와 물체 위치를 벡터로 나타냄
		tmpa.sub(viewCam.lco); //camera 의 위치는 (0, 0, 0)으로 간주
		tmpa.prod(viewCam.transform); //최종회전행렬 곱함
		
		tmpc.set(upV); 
		tmpc.add(loc); //3
		tmpc.sub(viewCam.lco); //camera 의 위치는 (0, 0, 0)으로 간주
		tmpc.prod(viewCam.transform); //최종회전행렬 곱함

		viewCam.projectPoint(tmpa, tmpb, addX, addY); // 카메라에 투영된 좌표 구함
		signMarker.set(tmpb); 
		viewCam.projectPoint(tmpc, tmpb, addX, addY); // 카메라에 투영된 좌표 구함
		fixMarker.set(tmpb); 
		
	}
	
	/**
	 * 카메라영역에 marker 의 좌표가 들어오는지 계산
	 * 
	 * @param viewCam	Camera
	 */
	void calcV(Camera viewCam) {
		isVisible = false;

		if (signMarker.z < -1f) {	//카메라가 바로보는 방향이면 z 좌표값이 음수이기 때문
			isVisible = true;
		}
	}

	/**
	 * 현재 자신의 위치와 물체의 위치를 이용하여 loc 벡터를 업데이트해 감
	 * 
	 * @param curGPSFix	현재 자신의 위치
	 * @param time	시간
	 */
	void update(Location curGPSFix, long time) {
		RealLocation.convLocToVec(curGPSFix, mGeoLoc, loc);
	}

	/**
	 * marker 좌표와 카메라 시야에 들어오는지를 계산
	 * 
	 * @param viewCam	Camera
	 * @param addX	이동할 X 좌표
	 * @param addY	이동할 Y 좌표
	 */
	void calcPaint(Camera viewCam, float addX, float addY) {
		cCMarker(origin, viewCam, addX, addY);
		calcV(viewCam);

	}

	/**
	 * marker 의 투영된 좌표를 계산
	 * 
	 * @param viewCam	Camera
	 */
	void calcPaint(Camera viewCam) {
		cCMarker(origin, viewCam, 0, 0);
	}

	public String mText;
	TextObj textBlock;

	/**
	 * marker 를 오버레이해주는 함수
	 * 
	 * @param dw	화면에 그려줄 객체의 정보
	 */
	void draw(PaintScreen dw) {

		float maxHeight = Math.round(dw.getHeight() / 10f) + 1;

		//textBlock이 초기화되어지지 않았다면 초기화
		if (textBlock == null) {
			textBlock = new TextObj(mText, Math.round(maxHeight / 2f) + 1,
					160, dw);
		}

		//marker 가 카메라의 시야에 들어왔을 경우
		if (isVisible) {
			int t = 0;
			if(mText.length() < 5){
				t = 20;
			}
			
			//marker 를 그림
			dw.setStrokeWidth(1f);
			dw.setFill(true);
			dw.paintBitmap(bit, signMarker.x, signMarker.y);
			dw.paintText(signMarker.x + t, signMarker.y + 80, mText);

		}
	}
	/**
	 * 객체가 클릭되었는지를 검사하는 함수
	 * 
	 * @param x	클릭된 x좌표 값
	 * @param y	클릭된 y좌표 값
	 * @return	클릭여부를 boolean 값으로 리턴
	 */
	boolean isClickMarker(float x, float y){
		
		int rangeW = (bit.getWidth());
		int rangeH = (bit.getHeight());
		float minX = signMarker.x;
		float maxX = signMarker.x + rangeW;
		float minY = signMarker.y;
		float maxY = signMarker.y + rangeH;
		
		return (x >= minX && x <= maxX && y >= minY && y <= maxY);
		
	}
	
	/**
	 * isClickedMarker함수를 호출함. 클릭여부 검사를 위한 함수
	 * 
	 * @param x	클릭된 x 좌표 값
	 * @param y	클릭된 y 좌표 값
	 * @return	클릭여부 boolean 값을 리턴
	 */
	public boolean isClicked(float x, float y) {
		
		return isClickMarker(x, y);
	}

}

//label 형식으로 marker 를 생성해주는 클래스
class Label implements ScreenObj {
	float x, y, w, h;
	float width, height;
	ScreenObj obj;

	public void prepare(ScreenObj drawObj) {
		obj = drawObj;
		w = obj.getWidth();
		h = obj.getHeight();

		x = w / 2;
		y = 0;

		width = w * 2;
		height = h * 2;
	}

	public void paint(PaintScreen dw) {
		dw.paintObj(obj, x, y, 0, 1);
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
	
}