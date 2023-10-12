package com.daofen.admin.service.report.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.alibaba.fastjson.JSONObject;

@Mapper
public interface ReportDao {
	
	List<JSONObject> selectChannelReport(JSONObject params);
	
}
