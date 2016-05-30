package com.isoftstone.web.dao;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

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
	
	/**
	 * 通过经纬度查找站点
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public Station getStaByPosition(double longitude, double latitude) {
		Station station = null;
		
		String sql = "select * from station_information where longitude=? AND latitude=?";
		
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setDouble(1, longitude);
			stmt.setDouble(2, latitude);
			result = stmt.executeQuery();
			if(result.next()) {
				Station sta = new Station();
				sta.setS_id(result.getInt("s_id"));
				sta.setS_name(result.getString("s_name"));
				sta.setLongitude(result.getDouble("longitude"));
				sta.setLatitude(result.getDouble("latitude"));
				station = sta;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return station;
	}
	
	public boolean addToExcel(List<Station> stalist,String path)
	{
		// 第一步，创建一个webbook，对应一个Excel文件  
        HSSFWorkbook wb = new HSSFWorkbook();  
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
        HSSFSheet sheet = wb.createSheet("站点信息表");  
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        HSSFRow row = sheet.createRow((int) 0);  
        // 第四步，创建单元格，并设置值表头 设置表头居中  
        HSSFCellStyle style = wb.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式  
  
        HSSFCell cell = row.createCell((short) 0);  
        cell.setCellValue("站点id");  
        cell.setCellStyle(style);  
        cell = row.createCell((short) 1);  
        cell.setCellValue("站点名称");  
        cell.setCellStyle(style);  
        cell = row.createCell((short) 2);  
        cell.setCellValue("经度");  
        cell.setCellStyle(style);  
        cell = row.createCell((short) 3);  
        cell.setCellValue("纬度");  
        cell.setCellStyle(style);   
  
        for (int i = 0; i < stalist.size(); i++)  
        {  
            row = sheet.createRow((int) i + 1);  
            Station sta = (Station) stalist.get(i);  
            // 第四步，创建单元格，并设置值  
            row.createCell((short) 0).setCellValue(sta.getS_id());  
            row.createCell((short) 1).setCellValue(sta.getS_name());  
            row.createCell((short) 2).setCellValue(sta.getLongitude());  
            row.createCell((short) 3).setCellValue(sta.getLatitude()); 
        }  
        // 第六步，将文件存到指定位置  
        try  
        {  
        	String name=path+"站点.xls";
            FileOutputStream fout = new FileOutputStream(name);  
            wb.write(fout);  
            fout.close();  
            return true;
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
        return false;
    }  
	
}
