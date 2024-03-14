package com.loan.cps.service.cache;


import com.loan.cps.service.cache.model.CacheConfigPO;

import java.util.List;

public interface CacheService {


    List<CacheConfigPO> getAll();

    String getValue(String field, String key);

    /**
     * 通过拼音城市名转中文城市名
     *
     * @param enCity 英文城市名
     * @return 中文城市名
     */
    String getValueByEnCity(String enCity);
}
