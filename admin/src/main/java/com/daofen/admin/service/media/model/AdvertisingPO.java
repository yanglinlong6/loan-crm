package com.daofen.admin.service.media.model;

import com.daofen.admin.basic.BasePO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdvertisingPO extends BasePO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String channel;
	
	private String operators;
	
	private String advertisingDate;
	
	private String city;
	
	private String account;
	
	private String media;
	
	private Integer type;
	
	private Double consume;
	
	private Integer conversion;
	
	private Double price;
		
}
