package com.crm.controller.statistic;

import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.statistic.AchievementService;
import com.crm.service.statistic.model.AchievementBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AchievementController {

    @Autowired
    AchievementService achievementService;

    @PostMapping("/achieve/personal/page")
    @ResponseBody
    public ResultVO personalPage(@RequestBody() PageBO<AchievementBO> page){
        achievementService.getAchievementPersonalPage(page);
        return ResultVO.success("个人业绩排行",page);
    }

    @PostMapping("/achieve/team/page")
    @ResponseBody
    public ResultVO teamPage(@RequestBody() PageBO<AchievementBO> page){
        achievementService.getAchievementTeamPage(page);
        return ResultVO.success("团队业绩排行",page);
    }


    @PostMapping("/achieve/shop/page")
    @ResponseBody
    public ResultVO shopPage(@RequestBody() PageBO<AchievementBO> page){
        achievementService.getAchievementShopPage(page);
        return ResultVO.success("门店业绩排行",page);
    }


}
