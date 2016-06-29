package com.isoftstone.web.dao;

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

public class EmpMStaDao {
	
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet result = null;
	
	private EmlopeeDao emlopDao=new EmlopeeDao();
	
	//获得所有的(不包括需要处理的)
	//根据员工名模糊查询获得的（也不包括需要处理的）（由于主要的功能和员工没什么关系，所以员工就做这些）
	
	//
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
		String sql = "select e_id from employee_station where s_id=?";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, s_id);
			result = stmt.executeQuery();
			while (result.next()) {
				Emlopee emlop=new Emlopee();
				//获得emlop
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
		String sql = "select * from employee_station where e_id=?";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, e_id);
			result = stmt.executeQuery();
			while (result.next()) {
				ems.setE_id(result.getInt("e_id"));
				ems.setS_id(result.getInt("s_id"));
				ems.setE_x(result.getDouble("e_x"));
				ems.setE_y(result.getDouble("e_y"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return ems;
	}
	
	/***
	 * 找到新增但还没有为其建造站点的员工
	 * 
	 * @return 这类员工信息集
	 */
	public List<Emlopee> matchEAndS()
	{
		List<Emlopee> emlopList=new ArrayList<Emlopee>();
		String sql="select * from employee_infor where Eid not in(select e_id from employee_station)";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			while (result.next()) {
				Emlopee emlop=new Emlopee();
				//获得emlop
				emlop.setEid(result.getInt("eid"));
				emlop.setEname(result.getString("ename"));
				emlop.setEpart(result.getString("epart"));
				emlop.setEgroup(result.getInt("egroup"));
				emlop.setEtime(result.getInt("etime"));
				emlop.setEx(result.getInt("ex"));
				emlop.setEy(result.getInt("ey"));
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
		String sql="insert into employee_station(e_id,s_id,e_x,e_y) values (?,?,?,?)";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, ems.getE_id());
			stmt.setInt(2, ems.getS_id());
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
	public boolean updateEMSByEid(int e_id, EmpMatchSta ems) {
		String sql = "update  employee_station set s_id=? where e_id=?";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, ems.getS_id());
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
		String sql="select * from employee_station where e_id not in(select Eid from employee_infor)";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			result = stmt.executeQuery();
			while (result.next()) {
				EmpMatchSta ems=new EmpMatchSta();
				//获得emlop
				ems.setE_id(result.getInt("e_id"));
				ems.setS_id(result.getInt("s_id"));
				ems.setE_x(result.getDouble("e_x"));
				ems.setE_y(result.getDouble("e_y"));
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
		String sql = "delete from employee_station where e_id=?";
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
}
