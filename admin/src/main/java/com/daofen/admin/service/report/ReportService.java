package com.daofen.admin.service.report;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface ReportService {
	
	JSONArray selectChannelReport(JSONObject params);
	
}
