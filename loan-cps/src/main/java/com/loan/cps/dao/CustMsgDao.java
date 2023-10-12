package com.loan.cps.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.alibaba.fastjson.JSONObject;

@Mapper
public interface CustMsgDao {
	
	List<JSONObject> getCustMsgMenuList(String msgId);
	
	List<JSONObject> getNodeMsgList(Integer nodeId);
	
}
