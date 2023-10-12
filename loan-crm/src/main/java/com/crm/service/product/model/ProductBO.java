package com.crm.service.product.model;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductBO extends ProductPO{

    private String channelName;

    private String channelCompany;

    private String startDate;

    private String endDate;

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }

}
