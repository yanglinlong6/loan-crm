package com.crm.service.customer;

import com.crm.common.PageBO;
import com.crm.service.customer.model.CustomerContractBO;
import com.crm.service.customer.model.CustomerContractPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContractService {

    /**
     * 签约客户分页列表
     * @param page PageBO<CustomerContractBO>
     */
    void getPage(PageBO<CustomerContractBO> page);

    /**
     * 创建客户签约信息
     * @param po CustomerContractPO
     */
    void addCustomerContract(CustomerContractPO po);

    /**
     * 更新客户签约信息
     * @param po CustomerContractPO
     */
    void updateCustomerContract(CustomerContractPO po);

    /**
     * 获取员工管理的签约客户列表
     * @param bo CustomerContractBO
     * @return List<CustomerContractBO>
     */
    List<CustomerContractBO> getEmployeeContract(CustomerContractBO bo);

    /**
     * 过去客户签约列表
     * @param customerId 客户id
     * @param state 签约状态,多个状态已,号隔开
     * @return List<CustomerContractBO>
     */
    List<CustomerContractBO> getCustomerContract(Long customerId,String state);

    CustomerContractBO getCustomerContractByFlowId(@Param("flowId") String flowId);

}
