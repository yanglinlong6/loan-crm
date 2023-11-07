package com.daofen.admin.service.user.model;

import com.daofen.admin.basic.BasePO;
import com.daofen.admin.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 后台管理系统用户表
 */
@Getter
@Setter
public class UserOrgRelPO extends BasePO {
    /**
     * 用户表ID
     */
    private Integer userId;

    /**
     * 组织表ID
     */
    private Integer orgId;

    /**
     * 删除标识 0 正常 -1 删除
     */
    private Integer delFlag;


    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}
