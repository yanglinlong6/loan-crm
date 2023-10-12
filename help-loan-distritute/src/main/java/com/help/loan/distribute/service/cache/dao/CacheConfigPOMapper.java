package com.help.loan.distribute.service.cache.dao;

import com.help.loan.distribute.service.cache.model.CacheConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface CacheConfigPOMapper {


    List<CacheConfigPO> selectAllCacheConfig();

    /**
     *
     * @param field
     * @param key
     * @return CacheConfigPO
     */
    CacheConfigPO selectCacheCofing(@Param("field") String field, @Param("key")String key);

}