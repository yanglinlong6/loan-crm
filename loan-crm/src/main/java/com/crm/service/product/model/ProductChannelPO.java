package com.crm.service.product.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class ProductChannelPO extends BasePO {

    private Long orgId;

    private String name;

    private String companyName;

    private String startDate;

    private String endDate;

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}