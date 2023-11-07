package com.daofen.admin.service.user.dao;

import com.daofen.admin.basic.PageVO;
import com.daofen.admin.service.user.model.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao {

    UserPO selectUserByUsername(@Param("username") String username);

    /**
     * 统计用户数量
     *
     * @param page 用户名
     * @return
     */
    Integer selectUserListCountByPage(PageVO<UserPO> page);

    /**
     * 分页查询用户集合
     *
     * @param page 用户名
     * @return
     */
    List<UserPO> selectUserListByPage(PageVO<UserPO> page);
}
