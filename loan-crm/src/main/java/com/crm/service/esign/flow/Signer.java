package com.crm.service.esign.flow;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 签署方信息
 * @date 2019年11月19日 下午2:36:30
 * @since JDK1.7
 */
@Setter
@Getter
public class Signer {
	
	//是否平台自动签署
	private Boolean platformSign;
	
	//签署方签署顺序
	private Integer signOrder;
	
	//签署方账号信息
	private SignerAccount signerAccount;
	
	//签署文件信息
	private List<SignfieldInfo> signfields;
	
	private String thirdOrderNo;

	/**
	 *
	 * @param platformSign 是否平台方自动签署，默认false,true-平台方自动签署,false-平台用户签署
	 * @param signOrder 签署顺序，默认1，且不小于1。顺序越小越先处理
	 * @param signerAccount
	 * @param signfields
	 * @param thirdOrderNo
	 */
	public Signer(Boolean platformSign, Integer signOrder, SignerAccount signerAccount, List<SignfieldInfo> signfields,
                  String thirdOrderNo) {
		this.platformSign = platformSign;
		this.signOrder = signOrder;
		this.signerAccount = signerAccount;
		this.signfields = signfields;
		this.thirdOrderNo = thirdOrderNo;
	}

	public Signer() {
	}

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
	 * @return  Signer
	 */
	public Signer putSignerAccount(String signerAccountId,String authorizedAccountId, String noticeType, String willTypes){
		if(StringUtils.isBlank(signerAccountId) && StringUtils.isBlank(authorizedAccountId)){
			return this;
		}
		signerAccount = new SignerAccount(signerAccountId,authorizedAccountId,noticeType,willTypes);
		return this;
	}


	public Signer putSignField(SignfieldInfo signfieldInfo){
		if(null == signfieldInfo)
			return this;
		if(CollectionUtils.isEmpty(signfields))
			signfields = new ArrayList<>();
		signfields.add(signfieldInfo);
		return this;
	}
	
}
