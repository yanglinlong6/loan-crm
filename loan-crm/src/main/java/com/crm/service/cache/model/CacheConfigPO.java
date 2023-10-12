package com.crm.service.cache.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CacheConfigPO extends BasePO {

    private String field;

    private String key;

    private String value;

    private String explain;

    @Override
    public String toString() {
        if(null == this)
            return "CacheConfigPO{null}";
        return JSONUtil.toJSONString(this);
    }
}