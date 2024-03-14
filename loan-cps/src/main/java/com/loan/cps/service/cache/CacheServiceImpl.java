package com.loan.cps.service.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.loan.cps.dao.CacheConfigDao;
import com.loan.cps.service.cache.model.CacheConfigPO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceImpl implements CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheServiceImpl.class);
    @Autowired
    CacheConfigDao cacheConfigDao;
    @Autowired
    RedisTemplate redisTemplate;

    private static final String SEPARATOR = "_";


    @Override
    public List<CacheConfigPO> getAll() {
        List<CacheConfigPO> list = cacheConfigDao.selectAllCacheConfig();
        if (CollectionUtil.isEmpty(list)) {
            return list;
        }
        for (CacheConfigPO cacheConfig : list) {
            log.info("缓存：field-{},key={},value={}", cacheConfig.getField(), cacheConfig.getKey(), cacheConfig.getValue());
            redisTemplate.opsForHash().put(cacheConfig.getField(), cacheConfig.getKey(), cacheConfig.getValue());
        }
        return list;
    }

    @Override
    public String getValue(String field, String key) {
        if (StringUtils.isBlank(field) || StringUtils.isBlank(key)) return null;
        String key2 = field + SEPARATOR + key;
        if (redisTemplate.hasKey(key2)) {
            Object value = redisTemplate.opsForValue().get(key2);
            if (null == value || StringUtils.isBlank(value.toString())) {
                return getDatabaseValue(field, key);
            }
            return value.toString();
        } else {
            return getDatabaseValue(field, key);
        }

    }

    @Override
    public String getValueByEnCity(String enCity) {
        CacheConfigPO valueByEnCity = cacheConfigDao.getValueByEnCity(enCity);
        if (Objects.isNull(valueByEnCity)) {
            return "";
        }
        return valueByEnCity.getValue();
    }

    public String getDatabaseValue(String field, String key) {
        CacheConfigPO configPO = cacheConfigDao.selectCacheCofing(field, key);
        if (null == configPO) {
            return null;
        }
        String key2 = field + SEPARATOR + key;
        redisTemplate.opsForValue().set(key2, configPO.getValue(), 10, TimeUnit.MINUTES);
        return configPO.getValue();
    }


}
