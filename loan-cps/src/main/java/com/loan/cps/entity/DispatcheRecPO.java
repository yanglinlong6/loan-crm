package com.loan.cps.entity;


import org.msgpack.annotation.Message;

import com.loan.cps.common.BasePO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Message
public class DispatcheRecPO extends BasePO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long orgId;
	
	private Long customerId;
	
	private Integer dispatchStatus;
	
	private String dispatchResult;
	
}
