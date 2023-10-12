package com.daofen.crm.service.counselor;

import com.daofen.crm.service.counselor.model.PermissionBO;
import com.daofen.crm.service.counselor.model.PermissionPO;
import com.daofen.crm.service.counselor.model.RolePO;
import com.daofen.crm.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

public interface PermissionService {

    List<PermissionPO> getRolePermissionList(Long roleId);

    void addPermission(PermissionPO permission);

    void delPermission(Long id);

    void savePermission(PermissionPO permission);

    void addRolePermission(RolePO role,List<Long> permissionIdList);

    void addRolePermission2(RolePO role,List<PermissionPO> permissionIdList);

    void delRolePermission(Long roleId);

    void initRolePermission(Long roleId,byte roleType);

    PermissionBO getAllPermission();


    default PermissionBO toRoot(List<PermissionBO> list){
        if(CollectionUtil.isEmpty(list)){
            return null;
        }
        PermissionBO root = new PermissionBO();
        root.setName("根目录");
        root.setId(0l);
        List<PermissionBO> rootChildList = new ArrayList<>();
        //递归成一棵树行结构
        for(PermissionBO permission : list){
            // 如果是一级菜单，则加入到根节点的子节点中
            if(null == permission.getParentId() || 0 == permission.getParentId().longValue()){
                getChild(permission,list);
                rootChildList.add(permission);
            }
        }
        root.setChildList(rootChildList);
        return root;
    }


    default void getChild(PermissionBO root,List<PermissionBO> list){
        List<PermissionBO> childList = new ArrayList<>();
        for(PermissionBO permission : list){
            if(root.getId().longValue() != permission.getParentId().longValue()){
                continue;
            }
            getChild(permission,list);
            childList.add(permission);
        }
        root.setChildList(childList);
    }


}
