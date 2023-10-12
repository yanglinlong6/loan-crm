package com.loan.cps.process;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.AIUtil;
import com.loan.cps.common.NumberUtils;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.common.WechatMsgFactory;
import com.loan.cps.entity.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class MobileLocationNode extends Node{
	
	private static final Integer MOBILE_LOCATION_TRUE = 1;
	
	private static final Integer MOBILE_LOCATION_FALSE = 2;

	private static final Logger LOG = LoggerFactory.getLogger(MobileLocationNode.class);

//	@Override
//	public NodeResult answerHandle(Session session, JSONObject userMsg) {
//		LOG.info("MobileLocationNode-->{}",userMsg.toJSONString());
//		String menuid = userMsg.getString("bizmsgmenuid");
//		NodeResult result = new NodeResult();
//		result.setState(NodeResult.NODE_FAIL);
//		if(StringUtils.isEmpty(menuid)) {
//			String content = userMsg.getString("Content").trim();
//			if(!StringUtils.isEmpty(content)) {
//				if(Pattern.matches("^\\D*\\d+\\D*$", content)||Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", content)) {
//					int	amount = NumberUtils.getIntegerByNumberStr(content.replaceAll("[^一二三四五六七八九十百千万亿|^0-9]", ""));
//					if(amount>0&&amount<3) {
//						JSONObject qualification = new JSONObject();
//						if(MOBILE_LOCATION_TRUE.equals(amount)) {
//							qualification.put("city", session.getCity());
//							qualification.put("level", 2);
//							result.setQualification(qualification);
//							nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//						}else {
//							nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//						}
//						result.setState(NodeResult.NODE_SUCCESS);
//					}else {
//						WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
//					}
//				}else if(content.contains("是")||content.contains("否")||content.contains("不")||content.contains("嗯")||content.contains("没错")){
//					JSONObject qualification = new JSONObject();
//					if(content.contains("否")||content.contains("不")) {
//						qualification.put("city", session.getCity());
//						qualification.put("level", 2);
//						result.setQualification(qualification);
//						nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//					}else {
//						nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//					}
//					result.setState(NodeResult.NODE_SUCCESS);
//				}else {
//					AIUtil.answerAIMsg(content, userMsg,session);
//					this.sendQuestion(session);
//				}
//			}
//		}else {
//			JSONArray jsonArray = super.getConfig().getQuestionMsg().getJSONArray("list");
//			if(jsonArray==null) {
//				return result;
//			}
//			for (int i = 0; i < jsonArray.size(); i++) {
//				JSONObject jsonObject = jsonArray.getJSONObject(i);
//				if (jsonObject.getString("menu_id").equals(menuid)) {
//					JSONObject qualification = new JSONObject();
//					if(jsonObject.getInteger("state").equals(MOBILE_LOCATION_TRUE)) {
//						qualification.put("level", 2);
//						qualification.put("city", session.getCity());
//						result.setQualification(qualification);
//						nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//					}else {
//						nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//					}
//					result.setState(NodeResult.NODE_SUCCESS);
//				}
//			}
//		}
//		return result;
//	}

	@Override
	public NodeResult parseQualification(JSONObject menu, NodeResult result) {
		JSONObject qualification = new JSONObject();
		if(menu.getInteger("state").equals(MOBILE_LOCATION_TRUE)) {
			qualification.put("level", 4);
			qualification.put("city", menu.getString("city"));
			result.setQualification(qualification);
		}
		result.setState(NodeResult.NODE_SUCCESS);
		return result;
	}

	@Override
	public JSONObject parseMsg(Session session, JSONObject userMsg) {
		String content = userMsg.getString("Content").trim();
		JSONObject qualification = new JSONObject();
		if(!StringUtils.isEmpty(content)) {
			if(Pattern.matches("^\\D*\\d+\\D*$", content)||Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", content)) {
				int	amount = NumberUtils.getIntegerByNumberStr(content.replaceAll("[^一二三四五六七八九十百千万亿|^0-9]", ""));
				if(amount>0&&amount<3) {
					JSONArray jsonArray = super.getConfig().getQuestionMsg().getJSONArray("list");
					for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						if (jsonObject.getString("content").startsWith(String.valueOf(amount))) {
							qualification.putAll(jsonObject);
							qualification.put("city", session.getCity());
						}
					}
				}else {
					WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
					return null;
				}
			}else if(content.contains("是")||content.contains("否")||content.contains("不")||content.contains("嗯")||content.contains("没错")){
				if(!(content.contains("否")||content.contains("不"))) {
					qualification.put("city", session.getCity());
					qualification.put("choose", 1);
					qualification.put("state", MOBILE_LOCATION_TRUE);
				}else {
					qualification.put("choose", 2);
					qualification.put("state", MOBILE_LOCATION_FALSE);
				}
			}else {
				AIUtil.answerAIMsg(content, userMsg,session);
				this.sendQuestion(session);
				return null;
			}
		}
		return qualification;
	}

}
