package com.isoftstone.web.pojo;

import java.util.List;

public class RoutePlan {

	private double averageDistance; // 平均路程
	private double distanceVariance; // 路程方差
	private double chengjun; // 平均乘坐率
	private List<com.isoftstone.web.pojo.Route> routes; // 路线数组
	
	
	public double getAverageDistance() {
		return averageDistance;
	}
	public void setAverageDistance(double averageDistance) {
		this.averageDistance = averageDistance;
	}
	public double getDistanceVariance() {
		return distanceVariance;
	}
	public void setDistanceVariance(double distanceVariance) {
		this.distanceVariance = distanceVariance;
	}
	public double getChengjun() {
		return chengjun;
	}
	public void setChengjun(double chengjun) {
		this.chengjun = chengjun;
	}
	public List<com.isoftstone.web.pojo.Route> getRoutes() {
		return routes;
	}
	public void setRoutes(List<com.isoftstone.web.pojo.Route> routes) {
		this.routes = routes;
	}
	
	
}
