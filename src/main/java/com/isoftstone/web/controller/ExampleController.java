package com.isoftstone.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isoftstone.web.dao.TestUserDao2;
import com.isoftstone.web.pojo.TestUser2;

@RestController
public class ExampleController {
	
	private TestUserDao2 userDao = new TestUserDao2();
	
	
	/***
	 * 通过给 listAllUsers 方法添加可选 GET 参数实现 条件查找
	 * 查询用户，如果含 name 参数，则查询符合条件的用户；不含 name 参数，则查询所有用户
	 * url:http://www.example.com/api/user{?name=test}
	 * method:GET
	 * @return 所有用户的信息
	 */
	@RequestMapping(value = "/api/user", method = RequestMethod.GET)
	public ResponseEntity<List<TestUser2>> listAllUsers(
			@RequestParam(value = "name", required = false) String name) {
		
		// 如果请求数据时带上了 name 参数，则查找符合要求的用户，否则列出所有用户
		if(name != null) {
			return getUserByName(name);
		}
		
		
		List<TestUser2> users = userDao.findAllUsers();
		
		// 如果查询不到用户，将 http 状态码设置为 NO_CONTENT
		if(users.isEmpty()) {
			return new ResponseEntity<List<TestUser2>>(HttpStatus.NO_CONTENT);
		}
		
		// 如果正确查询，则返回所有用户，同时将 http 状态码设为 OK
		return new ResponseEntity<List<TestUser2>>(users, HttpStatus.OK);
	}
	

	
	
	/**
	 * 通过 name 进行模糊查询
	 * @param name
	 * @return
	 */
	public ResponseEntity<List<TestUser2>> getUserByName(String name) {
		List<TestUser2> listUser = userDao.findByName(name);
		if(listUser.isEmpty()) {
			return new ResponseEntity<List<TestUser2>>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<List<TestUser2>>(listUser, HttpStatus.OK);
	}
	
	/***
	 * 获取单个用户信息，指定 id
	 * url:http://www.example.com/api/user/1
	 * method:GET
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/api/user/{id}", method = RequestMethod.GET)  // {id} 表示路径中这个位置是一个变化的参数，参数名为 id
	public ResponseEntity<TestUser2> getUser(@PathVariable("id") int id) {  // 在参数 id 前添加 PathVariable，表示这个参数对应路径中的 id 值
		System.out.println("获取用户通过id=" + id);
		TestUser2 user = userDao.findById(id);
		if(null == user) {
			System.out.println("用户没有找到 id=" + id);
			return new ResponseEntity<TestUser2>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<TestUser2>(user, HttpStatus.OK);
	}
	
	
	
	
	/***
	 * 创建一个用户
	 * url：http://www.example.com/api/user
	 * method：POST
	 * @param user 全哥提交 json 格式的完整的 user 信息
	 * @return 创建成功，返回创建的用户的信息
	 */
	//创建新的用户(只限于行政部、总务部人员)
	@RequestMapping(value = "/api/user", method = RequestMethod.POST)
	// 下面一行 RequestBody 这个注解表示全哥会传一个 json对象过来，对应 TestUser2，框架会自动将信息提取到参数 user 中，测试方法在 coding
	public ResponseEntity<TestUser2> createUser(@RequestBody TestUser2 user) { 
		System.out.println("创建用户 " + user.getUsername());
		// 检查提交的数据是否完整，如果不完整，将状态码设置为 BAD_REQUEST
		if(null == user.getUsername() || 0 == user.getAuthority()) {
			return new ResponseEntity<TestUser2>(HttpStatus.BAD_REQUEST);
		}
		//检测是否是行政部或者总务部
		if(userDao.isUserTrue(user)){
			System.out.println("用户 " + user.getUsername() + "不是行政部或者总务部人员");
			return new ResponseEntity<TestUser2>(HttpStatus.CONFLICT);
		}
		// 检测用户名是否冲突
		if(userDao.isUserExist(user)) {
			System.out.println("用户 " + user.getUsername() + "已存在");
			return new ResponseEntity<TestUser2>(HttpStatus.CONFLICT);
		}
		
		
		// 创建用户
		if(userDao.createUser(user)) {
			return new ResponseEntity<TestUser2>(user, HttpStatus.CREATED);
		}
		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<TestUser2>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	/***
	 * 修改对应 id 用户的信息，可以只修改部分……我们不用 PATCH 了
	 * @param id
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/api/user/{id}", method = RequestMethod.PUT)
	public ResponseEntity<TestUser2> updateUser(@PathVariable("id") int id, @RequestBody TestUser2 user) {
		System.out.println("更新用户信息，id=" + id);
		
		// 通过 id 找到用户当前的信息
		TestUser2 currentUser = userDao.findById(id);
		
		if(null == currentUser) {
			System.out.println("没有找到该用户，id=" + id);
			return new ResponseEntity<TestUser2>(HttpStatus.NOT_FOUND);
		}
		
		// 修改需要修改的属性
		if(user.getUsername() != null)
			currentUser.setUsername(user.getUsername());
		if(user.getPassword() != null) 
			currentUser.setPassword(user.getPassword());
		if(user.getAuthority() != 0)
			currentUser.setAuthority(user.getAuthority());
		
		// 数据库操作
		if(userDao.updateUser(id, currentUser)) {
			return new ResponseEntity<TestUser2>(currentUser, HttpStatus.OK);
		}
		
		// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
		return new ResponseEntity<TestUser2>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	/***
	 * 删除对应 id 用户信息
	 * @param id 用户id
	 * @return
	 */
	@RequestMapping(value = "/api/user/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<TestUser2> deleteUser(@PathVariable("id") int id) {
		System.out.println("删除一个用户 id=" + id);
		
		// 检测对应 id 用户是否存在，不存在将状态码设为 NOT_FOUND
		TestUser2 user = userDao.findById(id);
		if(null == user) {
			System.out.println("找不到 id=" + id + " 的用户");
			return new ResponseEntity<TestUser2>(HttpStatus.NOT_FOUND);
		}
		
		// 数据库操作
		if(userDao.deleteUser(id)) {
			System.out.println();
			return new ResponseEntity<TestUser2>(HttpStatus.NO_CONTENT);
		}
		
		return  new ResponseEntity<TestUser2>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
