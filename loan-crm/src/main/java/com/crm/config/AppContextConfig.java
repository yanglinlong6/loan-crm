package com.crm.config;

import com.crm.common.CrmConstant;
import com.crm.service.cache.CacheConfigService;
import com.crm.util.AppContextUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class AppContextConfig implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppContextUtil.set(applicationContext);
        AppContextUtil.getBean(CacheConfigService.class).getAllCacheConfig();// 加载缓存
        RedisTemplate redisTemplate = applicationContext.getBean("redisTemplate",RedisTemplate.class);
        redisTemplate.opsForValue().set(CrmConstant.Config.Socket.WEB_SOCKET_COUNTS,0);
    }
}
