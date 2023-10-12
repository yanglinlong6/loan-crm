package com.loan.wechat.msg.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.loan.wechat.common.HttpUtil;
import com.loan.wechat.common.MD5Util;

@Component
public class MLRTMsgUtil implements MsgTool,InitializingBean{
	
	@Value("${msg.mlrt.username}")
	private String username;
	@Value("${msg.mlrt.password}")
	private String password;
	@Value("${msg.mlrt.apikey}")
	private String apikey;
	@Value("${msg.mlrt.uri}")
	private String url;
	
	private String password_md5;
	
	private String encode = "UTF-8";
	
	@Override
	public String send(String mobile, String content) {
		return HttpUtil.getForObject(String.format(url, username,password_md5,apikey,mobile,content,encode));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		password_md5 = MD5Util.digest(password);
	}
	
	
}
