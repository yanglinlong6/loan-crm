package com.loan.cps.dao;

import org.apache.ibatis.annotations.Mapper;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.entity.Session;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface SessionLogDao {

	void save(Session session);
	
	Integer selLastTimeNodeId(String userId);
	
	JSONObject get(Session session);
}
