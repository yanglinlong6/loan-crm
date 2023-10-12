package com.crm.service.sms.dao;


import com.crm.service.sms.model.MsgRecPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface MsgRecMapper {

    int insertMsgRec(MsgRecPO record);

}