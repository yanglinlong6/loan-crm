package com.help.loan.distribute.service.api.process;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.AIUtil;
import com.help.loan.distribute.common.utils.NumberUtils;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.WechatMsgFactory;
import com.loan.cps.entity.Session;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class WelcomeNode extends Node {

    @Override
    public NodeResult answerHandle(Session session, JSONObject userMsg) {
        String menuid = userMsg.getString("bizmsgmenuid");
        NodeResult result = new NodeResult();
        result.setState(NodeResult.NODE_FAIL);
        if(StringUtils.isEmpty(menuid)) {
            String content = userMsg.getString("Content").trim();
            if(!StringUtils.isEmpty(content)) {
                if(Pattern.matches("^\\D*\\d+\\D*$", content) || Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", content)) {
                    int amount = NumberUtils.getIntegerByNumberStr(content.replaceAll("[^一二三四五六七八九十百千万亿|^0-9]", ""));
                    if(amount >= 3 && amount <= 1000) {
                        JSONObject qualification = new JSONObject();
                        qualification.put("loanAmount", amount * 10000);
                        String userInfo = WechatCenterUtil.getUserInfo(session.getUserId(), "", "");
                        JSONObject parse = JSON.parseObject(userInfo);
                        qualification.put("name", EmojiFilter.filterEmoji(parse.getString("nickname")));
                        result.setQualification(qualification);
                        result.setState(NodeResult.NODE_SUCCESS);
                        nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                    } else if(amount > 1000 && amount <= 10000000) {
                        JSONObject qualification = new JSONObject();
                        String userInfo = WechatCenterUtil.getUserInfo(session.getUserId(), "", "");
                        JSONObject parse = JSON.parseObject(userInfo);
                        qualification.put("name", EmojiFilter.filterEmoji(parse.getString("nickname")));
                        qualification.put("loanAmount", amount);
                        result.setQualification(qualification);
                        result.setState(NodeResult.NODE_SUCCESS);
                        nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                    }
//					else if(amount>0 && amount<5){
//						JSONArray jsonArray = super.getConfig().getQuestionMsg().getJSONArray("list");
//						for (int i = 0; i < jsonArray.size(); i++) {
//							JSONObject jsonObject = jsonArray.getJSONObject(i);
//							if (jsonObject.getInteger("state").equals(amount)) {
//								JSONObject qualification = new JSONObject();
//								String amo = jsonObject.getString("content").trim();
//								String[] split = amo.split(".");
//								qualification.put("loanAmount", split[1]);
//								result.setQualification(qualification);
//								result.setState(NodeResult.NODE_SUCCESS);
//								nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
//							}
//						}
//					}
                    else {
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
                    String content = jsonObject.getString("content").trim();
                    String[] split = content.split("\\.");
                    qualification.put("loanAmount", split[1]);
                    result.setQualification(qualification);
                    result.setState(NodeResult.NODE_SUCCESS);
                    nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                }
            }
        }
        return result;
    }

}
