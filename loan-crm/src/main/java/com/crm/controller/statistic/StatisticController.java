package com.crm.controller.statistic;

import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.service.statistic.StatisticService;
import com.crm.service.statistic.model.CustomerStatisticBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Result;

/**
 * 统计控制器
 */
@RestController
public class StatisticController {

    @Autowired
    StatisticService statisticService;


    @PostMapping("/statistic/personal/page")
    @ResponseBody
    public ResultVO personalStatisticPage(@RequestBody() PageBO<CustomerStatisticBO> page){
        statisticService.getPersonalStatisticPage(page);
        page.setParamObject(null);
        return ResultVO.success("个人统计-分页成功",page);
    }


    @PostMapping("/statistic/shop/page")
    @ResponseBody
    public ResultVO shopStatisticPage(@RequestBody() PageBO<CustomerStatisticBO> page){
        statisticService.getShopStatisticPage(page);
        page.setParamObject(null);
        return ResultVO.success("客户统计-团队统计成功",page);
    }


    @PostMapping("/statistic/team/page")
    @ResponseBody
    public ResultVO teamStatisticPage(@RequestBody() PageBO<CustomerStatisticBO> page){
        statisticService.getTeamStatisticPage(page);
        page.setParamObject(null);
        return ResultVO.success("客户统计-团队统计成功",page);
    }

}
