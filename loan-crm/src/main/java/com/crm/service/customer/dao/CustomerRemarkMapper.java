package com.crm.service.customer.dao;


import com.crm.service.customer.model.CustomerRemarkPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CustomerRemarkMapper {

    /**
     * 查询客户所有的跟进记录
     * @param customerId 客户id
     * @return List<CustomerRemarkPO>
     */
    List<CustomerRemarkPO> selectCustomerRemarkAll(@Param("customerId") Long customerId);

    int insertCustomerRemark(CustomerRemarkPO po);

}