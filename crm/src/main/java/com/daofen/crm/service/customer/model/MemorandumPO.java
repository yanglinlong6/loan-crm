package com.daofen.crm.service.customer.model;

import java.util.Date;

import com.daofen.crm.base.BasePO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemorandumPO extends BasePO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long custId;
	
	private Integer state;
	
	private Date thingTime;
	
	private String content;

}
