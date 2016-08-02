package com.isoftstone.web.pojo;

import java.sql.Date;
import java.sql.Time;

public class Emlopee {
	private int eid;
	private String ename;
	private String epart;
	private int egroup;
	private int etime;
	private String eiden;
	public String getEiden() {
		return eiden;
	}
	public void setEiden(String eiden) {
		this.eiden = eiden;
	}
	private String station;
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	private String address;
	
	public int getEid() {
		return eid;
	}
	public void setEid(int eid) {
		this.eid = eid;
	}
	public String getEname() {
		return ename;
	}
	public void setEname(String ename) {
		this.ename = ename;
	}
	public String getEpart() {
		return epart;
	}
	public void setEpart(String epart) {
		this.epart = epart;
	}
	public int getEgroup() {
		return egroup;
	}
	public void setEgroup(int egroup) {
		this.egroup = egroup;
	}
	public int getEtime() {
		return etime;
	}
	public void setEtime(int etime) {
		this.etime = etime;
	}
	
}
