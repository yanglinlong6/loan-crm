package com.daofen.crm.service.order.model;

import com.daofen.crm.base.BasePO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationLogPO extends BasePO{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 操作人ID
	 */
	private Long businessId;
	/**
	 * 操作类型
	 */
	private Integer type;
	/**
	 * 订单ID
	 */
	private Long orderId;
	
}
