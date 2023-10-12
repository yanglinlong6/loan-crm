package com.daofen.crm.controller.system;

import com.daofen.crm.base.CrmConstant;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.AbstractController;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.counselor.RoleService;
import com.daofen.crm.service.counselor.model.RoleBO;
import com.daofen.crm.service.counselor.model.RolePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
public class RoleController extends AbstractController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/role/list")
    @ResponseBody
    public ResultVO pageList(@RequestBody()PageVO<RolePO> pageVO){
        roleService.getRolePage(pageVO);
        return this.success(pageVO);
    }

    @PostMapping("/role/add")
    @ResponseBody
    public ResultVO addRole(@Validated()@RequestBody()RoleBO roleBO){
        roleService.addRole(roleBO);
        return this.success();
    }

    @PostMapping("/role/edit")
    @ResponseBody
    public ResultVO editRole(@Validated()@RequestBody()RoleBO roleBO){
        roleService.updateRole(roleBO);
        return this.success();
    }

    @GetMapping("/role/del")
    @ResponseBody
    public ResultVO delRole(@PathParam("id")Long id){
        if(null == id || id <= 0){
            return this.failed("非法id",null);
        }
        roleService.delRole(id);
        return this.success();
    }


    @GetMapping("/role/list/with/company")
    @ResponseBody
    public ResultVO companyRole(@PathParam("companyId")Long companyId){
        if(null == companyId) {
            if(CrmConstant.Company.Type.PARENT == LoginUtil.getLoginUser().getCompany().getType()
                && (null == LoginUtil.getLoginUser().getCompany().getParentId() ||  0l == LoginUtil.getLoginUser().getCompany().getParentId()))
                companyId = LoginUtil.getLoginUser().getCompanyId();
            else
                companyId = LoginUtil.getLoginUser().getCompany().getParentId();
        }
        return this.success(roleService.getCompanyRoleList(companyId));
    }



}
