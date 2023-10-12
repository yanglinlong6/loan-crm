package com.crm.service.customer.model;

import com.crm.common.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 客户点评对象
 */
@Setter
@Getter
public class CustomerCommentPO extends BasePO {

    private Long customerId;

    private Long employeeId;

    private String content;

    @Override
    public String toString() {
        return "CustomerCommentPO{}";
    }
}