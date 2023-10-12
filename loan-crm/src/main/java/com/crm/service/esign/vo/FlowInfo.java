package com.crm.service.esign.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @description 流程基本信息
 * @since JDK1.7
 */
@Setter
@Getter
public class FlowInfo {

	//是否自动归档
	private Boolean autoArchive=true;
	
	//是否自动开启
	private Boolean autoInitiate;
	
	//业务场景
	private String businessScene;
	
	//文件到期前，提前多少小时回调提醒续签，小时（时间区间：1小时——15天
	private Integer contractRemind;
	
	//文件有效截止日期,毫秒
	private Long contractValidity;
	
	//流程备注
//	private String remark;
	
	//签署有效截止时间，毫秒
	private Long signValidity;

	/**发起人账户id，即发起本次签署的操作人个人账号id；如不传，默认由对接平台发起*/
	private String initiatorAccountId;

	/**发起方主体id，如存在个人代机构发起签约，则需传入机构id；如不传，则默认是对接平台*/
	private String initiatorAuthorizedAccountId;

	//任务配置信息
	private FlowConfigInfo flowConfigInfo;

	/**
	 *
	 * @param autoArchive 是否自动归档
	 * @param autoInitiate 是否自动开启
	 * @param businessScene 业务场景,流程名称(自定义)
	 * @param contractRemind 文件到期前，提前多少小时回调提醒续签，小时（时间区间：1小时——15天
	 * @param contractValidity 文件有效截止日期,毫秒
	 * @param signValidity  签署有效截止时间，毫秒
	 * @param initiatorAccountId 发起人账户id，即发起本次签署的操作人个人账号id；如不传，默认由对接平台发起
	 * @param initiatorAuthorizedAccountId 发起方主体id，如存在个人代机构发起签约，则需传入机构id；如不传，则默认是对接平台
	 * @param flowConfigInfo FlowConfigInfo 任务配置信息
	 */
	public FlowInfo(Boolean autoArchive,
					Boolean autoInitiate,
					String businessScene,
					Integer contractRemind,
					Long contractValidity,
					Long signValidity,
					String initiatorAccountId,
					String initiatorAuthorizedAccountId,
					FlowConfigInfo flowConfigInfo) {
		this.autoArchive = autoArchive;
		this.autoInitiate = autoInitiate;
		this.businessScene = businessScene;
		this.contractRemind = contractRemind;
		this.contractValidity = contractValidity;
		this.signValidity = signValidity;
		this.initiatorAccountId = initiatorAccountId;
		this.initiatorAuthorizedAccountId = initiatorAuthorizedAccountId;
		this.flowConfigInfo = flowConfigInfo;
	}

	/**
	 *
	 * @param businessScene 业务场景,流程名称(自定义)
	 * @param initiatorAccountId 发起人账户id，即发起本次签署的操作人个人账号id；如不传，默认由对接平台发起
	 * @param orgId 发起方主体id，如存在个人代机构发起签约，则需传入机构id；如不传，则默认是对接平台
	 * @param flowConfigInfo FlowConfigInfo 任务配置信息
	 */
	public FlowInfo(String businessScene,
					String initiatorAccountId,
					String orgId,
					FlowConfigInfo flowConfigInfo) {
		this.businessScene = businessScene;
		this.initiatorAccountId = initiatorAccountId;
		this.initiatorAuthorizedAccountId = orgId;
		this.flowConfigInfo = flowConfigInfo;
	}

	public FlowInfo(String businessScene, FlowConfigInfo flowConfigInfo) {
		this.businessScene = businessScene;
		this.flowConfigInfo = flowConfigInfo;
	}

	public FlowInfo() {
	}
	
	
	
}
