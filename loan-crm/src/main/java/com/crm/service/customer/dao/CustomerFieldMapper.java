package com.crm.service.customer.dao;


import com.crm.service.customer.model.CustomerFieldPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CustomerFieldMapper {

    List<CustomerFieldPO> selectAllField(@Param("orgId") Long orgId);

}