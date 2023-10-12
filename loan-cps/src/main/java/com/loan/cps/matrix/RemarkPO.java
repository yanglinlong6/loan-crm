package com.loan.cps.matrix;

import com.loan.cps.common.BasePO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemarkPO extends BasePO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	/**
	 * 客户ID
	 */
	private Long customerId;
	/**
	 * 备注内容
	 */
	private String content;
	/**
	 * 业务员ID
	 */
	private Long businessId;
	
}
