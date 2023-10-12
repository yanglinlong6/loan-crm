package com.crm.service.behind.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class CustomerBehindRemarkPO extends BasePO {

    private Long customerId;

    private String remark;

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}