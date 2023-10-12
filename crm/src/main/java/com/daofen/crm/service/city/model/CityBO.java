package com.daofen.crm.service.city.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 城市业务对象
 */
@Setter
@Getter
public class CityBO extends CityPO {

    /**
     * 子城市列表
     */
    private List<CityBO> childList = new ArrayList<>();

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
