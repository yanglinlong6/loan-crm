package com.loan.cps.process;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.AIUtil;
import com.loan.cps.common.NumberUtils;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.common.WechatMsgFactory;
import com.loan.cps.entity.Session;

public class ZhimaNode extends Node{

	/**
	 *
	 */
//	@Override
//	public NodeResult answerHandle(Session session, JSONObject userMsg) {
//		String menuid = userMsg.getString("bizmsgmenuid");
//		NodeResult result = new NodeResult();
//		result.setState(NodeResult.NODE_FAIL);
//		if(StringUtils.isEmpty(menuid)) {
//			String content = userMsg.getString("Content").trim();;
//			if(!StringUtils.isEmpty(content)) {
//				if(Pattern.matches("^\\D*\\d+\\D*$", content)||Pattern.matches("^.*[一二三四五六七八九十百千万亿].*+$", content)) {
//					int zhima = -1;
//					if(Pattern.matches("^[一二三四五六七八九十百千万亿]+$", content)) {
//						zhima = NumberUtils.getIntegerByNumberStr(content.replaceAll("[^一二三四五六七八九十百千万亿]", ""));
//					}else {
//						zhima = Integer.valueOf(content.replaceAll("[^0-9]", ""));
//					}
//					if(zhima>=350 && zhima <=950) {
//						JSONObject qualification = new JSONObject();
//						qualification.put("zhima", 1);
//						qualification.put("zhimaScore", zhima);
//						if (zhima >= 650) {
//							session.setSettlement(1);
//						} else if(zhima >= 550 && zhima <650) {
//							session.setSettlement(-1);
//						}else{
//							session.setSettlement(-2);
//						}
//						qualification.put("level", 3);
//						nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//						result.setQualification(qualification);
//						result.setState(NodeResult.NODE_SUCCESS);
//					}else if(zhima == 0){
//						JSONObject qualification = new JSONObject();
//						qualification.put("zhima", 2);
//						qualification.put("level", 3);
//						session.setSettlement(-2);
//						nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//						result.setQualification(qualification);
//						result.setState(NodeResult.NODE_SUCCESS);
//					}else {
//						WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
//					}
//				}else if(content.contains("没")||content.contains("无")){
//					JSONObject qualification = new JSONObject();
//					qualification.put("zhima", 2);
//					qualification.put("level", 3);
//					session.setSettlement(-2);
//					nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//					result.setQualification(qualification);
//					result.setState(NodeResult.NODE_SUCCESS);
//				}else {
//					AIUtil.answerAIMsg(content, userMsg,session);
//					this.sendQuestion(session);
//				}
//			}
//		}
//		return result;
//	}

	@Override
	public NodeResult parseQualification(JSONObject menu, NodeResult result) {
		JSONObject qualification = new JSONObject();
		qualification.put("zhima", menu.getInteger("zhima"));
		if(menu.getInteger("zhimaScore")!=null) {
			qualification.put("zhimaScore", menu.getInteger("zhimaScore"));
		}
		qualification.put("level", 3);
		result.setQualification(qualification);
		result.setState(NodeResult.NODE_SUCCESS);
		return result;
	}

	@Override
	public JSONObject parseMsg(Session session, JSONObject userMsg) {
		String content = userMsg.getString("Content").trim();
		JSONObject qualification = new JSONObject();
		if(!StringUtils.isEmpty(content)) {
			if(Pattern.matches("^\\D*\\d+\\D*$", content)||Pattern.matches("^.*[一二三四五六七八九十百千万亿].*+$", content)) {
				int zhima = -1;
				if(Pattern.matches("^[一二三四五六七八九十百千万亿]+$", content)) {
					zhima = NumberUtils.getIntegerByNumberStr(content.replaceAll("[^一二三四五六七八九十百千万亿]", ""));
				}else {
					zhima = Integer.valueOf(content.replaceAll("[^0-9]", ""));
				}
				if(zhima>=350 && zhima <=950) {
					qualification.put("zhima", 1);
					qualification.put("zhimaScore", zhima);
					if (zhima >= 650) {
						session.setSettlement(1);
					} else if(zhima >= 550 && zhima <650) {
						session.setSettlement(-1);
					}else{
						session.setSettlement(-2);
					}
					qualification.put("choose", 2);
				}else if(zhima == 0){
					qualification.put("zhima", 2);
					qualification.put("choose", 2);
					session.setSettlement(-2);
				}else {
					WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
					return null;
				}
			}else if(content.contains("没")||content.contains("无")){
				qualification.put("zhima", 2);
				qualification.put("choose", 2);
				session.setSettlement(-2);
			}else {
				AIUtil.answerAIMsg(content, userMsg,session);
				this.sendQuestion(session);
				return null;
			}
		}
		return qualification;
	}
	
}
