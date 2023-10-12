package com.daofen.crm.controller.system;

import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.AbstractController;
import com.daofen.crm.service.counselor.PermissionService;
import com.daofen.crm.service.counselor.model.PermissionBO;
import com.daofen.crm.service.counselor.model.PermissionPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

@RestController
public class PermissionController extends AbstractController {

    @Autowired
    private PermissionService permissionService;


    @RequestMapping("/permission/list")
    @ResponseBody
    public ResultVO permissionList(){
        return this.success(permissionService.getAllPermission());
    }

    @RequestMapping("/permission/add")
    @ResponseBody
    public ResultVO addPermission(@Validated() @RequestBody() PermissionPO permissionPO){
        permissionService.addPermission(permissionPO);
        return this.success();
    }

    @RequestMapping("/permission/edit")
    @ResponseBody
    public ResultVO editPermission(@Validated() @RequestBody()PermissionBO permissionBO){
        permissionService.savePermission(permissionBO);
        return this.success();
    }

    @RequestMapping("/permission/del")
    @ResponseBody
    public ResultVO delPermission(@PathParam(value = "id") Long id){
        if(null == id || id <= 0){
            return this.success();
        }
        permissionService.delPermission(id);
        return this.success();
    }





}
