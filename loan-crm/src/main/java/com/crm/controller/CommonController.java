package com.crm.controller;

import com.crm.common.ResultVO;
import com.crm.service.city.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公共接口控制器
 */
@RestController
public class CommonController {

    private static final Logger LOG = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    CityService cityService;


    @GetMapping("/common/city")
    @ResponseBody
    public ResultVO  allCity(@RequestParam(value = "level", required = false, defaultValue = "2") Byte level){
        return ResultVO.success("获取全部城市成功",cityService.getCityForTree(cityService.getAll(level)));
    }


}
