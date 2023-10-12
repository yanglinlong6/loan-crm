package com.crm.service.statistic.dao;

import com.crm.service.statistic.model.CustomerLabelBO;
import com.crm.service.statistic.model.CustomerStateBO;
import com.crm.service.statistic.model.HomeIncomeSortBO;
import com.crm.service.statistic.model.HomeStatisticBO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 首页数据统计
 */
@Component
@Mapper
public interface HomeStatisticMapper {


    /**
     * 新客户数
     * @param bo HomeStatisticBO
     * @return Integer
     */
    Integer selectNewCustomerCount(HomeStatisticBO bo);

    /**
     * 协助数
     * @param bo HomeStatisticBO
     * @return Integer
     */
    Integer selectHelpCustomerCount(HomeStatisticBO bo);

    /**
     * 再分配数
     * @param bo HomeStatisticBO
     * @return Integer
     */
    Integer selectAgainCustomerCount(HomeStatisticBO bo);

    Integer selectCallCount(HomeStatisticBO bo);

    Integer selectFitCount(HomeStatisticBO bo);

    /**
     * 预约数
     * @param bo HomeStatisticBO
     * @return Integer
     */
    Integer selectAppointmentCustomerCount(HomeStatisticBO bo);

    /**
     * 上门数
     * @param bo HomeStatisticBO
     * @return Integer
     */
    Integer selectUpCustomerCount(HomeStatisticBO bo);

    /**
     * 签约数
     * @param bo
     * @return
     */
    Integer selectContractCount(HomeStatisticBO bo);

    /**
     * 诚意金统计
     * @param bo
     * @return
     */
    Double selectDepositAmount(HomeStatisticBO bo);

    /**
     * 进件统计
     * @param bo
     * @return
     */
    Integer selectImportCount(HomeStatisticBO bo);

    /**
     * 创收统计
     * @param bo
     * @return
     */
    Double selectIncomeAmount(HomeStatisticBO bo);


    /**
     * 查询员工总客户数量
     * @param bo HomeIncomeSortBO
     * @return Integer 第一次分配的新客户数量
     */
    Integer selectEmployeeCustomerCount(HomeIncomeSortBO bo);

    /**
     * 员工新
     * @param bo HomeIncomeSortBO
     * @return Integer 第一次分配的新客户数量签约数量
     */
    Integer selectEmployeeCustomerContractCount(HomeIncomeSortBO bo);

    /**
     * 员工新客户收款金额
     * @param bo HomeIncomeSortBO
     * @return Integer 第一次分配的新客户收款金额
     */
    Double selectEmployeeNewCustomerIncomeAmount(HomeIncomeSortBO bo);


    /**
     * 客户进度: 统计客户进度数量
     * @param bo CustomerStateBO
     * @return
     */
    Integer selectCustomerCount(CustomerStateBO bo);

    /**
     * 客户标签查询
     * @param bo CustomerLabelBO
     * @return Integer
     */
    Integer selectCustomerLabelCount(CustomerLabelBO bo);

}
