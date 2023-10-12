package com.crm.service.customer.dao;


import com.crm.common.PageBO;
import com.crm.service.customer.model.CustomerBO;
import com.crm.service.customer.model.CustomerPO;
import com.crm.service.customer.model.CustomerZiliaoPO;
import com.crm.service.employee.model.OrgEmployeePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CustomerMapper {

    /**
     * 所有进件客户的分页列表
     * @param pageBO PageBO<CustomerBO>
     * @return
     */
    List<CustomerBO> selectImportCustomerPage(PageBO<CustomerBO> pageBO);

    /**
     * 查询所有进件客户的总数量
     * @param pageBO PageBO<CustomerBO>
     * @return  Integer 全部进件客户的总数量
     */
    Integer selectImportCustomerPageCount(PageBO<CustomerBO> pageBO);

    List<CustomerBO> selectCustomerPage(PageBO<CustomerBO> pageBO);

    Integer selectCustomerPageCount(PageBO<CustomerBO> pageBO);

    List<CustomerBO> selectMyAllCustomer(CustomerBO bo);

    CustomerPO selectCustomerById(Long id);

    CustomerPO selectCustomerByMd5(@Param("orgId")Long orgId,@Param("mobileMd5")String mobileMd5);

    int selectChannelNewCustomerCount(@Param("orgId")Long orgId,
                               @Param("channelId")Long channelId,
                               @Param("city")String city,
                               @Param("media")String media,
                               @Param("startDate")String startDate);

    Integer selectEmployeeDistributeCount(@Param("orgId")Long orgId,@Param("employeeId")Long employeeId,@Param("disStatus")Byte disStatus,@Param("startDate")String startDate);

    /**
     * 查询机构新客户里列表 默认一次获取100条
     * @param orgId
     * @return
     */
    List<CustomerPO> selectOrgNewCustomer(Long orgId);

    int insertCustomer(CustomerPO customerPO);

    int updateCustomer(CustomerPO customerPO);

    int deleteCustomer(@Param("id") Long id);

    /**
     * 新增客户分配记录
     * @param customerId 客户id
     * @param employeeId 员工id
     * @return
     */
    int insertCustomerDistributeRec(@Param("customerId") Long customerId, @Param("employeeId")Long employeeId,@Param("status")Byte status,@Param("createBy")String createBy);

    /**
     * 查询客户资料列表
     * @param customerId 客户id  必填
     * @param orgId 机构id  不必填  如果填了加上机构id条件查询
     * @param employeeId 员工id 不必填 如果填了就是加上员工条件查询
     * @return List<CustomerZiliaoPO>
     */
    List<CustomerZiliaoPO> selectCustomerZiliaoList(@Param("customerId")Long customerId,@Param("orgId")Long orgId,@Param("employeeId")Long employeeId);

    /**
     * 插叙客户资料是否已经存在
     * @param customerId 客户id
     * @param ziliao 资料地址
     * @return CustomerZiliaoPO
     */
    CustomerZiliaoPO selectCustomerZiliaoByZiliao(@Param("customerId")Long customerId,@Param("ziliao")String ziliao);

    /**
     * 查询客户资料列表
     * @param customerId 客户id
     * @return List<CustomerZiliaoPO>
     */
    List<CustomerZiliaoPO> selectCustomerZiliaoByCustomerId(@Param("customerId")Long customerId);

    /**
     * 增加客户资料
     * @param po CustomerZiliaoPO
     * @return 增加的个数
     */
    int insertCustomerZiliao(CustomerZiliaoPO po);


    void updateCustomerShopTeamByEmployee(OrgEmployeePO employee);
}