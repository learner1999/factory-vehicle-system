package com.isoftstone.web.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.isoftstone.web.pojo.Station;
import com.isoftstone.web.util.JdbcUtil;

public class StationDao {
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet result = null;
	/*查询部分*/
	
	 /*获得所有的站点信息*/
	public List<Station> getAllStations()
	{
		List<Station> stationList = new ArrayList<>();
		String sql="select * from station_information";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			while (result.next()) {
				Station sta = new Station();
				sta.setS_id(result.getInt("s_id"));
				sta.setS_name(result.getString("s_name"));
				sta.setLongitude(result.getDouble("longitude"));
				sta.setLatitude(result.getDouble("latitude"));
				stationList.add(sta);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return stationList;
	}
	
	/*通过站点名字模糊搜索*/
	public List<Station> getStaByName(String name_part)
	{
		List<Station> stationList = new ArrayList<>();
		String sql="select * from station_information where s_name like '%"+name_part+"%'";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			while (result.next()) {
				Station sta = new Station();
				sta.setS_id(result.getInt("s_id"));
				sta.setS_name(result.getString("s_name"));
				sta.setLongitude(result.getDouble("longitude"));
				sta.setLatitude(result.getDouble("latitude"));
				stationList.add(sta);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return stationList;
	}
	
	/*通过id获取到对应的站点信息*/
	public Station getStaById(int id)
	{
		Station sta=new Station();
		String sql="select * from station_information where s_id="+id+"";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			while(result.next())
			{
				sta.setS_id(result.getInt("s_id"));
				sta.setS_name(result.getString("s_name"));
				sta.setLongitude(result.getDouble("longitude"));
				sta.setLatitude(result.getDouble("latitude"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return sta;
	}
	
	
	/*添加部分*/
	/*新增一个站点*/
	public boolean createStation(Station sta)
	{
		String sql="insert into station_information(s_name,longitude,latitude) values(?,?,?)";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			
			stmt.setString(1, sta.getS_name());
			stmt.setDouble(2, sta.getLongitude());
			stmt.setDouble(3, sta.getLatitude());
			int counter = stmt.executeUpdate();
			if (1 == counter) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, stmt, conn);
		}

		return false;
	}
	
	/*测试站点是否已经存在*/
	public boolean isStationExist(String s_name)
	{
		String sql = "select s_name from station_information where s_name='"+s_name+"'";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
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
	
	/*测试在这个坐标点上是否已经有站点了*/
	public boolean isxyExist(double x,double y)
	{
		String sql = "select longitude,latitude from station_information where longitude="+x+" and latitude="+y+"";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
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
	
	/*修改部分*/
	/*获得id和Station对象，通过id用新的数据替换掉原来的数据*/
	public boolean updateStaById(int id,Station staNow)
	{
		String sql = "update  station_information set s_name=?,longitude=?,latitude=? "
				+ " where s_id="+id+"";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, staNow.getS_name());
			stmt.setDouble(2, staNow.getLongitude());
			stmt.setDouble(3, staNow.getLatitude());
			int counter = stmt.executeUpdate();
			if (1 == counter) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, stmt, conn);
		}

		return false;
	}
	
	/*删除部分*/
	/*根据站点的id删除掉一个站点*/
	public boolean deleteSta(int id)
	{
		String sql = "delete from station_information where s_id="+id+"";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			int counter = stmt.executeUpdate();
			if (1 == counter) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, stmt, conn);
		}

		return false;
	}
	
}
