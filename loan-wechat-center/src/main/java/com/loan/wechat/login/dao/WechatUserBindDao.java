package com.loan.wechat.login.dao;

import org.apache.ibatis.annotations.Mapper;

import com.loan.wechat.login.entity.UserDTO;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface WechatUserBindDao {
	
	UserDTO selUserByOpenid(String openid);
	
	UserDTO selUserByUserid(String userid);
	
	void update(UserDTO dto);
	
	void insert(UserDTO dto);
	
}
