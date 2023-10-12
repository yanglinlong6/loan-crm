package com.loan.cps.service;

import com.alibaba.fastjson.JSONObject;

public interface DialogueFacade {
	
	void exc(JSONObject userMsg);
	
	void start(JSONObject userMsg);
	
	void end(JSONObject userMsg);
	
	void proceed(String userId,String domain2);
	
	void proceed2(String userId);
}
