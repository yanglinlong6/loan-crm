package com.loan.cps.dao;

import com.loan.cps.service.cache.model.CacheConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface CacheConfigDao {


    List<CacheConfigPO> selectAllCacheConfig();

    CacheConfigPO selectCacheCofing(@Param("field") String field, @Param("key")String key);


}