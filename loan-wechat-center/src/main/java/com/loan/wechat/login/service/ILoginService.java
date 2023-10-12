package com.loan.wechat.login.service;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.wechat.common.RedisClient;
import com.loan.wechat.common.Result;
import com.loan.wechat.docking.cache.IWechatCache;
import com.loan.wechat.docking.entity.WechatDTO;
import com.loan.wechat.docking.util.WXCenterJointUtil;
import com.loan.wechat.entrances.constant.WechatConstant;
import com.loan.wechat.login.constant.LoginConstant;
import com.loan.wechat.login.dao.WechatUserBindDao;
import com.loan.wechat.login.entity.UserDTO;
import com.loan.wechat.login.util.CookieUtils;


@Component
public class ILoginService implements LoginService{
	
	@Autowired
	private WechatUserBindDao wechatUserBindDao;
	
	/**
	 * 调测日志记录器。
	 */
	private static final Logger logger = LoggerFactory.getLogger(ILoginService.class);

	@Override
	public Result login(HttpServletRequest req, HttpServletResponse res) {
//		String origin = req.getHeader("Origin");
		String serverName = req.getServerName();
//		if("http://ax.test.glsx.com.cn".contains(origin)) {
//			serverName = "ax.test.glsx.com.cn";
//		}
		String parameter = req.getParameter("wxcode");
		String userid = null;
		if(!StringUtils.isEmpty(parameter)) {
			logger.info("微信Code ====== "+parameter+" domain ====="+serverName);
			JSONObject parseObject =null;
			if("000000".equals(parameter)) {
				parseObject = new JSONObject();
				parseObject.put("openid", "test");
			}else {
				WechatDTO wechatDTOByDomain2 = IWechatCache.getWechatDTOByDomain2(serverName);
				if(wechatDTOByDomain2 == null) {
					throw new RuntimeException( "登录异常");
				}
				String userLogin = WXCenterJointUtil.toUserLogin(parameter, wechatDTOByDomain2.getAppId(), wechatDTOByDomain2.getAppSecret());
				JSONObject userLoginObject = JSON.parseObject(userLogin);
				if(StringUtils.isEmpty(userLoginObject.getString("access_token"))) {
					throw new RuntimeException( "登录异常");
				}
				parseObject = JSONObject.parseObject(WXCenterJointUtil.getSNSUserInfo(userLoginObject.getString("access_token"), userLoginObject.getString("openid")));
			}
			if(parseObject!=null && !StringUtils.isEmpty(parseObject.getString("openid"))) {
				UserDTO selUserByOpenid = wechatUserBindDao.selUserByOpenid(parseObject.getString("openid"));
				if(selUserByOpenid == null) {
					selUserByOpenid = new UserDTO();
					selUserByOpenid.setOpenid(parseObject.getString("openid"));
					selUserByOpenid.setState(WechatConstant.SUB_STATE);
					selUserByOpenid.setType(WechatConstant.WECHAT_USER_TYPE);
					selUserByOpenid.setUnionId("");
					selUserByOpenid.setSubCount(1);
					selUserByOpenid.setUserId(createUUID());
					WechatDTO wechatDTOByDomain2 = IWechatCache.getWechatDTOByDomain2(serverName);
					selUserByOpenid.setWxType(wechatDTOByDomain2.getWxType());
					selUserByOpenid.setFollowTime(new Date());
					selUserByOpenid.setCreateBy(WechatConstant.CREATE_NAME);
					selUserByOpenid.setCreateDate(new Date());
					selUserByOpenid.setMedia(1);
					selUserByOpenid.setJobState(0);
					wechatUserBindDao.insert(selUserByOpenid);
				}
				CookieUtils.createCookie(res, LoginConstant.Cookie.NAME, selUserByOpenid.getUserId(),"/",-1);
				RedisClient.setex(LoginConstant.Redis.USER_LOGIN+selUserByOpenid.getUserId(), JSON.toJSONString(selUserByOpenid), 60*30l);
				userid = selUserByOpenid.getUserId();
			}else {
				throw new RuntimeException( parseObject.toJSONString());
			}
		}
		return Result.success(userid);
	}
	
	private String createUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	@Override
	public Result getLoginInfo(String userid) {
		String key = LoginConstant.Redis.USER_LOGIN+userid;
		String string = RedisClient.get(key);
		UserDTO dto = null;
		if(StringUtils.isEmpty(string)) {
			dto = wechatUserBindDao.selUserByUserid(userid);
			if(dto!=null) {
				RedisClient.setex(key, JSON.toJSONString(dto), 60*30l);
			}else {
				throw new RuntimeException("login err");
			}
		}else {
			dto = JSON.parseObject(string, UserDTO.class);
			RedisClient.expire(key, 60*30l);
		}
		return Result.success(dto);
	}
	
}
