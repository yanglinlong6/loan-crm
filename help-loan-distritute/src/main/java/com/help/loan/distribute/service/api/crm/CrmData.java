package com.help.loan.distribute.service.api.crm;

import com.help.loan.distribute.common.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CrmData {

    private String appid;

    private String secret;

    private String nonc;

    private long timestamp;

    private int encryption = 0;

    private String sign; // 数字签名

    private String params;

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}
