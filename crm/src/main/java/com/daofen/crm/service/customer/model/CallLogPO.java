package com.daofen.crm.service.customer.model;

import com.daofen.crm.base.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CallLogPO extends BasePO{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 顾问ID
	 */
	private Long businessId;
	/**
	 * 呼叫类型 日志类型 1,呼入 2呼出 3未接
	 */
	private Integer type;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 客户姓名
	 */
	private String name;
	/**
	 * 拨号时间
	 */
	private Date callTime;
	/**
	 * 
	 */
	private Integer callTimeMath;
	/**
	 * 通话时长
	 */
	private Long duration;
	/**
	 * 客户id
	 */
	private Long customerId;
	
}
