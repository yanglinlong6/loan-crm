package com.crm.service.cache;

import com.crm.service.cache.dao.CacheConfigMapper;
import com.crm.service.cache.model.CacheConfigPO;
import com.crm.util.ListUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CacheConfigServiceImpl implements CacheConfigService{

    private static final Logger log = LoggerFactory.getLogger(CacheConfigServiceImpl.class);

    @Autowired
    CacheConfigMapper cacheConfigMapper;

    @Autowired
    RedisTemplate redisTemplate;


    @Override
    public void getAllCacheConfig() {
        List<CacheConfigPO> list = cacheConfigMapper.selectAllCacheConfig();
        if(ListUtil.isEmpty(list))
            return;

        for(CacheConfigPO cacheConfig : list){
            log.info("加载缓存：{}",cacheConfig.toString());
            if(StringUtils.isBlank(cacheConfig.getField()) || StringUtils.isBlank(cacheConfig.getKey()) || StringUtils.isBlank(cacheConfig.getValue()))
                continue;
            redisTemplate.opsForHash().put(cacheConfig.getField(),cacheConfig.getKey(),cacheConfig.getValue());
        }
    }

    @Override
    public void getAllCacheConfig(String orgId) {
        List<CacheConfigPO> list = cacheConfigMapper.selectOrgConfig(orgId);
        if(ListUtil.isEmpty(list))
            return;
        for(CacheConfigPO cacheConfig : list){
            log.info("加载缓存：{}",cacheConfig.toString());
            if(StringUtils.isBlank(cacheConfig.getField()) || StringUtils.isBlank(cacheConfig.getKey()) || StringUtils.isBlank(cacheConfig.getValue()))
                continue;
            redisTemplate.opsForHash().put(cacheConfig.getField(),cacheConfig.getKey(),cacheConfig.getValue());
        }
    }

    @Override
    public String getCacheConfigValue(String field, String key) {
        if(redisTemplate.opsForHash().hasKey(field,key)){
            return redisTemplate.opsForHash().get(field,key).toString();
        }
        if(StringUtils.isBlank(field) || StringUtils.isBlank(key) )
            return null;
        CacheConfigPO po = cacheConfigMapper.selectCacheConfigPO(field,key);
        if(null == po)
            return null;
        redisTemplate.opsForHash().put(po.getField(),po.getKey(),po.getValue());
        return po.getValue();
    }

    @Override
    public List<CacheConfigPO> getConfig(Long orgId) {
        if(null == orgId || orgId <= 0){
            return null;
        }
        return cacheConfigMapper.selectOrgConfig(orgId.toString());
    }

    @Override
    public void addConfig(CacheConfigPO po) {
        cacheConfigMapper.insertConfig(po);
    }

    @Override
    public void updateConfig(CacheConfigPO po) {
        cacheConfigMapper.updateConfig(po);
    }
}
