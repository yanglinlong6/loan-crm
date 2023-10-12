package com.loan.cps.process;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.AIUtil;
import com.loan.cps.common.MobileLocationUtil;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.common.WechatMsgFactory;
import com.loan.cps.entity.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class MobileNode extends Node{
	private static final Logger LOG = LoggerFactory.getLogger(MobileLocationNode.class);
//	@Override
//	public NodeResult answerHandle(Session session, JSONObject userMsg) {
//		LOG.info("MobileNode-->{}",userMsg.toJSONString());
//		String menuid = userMsg.getString("bizmsgmenuid");
//		NodeResult result = new NodeResult();
//		result.setState(NodeResult.NODE_FAIL);
//		if (StringUtils.isEmpty(menuid)) {
//			String content = userMsg.getString("Content").trim();
//			if (!StringUtils.isEmpty(content)) {
//				if (Pattern.matches("^\\D*\\d{8,16}\\D*$", content)) {
//					String replaceAll = content.replaceAll("[^0-9]", "");
//					if(checkMoblie(replaceAll)) {
//						JSONObject qualification = new JSONObject();
//						JSONObject jsonObject = MobileLocationUtil.get(replaceAll);
//						if(jsonObject == null) {
//							nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//						}else {
//							session.setCity(jsonObject.getString("city").contains("市")?jsonObject.getString("city"):jsonObject.getString("city")+"市");
//							nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//						}
//						qualification.put("mobile", replaceAll);
//						result.setQualification(qualification);
//						result.setState(NodeResult.NODE_SUCCESS);
//					}else {
//						WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
//					}
//				} else {
//					AIUtil.answerAIMsg(content, userMsg,session);
//					this.sendQuestion(session);
//				}
//			}
//		}
//		return result;
//	}

	public boolean checkMoblie(String mobile) {
		String regex = "^((13[0-9])|(14[0-9])|(16[0-9])|(15[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$";
		boolean m = false;
		if(mobile.length()==11) {
			m = Pattern.matches(regex, mobile);
		}
		return m;
	}

	@Override
	public NodeResult parseQualification(JSONObject menu, NodeResult result) {
		JSONObject qualification = new JSONObject();
		qualification.put("mobile", menu.getString("mobile"));
		qualification.put("city", menu.getString("city"));
		qualification.put("level", 2);
		qualification.put("province", menu.getString("province"));
		result.setQualification(qualification);
		result.setState(NodeResult.NODE_SUCCESS);
		return result;
	}

	@Override
	public JSONObject parseMsg(Session session, JSONObject userMsg) {
		String content = userMsg.getString("Content").trim();
		JSONObject qualification = new JSONObject();
		if (!StringUtils.isEmpty(content)) {
			if (Pattern.matches("^\\D*\\d{8,16}\\D*$", content)) {
				String replaceAll = content.replaceAll("[^0-9]", "");
				if(checkMoblie(replaceAll)) {
					JSONObject jsonObject = MobileLocationUtil.getAndCheck(replaceAll);
					if(jsonObject == null) {
						WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
						return null;
					}else {
						session.setCity(jsonObject.getString("City").contains("市")?jsonObject.getString("City"):jsonObject.getString("City")+"市");
						qualification.put("province", jsonObject.getString("status"));
						qualification.put("mobile", replaceAll);
						qualification.put("city", session.getCity());
						qualification.put("choose", 1);
					}
				}else {
					WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
					return null;
				}
			} else {
				AIUtil.answerAIMsg(content, userMsg,session);
				this.sendQuestion(session);
				return null;
			}
		}
		return qualification;
	}
	
}
