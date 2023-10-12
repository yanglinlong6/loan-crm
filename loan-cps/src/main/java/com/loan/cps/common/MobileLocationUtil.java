package com.loan.cps.common;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobileLocationUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(MobileLocationUtil.class);

	/**聚合数据api*/
	private static final String JUHE_API = "http://apis.juhe.cn/mobile/get?phone=%s&key=fc50eac91ba97752655ed7bf9eb11e1f";

	public static JSONObject getAndCheck(String mobile) {
		JSONObject result = new JSONObject();
		try {
			String jsonStr = HttpUtil.getForObject(String.format(JUHE_API, mobile));
			JSONObject parseObject = JSONObject.parseObject(jsonStr);
			if ("200".equals(parseObject.getString("resultcode"))) {
				JSONObject data = parseObject.getJSONObject("result");
				result.put("City", data.getString("city"));
				result.put("province", data.getString("province"));
			}
			return result;
		} catch (Exception e) {
			LOG.error("获取手机归属地异常：{}--{}", e.getMessage(), e);
			return result;
		}
	}
	
}
