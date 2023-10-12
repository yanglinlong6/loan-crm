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
public class GLWApi5 implements ApiSender{
	
	private static Log logger = LogFactory.getLog(GLWApi5.class);
	
	private static final String URL = "http://data.pujiangwangshang.com/api/glwtg04/add_customer?";

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
		params.put("name",po.getName());
		params.put("mobile", po.getMobile());
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
		Integer house = 0;
		if(po.getHouse()==1||po.getHouse()==2) {
			house =1;
		}
		params.put("is_house", house);
		Integer car = 0;
		if(po.getCar()==1) {
			car =1;
		}
		params.put("is_car", car);
		Integer company = 0;
		if(po.getCompany()==1) {
			company=1;
		}
		params.put("is_company",company);
		params.put("is_credit", 0);
		Integer insurance = 0;
		if(po.getInsurance()==1) {
			insurance=1;
		}
		params.put("is_insurance",insurance);
		params.put("is_social", 0);
		Integer accumulationFund = 0;
		if(Pattern.matches("^\\D*\\d+-\\d+\\D*$", po.getPublicFund())||Pattern.matches("^\\D*\\d+\\D*$", po.getPublicFund())||Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", po.getPublicFund())) {
			accumulationFund =1;
		}
		params.put("is_fund", accumulationFund);
		Integer wages = 0;
		if(po.getGetwayIncome().equals(1)) {
			wages=1;
		}
		params.put("is_work", wages);
		params.put("is_tax", 0);
		params.put("webank", 0);
		String amount = po.getLoanAmount();
		if(Pattern.matches("\\d+", amount)) {
			amount = String.valueOf(Integer.valueOf(amount)<50000?50000:amount);
		}else {
			amount = amount.replace("《", "").replace("》", "");
		}
		params.put("money_demand", amount);
		params.put("remarks", po.getPublicFund());
		String forObject = HttpUtil.getForObject(paramSplicing(params,URL));
		JSONObject parseObject = JSON.parseObject(forObject);
		SendResult result = new SendResult();
		if("success".equals(parseObject.getString("status"))) {
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
