package com.crm.service.esign.flow;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @description 签署方账号信息
 * @author 宫清
 * @date 2019年11月19日 下午2:40:46
 * @since JDK1.7
 */
@Setter
@Getter
public class SignerAccount {
	
	//签署操作人个人账号标识，即操作本次签署的个人
	private String signerAccountId;
	
	//签约主体账号标识，即本次签署对应任务的归属方
	private String authorizedAccountId;

	/**通知方式，可选择多种通知方式，逗号分割，1-短信，2-邮件
	 注意：可以通过此字段控制单个签署方的通知方式，不同的签署方可以设置不同的通知方式
	 详细规则*/
	private String noticeType;

	/**指定该签署人的意愿认证方式
	 若flowConfigInfo下也传入willTypes，以
	 signerAccount下该参数优先
	 类型如下：
	 	CODE_SMS 短信验证码
	 	FACE_ZHIMA_XY 支付宝刷脸
	 	FACE_TECENT_CLOUD_H5 腾讯云刷脸
	 	FACE_FACE_LIVENESS_RECOGNITION e签宝刷脸
	 	FACE_WE_CHAT_FACE 微信小程序刷脸
	 	FACE_AUDIO_VIDEO_DUAL 智能视频认证*/
	private String willTypes;

	/**
	 *
	 * @param signerAccountId 签署操作人个人账号标识，即操作本次签署的个人
	 * @param authorizedAccountId 签约主体账号标识，即本次签署对应任务的归属方
	 * @param noticeType 可选择多种通知方式，逗号分割，1-短信，2-邮件
	 * @param willTypes 指定该签署人的意愿认证方式:
	 *                     CODE_SMS-短信验证码,
	 *                     FACE_ZHIMA_XY-支付宝刷脸 ,
	 *                     FACE_TECENT_CLOUD_H5-腾讯云刷脸,
	 *                     FACE_FACE_LIVENESS_RECOGNITION-e签宝刷脸 ,
	 *                     FACE_WE_CHAT_FACE-微信小程序刷脸 ,
	 *                     FACE_AUDIO_VIDEO_DUAL-智能视频认证
	 */
	public SignerAccount(String signerAccountId, String authorizedAccountId, String noticeType, String willTypes) {
		this.signerAccountId = signerAccountId;
		this.authorizedAccountId = authorizedAccountId;
		this.noticeType = noticeType;
		this.willTypes = willTypes;
	}

	public SignerAccount(String signerAccountId, String authorizedAccountId) {
		this.signerAccountId = signerAccountId;
		this.authorizedAccountId = authorizedAccountId;
	}
	public SignerAccount() {
	}


	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
}
