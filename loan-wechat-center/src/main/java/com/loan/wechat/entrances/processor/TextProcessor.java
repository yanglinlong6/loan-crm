package com.loan.wechat.entrances.processor;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.loan.wechat.common.HttpUtil;
import com.loan.wechat.entrances.constant.WechatConstant;
import com.loan.wechat.entrances.service.EntrancesService;
import com.loan.wechat.entrances.util.HttpEXProxy;


public class TextProcessor implements Processor{

	@Override
	public void process(Map<String, String> processRequestXml,EntrancesService entrancesService) {
		new HttpEXProxy() {
			@Override
			public String excute() {
				return HttpUtil.postForObject("http://"+processRequestXml.get("domain2")+"/dialogue/node/exc", JSON.toJSONString(processRequestXml));
			}
		}.doexcute();
	}
	
}
