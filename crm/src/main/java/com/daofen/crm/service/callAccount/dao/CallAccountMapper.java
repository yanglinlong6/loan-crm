package com.daofen.crm.service.callAccount.dao;

import com.daofen.crm.service.callAccount.model.CallAccountPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface CallAccountMapper {


    CallAccountPO selectById(@Param("id") Long id);

}
