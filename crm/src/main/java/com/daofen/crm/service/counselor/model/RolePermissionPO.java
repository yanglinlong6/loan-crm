package com.daofen.crm.service.counselor.model;

import com.daofen.crm.base.BasePO;
import com.daofen.crm.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RolePermissionPO extends BasePO {


    private Long roleId;

    private Long permissionId;

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}