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

public class FixedAssetsNode extends Node{
	
	private static final Integer FIXED_ASSETS_HOUSE_CAR = 1;
	
	private static final Integer FIXED_ASSETS_HOUSE = 2;
	
	private static final Integer FIXED_ASSETS_CAR = 3;
	
	private static final Integer FIXED_ASSETS_NONE = 4;

	@Override
	public NodeResult answerHandle(Session session, JSONObject userMsg) {
		String menuid = userMsg.getString("bizmsgmenuid");
		NodeResult result = new NodeResult();
		result.setState(NodeResult.NODE_FAIL);
		if (StringUtils.isEmpty(menuid)) {
			String content = userMsg.getString("Content").trim();
			if (!StringUtils.isEmpty(content)) {
				if(Pattern.matches("^\\D*\\d+\\D*$", content)) {
					int	choose = NumberUtils.getIntegerByNumberStr(content.replaceAll("[^一二三四五六七八九十百千万亿|^0-9]", ""));
					if(choose>0&&choose<5) {
						JSONObject qualification = new JSONObject();
						int car = 3;
						int room = 3;
						if(FIXED_ASSETS_HOUSE_CAR.equals(choose)) {
							car = 1;
							room=1;
							nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
						}else if(FIXED_ASSETS_HOUSE.equals(choose)){
							room=1;
							nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
						}else if(FIXED_ASSETS_CAR.equals(choose)){
							car = 1;
							nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
						}else {
							nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
						}
						qualification.put("car", car);
						qualification.put("house", room);
						result.setQualification(qualification);
						result.setState(NodeResult.NODE_SUCCESS);
					}else {
						WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
					}
				}else if(content.contains("没")||content.contains("都没")||content.contains("有")||content.contains("有房")||content.contains("房")||content.contains("车")){
					JSONObject qualification = new JSONObject();
					int car = 3;
					int room = 3;
					if(!content.contains("没")&&(content.contains("有房")||content.contains("都有"))) {
						room = 1;
					}
					if(!content.contains("没")&&(content.contains("有车")||content.contains("都有"))) {
						car = 1;
					}
					if(!content.contains("没")&&content.contains("按揭房")) {
						room = 2;
					}
					if(!content.contains("没")&&content.contains("按揭车")) {
						car = 2;
					}
					if(car ==1 ||room==1) {
						nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
					}else {
						nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
					}
					qualification.put("car", car);
					qualification.put("house", room);
					result.setQualification(qualification);
					result.setState(NodeResult.NODE_SUCCESS);
				}else {
					AIUtil.answerAIMsg(content, userMsg,session);
					this.sendQuestion(session);
				}
			}
		}else {
			JSONArray jsonArray = super.getConfig().getQuestionMsg().getJSONArray("list");
			if(jsonArray==null) {
				return result;
			}
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if (jsonObject.getString("menu_id").equals(menuid)) {
					JSONObject qualification = new JSONObject();
					int car = 3;
					int room = 3;
					if(FIXED_ASSETS_HOUSE_CAR.equals(jsonObject.getInteger("state"))) {
						car = 1;
						room=1;
						nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
					}else if(FIXED_ASSETS_HOUSE.equals(jsonObject.getInteger("state"))){
						room=1;
						nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
					}else if(FIXED_ASSETS_CAR.equals(jsonObject.getInteger("state"))){
						car = 1;
						nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
					}else {
						nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
					}
					qualification.put("car", car);
					qualification.put("house", room);
					result.setQualification(qualification);
					result.setState(NodeResult.NODE_SUCCESS);
				}
			}
		}
		return result;
	}

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
