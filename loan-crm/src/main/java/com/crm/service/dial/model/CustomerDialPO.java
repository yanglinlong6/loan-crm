package com.crm.service.dial.model;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class CustomerDialPO {
    private Long id;

    private Long orgId;

    private Long employeeId;

    private String employeePhone;

    private Long customerId;

    private String customerPhone;

    private byte status;

    private String createBy;

    private Date createDate;

    private String updateBy;

    private Date updateDate;

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}