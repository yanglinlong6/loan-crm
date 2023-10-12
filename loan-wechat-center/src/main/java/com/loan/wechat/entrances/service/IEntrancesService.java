package com.loan.wechat.entrances.service;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.loan.wechat.entrances.dao.ActionSetDao;
import com.loan.wechat.login.dao.WechatUserBindDao;
import com.loan.wechat.login.entity.UserDTO;

@Component
public class IEntrancesService implements EntrancesService{

	@Autowired
	private WechatUserBindDao wechatUserBindDao;
	
	@Autowired
	private ActionSetDao actionSetDao;

	@Override
	public UserDTO selUserByOpenid(String openid) {
		return wechatUserBindDao.selUserByOpenid(openid);
	}

	@Override
	public void bindUser(UserDTO dto) {
		wechatUserBindDao.insert(dto);
	}

	@Override
	public void updateBindUser(UserDTO dto) {
		wechatUserBindDao.update(dto);
	}

	@Override
	public Integer getSetId(String wechatId) {
		return actionSetDao.selSetId(wechatId);
	}

	@Override
	public void addWechatLog(Map<String, String> wechatMsg) {
		
	}

	@Override
	public UserDTO selUserByUserid(String userid) {
		return wechatUserBindDao.selUserByUserid(userid);
	}

	@Override
	public JSONObject getSet(String wechatId) {
		return actionSetDao.selSet(wechatId);
	}
	

}
