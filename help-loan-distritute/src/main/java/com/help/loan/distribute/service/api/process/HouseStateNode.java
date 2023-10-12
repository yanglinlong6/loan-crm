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

public class HouseStateNode extends Node {

    private static final Integer PAYMENT_IN_FULL = 1;

    private static final Integer ANJIE = 2;

    private static final Integer MORTGAGE = 3;

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
                        if(PAYMENT_IN_FULL.intValue() == fund) {
                            if(session.getHouse().equals(1)) {
                                nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                            } else {
                                nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
                            }
                        } else if(ANJIE.intValue() == fund) {
                            nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                        } else {
//							if(session.getHouse().equals(1)) {
                            nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
//							}else {
//								nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//							}
                        }
                        qualification.put("houseState", fund);
                        result.setQualification(qualification);
                        result.setState(NodeResult.NODE_SUCCESS);
                    } else {
                        WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
                    }
                } else if(content.contains("全款") || content.contains("按揭") || content.contains("抵押")) {
                    JSONObject qualification = new JSONObject();
                    if(content.contains("抵押") && !(content.contains("没有抵押") || content.contains("没抵押"))) {
                        qualification.put("houseState", MORTGAGE);
//						if(session.getHouse().equals(1)) {
                        nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
//						}else {
//							nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//						}
                    } else if(content.contains("全款") && !(content.contains("没有全款") || content.contains("没全款"))) {
                        qualification.put("houseState", PAYMENT_IN_FULL);
                        if(session.getHouse().equals(1)) {
                            nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                        } else {
                            nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
                        }
                    } else {
                        qualification.put("houseState", ANJIE);
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
                    if(PAYMENT_IN_FULL.equals(jsonObject.getInteger("state"))) {
                        if(session.getHouse().equals(1)) {
                            nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                        } else {
                            nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
                        }
                    } else if(ANJIE.equals(jsonObject.getInteger("state"))) {
                        nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
                    } else {
//						if(session.getHouse().equals(1)) {
                        nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
//						}else {
//							nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
//						}
                    }
                    qualification.put("houseState", jsonObject.getInteger("state"));
                    result.setQualification(qualification);
                    result.setState(NodeResult.NODE_SUCCESS);
                }
            }
        }
        return result;
    }

}
