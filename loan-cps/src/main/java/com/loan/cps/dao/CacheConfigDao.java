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

    CacheConfigPO selectCacheCofing(@Param("field") String field, @Param("key") String key);

    /**
     * 通过英文城市名转中文城市名
     *
     * @param enCity 英文城市名
     * @return 中文城市名
     */
    CacheConfigPO getValueByEnCity(@Param("enCity") String enCity);
}
