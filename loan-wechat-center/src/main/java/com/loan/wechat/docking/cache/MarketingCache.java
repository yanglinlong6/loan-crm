package com.loan.wechat.docking.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.wechat.common.HttpUtil;
import com.loan.wechat.common.RedisClient;

/**
 * 	腾讯广告授权token缓存处理
 * @author kongzhimin
 *
 */
@Component
public class MarketingCache{
	
	private static final Logger logger = LoggerFactory.getLogger(MarketingCache.class);
	
	public static final String TENCENT_MARKETING_ACCESS_TOKEN="TENCENT_MARKETING_ACCESS_TOKEN";
	
	public static final String TENCENT_MARKETING_REFRESH_TOKEN="TENCENT_MARKETING_REFRESH_TOKEN";
	
	@Value("${tencent_marketing_client_id}")
	private static String clientId;
	
	@Value("${tencent_marketing_client_secret}")
	private static String clientSecret;
	
	public static String getMarketingToken(String accountid) {
		return RedisClient.hget(TENCENT_MARKETING_ACCESS_TOKEN, accountid);
	}
	
	public static String getRefreshToken(String accountid) {
		return RedisClient.hget(TENCENT_MARKETING_REFRESH_TOKEN, accountid);
	}
	
	/**
	 * 
	 */
	public static void refreshAccessToken() {
		logger.info("刷新腾讯广告API授权accesstoken 开始");
		Map<Object, Object> hgetAll = RedisClient.hgetAll(TENCENT_MARKETING_REFRESH_TOKEN);
		Iterator<Entry<Object, Object>> iterator = hgetAll.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<Object, Object> next = iterator.next();
			String forObject = HttpUtil.getForObject(String.format("https://api.e.qq.com/oauth/token?client_id=%s&client_secret=%s&grant_type=refresh_token&refresh_token=%s", clientId, clientSecret,next.getValue()));
			logger.info("腾讯广告API返回： "+forObject);
			JSONObject tencentResult = JSON.parseObject(forObject);
			if(tencentResult.getInteger("code").equals(0)) {
				JSONObject jsonObject = tencentResult.getJSONObject("data");
				RedisClient.hset(TENCENT_MARKETING_ACCESS_TOKEN, next.getKey().toString(), jsonObject.getString("access_token"));
				RedisClient.hset(TENCENT_MARKETING_REFRESH_TOKEN, next.getKey().toString(), jsonObject.getString("refresh_token"));
			}
		}
	}
	
	/**
	 * 
	 * @param authorizationCode
	 * @return
	 */
	public static String authorizationAccessToken(String authorizationCode) {
		logger.info("腾讯广告API授权accesstoken 开始");
		String doGet = HttpUtil.getForObject(String.format("https://api.e.qq.com/oauth/token?client_id=%s&client_secret=%s&grant_type=authorization_code&authorization_code=%s&redirect_uri=http://www.eqianzhuang.com", clientId,clientSecret,authorizationCode));
		logger.info("腾讯广告API返回： "+doGet);
		JSONObject tencentResult = JSON.parseObject(doGet);
		if(tencentResult.getInteger("code").equals(0)) {
			JSONObject jsonObject = tencentResult.getJSONObject("data");
			RedisClient.hset(TENCENT_MARKETING_ACCESS_TOKEN, jsonObject.getJSONObject("authorizer_info").getString("account_id"), jsonObject.getString("access_token"));
			RedisClient.hset(TENCENT_MARKETING_REFRESH_TOKEN, jsonObject.getJSONObject("authorizer_info").getString("account_id"), jsonObject.getString("refresh_token"));
		}
		return doGet;
	}
	
	public static void main(String[] args) {
		String doGet = HttpUtil.getForObject(String.format("https://api.e.qq.com/oauth/token?client_id=%s&client_secret=%s&grant_type=authorization_code&authorization_code=%s&redirect_uri=http://www.eqianzhuang.com", "1108725887", "y6CUObr9Q0fouNP1","597457039de51abe55512cc8002ac7a3"));
		System.out.println(doGet);
	}
}
