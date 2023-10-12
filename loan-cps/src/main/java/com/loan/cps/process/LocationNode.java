package com.loan.cps.process;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.loan.cps.common.AIUtil;
import com.loan.cps.common.CityUtil;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.common.WechatMsgFactory;
import com.loan.cps.entity.Session;

public class LocationNode extends Node{

	private static final Logger LOG = LoggerFactory.getLogger(MobileLocationNode.class);

//	@Override
//	public NodeResult answerHandle(Session session, JSONObject userMsg) {
//		LOG.info("LocationNode-->{}",userMsg.toJSONString());
//		String menuid = userMsg.getString("bizmsgmenuid");
//		NodeResult result = new NodeResult();
//		result.setState(NodeResult.NODE_FAIL);
//		if (StringUtils.isEmpty(menuid)) {
//			String content = userMsg.getString("Content").trim();
//			if (!StringUtils.isEmpty(content)) {
//				if (content.length()>=2&&content.length()<12) {
//					JiebaSegmenter segmenter = new JiebaSegmenter();
//					JSONObject byName = CityUtil.getByName(segmenter.sentenceProcess(content), 2);
//					if(byName!=null && !StringUtils.isEmpty(byName.getString("name"))) {
//						JSONObject qualification = new JSONObject();
//						qualification.put("city", byName.getString("name"));
//						qualification.put("level", 2);
//						result.setQualification(qualification);
//						result.setState(NodeResult.NODE_SUCCESS);
//						nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//					}else {
//						WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
//					}
//				} else {
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
		qualification.put("city", menu.getString("city"));
		qualification.put("level", 4);
		result.setQualification(qualification);
		result.setState(NodeResult.NODE_SUCCESS);
		return result;
	}

	@Override
	public JSONObject parseMsg(Session session, JSONObject userMsg) {
		String content = userMsg.getString("Content").trim();
		JSONObject qualification = new JSONObject();
		if (!StringUtils.isEmpty(content)) {
			if (content.length()>=2&&content.length()<50) {
				JiebaSegmenter segmenter = new JiebaSegmenter();
				JSONObject byName = CityUtil.getByName(segmenter.sentenceProcess(content), 2);
				if(byName!=null && !StringUtils.isEmpty(byName.getString("name"))) {
					qualification.put("city", byName.getString("name"));
					qualification.put("choose", 1);
				}else {
					WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
					return null;
				}
			} else {
				AIUtil.answerAIMsg(content, userMsg,session);
				this.sendQuestion(session);
				return null;
			}
		}
		return qualification;
	}
	

}
