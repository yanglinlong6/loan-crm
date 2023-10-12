package com.daofen.crm.service.customer.model;


import com.daofen.crm.base.BasePO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MsgLogPO  extends BasePO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String from;
	
	private String to;
	
	private Integer type;
	
	private String content;
	
	private Integer dataType;
	
}
