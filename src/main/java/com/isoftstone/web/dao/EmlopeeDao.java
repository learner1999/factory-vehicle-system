package com.isoftstone.web.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.isoftstone.web.pojo.Car_inf;
import com.isoftstone.web.pojo.Emlopee;
import com.isoftstone.web.util.JdbcUtil;
import com.mysql.jdbc.CallableStatement;

public class EmlopeeDao {
	
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet result = null;
	private ResultSet result1 = null;
	CallableStatement cs = null;
	
	
	public Emlopee getEmlopById(int e_id)
	{
		Emlopee emlop=new Emlopee();
		String sql = "select * from employee_infor where eid=?";
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, e_id);
			result = stmt.executeQuery();
			while (result.next()) {
				emlop.setEid(result.getInt("eid"));
				emlop.setEname(result.getString("ename"));
				emlop.setEpart(result.getString("epart"));
				emlop.setEgroup(result.getInt("egroup"));
				emlop.setEtime(result.getInt("etime"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
		}
		return emlop;
	}
/**
 * 根据id获取员工的地址和站点
 * @param id
 * @return
 */
	public List getemp_inf(int id)
	{
		CallableStatement cs1 = null;
		List<Emlopee> emp = new ArrayList<>();
		String proc="{call selectByeid(?)}";
		String proc1="{call selectPartByeid(?)}";
		try {
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			cs.setInt(1, id);
			result= cs.executeQuery();
			cs1=(CallableStatement) conn.prepareCall(proc1);
			cs1.setInt(1, id);
			result1=cs1.executeQuery();
			while (result.next() && result1.next()) {
				Emlopee em= new Emlopee();
				em.setStation(result.getString(1));
				em.setAddress(result.getString(2));
				em.setEpart(result1.getString(1));
				emp.add(em);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
			JdbcUtil.closecs(cs);
		}
		return emp;
	}
	/**
	 * 更新员工的地址
	 * @param id
	 * @param address
	 * @return
	 */
	public boolean updateaddress(int id,String address)
	{
		String proc="{call updateaddress(?,?)}";
		try {
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(proc);
			cs.setInt(1, id);
			cs.setString(2,address);
			int counter = cs.executeUpdate();
			if(counter==1) {
				return true;
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
			JdbcUtil.closecs(cs);
		}
		return false;
	}
}
