package com.help.loan.distribute.service.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
@Mapper
public interface CustReportDao {
	
	List<JSONObject> getMediaChannelCustApt(JSONObject params);
	
	List<JSONObject> getOrgApt(JSONObject params);
	
	void insertMediaCustAptReport(List<JSONObject> params);
	
	void insertOrgCustAptReport(List<JSONObject> params);
	
	List<JSONObject> selOrgDetailedReport();
	
}
