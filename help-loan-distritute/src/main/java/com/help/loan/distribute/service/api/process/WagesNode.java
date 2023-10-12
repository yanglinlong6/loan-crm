package com.help.loan.distribute.service.api.process;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.AIUtil;
import com.help.loan.distribute.common.utils.NumberUtils;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.WechatMsgFactory;
import com.loan.cps.entity.Session;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class WagesNode extends Node {

    private static final Integer WAGE_MORE_5000 = 1;

    private static final Integer WAGE_LESS_5000 = 2;

    private static final Integer WAGE_NONE = 3;

    @Override
    public NodeResult answerHandle(Session session, JSONObject userMsg) {
        String menuid = userMsg.getString("bizmsgmenuid");
        NodeResult result = new NodeResult();
        result.setState(NodeResult.NODE_FAIL);
        if(StringUtils.isEmpty(menuid)) {
            String content = userMsg.getString("Content").trim();
            if(!StringUtils.isEmpty(content)) {
                if(Pattern.matches("^\\D*\\d+\\D*$", content) || Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", content)) {
                    int fund = NumberUtils.getIntegerByNumberStr(content.replaceAll("[^一二三四五六七八九十百千万亿|^0-9]", ""));
                    if(fund > 0 && fund < 4) {
                        JSONObject qualification = new JSONObject();
                        if(WAGE_MORE_5000.intValue() == fund) {
                            nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                        } else {
                            nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
                        }
                        qualification.put("getwayIncome", fund);
                        result.setQualification(qualification);
                        result.setState(NodeResult.NODE_SUCCESS);
                    } else if(fund > 2000 && fund < 100000) {
                        JSONObject qualification = new JSONObject();
                        if(fund > 2000 && fund < 5000) {
                            qualification.put("getwayIncome", WAGE_LESS_5000);
                            nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
                        } else if(fund == 5000) {
                            if(content.contains("没") || content.contains("不")) {
                                qualification.put("getwayIncome", WAGE_LESS_5000);
                                nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
                            } else {
                                qualification.put("getwayIncome", WAGE_MORE_5000);
                                nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                            }
                        } else {
                            qualification.put("getwayIncome", WAGE_MORE_5000);
                            nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                        }
                        result.setQualification(qualification);
                        result.setState(NodeResult.NODE_SUCCESS);
                    } else {
                        WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
                    }
                } else if(content.contains("没") || content.contains("无")) {
                    JSONObject qualification = new JSONObject();
                    qualification.put("getwayIncome", "3");
                    result.setQualification(qualification);
                    result.setState(NodeResult.NODE_SUCCESS);
                    nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
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
                    qualification.put("getwayIncome", jsonObject.getInteger("state"));
                    if(WAGE_MORE_5000.equals(jsonObject.getInteger("state"))) {
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
