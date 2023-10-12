package com.crm.service.behind.dao;


import com.crm.common.PageBO;
import com.crm.service.behind.model.CustomerBehindBO;
import com.crm.service.behind.model.CustomerBehindPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 后端客户dao
 */
@Mapper
@Component
public interface CustomerBehindPOMapper {


    /**
     * 后端客户分页
     * @param page PageBO<CustomerBehindPO>
     * @return List<CustomerBehindPO>
     */
    List<CustomerBehindBO> getBehindPage(PageBO<CustomerBehindBO> page);

    /**
     * 后端客户总数量
     * @param page List<CustomerBehindPO>
     * @return
     */
    Integer getBehindPageCount(PageBO<CustomerBehindBO> page);

    int insertCustomerBehindPO(CustomerBehindPO customerBehind);

    CustomerBehindPO selectById(@Param("customerId") Long id);

    CustomerBehindPO selectByCustomerId(@Param("customerId") Long customerId);

    int updateCustomerBehind(CustomerBehindPO customerBehind);

}