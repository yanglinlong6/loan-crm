package com.crm.service.dial.dao;


import com.crm.service.dial.model.CustomerDialPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface CustomerDialPOMapper {

    int insert(CustomerDialPO record);

    int updateDialStatus(@Param("id") Long id, @Param("status")Byte status);
}