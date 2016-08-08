package com.isoftstone.web.pojo;

import java.util.List;

public class Route {
	private int numOfEmp;  // 乘坐人数
	private double rateOfTake; // 乘坐率
	private RouteCar car;
	private List<RouteStation> stations;
	
	public int getNumOfEmp() {
		return numOfEmp;
	}
	public void setNumOfEmp(int numOfEmp) {
		this.numOfEmp = numOfEmp;
	}
	public double getRateOfTake() {
		return rateOfTake;
	}
	public void setRateOfTake(double rateOfTake) {
		this.rateOfTake = rateOfTake;
	}
	public RouteCar getCar() {
		return car;
	}
	public void setCar(RouteCar car) {
		this.car = car;
	}
	public List<RouteStation> getStations() {
		return stations;
	}
	public void setStations(List<RouteStation> stations) {
		this.stations = stations;
	}
	
	
}
