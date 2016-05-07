package com.isoftstone.web.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.isoftstone.web.util.JdbcUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginDao {
	
	/***
	 * 通过用户名密码登录
	 * @param username
	 * @param password
	 * @return id  登录成功返回
	 */
	public int login(String username, String password) {
		int id = 0;
		
		try {
			Connection conn = JdbcUtil.getConnection();
			String strSql = "SELECT * FROM `user` WHERE username=? AND password=?";
			PreparedStatement stat = conn.prepareStatement(strSql);
			stat.setString(1, username);
			stat.setString(2, password);
			
			ResultSet result = stat.executeQuery();
			if(result.next()) {
				id = result.getInt("id");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return id;
	}
}
