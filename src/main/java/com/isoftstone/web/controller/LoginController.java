package com.isoftstone.web.controller;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isoftstone.web.dao.LoginDao;

@RestController
public class LoginController {
	LoginDao loginDao = new LoginDao();
	/**
	 * 判断用户登录
	 * @param username
	 * @param password
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/api/login", method = RequestMethod.POST)
	public int login(@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password , HttpSession session) {
		if(session.getAttribute("userId") != null) {
			return 3; //已经登录了
		}
		int id = 0;
		if(loginDao.is_exist(username, password))
		{
			id = loginDao.login(username, password);
			if (0 == id) {
				System.out.println(" 0代表普通员工");
			} else if(1==id){
				System.out.println("1代表总务部");
			}
			else{
				System.out.println("2代表行政部");
			}
			session.setAttribute("userId", id);
			return id;
		}
		else
		{
			if(loginDao.is_username(username)){
				System.out.println("密码出错");
				return 4;
			}
			else{
				System.out.println("用户名不存在");
				return 5;
			}
			}
		}	
		
	}
/*
 *
 * 
		
 * 
 * */

