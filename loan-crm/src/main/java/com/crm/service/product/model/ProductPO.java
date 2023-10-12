package com.crm.service.product.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class ProductPO extends BasePO {


    private Long orgId;

    private String name;

    private Long channelId;

    private String condition;



    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}