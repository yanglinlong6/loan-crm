package com.daofen.admin.service.user;

import com.daofen.admin.service.user.dao.UserDao;
import com.daofen.admin.service.user.model.UserBO;
import com.daofen.admin.service.user.model.UserPO;
import com.daofen.admin.utils.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户service实现类：UserServiceImpl
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Override
    public UserBO getUserBOByUsername(String username) {
        if(StringUtils.isBlank(username)){
            log.info("用户：【{}】不能为空",username);
            return null;
        }
        UserPO userPO = userDao.selectUserByUsername(username);
        if(null == userPO){
            log.info("用户：【{}】不存在",username);
            return null;
        }
        log.info("用户【{}】获取用户:{}",username,userPO);
        return JSONUtil.toJavaBean(userPO.toString(),UserBO.class);
    }
}
