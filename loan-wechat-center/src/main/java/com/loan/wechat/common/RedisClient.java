package com.loan.wechat.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis java客户端
 * <p>
 * TODO 详细描述
 *
 *
 * @author luduntao
 * @since 2.2.4
 * @create_date 2017-7-31 下午4:37:09
 */
@Component
public class RedisClient {

	
	private static  RedisTemplate<String, Object> redisTemplate;

	private static Logger logger = LoggerFactory.getLogger(RedisClient.class);
	
	
	@Autowired
	public RedisClient(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * 向缓存中设置字符串内容
	 *
	 * @param key   key
	 * @param value value
	 * @return
	 * @throws Exception
	 */
	public static  boolean set(String key, String value,Long timeout) {
		try {
			redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	
	/**
	 * 向缓存中设置字符串内容
	 *
	 * @param key   key
	 * @param value value
	 * @return
	 * @throws Exception
	 */
	public static  boolean set(String key, String value) {
		try {
			redisTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 删除缓存中得对象，根据key
	 *
	 * @param key
	 * @return
	 */
	public static  boolean del(String key) {

		try {
			redisTemplate.delete(key);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根据key 获取内容
	 *
	 * @param key
	 * @return
	 */
	public static  String get(String key) {
		try {
			return (String) redisTemplate.opsForValue().get(key);
		} catch (Exception e) {
			logger.error(key + "=========从redis获取异常=========" + e.getMessage(), e);
			return null;
		}
	}

	public static  void expire(String key, Long seconds) {
		try {
			redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setex(String key,String value,Long expire) {
		redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
	}

	public static String hget(String key,String field) {
		Object object = redisTemplate.opsForHash().get(key, field);
		return object==null?null:object.toString();
	}
	
	public static void hset(String key,String field,String value) {
		redisTemplate.opsForHash().put(key, field, value);
	}
	
	public static Map<Object, Object> hgetAll(String key) {
		return redisTemplate.opsForHash().entries(key);
	}
	
}
