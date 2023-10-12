package com.crm.service.customer.model;

import com.crm.common.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class CustomerImportPO extends BasePO {


    private Long orgId;

    private Long shopId;

    private Long teamId;

    private Long employeeId;

    private Long contractId;

    private Long customerId;

    private String need;

    private Long productId;

    private String productName;

    private Integer counts;

    private String completion;

    private BigDecimal income;

    private Byte progress;

    private Byte state;

    private Date incomeDate;

    private String checkBy;

    private Date checkDate;

    private String certificate; // 证明材料



}