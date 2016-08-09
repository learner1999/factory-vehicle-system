package com.isoftstone.web.pojo;

/**
 * 与员工相关联的站点及路线信息
 * 
 * @author learner
 *
 */
public class EmpRoute {
	/**
	 * 员工上车站点的信息，包括班车到站时间
	 */
	private RouteStation station;
	
	/**
	 * 员工乘坐的班车路线信息，包含了车辆信息，以及所有站点信息
	 */
	private Route route;

	public RouteStation getStation() {
		return station;
	}

	public void setStation(RouteStation station) {
		this.station = station;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}
}
