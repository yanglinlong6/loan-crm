package com.help.loan.distribute.service.api.model;


import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.BasePO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DispatcheRecPO extends BasePO {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long orgId;

    private Long customerId;

    private Integer dispatchStatus;

    private String dispatchResult;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
