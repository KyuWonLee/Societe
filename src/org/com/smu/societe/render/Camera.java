/*
 * Camera 클래스는 카메라 프리뷰를 담당하는 클래스가 아니라 
 * 3차원상의 카메라가 실제 바라보는 방향을 관리하는 클래스임
 */
package org.com.smu.societe.render;

/**
 * Camera 클래스는 카메라 프리뷰를 담당하는 클래스가 아니라 
 * 3차원상의 카메라가 실제 바라보는 방향을 관리하는 클래스임 
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
	 * @param width		카메라의 너비
	 * @param height	카메라의 높이
	 */
	public Camera(int width, int height) {
		this(width, height, true);
	}

	/**
	 * Construct
	 * 
	 * @param width		카메라의 너비
	 * @param height	카메라의 높이
	 * @param init		초기화 여부
	 */
	public Camera(int width, int height, boolean init) {
		this.width = width;
		this.height = height;

		transform.toIdentity();
		lco.set(0, 0, 0);

	}

	/**
	 * 카메라의 시야를 설정하는 함수
	 * 
	 * @param viewAngle	시야값
	 */
	public void setViewAngle(float viewAngle) {
		this.viewAngle = viewAngle;
		this.dist = (this.width / 2)
				/ (float) Math.tan(viewAngle / 2);
	}

	/**
	 * 카메라의 높이, 너비, 시야각을 세팅하는 함수
	 * 
	 * @param width		너비
	 * @param height	높이
	 * @param viewAngle	시야값
	 */
	public void setViewAngle(int width, int height, float viewAngle) {
		this.viewAngle = viewAngle;
		this.dist = (width / 2) / (float) Math.tan(viewAngle / 2);
	}

	/**
	 * camera 에 물체를 Projection 시켜주는 함수
	 * 
	 * @param orgPoint	원래의 좌표
	 * @param prjPoint	프로젝션된 좌표
	 * @param addX		X축으로 이동할 값
	 * @param addY		Y축으로 이동할 값
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
