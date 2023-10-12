package com.loan.wechat.entrances.processor;

import java.util.Map;

import com.loan.wechat.entrances.service.EntrancesService;


public class EntranceProcessor implements Processor{
	
	@Override
	public void process(Map<String, String> processRequestXml, EntrancesService entrancesService) {
		MsgTypeEnum.getProcessor(processRequestXml.get("MsgType")).process(processRequestXml, entrancesService);
	}
	
}
