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

public class InsuranceNode extends Node {

    private static final Integer INSURANCE_HALF_YEAR_MORE = 1;

    private static final Integer INSURANCE_HALF_YEAR_LESS = 2;

    private static final Integer INSURANCE_NONE = 3;

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
                    if(choose > 0 && choose < 4) {
                        JSONObject qualification = new JSONObject();
                        if(INSURANCE_HALF_YEAR_MORE.equals(choose)) {
                            qualification.put("insurance", choose);
                            nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                        } else {
                            qualification.put("insurance", choose);
                            nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
                        }
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
        } else {
            JSONArray jsonArray = super.getConfig().getQuestionMsg().getJSONArray("list");
            if(jsonArray == null) {
                return result;
            }
            for(int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.getString("menu_id").equals(menuid)) {
                    JSONObject qualification = new JSONObject();
                    if(INSURANCE_HALF_YEAR_MORE.equals(jsonObject.getInteger("state"))) {
                        nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                    } else {
                        nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
                    }
                    qualification.put("insurance", jsonObject.getInteger("state"));
                    result.setQualification(qualification);
                    result.setState(NodeResult.NODE_SUCCESS);
                }
            }
        }
        return result;
    }

}
