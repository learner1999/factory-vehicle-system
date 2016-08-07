package com.isoftstone.web.pojo;

import java.sql.Date;

public class RouteCar {
	private int id;
	private String brand;
	private int seat;
	private Date logon;
	private Date dated;
	private String d_license;
	private String EName;
	private String Eiden;
	
	
	public RouteCar() {
		super();
	}
	
	public RouteCar(Car_inf carInf) {
		this.id = carInf.getId();
		this.brand = carInf.getBrand();
		this.seat = carInf.getSeat();
		this.logon = carInf.getLogon();
		this.dated = carInf.getDated();
		this.d_license = carInf.getD_license();
	}

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
	public String getEName() {
		return EName;
	}
	public void setEName(String eName) {
		EName = eName;
	}
	public String getEiden() {
		return Eiden;
	}
	public void setEiden(String eiden) {
		Eiden = eiden;
	}
	
	
}
