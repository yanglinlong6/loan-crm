package com.loan.cps.dao;


import org.apache.ibatis.annotations.Mapper;

import com.loan.cps.entity.UserDTO;


@Mapper
public interface WechatUserBindDao {
	
	UserDTO select(String userId);
	
	
}
