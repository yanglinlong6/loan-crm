package com.help.loan.distribute.service.org.model;

import com.help.loan.distribute.common.BasePO;
import com.help.loan.distribute.common.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrgDispatcheRecPO extends BasePO {

    private Long orgId;

    private Long customerId;

    private Boolean dispatchStatus;

    private String dispatchResult;


    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}