package com.isoftstone.web.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.isoftstone.web.algorithm.Cal;
import com.isoftstone.web.algorithm.Plan;
import com.isoftstone.web.pojo.Car_inf;
import com.isoftstone.web.pojo.Route;
import com.isoftstone.web.pojo.RouteCar;
import com.isoftstone.web.pojo.RoutePlan;
import com.isoftstone.web.pojo.RouteStation;
import com.isoftstone.web.pojo.Station;
import com.isoftstone.web.util.JdbcUtil;
import com.routematrix.pojo.Coordinate;
import com.routematrix.pojo.Result;
import com.webapi.ApiOp;

public class RouteDao {

	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet result = null;

	/**
	 * 通过 carId 查询线路信息
	 * 
	 * @param carId
	 * @return
	 */
	public Route findById(int carId) {

		Route route = new Route();

		// 数据库查询语句：查询对应车辆线路表
		String strSql = "SELECT * FROM car" + carId + " ORDER BY `order`";

		// 不存在对应 carId 的表，则返回 null
		if (!isRouteExist(carId)) {
			return null;
		}

		// 获取车辆信息
		Car_inf carInf = new Car_dao().getcarByid(carId);
		RouteCar car = getRouteCarByCarInf(carInf);
		route.setCar(car);
		
		// 获取站点信息
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			result = stmt.executeQuery();

			List<RouteStation> stationList = fetchStaData(result);
			calculateArrivalTime(stationList);
			
			route.setStations(stationList);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}

		return route;
	}

	/**
	 * 根据车辆信息对象（Car_inf）获取路线车辆对象（RouteCar），添加了当天的驾驶员信息
	 * @param carInf
	 * @return
	 */
	private RouteCar getRouteCarByCarInf(Car_inf carInf) {
		
		RouteCar routeCar = new RouteCar(carInf);
		
		String strSql = "SELECT * FROM `arrangement` WHERE Date=? AND c_id=?;";
		java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
		
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			stmt.setDate(1, date);
			stmt.setInt(2, routeCar.getId());
			result = stmt.executeQuery();

			// 存在对应 carId 的表，返回 true
			if (result.next()) {
				routeCar.setEName(result.getString("Ename"));
				routeCar.setEiden(result.getString("Eiden"));
				return routeCar;
			}
		} catch (SQLException e) {
			System.out.println("查询车辆当天驾驶员失败！");
			return null;
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		
		return null;
	}

	/**
	 * 计算站点的到站时间
	 * @param stationList
	 */
	private void calculateArrivalTime(List<RouteStation> stationList) {
		
		for (int i = 0, len = stationList.size(); i < len; i++) {
			List<String> arrival_time = new ArrayList<>();
			stationList.get(i).setArrival_time(arrival_time);
		}
		
		// 计算上班去公司时的到站时间，假设上班（到公司）时间9:00
		int curTime = 9 * 60;  // 把时间转成分钟
		int stopTime = 3;  // 假设每站停留的时间
		for (int i = 0, len = stationList.size(); i < len; i++) {
			RouteStation sta = stationList.get(i);
			List<String> arrival_time = sta.getArrival_time();
			int usedTime = sta.getUsed_time();
			curTime = curTime - usedTime - stopTime;
			arrival_time.add(timeToString(curTime));
		}
		
		// 假设下班（离开公司）时间17:00
		curTime = 17 * 60;
		for (int i = 0, len = stationList.size(); i < len; i++) {
			RouteStation sta = stationList.get(i);
			List<String> arrival_time = sta.getArrival_time();
			int usedTime = sta.getUsed_time();
			curTime = curTime + usedTime + stopTime;
			arrival_time.add(timeToString(curTime));
		}
		
	}

	/**
	 * 将时间转换成字符串（9 * 60  --->  9:00）
	 * @param time
	 * @return
	 */
	private String timeToString(int time) {
		
		return time / 60 + ":" + time % 60;
	}

	/**
	 * 是否存在对应 carId 的路线表
	 * 
	 * @param carId
	 * @return
	 */
	public boolean isRouteExist(int carId) {

		// 数据库查询语句：查询是否存在对应 carId 的路线表
		String strSql1 = "SELECT * FROM information_schema.TABLES WHERE TABLE_NAME=?";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql1);
			stmt.setString(1, "car" + carId);
			result = stmt.executeQuery();

			// 存在对应 carId 的表，返回 true
			if (result.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}

		return false;
	}

	/**
	 * 提取站点信息
	 * 
	 * @param result
	 * @return
	 */
	private List<RouteStation> fetchStaData(ResultSet result) {

		StationDao staDao = new StationDao();
		List<RouteStation> routeStationList = new ArrayList<>();

		try {
			while(result.next()) {
				RouteStation routeSta = new RouteStation();
				
				// 获取站点信息
				int staId = result.getInt("s_id");
				Station sta = staDao.getStaById(staId);
				int usedTime = result.getInt("time");
				
				// 获取站点人数
				int numOfEmp = getNumOfEmpByStaId(staId);
				
				routeSta.setS_id(sta.getS_id());
				routeSta.setS_name(sta.getS_name());
				routeSta.setLatitude(sta.getLatitude());
				routeSta.setLongitude(sta.getLongitude());
				routeSta.setNum_of_emp(numOfEmp);
				routeSta.setUsed_time(usedTime);
				
				routeStationList.add(routeSta);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		

		return routeStationList;
	}

	
	/**
	 * 获取站点对应的人数
	 * @param staId
	 * @return
	 */
	private int getNumOfEmpByStaId(int staId) {

		String strSql = "SELECT COUNT(e_id) AS counter FROM employee_station_copy WHERE s_id=" + staId;

		try {
			// 删除路线表
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			result = stmt.executeQuery();
			
			if(result.next()) {
				int counter = result.getInt("counter");
				return counter;
			}
		} catch (SQLException e) {
			System.out.println("查询站点人数失败！");
			return 0;
		} finally {
			JdbcUtil.close(null, stmt, conn);
		}
		
		return 0;
	}

	/**
	 * 更新路线
	 * 
	 * @param carId
	 * @param route
	 * @return
	 */
	public boolean updateRoute(int carId, Route curRoute) {
		// 先将路线原来的状态保存
		Route oldRoute = findById(carId);

		// 路线不存在
		if (null == oldRoute) {
			return createRoute(carId, curRoute);
		}

		// 数据库查询语句：添加站点信息
		String strSql = "INSERT INTO car" + carId + " (`s_id`) VALUES (?);";

		// 重构路线表，清空表记录，同时将自增字段计数归零
		if (!truncateRoute(carId)) {
			return false;
		}

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);

			// 遍历提交的站点信息
			List<RouteStation> listStation = curRoute.getStations();
			for (int i = 0; i < listStation.size(); i++) {
				RouteStation curStation = listStation.get(i);

				// 判断站点是否已经存在，不存在则创建
				// if (!staDao.isxyExist(curStation.getLongitude(),
				// curStation.getLatitude())) {
				// staDao.createStation(curStation);
				// }

				// 将站点插入当前线路
				stmt.setInt(1, curStation.getS_id());
				int counter = stmt.executeUpdate();
				if (counter != 1) {
					return false;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, stmt, conn);
		}

		// 更新当前路线新增站点的所属路线字段
		addCarIdToSta(carId, oldRoute, curRoute);

		// 更新当前路线删除站点的所属路线字段
		delCarIdFromSta(carId, oldRoute, curRoute);

		// 更新time字段
		updateRouteTime(carId);
		
		// 更新站点 s_car 数据
		// updateStationSCar();

		return true;
	}

	/**
	 * 清空路线表，同时重置自增字段计数
	 * 
	 * @param carId
	 */
	public boolean truncateRoute(int carId) {
		// 数据库查询语句：重构 carId 的路线表
		String strSql = "TRUNCATE TABLE car" + carId + ";";

		try {
			// 删除路线表
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			stmt.executeUpdate();
		} catch (SQLException e) {
			return false;
		} finally {
			JdbcUtil.close(null, stmt, conn);
		}

		return true;
	}

	/**
	 * 比较新旧路线之间站点变化，为删除的站点更新所属路线字段
	 * 
	 * @param carId
	 *            路线（车辆）id
	 * @param oldRoute
	 *            修改前的路线
	 * @param curRoute
	 *            即将修改成的路线
	 * @return 是否成功
	 */
	public boolean delCarIdFromSta(int carId, Route oldRoute, Route curRoute) {

		StationDao staDao = new StationDao();

		List<Integer> oldStation = fetchStationId(oldRoute.getStations());

		List<Integer> curStation = null;
		if (null != curRoute) {
			curStation = fetchStationId(curRoute.getStations());
		}

		// 找出路线中删除的站点，oldStation 中留下的站点就是更新路线删除的站点
		if (curStation != null && !oldStation.removeAll(curStation)) {
			return false;
		}

		// 分别为每一个删除的站点更新所属路线字段
		for (int i = 0, length = oldStation.size(); i < length; i++) {
			int staIdTemp = oldStation.get(i);
			staDao.delCarToSta(staIdTemp, carId + "");
		}

		return true;
	}

	/**
	 * 比较新旧路线之间站点变化，为新增的站点更新所属路线字段
	 * 
	 * @param carId
	 *            路线（车辆）id
	 * @param oldRoute
	 *            修改前的路线
	 * @param curRoute
	 *            即将修改成的路线
	 * @return 是否成功
	 */
	public boolean addCarIdToSta(int carId, Route oldRoute, Route curRoute) {

		StationDao staDao = new StationDao();

		List<Integer> oldStation = null;
		if (null != oldRoute) {
			oldStation = fetchStationId(oldRoute.getStations());
		}
		List<Integer> curStation = fetchStationId(curRoute.getStations());

		// 找出路线中新增的站点，curStation 中留下的站点就是更新路线新增的站点
		if (oldStation != null && !curStation.removeAll(oldStation)) {
			return false;
		}

		// 分别为每一个新增的站点更新所属路线字段
		for (int i = 0, length = curStation.size(); i < length; i++) {
			int staIdTemp = curStation.get(i);
			staDao.addCarToSta(staIdTemp, carId + "");
		}

		return true;
	}

	/**
	 * 从 List<Station> 中单独取出 id 构成一个 List
	 * 
	 * @param listSta
	 * @return
	 */
	public List<Integer> fetchStationId(List<RouteStation> listSta) {
		List<Integer> listStaId = new ArrayList<>();

		for (int i = 0, length = listSta.size(); i < length; i++) {
			listStaId.add(listSta.get(i).getS_id());
		}

		return listStaId;
	}

	/**
	 * 根据车id新增一条路线
	 * 
	 * @param carId
	 *            车辆id
	 * @param curRoute
	 *            要创建的路线信息
	 * @return 创建成功或失败
	 */
	public boolean createRoute(int carId, Route curRoute) {

		// 已存在路线
		if (findById(carId) != null) {
			return false;
		}

		// 新建一张路线表
		createRouteTable(carId);

		// 数据库查询语句：添加站点信息
		String strSql = "INSERT INTO car" + carId + " (`s_id`) VALUES (?);";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);

			// 遍历提交的站点信息
			List<RouteStation> listStation = curRoute.getStations();
			for (int i = 0; i < listStation.size(); i++) {
				RouteStation curStation = listStation.get(i);

				// 判断站点是否已经存在，不存在则创建
				// if (!staDao.isxyExist(curStation.getLongitude(),
				// curStation.getLatitude())) {
				// staDao.createStation(curStation);
				// }

				// 将站点插入当前线路
				stmt.setInt(1, curStation.getS_id());
				int counter = stmt.executeUpdate();
				if (counter != 1) {
					return false;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, stmt, conn);
		}

		// 更新当前路线新增站点的所属路线字段
		addCarIdToSta(carId, null, curRoute);

		// 更新路线表time字段
		updateRouteTime(carId);

		return true;
	}

	/**
	 * 根据 车辆id 创建一张路线表
	 * 
	 * @param carId
	 *            车辆id
	 * @return 创建路线表是否成功
	 */
	public boolean createRouteTable(int carId) {

		String sql = "CREATE TABLE `car" + carId + "` (`order` int(11) NOT NULL AUTO_INCREMENT,"
				+ "`s_id` int(11) DEFAULT NULL," + "`time` int(11) DEFAULT NULL," + "PRIMARY KEY (`order`)"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
		} catch (SQLException e) {
			return false;
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}

		return true;
	}

	/**
	 * 更新路线表time字段
	 * 
	 * @param carId
	 * @return
	 */
	public boolean updateRouteTime(int carId) {

		// 获取公司坐标
		StationDao staDao = new StationDao();
		Station company = staDao.getStaById(1);

		// 获取路线信息
		Route route = findById(carId);

		// 获取站点数据
		List<RouteStation> routeStationList = route.getStations();
		List<Station> staList = fetchStation(routeStationList);
		Station staFirst = staList.get(0);
		try {
			// 更新第一站点到公司之间的耗时
			Result result = ApiOp.getDistance(new Coordinate(company.getLatitude(), company.getLongitude()),
					new Coordinate(staFirst.getLatitude(), staFirst.getLongitude()));
			int minute = (int) (result.getDuration().getValue() / 60);
			// System.out.println(carId + " " + 1 + " " + minute);
			if (!updateRouteTimeOne(carId, 1, minute)) {
				return false;
			}

			// 更新其他站点之间的耗时
			for (int i = 1, len = staList.size(); i < len; i++) {
				result = ApiOp.getDistance(
						new Coordinate(staList.get(i - 1).getLatitude(), staList.get(i - 1).getLongitude()),
						new Coordinate(staList.get(i).getLatitude(), staList.get(i).getLongitude()));
				minute = (int) (result.getDuration().getValue() / 60);
				// System.out.println(carId + " " + (i + 1) + " " + minute);
				if (!updateRouteTimeOne(carId, i + 1, minute)) {
					return false;
				}
			}

		} catch (IOException e) {
			return false;
		}

		return true;
	}

	/**
	 * 根据提供的 RouteStationList 返回对应的 StationList
	 * @param routeStationList
	 * @return
	 */
	private List<Station> fetchStation(List<RouteStation> routeStationList) {
		StationDao staDao = new StationDao();
		List<Station> staList = new ArrayList<>();
		
		for (int i = 0, len = routeStationList.size(); i < len; i++) {
			int staId = routeStationList.get(i).getS_id();
			Station sta = staDao.getStaById(staId);
			staList.add(sta);
		}
		
		return staList;
	}

	/**
	 * 更新某一条路线的某一条记录的 time 字段
	 * 
	 * @param carId
	 * @param order
	 * @param time
	 * @return
	 */
	public boolean updateRouteTimeOne(int carId, int order, int time) {

		String strSql = "UPDATE `car" + carId + "` SET `time`=? WHERE (`order`=?) LIMIT 1";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			stmt.setInt(1, time);
			stmt.setInt(2, order);
			if (1 == stmt.executeUpdate()) {
				return true;
			}
		} catch (SQLException e) {
			return false;
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}

		return false;
	}

	/**
	 * 查询所有路线表的表名
	 * 
	 * @return 路线表表名组成的list
	 */
	public List<String> getRouteTableName() {

		List<String> tableNameList = new ArrayList<>();

		String strSql = "SELECT * FROM information_schema.TABLES WHERE TABLE_NAME LIKE 'car%' AND TABLE_NAME <> 'car_information'";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			result = stmt.executeQuery();
			while (result.next()) {
				String tableName = result.getString("TABLE_NAME");
				tableNameList.add(tableName);
			}
		} catch (SQLException e) {
			System.out.println("获取路线表名出错！");
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}

		return tableNameList;
	}

	/**
	 * 查询所有存在的路线
	 * 
	 * @return 路线构成的list
	 */
	public List<Route> findAll() {

		List<Route> routeList = new ArrayList<>();

		List<String> routeNameList = getRouteTableName();

		for (String routeName : routeNameList) {
			Route route = findById(Integer.valueOf(routeName.split("car")[1]));
			routeList.add(route);
		}

		return routeList;
	}

	/**
	 * 改变指定路线使用的车辆
	 * 
	 * @param routeId
	 *            路线id
	 * @param carId
	 *            车辆id
	 * @return
	 */
	public boolean changeCar(int routeId, int carId) {

		// 修改路线中涉及到的站点对应记录
		addCarIdToSta(carId, null, findById(routeId));
		delCarIdFromSta(routeId, findById(routeId), null);

		// 改变表名
		String strSql = "RENAME TABLE car" + routeId + " TO car" + carId;

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("路线改变车辆失败！");
			return false;
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}

		// updateStationSCar();

		return true;
	}

	
	public List<RoutePlan> planRoute() {
		StationDao staDao = new StationDao();
		Car_dao carDao = new Car_dao();
		
		// 获取公司坐标
		Station company = staDao.getStaById(1);
		double longitude = company.getLongitude();
		double latitude = company.getLatitude();
		
		// 规划路线，获取结果
		Cal cal = new Cal();
		List<Plan> planList = cal.calplan(longitude, latitude);
		
		// 从拿到的Plan列表中提取出前三个，并且组成易于使用RoutePlan的对象
		List<RoutePlan> routePlanList = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			Plan plan = planList.get(i);
			int[] cars = plan.getCar();
			List<int[]> routeIdList = plan.getRoute();
			
			// 构建 RoutePlan 对象
			RoutePlan routePlan = new RoutePlan();
			routePlan.setAverageDistance(plan.getJun());
			routePlan.setDistanceVariance(plan.getCha());
			routePlan.setChengjun(plan.getChengjun());
			
			// 遍历每一条路线，生成对应路线信息
			List<Route> routeList = new ArrayList<>();
			routePlan.setRoutes(routeList);
			for (int j = 0; j < cars.length; j++) {
				
				Route route = new Route();
				
				// 获取车辆信息
				int carId = cars[j];
				Car_inf carInf = carDao.getcarByid(carId);
				RouteCar car = new RouteCar(carInf);
				route.setCar(car);
				
				// 获取站点信息
				int[] staIds = routeIdList.get(j);
				if (null == staIds || 0 == staIds.length) {
					continue;
				}

				List<RouteStation> staList = new ArrayList<>();
				route.setStations(staList);
				
				// 遍历每一个站点，生成对应站点信息
				int[] manNum = plan.getMan();
				for (int k = 0; k < staIds.length; k++) {
					Station sta = staDao.getStaById(staIds[k]);
					RouteStation routeStation = new RouteStation(sta);
					routeStation.setNum_of_emp(manNum[k]);
					staList.add(routeStation);
				}
				routeList.add(route);
			}
			routePlanList.add(routePlan);
		}
		
		return routePlanList;
	}
	
	public static void main(String[] args) {

		RouteDao routeDao = new RouteDao();

		double x = 120.1541, y = 30.2778;
		Cal cal = new Cal();
		Plan plan = cal.calplan(x, y).get(0);

		int[] cars = plan.getCar();
		List<int[]> routeList = plan.getRoute();

		for (int i = 0; i < cars.length; i++) {
			int carId = cars[i];
			int[] staIds = routeList.get(i);
			if (null == staIds || 0 == staIds.length) {
				continue;
			}

			Route route = new Route();
			List<RouteStation> staList = new ArrayList<>();
			route.setStations(staList);
			for (int j = 0; j < staIds.length; j++) {
				RouteStation sta = new RouteStation();
				sta.setS_id(staIds[j]);
				staList.add(sta);
			}

			routeDao.updateRoute(carId, route);
		}
	}

	/**
	 * 查询站点对应的路线
	 * 
	 * @param staId
	 *            站点id
	 * @return 路线list
	 */
	public List<Route> findByStaId(int staId) {

		// 根据站点id查询经过站点的路线id
		StationDao staDao = new StationDao();
		Station sta = staDao.getStaById(staId);
		String strCarIds = sta.getS_car();
		String[] arrCarIds = strCarIds.split(",");

		// 根据路线id的列表取出所有路线
		List<Route> routeList = new ArrayList<>();
		for (String carId : arrCarIds) {
			Route route = findById(Integer.valueOf(carId));
			routeList.add(route);
		}

		return routeList;
	}

	/**
	 * 删除指定id路线
	 * 
	 * @param carId
	 *            路线id
	 * @return
	 */
	public boolean deleteById(int carId) {

		// 修改删除的站点的s_car数据
		if (!delCarIdFromSta(carId, findById(carId), null)) {
			return false;
		}

		// 删除路线表
		if (!deleteTableById(carId)) {
			return false;
		}

		return true;
	}

	/**
	 * 更新站点表s_car信息
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean updateStationSCar() {

		String strSql = "UPDATE `station_information_copy` SET `s_is_used`='0' WHERE (`s_car`='')";
		String strSql2 = "UPDATE `station_information_copy` SET `s_is_used`='1' WHERE (`s_car`<>'')";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			stmt.executeUpdate();
			stmt = conn.prepareStatement(strSql2);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("更新站点表s_car信息出错！");
			return false;
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}

		return true;
	}

	/**
	 * 删除指定id路线表
	 * 
	 * @param carId
	 * @return
	 */
	public boolean deleteTableById(int carId) {

		String strSql = "DROP TABLE `car" + carId + "`";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("删除路线表出错！");
			return false;
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}

		return true;
	}

}
