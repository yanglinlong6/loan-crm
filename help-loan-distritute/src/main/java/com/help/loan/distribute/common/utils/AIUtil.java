package com.help.loan.distribute.common.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.entity.Session;

public class AIUtil {

    private static String apiKey = "46bb2ec0040a4a9cad39f797622530e1";

    private static Integer count = 30;

    private static final String TULING_OPENAPI_URL = "http://openapi.tuling123.com/openapi/api/v2";

    public static void answerAIMsg(String content, JSONObject userMsg, Session session) {
        String answer = "";
        if(session.getAiCount() >= Integer.valueOf(count)) {
            answer = "抱歉，不能回答您更多问题了";
        } else {
            session.setAiCount(session.getAiCount().intValue() + 1);
            answer = parseResult(HttpUtil.postForJSON(TULING_OPENAPI_URL, getAIParams(content, userMsg.getString("FromUserName"))));
        }
        WechatCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(getCustMsgParams(answer), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
    }

    private static JSONObject getAIParams(String content, String userId) {
        JSONObject params = new JSONObject();
        params.put("reqType", 0);
        JSONObject perception = new JSONObject();
        JSONObject inputText = new JSONObject();
        inputText.put("text", content);
        perception.put("inputText", inputText);
        params.put("perception", perception);
        JSONObject userInfo = new JSONObject();
        userInfo.put("apiKey", apiKey);
        userInfo.put("userId", userId);
        params.put("userInfo", userInfo);
        return params;
    }

    private static String parseResult(String result) {
        JSONObject parseObject = JSON.parseObject(result);
        return parseObject.getJSONArray("results").getJSONObject(0).getJSONObject("values").getString("text");
    }

    private static JSONObject getCustMsgParams(String content) {
        JSONObject params = new JSONObject();
        params.put("msg_type", WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_TEXT);
        params.put("content", content);
        return params;
    }

}
