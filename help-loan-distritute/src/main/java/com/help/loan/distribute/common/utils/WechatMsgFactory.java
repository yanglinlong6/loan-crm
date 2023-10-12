package com.help.loan.distribute.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class WechatMsgFactory {

    private static final String WEHCAT_MENU_TEXT_MSG = "<a href=\"weixin://bizmsgmenu?msgmenucontent=%s&msgmenuid=%s\">%s</a> \r\n";

    private static final String LINE_FEED = "\r\n";

    public static JSONObject createTextMsg(JSONObject o, String userId) {
        JSONObject msg = new JSONObject();
        msg.put("touser", userId);
        msg.put("msgtype", WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_TEXT);
        JSONObject text = new JSONObject();
        text.put("content", o.get("content"));
        msg.put("text", text);
        return msg;
    }

    public static JSONObject createImageMsg(JSONObject o, String userId) {
        JSONObject msg = new JSONObject();
        msg.put("touser", userId);
        msg.put("msgtype", WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_IMAGE);
        JSONObject image = new JSONObject();
        image.put("media_id", o.get("media_id"));
        msg.put("image", image);
        return msg;
    }

    public static JSONObject createVoiceMsg(JSONObject o, String userId) {
        JSONObject msg = new JSONObject();
        msg.put("touser", userId);
        msg.put("msgtype", WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_VOICE);
        JSONObject voice = new JSONObject();
        voice.put("media_id", o.get("media_id"));
        msg.put("voice", voice);
        return msg;
    }

    public static JSONObject createNewsMsg(JSONObject o, String userId) {
        JSONObject msg = new JSONObject();
        msg.put("touser", userId);
        msg.put("msgtype", WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_NEWS);
        JSONObject news = new JSONObject();
        JSONArray articles = new JSONArray();
        JSONObject article = new JSONObject();
        article.put("title", o.get("title"));
        article.put("description", o.get("description"));
        article.put("url", o.get("url"));
        article.put("picurl", o.get("picurl"));
        articles.add(article);
        news.put("articles", articles);
        msg.put("news", news);
        return msg;
    }

    public static JSONObject createMPNewsMsg(JSONObject o, String userId) {
        JSONObject msg = new JSONObject();
        msg.put("touser", userId);
        msg.put("msgtype", WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_MPNEWS);
        JSONObject mpnews = new JSONObject();
        mpnews.put("media_id", o.get("media_id"));
        msg.put("mpnews", mpnews);
        return msg;
    }

    public static JSONObject createMenuMsg(JSONObject o, String userId) {
        JSONObject msg = new JSONObject();
        msg.put("touser", userId);
        msg.put("msgtype", WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_TEXT);
        JSONObject text = new JSONObject();
        text.put("content", getMenuContent(o));
        msg.put("text", text);
        return msg;
//		JSONObject msg = new JSONObject();
//		msg.put("touser", userId);
//		msg.put("msgtype", WXConstants.MsgConstants.WX_CUST_MSG_TYPE_MENU);
//		JSONObject msgmenu = new JSONObject();
//		msgmenu.put("head_content", o.get("head_content"));
//		JSONArray list = new JSONArray();
//		JSONArray jsonArray = o.getJSONArray("list");
//		for(int i=0;i<jsonArray.size();i++) {
//			JSONObject jsonObject = jsonArray.getJSONObject(i);
//			JSONObject b = new JSONObject();
//			b.put("id",jsonObject.get("menu_id"));
//			b.put("content", "👉"+jsonObject.get("content"));
//			list.add(b);
//		}
//		msgmenu.put("list", list);
//		msgmenu.put("tail_content", o.get("tail_content"));
//		msg.put("msgmenu", msgmenu);
//		return msg;
    }

    private static String getMenuContent(JSONObject o) {
        StringBuilder builder = new StringBuilder();
        builder.append(o.getString("head_content").replace("#换行#", "\r\n"));
        JSONArray jsonArray = o.getJSONArray("list");
        for(int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            builder.append(LINE_FEED);
            builder.append(String.format(WEHCAT_MENU_TEXT_MSG, jsonObject.get("content"), jsonObject.get("menu_id"), "👉" + jsonObject.get("content")));
        }
        builder.append(o.get("tail_content"));
        return builder.toString();
    }


    public static JSONObject createCustMsg(JSONObject o, String userId) {
        JSONObject msg = null;
        switch(o.getString("msg_type")) {
            case WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_TEXT:
                msg = WechatMsgFactory.createTextMsg(o, userId);
                break;
            case WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_IMAGE:
                msg = WechatMsgFactory.createImageMsg(o, userId);
                break;
            case WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_VOICE:
                msg = WechatMsgFactory.createVoiceMsg(o, userId);
                break;
            case WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_NEWS:
                msg = WechatMsgFactory.createNewsMsg(o, userId);
                break;
            case WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_MPNEWS:
                msg = WechatMsgFactory.createMPNewsMsg(o, userId);
                break;
            case WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_MENU:
                msg = WechatMsgFactory.createMenuMsg(o, userId);
                break;
            default:
                throw new RuntimeException("wecaht custom msg type err");

        }
        return msg;
    }

}
