package com.help.loan.distribute.common.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 微信对接中心链接工具
 *
 * @author kongzhimin
 */
public class WechatCenterUtil {

    private static Log logger = LogFactory.getLog(WechatCenterUtil.class);
    /**
     * 微信对接中心服务基础URL
     */
//	private static final String BASE_URL = "http://10.5.11.75/wechat";
    /**
     * 微信对接中心服务基础URL
     */
    private static final String BASE_URL = "http://htyj.daofen100.com/center";
    /**
     * 微信对接中心接口URL-获取token
     */
    private static final String WX_TOKEN_GET_URL = BASE_URL + "/wxcenter/token/get?wechatId=%s&domain2=%s&userid=%s";
    /**
     * 微信对接中心接口URL-获取公众号对象
     */
    private static final String WX_WECHAT_GET_URL = BASE_URL + "/wxcenter/wechat/get?wechatId=%s&domain2=%s&userid=%s";

    /**
     * 微信对接中心接口URL-微信模版消息发送
     */
    private static final String WX_TEMP_MSG_SEND_URL = BASE_URL + "/wxcenter/sendTempMsg?wechatId=%s&domain2=%s";
    /**
     * 微信对接中心接口URL-发送客服消息
     */
    private static final String WX_CUST_MSG_SEND_URL = BASE_URL + "/wxcenter/sendCustMsg?wechatId=%s&domain2=%s";
    /**
     * 微信对接中心接口URL-微信模版消息发送
     */
    private static final String WX_TEMP_MSG_LIST_SEND_URL = BASE_URL + "/wxcenter/sendTempMsgList?wechatId=%s&domain2=%s";
    /**
     * 微信对接中心接口URL-发送客服消息
     */
    private static final String WX_CUST_MSG_LIST_SEND_URL = BASE_URL + "/wxcenter/sendCustMsgList?wechatId=%s&domain2=%s";
    /**
     * 微信对接中心接口URL-微信网页授权登录
     */
    private static final String WX_CUST_HTML_LOGIN_URL = BASE_URL + "/wxcenter/code/htmlLogin?domain2=%s&code=%s";
    /**
     * 微信对接中心接口URL-微信公众号关注用户信息获取
     */
    private static final String WX_USERINFO_CGIBIN_GET_URL = BASE_URL + "/wxcenter/userinfo/get?wechatId=%s&domain2=%s&userid=%s";

    /**
     * 微信对接中心接口URL-微信公众号关注用户信息获取
     */
    private static final String WX_USERINFO_CGIBIN_GET_URL_1 = BASE_URL + "/wxcenter/userinfo/get1?wechatId=%s&domain2=%s&userid=%s";

    /**
     * 微信对接中心接口URL-腾讯广告token缓存获取接口
     */
    private static final String TENCENT_MARKETING_TOKEN_GET_URL = BASE_URL + "/tencent/marketing/token/get?accountid=%s";

    private static final String LEFT = "👉";

    /**
     * 获取公众号对象
     *
     * @param domain2  公众号二级域名
     * @param wechatId 公众号原始ID
     * @param openId   用户openId
     *                 以上3个参数传1即可
     * @return wechat 公众号基础信息封装
     */
    public static String getWechat(String wechatId, String domain2, String openId) {
        String response = HttpUtil.getForObject(String.format(WX_WECHAT_GET_URL, wechatId, domain2, openId));
        return response;
    }

    /**
     * 获取token
     *
     * @param domain2  公众号二级域名
     * @param wechatId 公众号原始ID
     * @param openId   用户openId
     *                 以上3个参数传1即可
     * @return token
     */
    public static String getToken(String wechatId, String domain2, String openId) {
        return HttpUtil.getForObject(String.format(WX_TOKEN_GET_URL, wechatId, domain2, openId));
    }

    /**
     * 发送微信公众号客服消息
     *
     * @param dto      客服消息参数封装对象    必须
     * @param domain2  公众号二级域名       非必须
     * @param wechatId 公众号原始ID    非必须
     * @return 同微信返回
     */
    public static String sendCustMsg(JSONObject dto, String wechatId, String domain2) {
        if(WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_TEXT.equals(dto.getString("msgtype"))) {
            JSONObject parseObject = JSON.parseObject(getWechat(wechatId, domain2, ""));
            JSONObject jsonObject = parseObject.getJSONObject("o");
            if(jsonObject != null) {
                JSONObject jsonObject2 = dto.getJSONObject("text");
                jsonObject2.put("content", jsonObject2.getString("content").replace("{appid}", jsonObject.getString("appId")).replace("{domain2}", domain2).replace("{userid}", dto.getString("touser")).replace("{left}", LEFT));
                dto.put("text", jsonObject2);
            }
        } else if(WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_MENU.equals(dto.getString("msgtype"))) {
            JSONObject parseObject = JSON.parseObject(getWechat(wechatId, domain2, ""));
            JSONObject jsonObject = parseObject.getJSONObject("o");
            if(jsonObject != null) {
                JSONObject jsonObject2 = dto.getJSONObject("msgmenu");
                jsonObject2.put("head_content", jsonObject2.getString("head_content").replace("{appid}", jsonObject.getString("appId")).replace("{domain2}", domain2).replace("{userid}", dto.getString("touser")).replace("{left}", LEFT));
                jsonObject2.put("tail_content", jsonObject2.getString("tail_content").replace("{appid}", jsonObject.getString("appId")).replace("{domain2}", domain2).replace("{userid}", dto.getString("touser")).replace("{left}", LEFT));
                dto.put("msgmenu", jsonObject2);
            }
        }
        return HttpUtil.postForJSON(String.format(WX_CUST_MSG_SEND_URL, wechatId, domain2), dto);
    }

    /**
     * 发送微信公众号客服消息
     *
     * @param dto      客服消息参数封装对象    必须
     * @param domain2  公众号二级域名       非必须
     * @param wechatId 公众号原始ID    非必须
     * @return 同微信返回
     */
    public static String sendCustMsg(JSONObject dto, String wechatId, String domain2,String userId) {
        if(WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_TEXT.equals(dto.getString("msgtype"))) {
            JSONObject parseObject = JSON.parseObject(getWechat(wechatId, domain2, userId));
            JSONObject jsonObject = parseObject.getJSONObject("o");
            if(jsonObject != null) {
                JSONObject jsonObject2 = dto.getJSONObject("text");
                jsonObject2.put("content", jsonObject2.getString("content").replace("{appid}", jsonObject.getString("appId")).replace("{domain2}", domain2).replace("{userid}", dto.getString("touser")).replace("{left}", LEFT));
                dto.put("text", jsonObject2);
            }
        } else if(WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_MENU.equals(dto.getString("msgtype"))) {
            JSONObject parseObject = JSON.parseObject(getWechat(wechatId, domain2, userId));
            JSONObject jsonObject = parseObject.getJSONObject("o");
            if(jsonObject != null) {
                JSONObject jsonObject2 = dto.getJSONObject("msgmenu");
                jsonObject2.put("head_content", jsonObject2.getString("head_content").replace("{appid}", jsonObject.getString("appId")).replace("{domain2}", domain2).replace("{userid}", dto.getString("touser")).replace("{left}", LEFT));
                jsonObject2.put("tail_content", jsonObject2.getString("tail_content").replace("{appid}", jsonObject.getString("appId")).replace("{domain2}", domain2).replace("{userid}", dto.getString("touser")).replace("{left}", LEFT));
                dto.put("msgmenu", jsonObject2);
            }
        }
        return HttpUtil.postForJSON(String.format(WX_CUST_MSG_SEND_URL, wechatId, domain2), dto);
    }

    /**
     * 发送微信公众号客服消息 （批量）
     *
     * @param domain2  公众号二级域名       非必须
     * @param wechatId 公众号原始ID    非必须
     * @return 同微信返回
     */
    public static String sendCustMsgList(JSONArray array, String wechatId, String domain2) {
        return HttpUtil.postForJSON(String.format(WX_CUST_MSG_LIST_SEND_URL, wechatId, domain2), array.toJSONString());
    }

    /**
     * 发送微信公众号模版消息
     *
     * @param dto      客服消息参数封装对象    必须
     * @param domain2  公众号二级域名       非必须
     * @param wechatId 公众号原始ID    非必须
     * @return 同微信返回
     */
    public static String sendWechatTempMsg(JSONObject dto, String wechatId, String domain2) {
        return HttpUtil.postForJSON(String.format(WX_TEMP_MSG_SEND_URL, wechatId, domain2), dto);
    }

    /**
     * 发送微信公众号模版消息  (批量)
     *
     * @param domain2  公众号二级域名       非必须
     * @param wechatId 公众号原始ID    非必须
     * @return 同微信返回
     */
    public static String sendWechatTempMsgList(JSONArray array, String wechatId, String domain2) {
        return HttpUtil.postForJSON(String.format(WX_TEMP_MSG_LIST_SEND_URL, wechatId, domain2), array.toJSONString());
    }

    /**
     * 微信网页授权登录接口
     *
     * @param code    微信网页授权CODE 必须
     * @param domain2 公众号二级域名   必须
     * @return 同微信返回
     */
    public static String htmlLogin(String code, String domain2) {
        return HttpUtil.getForObject(String.format(WX_CUST_HTML_LOGIN_URL, domain2, code));
    }

    /**
     * 微信公众号关注用户信息获取
     *
     * @param openId   微信用户唯一标识    必须
     * @param domain2  公众号二级域名       非必须
     * @param wechatId 公众号原始ID    非必须
     * @return 同微信返回
     */
    public static String getUserInfo(String openId, String wechatId, String domain2) {
        return HttpUtil.getForObject(String.format(WX_USERINFO_CGIBIN_GET_URL, wechatId, domain2, openId));
    }

    /**
     * 微信公众号关注用户信息获取
     *
     * @param openId   微信用户唯一标识    必须
     * @param domain2  公众号二级域名       非必须
     * @param wechatId 公众号原始ID    非必须
     * @return 同微信返回
     */
    public static String getUserInfo_1(String openId, String wechatId, String domain2) {
        return HttpUtil.getForObject(String.format(WX_USERINFO_CGIBIN_GET_URL_1, wechatId, domain2, openId));
    }

    /**
     * 获取腾讯广告授权TOKEN
     *
     * @param accountid
     * @return
     */
    public static String getMarketingAPIToken(String accountid) {
        return HttpUtil.getForObject(String.format(TENCENT_MARKETING_TOKEN_GET_URL, accountid));
    }


//    public static void main(String[] args){
//
//        String result = getWechat("","","aa5c6be712854a2fab96fd0d2dd8e95c");
//        System.out.println(result);
//
//    }

}
