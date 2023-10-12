package com.daofen.crm.service.counselor.dao;


import com.daofen.crm.service.counselor.model.PermissionBO;
import com.daofen.crm.service.counselor.model.PermissionPO;
import com.daofen.crm.service.counselor.model.RolePermissionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface PermissionMapper {

    /**
     * 查询角色的权限列表
     * @param roleId 角色id
     * @return
     */
    List<PermissionPO> selectRolePermissionList(Long roleId);

    List<PermissionBO> selectAllPermission();

    /**
     * 插入权限信息
     * @param permission PermissionPO
     * @return
     */
    int insertPermission(PermissionPO permission);

    /**
     * 更新修改权限信息
     * @param permission PermissionPO
     * @return
     */
    int updatePermission(PermissionPO permission);

    int delPermission(Long id);
    /**
     * 新增角色与权限的关联关系
     * @param rolePermission RolePermissionPO
     */
    void insertRolePermission(RolePermissionPO rolePermission);

    void initRolePermission(@Param("roleId") Long roleId, @Param("roleType")Byte roleType, @Param("createBy")String createBy);

    /**
     * 删除角色与权限的关联关系
     * @param roleId 角色id
     */
    void delRolePermission(Long roleId);
}