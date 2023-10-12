package com.crm.service.role.model;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RoleBO extends RolePO{

    private List<Long> permissionIdList;

    private List<PermissionBO> permissions;


    @Override
    public String toString() {
        if(null == this){
            return "RoleBO{}";
        }
        return JSONUtil.toJSONString(this);
    }
}
