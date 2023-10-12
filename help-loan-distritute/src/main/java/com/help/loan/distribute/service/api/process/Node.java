package com.help.loan.distribute.service.api.process;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.WechatMsgFactory;
import com.help.loan.distribute.service.lender.LenderFilter;
import com.help.loan.distribute.service.lender.LenderMsgBuilder;
import com.help.loan.distribute.service.lender.model.LenderPO;
import com.help.loan.distribute.service.session.SessionServiceImpl;
import com.loan.cps.entity.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public abstract class Node {

    private static Log logger = LogFactory.getLog(SessionServiceImpl.class);

    protected NodeConfig config;

    protected NodeManager nodeManager;

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public void setNodeManager(NodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }

    public abstract NodeResult answerHandle(Session session, JSONObject userMsg);

    public NodeConfig getConfig() {
        return config;
    }

    public void setConfig(NodeConfig config) {
        this.config = config;
    }

    public void sendQuestion(Session session) {
        if(config.getNodeId().equals(NodeManager.NAME)) {
            JSONObject questionMsg = config.getQuestionMsg();
            JSONObject questionMsg2 = (JSONObject) questionMsg.clone();
            String time = "两个小时内";
            if(System.currentTimeMillis() / 3600000 % 24 > 10) {
                time = "12个小时后";
            }
            questionMsg2.put("head_content", String.format(questionMsg2.getString("head_content"), time));
            WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(questionMsg2, session.getUserId()), "", session.getDomain2());
            session.setNodeId(config.getNodeId());
        } else if(config.getNodeId().equals(NodeManager.WELCOME)) {
            String userInfo = WechatCenterUtil.getUserInfo(session.getUserId(), "", session.getDomain2());
            JSONObject parse = JSON.parseObject(userInfo);
            JSONObject clone = (JSONObject) config.getQuestionMsg().clone();
            clone.put("head_content", String.format(clone.getString("head_content"), parse.getString("nickname")));
            WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(clone, session.getUserId()), "", session.getDomain2());
        } else if(config.getNodeId().equals(NodeManager.FINISH3)) {
            List<LenderPO> filter = LenderFilter.filter(session.getSettlement(), session);
            if(filter.isEmpty()) {
                WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(config.getQuestionMsg(), session.getUserId()), "", session.getDomain2());
            } else {
                JSONObject clone = (JSONObject) config.getRecommendMsg().clone();
                clone.put("content", String.format(clone.getString("content"), LenderMsgBuilder.buildLenderMsg(filter, session.getUserId(), session.getDomain2())));
                WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(clone, session.getUserId()), "", session.getDomain2());
            }
            session.setNodeId(config.getNodeId());
        } else {
            WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(config.getQuestionMsg(), session.getUserId()), "", session.getDomain2());
            session.setNodeId(config.getNodeId());
        }
    }

    public void sendUrgeMsg(Session session) {
        String userInfo = WechatCenterUtil.getUserInfo(session.getUserId(), "", session.getDomain2());
        JSONObject parse = JSON.parseObject(userInfo);
        JSONObject clone = (JSONObject) config.getUrgeMsg().clone();
        clone.put("content", String.format(clone.getString("content"), parse.getString("nickname")));
        WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(clone, session.getUserId()), "", session.getDomain2());
//		if(!config.getNodeId().equals(NodeManager.FINISH5)) {
//			WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(config.getQuestionMsg(), session.getUserId()), "", session.getDomain2());
//		}
    }

}
