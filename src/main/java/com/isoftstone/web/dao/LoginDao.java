package com.isoftstone.web.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.isoftstone.web.util.JdbcUtil;
import com.mysql.jdbc.CallableStatement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginDao {
	
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet result = null;
	CallableStatement cs = null;
	
	/***
	 * 通过用户名密码登录
	 * @param username
	 * @param password
	 * @return id  登录成功返回0,1,2
	 */
	public int login(String username, String password) {
		int id = 0;
			String prco = "{call userlogin(?,?)}";
			try {
				conn = JdbcUtil.getConnection();
				cs= (CallableStatement) conn.prepareCall(prco);
				cs.setString(1,username);
				cs.setString(2,password);
				result = cs.executeQuery();
				if(result.next()){
					id=result.getInt(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtil.close(result, stmt, conn);
				JdbcUtil.closecs(cs);
			}

			return id;
	
}
	
	public boolean is_exist(String username, String password) {
		
			String prco = "{call userlogin(?,?)}";
			try {
				conn = JdbcUtil.getConnection();
				cs= (CallableStatement) conn.prepareCall(prco);
				cs.setString(1,username);
				cs.setString(2,password);
				result = cs.executeQuery();
				if(result.next()){
					return true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtil.close(result, stmt, conn);
				JdbcUtil.closecs(cs);
			}

			return false;
	
}
	
	public boolean is_username(String username) {
		
		String prco = "{call is_username(?)}";
		try {
			conn = JdbcUtil.getConnection();
			cs= (CallableStatement) conn.prepareCall(prco);
			cs.setString(1,username);
			result = cs.executeQuery();
			if(result.next()){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn);
			JdbcUtil.closecs(cs);
		}

		return false;

}
}

