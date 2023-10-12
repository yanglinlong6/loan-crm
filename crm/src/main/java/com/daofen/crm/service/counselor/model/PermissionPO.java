package com.daofen.crm.service.counselor.model;

import com.daofen.crm.base.BasePO;
import com.daofen.crm.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Setter
@Getter
public class PermissionPO extends BasePO {
    @NotNull(message = "请选择父级菜单,如果是一级菜单,则父级菜单请选择根菜单")
    private Long parentId; // 父级菜单id

    @NotBlank(message = "菜单或者功能按钮名称不能为空,必填")
    private String name; // 权限名称

    @NotNull(message = "请选择菜单类型")
    @Min(value = 1,message = "请选择菜单类型")
    @Max(value = 2,message = "请选择菜单类型")
    private Byte type; //权限类型(1-菜单，2-功能按钮)

//    @NotBlank(message = "请输入该菜单请求的uri地址")
    private String uri;// 菜单请求的uri地址

//    @NotBlank(message = "请输入菜单前端路由地址")
    private String routeUri;//前端路由uri地址

    private String remark; // 备注信息


    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}