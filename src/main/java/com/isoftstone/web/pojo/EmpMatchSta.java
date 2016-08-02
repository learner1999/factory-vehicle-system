package com.isoftstone.web.pojo;

public class EmpMatchSta {
	private int e_id;
	private String e_name;
	private int s_id;
	private String s_name;
	private String e_address;
	private double e_x;
	private double e_y;
	private int used;
	
	
	
	public String getE_name() {
		return e_name;
	}
	public void setE_name(String e_name) {
		this.e_name = e_name;
	}
	public String getS_name() {
		return s_name;
	}
	public void setS_name(String s_name) {
		this.s_name = s_name;
	}
	public int getUsed() {
		return used;
	}
	public void setUsed(int used) {
		this.used = used;
	}
	public double getE_x() {
		return e_x;
	}
	public void setE_x(double e_x) {
		this.e_x = e_x;
	}
	public double getE_y() {
		return e_y;
	}
	public void setE_y(double e_y) {
		this.e_y = e_y;
	}
	
	public String getE_address() {
		return e_address;
	}
	public void setE_address(String e_address) {
		this.e_address = e_address;
	}
	public int getE_id() {
		return e_id;
	}
	public void setE_id(int e_id) {
		this.e_id = e_id;
	}
	public int getS_id() {
		return s_id;
	}
	public void setS_id(int s_id) {
		this.s_id = s_id;
	}
	
}
