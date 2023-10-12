package com.help.loan.distribute.service.api.process;


import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.AIUtil;
import com.loan.cps.entity.Session;
import org.springframework.util.StringUtils;

public class Finish5Node extends Node {

    @Override
    public NodeResult answerHandle(Session session, JSONObject userMsg) {
        String menuid = userMsg.getString("bizmsgmenuid");
        NodeResult result = new NodeResult();
        result.setState(NodeResult.NODE_FAIL);
        if(StringUtils.isEmpty(menuid)) {
            String content = userMsg.getString("Content").trim();
            if(!StringUtils.isEmpty(content)) {
                AIUtil.answerAIMsg(content, userMsg, session);
            }
        }
        return result;

    }

}
