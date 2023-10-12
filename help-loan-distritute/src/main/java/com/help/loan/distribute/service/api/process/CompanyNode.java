package com.help.loan.distribute.service.api.process;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.AIUtil;
import com.help.loan.distribute.common.utils.NumberUtils;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.WechatMsgFactory;
import com.loan.cps.entity.Session;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class CompanyNode extends Node {

    private static final Integer COMPANY_OPERATOR = 1;

    private static final Integer COMPANY_NONE = 2;

    @Override
    public NodeResult answerHandle(Session session, JSONObject userMsg) {
        String menuid = userMsg.getString("bizmsgmenuid");
        NodeResult result = new NodeResult();
        result.setState(NodeResult.NODE_FAIL);
        if(StringUtils.isEmpty(menuid)) {
            String content = userMsg.getString("Content").trim();
            if(!StringUtils.isEmpty(content)) {
                if(Pattern.matches("^\\D*\\d+\\D*$", content)) {
                    int choose = NumberUtils.getIntegerByNumberStr(content.replaceAll("[^一二三四五六七八九十百千万亿|^0-9]", ""));
                    if(choose > 0 && choose < 2) {
                        JSONObject qualification = new JSONObject();
                        if(COMPANY_OPERATOR.equals(choose)) {
                            qualification.put("company", 1);
                            nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                        } else {
                            qualification.put("company", 2);
                            nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
                        }
                        result.setQualification(qualification);
                        result.setState(NodeResult.NODE_SUCCESS);
                    } else {
                        WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
                    }
                } else if(content.contains("没") || content.contains("有") || content.contains("无")) {
                    JSONObject qualification = new JSONObject();
                    if(content.contains("没") || content.contains("无")) {
                        qualification.put("company", 1);
                        nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
                    } else {
                        qualification.put("company", 2);
                        nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                    }
                    result.setQualification(qualification);
                    result.setState(NodeResult.NODE_SUCCESS);
                } else {
                    AIUtil.answerAIMsg(content, userMsg, session);
                    this.sendQuestion(session);
                }
            }
        } else {
            JSONArray jsonArray = super.getConfig().getQuestionMsg().getJSONArray("list");
            if(jsonArray == null) {
                return result;
            }
            for(int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.getString("menu_id").equals(menuid)) {
                    JSONObject qualification = new JSONObject();
                    qualification.put("company", jsonObject.getInteger("state"));
                    if(COMPANY_OPERATOR.equals(jsonObject.getInteger("state"))) {
                        nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                    } else {
                        nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
                    }
                    result.setQualification(qualification);
                    result.setState(NodeResult.NODE_SUCCESS);
                }
            }
        }
        return result;
    }

}
