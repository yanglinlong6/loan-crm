package com.daofen.crm.service.counselor;

import com.daofen.crm.base.CrmConstant;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import com.daofen.crm.service.counselor.model.RoleBO;
import com.daofen.crm.service.counselor.model.RolePO;
import com.daofen.crm.utils.CollectionUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public interface RoleService {

    void getRolePage(PageVO<RolePO> pageVO);

    /**
     * 根据 角色id查询角色信息对象
     * @param id 角色id
     * @return RolePO
     */
    RoleBO getRole(Long id);

    /**
     * 查询机构下的角色列表
     * @param companyId 机构id
     * @return List<RolePO>
     */
    List<RolePO> getCompanyRoleList(Long companyId);


    /**
     *  角色对象
     * @param rolePO RolePO
     * @return RolePO
     */
    RolePO addRole(RolePO rolePO);

    @Transactional
    RoleBO addRole(RoleBO roleBO);

    @Transactional
    void updateRole(RoleBO roleBO);


    void delRole(Long id);

    /**
     * 初始化角色权限信息
     * @param roleId 角色id
     * @param roleType 角色类型(0-超级管理员，1-管理员，2-门店店长，3-团队主管，4-普通账号)
     */
    void intRolePermission(Long roleId,byte roleType);

    /**
     * 删除角色和权限的关联关系
     * @param roleId 角色id
     */
    void delRolePermission(Long roleId);

    /**
     * 初始化机构的角色列表
     * @param counselor CompanyCounselorBO 待新增的顾问业务对象
     * @return RolePO  返回管理员角色信息
     */
    default RolePO initCompanyRole(CompanyCounselorBO counselor){
        Long parentId = 0l;
        if(CrmConstant.Company.Type.PARENT == counselor.getCompany().getType().byteValue())
            parentId = counselor.getCompanyId();
        else
            parentId = counselor.getCompany().getParentId();
        List<RolePO> roles = getCompanyRoleList(parentId);
        if(null == roles)
            roles = new ArrayList<>();
        initRole(parentId,roles,CrmConstant.Role.Type.SHOP);
        initRole(parentId,roles,CrmConstant.Role.Type.GROUP);
        initRole(parentId,roles,CrmConstant.Role.Type.COUNSELOR);
        if(CrmConstant.Company.Type.PARENT == counselor.getCompany().getType().byteValue() ){
            initRole(parentId,roles,CrmConstant.Role.Type.COMPANY);
            return initRole(parentId,roles,CrmConstant.Role.Type.ADMIN);
        }
        initRole(parentId,roles,CrmConstant.Role.Type.ADMIN);
        return initRole(parentId,roles,CrmConstant.Role.Type.COMPANY);
    }

    /**
     * 新增初始化机构角色
     * @param companyId 机构id
     * @return RolePO
     */
    default RolePO initRole(Long companyId,List<RolePO> roles,byte type){
        RolePO rolePO = findRole(roles,type);
        String name="";
        if(null == rolePO){
            switch(type) {
                case CrmConstant.Role.Type.ADMIN:
                    name = "管理员";
                    break;
                case CrmConstant.Role.Type.COMPANY:
                    name = "公司管理员";
                    break;
                case CrmConstant.Role.Type.SHOP:
                    name = "店长";
                    break;
                case CrmConstant.Role.Type.GROUP:
                    name = "团队主管";
                    break;
                case CrmConstant.Role.Type.COUNSELOR:
                    name = "顾问";
                    break;
                default:name = "超级管理员";
            }
            RolePO role = new RolePO(name, type,companyId);
            RolePO newRole = addRole(role);
            intRolePermission(newRole.getId(),type);
            return role;
        }
        return  rolePO;
    }
    default RolePO findRole(List<RolePO> roles,byte type){
        if(CollectionUtil.isEmpty(roles)){
            return null;
        }
        for(RolePO role : roles){
            if(type == role.getType().byteValue()){
                return role;
            }
        }
        return null;
    }

}
