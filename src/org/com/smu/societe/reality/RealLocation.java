package org.com.smu.societe.reality;

import org.com.smu.societe.render.MixVector;

import android.location.Location;

/**
 * 현재 위치를 가지고 객체의 실제 좌표를 이용하여 화면에 표시될 좌표를 계산해줌
 * 위도와 경도를 가지로 벡터 연산을 하여 최종 벡터를 계산해준다.
 * 
 * @Project  Campus AR 
 * @File  RealLocation.java
 * @Date  2010-10-10
 * @author App+
 *
 */
public class RealLocation {
	double latitude;
	double longitude;
	double altitude;

	public RealLocation() {

	}

	public RealLocation(RealLocation pl) {
		this.setTo(pl.latitude, pl.longitude, pl.altitude);
	}

	public RealLocation(double latitude, double longitude, double altitude) {
		this.setTo(latitude, longitude, altitude);
	}
	
	/**
	 * 위도 경도를 세팅해준다
	 * 
	 * @param latitude	위도
	 * @param longitude	경도
	 * @param altitude	고도
	 */
	public void setTo(double latitude, double longitude, double altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	/**
	 * 위도, 경도, 고도를 전달받은 값으로 세팅함
	 * @param pl	RealLocation(위도, 경도, 고도)
	 */
	public void setTo(RealLocation pl) {
		this.latitude = pl.latitude;
		this.longitude = pl.longitude;
		this.altitude = pl.altitude;
	}

	/**
	 * 위도, 경도, 고도값을 문자열로 리턴함
	 * 
	 * @return	string 	위도, 경도, 고도
	 */
	@Override
	public String toString() {
		return "(lat=" + latitude + ", lng=" + longitude + ", alt=" + altitude + ")";
	}

	/**
	 * 위도값을 얻는 함수
	 * 
	 * @return	latitude	위도
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * 위도값을 세팅하는 함수
	 * 
	 * @param latitude	위도
	 */	
	public void setLatitude(double latitude) {		
		this.latitude = latitude;
	}

	/**
	 * 경도를 얻는 함수
	 * 
	 * @return	longitude	경도
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * 경도를 세팅하는 함수
	 * 
	 * @param longitude		경도
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * 고도값을 얻는 함수
	 * 
	 * @return altitude		고도
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * 고도값을 세팅하는 함수
	 * 
	 * @param altitude 		고도
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	/**
	 * 기울기 값과 위도, 경도의 회전값을 이용하여 전달받은 실제위치의 객체에 할당함
	 * 
	 * @param lat1Deg	위도에 관한 회전값
	 * @param lon1Deg	경도에 관한 회전값
	 * @param bear		bear
	 * @param d			거리
	 * @param dest		실제 객체의 위치 객체
	 */
	public static void calcDestination(double lat1Deg, double lon1Deg,
			double bear, double d, RealLocation dest) {
		/** see http://en.wikipedia.org/wiki/Great-circle_distance */
		
		double brng = Math.toRadians(bear);
		double lat1 = Math.toRadians(lat1Deg);
		double lon1 = Math.toRadians(lon1Deg);
		double R = 6371.0 * 1000.0; 

		double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d / R)
				+ Math.cos(lat1) * Math.sin(d / R) * Math.cos(brng));
		double lon2 = lon1
				+ Math.atan2(Math.sin(brng) * Math.sin(d / R) * Math.cos(lat1),
						Math.cos(d / R) - Math.sin(lat1) * Math.sin(lat2));

		
		dest.setLatitude(Math.toDegrees(lat2));
		dest.setLongitude(Math.toDegrees(lat2));
		dest.setAltitude(Math.toDegrees(lon2));
	}

	/**
	 * 위치정보를 벡터값으로 변환해주는 함수
	 * 
	 * @param org	현재위치정보
	 * @param gp	물체의 위치정보
	 * @param v		최종의 벡터
	 */
	public static void convLocToVec(Location org, RealLocation gp,
			MixVector v) {
		float[] z = new float[1];
		z[0] = 0;
		Location.distanceBetween(org.getLatitude(), org.getLongitude(), gp
				.getLatitude(), org.getLongitude(), z);
		float[] x = new float[1];
		Location.distanceBetween(org.getLatitude(), org.getLongitude(), org
				.getLatitude(), gp.getLongitude(), x);
		double y = gp.getAltitude() - org.getAltitude();
		if (org.getLatitude() < gp.getLatitude())
			z[0] *= -1;
		if (org.getLongitude() > gp.getLongitude())
			x[0] *= -1;

		v.set(x[0], (float) y, z[0]);
	}

	/**
	 * 벡터값을 위치값으로 변환하는 함수
	 * 
	 * @param v		변환할 벡터값
	 * @param org	이전의 위치값
	 * @param gp	최종의 위치값
	 */
	public static void convertVecToLoc(MixVector v, Location org, Location gp) {
		double brngNS = 0, brngEW = 90;
		if (v.z > 0)
			brngNS = 180;
		if (v.x < 0)
			brngEW = 270;

		RealLocation tmp1Loc = new RealLocation();
		RealLocation tmp2Loc = new RealLocation();
		RealLocation.calcDestination(org.getLatitude(), org.getLongitude(), brngNS,
				Math.abs(v.z), tmp1Loc);
		RealLocation.calcDestination(tmp1Loc.getLatitude(), tmp1Loc.getLongitude(),
				brngEW, Math.abs(v.x), tmp2Loc);

		gp.setLatitude(tmp2Loc.getLatitude());
		gp.setLongitude(tmp2Loc.getLongitude());
		gp.setAltitude(org.getAltitude() + v.y);
	}
}
