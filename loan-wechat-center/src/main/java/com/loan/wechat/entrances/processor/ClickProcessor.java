package com.loan.wechat.entrances.processor;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.loan.wechat.common.HttpUtil;
import com.loan.wechat.entrances.service.EntrancesService;
import com.loan.wechat.entrances.util.HttpEXProxy;


public class ClickProcessor implements Processor{
	
	private static final String CLICK_EVENT_KEY_PROCEED = "PROCEED";

	@Override
	public void process(Map<String, String> processRequestXml, EntrancesService entrancesService ) {
		String string = processRequestXml.get("EventKey");
		if(CLICK_EVENT_KEY_PROCEED.equals(string)) {
			new HttpEXProxy() {
				@Override
				public String excute() {
					return HttpUtil.postForObject("http://"+processRequestXml.get("domain2")+"/dialogue/node/proceed", JSON.toJSONString(processRequestXml));
				}
			}.doexcute();
		}
	}
	
}
