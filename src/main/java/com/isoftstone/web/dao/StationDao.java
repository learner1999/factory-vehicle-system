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

import com.isoftstone.web.pojo.EmpMatchSta;
import com.isoftstone.web.pojo.Station;
import com.isoftstone.web.util.JdbcUtil;

public class StationDao {
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet result = null;

	/***
	 * 查询数据库中所有的站点/临时停车点信息
	 * 
	 * @param isSta
	 * 			是否是站点
	 * @return 所有站点/临时停车点信息
	 */
	public List<Station> showAllSta(int isSta){
		List<Station> staList=new ArrayList<Station>();
		String sql="select * from station_information_copy where s_is_used=?";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, isSta);
			result = stmt.executeQuery();
			while (result.next()) {
				Station sta = new Station();
				sta.setS_id(result.getInt("s_id"));
				sta.setS_name(result.getString("s_name"));
				sta.setS_address(result.getString("s_address"));
				sta.setLongitude(result.getDouble("longitude"));
				sta.setLatitude(result.getDouble("latitude"));
				sta.setS_car(result.getString("s_car"));
				sta.setS_is_used(result.getInt("s_is_used"));
				staList.add(sta);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return staList;
	}
	
	/***
	 * 按照名称或者地址搜索站点/临时停车点
	 * 
	 * @param StrPart,isSta
	 * 			模糊搜索的字段，是否是站点
	 * @return 模糊搜索的查询结果
	 */
	public List<Station> searchSta(String StrPart,int isSta){
		List<Station> staList=new ArrayList<Station>();
		String sql="select * from station_information_copy where (s_name or s_address like ?) and s_is_used=?";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "%" + StrPart + "%");
			stmt.setInt(2,isSta);
			result = stmt.executeQuery();
			while (result.next()) {
				Station sta = new Station();
				sta.setS_id(result.getInt("s_id"));
				sta.setS_name(result.getString("s_name"));
				sta.setS_address(result.getString("s_address"));
				sta.setLongitude(result.getDouble("longitude"));
				sta.setLatitude(result.getDouble("latitude"));
				sta.setS_car(result.getString("s_car"));
				sta.setS_is_used(result.getInt("s_is_used"));
				staList.add(sta);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return staList;
	}
	
	/***
	 *  获得id，将站点转换为临时停车点
	 * 
	 * @param id
	 *            要修改的站点id
	 * @return 是否修改成功
	 */
	public boolean changeStaToPo(int id) {
		String sql = "update station_information_copy set s_is_used=0 where s_id=? and s_is_used=1";
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
	
	/***
	 *  获得id，将临时停车点转换为站点
	 * 
	 * @param id
	 *            要修改的站点id
	 * @return 是否修改成功
	 */
	public boolean changePoToSta(int id) {
		String sql = "update station_information_copy set s_is_used=1 where s_id=? and s_is_used=0";
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
	
	/***
	 * 通过站点/临时停车点id修改站点名称（由于不存在操作冲突的情况，所以可以公用）
	 * 
	 * @param s_id,nameNow
	 * 			站点/临时停车点id，修改后的名称
	 * @return 是否修改成功
	 */
	public boolean change(int id, String nameNow) {
		String sql = "update station_information_copy set s_name=? where s_id=?";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, nameNow);
			stmt.setInt(2, id);
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
	 * 根据站点/临时停车点的id删除掉一个站点（由于不存在操作冲突的情况，所以可以公用）
	 * 
	 * @param s_id
	 *            要删除的站点/临时停车点id
	 * @return 是否删除成功
	 */
	public boolean deleteSta(int id) {
		String sql = "delete from station_information_copy where s_id=?";
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
	
	/***
	 * 将所有站点变成未使用站点
	 * 
	 * @param staList
	 * 			规划好的站点集合
	 * @return 是否保存成功
	 */
	/*public boolean changeAll()
	{
		String sql = "update station_information_copy set s_is_used=0 where s_is_used=1";

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
	}*/
	
	/***
	 * 新增一个站点
	 * 
	 * @param sta
	 *            新增的站点对象
	 * @return 是否增加成功
	 */
	public boolean createStation(Station sta) {
		String sql = "insert into station_information_copy(s_name,s_address,longitude,latitude,s_is_used) values(?,?,?,?,?)";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, sta.getS_name());
			stmt.setString(2, sta.getS_address());
			stmt.setDouble(3, sta.getLongitude());
			stmt.setDouble(4, sta.getLatitude());
			stmt.setInt(5, sta.getS_is_used());
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
	 * 将站点/停车点数据Excel导出
	 * 
	 * @param stalist,path
	 *            要导出的站点/停车点集合，excel文件下载地址
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
		cell.setCellValue("站点地址");
		cell.setCellStyle(style);

		for (int i = 0; i < stalist.size(); i++) {
			row = sheet.createRow((int) i + 1);
			Station sta = (Station) stalist.get(i);
			// 第四步，创建单元格，并设置值
			row.createCell((short) 0).setCellValue(sta.getS_id());
			row.createCell((short) 1).setCellValue(sta.getS_name());
			row.createCell((short) 2).setCellValue(sta.getS_address());
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

	//在新建的时候判断用的。提示，已存在名为***的停车点或站点
	/***
	 * 测试站点是否已经存在
	 * 
	 * @param s_name
	 *            查询的站点名称
	 * @return 是否存在
	 */
	public boolean isStationExist(String s_name) {
		String sql = "select s_name from station_information_copy where s_name=?";

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
	
	//在新建的时候判断用的。提示，在此地址已存在停车点或站点
		/***
		 * 测试站点是否已经存在
		 * 
		 * @param x，y
		 *            本地经度，本地纬度
		 * @return 是否存在
		 */
		public boolean isxyExist(double x,double y) {
			String sql = "select * from station_information_copy where longitude=? and latitude=?";

			try {
				conn = JdbcUtil.getConnection();
				stmt = conn.prepareStatement(sql);
				stmt.setDouble(1, x);
				stmt.setDouble(2,y);
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
	 * 通过id获取到对应的站点信息（由于通过id查的的结果是唯一的，所以不会出现冲突）
	 * 
	 * @param id
	 *            站点id
	 * @return 查询结果
	 */
	public Station getStaById(int id) {
		Station sta = new Station();
		String sql = "select * from station_information_copy where s_id=?";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			result = stmt.executeQuery();
			while (result.next()) {
				sta.setS_id(result.getInt("s_id"));
				sta.setS_name(result.getString("s_name"));
				sta.setS_address(result.getString("s_address"));
				sta.setLongitude(result.getDouble("longitude"));
				sta.setLatitude(result.getDouble("latitude"));
				sta.setS_car(result.getString("s_car"));
				sta.setS_is_used(result.getInt("s_is_used"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return sta;
	}
	
	//可能会用在人员名单里面，也可能又不长眼的把多个路线经过一个站点
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
	
	//保不齐有那种傻得一个站点好几辆车来
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
		String sql = "update  station_information_copy set s_car=? "
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
		String sql = "update  station_information_copy set s_car=? "
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
		String sql = "select s_car from station_information_copy where s_id=?";
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
			System.out.println("鎵句笉鍒癮ccess椹卞姩绋嬪簭");
		}finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return null;
	}
	
	/***
	 * 检测无用站点
	 * 
	 * @return 无用站点集
	 */
	public List<Station> isStationUsed() {
		List<Station> stationList = new ArrayList<>();
		String sql = "select * from station_information_copy where s_car is null";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();

			while (result.next()) {
				Station sta = new Station();
				sta.setS_id(result.getInt("s_id"));
				sta.setS_name(result.getString("s_name"));
				sta.setS_address(result.getString("s_address"));
				sta.setLongitude(result.getDouble("longitude"));
				sta.setLatitude(result.getDouble("latitude"));
				sta.setS_car(result.getString("s_car"));
				sta.setS_is_used(result.getInt("s_is_used"));
				stationList.add(sta);
			}
		} catch (Exception e) {
			//System.out.println("鎵句笉鍒癮ccess椹卞姩绋嬪簭");
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return stationList;
	}
	
	/***
	 * 测试在这个位置上是否已经有站点了
	 * 
	 * @param add
	 *            地址
	 * @return 是否存在
	 */
	/*public boolean isxyExist(String add) {
		String sql = "select s_address from station_information_copy where s_address=?";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, add);
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
	}*/
	
	//算法用
	/***
	 * 给定地址，查找最近站点/停车点
	 * 
	 * @param x,y
	 * 			站点/停车点纬度，站点/停车点经度
	 * @return 最近站点
	 */
	public Station findNearPoint(double x,double y)
	{
		Station sta=new Station();
		String sql = "select * from station_information_copy order by ((longitude-?)*(longitude-?)"
							+ "+(latitude-?)*(latitude-?)) limit 1";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setDouble(1,x);
			stmt.setDouble(2,x);
			stmt.setDouble(3,y);
			stmt.setDouble(4,y);
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
	 * 找出所有人数为0的站点
	 */
	public List<Station> isStationZero() {
		List<Station> stationList = new ArrayList<>();
		String sql="select * from station_information_copy where s_is_used=1 and s_id not in(select s_id from employee_station_copy)";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();

			while (result.next()) {
				Station sta = new Station();
				sta.setS_id(result.getInt("s_id"));
				sta.setS_name(result.getString("s_name"));
				sta.setS_address(result.getString("s_address"));
				sta.setLongitude(result.getDouble("longitude"));
				sta.setLatitude(result.getDouble("latitude"));
				sta.setS_car(result.getString("s_car"));
				sta.setS_is_used(result.getInt("s_is_used"));
				stationList.add(sta);
			}
		} catch (Exception e) {
			System.out.println("鎵句笉鍒癮ccess椹卞姩绋嬪簭");
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return stationList;
	}
	
	/***
	 * 提醒不合适情况。有员工未匹配，有无用站点
	 * 
	 * 返回不合适代码。1是人员站点不匹配，建议重新同步。2是有人数为0的站点，建议删除
	 */
	public int isOk()
	{
		EmpMStaDao emsDao=new EmpMStaDao();
		if(emsDao.getAllchange().size()>0||emsDao.getAllnew().size()>0)
		{
			return 1;
		}
		if(isStationZero().size()>0)
		{
			return 2;
		}
		return 0;
	}
	
	/***
	 * 查看员工附近站点/停车点
	 * 
	 * @param ems
	 * 			员工站点对应信息
	 * @return 附近站点集合
	 */
	public List<Station> getNearMan(EmpMatchSta ems)
	{
		List<Station> staList=new ArrayList<Station>();
		String sql="select * from station_information_copy where s_id in(select s_id from employee_station_copy"
				+ " order by (e_x-?)*(e_x-?)+(e_y-?)*(e_y-?)) limit 10";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setDouble(1, ems.getE_x());
			stmt.setDouble(2, ems.getE_x());
			stmt.setDouble(3, ems.getE_y());
			stmt.setDouble(4, ems.getE_y());
			result = stmt.executeQuery();
			while (result.next()) {
				Station sta=new Station();
				sta.setS_id(result.getInt("s_id"));
				sta.setS_name(result.getString("s_name"));
				sta.setS_address(result.getString("s_address"));
				sta.setLongitude(result.getDouble("longitude"));
				sta.setLatitude(result.getDouble("latitude"));
				sta.setS_car(result.getString("s_car"));
				sta.setS_is_used(result.getInt("s_is_used"));
				staList.add(sta);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return staList;
	}
	
	
	//在同步的时候就把信息存进对应表里面,并且把x，y存进去，在测试一遍规划算法!!!
	//新增的同步就存，规划才改站点，修改的规划地址和站
	//规划算法为那两种的规划，那么就开始规划的时候，将这两种ems类合在一起用参数传进去
	//显示的时候干脆也分成两种情况显示
	
	/**
	 * 通过站点id获得站点人数。在前面实现的时候直接用匹配的函数
	 */
}
