package com.loan.wechat.docking.cache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.loan.wechat.common.HttpUtil;
import com.loan.wechat.common.RedisClient;
import com.loan.wechat.docking.dao.WechatBaseDataDao;
import com.loan.wechat.docking.entity.WechatDTO;
import com.loan.wechat.docking.util.WXCenterJointUtil;

/**
 * 微信公众号信息缓存
 * @author kongzhimin
 *
 */
@Component
public class IWechatCache implements InitializingBean,WechatListReplace,DisposableBean{
	
	/**
	 * 微信公众号基本信息服务注入
	 */
	@Autowired
	private WechatBaseDataDao wechat;
	
	@Autowired
	private RedisClient redis;//IWechatCache初始化依赖      使RedisClient先初始化
	
	private static final String WECHAT_LIST_REDIS_CACHE = "WECHAT_LIST_REDIS_CACHE";
	
	private static Logger logger = LoggerFactory.getLogger(IWechatCache.class);
	
	
	private static void setRedisList(List<WechatDTO> list) {
		RedisClient.set(WECHAT_LIST_REDIS_CACHE, JSON.toJSONString(list));
	}
	
	/**
	 * 获取公众号列表对象
	 * @return
	 */
	private static List<WechatDTO> getRedisList() {
		return JSON.parseArray(RedisClient.get(WECHAT_LIST_REDIS_CACHE),WechatDTO.class) ;
	}
	
	/**
	 * 根据微信公众号二级域名配置获取公众号信息
	 * @param domain2 二级域名配置
	 * 
	 */
	public static WechatDTO getWechatDTOByDomain2(String domain2) {
		List<WechatDTO> list = getRedisList();
		for(WechatDTO dto:list) {
			if(dto.getDomain2().equals(domain2)) {
				return dto; 
			}
		}
		throw new RuntimeException();
	}
	
	/**
	 * 根据OPENID前6位获取公众号信息
	 *
	 */
	public static WechatDTO getWechatDTOByOpenId(String openId) {
		List<WechatDTO> list = getRedisList();
		for(WechatDTO dto:list) {
			if(openId.startsWith(dto.getOpenid())) {
				return dto; 
			}
		}
		throw new RuntimeException();
	}
	
	/**
	 * 根据wxtype获取公众号信息
	 *
	 */
	public static WechatDTO getWechatDTOByType(Integer wxType) {
		List<WechatDTO> list = getRedisList();
		
		for(WechatDTO dto:list) {
			if(dto.getWxType().equals(wxType)) {
				return dto; 
			}
		}
		throw new RuntimeException();
	}
	
	/**
	 * 刷新微信公众号列表缓存
	 */
	public void replaceWechatList() {
		setWechatsToken( wechat.getAllWechatsList());
	}
	
	/**
	 * 根据微信公众号原始ID获取公众号信息
	 * @param wechatId 微信公众号原始ID
	 * 
	 */
	public static WechatDTO getWechatDTOByWechatId(String wechatId) {
		List<WechatDTO> list = getRedisList();
		for(WechatDTO dto:list) {
			if(dto.getWechatId().equals(wechatId)) {
				return dto; 
			}
		}
		throw new RuntimeException();
	}
	
	/**
	 * 每110分钟刷新公众号接口调用凭证TOKEN
	 */
	@Scheduled(fixedRate = 110*60*1000)
	public void execute() {
		if (isFailure()) {
			setWechatsToken(wechat.getAllWechatsList());
			setFailureTime();
		}
	}
	
	/**
	 * 获取微信公众号列表 接口调用凭证TOKEN JS-SDK加密TICKET
	 * @param allWechatsList 微信公众号列表
	 */
	private void setWechatsToken(List<WechatDTO> allWechatsList) {
		for(WechatDTO dto:allWechatsList) {
			try {
				dto.setToken(WXCenterJointUtil.getToken(dto.getAppId(),dto.getAppSecret()));
				dto.setJsapi(WXCenterJointUtil.getJSapiTicket(dto.getToken()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			setRedisList(allWechatsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 失效时间 KEY值
	 */
	private static final String FAILURE_TIME_KEY = "WECHAT_TOKEN_FAILURE_TIME_KEY";
	
	/**
	 * 缓存微信公众号列表在类实例化时初始化
	 * 
	 */
	public void afterPropertiesSet() throws Exception {
		setWechatsToken(wechat.getAllWechatsList());
		setFailureTime();
	}
	/**
	 * 检查缓存公众号列表是否到失效时间
	 * @return
	 */
	private boolean isFailure() {
		String check = RedisClient.get(FAILURE_TIME_KEY);
		if(StringUtils.isEmpty(check)) {
			return true;
		}
		return false;
	}
	/**
	 * 设置公众号列表可刷新时间点
	 */
	private void setFailureTime() {
		RedisClient.setex(FAILURE_TIME_KEY, "A"+System.currentTimeMillis(), 105*60l);
	}
	
	/**
	 * 清楚公众号列表缓存
	 */
	public void destroy() throws Exception {
	}
	
}
