package com.isoftstone.web.pojo;

import java.util.List;

public class Route {
	private RouteCar car;
	private List<RouteStation> stations;
	
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
