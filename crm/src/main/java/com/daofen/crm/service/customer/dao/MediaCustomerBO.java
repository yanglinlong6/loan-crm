package com.daofen.crm.service.customer.dao;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaCustomerBO {
	
	private String appid;
	
	private String nonc;
	
	private String sign;
	
	private Long timestamp;
	
	private String params;
	
	private Integer encryption;
	
}
