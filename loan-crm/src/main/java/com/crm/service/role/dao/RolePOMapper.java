package com.crm.service.role.dao;


import com.crm.service.role.model.PermissionPO;
import com.crm.service.role.model.RolePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface RolePOMapper {

    int deleteRoleById(Long id);

    int insertRole(RolePO rolePO);

    RolePO selectRoleById(Long id);

    /**
     * 查询机构下的岗位
     * @param rolePO RolePO
     * @return RolePO
     */
    RolePO selectRole(RolePO rolePO);

    int updateRole(RolePO rolePO);


    List<RolePO> selectRoles(Long orgId);

    /**
     * 新增岗位和权限关系对象信息
     * @param roleId 岗位id
     * @param permissionId 权限id
     */
    void inserRolePermisson(@Param("roleId") Long roleId,@Param("permissionId")Long permissionId);

    /**
     * 删除岗位与权限的关系对象信息
     * @param roleId 岗位id
     */
    void delRolePermission(Long roleId);

    /**
     * 查询岗位权限列表
     * @param roleId 岗位id
     * @return List<PermissionPO>
     */
    List<PermissionPO> selectRolePermissionsByRoleId(Long roleId);

    /**
     * 查询所有权限列表
     * @return List<PermissionPO>
     */
    List<PermissionPO> selectRolePermissions();

}