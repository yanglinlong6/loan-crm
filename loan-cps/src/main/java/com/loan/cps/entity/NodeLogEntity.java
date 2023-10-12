package com.loan.cps.entity;

import org.msgpack.annotation.Message;

import lombok.Getter;
import lombok.Setter;

@Message
@Setter
@Getter
public class NodeLogEntity {
	
	private Integer nodeId;
	
	private String userId;
	
	private String sessionId;
	
	private Long time;
	
	private Integer state;
	
	private Long secondTime;
	
}
