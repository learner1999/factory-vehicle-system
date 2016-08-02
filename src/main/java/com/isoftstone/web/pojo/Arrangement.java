package com.isoftstone.web.pojo;

import java.sql.Date;

public class Arrangement {

	
	private int eid;
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
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public String getC_license() {
		return c_license;
	}
	public void setC_license(String c_license) {
		this.c_license = c_license;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	private String ename;
	private int cid;
	private String c_license;
	private Date date;
	private String eiden;
	public String getEiden() {
		return eiden;
	}
	public void setEiden(String eiden) {
		this.eiden = eiden;
	}
}
