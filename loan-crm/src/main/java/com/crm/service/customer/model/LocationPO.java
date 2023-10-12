package com.crm.service.customer.model;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LocationPO {

    private Integer page;

    private Float x;

    private Float y;


    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}
