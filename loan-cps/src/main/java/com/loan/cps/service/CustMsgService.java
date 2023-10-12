package com.loan.cps.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public interface CustMsgService {
	
	List<JSONObject> getNodeMsg(Integer nodeId);
	
}
