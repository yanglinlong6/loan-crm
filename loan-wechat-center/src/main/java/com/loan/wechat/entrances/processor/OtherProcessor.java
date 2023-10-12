package com.loan.wechat.entrances.processor;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.loan.wechat.entrances.service.EntrancesService;
import com.loan.wechat.entrances.service.IEntrancesFacade;


public class OtherProcessor implements Processor{

	private static Log logger = LogFactory.getLog(IEntrancesFacade.class);
	
	@Override
	public void process(Map<String, String> processRequestXml, EntrancesService entrancesService) {
	}

}
