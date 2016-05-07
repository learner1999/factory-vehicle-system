package com.isoftstone.web.controller;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isoftstone.web.dao.LoginDao;

@RestController
public class LoginController {

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password, HttpSession session) {
		int id = 0;
		LoginDao loginDao = new LoginDao();

		id = loginDao.login(username, password);

		if (0 == id) {
			return "账号或密码错误";
		} else {
			session.setAttribute("userId", id);
			return "登录成功";
		}
	}
}
