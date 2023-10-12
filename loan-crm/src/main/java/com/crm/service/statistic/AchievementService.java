package com.crm.service.statistic;

import com.crm.common.CrmConstant;
import com.crm.common.PageBO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.statistic.model.AchievementBO;
import com.crm.util.DateUtil;
import com.crm.util.JudgeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public interface AchievementService {

    void getAchievementPersonalPage(PageBO<AchievementBO> page);

    void getAchievementTeamPage(PageBO<AchievementBO> page);

    void getAchievementShopPage(PageBO<AchievementBO> page);






    default void setParam(PageBO<AchievementBO> page){
        if(null == page)
            return;
        AchievementBO achievement = page.getParamObject();
        if(null == achievement)
            achievement = new AchievementBO();

        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        achievement.setOrgId(employee.getOrgId());

        if(CrmConstant.Role.Type.ADMIN == employee.getRole().getType() && null != achievement.getShopId()){
            achievement.setShopId(achievement.getShopId());
        }else
            achievement.setShopId(employee.getShopId());

        if(JudgeUtil.in(employee.getRole().getType(),CrmConstant.Role.Type.ADMIN,CrmConstant.Role.Type.SHOP) && null != achievement.getTeamId()){
            achievement.setTeamId(achievement.getTeamId());
        }else
            achievement.setTeamId(employee.getTeamId());

        String start = page.getParamObject().getStartDate();
        String end = page.getParamObject().getEndDate();
        String concurrentDate = DateUtil.getCurrentDate(DateUtil.yyyymmdd);
        if(StringUtils.isBlank(start))
            start = DateUtil.computeMonthDay(new Date(),1,DateUtil.yyyymmdd)+CrmConstant.Date.START;
        else start = DateUtil.cumputeStartDate(start);
        if(StringUtils.isBlank(end))
            end = DateUtil.computeEndDate(concurrentDate);
        else end = DateUtil.computeEndDate(end);
        achievement.setStartDate(start);
        achievement.setEndDate(end);
        page.setParamObject(achievement);
    }

}
