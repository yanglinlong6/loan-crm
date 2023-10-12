package com.loan.wechat.entrances.processor;

import java.util.Map;

import com.loan.wechat.entrances.service.EntrancesService;


public class EventProcessor implements Processor{

	@Override
	public void process(Map<String, String> processRequestXml, EntrancesService entrancesService) {
		EventTypeEnum.getProcessor(processRequestXml.get("Event")).process(processRequestXml, entrancesService);
	}

}
