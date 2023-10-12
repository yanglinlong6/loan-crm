package com.loan.wechat.docking.entity;

import com.loan.wechat.common.BaseDTO;

/**
 * 微信公众号对象
 * @author kongzhimin
 *
 */
public class WechatDTO extends BaseDTO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * mysqlID
	 */
	private Integer id;
	/**
	 * 公众号原始ID
	 */
	private String wechatId;
	/**
	 * 公众号编号（适应原设计）
	 */
	private Integer wxType;
	/**
	 * 公众号接口调用凭证
	 */
	private String token;
	/**
	 * 公众号appid
	 */
	private String appId;
	/**
	 * 公众号appSecret
	 */
	private String appSecret;
	/**
	 * 公众号配置的二级域名
	 */
	private String domain2;
	/**
	 * 公众号JS-SDK加密TICKET
	 */
	private String jsapi;
	/**
	 * 公众号用户openID前6位
	 */
	private String openid;
	/**
	 * 公众号媒体名称
	 */
	private String mediaName;
	/**
	 * 公众号媒体ID
	 */
	private Integer mediaId;
	
	private String wechat;
	
	private Integer appType;
	
	public Integer getAppType() {
		return appType;
	}

	public void setAppType(Integer appType) {
		this.appType = appType;
	}

	public String getWechat() {
		return wechat;
	}

	public void setWechat(String wechat) {
		this.wechat = wechat;
	}

	public String getMediaName() {
		return mediaName;
	}

	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}

	public Integer getMediaId() {
		return mediaId;
	}

	public void setMediaId(Integer mediaId) {
		this.mediaId = mediaId;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getJsapi() {
		return jsapi;
	}

	public void setJsapi(String jsapi) {
		this.jsapi = jsapi;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWechatId() {
		return wechatId;
	}

	public void setWechatId(String wechatId) {
		this.wechatId = wechatId;
	}

	public Integer getWxType() {
		return wxType;
	}

	public void setWxType(Integer wxType) {
		this.wxType = wxType;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getDomain2() {
		return domain2;
	}

	public void setDomain2(String domain2) {
		this.domain2 = domain2;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
