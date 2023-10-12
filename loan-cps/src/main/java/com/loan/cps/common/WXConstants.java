package com.loan.cps.common;
/**
 * 
 * @author kongzhimin
 *
 */
public interface WXConstants {
	/**
	 * 客服消息常量
	 * @author kongzhimin
	 *
	 */
	interface MsgConstants{
		/**
		 * 客服消息类型   文本消息
		 */
		String WX_CUST_MSG_TYPE_TEXT = "text";
		/**
		 * 客服消息类型  图片消息
		 */
		String WX_CUST_MSG_TYPE_IMAGE = "image";
		/**
		 * 客服消息类型  语音消息
		 */
		String WX_CUST_MSG_TYPE_VOICE = "voice";
		/**
		 * 客服消息类型  视频消息
		 */
		String WX_CUST_MSG_TYPE_VIDEO = "video";
		/**
		 * 客服消息类型  音乐消息
		 */
		String WX_CUST_MSG_TYPE_MUSIC = "music";
		/**
		 * 客服消息类型  图文外链消息
		 */
		String WX_CUST_MSG_TYPE_NEWS = "news";
		/**
		 * 客服消息类型  推文消息
		 */
		String WX_CUST_MSG_TYPE_MPNEWS = "mpnews";
		/**
		 * 客服消息类型   卡卷消息
		 */
		String WX_CUST_MSG_TYPE_CARD = "wxcard";
		/**
		 * 客服消息类型   菜单消息
		 */
		String WX_CUST_MSG_TYPE_MENU = "msgmenu";
		
	}
	/**
	 * 模版消息常量
	 * @author kongzhimin
	 *
	 */
	interface TempConstants{
		
		
		
		
	}
	
	/**
	 * 微信接口URL常量
	 * @author kongzhimin
	 *
	 */
	interface WXUrlConstants{
		/**
		 * 微信获取接口调用凭证access_token
		 * 
		 * appid 微信appid
		 * 
		 * secret 微信secret
		 * 
		 */
		String WX_TOKEN_URL="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
		/**
		 * 微信获取api_ticket
		 * 
		 * access_token 微信缓存接口调用凭证TOKEN
		 * 
		 */
		String WX_JSAPI_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";
		/**
		 * 微信网页授权access_token获取接口
		 * 
		 * appid 微信appid
		 * 
		 * secret 微信secret
		 * 
		 * code 微信网页授权code
		 * 
		 */
		String WX_ACCESSTOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
		/**
		 * 微信网页授权拉取用户信息
		 * 
		 * access_token 微信网页授权access_token
		 * 
		 * openid 微信用户唯一标识
		 * 
		 */
		String WX_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
		/**
		 * 微信获取用户基本信息（包括UnionID机制）
		 * 
		 * access_token 微信缓存接口调用凭证TOKEN
		 * 
		 * openid 微信用户唯一标识
		 * 
		 */
		String WX_CGIBIN_USERINFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
		/**
		 * 微信模板消息接口
		 * 
		 * access_token 微信缓存接口调用凭证TOKEN
		 */
		String WX_TEMP_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
		/**
		 * 微信客服接口-发消息
		 * 
		 * access_token 微信缓存接口调用凭证TOKEN
		 */
		String WX_CUST_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=%s";
	}
	
}
