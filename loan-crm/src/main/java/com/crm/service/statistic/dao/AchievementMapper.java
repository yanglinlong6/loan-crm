package com.crm.service.statistic.dao;

import com.crm.common.PageBO;
import com.crm.service.statistic.model.AchievementBO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface AchievementMapper {


    List<AchievementBO> selectPersonalPage(PageBO<AchievementBO> page);

    Integer selectPersonalPageCount(PageBO<AchievementBO> page);


    List<AchievementBO> selectTeamPage(PageBO<AchievementBO> page);

    Integer selectTeamPageCount(PageBO<AchievementBO> page);


    List<AchievementBO> selectShopPage(PageBO<AchievementBO> page);

    Integer selectShopPageCount(PageBO<AchievementBO> page);


    Integer selectNewCustomerCount(AchievementBO bo);

    /**
     * 签约数量统计
     * @param  bo AchievementBO
     * @return Integer
     */
    Integer selectContractCount(AchievementBO bo);

    Double selectContractCostRateAvg(AchievementBO bo);

    /**
     * 合同金额
     * @param bo AchievementBO
     * @return Double
     */
    Double selectContractDepositAmount(AchievementBO bo);

    /**
     * 进件数量统计
     * @param bo AchievementBO
     * @return List<Map<String,Object>>
     */
    Integer selectImportCount(AchievementBO bo);

    /**
     * 进件产品金额汇总统计
     * @param bo AchievementBO
     * @return Double
     */
    Double selectImportCompletionAmount(AchievementBO bo);

    /**
     * 进件创收统计
     * @param bo AchievementBO
     * @return Double
     * @auth zhangqiuping
     */
    Double selectImportIncomeAmount(AchievementBO bo);

    /**
     * 计算查询日期内的消费金额
     * 区分：门店，团队，员工
     * @param bo AchievementBO
     * @return Double
     */
    Double selectConsumeAmount(AchievementBO bo);


    /**
     * 统计客户接通数
     * 区分：门店，团队，员工
     * @param bo AchievementBO
     * @return Integer
     */
    Integer selectCallCustomerCount(AchievementBO bo);

    /**
     * 统计有效客户数
     * 区分：门店，团队，员工
     * @param bo
     * @return Integer
     */
    Integer selectFitCustomerCount(AchievementBO bo);
}
