package com.isoftstone.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.isoftstone.web.dao.Car_dao;
import com.isoftstone.web.dao.RouteDao;
import com.isoftstone.web.pojo.Car_inf;
import com.isoftstone.web.pojo.Route;

@RestController
public class RouteController {

	RouteDao routeDao = new RouteDao();
	
	
	/***
	 * 根据车辆 id，查询线路
	 * @param carId
	 * @return
	 */
	@RequestMapping(value = "/api/route/{carId}", method = RequestMethod.GET)
	public ResponseEntity<Route> getRoute(@PathVariable("carId") int carId) {
		Route route;
		
		route = routeDao.findById(carId);
		if(null == route) {
			return new ResponseEntity<Route>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Route>(route, HttpStatus.OK); 
	}
	
	/**
	 * 更新对应 carId 的路线信息
	 * @param carId
	 * @param route
	 * @return
	 */
	@RequestMapping(value = "/api/route/{carId}", method = RequestMethod.PUT)
	public ResponseEntity<Route> updateRoute(@PathVariable("carId") int carId, @RequestBody Route route) {
		Car_inf carInf;
		
		// 检测对应编号的车辆是否存在
		carInf = new Car_dao().getcarByid(carId);
		if(null == carInf) {
			return new ResponseEntity<Route>(HttpStatus.NOT_FOUND);
		}
		
		// 更新路线信息
		if(routeDao.updateRoute(carId, route)) {
			return new ResponseEntity<Route>(route, HttpStatus.OK);
		}
		
		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<Route>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
}
