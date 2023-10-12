package com.daofen.crm.service.sms.model;

import com.daofen.crm.base.BasePO;
import com.daofen.crm.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 手机短信发送记录
 */
@Setter
@Getter
public class SmsSendRecordPO extends BasePO {
    private String mobile;

    private String content;

    public SmsSendRecordPO() {
    }

    public SmsSendRecordPO(String mobile, String content) {
        this.mobile = mobile;
        this.content = content;
    }

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}