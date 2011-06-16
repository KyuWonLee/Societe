package org.com.smu.societe.reality;

import org.com.smu.societe.render.MixVector;

import android.location.Location;

/**
 * ���� ��ġ�� ������ ��ü�� ���� ��ǥ�� �̿��Ͽ� ȭ�鿡 ǥ�õ� ��ǥ�� �������
 * ������ �浵�� ������ ���� ������ �Ͽ� ���� ���͸� ������ش�.
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
	 * ���� �浵�� �������ش�
	 * 
	 * @param latitude	����
	 * @param longitude	�浵
	 * @param altitude	��
	 */
	public void setTo(double latitude, double longitude, double altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	/**
	 * ����, �浵, ���� ���޹��� ������ ������
	 * @param pl	RealLocation(����, �浵, ��)
	 */
	public void setTo(RealLocation pl) {
		this.latitude = pl.latitude;
		this.longitude = pl.longitude;
		this.altitude = pl.altitude;
	}

	/**
	 * ����, �浵, ������ ���ڿ��� ������
	 * 
	 * @return	string 	����, �浵, ��
	 */
	@Override
	public String toString() {
		return "(lat=" + latitude + ", lng=" + longitude + ", alt=" + altitude + ")";
	}

	/**
	 * �������� ��� �Լ�
	 * 
	 * @return	latitude	����
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * �������� �����ϴ� �Լ�
	 * 
	 * @param latitude	����
	 */	
	public void setLatitude(double latitude) {		
		this.latitude = latitude;
	}

	/**
	 * �浵�� ��� �Լ�
	 * 
	 * @return	longitude	�浵
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * �浵�� �����ϴ� �Լ�
	 * 
	 * @param longitude		�浵
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * ������ ��� �Լ�
	 * 
	 * @return altitude		��
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * ������ �����ϴ� �Լ�
	 * 
	 * @param altitude 		��
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	/**
	 * ���� ���� ����, �浵�� ȸ������ �̿��Ͽ� ���޹��� ������ġ�� ��ü�� �Ҵ���
	 * 
	 * @param lat1Deg	������ ���� ȸ����
	 * @param lon1Deg	�浵�� ���� ȸ����
	 * @param bear		bear
	 * @param d			�Ÿ�
	 * @param dest		���� ��ü�� ��ġ ��ü
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
	 * ��ġ������ ���Ͱ����� ��ȯ���ִ� �Լ�
	 * 
	 * @param org	������ġ����
	 * @param gp	��ü�� ��ġ����
	 * @param v		������ ����
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
	 * ���Ͱ��� ��ġ������ ��ȯ�ϴ� �Լ�
	 * 
	 * @param v		��ȯ�� ���Ͱ�
	 * @param org	������ ��ġ��
	 * @param gp	������ ��ġ��
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
