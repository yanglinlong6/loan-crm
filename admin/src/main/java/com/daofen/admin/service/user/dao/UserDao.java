package com.daofen.admin.service.user.dao;

import com.daofen.admin.service.user.model.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {

    UserPO selectUserByUsername(@Param("username") String username);

}
