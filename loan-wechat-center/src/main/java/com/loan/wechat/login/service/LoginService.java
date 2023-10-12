package com.loan.wechat.login.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.loan.wechat.common.Result;


public interface LoginService {

	Result login(HttpServletRequest req,HttpServletResponse res);
	
	Result getLoginInfo(String userid);
	
}
