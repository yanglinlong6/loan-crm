package com.daofen.crm.service.counselor.model;

import com.daofen.crm.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class PermissionBO extends PermissionPO {

    private List<PermissionBO> childList; // 子权限列表

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}