package com.crm.service.customer.model;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CustomerImportBO extends CustomerImportPO{

    /**
     * 签约合同业务对象BO
     */
    private String customerName;

    private String customerMobile;

    private BigDecimal deposit;//诚意金 改成 合同金额

    private String contractCode; // 合约编号

    private String startDate; // 查询开始日期

    private String endDate;  //  查询结束日期

    @Override
    public String toString() {
        if(null == this)
            return "CustomerImportBO{}";
        return JSONUtil.toJSONString(this);
    }
}
