package com.crm.controller.channel;

import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.cache.CacheConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CityController {

    private static final Logger log = LoggerFactory.getLogger(CityController.class);

    @Autowired
    CacheConfigService cacheConfigService;




    @PostMapping("/city")
    @ResponseBody
    public ResultVO getCityList(){
        String value = cacheConfigService.getCacheConfigValue("city", LoginUtil.getLoginEmployee().getOrgId().toString());
        if(StringUtils.isBlank(value)){
            ResultVO.fail("城市列表未配置,请联系系统技术人员配置",null);
        }
        String[] array = value.split(",");
        return ResultVO.success("查询城市列表成功",array);
    }

}
