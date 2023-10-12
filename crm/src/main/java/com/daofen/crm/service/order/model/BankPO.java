package com.daofen.crm.service.order.model;

import com.daofen.crm.base.BasePO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankPO extends BasePO{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 银行名称
	 */
	private String name;

}
