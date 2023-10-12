package com.loan.cps.dao;

import org.apache.ibatis.annotations.Mapper;

import com.alibaba.fastjson.JSONObject;

@Mapper
public interface NodeConfigDao {
	
	JSONObject getNodeConfig(Integer node);
	
}
