package com.loan.cps.process;


import java.util.List;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.AIUtil;
import com.loan.cps.common.LenderCache;
import com.loan.cps.common.LenderFilter;
import com.loan.cps.common.LenderMsgBuilder;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.common.WechatMsgFactory;
import com.loan.cps.entity.LenderPO;
import com.loan.cps.entity.Session;

public class Finish3Node extends Node{

//	@Override
//	public NodeResult answerHandle(Session session, JSONObject userMsg) {
//		NodeResult result = new NodeResult();
//		result.setState(NodeResult.NODE_FAIL);
//		String content = userMsg.getString("Content").trim();
//		if(content.contains("换")) {
//			List<LenderPO> filter = LenderFilter.filter(session.getSettlement(), session,LenderCache.getCacheList(),3);
//			if(filter.isEmpty()) {
//				WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(config.getErrMsg(), session.getUserId()), "", session.getDomain2());
//			}else {
//				JSONObject clone = (JSONObject) config.getRecommendMsg().clone();
//				clone.put("content", String.format(clone.getString("content"),LenderMsgBuilder.buildLenderMsg(filter, session.getUserId(), session.getDomain2(),LenderMsgBuilder.FINISH)));
//				WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(clone, session.getUserId()), "", session.getDomain2());
//			}
//		}else {
//			AIUtil.answerAIMsg(content, userMsg, session);
//		}
//		return result;
//	}

	@Override
	public NodeResult parseQualification(JSONObject menu, NodeResult result) {
		return result;
	}

	@Override
	public JSONObject parseMsg(Session session, JSONObject userMsg) {
		String content = userMsg.getString("Content").trim();
		if(content.contains("换")) {
			List<LenderPO> filter = LenderFilter.filter(session.getSettlement(), session,LenderCache.getCacheList(),3);
			if(filter.isEmpty()) {
				WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(config.getErrMsg(), session.getUserId()), "", session.getDomain2());
			}else {
				JSONObject clone = (JSONObject) config.getRecommendMsg().clone();
				clone.put("content", String.format(clone.getString("content"),LenderMsgBuilder.buildLenderMsg(filter, session.getUserId(), session.getDomain2(),LenderMsgBuilder.FINISH)));
				WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(clone, session.getUserId()), "", session.getDomain2());
			}
		}else {
			AIUtil.answerAIMsg(content, userMsg, session);
		}
		return null;
	}

}
