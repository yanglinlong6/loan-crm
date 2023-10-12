package com.help.loan.distribute.controller;

import com.help.loan.distribute.common.ResultCode;
import com.help.loan.distribute.common.ResultVO;
import com.help.loan.distribute.service.api.utils.MainCity;
import com.help.loan.distribute.service.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {

    private static final Logger LOG = LoggerFactory.getLogger(CacheController.class);

    @Autowired
    CacheService cacheService;

    @RequestMapping("/refresh/cache")
    public ResultVO refreshCache(){
        cacheService.getAll();
        String value  = cacheService.getValue("advertising","city");
        if(StringUtils.isEmpty(value))
            MainCity.CITY = value;
        return new ResultVO(ResultCode.SUCCESS);
    }
}
