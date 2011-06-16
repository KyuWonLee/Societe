package org.com.smu.societe;

import org.com.smu.societe.render.Matrix;
import org.com.smu.societe.render.MixVector;
/**
 *
 * ARState Ŭ������ ���� Bearing, Pitch��(����)�� ���� ������ ������ Ŭ�����̴�.
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
	  * ���� Bearing ���� �����ϴ� �Լ�
	  *
	  * @return Bearing value
	  */
	public float getCurBearing() {
		return curBearing;
	}
	
	/**
	  * ���� Pitch ���� �����ϴ� �Լ�
	  *
	  * @return pitch value
	  */
	public float getCurPitch() {
		return curPitch;
	}

	/**
	  * ���޹��� ȸ����ķ� ������ Pitch(����)�� Bearing(����)�� �����
	  *
	  * @param rotationM ȸ�����
	  */
	public void calcPitchBearing(Matrix rotationM) {
	
		MixVector looking = new MixVector();
		//ȸ������� ��ġ����� ����İ� ����, ȸ������� ��������̱� ����
		rotationM.transpose();
		looking.set(1, 0, 0);
		looking.prod(rotationM);
		
		//������ x��� z�����θ� ����� �� ����(ī�޶�� y���� �߽����� rolling�ϱ� ����)
		this.curBearing = (int) (ARUtils.getAngle(0, 0, looking.x, looking.z)  + 360 ) % 360 ;
		
		rotationM.transpose();
		looking.set(0, 1, 0);
		looking.prod(rotationM);
		
		//����� y��� z�����θ� ����ؾ� ��(����� x���� �߽����� rolling�ϱ� ����)
		this.curPitch = -ARUtils.getAngle(0, 0, looking.y, looking.z);
	
	}

}