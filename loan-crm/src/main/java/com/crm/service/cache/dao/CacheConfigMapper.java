package com.crm.service.cache.dao;


import com.crm.service.cache.model.CacheConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface CacheConfigMapper {

    List<CacheConfigPO> selectAllCacheConfig();

    CacheConfigPO selectCacheConfigPO(@Param("field") String field, @Param("key") String key);

    List<CacheConfigPO> selectOrgConfig(@Param("orgId")String orgId);

    int insertConfig(CacheConfigPO cacheConfigPO);

    int updateConfig(CacheConfigPO cacheConfigPO);



}