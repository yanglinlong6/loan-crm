package com.help.loan.distribute.service.org.model;

import com.help.loan.distribute.common.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrgAcquireCustomerStatisticsBO {

    private String acquireDate;

    private String acquireCity;

    private String acquireNum;

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}
