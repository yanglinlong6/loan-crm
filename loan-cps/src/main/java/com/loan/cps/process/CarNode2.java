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

public class CarNode2 extends Node{
	
	private static final Integer LOCAL_PAYMENT_IN_FULL = 1;
	
	private static final Integer LOCAL_ANJIE = 2;
	
	private static final Integer OTHER = 4;

	private static final Integer NONE = 3;

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
//						JSONObject qualification = new JSONObject();
//						if(LOCAL_PAYMENT_IN_FULL.intValue()==fund ||OTHER==fund) {
//							nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//						}else {
//							nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//						}
//						qualification.put("car", fund);
//						result.setQualification(qualification);
//						result.setState(NodeResult.NODE_SUCCESS);
//					} else if(fund>10000 && fund<500000){
//						JSONObject qualification = new JSONObject();
//						if(fund == 100000) {
//							if(!(content.contains("不足")||content.contains("没"))) {
//								if(content.contains("外地")&&!content.contains("不是外地")) {
//									qualification.put("car", OTHER);
//									nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//								}else {
//									if(content.contains("按揭")&&!(content.contains("不是按揭")||content.contains("没按揭"))) {
//										qualification.put("car", LOCAL_ANJIE);
//										nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//									}else {
//										qualification.put("car", LOCAL_PAYMENT_IN_FULL);
//										nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//									}
//								}
//							}else {
//								qualification.put("car", NONE);
//								nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//							}
//						}else if(fund >100000) {
//							if(content.contains("外地")&&!content.contains("不是外地")) {
//								qualification.put("car", OTHER);
//								nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//							}else {
//								if(content.contains("按揭")&&!(content.contains("不是按揭")||content.contains("没按揭"))) {
//									qualification.put("car", LOCAL_ANJIE);
//									nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//								}else {
//									qualification.put("car", LOCAL_PAYMENT_IN_FULL);
//									nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//								}
//							}
//						}else {
//							qualification.put("car", NONE);
//							nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//						}
//						result.setQualification(qualification);
//						result.setState(NodeResult.NODE_SUCCESS);
//					}else {
//						WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
//					}
//				}else if(content.contains("没")||content.contains("无")){
//					JSONObject qualification = new JSONObject();
//					qualification.put("car", NONE);
//					result.setQualification(qualification);
//					result.setState(NodeResult.NODE_SUCCESS);
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
//					JSONObject qualification = new JSONObject();
//					if(LOCAL_PAYMENT_IN_FULL.equals(jsonObject.getInteger("state")) ||OTHER.equals(jsonObject.getInteger("state"))) {
//						nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//					}else {
//						nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//					}
//					qualification.put("car", jsonObject.getInteger("state"));
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
		qualification.put("car", menu.getInteger("state"));
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
				} else if(fund>10000 && fund<500000){
					if(fund == 100000) {
						if(!(content.contains("不足")||content.contains("没"))) {
							if(content.contains("外地")&&!content.contains("不是外地")) {
								qualification.put("state", OTHER);
								qualification.put("choose", 1);
							}else {
								if(content.contains("按揭")&&!(content.contains("不是按揭")||content.contains("没按揭"))) {
									qualification.put("state", LOCAL_ANJIE);
									qualification.put("choose", 2);
								}else {
									qualification.put("state", LOCAL_PAYMENT_IN_FULL);
									qualification.put("choose", 1);
								}
							}
						}else {
							qualification.put("state", NONE);
							qualification.put("choose", 2);
						}
					}else if(fund >100000) {
						if(content.contains("外地")&&!content.contains("不是外地")) {
							qualification.put("state", OTHER);
							qualification.put("choose", 1);
						}else {
							if(content.contains("按揭")&&!(content.contains("不是按揭")||content.contains("没按揭"))) {
								qualification.put("state", LOCAL_ANJIE);
								qualification.put("choose", 2);
							}else {
								qualification.put("state", LOCAL_PAYMENT_IN_FULL);
								qualification.put("choose", 1);
							}
						}
					}else {
						qualification.put("state", NONE);
						qualification.put("choose", 2);
					}
				}else {
					WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
					return null;
				}
			}else if(content.contains("没")||content.contains("无")){
				qualification.put("state", NONE);
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
