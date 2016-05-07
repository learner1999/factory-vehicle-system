package com.isoftstone.web.controller;

import java.io.IOException;
import java.util.Date;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isoftstone.web.pojo.TestUser;


//类名前加上这句注解，框架会自动识别当前类为 controller，且会自动将当前类中方法返回的 java 对象序列化成 json 对象
@RestController 				
public class TestRestfulController {

	/***
	 * 下面这句注解表示当前方法映射的路径和访问方式，也就是说当通过 get 方式访问 /getTestUser 时，会执行该方法，并返回一个 User
	 * 对象，而返回的 User 对象会被框架自动序列化为 json 字符串
	 * 
	 * @return User对象（实际是已经序列化为json对象的字符串）
	 */
	@RequestMapping(value = "/getTestUser", method = RequestMethod.GET)
	public TestUser getTestUser() {
		TestUser user = new TestUser();
		user.setUserName("小明");
		user.setUserId("xiaoming");
		user.setUserAge(12);
		user.setCreateDate(new Date());

		return user;
	}

	
	/***
	 * 访问路径  http://localhost/getUserName?name=123
	 * 显示结果  123
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/getUserName", method = RequestMethod.GET)
	public String getUserName(@RequestParam(value = "name") String name) {
		return name;
	}

	

	@RequestMapping("/getUserBody")
	public TestUser getUserBody(@RequestBody String body) {  // 加上 RequestBody 表示此处的参数是访问时传来的数据
		ObjectMapper mapper = new ObjectMapper();
		TestUser user = null;
		try {
			user = mapper.readValue(body, TestUser.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return user;
	}

}