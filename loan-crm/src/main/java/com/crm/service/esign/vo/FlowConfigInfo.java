package com.crm.service.esign.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @description 任务配置信息
 * @author 宫清
 * @date 2019年11月19日 下午2:32:59
 * @since JDK1.7
 */
@Setter
@Getter
public class FlowConfigInfo {
	
	//通知开发者地址
	private String noticeDeveloperUrl;
	
	/**
	 * 签署通知方式， 默认方式：1。
	 * 	同时支持多种通知方式，用逗号分割。
	 * 	1-短信，2-邮件。
	 */
	private String noticeType;
	
	//签署完成重定向地址
	private String redirectUrl;
	
	//签署平台
	private String signPlatform;


	public FlowConfigInfo(String noticeType) {
		this.noticeType = noticeType;
	}

	/**
	 * 创建默认流程配置信息对象
	 * @param noticeDeveloperUrl 通知开发者地址 通知开发者流程签署状态的回调地址
	 * @param noticeType 签署通知方式， 默认方式：1。(1-短信，2-邮件。) 同时支持多种通知方式，用逗号分割。
	 * @param redirectUrl 签署完成后，重定向跳转地址（http/https）
	 * @param signPlatform 签署平台，默认值1,2。可选择多种签署平台，逗号分割。(1-H5网页版方式进行签署；2-跳转支付宝(移动端)或支付宝扫码进行签署(PC端)。)
	 */
	public FlowConfigInfo(String noticeDeveloperUrl, String noticeType, String redirectUrl, String signPlatform) {
		this.noticeDeveloperUrl = noticeDeveloperUrl;
		this.noticeType = noticeType;
		this.redirectUrl = redirectUrl;
		this.signPlatform = signPlatform;
	}

	public FlowConfigInfo() {
	}
	
	
}
