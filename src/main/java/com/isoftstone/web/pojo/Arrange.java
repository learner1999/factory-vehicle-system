package com.isoftstone.web.pojo;

import java.sql.Date;

public class Arrange {
	private int drivers;//司机人数
	public int getDrivers() {
		return drivers;
	}
	public void setDrivers(int drivers) {
		this.drivers = drivers;
	}
	public int getArrers() {
		return arrers;
	}
	public void setArrers(int arrers) {
		this.arrers = arrers;
	}
	public int getArrs() {
		return arrs;
	}
	public void setArrs(int arrs) {
		this.arrs = arrs;
	}
	private int arrers;//组员数
	private int arrs;//组数
	private int d_id;//司机id（默认从一递增）
	public int getD_id() {
		return d_id;
	}
	public void setD_id(int d_id) {
		this.d_id = d_id;
	}
	
	
	
}
