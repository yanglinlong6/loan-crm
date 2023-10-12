package com.crm.service.customer.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomerRemarkPO extends BasePO {

    private Long customerId;

    private Long employeeId;

    private String remark;

    public CustomerRemarkPO() {
    }

    public CustomerRemarkPO(Long customerId, Long employeeId, String remark) {
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.remark = remark;
    }

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}