package com.loan.wechat.msg.controller;

import java.util.Random;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.loan.wechat.common.RedisClient;
import com.loan.wechat.common.Result;
import com.loan.wechat.docking.controller.WXController;
import com.loan.wechat.login.util.CookieUtils;
import com.loan.wechat.msg.util.MsgTool;

@RestController
public class MsgController {
	
	private static final String VER_CODE_COOKIE = "C19A6685_";
	
	private static final Log logger = LogFactory.getLog(MsgController.class);
	
	@Value("${msg.verification.code.content}")
	private String content;
	
	@Autowired
	private MsgTool msgTool;
	
	@RequestMapping("/msg/verification/send")
	public Result sendVerificationCode(HttpServletRequest req, HttpServletResponse res,@RequestParam String mobile)
	{	
		String cookie = getVerificationCodeCookie(req,res);
		String string1 = RedisClient.get("vc_"+mobile);
		Result result = new Result();
		if(!StringUtils.isEmpty(string1)) {
			result.setCode("00002");
			result.setMsg("验证码已发送，请输入正确验证码，验证码丢失请等待一分钟后重新发送");
			return result;
		}
		String string = RedisClient.get("vc_count_"+cookie);
		Integer count = 0;
		if(!StringUtils.isEmpty(string)) {
			count = Integer.valueOf(string);
		}
		if(count>=5) {
			result.setCode("00002");
			result.setMsg("验证码发送次数过多，请等待一段时间再操作");
			return result;
		}
		count++;
		Random r = new Random();
		Integer aa =  r.nextInt(1000000);
		String string2 = aa.toString();
		if(string2.length()<6) {
			int c = 6-string2.length();
			for(int i=0; i<c;i++) {
				string2 = "0"+string2;
			}
		}
		RedisClient.setex("vc_"+mobile, string2, 60l);
		RedisClient.setex("vc_count_"+cookie, count.toString(), 3*60*60l);
		msgTool.send(mobile, String.format(content, string2));
		return Result.success();
	}
	
	@RequestMapping("/msg/verification/check")
	public Result checkVerificationCode(HttpServletRequest req, HttpServletResponse res,@RequestParam String mobile,@RequestParam String vcode)
	{	
		String string = RedisClient.get("vc_"+mobile);
		Result result = Result.success();
		logger.info("verification msg code  mobile = "+mobile +" vcode ="+ string +" param =" +vcode);
		if(StringUtils.isEmpty(string)) {
			result.setCode("00002");
			result.setMsg("验证码已过期，请重新发送");
			return result;
		}
		if(!string.equals(vcode)) {
			result.setCode("00002");
			result.setMsg("验证码错误");
		}
		return result;
	}
	
	
	private String getVerificationCodeCookie(HttpServletRequest req, HttpServletResponse res) {
		String cookie = getVerificationCodeCookie(req);
		if(StringUtils.isEmpty(cookie)) {
			cookie = createUUID();
			CookieUtils.createCookie(res, VER_CODE_COOKIE, cookie,"/",-1);
		}
		return cookie;
	}
	
	private String getVerificationCodeCookie(HttpServletRequest req) {
		Cookie[] cookies = req.getCookies();
		if(cookies ==null) {
			return "";
		}
		String value = "";
		for(Cookie c:cookies) {
			if(c.getName().equals(VER_CODE_COOKIE)) {
				value = c.getValue();
			}
		}
		return value;
	}
	
	private String createUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
