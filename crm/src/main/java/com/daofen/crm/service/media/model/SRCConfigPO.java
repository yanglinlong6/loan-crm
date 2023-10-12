package com.daofen.crm.service.media.model;

import com.daofen.crm.base.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SRCConfigPO extends BasePO{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 来源ID
	 */
	private Long sourceId;
	/**
	 * 来源状态：0 开启 1 关闭
	 */
	private Integer state;
	/**
	 * 配置状态：1 执行 0 未执行
	 */
	private Integer configState;
	/**
	 * 配量
	 */
	private Integer configNum;
	/**
	 * 时间段控制，例子 0-24 就是控制0点-24点
	 */
	private String duanDate;
	/**
	 * 执行时间
	 */
	private Date excDate;
	/**
	 * 公司ID
	 */
	private Long companyId;
	/**
	 * 媒体名称
	 */
	private String mediaName;
	/**
	 * 媒体简称
	 */
	private String mediaShorthand;
	/**
	 * 平台名称
	 */
	private String forumName;
	/**
	 * 来源名称
	 */
	private String sourceName;
	/**
	 * 城市
	 */
	private String city;
	
}
