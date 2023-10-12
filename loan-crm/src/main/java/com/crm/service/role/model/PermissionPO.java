package com.crm.service.role.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class PermissionPO extends BasePO {

    private String name;

    private String h5Uri; // 前端路径

    private String dataUri;// 后端路径

    private Long parentId; // 上级id

    private Integer sort; //  排序字段，默认升序排


    @Override
    public String toString() {
        if(null == this)
            return "PermissionPO{}";
        return JSONUtil.toJSONString(this);
    }
}