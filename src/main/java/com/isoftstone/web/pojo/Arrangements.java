package com.isoftstone.web.pojo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
public class Arrangements {
	
private Date date;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	private String mon;
	public String getMon() {
		return mon;
	}
	public void setMon(String mon) {
		this.mon = mon;
	}
	public List<Arrangement> ag;
	
	public List<Arrangement> getAg() {
		return ag;
	}
	public void setAg(List<Arrangement> ag) {
		this.ag = ag;
	}
}
