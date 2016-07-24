package com.isoftstone.web.algorithm;

import java.util.*;

public class Plan {
	private List<int []> route;
	private int man[];
	private int car[];//用来存放车的。。暂时还没有用到
	private int deep[];
	private double distence[];
	private double lv[];
	private double jun;
	private double cha;
	private double chengjun;
	
	
	
	public double getChengjun() {
		return chengjun;
	}
	public void setChengjun(double chengjun) {
		this.chengjun = chengjun;
	}
	public double getJun() {
		return jun;
	}
	public void setJun(double jun) {
		this.jun = jun;
	}
	public double getCha() {
		return cha;
	}
	public void setCha(double cha) {
		this.cha = cha;
	}
	public double[] getLv() {
		return lv;
	}
	public void setLv(double[] lv) {
		this.lv = lv;
	}
	public double[] getDistence() {
		return distence;
	}
	public void setDistence(double[] distence) {
		this.distence = distence;
	}
	public int[] getDeep() {
		return deep;
	}
	public void setDeep(int[] deep) {
		this.deep = deep;
	}
	public List<int[]> getRoute() {
		return route;
	}
	public void setRoute(List<int[]> route) {
		this.route = route;
	}
	public int[] getMan() {
		return man;
	}
	public void setMan(int[] man) {
		this.man = man;
	}
	public int[] getCar() {
		return car;
	}
	public void setCar(int[] car) {
		this.car = car;
	}
	
}
