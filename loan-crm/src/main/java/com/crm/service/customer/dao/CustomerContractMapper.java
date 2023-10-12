package com.crm.service.customer.dao;


import com.crm.common.PageBO;
import com.crm.service.customer.model.CustomerContractBO;
import com.crm.service.customer.model.CustomerContractPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CustomerContractMapper {

    /**
     * 合同签约分页列表
     * @param page PageBO<CustomerContractBO>
     * @return List<CustomerContractBO>
     */
    List<CustomerContractBO> selectPage(PageBO<CustomerContractBO> page);

    /**
     * 合同签约分页列表总数量
     * @param page PageBO<CustomerContractBO
     * @return int 我的签约合同总数量
     */
    int selectPageCount(PageBO<CustomerContractBO> page);


    CustomerContractPO selectCustomerContractById(@Param("id") Long id);



    /**
     * 查询客户签约合同对象
     * @param customerId 客户id
     * @param state 合同状态（多个状态以,号隔开）
     * @return CustomerContractPO
     */
    CustomerContractPO selectCustomerContractByCustomerIdAndState(@Param("customerId") Long customerId,@Param("state") String state);

    CustomerContractPO selectCustomerContractByContractIdAndState(@Param("id") Long id,@Param("state") String state);

    int insertCustomerContract(CustomerContractPO po);

    int updateCustomerContract(CustomerContractPO po);

    /**
     * 获取机构,门店,团队,员工等所有签约列表
     * @param bo CustomerContractBO
     * @return List<CustomerContractBO>
     */
    List<CustomerContractBO> selectContractAll(CustomerContractBO bo);

    /**
     * 查询客户的签约列表
     * @param customerId 客户id
     * @return List<CustomerContractBO>
     */
    List<CustomerContractBO> selectCustomerContractByCustomerId(@Param("customerId") Long customerId);

    CustomerContractBO selectCustomerContractByFlowId(@Param("flowId")String flowId);

}