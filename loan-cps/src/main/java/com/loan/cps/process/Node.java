package com.loan.cps.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.LenderCache;
import com.loan.cps.common.LenderFilter;
import com.loan.cps.common.LenderMsgBuilder;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.common.WechatMsgFactory;
import com.loan.cps.entity.LenderPO;
import com.loan.cps.entity.Session;


public abstract class Node {
	
	protected static Log logger = LogFactory.getLog(Node.class);
	
	protected NodeConfig config;
	
	protected NodeManager nodeManager;
	
	public NodeManager getNodeManager() {
		return nodeManager;
	}

	public void setNodeManager(NodeManager nodeManager) {
		this.nodeManager = nodeManager;
	}

	public NodeResult answerHandle(Session session,JSONObject userMsg) {
		String menuid = userMsg.getString("bizmsgmenuid");
		NodeResult result = new NodeResult();
		result.setState(NodeResult.NODE_FAIL);
		JSONObject menu = null;
		if(StringUtils.isEmpty(menuid)||(session.getNodeId().equals(NodeManager.FINISH3)&&userMsg.getString("Content").trim().contains("换"))) {
			menu = parseMsg(session,userMsg);
		}else {
			menu = getMsgMenu(menuid);
			if(menu==null) {
				result = NodeManager.paseMsgMenu(menuid, result,session);
			}else {
				menu.put("city", session.getCity());
				menu.put("userId", session.getUserId());
			}
		}
		if(menu!=null) {
			result = parseQualification(menu,result);
			if(result.getState().equals(NodeResult.NODE_SUCCESS)&&result.getQualification()!=null&&!StringUtils.isEmpty(result.getQualification().getString("name"))) {
				session.setHouse(999);
			}
			chooseNode(menu,session);
		}
		return result;
	}
	
	public void chooseNode(JSONObject menu,Session session) {
		if(menu.getIntValue("choose")==1) {
			nodeManager.getNode(this.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
		}else if(menu.getIntValue("choose")==2) {
			nodeManager.getNode(this.getConfig().getLeftNodeId(session.getLocation())).sendQuestion(session);
		}
	}
	
	public NodeConfig getConfig() {
		return config;
	}

	public void setConfig(NodeConfig config) {
		this.config = config;
	}
	
	public void sendQuestion(Session session) {
		Integer nodeid = config.getNodeId();
		if(config.getNodeId().equals(NodeManager.NAME)) {
			JSONObject questionMsg = config.getQuestionMsg();
			JSONObject questionMsg2 = (JSONObject) questionMsg.clone();
			String time = "两个小时内";
			if(System.currentTimeMillis()/3600000%24>10) {
				time = "12个小时后";
			}
			questionMsg2.put("head_content", String.format(questionMsg2.getString("head_content"), time));
			WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(questionMsg2, session.getUserId()), "", session.getDomain2());
		} else if(config.getNodeId().equals(NodeManager.WELCOME)||config.getNodeId().equals(NodeManager.DF_WELCOME)){
			String userInfo = WXCenterUtil.getUserInfo(session.getUserId(),"",  session.getDomain2());
			JSONObject parse = JSON.parseObject(userInfo);
			JSONObject clone = (JSONObject) config.getQuestionMsg().clone();
			clone.put("head_content", String.format(clone.getString("head_content"), StringUtils.isEmpty(parse.getString("nickname"))?"亲":parse.getString("nickname")));
			WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(clone, session.getUserId()), "", session.getDomain2());
		}else if(config.getNodeId().equals(NodeManager.FINISH3)) {
			if(session.getSettlement()==null) {
				session.setSettlement(1);
			}
			List<LenderPO> filter = LenderFilter.filter(session.getSettlement(), session,LenderCache.getCacheList(),3);
			if(filter.isEmpty()) {
				WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(config.getQuestionMsg(), session.getUserId()), "", session.getDomain2());
			}else {
				JSONObject clone = (JSONObject) config.getRecommendMsg().clone();
				clone.put("content", String.format(clone.getString("content"),LenderMsgBuilder.buildLenderMsg(filter, session.getUserId(), session.getDomain2(),LenderMsgBuilder.FINISH)));
				WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(clone, session.getUserId()), "", session.getDomain2());
			}
		}else if(config.getNodeId().equals(NodeManager.FINISH4)) {
			LenderPO filter = LenderCache.getDfLender();
			List<LenderPO> recList = new ArrayList<LenderPO>();
			recList.add(filter);
			JSONObject clone = (JSONObject) config.getRecommendMsg().clone();
			logger.info("send df "+JSON.toJSONString(filter)+" content = "+clone.getString("content"));
			clone.put("content", String.format(clone.getString("content"),
					LenderMsgBuilder.buildLenderMsg2(recList, session.getUserId(), session.getDomain2(),LenderMsgBuilder.FINISH)));
			WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(clone, session.getUserId()), "",
					session.getDomain2());
		}else if(config.getNodeId().equals(NodeManager.MOBILE_LOCATION)) {
			JSONObject questionMsg = (JSONObject) config.getQuestionMsg().clone();
			questionMsg.put("head_content", String.format(questionMsg.getString("head_content"), session.getCity()));
			WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(questionMsg, session.getUserId()), "", session.getDomain2());
		}else if(config.getNodeId().equals(NodeManager.FINISH5)) {
			WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(config.getQuestionMsg(), session.getUserId()), "", session.getDomain2());
			session.setFinish(1);
			if(session.getLocation()!=null) {
				Integer leftNodeId2 = NodeManager.getNode(session.getLocation()).getConfig().getLeftNodeId();
				if(NodeManager.MOBILE.equals(NodeManager.getNode(leftNodeId2).getConfig().getRightNodeId())) {
					NodeManager.getNode(NodeManager.FINISH).sendQuestion(session);
					Integer leftNodeId = NodeManager.getNode(session.getLocation()).getConfig().getLeftNodeId();
					NodeManager.getNode(leftNodeId).sendQuestion(session);
					nodeid = leftNodeId;
				}
			}
		}else if(NodeManager.SURNAME.equals(config.getNodeId())) {
			if(session.getHouse()!=null&&session.getHouse().equals(999)) {
				NodeManager.getNode(config.getRightNodeId()).sendQuestion(session);
				nodeid = config.getRightNodeId();
			}else {
				WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(config.getQuestionMsg(), session.getUserId()), "", session.getDomain2());
			}
		}
		else {
			WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(config.getQuestionMsg(), session.getUserId()), "", session.getDomain2());
		}
		if(NodeManager.MOBILE.equals(config.getNodeId())&&!NodeManager.MOBILE.equals(session.getNodeId())) {
			session.setLocation(session.getNodeId());
		}
		session.setNodeId(nodeid);
		session.setDown(System.currentTimeMillis());
	}
	
	public void sendUrgeMsg(Session session) {
		String userInfo = WXCenterUtil.getUserInfo(session.getUserId(),"",  session.getDomain2());
		JSONObject parse = JSON.parseObject(userInfo);
		JSONObject clone = (JSONObject) config.getUrgeMsg().clone();
		clone.put("content", String.format(clone.getString("content"), parse.getString("nickname")));
		WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(clone,session.getUserId()), "",  session.getDomain2());
//		if(!config.getNodeId().equals(NodeManager.FINISH5)) {
//			WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(config.getQuestionMsg(), session.getUserId()), "", session.getDomain2());
//		}
	}
	
	public JSONObject getMsgMenu(String menuid) {
		JSONObject questionMsg = this.getConfig().getQuestionMsg();
		if(questionMsg == null) {
			return null;
		}
		JSONArray jsonArray = questionMsg.getJSONArray("list");
		if(jsonArray==null) {
			return null;
		}
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if (jsonObject.getString("menu_id").equals(menuid)) {
				return (JSONObject) jsonObject.clone();
			}
		}
		return null;
	}
	
	public abstract NodeResult parseQualification(JSONObject menu,NodeResult result) ;
	
	public abstract JSONObject parseMsg(Session session, JSONObject userMsg) ;
	
}
