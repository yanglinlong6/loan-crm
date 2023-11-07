package com.daofen.admin.service.user;

import com.daofen.admin.basic.PageVO;
import com.daofen.admin.service.user.model.LinkOrgVO;
import com.daofen.admin.service.user.model.UserBO;
import com.daofen.admin.service.user.model.UserPO;

/**
 * 用户service接口
 */
public interface UserService {

    /**
     * 获取用户业务对象
     *
     * @param username 用户账户名
     * @return UserBO
     */
    UserBO getUserBOByUsername(String username);


    void getList(PageVO<UserPO> pageVO);

    void saveUser(UserPO userPO);

    void updateUser(UserPO userPO);

    void deleteUser(UserPO userPO);

    void findOneUser(UserPO userPO);

    void resetPassword(UserPO userPO);

    void linkOrg(LinkOrgVO linkOrgVO);
}
