package com.crm.controller.statistic;

import com.crm.common.ResultVO;
import com.crm.service.statistic.HomeStatisticService;
import com.crm.service.statistic.model.CustomerLabelBO;
import com.crm.service.statistic.model.CustomerStateBO;
import com.crm.service.statistic.model.HomeIncomeSortBO;
import com.crm.service.statistic.model.HomeStatisticBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomeController {

    @Autowired
    HomeStatisticService homeStatisticService;

    @PostMapping("/home")
    @ResponseBody
    public ResultVO home(){
        List<HomeStatisticBO> list = homeStatisticService.getHomeStatistic();
        return ResultVO.success("首页数据统计成功",list);
    }


    /**
     * 首页 业绩排行
     * @param homeIncomeSortBO HomeIncomeSortBO
     * @return ResultVO
     */
    @PostMapping("/home/income/sort")
    @ResponseBody
    public ResultVO incomeSort(@RequestBody() HomeIncomeSortBO homeIncomeSortBO){
        return ResultVO.success("首页排行榜获取成功",homeStatisticService.getIncomeSort(homeIncomeSortBO));
    }

    /**
     * 首页 客户状态统计
     * @param customerStateBO CustomerStateBO
     * @return ResultVO
     */
    @PostMapping("/home/customer/state")
    @ResponseBody
    public ResultVO customerState(@RequestBody() CustomerStateBO customerStateBO){
        return ResultVO.success("首页排行榜获取成功",homeStatisticService.getProcessStatistic(customerStateBO));
    }

    @PostMapping("/home/customer/label")
    @ResponseBody
    public ResultVO customerLabel(@RequestBody() CustomerLabelBO bo){
        return ResultVO.success("首页排行榜获取成功",homeStatisticService.getLabelStatistic(bo));
    }




}
