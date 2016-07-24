package com.isoftstone.web.pojo;

public class Emlopee {
	private int eid;
	private String ename;
	private String epart;
	private int egroup;
	private int etime;
	private String EAddress;
	private String Eiden;
	
	
	public String getEAddress() {
		return EAddress;
	}
	public void setEAddress(String eAddress) {
		EAddress = eAddress;
	}
	public String getEiden() {
		return Eiden;
	}
	public void setEiden(String eiden) {
		Eiden = eiden;
	}
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
