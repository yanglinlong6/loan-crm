package com.daofen.crm.service.counselor;

import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.counselor.dao.PermissionMapper;
import com.daofen.crm.service.counselor.model.PermissionBO;
import com.daofen.crm.service.counselor.model.PermissionPO;
import com.daofen.crm.service.counselor.model.RolePO;
import com.daofen.crm.service.counselor.model.RolePermissionPO;
import com.daofen.crm.utils.CollectionUtil;
import com.daofen.crm.utils.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 权限模块service接口
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<PermissionPO> getRolePermissionList(Long roleId) {
        return permissionMapper.selectRolePermissionList(roleId);
    }

    public void addPermission(PermissionPO permission){
        permission.setCreateBy(LoginUtil.getLoginUser().getId().toString());
        permission.setCreateDate(new Date());
        permissionMapper.insertPermission(permission);
    }

    public void savePermission(PermissionPO permission){
        permission.setUpdateBy(LoginUtil.getLoginUser().getId().toString());
        permission.setUpdateDate(new Date());
        permissionMapper.updatePermission(permission);
    }
    @Override
    public void delPermission(Long id) {
        permissionMapper.delPermission(id);
    }

    @Override
    public void addRolePermission(RolePO role, List<Long> permissionIdList) {
        if(null == permissionIdList || permissionIdList.size() <= 0){
            return;
        }
        for(Long permissionId : permissionIdList){
            RolePermissionPO rolePermission = new RolePermissionPO();
            rolePermission.setPermissionId(permissionId);
            rolePermission.setRoleId(role.getId());
            rolePermission.setCreateBy(LoginUtil.getLoginUser().getId().toString());
            rolePermission.setCreateDate(new Date());
            permissionMapper.insertRolePermission(rolePermission);
        }
    }

    @Override
    public void addRolePermission2(RolePO role, List<PermissionPO> permissionIdList) {
        if(CollectionUtil.isEmpty(permissionIdList))
            return;
        for(PermissionPO permission : permissionIdList){
            RolePermissionPO rolePermission = new RolePermissionPO();
            rolePermission.setPermissionId(permission.getId());
            rolePermission.setRoleId(role.getId());
            rolePermission.setCreateBy(LoginUtil.getLoginUser().getId().toString());
            rolePermission.setCreateDate(new Date());
            permissionMapper.insertRolePermission(rolePermission);
        }
    }

    @Override
    public void delRolePermission(Long roleId) {
        permissionMapper.delRolePermission(roleId);
    }

    @Override
    public void initRolePermission(Long roleId, byte roleType) {
        permissionMapper.initRolePermission(roleId,roleType,LoginUtil.getLoginUser().getId().toString());
    }

    @Override
    public PermissionBO getAllPermission() {
        List<PermissionBO> permissions = permissionMapper.selectAllPermission();;
        return toRoot(JSONUtil.toJavaBeanList(JSONUtil.toString(permissions),PermissionBO.class));
    }
}
