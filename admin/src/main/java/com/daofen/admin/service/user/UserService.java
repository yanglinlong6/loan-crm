package com.daofen.admin.service.user;

import com.daofen.admin.service.user.model.UserBO;

/**
 * 用户service接口
 */
public interface UserService {

    /**
     * 获取用户业务对象
     * @param username 用户账户名
     * @return UserBO
     */
    UserBO getUserBOByUsername(String username);


}
