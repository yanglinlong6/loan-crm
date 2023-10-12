package com.loan.wechat.msg.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

import com.alibaba.fastjson.JSONObject;
import com.loan.wechat.common.HttpUtil;

public class Demo {
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		String encode = "UTF-8";
		
		String username = "13049692800";  //用户名
		MessageDigest instance = MessageDigest.getInstance("MD5");
//		instance.digest(input);
		String password_md5 = "asdasd123";  //密码
		byte[] digest = instance.digest(password_md5.getBytes());
		password_md5 = Hex.encodeHexString(digest);
		String mobile = "13049692800";  //手机号,只发一个号码：13800000001。发多个号码：13800000001,13800000002,...N 。使用半角逗号分隔。
		
		String apikey = "aca94d1a3112908e37bf42e85bfbe0d2";  //apikey秘钥（请登录 http://m.5c.com.cn 短信平台-->账号管理-->我的信息 中复制apikey）
		
		String content = "您好，您的验证码是：12345【美联】";  //要发送的短信内容，特别注意：签名必须设置，网页验证码应用需要加添加【图形识别码】。
		String postForObject = HttpUtil.getForObject("http://m.5c.com.cn/api/send/index.php?username="+username+"&password_md5="+password_md5+"&mobile="+mobile+"&apikey="+apikey+"&content="+content+"&encode="+encode);
		System.out.println(postForObject);
	}
	
}
