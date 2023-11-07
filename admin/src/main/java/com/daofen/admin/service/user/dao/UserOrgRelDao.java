package com.daofen.admin.service.user.dao;

import com.daofen.admin.service.user.model.UserOrgRelPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserOrgRelDao {

    void deleteUserOrgRelByUserId(@Param("userId") Integer userId);

    void saveUserOrgRel(UserOrgRelPO userOrgRelPO);
}
