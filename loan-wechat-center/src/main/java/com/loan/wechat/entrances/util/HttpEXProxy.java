package com.loan.wechat.entrances.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public abstract class HttpEXProxy {

	private static Log logger = LogFactory.getLog(HttpEXProxy.class);
	
	public String doexcute() {
		String ss=null;
		try {
			ss=  excute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  ss;
	}
	
	public static Map<String,String> getDefHeadMap(){
		Map<String,String> map = new HashMap<String,String>();
		map.put("Content-Type", "application/json");
		return map;
	}
	
	public abstract String excute();
	
	public static Integer getWxType(String domain) {
		JSONObject parseObject = null;
		for(int i=0;i<3;i++) {
			try {
				parseObject = JSON.parseObject("");
				return parseObject.getJSONObject("datas").getIntValue("wxType");
			}catch (Exception e) {
				e.printStackTrace();
				logger.info(parseObject);
			}
		}
		return 0;
	}
	
}
