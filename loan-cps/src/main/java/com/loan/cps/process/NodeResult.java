package com.loan.cps.process;

import com.alibaba.fastjson.JSONObject;

public class NodeResult {
	
	public static final Integer NODE_SUCCESS= 0;
	
	public static final Integer NODE_FAIL = 1;
	
	private Integer state;
	
	private JSONObject qualification;
	
	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public JSONObject getQualification() {
		return qualification;
	}

	public void setQualification(JSONObject qualification) {
		this.qualification = qualification;
	}
	
}
