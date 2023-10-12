package com.daofen.crm.service.callAccount.model;

import com.daofen.crm.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CallAccountPO {

    private Long id;

    private String accountId;

    private String apiSecret;

    private String host;

    private String sign;

    private String  auth;

    private String fromExten;

    private String createDate;

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}
