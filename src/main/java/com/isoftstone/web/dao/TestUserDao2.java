package com.isoftstone.web.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.isoftstone.web.pojo.Emlopee;
import com.isoftstone.web.pojo.TestUser2;
import com.isoftstone.web.util.JdbcUtil;
import com.mysql.jdbc.CallableStatement;

public class TestUserDao2 {
	CallableStatement cs = null;
	/***
	 * 查找所有总务部和行政部(没有创建账号)人员
	 * @return 所有用户构成的 list
	 */
	public List<Emlopee> findUsersidBypart(int part) {
		// 创建数据库连接时需要的对象
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		// 创建一个 List 用于存放 User 对象
		List<Emlopee> userList = new ArrayList<>();
		// 数据库查询语句
		String prco = "call findUsersidBypart(?)";
		String p;
		try {
			if(part==1)
				p="总务部";
			else if(part==2)
				p="行政部";
			else 
				p="";
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(prco); // 这个地方用了
	   									// prepareStatement，主要目的是防止
			 cs.setString(1, p);
			result =cs.executeQuery();
			while (result.next()) {
				Emlopee user = new Emlopee();
				user.setEid(result.getInt("EId"));
				user.setEname(result.getString("EName"));
				user.setEpart(result.getString("EPart"));
				user.setEgroup(result.getInt("EGroup"));
				user.setEtime(result.getInt("ETime"));
				userList.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn); // 在最后记得关闭数据库连接
			JdbcUtil.closecs(cs);
		}

		return userList;
	}

	/**
	 * 判断该账号是否存在
	 * @param username
	 * @return 是否存在
	 */
	public boolean findByusername(String username) {
		// jdbc 必备
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		// 数据库查询字符串，其中的 '?' 号是一个占位符，预编译的方式（prepareStatement）需要这种写法
		String prco = "call findByusername(?)";
		try {
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(prco);
			cs.setString(1,username);// 将 id 值替换至 '?' 号占位的地方
			result = cs.executeQuery();
			if (result.next()) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn); // 关闭数据库连接
			JdbcUtil.closecs(cs);
		}
		return true;
	}

	/***
	 * 查询数据库中是否已经存在相同用户名用户
	 * 
	 * @param user
	 *            查询的用户对象
	 * @return 是否存在
	 */
	public boolean isUserExist(int user) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		String prco = "call isUserExist(?)";
		try {
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(prco);
			String a=String.valueOf(user);
			cs.setString(1, a);
			result = cs.executeQuery();
			if (result.next()) {
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
     /**
      * 判断是否是行政部或者总务部
      * @param user
      * @return 是的返回false,不是返回true
      */
	public boolean isUserTrue(TestUser2 user) {
		Connection conn = null;
		ResultSet result = null;
		PreparedStatement stmt = null;
		String prco = "{call IS_Uid(?)}";
		try {
			int a=Integer.parseInt(user.getUsername());
			conn = JdbcUtil.getConnection();
			cs= (CallableStatement) conn.prepareCall(prco);
			cs.setInt(1,a);
			result = cs.executeQuery();
			if(result.next()){
				if(result.getInt("res")==0)
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
	
	/***
	 * 将 ResultSet 中的数据提取到 user 对象中
	 * 
	 * @param result
	 *            执行数据库查询后返回的 ResultSet
	 * @return user对象
	 * @throws SQLException
	 */
	private TestUser2 fetchData(ResultSet result) throws SQLException {
		TestUser2 user = new TestUser2();
		user.setId(result.getInt("id"));
		user.setUsername(result.getString("username"));
		user.setPassword(result.getString("password"));
		user.setAuthority(result.getInt("authority"));

		return user;
	}

	/***
	 * 向数据库中插入一个用户
	 * 
	 * @param user
	 *            用户信息
	 * @return 成功返回 true，失败返回 false
	 */
	public boolean createUser(Emlopee user) {
		Connection conn = null;
		PreparedStatement stmt = null;
		// 3 个占位符，分别对应位置为 1,2,3
		String prco = "call createUser(?,?)";
		try {
			conn = JdbcUtil.getConnection();
			// 替换占位符位置的值
			String a=String.valueOf(user.getEid());
			if(a!=null){
			 cs = (CallableStatement) conn.prepareCall(prco);
			 cs.setString(1, a);
			if(user.getEpart().equals("总务部")){
				cs.setInt(2, 1);
			}
			else if(user.getEpart().equals("行政部")){
				cs.setInt(2, 2);
			}
			else{
              cs.setInt(2, 0);				
			}
			}

			
			int counter = cs.executeUpdate();
			if (1 == counter) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, stmt, conn);
			JdbcUtil.closecs(cs);
		}

		return false;
	}

	/***
	 * 修改数据库中对应 用户名 用户信息
	 * 
	 * @param user
	 *            用户信息
	 * @return 成功返回 true，失败返回 false
	 */
	public boolean updateUser(TestUser2 user) {
		Connection conn = null;
		PreparedStatement stmt = null;

		String prco = "call updateUser(?,?,?)";
		try {
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(prco);
			cs.setString(1, user.getUsername());
			cs.setString(2, user.getPassword());
			cs.setInt(3, user.getAuthority());
			int counter = cs.executeUpdate();
			if (1 == counter) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, stmt, conn);
			JdbcUtil.closecs(cs);
		}

		return false;
	}

	/***
	 * 删除指定用户名用户信息
	 * @return 成功返回 true，失败返回 false
	 */
	public boolean deleteUser(String username) {
		Connection conn = null;
		PreparedStatement stmt = null;
		String prco = "call deleteUser(?)";
		try {
			conn = JdbcUtil.getConnection();
			cs = (CallableStatement) conn.prepareCall(prco);
			cs.setString(1, username);
			int counter = cs.executeUpdate();
			if (1 == counter) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, stmt, conn);
			JdbcUtil.closecs(cs);
		}

		return false;
	}
/**
 * 模糊查询账户信息
 * @param name
 * @return
 */
	public List<TestUser2> findByName(String name) {
		// 创建数据库连接时需要的对象
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		// 创建一个 List 用于存放 User 对象
		List<TestUser2> userList = new ArrayList<>();

		// 数据库查询语句
		String strSql = "SELECT * FROM `user` WHERE username like ?";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql); 
			stmt.setString(1, "%" + name + "%");
			result = stmt.executeQuery();
			while (result.next()) {
				TestUser2 user = fetchData(result); 
				userList.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn); // 在最后记得关闭数据库连接
		}

		return userList;

	}

	
		
	
}
