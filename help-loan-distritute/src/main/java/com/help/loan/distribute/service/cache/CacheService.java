package com.help.loan.distribute.service.cache;

import com.help.loan.distribute.service.cache.model.CacheConfigPO;

import java.util.List;

public interface CacheService {


    List<CacheConfigPO> getAll();

    String getValue(String field,String key);

}
