package com.daofen.crm.base;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.daofen.crm.utils.HttpUtil;

public class MobileLocationUtil {
	
	private static String MOBILE_LOCATION_URL = "https://v.showji.com/Locating/showji.com20180331.aspx?output=json&m=%s";
	
	private static String SUCCESS_RESULT_CODE = "200";

	public static JSONObject get(String mobile) {
		String forObject = HttpUtil.getForObject(String.format(MOBILE_LOCATION_URL, mobile));
		JSONObject parseObject = JSONObject.parseObject(forObject);
		JSONObject result = null;
		if(StringUtils.isNotBlank(parseObject.getString("City"))) {
			result = new JSONObject();
			result.put("city", parseObject.getString("City"));
		}
//		JSONObject jsonObject = parseObject.getJSONObject("responseHeader");
//		if(SUCCESS_RESULT_CODE.equals(jsonObject.getString("status"))) {
//			result = new JSONObject();
//			JSONObject jsonObject2 = parseObject.getJSONObject("response").getJSONObject(mobile).getJSONObject("detail");
//			result.put("city", jsonObject2.getJSONArray("area").getJSONObject(0).getString("city"));
//			result.put("province", jsonObject2.getString("province"));
//			result.put("type", jsonObject2.getString("type"));
//			result.put("operator", jsonObject2.getString("operator"));
//		}
		return result;
	}
	
}
