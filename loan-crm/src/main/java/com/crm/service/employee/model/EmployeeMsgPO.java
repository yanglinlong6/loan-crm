package com.crm.service.employee.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmployeeMsgPO extends BasePO {

    private Long employeeId;

    private String employeePhone;

    private String msg;

    private byte status;

    public EmployeeMsgPO() {
    }

    public EmployeeMsgPO(Long employeeId, String employeePhone, String msg, byte status) {
        this.employeeId = employeeId;
        this.employeePhone = employeePhone;
        this.msg = msg;
        this.status = status;
    }

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}
