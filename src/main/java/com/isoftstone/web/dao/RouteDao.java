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
	public boolean updateRoute(int carId, Route route) {

		StationDao staDao = new StationDao();

		// 数据库查询语句：添加站点信息
		String strSql = "INSERT INTO car" + carId + " (`s_id`) VALUES (?);";

		// // 删除路线表
		// deleteRoute(carId);
		//
		// // 创建路线表
		// createRoute(carId);

		// 重构路线表，清空表记录，同时将自增字段计数归零
		truncateRoute(carId);

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);

			List<Station> listStation = route.getStations();

			// 遍历提交的站点信息
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

		return true;
	}

	private void truncateRoute(int carId) {
		// 数据库查询语句：删除对应 carId 的路线表
		String strSql = "TRUNCATE TABLE car" + carId + ";";

		try {
			// 删除路线表
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, stmt, conn);
		}
	}

	/**
	 * 删除对应 carId 的路线表
	 * 
	 * @param carId
	 */
	private void deleteRoute(int carId) {

		// 数据库查询语句：删除对应 carId 的路线表
		String strSql = "DROP TABLE IF EXISTS car" + carId + ";";

		try {
			// 删除路线表
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, stmt, conn);
		}
	}

	/**
	 * 创建对应 carId 路线表
	 * 
	 * @param carId
	 */
	private void createRoute(int carId) {

		// 数据库查询语句：新建对应 carId 的路线表
		String strSql = "CREATE TABLE `car" + carId + "` (" + "`s_id`  int(7) NULL DEFAULT NULL ,"
				+ "`order`  int(11) NOT NULL AUTO_INCREMENT ," + "PRIMARY KEY (`order`) " + ") " + "ENGINE=InnoDB "
				+ "DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci " + ";";

		try {
			// 创建路线表
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, stmt, conn);
		}
	}

}
