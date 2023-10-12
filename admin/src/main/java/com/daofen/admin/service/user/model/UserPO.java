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
public class UserPO extends BasePO {

    private String name; // 账户姓名

    private String username;//账号

    private String password;//密码


    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}
