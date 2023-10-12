package com.daofen.admin.controller.city;

import com.daofen.admin.basic.ResultCode;
import com.daofen.admin.basic.ResultVO;
import com.daofen.admin.service.city.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CityController {

    @Autowired
    private CityService cityService;


    @RequestMapping("/city")
    @ResponseBody
    public ResultVO city(){
        return new ResultVO(ResultCode.SUC,cityService.getCityForTree(Byte.valueOf("2")));
    }
}
