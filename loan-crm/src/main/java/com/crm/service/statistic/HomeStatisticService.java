package com.crm.service.statistic;

import com.crm.service.statistic.model.CustomerLabelBO;
import com.crm.service.statistic.model.CustomerStateBO;
import com.crm.service.statistic.model.HomeIncomeSortBO;
import com.crm.service.statistic.model.HomeStatisticBO;

import java.util.List;

public interface HomeStatisticService {

    /**
     * 原来首页统计方法
     * @return List<HomeStatisticBO>
     * @auth zhangqiuping
     *
     */
    List<HomeStatisticBO> getHomeStatistic();

    /**
     * 首页: 业绩排行
     * @param bo HomeIncomeSortBO
     * @return List<HomeIncomeSortBO>
     * @auth zhangqiuping
     */
    List<HomeIncomeSortBO> getIncomeSort(HomeIncomeSortBO bo);


    /**
     * 客户状态同济
     * @param bo
     * @return
     */
    List<CustomerStateBO> getProcessStatistic(CustomerStateBO bo);

    /**
     * 客户标签统计
     * @param bo
     * @return
     */
    List<CustomerLabelBO> getLabelStatistic(CustomerLabelBO bo);

}
