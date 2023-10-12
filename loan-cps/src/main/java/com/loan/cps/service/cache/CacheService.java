package com.loan.cps.service.cache;


import com.loan.cps.service.cache.model.CacheConfigPO;

import java.util.List;

public interface CacheService {


    List<CacheConfigPO> getAll();

    String getValue(String field,String key);

}
