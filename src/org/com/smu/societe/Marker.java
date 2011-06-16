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
 * Marker Ŭ������ ���� �������̸� ���ִ� Ŭ������
 * ���� marker �� ī�޶���� ������ ��ǥ�� ����ϰ�, ī�޶���� �����ȿ� ���������� ����Ͽ�
 * marker �� ī�޶� �������� ����
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
	 * ī�޶� ������ marker �� ��ǥ ���
	 * 
	 * @param originalPoint	
	 * @param viewCam	Camera
	 * @param addX	�̵��� X ��
	 * @param addY	�̵��� Y ��
	 */
	void cCMarker(MixVector originalPoint, Camera viewCam, float addX,
			float addY) {
		tmpa.set(originalPoint); 
		tmpa.add(loc); //���� ��ġ�� ��ü ��ġ�� ���ͷ� ��Ÿ��
		tmpa.sub(viewCam.lco); //camera �� ��ġ�� (0, 0, 0)���� ����
		tmpa.prod(viewCam.transform); //����ȸ����� ����
		
		tmpc.set(upV); 
		tmpc.add(loc); //3
		tmpc.sub(viewCam.lco); //camera �� ��ġ�� (0, 0, 0)���� ����
		tmpc.prod(viewCam.transform); //����ȸ����� ����

		viewCam.projectPoint(tmpa, tmpb, addX, addY); // ī�޶� ������ ��ǥ ����
		signMarker.set(tmpb); 
		viewCam.projectPoint(tmpc, tmpb, addX, addY); // ī�޶� ������ ��ǥ ����
		fixMarker.set(tmpb); 
		
	}
	
	/**
	 * ī�޶󿵿��� marker �� ��ǥ�� �������� ���
	 * 
	 * @param viewCam	Camera
	 */
	void calcV(Camera viewCam) {
		isVisible = false;

		if (signMarker.z < -1f) {	//ī�޶� �ٷκ��� �����̸� z ��ǥ���� �����̱� ����
			isVisible = true;
		}
	}

	/**
	 * ���� �ڽ��� ��ġ�� ��ü�� ��ġ�� �̿��Ͽ� loc ���͸� ������Ʈ�� ��
	 * 
	 * @param curGPSFix	���� �ڽ��� ��ġ
	 * @param time	�ð�
	 */
	void update(Location curGPSFix, long time) {
		RealLocation.convLocToVec(curGPSFix, mGeoLoc, loc);
	}

	/**
	 * marker ��ǥ�� ī�޶� �þ߿� ���������� ���
	 * 
	 * @param viewCam	Camera
	 * @param addX	�̵��� X ��ǥ
	 * @param addY	�̵��� Y ��ǥ
	 */
	void calcPaint(Camera viewCam, float addX, float addY) {
		cCMarker(origin, viewCam, addX, addY);
		calcV(viewCam);

	}

	/**
	 * marker �� ������ ��ǥ�� ���
	 * 
	 * @param viewCam	Camera
	 */
	void calcPaint(Camera viewCam) {
		cCMarker(origin, viewCam, 0, 0);
	}

	public String mText;
	TextObj textBlock;

	/**
	 * marker �� �����������ִ� �Լ�
	 * 
	 * @param dw	ȭ�鿡 �׷��� ��ü�� ����
	 */
	void draw(PaintScreen dw) {

		float maxHeight = Math.round(dw.getHeight() / 10f) + 1;

		//textBlock�� �ʱ�ȭ�Ǿ����� �ʾҴٸ� �ʱ�ȭ
		if (textBlock == null) {
			textBlock = new TextObj(mText, Math.round(maxHeight / 2f) + 1,
					160, dw);
		}

		//marker �� ī�޶��� �þ߿� ������ ���
		if (isVisible) {
			int t = 0;
			if(mText.length() < 5){
				t = 20;
			}
			
			//marker �� �׸�
			dw.setStrokeWidth(1f);
			dw.setFill(true);
			dw.paintBitmap(bit, signMarker.x, signMarker.y);
			dw.paintText(signMarker.x + t, signMarker.y + 80, mText);

		}
	}
	/**
	 * ��ü�� Ŭ���Ǿ������� �˻��ϴ� �Լ�
	 * 
	 * @param x	Ŭ���� x��ǥ ��
	 * @param y	Ŭ���� y��ǥ ��
	 * @return	Ŭ�����θ� boolean ������ ����
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
	 * isClickedMarker�Լ��� ȣ����. Ŭ������ �˻縦 ���� �Լ�
	 * 
	 * @param x	Ŭ���� x ��ǥ ��
	 * @param y	Ŭ���� y ��ǥ ��
	 * @return	Ŭ������ boolean ���� ����
	 */
	public boolean isClicked(float x, float y) {
		
		return isClickMarker(x, y);
	}

}

//label �������� marker �� �������ִ� Ŭ����
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