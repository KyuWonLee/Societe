package org.com.smu.societe;

import org.com.smu.societe.render.Matrix;
import org.com.smu.societe.render.MixVector;
/**
 *
 * ARState 클래스는 현재 Bearing, Pitch값(상태)에 대한 정보를 가지는 클래스이다.
 *
 * @Project  	Societe
 * @File  		ARState.java
 * @Date  		2010-10-10
 * @Author  	ACE
 *  
 */
public class ARState {
	public static int NOT_STARTED = 0; 
	public static int PROCESSING = 1; 
	public static int READY = 2; 
	public static int DONE = 3; 

	int nextLStatus = ARState.NOT_STARTED;

	private float curBearing, curPitch;
	boolean detailsView = false;

	/**
	  * 현재 Bearing 값을 리턴하는 함수
	  *
	  * @return Bearing value
	  */
	public float getCurBearing() {
		return curBearing;
	}
	
	/**
	  * 현재 Pitch 값을 리턴하는 함수
	  *
	  * @return pitch value
	  */
	public float getCurPitch() {
		return curPitch;
	}

	/**
	  * 전달받은 회전행렬로 현재의 Pitch(기울기)와 Bearing(방향)을 계산함
	  *
	  * @param rotationM 회전행렬
	  */
	public void calcPitchBearing(Matrix rotationM) {
	
		MixVector looking = new MixVector();
		//회전행렬의 전치행렬은 역행렬과 같음, 회전행렬은 직교행렬이기 때문
		rotationM.transpose();
		looking.set(1, 0, 0);
		looking.prod(rotationM);
		
		//방향은 x축과 z축으로만 계산할 수 있음(카메라는 y축을 중심으로 rolling하기 때문)
		this.curBearing = (int) (ARUtils.getAngle(0, 0, looking.x, looking.z)  + 360 ) % 360 ;
		
		rotationM.transpose();
		looking.set(0, 1, 0);
		looking.prod(rotationM);
		
		//기울기는 y축과 z축으로만 계산해야 함(기울기는 x축을 중심으로 rolling하기 때문)
		this.curPitch = -ARUtils.getAngle(0, 0, looking.y, looking.z);
	
	}

}