package com.crm.service.behind.dao;


import com.crm.service.behind.model.CustomerBehindRemarkPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CustomerBehindRemarkMapper {


    int insertCustomerBehindRemark(CustomerBehindRemarkPO record);

    List<CustomerBehindRemarkPO> selectAllCustomerBehindRemark(Long customerId);
}