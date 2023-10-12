package com.loan.wechat.entrances.processor;

import java.util.Map;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.loan.wechat.common.HttpUtil;
import com.loan.wechat.entrances.service.EntrancesService;
import com.loan.wechat.entrances.util.HttpEXProxy;

public class ViewProcessor implements Processor{

	@Override
	public void process(Map<String, String> processRequestXml, EntrancesService entrancesService) {
	}

}
