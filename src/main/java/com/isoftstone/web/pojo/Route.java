package com.isoftstone.web.pojo;

import java.util.List;

public class Route {
	private Car_inf car;
	private List<RouteStation> stations;
	
	public Car_inf getCar() {
		return car;
	}
	public void setCar(Car_inf car) {
		this.car = car;
	}
	public List<RouteStation> getStations() {
		return stations;
	}
	public void setStations(List<RouteStation> stations) {
		this.stations = stations;
	}
	
	
}
