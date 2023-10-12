package com.loan.wechat.entrances.processor;

import java.util.Map;

import com.loan.wechat.entrances.service.EntrancesService;


public interface Processor {

	void process(Map<String,String> processRequestXml ,EntrancesService entrancesService);
	
}
