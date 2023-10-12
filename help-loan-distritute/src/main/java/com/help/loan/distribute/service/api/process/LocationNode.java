package com.help.loan.distribute.service.api.process;


import com.alibaba.fastjson.JSONObject;
import com.loan.cps.entity.Session;
public class LocationNode extends Node {

    @Override
    public NodeResult answerHandle(Session session, JSONObject userMsg) {
//        String menuid = userMsg.getString("bizmsgmenuid");
//        NodeResult result = new NodeResult();
//        result.setState(NodeResult.NODE_FAIL);
//        if(StringUtils.isEmpty(menuid)) {
//            String content = userMsg.getString("Content").trim();
//            if(!StringUtils.isEmpty(content)) {
//                if(content.length() >= 2 && content.length() < 12) {
//                    JiebaSegmenter segmenter = new JiebaSegmenter();
//                    JSONObject byName = CityUtil.getByName(segmenter.sentenceProcess(content), 2);
//                    if(byName != null && !StringUtils.isEmpty(byName.getString("name"))) {
//                        JSONObject qualification = new JSONObject();
//                        qualification.put("city", byName.getString("name"));
//                        qualification.put("level", 2);
//                        result.setQualification(qualification);
//                        result.setState(NodeResult.NODE_SUCCESS);
//                        nodeManager.getNode(super.getConfig().getRightNodeId()).sendQuestion(session);
//                    } else {
//                        WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
//                    }
//                } else {
//                    AIUtil.answerAIMsg(content, userMsg, session);
//                    this.sendQuestion(session);
//                }
//            }
//        }
        return null;
    }


}
