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
import com.isoftstone.web.pojo.Route;
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

		// 存在对应 carId 的表，则获取表中内容
		route.setCarId(carId);
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			result = stmt.executeQuery();

			List<Station> listStation = new ArrayList<>();
			while (result.next()) {
				Station sta = fetchStaData(result);
				listStation.add(sta);
			}
			route.setStations(listStation);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}

		return route;
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
	private Station fetchStaData(ResultSet result) {

		StationDao staDao = new StationDao();

		try {
			int staId = result.getInt("s_id");
			return staDao.getStaById(staId);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
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
		if(null == oldRoute) {
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
			List<Station> listStation = curRoute.getStations();
			for (int i = 0; i < listStation.size(); i++) {
				Station curStation = listStation.get(i);

				// 判断站点是否已经存在，不存在则创建
//				if (!staDao.isxyExist(curStation.getLongitude(), curStation.getLatitude())) {
//					staDao.createStation(curStation);
//				}

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
		List<Integer> curStation = fetchStationId(curRoute.getStations());

		// 找出路线中删除的站点，oldStation 中留下的站点就是更新路线删除的站点
		if (!oldStation.removeAll(curStation)) {
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
		if(null != oldRoute) {
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
	public List<Integer> fetchStationId(List<Station> listSta) {
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
			List<Station> listStation = curRoute.getStations();
			for (int i = 0; i < listStation.size(); i++) {
				Station curStation = listStation.get(i);

				// 判断站点是否已经存在，不存在则创建
//				if (!staDao.isxyExist(curStation.getLongitude(), curStation.getLatitude())) {
//					staDao.createStation(curStation);
//				}

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
	
	
	public boolean updateRouteTime(int carId) {
		
		// 获取公司坐标
		StationDao staDao = new StationDao();
		Station company = staDao.getStaById(1);
		
		// 获取路线信息
		Route route = findById(carId);
		
		// 获取站点数据
		List<Station> staList = route.getStations();
		Station staFirst = staList.get(0);
		try {
			// 更新第一站点到公司之间的耗时
			Result result = ApiOp.getDistance(new Coordinate(company.getLatitude(), company.getLongitude()),
					new Coordinate(staFirst.getLatitude(), staFirst.getLongitude()));
			int minute = (int)(result.getDuration().getValue() / 60);
			// System.out.println(carId + " " + 1 + " " + minute);
			if(!updateRouteTimeOne(carId, 1, minute)) {
				return false;
			}
			
			// 更新其他站点之间的耗时
			for(int i = 1, len = staList.size(); i < len; i++) {
				result = ApiOp.getDistance(
						new Coordinate(staList.get(i - 1).getLatitude(), staList.get(i - 1).getLongitude()),
						new Coordinate(staList.get(i).getLatitude(), staList.get(i).getLongitude()));
				minute = (int)(result.getDuration().getValue() / 60);
				// System.out.println(carId + " " + (i + 1) + " " + minute);
				if(!updateRouteTimeOne(carId, i + 1, minute)) {
					return false;
				}
			}
			
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * 更新某一条路线的某一条记录的 time 字段
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
			if(1 == stmt.executeUpdate()) {
				return true;
			}
		} catch (SQLException e) {
			return false;
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		
		return false;
	}

	
	public static void main(String[] args) {
		
		RouteDao routeDao = new RouteDao();
		
		double x=120.1541,y=30.2778;
		Cal cal = new Cal();
		Plan plan = cal.calplan(x, y);
		
		int[] cars = plan.getCar();
		List<int[]> routeList = plan.getRoute();
		
		for(int i = 0; i < cars.length; i++) {
			int carId = cars[i];
			int[] staIds = routeList.get(i);
			if(null == staIds || 0 == staIds.length) {
				continue;
			}
			
			Route route = new Route();
			List<Station> staList = new ArrayList<>();
			route.setStations(staList);
			for(int j = 0; j < staIds.length; j++) {
				Station sta = new Station();
				sta.setS_id(staIds[j]);
				staList.add(sta);
			}
			
			routeDao.updateRoute(carId, route);
			routeDao.updateRouteTime(carId);
		}
	}

}
