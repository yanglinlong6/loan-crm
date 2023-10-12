package com.crm.service.customer.dao;


import com.crm.service.customer.model.CustomerCommentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface CustomerCommentMapper {

    CustomerCommentPO selectCustomerCommentByCustomerId(@Param("customerId") Long customerId);

    int insertCustomerComment(CustomerCommentPO po);

}