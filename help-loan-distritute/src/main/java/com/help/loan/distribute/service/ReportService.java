package com.help.loan.distribute.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public interface ReportService {
	
	List<JSONObject> getMediaChannelCustApt(JSONObject params);
	
	List<JSONObject> getOrgApt(JSONObject params);
	
	void insertMediaCustAptReport(List<JSONObject> params);
	
	void insertOrgCustAptReport(List<JSONObject> params);
	
	List<JSONObject> selOrgDetailedReport();
}
