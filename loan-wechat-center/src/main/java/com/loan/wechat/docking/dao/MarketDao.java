package com.loan.wechat.docking.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

@Component
@Mapper
public interface MarketDao {
	
	List<JSONObject> getById(String id);
	
	List<JSONObject> getByChannel();
	
	Long add(JSONObject market);
	
	void update(JSONObject market);
}
