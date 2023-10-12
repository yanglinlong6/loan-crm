package com.help.loan.distribute.service.api.process;


import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.AIUtil;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.WechatMsgFactory;
import com.help.loan.distribute.service.lender.LenderFilter;
import com.help.loan.distribute.service.lender.LenderMsgBuilder;
import com.help.loan.distribute.service.lender.model.LenderPO;
import com.loan.cps.entity.Session;

import java.util.List;

public class Finish3Node extends Node {

    @Override
    public NodeResult answerHandle(Session session, JSONObject userMsg) {
        NodeResult result = new NodeResult();
        result.setState(NodeResult.NODE_FAIL);
        String content = userMsg.getString("Content").trim();
        if(content.contains("换")) {
            List<LenderPO> filter = LenderFilter.filter(session.getSettlement(), session);
            if(filter.isEmpty()) {
                WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(config.getErrMsg(), session.getUserId()), "", session.getDomain2());
            } else {
                JSONObject clone = (JSONObject) config.getRecommendMsg().clone();
                clone.put("content", String.format(clone.getString("content"), LenderMsgBuilder.buildLenderMsg(filter, session.getUserId(), session.getDomain2())));
                WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(clone, session.getUserId()), "", session.getDomain2());
            }
        } else {
            AIUtil.answerAIMsg(content, userMsg, session);
        }
        return result;
    }

}
