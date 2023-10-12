/*
 * 文件名：EfinancialConstant.java 版权：深圳融信信息咨询有限公司 修改人：zhangqiuping
 * 修改时间：@create_date 2018年3月19日 下午6:00:44 修改内容：新增
 */
package com.loan.wechat.login.constant;

/**
 * 
 * 小易顾问常量类
 * @author     zhangqiuping
 * @since      2.2.4
 * @create_date 2018年3月19日 下午6:00:44
 */
public interface LoginConstant {
	/**
	/**cookies常量*/
	public interface Cookie {
		/**cookies 名称*/
		String NAME = "l_access_cookie";
	}
	
	/**
	/**Redis常量*/
	public interface Redis {
		/**cookies 名称*/
		String USER_LOGIN = "USER_LOGIN_";
		/** 原公众号前缀 */
		String OPENID_PREFIX= "oHNQM0";
	}
	
	public interface Session{
		
		Integer INIT_STATE = 0;
		
	}
	
}
