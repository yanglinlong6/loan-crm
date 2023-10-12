package com.crm.service.sms;

import com.crm.service.sms.dao.MsgRecMapper;
import com.crm.service.sms.model.MsgRecPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService{

    private static final Logger LOG = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Autowired
    MsgRecMapper msgRecMapper;

    @Override
    public void addMsgRec(MsgRecPO msgRecPO) {
        if(null == msgRecPO)
            return;
        LOG.info("保存短信记录:{}",msgRecPO.toString());
        msgRecMapper.insertMsgRec(msgRecPO);
    }
}
