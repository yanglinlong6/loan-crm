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

public class CardNode extends Node{
	
	public static final Integer CARD_STATE_HAD_SIX= 1 ;
	
	public static final Integer CARD_STATE_HAD_NO_SIX= 2 ;
	
	public static final Integer CARD_STATE_NO_CARD= 3 ;

//	@Override
//	public NodeResult answerHandle(Session session, JSONObject userMsg) {
//		String menuid = userMsg.getString("bizmsgmenuid");
//		NodeResult result = new NodeResult();
//		result.setState(NodeResult.NODE_FAIL);
//		if(StringUtils.isEmpty(menuid)) {
//			String content = userMsg.getString("Content").trim();;
//			if(!StringUtils.isEmpty(content)) {
//				if(content.contains("是的")||content.contains("有的")||content.contains("1")||content.contains("2")||content.contains("刚办")||content.contains("最近办")||content.contains("没")||content.contains("3")) {
//					JSONObject qualification = new JSONObject();
//					qualification.put("creditCard", CARD_STATE_HAD_SIX);
//					if(content.contains("2")||content.contains("刚办")||content.contains("最近办")) {
//						qualification.put("creditCard", CARD_STATE_HAD_NO_SIX);
//					}else if(content.contains("3")||content.contains("没")) {
//						qualification.put("creditCard", CARD_STATE_NO_CARD);
//					}else {
//						
//					}
//					result.setQualification(qualification);
//					result.setState(NodeResult.NODE_SUCCESS);
//					nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//				}else {
//					AIUtil.answerAIMsg(content, userMsg,session);
//				}
//			}
//		}else {
//			JSONArray jsonArray = super.getConfig().getQuestionMsg().getJSONArray("list");
//			if(jsonArray==null) {
//				return result;
//			}
//			for(int i=0;i<jsonArray.size();i++) {
//				JSONObject jsonObject = jsonArray.getJSONObject(i);
//				if(jsonObject.getString("menu_id").equals(menuid)) {
//					JSONObject qualification = new JSONObject();
//					qualification.put("creditCard", jsonObject.getInteger("state"));
//					result.setQualification(qualification);
//					result.setState(NodeResult.NODE_SUCCESS);
//					nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//				}
//			}
//		}
//		return result;
//	}

	@Override
	public NodeResult parseQualification(JSONObject menu, NodeResult result) {
		JSONObject qualification = new JSONObject();
		qualification.put("creditCard", menu.getInteger("state"));
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
				if(fund>0 && fund <4) {
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
			}else if(content.contains("是的")||content.contains("有的")||content.contains("刚办")||content.contains("最近办")||content.contains("没")||content.contains("无")){
				if((content.contains("没")||content.contains("无"))&&!(content.contains("没有六个月")||content.contains("不足六个月")||content.contains("不够六个月"))) {
					qualification.put("state", CARD_STATE_NO_CARD);
				}else if(content.contains("刚办")||content.contains("最近办")||content.contains("没有六个月")||content.contains("不足六个月")||content.contains("不够六个月")) {
					qualification.put("state", CARD_STATE_HAD_NO_SIX);
				}else {
					qualification.put("state", CARD_STATE_HAD_SIX);
				}
				qualification.put("choose", 1);
			}else {
				AIUtil.answerAIMsg(content, userMsg,session);
				return null;
			}
		}
		return qualification;
	}

}
