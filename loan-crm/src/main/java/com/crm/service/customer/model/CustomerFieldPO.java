package com.crm.service.customer.model;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomerFieldPO {

    private Long id;

    private String fieldCode;

    private String fieldName;

    private String value;

    private Byte type=1; // 区分是输入框或者下拉框:1-输入框,2-下拉框

    @Override
    public String toString() {
        if(null == this)
            return "CustomerFieldPO{null}";
        return JSONUtil.toJSONString(this);
    }
}