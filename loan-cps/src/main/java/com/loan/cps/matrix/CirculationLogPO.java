package com.loan.cps.matrix;

import com.loan.cps.common.BasePO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CirculationLogPO extends BasePO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 客户ID
	 */
	private Long customerId;
	/**
	 * 流转类型：1自动分配 2再分配 3公共池分配 4等待池分配 5加入公共池
	 */
	private Integer type;
	/**
	 * 业务员ID
	 */
	private Long businessId;
	
}
