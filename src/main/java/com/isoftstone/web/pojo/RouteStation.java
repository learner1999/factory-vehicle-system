package com.isoftstone.web.pojo;

import java.util.List;

public class RouteStation {
	private int s_id;
	private String s_name;
	private int num_of_emp;
	private int used_time;
	private List<String> arrival_time;
	
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
	public int getNum_of_emp() {
		return num_of_emp;
	}
	public void setNum_of_emp(int num_of_emp) {
		this.num_of_emp = num_of_emp;
	}
	public int getUsed_time() {
		return used_time;
	}
	public void setUsed_time(int used_time) {
		this.used_time = used_time;
	}
	public List<String> getArrival_time() {
		return arrival_time;
	}
	public void setArrival_time(List<String> arrival_time) {
		this.arrival_time = arrival_time;
	}
}
