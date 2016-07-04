package com.isoftstone.web.pojo;

import java.sql.Date;

public class Car_inf {
	private int id;
	private String brand;
	private int seat;
	private Date logon;
	private Date dated;
	private String d_license;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public int getSeat() {
		return seat;
	}
	public void setSeat(int seat) {
		this.seat = seat;
	}
	public Date getLogon() {
		return logon;
	}
	public void setLogon(Date logon) {
		this.logon = logon;
	}
	public Date getDated() {
		return dated;
	}
	public void setDated(Date dated) {
		this.dated = dated;
	}
	
	public String getD_license() {
		return d_license;
	}
	public void setD_license(String d_license) {
		this.d_license = d_license;
	}
	

}
