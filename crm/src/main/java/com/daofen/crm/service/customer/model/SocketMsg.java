package com.daofen.crm.service.customer.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocketMsg {
	
	public static final Integer ONLINE = 1;
	
	public static final Integer OFFLINE = 2;
	
	public static final Integer TEXT = 0;
	
	private String to;
	
	private String from;
	
	private String content;
	
	private Long time;
	
	private Integer type;
	
	private Object params;
	
	public static SocketMsg generate() {
		return new SocketMsg();
	}
	
}
