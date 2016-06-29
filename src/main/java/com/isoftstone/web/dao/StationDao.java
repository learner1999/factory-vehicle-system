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

	/***
	 * 查询数据库中所有的站点信息
	 * 
	 * @return 所有站点信息
	 */
	public List<Station> getAllStations() {
		List<Station> stationList = new ArrayList<>();
		String sql = "select * from station_information";
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
				sta.setS_car(result.getString("s_car"));
				stationList.add(sta);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return stationList;
	}

	/***
	 * 通过站点名字模糊搜索
	 * 
	 * @param name_part
	 *            模糊查询的字段
	 * @return 模糊搜索的查询结果
	 */
	public List<Station> getStaByName(String name_part) {
		List<Station> stationList = new ArrayList<>();
		String sql = "select * from station_information where s_name like ?";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "%" + name_part + "%");
			result = stmt.executeQuery();
			while (result.next()) {
				Station sta = new Station();
				sta.setS_id(result.getInt("s_id"));
				sta.setS_name(result.getString("s_name"));
				sta.setLongitude(result.getDouble("longitude"));
				sta.setLatitude(result.getDouble("latitude"));
				sta.setS_car(result.getString("s_car"));
				stationList.add(sta);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return stationList;
	}

	/***
	 * 通过id获取到对应的站点信息
	 * 
	 * @param id
	 *            站点id
	 * @return 查询结果
	 */
	public Station getStaById(int id) {
		Station sta = new Station();
		String sql = "select * from station_information where s_id=?";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			result = stmt.executeQuery();
			while (result.next()) {
				sta.setS_id(result.getInt("s_id"));
				sta.setS_name(result.getString("s_name"));
				sta.setLongitude(result.getDouble("longitude"));
				sta.setLatitude(result.getDouble("latitude"));
				sta.setS_car(result.getString("s_car"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return sta;
	}

	/***
	 * 新增一个站点
	 * 
	 * @param sta
	 *            新增的站点对象
	 * @return 是否增加成功
	 */
	public boolean createStation(Station sta) {
		String sql = "insert into station_information(s_name,longitude,latitude) values(?,?,?)";
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

	/***
	 * 测试站点是否已经存在
	 * 
	 * @param s_name
	 *            查询的站点名称
	 * @return 是否存在
	 */
	public boolean isStationExist(String s_name) {
		String sql = "select s_name from station_information where s_name=?";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, s_name);
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

	/***
	 * 测试在这个坐标点上是否已经有站点了
	 * 
	 * @param x,y
	 *            经度，纬度
	 * @return 是否存在
	 */
	public boolean isxyExist(double x, double y) {
		String sql = "select longitude,latitude from station_information where longitude=? and latitude=?";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setDouble(1, x);
			stmt.setDouble(2, y);
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

	/***
	 *  获得id和Station对象，通过id用新的数据替换掉原来的数据
	 * 
	 * @param id,staNow
	 *            要修改的站点id，修改后的站点对象
	 * @return 是否修改成功
	 */
	public boolean updateStaById(int id, Station staNow) {
		String sql = "update  station_information set s_name=?,longitude=?,latitude=? "
				+ " where s_id=?";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, staNow.getS_name());
			stmt.setDouble(2, staNow.getLongitude());
			stmt.setDouble(3, staNow.getLatitude());
			stmt.setInt(4, id);
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

	/***
	 * 根据站点的id删除掉一个站点
	 * 
	 * @param s_id
	 *            要删除的站点id
	 * @return 是否删除成功
	 */
	public boolean deleteSta(int id) {
		String sql = "delete from station_information where s_id=?";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
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
	 * 
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
			if (result.next()) {
				Station sta = new Station();
				sta.setS_id(result.getInt("s_id"));
				sta.setS_name(result.getString("s_name"));
				sta.setLongitude(result.getDouble("longitude"));
				sta.setLatitude(result.getDouble("latitude"));
				sta.setS_car(result.getString("s_car"));
				station = sta;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}

		return station;
	}
	
	/***
	 * 将站点数据Excel导出
	 * 
	 * @param stalist,path
	 *            要导出的站点集合，excel文件下载地址
	 * @return 是否导出成功
	 */
	public boolean addToExcel(List<Station> stalist, String path) {
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

		for (int i = 0; i < stalist.size(); i++) {
			row = sheet.createRow((int) i + 1);
			Station sta = (Station) stalist.get(i);
			// 第四步，创建单元格，并设置值
			row.createCell((short) 0).setCellValue(sta.getS_id());
			row.createCell((short) 1).setCellValue(sta.getS_name());
			row.createCell((short) 2).setCellValue(sta.getLongitude());
			row.createCell((short) 3).setCellValue(sta.getLatitude());
		}
		// 第六步，将文件存到指定位置
		try {
			String name = path + "站点.xls";
			FileOutputStream fout = new FileOutputStream(name);
			wb.write(fout);
			fout.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/***
	 * 检测无用站点
	 * 
	 * @return 无用站点集
	 */
	public List<Station> isStationUsed() {
		List<Station> stationList = new ArrayList<>();
		String sql = "select * from station_information where s_car is null";
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
				sta.setS_car(result.getString("s_car"));
				stationList.add(sta);
			}
		} catch (Exception e) {
			System.out.println("找不到access驱动程序");
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return stationList;
	}
	
	/***
	 * 将string,string,string格式的字符串转换为stringList
	 * 
	 * @param str
	 *            原字符串
	 * @return 转换后的string集合
	 */
	public List<String> getStaList(String str)
	{
		List<String> staList=new ArrayList<String>();
		int point;
		String strPart;
		while(str!=null)
		{
			point=str.indexOf(",");
			if(point>=0)
			{
				strPart=str.substring(0, point);
				str=str.substring(point+1);
				staList.add(strPart);
			}else{
				staList.add(str);
				str=null;
			}
			
		}
		return staList;
	}
	
	/***
	 * 从字符串里面删除特定的字符
	 * 
	 * @param str,car
	 *            原字符串，要删除的字符串
	 * @return 处理后的字符串
	 */
	public String delFromStaList(String str,String car)
	{
		List<String> strList=getStaList(str);
		int size=strList.size();
		String strPart[]=(String [])strList.toArray(new String[size]);
		String strTurn="";
		for(int i=0;i<size;i++)
		{
			if(strPart[i].equals(car)==false)
			{
				if(strTurn.equals("")==true)
				{
					strTurn=strPart[i];
				}else
				{
					strTurn=strTurn+","+strPart[i];
				}
			}
		}
		return strTurn;
	}
	
	/***
	 * 往字符串里添加一个字符串
	 * 
	 * @param str,car
	 *            原字符串，要添加的字符串
	 * @return 处理后的字符串
	 */
	public String addToStaList(String str,String car)
	{
		if(str.equals("")==true)
		{
			str=car;
		}else
		{
			str=str+","+car;
		}
		return str;
	}
	
	/*涛哥看向我！！！
	 * 以下的函数都是给你用的！！！*/
	//
	/***
	 * 向站点车辆对应表中添加一辆车
	 * 
	 * @param id,car
	 *            要处理的站点id，要添加的车辆
	 * @return 是否添加成功
	 */
	public boolean addCarToSta(int id,String car)
	{
		String str=getStaById(id).getS_car();
		String strNow=addToStaList(str, car);
		String sql = "update  station_information set s_car=? "
				+ " where s_id=?";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, strNow);
			stmt.setDouble(2, id);
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
	
	/***
	 * 从站点对应表中删除这辆车
	 * 
	 * @param id,car
	 *            要处理的站点id，要删除的车辆
	 * @return 是否添加成功
	 */
	public boolean delCarToSta(int id,String car)
	{
		String str=getStaById(id).getS_car();
		String strNow=delFromStaList(str, car);
		String sql = "update  station_information set s_car=? "
				+ " where s_id=?";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, strNow);
			stmt.setDouble(2, id);
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
	
	/***
	 * 通过站点id反推其路线
	 * 
	 * @param id
	 *            查询的站点id
	 * @return 路线集合
	 */
	public List<String> getS_car(int id) {
		String sql = "select s_car from station_information where s_id=?";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			result = stmt.executeQuery();
			

			if (result.next()) {
				List<String> strList=getStaList(result.getString("s_car"));
				return strList;
				}
		} catch (Exception e) {
			System.out.println("找不到access驱动程序");
		}finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return null;
	}

}
