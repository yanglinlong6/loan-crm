package com.daofen.admin.service.user;

import com.daofen.admin.basic.PageVO;
import com.daofen.admin.service.user.dao.UserDao;
import com.daofen.admin.service.user.dao.UserOrgRelDao;
import com.daofen.admin.service.user.model.LinkOrgVO;
import com.daofen.admin.service.user.model.UserBO;
import com.daofen.admin.service.user.model.UserOrgRelPO;
import com.daofen.admin.service.user.model.UserPO;
import com.daofen.admin.utils.JSONUtil;
import com.daofen.admin.utils.MD5Util;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户service实现类：UserServiceImpl
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserOrgRelDao userOrgRelDao;

    @Override
    public UserBO getUserBOByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            log.info("用户：【{}】不能为空", username);
            return null;
        }
        UserPO userPO = userDao.selectUserByUsername(username);
        if (null == userPO) {
            log.info("用户：【{}】不存在", username);
            return null;
        }
        log.info("用户【{}】获取用户:{}", username, userPO);
        return JSONUtil.toJavaBean(userPO.toString(), UserBO.class);
    }

    @Override
    public void getList(PageVO<UserPO> page) {
        Integer count = userDao.selectUserListCountByPage(page);
        page.setData(userDao.selectUserListByPage(page));
        page.setTotalCount(count);
    }

    @Override
    public void saveUser(UserPO userPO) {
        userPO.setName(userPO.getUsername());
        userPO.setPassword(MD5Util.getMd5String(userPO.getPassword()));
        userPO.setCreateDate(new Date());
        userDao.saveUser(userPO);
    }

    @Override
    public void updateUser(UserPO userPO) {
        userPO.setName(userPO.getUsername());
        userPO.setUpdateDate(new Date());
        userDao.updateUser(userPO);
    }

    @Override
    public void deleteUser(UserPO userPO) {
        userDao.deleteUser(userPO);
    }

    @Override
    public void findOneUser(UserPO userPO) {
        userDao.findOneUser(userPO);
    }

    @Override
    public void resetPassword(UserPO userPO) {
        userPO.setPassword(MD5Util.getMd5String("123456"));
        userPO.setUpdateDate(new Date());
        userDao.resetPassword(userPO);
    }

    @Override
    public void linkOrg(LinkOrgVO linkOrgVO) {
        //删除之前已经关联的
        userOrgRelDao.deleteUserOrgRelByUserId(linkOrgVO.getUserId());
        // 重新关联新的组织ID
        if (CollectionUtils.isNotEmpty(linkOrgVO.getOrgIds())) {
            linkOrgVO.getOrgIds().forEach(orgId -> {
                UserOrgRelPO userOrgRelPO = new UserOrgRelPO();
                userOrgRelPO.setUserId(linkOrgVO.getUserId());
                userOrgRelPO.setOrgId(orgId);
                userOrgRelPO.setDelFlag(0);
                userOrgRelDao.saveUserOrgRel(userOrgRelPO);
            });
        }
    }
}
