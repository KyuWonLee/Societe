/*
 * Camera Ŭ������ ī�޶� �����並 ����ϴ� Ŭ������ �ƴ϶� 
 * 3�������� ī�޶� ���� �ٶ󺸴� ������ �����ϴ� Ŭ������
 */
package org.com.smu.societe.render;

/**
 * Camera Ŭ������ ī�޶� �����並 ����ϴ� Ŭ������ �ƴ϶� 
 * 3�������� ī�޶� ���� �ٶ󺸴� ������ �����ϴ� Ŭ������ 
 * @Project  Campus AR 
 * @File  Camera.java
 * @Date  2010-10-10 
 * @author App+
 *
 */
public class Camera {
	public static float DEFAULT_VIEW_ANGLE = (float) Math.toRadians(45);
	public int width;
	public int height;

	public Matrix transform = new Matrix();
	public MixVector lco = new MixVector();

	float viewAngle;
	float dist;

	/**
	 * Construct
	 * 
	 * @param width		ī�޶��� �ʺ�
	 * @param height	ī�޶��� ����
	 */
	public Camera(int width, int height) {
		this(width, height, true);
	}

	/**
	 * Construct
	 * 
	 * @param width		ī�޶��� �ʺ�
	 * @param height	ī�޶��� ����
	 * @param init		�ʱ�ȭ ����
	 */
	public Camera(int width, int height, boolean init) {
		this.width = width;
		this.height = height;

		transform.toIdentity();
		lco.set(0, 0, 0);

	}

	/**
	 * ī�޶��� �þ߸� �����ϴ� �Լ�
	 * 
	 * @param viewAngle	�þ߰�
	 */
	public void setViewAngle(float viewAngle) {
		this.viewAngle = viewAngle;
		this.dist = (this.width / 2)
				/ (float) Math.tan(viewAngle / 2);
	}

	/**
	 * ī�޶��� ����, �ʺ�, �þ߰��� �����ϴ� �Լ�
	 * 
	 * @param width		�ʺ�
	 * @param height	����
	 * @param viewAngle	�þ߰�
	 */
	public void setViewAngle(int width, int height, float viewAngle) {
		this.viewAngle = viewAngle;
		this.dist = (width / 2) / (float) Math.tan(viewAngle / 2);
	}

	/**
	 * camera �� ��ü�� Projection �����ִ� �Լ�
	 * 
	 * @param orgPoint	������ ��ǥ
	 * @param prjPoint	�������ǵ� ��ǥ
	 * @param addX		X������ �̵��� ��
	 * @param addY		Y������ �̵��� ��
	 */
	public void projectPoint(MixVector orgPoint, MixVector prjPoint, float addX,
			float addY) {
		prjPoint.x = dist * orgPoint.x / -orgPoint.z;
		prjPoint.y = dist * orgPoint.y / -orgPoint.z;
		prjPoint.z = orgPoint.z;
		prjPoint.x = prjPoint.x + addX + width / 2;
		prjPoint.y = -prjPoint.y + addY + height / 2;
	}
	
	@Override
	public String toString() {
		return "CAM(" + width + "," + height + ")";
	}
}
