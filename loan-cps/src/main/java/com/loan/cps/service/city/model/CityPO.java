package com.loan.cps.service.city.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

/**
 * 省份城市对象
 */
@Setter
@Getter
public class CityPO {

    private String id;
    private Byte level;
    private String name;
    private Integer ordered;
    private String parentId;



    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}