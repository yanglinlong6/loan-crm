package com.crm.service.role.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class RolePO extends BasePO {

    private Long orgId;// 所属的机构id

    private String name; // 角色名称（岗位）

    private Byte type; // 角色类型：0-总经理(管理员)，1-门店管理者，2-团队管理者，3-普通

    @Override
    public String toString() {
        if(null == this)
            return "RolePO{}";
        return JSONUtil.toJSONString(this);
    }
}