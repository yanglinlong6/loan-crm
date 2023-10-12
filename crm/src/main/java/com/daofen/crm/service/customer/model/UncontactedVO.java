package com.daofen.crm.service.customer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UncontactedVO {
	/**
	 * 未联系客户数量
	 */
	private Integer unum;
	/**
	 * 未联系客户类型  1：0星客户超6小时未联系  2:2星或2+星用户超6天未联系   3:3星或4星用户超4天未联系
	 */
	private Integer utp;
	
}
