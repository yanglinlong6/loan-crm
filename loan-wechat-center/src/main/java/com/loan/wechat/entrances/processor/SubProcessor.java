package com.loan.wechat.entrances.processor;

import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan.wechat.common.HttpUtil;
import com.loan.wechat.docking.cache.IWechatCache;
import com.loan.wechat.entrances.constant.WechatConstant;
import com.loan.wechat.entrances.service.EntrancesService;
import com.loan.wechat.entrances.util.HttpEXProxy;
import com.loan.wechat.login.entity.UserDTO;

public class SubProcessor implements Processor{
	
	private static Log logger = LogFactory.getLog(SubProcessor.class);

	@Override
	public void process(Map<String, String> processRequestXml, EntrancesService entrancesService) {
		new HttpEXProxy() {
			@Override
			public String excute() {
				return HttpUtil.postForObject("http://"+processRequestXml.get("domain2")+"/dialogue/node/start", JSON.toJSONString(processRequestXml));
			}
		}.doexcute();
	}
	
}
