package com.crm.service.statistic.dao;

import com.crm.common.PageBO;
import com.crm.service.statistic.model.CustomerStatisticBO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface StatisticMapper {


    /**
     * 个人统计
     * @param page PageBO<CustomerStatisticBO>
     * @return List<CustomerStatisticBO>
     */
    List<CustomerStatisticBO> selectPersonalStatisticPage(PageBO<CustomerStatisticBO> page);

    int selectPersonalStatisticPageCount(PageBO<CustomerStatisticBO> page);

    List<Map<String,Object>> selectLevelCount(CustomerStatisticBO bo);

    List<Map<String,Object>> selectCallCount(CustomerStatisticBO bo);

    List<Map<String,Object>> selectFitCount(CustomerStatisticBO bo);

    /**
     * 门店统计
     * @param page PageBO<CustomerStatisticBO>
     * @return List<CustomerStatisticBO>
     */
    List<CustomerStatisticBO> selectShopStatisticPage(PageBO<CustomerStatisticBO> page);

    Integer selectShopStatisticPageCount(PageBO<CustomerStatisticBO> page);

    /**
     * 团队统计
     * @param page PageBO<CustomerStatisticBO>
     * @return List<CustomerStatisticBO>
     */
    List<CustomerStatisticBO> selectTeamStatisticPage(PageBO<CustomerStatisticBO> page);


    Integer selectTeamStatisticPageCount(PageBO<CustomerStatisticBO> page);

}
