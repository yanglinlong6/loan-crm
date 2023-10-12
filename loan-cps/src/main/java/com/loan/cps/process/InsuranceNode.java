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

public class InsuranceNode extends Node{
	
	private static final Integer INSURANCE_HALF_YEAR_MORE = 1;
	
	private static final Integer INSURANCE_HALF_YEAR_LESS = 2;
	
	private static final Integer INSURANCE_NONE = 3;
	private static final Logger LOG = LoggerFactory.getLogger(MobileLocationNode.class);
//	@Override
//	public NodeResult answerHandle(Session session, JSONObject userMsg) {
//		LOG.info("InsuranceNode-->{}",userMsg.toJSONString());
//		String menuid = userMsg.getString("bizmsgmenuid");
//		NodeResult result = new NodeResult();
//		result.setState(NodeResult.NODE_FAIL);
//		if (StringUtils.isEmpty(menuid)) {
//			String content = userMsg.getString("Content").trim();
//			if (!StringUtils.isEmpty(content)) {
//				if(Pattern.matches("^\\D*\\d+\\D*$", content)) {
//					int	choose = NumberUtils.getIntegerByNumberStr(content.replaceAll("[^一二三四五六七八九十百千万亿|^0-9]", ""));
//					if(choose>0&&choose<4) {
//						JSONObject qualification = new JSONObject();
//						if(INSURANCE_HALF_YEAR_MORE.equals(choose)) {
//							qualification.put("insurance", choose);
//							nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//						}else {
//							qualification.put("insurance", choose);
//							nodeManager.getNode(super.getConfig().getLeftNodeId(session.getLocation())).sendQuestion(session);
//						}
//						result.setQualification(qualification);
//						result.setState(NodeResult.NODE_SUCCESS);
//					}else {
//						WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
//					}
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
//					if(INSURANCE_HALF_YEAR_MORE.equals(jsonObject.getInteger("state"))) {
//						nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//					}else {
//						nodeManager.getNode(super.getConfig().getLeftNodeId(session.getLocation())).sendQuestion(session);
//					}
//					qualification.put("insurance", jsonObject.getInteger("state"));
//					result.setQualification(qualification);
//					result.setState(NodeResult.NODE_SUCCESS);
//				}
//			}
//		}
//		return result;
//	}
	@Override
	public NodeResult parseQualification(JSONObject menu, NodeResult result) {
		JSONObject qualification = new JSONObject();
		qualification.put("insurance", menu.getInteger("state"));
		result.setQualification(qualification);
		result.setState(NodeResult.NODE_SUCCESS);
		return result;
	}
	@Override
	public JSONObject parseMsg(Session session, JSONObject userMsg) {
		String content = userMsg.getString("Content").trim();
		JSONObject qualification = new JSONObject();
		if (!StringUtils.isEmpty(content)) {
			if(Pattern.matches("^\\D*\\d+\\D*$", content)) {
				int	choose = NumberUtils.getIntegerByNumberStr(content.replaceAll("[^一二三四五六七八九十百千万亿|^0-9]", ""));
				if(choose>0&&choose<4) {
					JSONArray jsonArray = super.getConfig().getQuestionMsg().getJSONArray("list");
					for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						if (jsonObject.getString("content").startsWith(String.valueOf(choose))) {
							qualification.putAll(jsonObject);
						}
					}
				}else {
					WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
					return null;
				}
			}else if(content.contains("没有")||content.contains("有")||content.contains("买")||content.contains("不够")||content.contains("不足")||content.contains("近")||content.contains("刚")){
				if(content.contains("没")) {
					qualification.put("state", INSURANCE_NONE);
					qualification.put("choose", 2);
				}else if(content.contains("不够")||content.contains("不足")||content.contains("近")||content.contains("刚")) {
					qualification.put("state", INSURANCE_HALF_YEAR_LESS);
					qualification.put("choose", 2);
				}else {
					qualification.put("state", INSURANCE_HALF_YEAR_MORE);
					qualification.put("choose", 1);
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
