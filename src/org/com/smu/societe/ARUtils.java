package org.com.smu.societe;

/**
*
*  ARUtils 클래스는 현재 angle과 거리, 화면내의 위치여부 등을 알아보기 위한 클래스임
*
*  @Project Societe
*  @File  	ARUtils.java
*  @Date  	2010-10-10
*  @Author  ACE
*
*/
class ARUtils {

	/**
	  * Radar 의 범위를 km 또는 m 의 단위로 환산해주는 함수
	  *
	  * @param float  변환할 거리값
	  * @return m 또는 km의 값을 문자열의 형태로 리턴
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
	  * Math.pow()는 거듭제곱을 나타내주는 함수
	  *
	  * @param float float
	  * @return front.back형식인 문자열을 리턴
	  */
	static String formatDec(float val, int dec) {
		//Math.pow()는 거듭제곱을 나타내주는 함수
		//10의 dec승을 구해줌
		int factor = (int) Math.pow(10, dec);

		int front = (int) (val);
		
		//Math.abs()는 절대값을 구해주는 메서드
		int back = (int) Math.abs(val * (factor)) % factor;

		return front + "." + back;
	}

	/**
	  * 현재 좌표가 화면 내에 위치하는지를 검사하는 함수
	  *
	  * @param float float float float float float
	  * @return boolean data, 시야 안에 위치했는지 여부
	  */
	public static boolean pointInside(float Point_x, float Point_y, float minimum_x,
		float minimum_y, float w, float h) {
		
		//w, h 는 화면의 width, height 를 의미함
		//해당 마커의 좌표가 minimum_x, minimum_y보단 크고
		//minimum_x+width, minimum_y+height 보단 작아야 화면 내에 위치함
		return (Point_x > minimum_x && Point_x < minimum_x + w && Point_y > minimum_y && Point_y < minimum_y + h);
	}

	/**
	  * 현재 view angle을 계산하는 함수
	  *
	  * @param float float float float float float
	  * @return data of current view angle 
	  */
	public static float getAngle(float center_x, float center_y, float post_x,
			float post_y) {
		//center_x는, center_y는 현재 카메라의 위치라고 생각하면 됨(여기선 0, 0 으로 간주할 것임)
		//카메라와 물체의 좌표사이의 벡터를 구함
		float tmpv_x = post_x - center_x;
		float tmpv_y = post_y - center_y;
		
		//카메라와 물체사이의 거리를 구함
		float d = (float) Math.sqrt(tmpv_x * tmpv_x + tmpv_y * tmpv_y);
		
		//sin값을 이용하여 각도를 구함
		float cos = tmpv_x / d;
		float angle = (float) Math.toDegrees(Math.acos(cos));

		//y좌표가 0보다 커야지만 화면에 표시해줘야 하기 때문
		angle = (tmpv_y < 0) ? angle * -1 : angle;

		return angle;
	}
}