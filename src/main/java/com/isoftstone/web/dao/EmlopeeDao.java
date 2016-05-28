package com.isoftstone.web.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.isoftstone.web.pojo.Emlopee;
import com.isoftstone.web.util.JdbcUtil;

public class EmlopeeDao {
	
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet result = null;
	
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
}
