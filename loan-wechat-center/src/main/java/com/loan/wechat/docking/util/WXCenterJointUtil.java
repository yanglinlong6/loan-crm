package com.loan.wechat.docking.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.wechat.common.HttpUtil;
import com.loan.wechat.docking.constant.WXConstants;

/**
 * 微信对接工具
 * @author kongzhimin
 *
 */
public class WXCenterJointUtil {
	/**
	 * 获取微信接口授权凭证token
	 * @param appId      微信APPID
	 * @param appSecret  微信SECRET
	 * @return token 微信接口授权凭证token
	 */
	public static String getToken(String appId,String appSecret) {
		return JSON.parseObject(HttpUtil.getForObject(String.format(WXConstants.WXUrlConstants.WX_TOKEN_URL, appId,appSecret))).getString("access_token");
	}
	/**
	 * 获取微信JS-SDK网页授权加密ticket
	 * @param token 微信接口授权凭证token
	 * @return ticket 微信JS-SDK网页授权加密ticket
	 */
	public static String getJSapiTicket(String token) {
		return JSON.parseObject( HttpUtil.getForObject(String.format(WXConstants.WXUrlConstants.WX_JSAPI_TICKET_URL,token))).getString("ticket");
	}
	/**
	 * 对接微信发送客服消息
	 * @param token 微信接口授权凭证token
	 * @param dto 客服消息参数封装类
	 * @return 成功微信返回{"errcode" : 0,"errmsg" : "ok"}错误时微信会返回错误码等信息，请根据错误码查询错误信息
	 */
	public static String sendCustMsg(String token,JSONObject o) {
		return HttpUtil.postForObject(String.format(WXConstants.WXUrlConstants.WX_CUST_MSG_URL,token), o);
	}
	/**
	 * 对接微信发送模版消息
	 * @param token 微信接口授权凭证token
	 * @param dto  模版消息参数封装
	 * @return 成功微信返回{"errcode":0,"errmsg":"ok","msgid":200228332}错误时微信会返回错误码等信息，请根据错误码查询错误信息
	 */
	public static String sendTempMsg(String token,JSONObject o) {
		return HttpUtil.postForObject(String.format(WXConstants.WXUrlConstants.WX_TEMP_MSG_URL,token), o);
	}
	/**
	 * 微信网页授权CODE获取用户授权accessToken
	 * @param code 网页授权CODE
	 * @param appId 微信APPID
	 * @param appSecret 微信SECRET
	 * @return 成功微信返回{ "access_token":"ACCESS_TOKEN","expires_in":7200,"refresh_token":"REFRESH_TOKEN","openid":"OPENID","scope":"SCOPE" }错误时微信会返回错误码等信息，请根据错误码查询错误信息
	 */
	public static String toUserLogin(String code,String appId,String appSecret) {
		return HttpUtil.getForObject(String.format(WXConstants.WXUrlConstants.WX_ACCESSTOKEN_URL,appId,appSecret,code));
	}
	/**
	 * 根据网页授权获取用户信息
	 * @param accesstoken 用户授权accessToken
	 * @param openId 微信用户唯一标识
	 * @return 成功微信返回
	 * 			{    "openid":" OPENID",
					" nickname": NICKNAME,
					"sex":"1",
					"province":"PROVINCE"
					"city":"CITY",
					"country":"COUNTRY",
					"headimgurl":    "http://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46",
					"privilege":[ "PRIVILEGE1" "PRIVILEGE2"     ],
					"unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
					}
					错误时微信会返回错误码等信息，请根据错误码查询错误信息
	 */
	public static String getSNSUserInfo(String accesstoken,String openId) {
		return HttpUtil.getForObject(String.format(WXConstants.WXUrlConstants.WX_USERINFO_URL,accesstoken,openId));
	}
	/**
	 * 查询关注用户信息(UnionID机制)
	 * @param token 微信接口授权凭证token
	 * @param openId 微信用户唯一标识
	 * @return 微信成功返回
	 * 			{
    				"subscribe": 1, 
    				"openid": "o6_bmjrPTlm6_2sgVt7hMZOPfL2M", 
    				"nickname": "Band", 
    				"sex": 1, 
    				"language": "zh_CN", 
    				"city": "广州", 
    				"province": "广东", 
    				"country": "中国", 
    				"headimgurl":"http://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
    				"subscribe_time": 1382694957,
    				"unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
    				"remark": "",
    				"groupid": 0,
    				"tagid_list":[128,2],
    				"subscribe_scene": "ADD_SCENE_QR_CODE",
    				"qr_scene": 98765,
    				"qr_scene_str": ""
				}
				错误时微信会返回错误码等信息，请根据错误码查询错误信息
	 */
	public static String getCgiBinUserInfo(String token,String openId) {
		return HttpUtil.getForObject(String.format(WXConstants.WXUrlConstants.WX_CGIBIN_USERINFO_URL,token,openId));
	}
	
	
}
