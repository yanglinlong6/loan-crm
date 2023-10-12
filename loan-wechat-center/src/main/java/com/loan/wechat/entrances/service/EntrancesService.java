package com.loan.wechat.entrances.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.loan.wechat.login.entity.UserDTO;

public interface EntrancesService {

	UserDTO selUserByOpenid(String openid);
	
	UserDTO selUserByUserid(String userid);
	
	void bindUser(UserDTO dto);
	
	void updateBindUser(UserDTO dto);
	
	Integer getSetId(String wechatId);
	
	JSONObject getSet(String wechatId);
	
	void addWechatLog(Map<String, String> wechatMsg);
}
