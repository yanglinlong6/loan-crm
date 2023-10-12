package com.loan.wechat.entrances.dao;

import org.apache.ibatis.annotations.Mapper;

import com.alibaba.fastjson.JSONObject;

@Mapper
public interface ActionSetDao {
	
	Integer selSetId(String wechatid);
	
	JSONObject selSet(String wechatid);
	
}
