package com.loan.cps.process;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.entity.Session;

public class UrgeMsgNode extends Node{

	@Override
	public NodeResult answerHandle(Session session, JSONObject userMsg) {
		return null;
	}

	@Override
	public NodeResult parseQualification(JSONObject menu, NodeResult result) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject parseMsg(Session session, JSONObject userMsg) {
		// TODO Auto-generated method stub
		return null;
	}

}
