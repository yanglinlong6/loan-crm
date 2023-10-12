package com.crm.service.behind.model;

import com.crm.common.BasePO;
import com.crm.service.customer.model.ContractProductPO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class CustomerBehindPO extends BasePO {


    private Long contractId;

    private Long orgId;

    private Long shopId;

    private Long teamId;

    private Long employeeId;

    private Long firstEmployee;

    private Long customerId;

    private Byte process;

    private Date disDate;

    private String info;

    private Date remarkDate;

    private List<CustomerBehindRemarkPO> remarkList;

    private List<ContractProductPO> productList;

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}