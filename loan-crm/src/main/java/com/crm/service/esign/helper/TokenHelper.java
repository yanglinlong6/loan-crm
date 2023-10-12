package com.crm.service.esign.helper;

import com.alibaba.fastjson.JSONObject;
import com.crm.service.esign.util.DefineException;
import com.crm.service.esign.vo.CacheKeyConstant;
import com.crm.service.esign.vo.ConfigConstant;
import com.crm.service.esign.vo.RequestType;
import com.crm.util.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @description 鉴权认证 辅助类
 * @author 宫清
 * @date 2019年7月19日 上午11:54:28
 * @since JDK1.7
 */
public class TokenHelper {

	/**
	 * 不允许外部创建实例
	 */
	private TokenHelper() {
	}

	
	
	// -------------------------------------------公有方法start-------------------------------------------------------

	/**
	 * @description 获取token
	 * 
	 *              实际使用中，可获取token后，放在redis缓存中，如果是非分布式架构，也可使用guava的LoadingCache或者自己构建本地缓存来存储该token，
	 *              缓存存储时，需要注意缓存失效截止时间expiresIn
	 * 
	 * @date 2019年7月19日 上午11:55:57
	 * @author 宫清
	 * @throws DefineException
	 */
	public static JSONObject getTokenData() throws DefineException {
		Object token = LocalCacheHelper.get(CacheKeyConstant.TOKEN);
		if(null != token){
			return JSONUtil.toJSON(token);
		}
		JSONObject json = HttpHelper.doCommHttp(RequestType.GET,
				ConfigConstant.getToken_URL(ConfigConstant.PROJECT_ID, ConfigConstant.PROJECT_SECRET), null);
		json = JSONHelper.castDataJson(json,JSONObject.class);

		// 模拟存放本地缓存
		toLocalCache(json);
		return json;

	}

	public static JSONObject getTokenData(StringRedisTemplate stringRedisTemplate) throws DefineException {
		if(null != stringRedisTemplate){
			String tokenString = stringRedisTemplate.opsForValue().get(CacheKeyConstant.TOKEN);
			if(StringUtils.isNotBlank(tokenString)){
				JSONObject json = JSONUtil.toJSON(tokenString);
				toLocalCache(json);
				return json;
			}
		}
		JSONObject json = HttpHelper.doCommHttp(RequestType.GET,
				ConfigConstant.getToken_URL(ConfigConstant.PROJECT_ID, ConfigConstant.PROJECT_SECRET), null);
		json = JSONHelper.castDataJson(json,JSONObject.class);
		if(null != stringRedisTemplate){
			stringRedisTemplate.opsForValue().set(CacheKeyConstant.TOKEN,json.toJSONString(),110l, TimeUnit.MINUTES);
		}
		// 模拟存放本地缓存
		toLocalCache(json);
		return json;

	}

	/**
	 * @description 刷新token
	 * 
	 *              刷新token后，新旧token共存时间为5分钟，实际使用中，若使用redis或者guava的LoadingCache或自建本地缓存的方式来存储token，请注意刷新缓存时间
	 * 
	 * @param refreshToken {@link String} 之前获取token时返回的refreshToken
	 * @date 2019年7月19日 上午11:55:57
	 * @author 宫清
	 * @throws DefineException
	 */
	public static JSONObject refreshToken(String refreshToken,StringRedisTemplate stringRedisTemplate) throws DefineException {
		JSONObject json = HttpHelper.doCommHttp(RequestType.GET,
				ConfigConstant.refreshToken_URL(ConfigConstant.PROJECT_ID, refreshToken), null);
		json = JSONHelper.castDataJson(json,JSONObject.class);
		// 模拟存放本地缓存
		if(null != stringRedisTemplate){
			stringRedisTemplate.opsForValue().set(CacheKeyConstant.TOKEN,json.toJSONString(),110l, TimeUnit.MINUTES);
		}

		return json;
	}

	/**
	 * @description 刷新token
	 *
	 *              刷新token后，新旧token共存时间为5分钟，实际使用中，若使用redis或者guava的LoadingCache或自建本地缓存的方式来存储token，请注意刷新缓存时间
	 *
	 * @param refreshToken {@link String} 之前获取token时返回的refreshToken
	 * @date 2019年7月19日 上午11:55:57
	 * @author 宫清
	 * @throws DefineException
	 */
	public static JSONObject refreshToken(String refreshToken) throws DefineException {
		JSONObject json = HttpHelper.doCommHttp(RequestType.GET,
				ConfigConstant.refreshToken_URL(ConfigConstant.PROJECT_ID, refreshToken), null);
		json = JSONHelper.castDataJson(json,JSONObject.class);

		// 模拟存放本地缓存
		toLocalCache(json);

		return json;
	}
	
	// -------------------------------------------公有方法end---------------------------------------------------------

	// -------------------------------------------私有方法start-------------------------------------------------------

	/**
	 * @description 模拟存放本地缓存
	 *
	 * @param json
	 * @author 宫清
	 * @date 2019年7月20日 下午4:04:08
	 */
	private static void toLocalCache(JSONObject json) {
		LocalCacheHelper.put(CacheKeyConstant.TOKEN, json.getString("token"));
		LocalCacheHelper.put(CacheKeyConstant.REFRESH_TOKEN, json.getString("refreshToken"));
	}

	// -------------------------------------------私有方法end---------------------------------------------------------
}
