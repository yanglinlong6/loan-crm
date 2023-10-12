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

import ch.qos.logback.classic.Logger;

public class HouseNode extends Node{
	
	private static final Integer LOCAL_COMMODITY_HOUSE = 1;
	
	private static final Integer OTHER_COMMODITY_HOUSE = 2;
	
	private static final Integer SELF_BUILD_HOUSE = 4;

	private static final Integer NONE_HOUSE = 3;
	
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
//						if(LOCAL_COMMODITY_HOUSE.intValue()==fund ||OTHER_COMMODITY_HOUSE.intValue() == fund) {
//							nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//							qualification.put("house", 1);
//						}else {
//							nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//							qualification.put("house", fund);
//						}
//						result.setQualification(qualification);
//						result.setState(NodeResult.NODE_SUCCESS);
//					}else {
//						WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
//					}
//				}else if(content.contains("商品")||content.contains("自建")||(content.contains("有")&&!content.contains("没有"))){
//					JSONObject qualification = new JSONObject();
//					if(content.contains("商品")&&!content.contains("不是商品")) {
//						if(content.contains("不是本地")||(content.contains("外地")&&!content.contains("不是外地"))) {
//							qualification.put("house", 2);
//						}else {
//							qualification.put("house", 1);
//						}
//						nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//					}else  {
//						qualification.put("house", SELF_BUILD_HOUSE);
//						nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//					}
//					result.setQualification(qualification);
//					result.setState(NodeResult.NODE_SUCCESS);
//				}else if(content.contains("没")||content.contains("无")){
//					JSONObject qualification = new JSONObject();
//					qualification.put("house", NONE_HOUSE);
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
//					if(LOCAL_COMMODITY_HOUSE.equals(jsonObject.getInteger("state"))||OTHER_COMMODITY_HOUSE.equals(jsonObject.getInteger("state"))) {
//						nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//					}else {
//						nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//					}
//					qualification.put("house", jsonObject.getInteger("state"));
//					result.setQualification(qualification);
//					result.setState(NodeResult.NODE_SUCCESS);
//				}
//			}
//		}
//		if(result.getState().equals(NodeResult.NODE_SUCCESS)) {
//			session.setHouse(result.getQualification().getInteger("house"));
//		}
//		return result;
//	}

	@Override
	public NodeResult parseQualification(JSONObject menu, NodeResult result) {
		JSONObject qualification = new JSONObject();
		qualification.put("house", menu.getInteger("state"));
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
			}else if(content.contains("商品")||content.contains("自建")||(content.contains("有")&&!content.contains("没有"))){
				if(content.contains("商品")&&!content.contains("不是商品")) {
					if(content.contains("不是本地")||(content.contains("外地")&&!content.contains("不是外地"))) {
						qualification.put("state", OTHER_COMMODITY_HOUSE);
					}else {
						qualification.put("state", LOCAL_COMMODITY_HOUSE);
					}
					qualification.put("choose", 1);
				}else  {
					qualification.put("state", SELF_BUILD_HOUSE);
					qualification.put("choose", 2);
				}
			}else if(content.contains("没")||content.contains("无")){
				qualification.put("state", NONE_HOUSE);
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
