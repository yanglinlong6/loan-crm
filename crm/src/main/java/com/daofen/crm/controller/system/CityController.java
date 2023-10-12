package com.daofen.crm.controller.system;

import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.AbstractController;
import com.daofen.crm.service.city.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 城市模块控制器
 */
@RestController
public class CityController extends AbstractController {

    @Autowired
    private CityService cityService;


    /**
     * 获取省份，城市，区或者县数据
     * @param level 等级字段：1-获取只获取一级:省，2-获取两级:省和市，3-获取三级:省,市,区(或者县)
     * @return
     */
    @RequestMapping("/city")
    @ResponseBody
    public ResultVO getCity(@RequestParam(value = "level", required = false, defaultValue = "2") Byte level){
        return this.success(cityService.getCityForTree(level));
    }

    /**
     * 获取投放省份，城市，区或者县数据
     * @param level 等级字段：1-获取只获取一级:省，2-获取两级:省和市，3-获取三级:省,市,区(或者县)
     * @return
     */
    @RequestMapping("/mp/city")
    @ResponseBody
    public ResultVO getCityMp(@RequestParam(value = "level", required = false, defaultValue = "2") Byte level){
        return this.success(cityService.getMpCityForTree(level));
    }



}
