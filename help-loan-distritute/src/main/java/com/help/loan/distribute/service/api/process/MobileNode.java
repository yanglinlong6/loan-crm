package com.help.loan.distribute.service.api.process;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.AIUtil;
import com.help.loan.distribute.common.utils.MobileLocationUtil;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.WechatMsgFactory;
import com.loan.cps.entity.Session;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class MobileNode extends Node {

    @Override
    public NodeResult answerHandle(Session session, JSONObject userMsg) {
        String menuid = userMsg.getString("bizmsgmenuid");
        NodeResult result = new NodeResult();
        result.setState(NodeResult.NODE_FAIL);
        if(StringUtils.isEmpty(menuid)) {
            String content = userMsg.getString("Content").trim();
            if(!StringUtils.isEmpty(content)) {
                if(Pattern.matches("^\\D*\\d{8,16}\\D*$", content)) {
                    String replaceAll = content.replaceAll("[^0-9]", "");
                    if(checkMoblie(replaceAll)) {
                        JSONObject qualification = new JSONObject();
                        String city = MobileLocationUtil.getCity(replaceAll);
                        if(org.apache.commons.lang3.StringUtils.isBlank(city)) {
                            nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
                        } else {
                            session.setCity(city);
                            Node node = nodeManager.getNode(super.getConfig().getRightNodeId());
                            JSONObject questionMsg = (JSONObject) node.config.getQuestionMsg().clone();
                            questionMsg.put("head_content", String.format(questionMsg.getString("head_content"), city));
                            WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(questionMsg, session.getUserId()), "", session.getDomain2());
                            session.setNodeId(node.config.getNodeId());
                        }
                        qualification.put("mobile", replaceAll);
                        result.setQualification(qualification);
                        result.setState(NodeResult.NODE_SUCCESS);
                    } else {
                        WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
                    }
                } else {
                    AIUtil.answerAIMsg(content, userMsg, session);
                    this.sendQuestion(session);
                }
            }
        }
        return result;
    }

    public boolean checkMoblie(String mobile) {
        String regex = "^((13[0-9])|(14[0-9])|(16[0-9])|(15[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$";
        boolean m = false;
        if(mobile.length() == 11) {
            m = Pattern.matches(regex, mobile);
        }
        return m;
    }

}
