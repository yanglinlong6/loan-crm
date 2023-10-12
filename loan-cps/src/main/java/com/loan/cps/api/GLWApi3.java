package com.loan.cps.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.HttpUtil;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.entity.UserAptitudePO;
import com.loan.cps.entity.UserDTO;

@Component
public class GLWApi3 implements ApiSender{
	
	private static Log logger = LogFactory.getLog(GLWApi3.class);
	
	private static final String URL = "http://mgjr.360yhzd.com/api/yhzd/custDataApply?";

	@Override
	public SendResult send(UserAptitudePO po, UserDTO select) {
		Map<String,Object> params = new HashMap<String,Object>();
		String userInfo = WXCenterUtil.getUserInfo(po.getUserId(),"",  "");
		JSONObject parse = JSON.parseObject(userInfo);
		if(StringUtils.isEmpty(po.getName())) {
			if(StringUtils.isEmpty(parse.get("openid"))) {
				po.setName("公众号用户");
			}else {
				po.setName(parse.getString("nickname"));
			}
		}
		params.put("cust_name",po.getName());
		params.put("mobile", po.getMobile());
		params.put("media_source", "");
		params.put("age", 30);
		Integer sex =0 ;
		if(parse.getInteger("sex") == null) {
			sex =0 ;
		}else if(parse.getInteger("sex")==1) {
			sex=1;
		}else if(parse.getInteger("sex")==2){
			sex=2;
		}
		params.put("sex", sex);
		params.put("city", po.getCity().contains("市")?po.getCity():po.getCity()+"市");
		String channel = "wxgzh01";
		if(select.getWxType()==7) {
			channel = "wxgzh02";
		}
		params.put("channel_source", channel);
		Integer house = 0;
		if(po.getHouse()==1||po.getHouse()==2) {
			house =1;
		}
		params.put("has_house", house);
		Integer car = 0;
		if(po.getCar()==1) {
			car =1;
		}
		params.put("has_car", car);
		Integer company = 0;
		if(po.getCompany()==1) {
			company=1;
		}
		params.put("has_company",company);
		params.put("has_credit", 0);
		Integer insurance = 0;
		if(po.getInsurance()==1) {
			insurance=1;
		}
		params.put("has_policy",insurance);
		params.put("has_social", 0);
		Integer accumulationFund = 0;
		if(Pattern.matches("^\\D*\\d+-\\d+\\D*$", po.getPublicFund())||Pattern.matches("^\\D*\\d+\\D*$", po.getPublicFund())||Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", po.getPublicFund())) {
			accumulationFund =1;
		}
		params.put("has_fund", accumulationFund);
		Integer wages = 0;
		if(po.getGetwayIncome().equals(1)) {
			wages=1;
		}
		params.put("has_work", wages);
		params.put("has_tax", 0);
		params.put("wedebt_limit", 0);
		String amount = po.getLoanAmount();
		if(Pattern.matches("\\d+", amount)) {
			amount = String.valueOf(Integer.valueOf(amount)<50000?50000:amount);
		}else {
			if("《5万以内》".equals(amount)) {
				amount = "50000";
			}else if("《5-20万》".equals(amount)) {
				amount = "100000";
			}else if("《20-50万》".equals(amount)) {
				amount = "300000";
			}else if("《50万以上》".equals(amount)) {
				amount = "1000000";
			}else {
				amount = "30000";
			}
		}
		params.put("apply_limit", amount);
		params.put("remarks", po.getPublicFund());
		String forObject = HttpUtil.getForObject(paramSplicing(params,URL));
		JSONObject parseObject = JSON.parseObject(forObject);
		SendResult result = new SendResult();
		if("0".equals(parseObject.getJSONObject("resMap").getString("errorNo"))) {
			logger.info("send GLW success userId = "+po.getUserId());
			result.setSuccess(true);
		}else {
			logger.info("send GLW fail api result = "+forObject);
		}
		result.setResultMsg(forObject);
		return result;
	}
	
	private String paramSplicing(Map<String,Object> paramsMap,String url) {
		Set<String> keySet = paramsMap.keySet();
		Iterator<String> iterator = keySet.iterator();
		StringBuilder builder = null;
		while (iterator.hasNext()) {
			String next = iterator.next();
			if (builder == null) {
				builder = new StringBuilder(url);
			}else {
				builder.append("&");
			}
			builder.append(next);
			builder.append("=");
			builder.append(paramsMap.get(next));
		}
		String result = "";
		if(builder!=null) {
			result = builder.toString();
		}
		logger.info("send GLW signature = "+result);
		return result;
	}
	
}
