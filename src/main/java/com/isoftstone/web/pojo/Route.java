package com.isoftstone.web.pojo;

import java.util.List;

public class Route {
	private int carId;
	private List<Station> stations;
	private List<Integer> times;
	public List<Integer> getTimes() {
		return times;
	}
	public void setTimes(List<Integer> times) {
		this.times = times;
	}
	public int getCarId() {
		return carId;
	}
	public void setCarId(int carId) {
		this.carId = carId;
	}
	public List<Station> getStations() {
		return stations;
	}
	public void setStations(List<Station> stations) {
		this.stations = stations;
	}
}
