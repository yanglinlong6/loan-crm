package com.crm.service.dial.model;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class CustomerVoicePO {

    private Long id;

    private Long orgId;

    private Long employeeId;

    private String employeePhone;

    private String customerPhone;

    private String fileUrl;

    private String createBy;

    private Date createDate;

    private String employeeName;

    public CustomerVoicePO() {
    }

    public CustomerVoicePO(Long orgId, Long employeeId, String employeePhone, String customerPhone, String fileUrl) {
        this.orgId = orgId;
        this.employeeId = employeeId;
        this.employeePhone = employeePhone;
        this.customerPhone = customerPhone;
        this.fileUrl = fileUrl;
    }

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}