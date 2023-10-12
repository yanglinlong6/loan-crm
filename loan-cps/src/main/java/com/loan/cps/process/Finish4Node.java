package com.loan.cps.process;


import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.AIUtil;
import com.loan.cps.entity.Session;

public class Finish4Node extends Node{

//	@Override
//	public NodeResult answerHandle(Session session, JSONObject userMsg) {
//		NodeResult result = new NodeResult();
//		result.setState(NodeResult.NODE_FAIL);
//		String content = userMsg.getString("Content").trim();
//		AIUtil.answerAIMsg(content, userMsg, session);
//		return result;
//	}

	@Override
	public NodeResult parseQualification(JSONObject menu, NodeResult result) {
		return result;
	}

	@Override
	public JSONObject parseMsg(Session session, JSONObject userMsg) {
		String content = userMsg.getString("Content").trim();
		if (!StringUtils.isEmpty(content)) {
			AIUtil.answerAIMsg(content, userMsg,session);
		}
		return null;
	}

}
