package com.crm.service.label.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 标签对象
 */
@Setter
@Getter
public class LabelPO extends BasePO {

    private Long orgId;

    private String name;

    private Byte status;

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}