package com.loan.cps.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.SessionScope;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.HttpUtil;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.dao.WechatUserBindDao;
import com.loan.cps.entity.Session;
import com.loan.cps.entity.UserAptitudePO;
import com.loan.cps.entity.UserDTO;
import com.loan.cps.service.SessionService;

@Component
public class Bank51Api implements ApiSender{
	
	private static final String API_SEND_URL = "http://www.51yhdai.com/api.php"; 
	
	@Autowired
	private SessionService sessionService;
	
	private static Log logger = LogFactory.getLog(Bank51Api.class);

	@Override
	public SendResult send(UserAptitudePO po,UserDTO select) {
		String userInfo = WXCenterUtil.getUserInfo(po.getUserId(),"",  "");
		JSONObject parse = JSON.parseObject(userInfo);
		if(StringUtils.isEmpty(po.getName())) {
			if(StringUtils.isEmpty(parse.get("openid"))) {
				po.setName("公众号用户");
			}else {
				po.setName(parse.getString("nickname"));
			}
		}
		JSONObject params = new JSONObject();
		params.put("trafficSources", "zdt"+"-"+select.getWxType()+"-"+select.getMedia());
		params.put("customName",po.getName());
		params.put("mobile", po.getMobile());
		params.put("cityname", po.getCity().contains("市")?po.getCity().replace("市", ""):po.getCity());
		String sex = "未知";
		if(parse.getInteger("sex") == null) {
			sex = "未知";
		}else if(parse.getInteger("sex").equals(1)) {
			sex="男";
		}else if(parse.getInteger("sex").equals(2)){
			sex="女";
		}
		params.put("sex", sex);
		params.put("age", 0);
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
		params.put("amount", amount);
		params.put("loanType", "信用贷");
		String salary = "";
		if(po.getGetwayIncome()==1) {
			salary = "5000";
		}else if(po.getGetwayIncome()==2) {
			salary = "3000";
		}
		params.put("salary", salary);
		params.put("insurance", "2");
		int accumulationFund = 2;
		if(Pattern.matches("^\\D*\\d+-\\d+\\D*$", po.getPublicFund())||Pattern.matches("^\\D*\\d+\\D*$", po.getPublicFund())||Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", po.getPublicFund())) {
			accumulationFund =1;
		}
		params.put("accumulationFund", accumulationFund);
		params.put("creditYears", "0");
		params.put("credit", "2");
		int house = 2;
		if(po.getHouse()==1||po.getHouse()==2) {
			house =1;
		}
		params.put("house", house);
		int car = 2;
		if(po.getCar()==1) {
			car =1;
		}
		params.put("car", car);
		params.put("carLoan", "无");
		params.put("houseLoan", "无");
		String insurancePolicy = "无";
		if( po.getInsurance() ==1) {
			insurancePolicy = "有半年以上保单";
		}
		params.put("insurancePolicy", insurancePolicy);
		SimpleDateFormat dataformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		params.put("applyDate",dataformat.format(po.getUpdateDate()) );
		params.put("credit_score",po.getZhimaScore());
		params.put("creditQuota","0");
		params.put("businessLicense",po.getCompany() );
		params.put("column1","0" );
		params.put("column2","无");
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		array.add(params);
		obj.put("thirdCustomerResult", array);
		LinkedMultiValueMap<String,String> paramsMap= new LinkedMultiValueMap<String,String>();
		paramsMap.add("Obj", obj.toJSONString());
		logger.info("send bank51 params = "+JSON.toJSONString(paramsMap));
		String postForObject = HttpUtil.postForObject(API_SEND_URL, paramsMap);
		JSONObject parseObject = JSON.parseObject(postForObject);
		SendResult result = new SendResult();
		if("00".equals(parseObject.getString("code"))) {
			logger.info("send bank51 success userId = "+po.getUserId());
			result.setSuccess(true);
		}else {
			logger.info("send fail api result = "+postForObject);
		}
		result.setResultMsg(postForObject);
		return result;
	}
	
}
