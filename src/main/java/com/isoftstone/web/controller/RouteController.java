package com.isoftstone.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	 * 
	 * @param carId
	 * @return
	 */
	@RequestMapping(value = "/api/route/{carId}", method = RequestMethod.GET)
	public ResponseEntity<Route> getRoute(@PathVariable("carId") int carId) {
		Route route;

		route = routeDao.findById(carId);
		if (null == route) {
			return new ResponseEntity<Route>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Route>(route, HttpStatus.OK);
	}
	
	/**
	 * 删除指定id路线
	 * @param carId
	 * @return
	 */
	@RequestMapping(value = "/api/route/{carId}", method = RequestMethod.DELETE)
	public ResponseEntity<Route> deleteRoute(@PathVariable("carId") int carId) {
		
		if (null == routeDao.findById(carId)) {
			return new ResponseEntity<Route>(HttpStatus.NOT_FOUND);
		}
		
		if(routeDao.deleteById(carId)) {
			return new ResponseEntity<Route>(HttpStatus.OK);
		}
		
		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<Route>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 更新对应 carId 的路线信息
	 * 
	 * @param carId
	 * @param route
	 * @return
	 */
	@RequestMapping(value = "/api/route/{routeId}", method = RequestMethod.PUT)
	public ResponseEntity<Route> updateRoute(@PathVariable("routeId") int routeId, @RequestBody Route route) {

		// 检测对应编号的路线是否存在
		if (null == routeDao.findById(routeId)) {
			return new ResponseEntity<Route>(HttpStatus.NOT_FOUND);
		}

		// 更新路线信息
		if (routeDao.updateRoute(routeId, route)) {
			return new ResponseEntity<Route>(routeDao.findById(routeId), HttpStatus.OK);
		}

		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<Route>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 创建一条路线
	 * 
	 * @param route
	 * @return
	 */
	@RequestMapping(value = "/api/route", method = RequestMethod.POST)
	public ResponseEntity<Route> createRoute(@RequestBody Route route) {

		// 检测对应编号的车辆是否存在
		Car_inf carInf;
		carInf = new Car_dao().getcarByid(route.getCar().getId());
		if (null == carInf) {
			return new ResponseEntity<Route>(HttpStatus.NOT_FOUND);
		}

		// 创建路线
		if (routeDao.createRoute(route.getCar().getId(), route)) {
			return new ResponseEntity<Route>(route, HttpStatus.OK);
		}

		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<Route>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 获取所有路线
	 * 
	 * @return
	 */
	@RequestMapping(value = "/api/route", method = RequestMethod.GET)
	public ResponseEntity<List<Route>> getAllRoute(
			@RequestParam(value = "station", required = false) String staId) {

		// 根据站点反查路线
		if (null != staId) {
			return getRouteByStaId(Integer.valueOf(staId));
		}

		// 查询出所有路线
		List<Route> routeList = routeDao.findAll();
		if (null != routeList) {
			return new ResponseEntity<List<Route>>(routeList, HttpStatus.OK);
		}

		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<List<Route>>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 根据站点id查询出所有路线
	 * 
	 * @param staId
	 *            站点id
	 * @return
	 */
	private ResponseEntity<List<Route>> getRouteByStaId(int staId) {

		// 根据站点id查询路线
		List<Route> routeList = routeDao.findByStaId(staId);
		if (null != routeList) {
			return new ResponseEntity<List<Route>>(routeList, HttpStatus.OK);
		}

		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<List<Route>>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 改变某一路线的用车
	 * 
	 * @param routeId
	 *            路线id（原车辆id）
	 * @param carId
	 *            新的车辆id
	 * @return
	 */
	@RequestMapping(value = "/api/route/{routeId}/car/{carId}", method = RequestMethod.PATCH)
	public ResponseEntity<Route> changeCar(@PathVariable("routeId") int routeId, @PathVariable("carId") int carId) {

		// 查询路线和车辆是否都存在
		if (null == routeDao.findById(routeId) || null == new Car_dao().getcarByid(carId)) {
			return new ResponseEntity<Route>(HttpStatus.NOT_FOUND);
		}

		// 更换路线用车
		if (routeDao.changeCar(routeId, carId)) {
			return new ResponseEntity<Route>(HttpStatus.OK);
		}

		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<Route>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
