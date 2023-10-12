package com.crm.service.sms.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class MsgRecPO extends BasePO {

    private String mobile;

    private String content;

    private String domain;

    private Byte status;


    public MsgRecPO() {
    }

    public MsgRecPO(String mobile, String content, String domain, Byte status,String createBy) {
        this.mobile = mobile;
        this.content = content;
        this.domain = domain;
        this.status = status;
        this.createBy = createBy;
    }

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}