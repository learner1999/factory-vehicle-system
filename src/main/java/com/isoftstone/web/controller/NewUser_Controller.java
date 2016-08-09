package com.isoftstone.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.isoftstone.web.dao.TestUserDao2;
import com.isoftstone.web.pojo.Emlopee;
import com.isoftstone.web.pojo.TestUser2;
@RestController
public class NewUser_Controller {
	private TestUserDao2 userDao = new TestUserDao2();
	/**
	 * 传入参数0(全部员工)或1(总务部)或者2(行政部),返回未创建员工信息
	 * @param part
	 * @return
	 */
	@RequestMapping(value = "/api/newuser/{part}", method = RequestMethod.GET)
	public ResponseEntity<List<Emlopee>> getuseridBypart(@PathVariable("part") int part) 
	{
		System.out.print("查询"+part+"部门的员工信息");
		List<Emlopee> useridList=userDao.findUsersidBypart(part);
		if(useridList.isEmpty()) {//结果为空
			System.out.print("查询失败");
			return new ResponseEntity<List<Emlopee>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Emlopee>>(useridList, HttpStatus.OK);
}
	
	
	/**
	 * 传入参数0(普通)或1(总务部)或者2(行政部),返回已创建员工信息
	 * @param part
	 * @return
	 */
	@RequestMapping(value = "/api/Newuser/{part}", method = RequestMethod.GET)
	public ResponseEntity<List<Emlopee>> getempBypart(@PathVariable("part") int part) 
	{
		System.out.print("查询"+part+"部门的员工信息");
		List<Emlopee> useridList=userDao.findEmpBypart(part);
		if(useridList.isEmpty()) {//结果为空
			System.out.print("查询失败");
			return new ResponseEntity<List<Emlopee>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Emlopee>>(useridList, HttpStatus.OK);
}
	
	/**
	 * 创建新的账户信息
	 * @param user
	 * @return
	 */
		@RequestMapping(value = "/api/newuser", method = RequestMethod.POST)
		// 下面一行 RequestBody 这个注解表示全哥会传一个 json对象过来，对应 TestUser2，框架会自动将信息提取到参数 user 中，测试方法在 coding
		public ResponseEntity<Emlopee> createUser(@RequestBody Emlopee user) { 
			System.out.println("创建新用户 " + user.getEid());
			// 检查提交的数据是否完整，如果不完整，将状态码设置为 BAD_REQUEST
			/*测是否是行政部或者总务部
			if(userDao.isUserTrue(user)){
				System.out.println("用户 " + user.getUsername() + "不是行政部或者总务部人员");
				return new ResponseEntity<TestUser2>(HttpStatus.CONFLICT);
			}*/
			// 检测用户名是否冲突
			if(userDao.isUserExist(user.getEid())) {
				System.out.println("用户 " + user.getEid() + "已存在");
				return new ResponseEntity<Emlopee>(HttpStatus.CONFLICT);
			}
		
			// 创建用户
			if(userDao.createUser(user)) {
				System.out.println("创建用户"+user.getEid()+"成功");
				return new ResponseEntity<Emlopee>(user, HttpStatus.CREATED);
			}
			// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
			return new ResponseEntity<Emlopee>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
      /**
       * 更新账户信息
       * @param user
       * @return
       */
		// 修改对应 id 用户的信息，可以只修改部分……我们不用 PATCH 了
		@RequestMapping(value = "/api/newuser", method = RequestMethod.PUT)
		public ResponseEntity<TestUser2> updateUser(@RequestBody TestUser2 user) {
			System.out.println("更新用户名为=" + user.getUsername()+"的信息");
			
			if(user.getUsername() == null||user.getPassword() == null||user.getAuthority() == 0){
				return new ResponseEntity<TestUser2>(HttpStatus.CONFLICT);
			}
			// 通过 用户名 找到用户当前的信息
			if(userDao.findByusername(user.getUsername())){
				System.out.println("未找到用户名为=" + user.getUsername()+"的用户");
				return new ResponseEntity<TestUser2>(HttpStatus.NOT_FOUND);
			}
			
			// 数据库操作
			if(userDao.updateUser(user)) {
				return new ResponseEntity<TestUser2>(user, HttpStatus.OK);
			}
			
			// 如果出现异常，将状态码设为 internal server error(寻找更好的解决方案中……)
			return new ResponseEntity<TestUser2>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	/**
	 * 注销账号信息
	 * @param id
	 * @return
	 */
		@RequestMapping(value = "/api/newuser/{eid}", method = RequestMethod.DELETE)
		public ResponseEntity<TestUser2> deleteUser(@PathVariable("eid") int eid) {
			System.out.println("删除一个用户名=" + eid);
			// 检测对应 id 用户是否存在，不存在将状态码设为 NOT_FOUND
			String username=String.valueOf(eid);
			if(userDao.findByusername(username)) {
				System.out.println("找不到 用户名=" +username+" 的用户");
				return new ResponseEntity<TestUser2>(HttpStatus.NOT_FOUND);
			}
			// 数据库操作
			if(userDao.deleteUser(username)) {
				System.out.println("删除成功");
				return new ResponseEntity<TestUser2>(HttpStatus.OK);
			}
			
			return  new ResponseEntity<TestUser2>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
			
}
