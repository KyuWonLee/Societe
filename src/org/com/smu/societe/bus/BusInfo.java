package org.com.smu.societe.bus;

public class BusInfo{
	private String busNumber;
	private int time;
	private int count;
	private String name;

	public BusInfo(String busNumber, int time,
			int count, String name) {
		// TODO Auto-generated constructor stub
		this.busNumber = busNumber;
		this.time = time;
		this.count = count;
		this.name = name;
	}

	public String getBusNumber() {
		return busNumber;
	}

	public void setBusNumber(String busNumber) {
		this.busNumber = busNumber;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}