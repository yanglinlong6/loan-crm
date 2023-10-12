package com.loan.cps.process;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.AIUtil;
import com.loan.cps.common.EmojiFilter;
import com.loan.cps.common.NumberUtils;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.common.WechatMsgFactory;
import com.loan.cps.entity.Session;

public class DFWelcomeNode extends Node{
	
	private static final Integer QUOTA_50000_LESS = 1;
	
	private static final Integer QUOTA_50000_MORE = 2;

//	@Override
//	public NodeResult answerHandle(Session session, JSONObject userMsg) {
//		String menuid = userMsg.getString("bizmsgmenuid");
//		NodeResult result = new NodeResult();
//		result.setState(NodeResult.NODE_FAIL);
//		if(StringUtils.isEmpty(menuid)) {
//			String content = userMsg.getString("Content").trim();
//			if(!StringUtils.isEmpty(content)) {
//				if(Pattern.matches("^\\D*\\d+\\D*$", content)||Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", content)) {
//					int	amount = NumberUtils.getIntegerByNumberStr(content.replaceAll("[^一二三四五六七八九十百千万亿|^0-9]", ""));
//					if(amount>=3 && amount <=1000) {
//						JSONObject qualification = new JSONObject();
//						if(amount>=5) {
//							nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//						}else {
//							nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//						}
//						qualification.put("loanAmount", amount*10000);
//						String userInfo = WXCenterUtil.getUserInfo(session.getUserId(),"",  "");
//						JSONObject parse = JSON.parseObject(userInfo);
//						qualification.put("name", EmojiFilter.filterEmoji(parse.getString("nickname")));
//						result.setQualification(qualification);
//						result.setState(NodeResult.NODE_SUCCESS);
//					}else if(amount >1000 &&amount<=10000000){
//						JSONObject qualification = new JSONObject();
//						if(amount>=50000) {
//							nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//						}else {
//							nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//						}
//						String userInfo = WXCenterUtil.getUserInfo(session.getUserId(),"",  "");
//						JSONObject parse = JSON.parseObject(userInfo);
//						qualification.put("name", EmojiFilter.filterEmoji(parse.getString("nickname")));
//						qualification.put("loanAmount", amount);
//						result.setQualification(qualification);
//						result.setState(NodeResult.NODE_SUCCESS);
//					}
//					else if(amount>0 && amount<3){
//						JSONArray jsonArray = super.getConfig().getQuestionMsg().getJSONArray("list");
//						for (int i = 0; i < jsonArray.size(); i++) {
//							JSONObject jsonObject = jsonArray.getJSONObject(i);
//							if (jsonObject.getInteger("state").equals(amount)) {
//								if(QUOTA_50000_MORE.equals(jsonObject.getInteger("state"))) {
//									nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//								}else {
//									nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//								}
//								JSONObject qualification = new JSONObject();
//								String amo = jsonObject.getString("content").trim();
//								String[] split = amo.split(".");
//								qualification.put("loanAmount", split[1]);
//								result.setQualification(qualification);
//								result.setState(NodeResult.NODE_SUCCESS);
//							}
//						}
//					}
//					else {
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
//					if(QUOTA_50000_MORE.equals(jsonObject.getInteger("state"))) {
//						nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//					}else {
//						nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//					}
//					JSONObject qualification = new JSONObject();
//					String content = jsonObject.getString("content").trim();
//					String[] split = content.split("\\.");
//					qualification.put("loanAmount", split[1]);
//					result.setQualification(qualification);
//					result.setState(NodeResult.NODE_SUCCESS);
//				}
//			}
//		}
//		return result;
//	}

	@Override
	public NodeResult parseQualification(JSONObject menu, NodeResult result) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject parseMsg(Session session, JSONObject userMsg) {
		// TODO Auto-generated method stub
		return null;
	}

}
