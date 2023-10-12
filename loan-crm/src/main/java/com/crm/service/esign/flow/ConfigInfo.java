package com.crm.service.esign.flow;

/**
 * @description 流程配置实体类
 * @author 宫清
 * @date 2019年7月15日 下午2:53:00
 * @since JDK1.7
 */
public class ConfigInfo {

	// 回调通知地址 ,默认取项目配置通知地址
	private String noticeDeveloperUrl;

	// 通知方式，逗号分割，1-短信，2-邮件 。默认值1，请务必请选择一个通知方式，
	// 否则客户将接收不到流程的签署通知和审批通知，如果流程需要审批，将导致审批无法完成
	private String noticeType;

	// 签署完成重定向地址 ,默认签署完成停在当前页面
	private String redirectUrl;

	// 签署平台，逗号分割，1-开放服务h5 ，2-支付宝签 ，默认值1
	private String signPlatform;

	public ConfigInfo(String noticeDeveloperUrl, String noticeType, String redirectUrl, String signPlatform) {
		this.noticeDeveloperUrl = noticeDeveloperUrl;
		this.noticeType = noticeType;
		this.redirectUrl = redirectUrl;
		this.signPlatform = signPlatform;
	}

	public ConfigInfo() {
	}

	public String getNoticeDeveloperUrl() {
		return noticeDeveloperUrl;
	}

	public void setNoticeDeveloperUrl(String noticeDeveloperUrl) {
		this.noticeDeveloperUrl = noticeDeveloperUrl;
	}

	public String getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getSignPlatform() {
		return signPlatform;
	}

	public void setSignPlatform(String signPlatform) {
		this.signPlatform = signPlatform;
	}

}
