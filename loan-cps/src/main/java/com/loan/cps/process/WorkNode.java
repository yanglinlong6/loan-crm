package com.loan.cps.process;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.AIUtil;
import com.loan.cps.common.NumberUtils;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.common.WechatMsgFactory;
import com.loan.cps.entity.Session;

public class WorkNode extends Node{
	
	private static final Integer OFFICE_WORKER = 1;
	
	private static final Integer BUSINESS_MAN = 2;
	
	private static final Integer OFFICE_BUSINESS =3;
	
	private static final Integer UNEMPLOYED = 4;

//	@Override
//	public NodeResult answerHandle(Session session, JSONObject userMsg) {
//		String menuid = userMsg.getString("bizmsgmenuid");
//		NodeResult result = new NodeResult();
//		result.setState(NodeResult.NODE_FAIL);
//		if(StringUtils.isEmpty(menuid)) {
//			String content = userMsg.getString("Content").trim();
//			if(!StringUtils.isEmpty(content)) {
//				if(Pattern.matches("^\\D*\\d+\\D*$", content)||Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", content)) {
//					int	fund = NumberUtils.getIntegerByNumberStr(content.replaceAll("[^一二三四五六七八九十百千万亿|^0-9]", ""));
//					if(fund>0 && fund<5){
//						if(BUSINESS_MAN.intValue() == fund) {
//							nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//						}else {
//							nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//						}
//						JSONObject qualification = new JSONObject();
//						qualification.put("occupation", fund);
//						result.setQualification(qualification);
//						result.setState(NodeResult.NODE_SUCCESS);
//					}else {
//						WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
//					}
//				}else if (content.contains("企业")||content.contains("生意")||content.contains("上班")){
//					if(content.contains("上班")&&!content.contains("没上班")) {
//						JSONObject qualification = new JSONObject();
//						qualification.put("occupation", 1);
//						result.setQualification(qualification);
//						result.setState(NodeResult.NODE_SUCCESS);
//						nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//					}else if((content.contains("生意")||content.contains("企业"))&&!content.contains("没做生意")) {
//						JSONObject qualification = new JSONObject();
//						qualification.put("occupation", 2);
//						result.setQualification(qualification);
//						result.setState(NodeResult.NODE_SUCCESS);
//						nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//					}else {
//						JSONObject qualification = new JSONObject();
//						qualification.put("occupation", UNEMPLOYED);
//						result.setQualification(qualification);
//						result.setState(NodeResult.NODE_SUCCESS);
//						nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//					}
//				}else  if(content.contains("没")||content.contains("无")){
//					JSONObject qualification = new JSONObject();
//					qualification.put("occupation", UNEMPLOYED);
//					nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
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
//					if(BUSINESS_MAN.equals(jsonObject.getInteger("state"))) {
//						nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//					}else {
//						nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//					}
//					JSONObject qualification = new JSONObject();
//					qualification.put("occupation", jsonObject.getInteger("state"));
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
		qualification.put("occupation", menu.getInteger("state"));
		result.setQualification(qualification);
		result.setState(NodeResult.NODE_SUCCESS);
		return result;
	}

	@Override
	public JSONObject parseMsg(Session session, JSONObject userMsg) {
		String content = userMsg.getString("Content").trim();
		JSONObject qualification = new JSONObject();
		if(!StringUtils.isEmpty(content)) {
			if(Pattern.matches("^\\D*\\d+\\D*$", content)||Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", content)) {
				int	fund = NumberUtils.getIntegerByNumberStr(content.replaceAll("[^一二三四五六七八九十百千万亿|^0-9]", ""));
				if(fund>0 && fund<5){
					JSONArray jsonArray = super.getConfig().getQuestionMsg().getJSONArray("list");
					for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						if (jsonObject.getString("content").startsWith(String.valueOf(fund))) {
							qualification.putAll(jsonObject);
						}
					}
				}else {
					WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
					return null;
				}
			}else if (content.contains("企业")||content.contains("生意")||content.contains("上班")){
				if(content.contains("上班")&&!content.contains("没上班")) {
					qualification.put("occupation", 1);
					qualification.put("choose", 2);
				}else if((content.contains("生意")||content.contains("企业"))&&!content.contains("没做生意")) {
					qualification.put("occupation", 2);
					qualification.put("choose", 1);
				}else {
					qualification.put("occupation", UNEMPLOYED);
					qualification.put("choose", 2);
				}
			}else  if(content.contains("没")||content.contains("无")){
				qualification.put("occupation", UNEMPLOYED);
				qualification.put("choose", 2);
			}else {
				AIUtil.answerAIMsg(content, userMsg,session);
				this.sendQuestion(session);
				return null;
			}
		}
		return qualification;
	}

}
