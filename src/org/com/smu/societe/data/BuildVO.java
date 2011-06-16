package org.com.smu.societe.data;


public class BuildVO{
	private String name;
	private String marker;
	private double latitude;
	private double longitude;
	private double altitude;
	private String phone;
	private String build_picture;
	private String comforts;
	private String description;
	
	public BuildVO(String name, String marker, 
			double latitude, double longitude, double altitude,
			String phone, String build_picture, String comforts, String description){
		this.name = name;
		this.marker = marker;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.phone = phone;
		this.build_picture = build_picture;
		this.comforts = comforts;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getMarker() {
		return marker;
	}
	
	public void setMarker(String marker) {
		this.marker = marker;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getAltitude() {
		return altitude;
	}
	
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getBuild_picture() {
		return build_picture;
	}
	
	public void setBuild_picture(String buildPicture) {
		build_picture = buildPicture;
	}
	
	public String getComforts() {
		return comforts;
	}
	
	public void setComforts(String comforts) {
		this.comforts = comforts;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}
