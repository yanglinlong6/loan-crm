package com.daofen.crm.service.sms.dao;


import com.daofen.crm.service.sms.model.SmsSendRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface SmsSendRecordMapper {

    int insertSmsSendRecord(SmsSendRecordPO record);
}