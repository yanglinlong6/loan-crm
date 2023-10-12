package com.crm.service.statistic;

import com.crm.common.CrmConstant;
import com.crm.common.PageBO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.statistic.dao.StatisticMapper;
import com.crm.service.statistic.model.CustomerStatisticBO;
import com.crm.util.DateUtil;
import com.crm.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class StatisticServiceImpl implements StatisticService{

    @Autowired
    StatisticMapper statisticMapper;

    @Override
    public void getPersonalStatisticPage(PageBO<CustomerStatisticBO> page) {
        CustomerStatisticBO bo = page.getParamObject();
        if(null == bo)
            bo = new CustomerStatisticBO();
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        bo.setOrgId(employee.getOrgId());
        bo.setShopId(employee.getShopId());
        bo.setTeamId(employee.getTeamId());
        bo.setStartDate(DateUtil.cumputeStartDate(page.getParamObject().getStartDate()));
        bo.setEndDate(DateUtil.computeEndDate(page.getParamObject().getEndDate()));
        page.setParamObject(bo);
        List<CustomerStatisticBO> list = statisticMapper.selectPersonalStatisticPage(page);
        if(ListUtil.isEmpty(list))
            return;
        for(CustomerStatisticBO statistic : list){
            statistic.setOrgId(employee.getOrgId());
            statistic.setEmployeeId(statistic.getId());
            statistic.setStartDate(page.getParamObject().getStartDate());
            statistic.setEndDate(page.getParamObject().getEndDate());
            statistic.setType(bo.getType());
            List<Map<String,Object>> leveCountList = statisticMapper.selectLevelCount(statistic);
            statistic.setTotalCount(getTotalCount(leveCountList));
            setLevelCount(statistic,leveCountList);
            setCallCount(statistic,statisticMapper.selectCallCount(statistic));
            setFitCount(statistic,statisticMapper.selectFitCount(statistic));
        }
        page.setDataList(list);
        page.setTotalCount(statisticMapper.selectPersonalStatisticPageCount(page));
    }



    @Override
    public void getTeamStatisticPage(PageBO<CustomerStatisticBO> page) {
        setParam(page);
        // 团队列表
        List<CustomerStatisticBO> list = statisticMapper.selectTeamStatisticPage(page);
        if(ListUtil.isEmpty(list))
            return;
        // 循环统计各个团队数据
        for(CustomerStatisticBO statistic : list){
            statistic.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
            statistic.setTeamId(statistic.getId());
            statistic.setStartDate(page.getParamObject().getStartDate());
            statistic.setEndDate(page.getParamObject().getEndDate());

            List<Map<String,Object>> leveCountList = statisticMapper.selectLevelCount(statistic);
            statistic.setTotalCount(getTotalCount(leveCountList));
            setLevelCount(statistic,leveCountList);
            setCallCount(statistic,statisticMapper.selectCallCount(statistic));
            setFitCount(statistic,statisticMapper.selectFitCount(statistic));
        }
        page.setDataList(list);
        page.setTotalCount(statisticMapper.selectTeamStatisticPageCount(page));
    }

    @Override
    public void getShopStatisticPage(PageBO<CustomerStatisticBO> page) {
        setParam(page);
        // 门店列表
        List<CustomerStatisticBO> list = statisticMapper.selectShopStatisticPage(page);
        if(ListUtil.isEmpty(list))
            return;
        // 统计门店数据
        for(CustomerStatisticBO statistic : list){
            statistic.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
            statistic.setShopId(statistic.getId());
            statistic.setStartDate(page.getParamObject().getStartDate());
            statistic.setEndDate(page.getParamObject().getEndDate());

            List<Map<String,Object>> leveCountList = statisticMapper.selectLevelCount(statistic);
            statistic.setTotalCount(getTotalCount(leveCountList));
            setLevelCount(statistic,leveCountList);
            setCallCount(statistic,statisticMapper.selectCallCount(statistic));
            setFitCount(statistic,statisticMapper.selectFitCount(statistic));
        }
        page.setDataList(list);
        page.setTotalCount(statisticMapper.selectShopStatisticPageCount(page));
    }


}
