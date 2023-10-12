package com.crm.controller.org;

import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.role.RoleService;
import com.crm.service.role.model.RoleBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 岗位接口
 */
@RestController
public class RoleController {

    @Autowired
    RoleService roleService;

    @PostMapping("/role/list")
    public ResultVO all(RoleBO roleBO){
        return ResultVO.success("获取岗位列表成功",roleService.getAll(LoginUtil.getLoginEmployee().getOrgId()));
    }

    @PostMapping("/role/add")
    public ResultVO add(@RequestBody() RoleBO roleBO){
        roleService.addRole(roleBO);
        return ResultVO.success("新增岗位列表成功",null);
    }

    @PostMapping("/role/update")
    public ResultVO update(@RequestBody()RoleBO roleBO){
        roleService.updateRole(roleBO);
        return ResultVO.success("新增岗位列表成功",null);
    }





}
