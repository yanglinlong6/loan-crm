package com.daofen.crm.service.counselor.model;

import com.daofen.crm.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class RoleBO extends RolePO {

    private List<PermissionPO> permissions;

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}