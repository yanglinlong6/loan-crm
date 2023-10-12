package com.loan.wechat.login.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loan.wechat.common.Result;
import com.loan.wechat.login.service.LoginService;


@RestController
public class LoginController {
	
	
	@Autowired
	private LoginService loginService;
	
	/**
	 *	用户网页登录 
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping("/wechat/login/do")
	public Result login(HttpServletRequest req, HttpServletResponse res)
	{	
		return loginService.login(req, res);
	}
	
	
}
