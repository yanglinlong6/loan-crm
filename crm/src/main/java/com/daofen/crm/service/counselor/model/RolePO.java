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
public class RolePO extends BasePO {
    @NotBlank(message = "角色名称必填")
    private String name; // 角色名称

    @NotNull(message = "角色类型是必选项")
    @Min(value = 0,message = "角色类型必须是0-5之间的整数")
    @Max(value = 5,message = "角色类型必须是0-5之间的整数")
    private Byte type; // 角色类型(0-超级管理员，1-管理员，2-公司管理，3-门店主管，4-团队主管，5-普通账号)

    private Long companyId;// 所属机构id

    public RolePO() {
    }

    public RolePO(String name, Byte type, Long companyId) {
        this.name = name;
        this.type = type;
        this.companyId = companyId;
    }

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}