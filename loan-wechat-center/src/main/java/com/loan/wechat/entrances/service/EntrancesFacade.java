package com.loan.wechat.entrances.service;

import java.util.Map;

/**
 * 
 * @author kongzhimin
 *
 */
public interface EntrancesFacade {
	
	/**
	 * 接收处理微信推送消息
	 * @param req
	 * @return
	 */
	String xmlMsgReceive(Map<String, String> wechatMsg);
	
}
