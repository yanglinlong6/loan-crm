/*
 * 文件名：CookieUtils.java 版权：深圳融信信息咨询有限公司 修改人：zhangqiuping 修改时间：@create_date
 * 2017年11月2日 上午9:22:31 修改内容：新增
 */
package com.loan.wechat.login.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * 浏览器cookie工具类
 * @author     zhangqiuping
 * @since      2.2.4
 * @create_date 2017年11月2日 上午9:22:31
 */
public class CookieUtils
{
	
	/**
	 * 调测日志记录器。
	 */
	private static final Logger logger = LoggerFactory.getLogger(CookieUtils.class);
	
	/**
	 * 创建根目录(/)下的cookie,
	 * 默认有效期时间为：Integer.MAX_VALUE
	 * @param response {@link HttpServletResponse}响应对象
	 * @param name cookie name
	 * @param value cookie有效路径
	 * @author zhangqiuping
	 */
	public static void createCookie(HttpServletResponse response,String name,String value)
	{
		createCookie(response,name,value,"/");
	}
	
	/**
	 * 创建目录为（path）下的cookie,
	 * @param response  {@link HttpServletResponse}响应对象
	 * @param name cookie name
	 * @param value cookie值
	 * @param path cookie有效路径
	 * @author zhangqiuping
	 */
	public static void createCookie(HttpServletResponse response,String name,String value,String path)
	{
		createCookie(response,name,value,"/",Integer.MAX_VALUE);
	}
	
	/**
	 * 创建目录为（path）下的cookie,有效时间为（maxAge）
	 * @param response  {@link HttpServletResponse}响应对象
	 * @param name cookie name
	 * @param value cookie值
	 * @param path cookie有效路径
	 * @param maxAge cookie有效时间
	 * @author zhangqiuping
	 */
	public static void createCookie(HttpServletResponse response,String name,String value,String path,Integer maxAge)
	{
		logger.info("创建cookie【name:" + name + ",value:" + value + ",path:" + path + ",maxAge:" + maxAge + "】");
		Cookie cookie = new Cookie(name,value);
		cookie.setPath(path);// 根目录有效
		cookie.setMaxAge(maxAge);// 永久保存
		response.addCookie(cookie);
	}
	
	/**
	 * 获取cookie数据
	 * @param request {@link HttpServletRequest} 请求对象
	 * @param name 浏览器中cookie name
	 * @return cookie数据
	 */
	public static String getCookieValue(HttpServletRequest request,String name)
	{
		if(null == request || StringUtils.isEmpty(name))
		{
			return null;
		}
		Cookie[] cookies = request.getCookies();
		if(null == cookies || cookies.length <= 0)
		{
			return null;
		}
		for(Cookie cookie:cookies)
		{
			logger.debug("cookies name：" + cookie.getName() + "，value" + cookie.getValue());
			if(name.equals(cookie.getName()))
			{
				return cookie.getValue();
			}
		}
		return null;
	}
	
	public static void removeCookie(HttpServletRequest request,HttpServletResponse response,String name) {
		Cookie[] cookies = request.getCookies();
		if(null == cookies || cookies.length <= 0)
		{
			return;
		}
		for(Cookie cookie:cookies)
		{
			logger.debug("cookies name：" + cookie.getName() + "，value" + cookie.getValue());
			if(name.equals(cookie.getName()))
			{
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}
}
