package com.isoftstone.web.pojo;

public class Station {
	private int s_id;
	private String s_name;
	private String s_address;
	private double longitude;
	private double latitude;
	private String s_car;
	private int s_is_used;
	
	public int getS_id() {
		return s_id;
	}
	public void setS_id(int s_id) {
		this.s_id = s_id;
	}
	public String getS_name() {
		return s_name;
	}
	public void setS_name(String s_name) {
		this.s_name = s_name;
	}
	public String getS_address() {
		return s_address;
	}
	public void setS_address(String s_address) {
		this.s_address = s_address;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getS_car() {
		return s_car;
	}
	public void setS_car(String s_car) {
		this.s_car = s_car;
	}
	public int getS_is_used() {
		return s_is_used;
	}
	public void setS_is_used(int s_is_used) {
		this.s_is_used = s_is_used;
	}
}
