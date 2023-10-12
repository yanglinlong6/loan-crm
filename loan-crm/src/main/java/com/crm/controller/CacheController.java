package com.crm.controller;

import com.crm.common.ResultVO;
import com.crm.service.cache.CacheConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CacheController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);
    @Autowired
    CacheConfigService cacheConfigService;

    @PostMapping("/cache")
    @ResponseBody
    public ResultVO freshCache(){
        cacheConfigService.getAllCacheConfig();
        return ResultVO.success("刷新缓存成功 ","");
    }
}
