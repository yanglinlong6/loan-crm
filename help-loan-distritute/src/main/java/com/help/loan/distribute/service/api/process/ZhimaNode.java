package com.help.loan.distribute.service.api.process;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.AIUtil;
import com.help.loan.distribute.common.utils.NumberUtils;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.WechatMsgFactory;
import com.loan.cps.entity.Session;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class ZhimaNode extends Node {

    /**
     *
     */
    @Override
    public NodeResult answerHandle(Session session, JSONObject userMsg) {
        String menuid = userMsg.getString("bizmsgmenuid");
        NodeResult result = new NodeResult();
        result.setState(NodeResult.NODE_FAIL);
        if(StringUtils.isEmpty(menuid)) {
            String content = userMsg.getString("Content").trim();
            ;
            if(!StringUtils.isEmpty(content)) {
//				String wechat = WXCenterUtil.getWechat("", "", session.getUserId());
//				JSONObject parse = JSON.parseObject(wechat);
//				Integer wxType = 7;
//				if("0".equals(parse.getString("code"))) {
//					wxType = parse.getJSONObject("o").getInteger("wxType");
//				}
                if(Pattern.matches("^\\D*\\d+\\D*$", content) || Pattern.matches("^.*[一二三四五六七八九十百千万亿].*+$", content)) {
                    int zhima = -1;
                    if(Pattern.matches("^[一二三四五六七八九十百千万亿]+$", content)) {
                        zhima = NumberUtils.getIntegerByNumberStr(content.replaceAll("[^一二三四五六七八九十百千万亿]", ""));
                    } else {
                        zhima = Integer.valueOf(content.replaceAll("[^0-9]", ""));
                    }
                    if(zhima >= 350 && zhima <= 950) {
                        JSONObject qualification = new JSONObject();
                        qualification.put("zhima", 1);
                        qualification.put("zhimaScore", zhima);
                        if(zhima >= 650) {
                            session.setSettlement(1);
                        } else if(zhima >= 550 && zhima < 650) {
                            session.setSettlement(-1);
                        } else {
                            session.setSettlement(-2);
                        }
                        qualification.put("level", 3);
                        nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
                        result.setQualification(qualification);
                        result.setState(NodeResult.NODE_SUCCESS);
                    } else if(zhima == 0) {
                        JSONObject qualification = new JSONObject();
                        qualification.put("zhima", 2);
                        qualification.put("level", 3);
                        session.setSettlement(-2);
                        nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
                        result.setQualification(qualification);
                        result.setState(NodeResult.NODE_SUCCESS);
                    } else {
                        WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
                    }
                } else if(content.contains("没") || content.contains("无")) {
                    JSONObject qualification = new JSONObject();
                    qualification.put("zhima", 2);
                    qualification.put("level", 3);
                    session.setSettlement(-2);
                    nodeManager.getNode(super.getConfig().getLeftNodeId()).sendQuestion(session);
                    result.setQualification(qualification);
                    result.setState(NodeResult.NODE_SUCCESS);
                } else {
                    AIUtil.answerAIMsg(content, userMsg, session);
                    this.sendQuestion(session);
                }
            }
        }
        return result;
    }

}
