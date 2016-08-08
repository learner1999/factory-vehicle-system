package com.isoftstone.web.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.isoftstone.web.pojo.Excel;
import com.isoftstone.web.util.JdbcUtil;

public class ExcelToDbDao {
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet result = null;
	private StationDao staDao=new StationDao();
	
	/***
	 * 给定日期获取这一天各站点的人员乘坐信息
	 * 
	 * @param now
	 * 		给定的日期
	 * @return 这一天各站点的人员乘坐信息
	 */
	public List<Excel> getCountByDay(String now)
	{
		List<Excel> eList=new ArrayList<Excel>();
		String sql="select CStationName,CCount from Record2016 where CDate=?";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, now);
			result = stmt.executeQuery();
			while (result.next()) {
				Excel excel=new Excel();
				excel.setStaName(result.getString("CStationName"));
				excel.setsCount(result.getInt("CCount"));
				eList.add(excel);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return eList;
	}
	/***
	 * 给定起始日期和结束日期，获取这一时间段各站点的总人员乘坐信息
	 * 
	 * @param first
	 * 		起始日期
	 * @param last
	 * 		结束日期
	 * @return 这一天各站点的人员乘坐信息
	 */
	public List<Excel> getCountByDays(String first,String last)
	{
		List<Excel> eList=new ArrayList<Excel>();
		String sql="select CStationName,sum(CCount) from Record2016 where CDate between ? and ? group by CStationName";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, first);
			stmt.setString(2, last);
			result = stmt.executeQuery();
			while (result.next()) {
				Excel excel=new Excel();
				excel.setStaName(result.getString("CStationName"));
				excel.setsCount(result.getInt("sum(CCount)"));
				eList.add(excel);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return eList;
	}
	
	
	/***
	 * 给定日期获取这一天各路线的人员乘坐信息
	 * 
	 * @param now
	 * 		给定的日期
	 * @return 这一天各路线的人员乘坐信息
	 */
	public List<Excel> getRouteCountByDay(String now)
	{
		List<Excel> eList=new ArrayList<Excel>();
		String sql="SELECT CRouteId, SUM(CCount) AS CCount FROM Record2016 WHERE CDate=? GROUP BY CRouteId";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, now);
			result = stmt.executeQuery();
			while (result.next()) {
				Excel excel=new Excel();
				excel.setStaName(routeIdToName(result.getInt("CRouteId")));
				excel.setsCount(result.getInt("CCount"));
				eList.add(excel);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return eList;
	}
	
	
	/***
	 * 给定起始日期和结束日期，获取这一时间段各路线的总人员乘坐信息
	 * 
	 * @param first
	 * 		起始日期
	 * @param last
	 * 		结束日期
	 * @return 这一天各路线的人员乘坐信息
	 */
	public List<Excel> getRouteCountByDays(String first,String last)
	{
		List<Excel> eList=new ArrayList<Excel>();
		String sql="SELECT CRouteId, SUM(CCount) AS CCount FROM Record2016 WHERE CDate BETWEEN ? AND ? GROUP BY CRouteId";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, first);
			stmt.setString(2, last);
			result = stmt.executeQuery();
			while (result.next()) {
				Excel excel=new Excel();
				excel.setStaName(routeIdToName(result.getInt("CRouteId")));
				excel.setsCount(result.getInt("CCount"));
				eList.add(excel);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return eList;
	}
	
	/**
	 * 根据路线id返回路线名
	 * @param routeId
	 * @return
	 */
	private String routeIdToName(int routeId) {
		int id = routeId % 100;
		
		return id + "号线";
	}
	
	/***
	 * 通过日期和站点id查询乘车人员名单
	 * @param date 要查询的日期
	 * @param sid 要查询的站点id
	 * @return 员工名单集合
	 */
	public List<String> getNameList(String date,int sid)
	{
		List<String> eList=new ArrayList<String>();
		String nameStr="";
		String sql="select CnameList from Record2016 where CDate=? and CStationId=?";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, date);
			stmt.setInt(2, sid);
			result = stmt.executeQuery();
			while (result.next()) {
				nameStr=result.getString("CnameList");
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		eList=staDao.getStaList(nameStr);
		return eList;
	}
	
	/***
	 * 通过日期和路线id查询乘车人员名单
	 * @param date 要查询的日期
	 * @param sid 要查询的路线id
	 * @return 员工名单集合
	 */
	public List<String> getNameRoute(String date,int rid)
	{
		List<String> all=new ArrayList<String>();
		List<String> eList=new ArrayList<String>();
		String sql="select CnameList from Record2016 where CDate=? and CRouteId=?";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, date);
			stmt.setInt(2, rid);
			result = stmt.executeQuery();
			while (result.next()) {
				String nameStr=result.getString("CnameList");
				eList.add(nameStr);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		String addStr="";
		for(int i=0;i<eList.size();i++)
		{
			addStr+=eList.get(i);
			if(i!=eList.size()-1)
			{
				addStr+=",";
			}
		}
		all=staDao.getStaList(addStr);
		return all;
	}

}
