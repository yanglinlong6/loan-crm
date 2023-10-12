package com.crm.service.role;

import com.crm.service.role.model.PermissionBO;
import com.crm.service.role.model.PermissionPO;
import com.crm.service.role.model.RoleBO;
import com.crm.service.role.model.RolePO;
import com.crm.util.JSONUtil;
import com.crm.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

public interface RoleService {

    /**
     * 根据role获取岗位角色对象(主要用户登陆,获取岗位下有的权限id)
     * @param roleId 岗位爵角色id
     * @return RoleBO
     */
    RoleBO getRoleBO(Long roleId);

    default List<PermissionBO> toTree(RoleBO role, List<PermissionPO> permissions){
        if(null == role || ListUtil.isEmpty(permissions)){
            return null;
        }
        List<PermissionBO> rootList = new ArrayList<>();
        for(PermissionPO permission : permissions){
            if(0 != permission.getParentId())
                continue;
            PermissionBO bo = JSONUtil.toJavaBean(permission.toString(),PermissionBO.class);
            bo.setChildList(getChild(permission,permissions));
            rootList.add(bo);
        }
        return rootList;
    }
    default List<PermissionBO> getChild(PermissionPO permission,List<PermissionPO> permissions){
        List<PermissionBO> childList = new ArrayList<>();
        for(PermissionPO po : permissions){
            if(po.getParentId() != permission.getId()){
                continue;
            }
            childList.add(JSONUtil.toJavaBean(po.toString(),PermissionBO.class));
        }
        return childList;
    }

    /**
     * 获取机构下所有岗位
     * @param orgId 机构id
     * @return List<RolePO>
     */
    List<RoleBO> getAll(Long orgId);

    void addRole(RoleBO roleBO);

    void updateRole(RoleBO roleBO);
}
