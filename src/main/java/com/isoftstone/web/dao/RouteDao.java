package com.isoftstone.web.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.isoftstone.web.pojo.Route;
import com.isoftstone.web.pojo.Station;
import com.isoftstone.web.util.JdbcUtil;

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

		StationDao staDao = new StationDao();

		// 数据库查询语句：添加站点信息
		String strSql = "INSERT INTO car" + carId + " (`s_id`) VALUES (?);";

		// 重构路线表，清空表记录，同时将自增字段计数归零
		if(!truncateRoute(carId)) {
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
				if (!staDao.isxyExist(curStation.getLongitude(), curStation.getLatitude())) {
					staDao.createStation(curStation);
				}

				// 获取站点 id
				Station station = staDao.getStaByPosition(curStation.getLongitude(), curStation.getLatitude());

				// 将站点插入当前线路
				stmt.setInt(1, station.getS_id());
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
	 * @param carId	路线（车辆）id
	 * @param oldRoute 修改前的路线
	 * @param curRoute 即将修改成的路线
	 * @return 是否成功
	 */
	public boolean delCarIdFromSta(int carId, Route oldRoute, Route curRoute) {
		
		StationDao staDao = new StationDao();
		
		List<Integer> oldStation = fetchStationId(oldRoute.getStations());
		List<Integer> curStation = fetchStationId(curRoute.getStations());
		
		
		// 找出路线中删除的站点，oldStation 中留下的站点就是更新路线删除的站点
		if(!oldStation.removeAll(curStation)) {
			return false;
		}
		
		System.out.println(oldStation);
		
		// 分别为每一个删除的站点更新所属路线字段
		for(int i = 0, length = oldStation.size(); i < length; i++) {
			int staIdTemp = oldStation.get(i);
			staDao.delCarToSta(staIdTemp, carId + "");
		}
		
		return true;
	}
	
	
	/**
	 * 比较新旧路线之间站点变化，为新增的站点更新所属路线字段
	 * @param carId	路线（车辆）id
	 * @param oldRoute 修改前的路线
	 * @param curRoute 即将修改成的路线
	 * @return 是否成功
	 */
	public boolean addCarIdToSta(int carId, Route oldRoute, Route curRoute) {
		
		StationDao staDao = new StationDao();
		
		List<Integer> oldStation = fetchStationId(oldRoute.getStations());
		List<Integer> curStation = fetchStationId(curRoute.getStations());
		
		// 找出路线中新增的站点，curStation 中留下的站点就是更新路线新增的站点
		if(!curStation.removeAll(oldStation)) {
			return false;
		}
		
		// 分别为每一个新增的站点更新所属路线字段
		for(int i = 0, length = curStation.size(); i < length; i++) {
			int staIdTemp = curStation.get(i);
			staDao.addCarToSta(staIdTemp, carId + "");
		}
		
		return true;
	}
	
	
	/**
	 * 从 List<Station> 中单独取出 id 构成一个 List
	 * @param listSta
	 * @return
	 */
	public List<Integer> fetchStationId(List<Station> listSta) {
		List<Integer> listStaId = new ArrayList<>();
		
		for(int i = 0, length = listSta.size(); i < length; i++) {
			listStaId.add(listSta.get(i).getS_id());
		}
		
		return listStaId;
	}

}
