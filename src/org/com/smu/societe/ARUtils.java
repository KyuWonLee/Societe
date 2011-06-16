package org.com.smu.societe;

/**
*
*  ARUtils Ŭ������ ���� angle�� �Ÿ�, ȭ�鳻�� ��ġ���� ���� �˾ƺ��� ���� Ŭ������
*
*  @Project Societe
*  @File  	ARUtils.java
*  @Date  	2010-10-10
*  @Author  ACE
*
*/
class ARUtils {

	/**
	  * Radar �� ������ km �Ǵ� m �� ������ ȯ�����ִ� �Լ�
	  *
	  * @param float  ��ȯ�� �Ÿ���
	  * @return m �Ǵ� km�� ���� ���ڿ��� ���·� ����
	  */
	public static String formatDist(float meters) {
		if (meters < 1000) {
			return ((int) meters) + "m";
		} else if (meters < 10000) {
			return formatDec(meters / 1000f, 1) + "km";
		} else {
			return ((int) (meters / 1000f)) + "km";
		}
	}

	/**
	  * Math.pow()�� �ŵ������� ��Ÿ���ִ� �Լ�
	  *
	  * @param float float
	  * @return front.back������ ���ڿ��� ����
	  */
	static String formatDec(float val, int dec) {
		//Math.pow()�� �ŵ������� ��Ÿ���ִ� �Լ�
		//10�� dec���� ������
		int factor = (int) Math.pow(10, dec);

		int front = (int) (val);
		
		//Math.abs()�� ���밪�� �����ִ� �޼���
		int back = (int) Math.abs(val * (factor)) % factor;

		return front + "." + back;
	}

	/**
	  * ���� ��ǥ�� ȭ�� ���� ��ġ�ϴ����� �˻��ϴ� �Լ�
	  *
	  * @param float float float float float float
	  * @return boolean data, �þ� �ȿ� ��ġ�ߴ��� ����
	  */
	public static boolean pointInside(float Point_x, float Point_y, float minimum_x,
		float minimum_y, float w, float h) {
		
		//w, h �� ȭ���� width, height �� �ǹ���
		//�ش� ��Ŀ�� ��ǥ�� minimum_x, minimum_y���� ũ��
		//minimum_x+width, minimum_y+height ���� �۾ƾ� ȭ�� ���� ��ġ��
		return (Point_x > minimum_x && Point_x < minimum_x + w && Point_y > minimum_y && Point_y < minimum_y + h);
	}

	/**
	  * ���� view angle�� ����ϴ� �Լ�
	  *
	  * @param float float float float float float
	  * @return data of current view angle 
	  */
	public static float getAngle(float center_x, float center_y, float post_x,
			float post_y) {
		//center_x��, center_y�� ���� ī�޶��� ��ġ��� �����ϸ� ��(���⼱ 0, 0 ���� ������ ����)
		//ī�޶�� ��ü�� ��ǥ������ ���͸� ����
		float tmpv_x = post_x - center_x;
		float tmpv_y = post_y - center_y;
		
		//ī�޶�� ��ü������ �Ÿ��� ����
		float d = (float) Math.sqrt(tmpv_x * tmpv_x + tmpv_y * tmpv_y);
		
		//sin���� �̿��Ͽ� ������ ����
		float cos = tmpv_x / d;
		float angle = (float) Math.toDegrees(Math.acos(cos));

		//y��ǥ�� 0���� Ŀ������ ȭ�鿡 ǥ������� �ϱ� ����
		angle = (tmpv_y < 0) ? angle * -1 : angle;

		return angle;
	}
}