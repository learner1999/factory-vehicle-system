package com.isoftstone.web.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.isoftstone.web.pojo.TestUser2;
import com.isoftstone.web.util.JdbcUtil;

public class TestUserDao2 {

	/***
	 * 查找所有用户
	 * 
	 * @return 所有用户构成的 list
	 */
	public List<TestUser2> findAllUsers() {

		// 创建数据库连接时需要的对象
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		// 创建一个 List 用于存放 User 对象
		List<TestUser2> userList = new ArrayList<>();

		// 数据库查询语句
		String strSql = "SELECT * FROM `user`";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql); // 这个地方用了
													// prepareStatement，主要目的是防止
													// sql 注入
			result = stmt.executeQuery();
			while (result.next()) {
				TestUser2 user = fetchData(result); // 写了一个 fetchData
													// 函数便于获取数据库返回的数据
				userList.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn); // 在最后记得关闭数据库连接
		}

		return userList;
	}

	/***
	 * 通过用户 id 查找用户信息
	 * 
	 * @param id
	 *            用户id
	 * @return 用户信息
	 */
	public TestUser2 findById(int id) {

		// jdbc 必备
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		// 用来存放查询得到的用户信息
		TestUser2 user = null;

		// 数据库查询字符串，其中的 '?' 号是一个占位符，预编译的方式（prepareStatement）需要这种写法
		String strSql = "SELECT * FROM `user` WHERE id=?";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			stmt.setInt(1, id); // 将 id 值替换至 '?' 号占位的地方
			result = stmt.executeQuery();
			if (result.next()) {
				user = fetchData(result); // 提取数据库返回的信息
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(result, stmt, conn); // 关闭数据库连接
		}

		return user;
	}

	/***
	 * 查询数据库中是否已经存在相同用户名用户
	 * 
	 * @param user
	 *            查询的用户对象
	 * @return 是否存在
	 */
	public boolean isUserExist(TestUser2 user) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		String strSql = "SELECT * FROM `user` WHERE username=?";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			stmt.setString(1, user.getUsername());
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
	public boolean createUser(TestUser2 user) {
		Connection conn = null;
		PreparedStatement stmt = null;

		// 3 个占位符，分别对应位置为 1,2,3
		String strSql = "INSERT INTO `user` (username, password, authority) VALUES (?, ?, ?)";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);

			// 替换占位符位置的值
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPassword());
			stmt.setInt(3, user.getAuthority());

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
	 * 修改数据库中对应 id 用户信息
	 * 
	 * @param id
	 *            用户id
	 * @param user
	 *            用户信息
	 * @return 成功返回 true，失败返回 false
	 */
	public boolean updateUser(int id, TestUser2 user) {
		Connection conn = null;
		PreparedStatement stmt = null;

		String strSql = "UPDATE `user` SET `username`=?, `password`=?, `authority`=? WHERE (`id`=?)";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPassword());
			stmt.setInt(3, user.getAuthority());
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
	 * 删除指定 id 用户信息
	 * 
	 * @param id
	 *            用户id
	 * @return 成功返回 true，失败返回 false
	 */
	public boolean deleteUser(int id) {
		Connection conn = null;
		PreparedStatement stmt = null;

		String strSql = "DELETE FROM `user` WHERE (`id`=?)";

		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.prepareStatement(strSql);
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
