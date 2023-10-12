package com.crm.service.role;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.controller.login.LoginUtil;
import com.crm.service.role.dao.RolePOMapper;
import com.crm.service.role.model.RoleBO;
import com.crm.service.role.model.RolePO;
import com.crm.util.JSONUtil;
import com.crm.util.JudgeUtil;
import com.crm.util.ListUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RolePOMapper rolePOMapper;

    @Override
    public RoleBO getRoleBO(Long roleId) {
        if(null == roleId || roleId <0){
            throw new CrmException(CrmConstant.ResultCode.EX,"岗位ID不能空");
        }
        RoleBO role;
        RolePO rolePO = rolePOMapper.selectRoleById(roleId);
        if(roleId == 0 || (null != rolePO && JudgeUtil.in(rolePO.getType(),CrmConstant.Role.Type.ADMIN))){ // 管理员，拥有所有权限
            role = new RoleBO();
            role.setName("总经理");
            role.setType(CrmConstant.Role.Type.ADMIN);
            role.setPermissions(toTree(role,rolePOMapper.selectRolePermissions()));
            return role;
        }
        if(null == rolePO){
            throw new CrmException(CrmConstant.ResultCode.EX,"非法岗位");
        }
        role = JSONUtil.toJavaBean(rolePO.toString(), RoleBO.class);
        role.setPermissions(toTree(role,rolePOMapper.selectRolePermissionsByRoleId(roleId)));
        return role;
    }

    @Override
    public List<RoleBO> getAll(Long orgId) {
        List<RolePO> roles = rolePOMapper.selectRoles(orgId);
        if(ListUtil.isEmpty(roles)){
            return null;
        }
        List<RoleBO> list = new ArrayList<>();
        for(RolePO role : roles){
            list.add(getRoleBO(role.getId()));
        }
        return list;
    }

    @Transactional
    @Override
    public void addRole(RoleBO roleBO) {
        if(null == roleBO || StringUtils.isBlank(roleBO.getName()) || ListUtil.isEmpty(roleBO.getPermissionIdList())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"未填写岗位信息,或者未填写岗位名称,或者未选择权限");
        }
        RolePO selecRolePO = new RoleBO();
        selecRolePO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        selecRolePO.setName(roleBO.getName());
        RolePO old = rolePOMapper.selectRole(selecRolePO);
        if(null != old){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"岗位名称已存在!");
        }
        roleBO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        roleBO.setCreateBy(LoginUtil.getLoginEmployee().getName());
        roleBO.setUpdateBy(LoginUtil.getLoginEmployee().getName());
        rolePOMapper.insertRole(roleBO);
        if(roleBO.getType() ==0 ){//如果是管理员  默认有所有权限 无需新增岗位与权限关系
            return;
        }
        //新增岗位和权限对应关系
        for(Long permissonId:roleBO.getPermissionIdList()){
            rolePOMapper.inserRolePermisson(roleBO.getId(),permissonId);
        }
    }

    @Transactional
    @Override
    public void updateRole(RoleBO roleBO) {
        if(null == roleBO || null == roleBO.getId() || StringUtils.isBlank(roleBO.getName())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"未填写岗位信息,或者未填写岗位名称");
        }
        RolePO selecRolePO = new RoleBO();
        selecRolePO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        selecRolePO.setName(roleBO.getName());
        RolePO old = rolePOMapper.selectRole(selecRolePO);
        if(null != old && roleBO.getId() != old.getId()){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"岗位名称已存在!");
        }
        roleBO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        roleBO.setUpdateBy(LoginUtil.getLoginEmployee().getName());
        rolePOMapper.updateRole(roleBO);
        if(ListUtil.isEmpty(roleBO.getPermissionIdList()) || roleBO.getType() == 0){
            return;
        }
        rolePOMapper.delRolePermission(roleBO.getId());
        for(Long permissonId:roleBO.getPermissionIdList()){
            rolePOMapper.inserRolePermisson(roleBO.getId(),permissonId);
        }
    }


}
