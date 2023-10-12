package com.daofen.crm.service.counselor;

import com.daofen.crm.base.CrmConstant;
import com.daofen.crm.base.CrmException;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.company.CompanyService;
import com.daofen.crm.service.counselor.dao.RoleMapper;
import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import com.daofen.crm.service.counselor.model.PermissionPO;
import com.daofen.crm.service.counselor.model.RoleBO;
import com.daofen.crm.service.counselor.model.RolePO;
import com.daofen.crm.utils.CollectionUtil;
import com.daofen.crm.utils.JSONUtil;
import com.daofen.crm.utils.JudgeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private CompanyService companyService;

    private static final Logger LOG = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Override
    public void getRolePage(PageVO<RolePO> pageVO) {
        if(null == pageVO.getParam()){
            RolePO role = new RolePO();
            role.setCompanyId(LoginUtil.getLoginUser().getCompanyId());
            pageVO.setParam(role);
        }else{
            pageVO.getParam().setCompanyId(LoginUtil.getLoginUser().getCompanyId());
        }
        pageVO.setData(roleMapper.selectRolePage(pageVO));
        pageVO.setTotalCount(roleMapper.selectRolePageCount(pageVO));
        pageVO.getParam().setCompanyId(null);
    }

    @Override
    public RoleBO getRole(Long id) {
        RolePO role = roleMapper.selectRoleById(id);
        if(null == role){
            return null;
        }
        RoleBO bo = JSONUtil.toJavaBean(role.toString(),RoleBO.class);
        List<PermissionPO> list = permissionService.getRolePermissionList(role.getId());
        bo.setPermissions(list);
        return bo;
    }

    @Override
    public List<RolePO> getCompanyRoleList(Long companyId) {
        return roleMapper.selectRoleList(companyId);
    }

    @Override
    public RolePO addRole(RolePO rolePO) {
        CompanyCounselorBO account = LoginUtil.getLoginUser();
        if(null == rolePO.getCompanyId()){
            if(CrmConstant.Role.Type.ADMIN == account.getRole().getType()){
                rolePO.setCompanyId(account.getCompanyId());
            }else{
                rolePO.setCompanyId(account.getCompany().getParentId());
            }
        }
        rolePO.setCreateBy(account.getId().toString());
        rolePO.setCreateDate(new Date());
        roleMapper.insertRole(rolePO);
        return rolePO;
    }

    @Override
    public RoleBO addRole(RoleBO roleBO) {
        if(CollectionUtil.isEmpty(roleBO.getPermissions()))
            return null;
        CompanyCounselorBO account = LoginUtil.getLoginUser();
        Long parentId = null;
        if(null == account.getCompany().getParentId()){
            parentId = account.getCompanyId();
        }else
            parentId = account.getCompany().getParentId();
        String companyIds = companyService.getAllCompanyIds(parentId);
        if(null != roleMapper.selectRoleByCompanyIdAndName(companyIds,roleBO.getName()))
            throw new CrmException(ResultVO.ResultCode.FAIL,"角色名称已存在");
        roleBO.setCompanyId(account.getCompanyId());
        roleBO.setCreateBy(account.getId().toString());
        roleBO.setCreateDate(new Date());
        roleMapper.insertRole(roleBO);
        permissionService.addRolePermission2(roleBO,roleBO.getPermissions());
        return roleBO;
    }

    @Override
    public void updateRole(RoleBO roleBO) {
        if(roleBO.getId() == null || CollectionUtil.isEmpty(roleBO.getPermissions()))
            return;
        CompanyCounselorBO account = LoginUtil.getLoginUser();
        if(CrmConstant.Role.Type.ADMIN == account.getRole().getType()){
            roleBO.setCompanyId(account.getCompanyId());
        }else{
            roleBO.setCompanyId(account.getCompany().getParentId());
        }
        roleBO.setUpdateBy(account.getId().toString());
        roleBO.setUpdateDate(new Date());

        roleMapper.updateRole(roleBO);
        permissionService.delRolePermission(roleBO.getId());
        permissionService.addRolePermission2(JSONUtil.toJavaBean(roleBO.toString(),RolePO.class),roleBO.getPermissions());
    }

    @Override
    public void delRole(Long id) {
        roleMapper.deleteRoleById(id);
    }

    @Override
    public void intRolePermission(Long roleId, byte roleType) {
        permissionService.initRolePermission(roleId,roleType);
    }

    @Override
    public void delRolePermission(Long roleId) {
        if(null == roleId || roleId <= 0)
            return;
        RolePO role = roleMapper.selectRoleById(roleId);
        if(null == role || JudgeUtil.in(role.getType().intValue(),0,1,2,3,4,5))
            throw new CrmException(ResultVO.ResultCode.FAIL,"系统角色不能删除！");
        permissionService.delRolePermission(roleId);
    }
}
