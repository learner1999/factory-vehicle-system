package com.isoftstone.web.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.isoftstone.web.pojo.Emlopee;
import com.isoftstone.web.pojo.EmpMatchSta;
import com.isoftstone.web.pojo.Station;
import com.isoftstone.web.util.JdbcUtil;
import com.routematrix.pojo.Coordinate;
import com.webapi.ApiOp;


public class EmpMStaDao {
	
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet result = null;
	
	private EmlopeeDao emlopDao=new EmlopeeDao();
	
	/***
	 * 查询数据库中所有的站点对应信息。为计算做准备
	 * 
	 * @return 所有站点对应信息
	 */
	public List<EmpMatchSta> showAllnew(){
		List<EmpMatchSta> emsList=new ArrayList<EmpMatchSta>();
		String sql="select x.e_id,x.s_id,x.e_address,x.e_x,x.e_y,y.ename from "
				+ "employee_station_copy as x,employee_infor_copy as y,"
				+ "where x.e_id=y.eid and x.s_id is null order by x.e_y";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			while (result.next()) {
				EmpMatchSta ems = new EmpMatchSta();
				ems.setE_id(result.getInt("x.e_id"));
				ems.setS_id(result.getInt("x.s_id"));
				ems.setE_address(result.getString("x.e_address"));
				ems.setE_x(result.getDouble("x.e_x"));
				ems.setE_y(result.getDouble("x.e_y"));
				ems.setE_name(result.getString("y.ename"));
				ems.setUsed(0);
				emsList.add(ems);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return emsList;
	}
	
	/***
	 * 查询数据库中所有的站点对应信息
	 * 
	 * @return 所有站点对应信息
	 */
	public List<EmpMatchSta> showAll(){
		List<EmpMatchSta> emsList=new ArrayList<EmpMatchSta>();
		String sql="select x.e_id,x.s_id,x.e_address,x.e_x,x.e_y,y.ename,z.s_name from "
				+ "employee_station_copy as x,employee_infor_copy as y,"
				+ "station_information_copy as z where x.e_id=y.eid and x.s_id=z.s_id";
		//"select * from employee_station_copy";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			while (result.next()) {
				EmpMatchSta ems = new EmpMatchSta();
				ems.setE_id(result.getInt("x.e_id"));
				ems.setS_id(result.getInt("x.s_id"));
				ems.setE_address(result.getString("x.e_address"));
				ems.setE_x(result.getDouble("x.e_x"));
				ems.setE_y(result.getDouble("x.e_y"));
				ems.setE_name(result.getString("y.ename"));
				ems.setS_name(result.getString("z.s_name"));
				emsList.add(ems);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return emsList;
	}
	
	/***
	 * 通过站点id查看此站点的员工
	 * 
	 * @param s_id
	 *            要查询的站点id
	 * @return 员工id集合
	 */
	public List<Emlopee> getEmlopBySta(int s_id)
	{
		List<Emlopee> emlopList=new ArrayList<Emlopee>();
		String sql = "select e_id from employee_station_copy where s_id=?";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, s_id);
			result = stmt.executeQuery();
			while (result.next()) {
				Emlopee emlop=new Emlopee();
				emlop=emlopDao.getEmlopById(result.getInt("e_id"));
				emlopList.add(emlop);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return emlopList;
	}
	
	/***
	 * 通过员工id找到这条记录
	 * 
	 * @param e_id
	 *            员工id
	 * @return 员工站点对应信息
	 */
	public EmpMatchSta getEmsByEid(int e_id)
	{
		EmpMatchSta ems=new EmpMatchSta();
		String sql = "select x.e_id,x.s_id,x.e_address,x.e_x,x.e_y,y.ename,z.s_name from "
				+ "employee_station_copy as x,employee_infor_copy as y,"
				+ "station_information_copy as z where x.e_id=? and x.e_id=y.eid and x.s_id=z.s_id";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, e_id);
			result = stmt.executeQuery();
			while (result.next()) {
				ems.setE_id(result.getInt("x.e_id"));
				ems.setS_id(result.getInt("x.s_id"));
				ems.setE_address(result.getString("x.e_address"));
				ems.setE_x(result.getDouble("x.e_x"));
				ems.setE_y(result.getDouble("x.e_y"));
				ems.setE_name(result.getString("y.ename"));
				ems.setS_name(result.getString("z.s_name"));  
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return ems;
	}
	
	/***
	 * 同步员工
	 * 
	 * @return 新增的员工集合
	 */
	public List<Emlopee> matchEAndS()
	{
		List<Emlopee> emlopList=new ArrayList<Emlopee>();
		String sql="select * from employee_infor_copy where Eid not in(select e_id from employee_station_copy)";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			while (result.next()) {
				Emlopee emlop=new Emlopee();
				emlop.setEid(result.getInt("eid"));
				emlop.setEname(result.getString("ename"));
				emlop.setEpart(result.getString("epart"));
				emlop.setEgroup(result.getInt("egroup"));
				emlop.setEtime(result.getInt("etime"));
				emlop.setAddress(result.getString("EAddress"));
				emlop.setEiden(result.getString("Eiden"));
				emlopList.add(emlop);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return emlopList;
	}

	
	/***
	 * 新增员工站点对应表信息
	 * 
	 * @param ems
	 *            员工站点对应信息
	 * @return 是否添加成功
	 */
	public boolean creatEMS(EmpMatchSta ems)
	{
		String sql="insert into employee_station_copy(e_id,e_address,e_x,e_y) values (?,?,?,?)";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, ems.getE_id());
			stmt.setString(2, ems.getE_address());
			stmt.setDouble(3,ems.getE_x());
			stmt.setDouble(4, ems.getE_y());
			int counter = stmt.executeUpdate();
			if (1 == counter) {
				JdbcUtil.close(null, stmt, conn);
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
	 * 修改员工对应的站点表
	 * 
	 * @param e_id,ems
	 *            员工id,员工站点对应信息
	 * @return 是否修改成功
	 */
	public boolean updateEMSByEid(int e_id, int s_id) {
		String sql = "update  employee_station_copy set s_id=? where e_id=?";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, s_id);
			stmt.setInt(2, e_id);
			int counter = stmt.executeUpdate();
			if (1 == counter) {
				JdbcUtil.close(null, stmt, conn);
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
	 * 找到虽有匹配站点但没有员工信息的记录
	 * 
	 * @return 这类员工信息集
	 */
	public List<EmpMatchSta> matchSAndE()
	{
		List<EmpMatchSta> emsList=new ArrayList<EmpMatchSta>();
		String sql="select x.e_id,x.s_id,x.e_address,x.e_x,x.e_y,y.ename,z.s_name from "
				+ "employee_station_copy as x,employee_infor_copy as y,"
				+ "station_information_copy as z where x.e_id=y.eid and x.s_id=z.s_id and x.e_id not in(select Eid from employee_infor_copy)";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			while (result.next()) {
				EmpMatchSta ems=new EmpMatchSta();
				ems.setE_id(result.getInt("x.e_id"));
				ems.setS_id(result.getInt("x.s_id"));
				ems.setE_address(result.getString("x.e_address"));
				ems.setE_x(result.getDouble("x.e_x"));
				ems.setE_y(result.getDouble("x.e_y"));
				ems.setE_name(result.getString("y.ename"));
				ems.setS_name(result.getString("z.s_name"));
				emsList.add(ems);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return emsList;
	}
	
	/***
	 * 删除某员工对应的员工站点信息
	 * 
	 * @param e_id
	 *            员工id
	 * @return 是否删除成功
	 */
	public boolean deleteEms(int e_id) {
		String sql = "delete from employee_station_copy where e_id=?";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, e_id);
			int counter = stmt.executeUpdate();
			if (1 == counter) {
				JdbcUtil.close(null, stmt, conn);
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
	 * 通过员工地址搜索员工站点对应信息
	 * 
	 * @param addPrat
	 * 				员工住址的部分字段
	 * @return 此类员工站点对应信息
	 */
	public List<EmpMatchSta> search(String addPart)
	{
		List<EmpMatchSta> emsList=new ArrayList<EmpMatchSta>();
		String sql="select x.e_id,x.s_id,x.e_address,x.e_x,x.e_y,y.ename,z.s_name from "
				+ "employee_station_copy as x,employee_infor_copy as y,"
				+ "station_information_copy as z where x.e_id=y.eid and x.s_id=z.s_id and x.e_address like ?";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "%" + addPart + "%");
			result = stmt.executeQuery();
			while (result.next()) {
				EmpMatchSta ems=new EmpMatchSta();
				ems.setE_id(result.getInt("x.e_id"));
				ems.setS_id(result.getInt("x.s_id"));
				ems.setE_address(result.getString("x.e_address"));
				ems.setE_x(result.getDouble("x.e_x"));
				ems.setE_y(result.getDouble("x.e_y"));
				ems.setE_name(result.getString("y.ename"));
				ems.setS_name(result.getString("z.s_name"));
				emsList.add(ems);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return emsList;
	}
	
	/***
	 * 找到新增但还没有为其建造站点的员工
	 * 
	 * @return 这类员工信息集
	 */
	public List<EmpMatchSta> getAllnew(){
		List<EmpMatchSta> emsList=new ArrayList<EmpMatchSta>();
		String sql="select x.e_id,x.s_id,x.e_address,x.e_x,x.e_y,y.ename from "
				+ "employee_station_copy as x,employee_infor_copy as y"
				+ " where x.e_id=y.eid and x.s_id is null";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			while (result.next()) {
				EmpMatchSta ems = new EmpMatchSta();
				ems.setE_id(result.getInt("x.e_id"));
				ems.setS_id(result.getInt("x.s_id"));
				ems.setE_address(result.getString("x.e_address"));
				ems.setE_x(result.getDouble("x.e_x"));
				ems.setE_y(result.getDouble("x.e_y"));
				ems.setE_name(result.getString("y.ename"));
				ems.setUsed(0);
				emsList.add(ems);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return emsList;
	}	
	
	/***
	 * 找到修改了地址但未为其重新匹配站点的员工
	 * 
	 * @return 此类员工集合
	 */
	public List<Emlopee> getAllchange(){
		List<Emlopee> emlopList=new ArrayList<Emlopee>();
		String sql="select y.eid,y.EAddress from"
				+ " employee_station_copy as x,employee_infor_copy as y where x.e_id=y.eid"
				+ " and x.e_address <> y.EAddress";
		try{
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			while (result.next()) {
				Emlopee e = new Emlopee();
				e.setEid(result.getInt("y.eid"));
				e.setAddress(result.getString("y.EAddress"));
				emlopList.add(e);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return emlopList;
	}	

	/***
	 * 修改员工对应的站点表
	 * 
	 * @param ems
	 *            员工站点对应信息
	 * @return 是否修改成功
	 */
	public boolean updateEMSForAdd(EmpMatchSta ems) {
		String sql = "update  employee_station_copy set s_id=null,e_address=?,e_x=?,e_y=? where e_id=?";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, ems.getE_address());
			stmt.setDouble(2, ems.getE_x());
			stmt.setDouble(3, ems.getE_y());
			stmt.setInt(4, ems.getE_id());
			int counter = stmt.executeUpdate();
			if (1 == counter) {
				JdbcUtil.close(null, stmt, conn);
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
	 * 通过员工住址获得x，y
	 */
	public EmpMatchSta getXYByAdd(Emlopee e)
	{
		EmpMatchSta ems=new EmpMatchSta();
		Coordinate c=new Coordinate();
		try {
			c = ApiOp.getXYByAddress(e.getAddress());
		} catch (IOException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		ems.setE_id(e.getEid());
		ems.setE_address(e.getAddress());
		ems.setE_x(c.getLng());
		ems.setE_y(c.getLat());
		//System.out.println(c.getLng()+"  "+c.getLat());
		return ems;
	}

	//关于附近的能不能默认先显示10个，如果用户点击查看更多，再显示其余的
	/***
	 * 查看此站点/停车点附近的员工
	 * 
	 * @param sta
	 * 			操作的站点
	 * @return 这类员工集合
	 */
	public List<EmpMatchSta> getNear(Station sta)
	{
		List<EmpMatchSta> emsList=new ArrayList<EmpMatchSta>();
		String sql="select x.e_id,x.s_id,x.e_address,x.e_x,x.e_y,y.ename,z.s_name from "
				+ "employee_station_copy as x,employee_infor_copy as y,"
				+ "station_information_copy as z where x.e_id=y.eid and x.s_id=z.s_id order by (x.e_x-?)*(x.e_x-?)+(x.e_y-?)*(x.e_y-?) limit 10";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setDouble(1, sta.getLongitude());
			stmt.setDouble(2, sta.getLongitude());
			stmt.setDouble(3, sta.getLatitude());
			stmt.setDouble(4, sta.getLatitude());
			result = stmt.executeQuery();
			while (result.next()) {
				EmpMatchSta ems = new EmpMatchSta();
				ems.setE_id(result.getInt("x.e_id"));
				ems.setS_id(result.getInt("x.s_id"));
				ems.setE_address(result.getString("x.e_address"));
				ems.setE_x(result.getDouble("x.e_x"));
				ems.setE_y(result.getDouble("x.e_y"));
				ems.setE_name(result.getString("y.ename"));
				ems.setS_name(result.getString("z.s_name"));
				emsList.add(ems);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return emsList;
	}
}
