package com.help.loan.distribute.service.cache;

import com.help.loan.distribute.common.utils.CollectionUtil;
import com.help.loan.distribute.service.cache.dao.CacheConfigPOMapper;
import com.help.loan.distribute.service.cache.model.CacheConfigPO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CacheServiceImpl implements  CacheService{

    private static final Logger log = LoggerFactory.getLogger(CacheServiceImpl.class);
    @Autowired
    CacheConfigPOMapper cacheConfigMapper;
    @Autowired
    RedisTemplate redisTemplate;


    @Override
    public List<CacheConfigPO> getAll() {
        List<CacheConfigPO> list = cacheConfigMapper.selectAllCacheConfig();
        if(CollectionUtil.isEmpty(list)){
            return list;
        }
        for(CacheConfigPO cacheConfig : list){
            log.info("缓存：field-{},key={},value={}",cacheConfig.getField(),cacheConfig.getKey(),cacheConfig.getValue());
            redisTemplate.opsForHash().put(cacheConfig.getField(),cacheConfig.getKey(),cacheConfig.getValue());
        }
        return list;
    }

    @Override
    public String getValue(String field, String key) {
        if(StringUtils.isBlank(field) || StringUtils.isBlank(key))
            return null;

        if(redisTemplate.opsForHash().hasKey(field,key)){
            return redisTemplate.opsForHash().get(field,key).toString();
        }
        CacheConfigPO configPO = cacheConfigMapper.selectCacheCofing(field,key);
        if(null == configPO){
            return null;
        }
        redisTemplate.opsForHash().put(configPO.getField(),configPO.getKey(),configPO.getValue());
        return configPO.getValue();
    }


}
