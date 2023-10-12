package com.crm.service.role.model;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PermissionBO extends PermissionPO{

    private List<PermissionBO> childList;

    @Override
    public String toString() {
        if(null == this){
            return "PermissionBO{}";
        }
        return JSONUtil.toJSONString(this);
    }
}
