package com.daofen.crm.service.media.model;

import com.daofen.crm.base.BasePO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourcePO extends BasePO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 来源所属媒体
	 */
	private Long mediaId;
	/**
	 * 来源状态
	 */
	private Integer state;
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
	/**
	 * 配量
	 */
	private Integer num;
	/**
	 * 时间段
	 */
	private String duanDate;
	/**
	 * 单价
	 */
	private Integer price;
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
	
}
