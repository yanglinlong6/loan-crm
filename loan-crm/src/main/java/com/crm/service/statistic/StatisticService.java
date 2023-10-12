package com.crm.service.statistic;

import com.crm.common.CrmConstant;
import com.crm.common.PageBO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.statistic.model.CustomerStatisticBO;
import com.crm.util.DateUtil;
import com.crm.util.ListUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StatisticService {

    String LEVEL = "level";

    String COUNTS = "counts";

    /**
     * 个人数据统计
     * @param page PageBO<CustomerStatisticBO>
     */
    void getPersonalStatisticPage(PageBO<CustomerStatisticBO> page);

    /**
     * 团队数据统计
     * @param page PageBO<CustomerStatisticBO>
     */
    void getTeamStatisticPage(PageBO<CustomerStatisticBO> page);

    /**
     * 门店数据统计
     * @param page
     */
    void getShopStatisticPage(PageBO<CustomerStatisticBO> page);



    default void setParam(PageBO<CustomerStatisticBO> page){
        CustomerStatisticBO bo = page.getParamObject();
        if(null == bo)
            bo = new CustomerStatisticBO();
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        bo.setOrgId(employee.getOrgId());
        if(null == bo.getShopId() || bo.getShopId() <= 0)
            bo.setShopId(employee.getShopId());
        bo.setStartDate(DateUtil.cumputeStartDate(page.getParamObject().getStartDate()));
        bo.setEndDate(DateUtil.computeEndDate(page.getParamObject().getEndDate()));
        page.setParamObject(bo);
    }

    /**
     * 设置星级数量和比率
     * @param statistic CustomerStatisticBO
     * @param list  Map<String,Integer>
     */
    default void setLevelCount(CustomerStatisticBO statistic, List<Map<String,Object>> list ){
        if(ListUtil.isEmpty(list))
            return;
        for(Map<String,Object> map: list){
            String level = map.get(LEVEL).toString();
            if(level.equals(CrmConstant.Customer.Level.ZERO)){
                statistic.setLevel0Count(Integer.valueOf(map.getOrDefault(COUNTS,0l).toString()));
                continue;
            }
            if(level.equals(CrmConstant.Customer.Level.ONE)){
                statistic.setLevel1Count(Integer.valueOf(map.getOrDefault(COUNTS,0l).toString()));
                continue;
            }
            if(level.equals(CrmConstant.Customer.Level.TWO)){
                statistic.setLevel2Count(Integer.valueOf(map.getOrDefault(COUNTS,0l).toString()));
                continue;
            }
            if(level.equals(CrmConstant.Customer.Level.TWO_FIVE)){
                statistic.setLevel25Count(Integer.valueOf(map.getOrDefault(COUNTS,0l).toString()));
                continue;
            }
            if(level.equals(CrmConstant.Customer.Level.THREE)){
                statistic.setLevel3Count(Integer.valueOf(map.getOrDefault(COUNTS,0l).toString()));
                continue;
            }
            if(level.equals(CrmConstant.Customer.Level.FOUR)){
                statistic.setLevel4Count(Integer.valueOf(map.getOrDefault(COUNTS,0l).toString()));
                continue;
            }
            if(level.equals(CrmConstant.Customer.Level.FIVE)){
                statistic.setLevel5Count(Integer.valueOf(map.getOrDefault(COUNTS,0l).toString()));
                continue;
            }
        }






    }

    /**
     * 设置接通数量和比率
     * @param statistic CustomerStatisticBO
     * @param list List<Map<Byte,Integer>>
     */
    default void setCallCount(CustomerStatisticBO statistic,List<Map<String,Object>> list ){
        if(ListUtil.isEmpty(list))
            statistic.setCallCount(0);
        for(Map<String,Object> map : list){
            Byte call = Byte.valueOf(map.getOrDefault("call",CrmConstant.Customer.init).toString());
            if(call == CrmConstant.Customer.Call.CALL){
                statistic.setCallCount(Integer.valueOf(map.getOrDefault(COUNTS,0L).toString()));
            }
        }

    }

    /**
     * 设置有效数量和比率
     * @param statistic CustomerStatisticBO
     * @param list Map<Integer,Integer>
     */
    default void setFitCount(CustomerStatisticBO statistic,List<Map<String,Object>> list){
        if(ListUtil.isEmpty(list))
            statistic.setFixCount(0);
        for(Map<String,Object> map: list){
            Byte fit = Byte.valueOf(map.getOrDefault("fit",CrmConstant.Customer.init).toString());
            if(fit == CrmConstant.Customer.Fit.FIT){
                statistic.setFixCount(Integer.valueOf(map.getOrDefault(CrmConstant.Customer.Fit.FIT,0L).toString()));
            }
        }

    }

    /**
     * 设置总数量
     * @param list List<Map<String,Integer>> Map<String,Integer>
     * @return Integer
     */
    default Integer getTotalCount(List<Map<String,Object>> list){
        Long totalCount = 0L;
        for(Map<String,Object> map : list){
            Long counts = Long.valueOf(map.get(COUNTS).toString());
            totalCount += counts;
        }
        return totalCount.intValue();
    }
}
