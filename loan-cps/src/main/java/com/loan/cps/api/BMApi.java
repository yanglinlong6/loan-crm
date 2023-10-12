package com.loan.cps.api;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.HttpUtil;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.entity.Session;
import com.loan.cps.entity.UserAptitudePO;
import com.loan.cps.entity.UserDTO;
import com.loan.cps.service.SessionService;

@Component
public class BMApi implements ApiSender{
	
	private static Log logger = LogFactory.getLog(BMApi.class);
	
	private static final String URL = "http://crm.91zhouzhuan.com/api";
	
	private static final String BM_KEY = "7X2qTPqm";
	
	@Override
	public SendResult send(UserAptitudePO po, UserDTO select) {
		String forObject = HttpUtil.getForObject(URL+"?key="+BM_KEY+"&md5_tel="+md5(po.getMobile()));
		JSONObject parseObject2 = JSON.parseObject(forObject);
		SendResult result = new SendResult();
		if("1".equals(parseObject2.getString("success"))) {
			LinkedMultiValueMap<String,Object> paramsMap= new LinkedMultiValueMap<String,Object>();
			String userInfo = WXCenterUtil.getUserInfo(po.getUserId(),"",  "");
			JSONObject parse = JSON.parseObject(userInfo);
			if(StringUtils.isEmpty(po.getName())) {
				if(StringUtils.isEmpty(parse.get("openid"))) {
					po.setName("公众号用户");
				}else {
					po.setName(parse.getString("nickname"));
				}
			}
			paramsMap.add("tel", po.getMobile());
			paramsMap.add("name", po.getName());
			paramsMap.add("city", po.getCity());
			paramsMap.add("age", 30);
			String sex = "0";
			if(parse.getInteger("sex") == null) {
				sex =  "0";
			}else if(parse.getInteger("sex")==1) {
				sex="1";
			}else if(parse.getInteger("sex")==2){
				sex="2";
			}
			paramsMap.add("sex", sex);
			String amount = po.getLoanAmount();
			if(Pattern.matches("\\d+", amount)) {
				amount = String.valueOf(Integer.valueOf(amount)/10000)+"万元";
			}else {
				amount = amount.replace("《", "").replace("》", "");
			}
			paramsMap.add("need_quota", amount);
			paramsMap.add("zm_point", po.getZhimaScore());
			String car = "无";
			if(po.getCar()==1) {
				car ="全款本地车";
			}else if(po.getCar()==2) {
				car ="按揭本地车";
			}else if(po.getCar()==4) {
				car ="全款外地车";
			}
			paramsMap.add("car", car);
			String house = "无";
			if(po.getHouse()==1) {
				house ="本地商品房";
			}else if(po.getHouse()==2) {
				house ="外地商品房";
			}else if(po.getHouse()==4) {
				house ="自建房";
			}
			paramsMap.add("house", house);
			String accumulationFund = "无";
			if(Pattern.matches("^\\D*\\d+-\\d+\\D*$", po.getPublicFund())||Pattern.matches("^\\D*\\d+\\D*$", po.getPublicFund())||Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", po.getPublicFund())) {
				accumulationFund ="有";
			}
			paramsMap.add("funds", accumulationFund);
			String baoxian = "无";
			if(po.getInsurance()==1) {
				baoxian = "有";
			}
			paramsMap.add("insurance", baoxian);
			String wages = "无";
			if(po.getGetwayIncome().equals(1)) {
				wages="有";
			}else if(po.getGetwayIncome().equals(2)) {
				wages="有";
			}
			paramsMap.add("wages",wages);
			String postFormForObject = HttpUtil.postFormForObject(URL+"?key="+BM_KEY, paramsMap);
			JSONObject parseObject = JSON.parseObject(postFormForObject);
			if("1".equals(parseObject.getString("success"))) {
				logger.info("send BM SUCCESS userid = "+ po.getUserId());
				result.setSuccess(true);
			}else {
				logger.info("send BM failed userid = "+ po.getUserId()+" result = "+postFormForObject);
			}
			result.setResultMsg(postFormForObject);
		}else {
			logger.info("send BM failed mobile repeat");
			result.setResultMsg(forObject);
		}
		return result;
	}
	
	private String md5(String param) {
		try {
			MessageDigest instance = MessageDigest.getInstance("md5");
			byte[] digest = instance.digest(param.getBytes());
			return Hex.encodeHexString(digest);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
//	public static void main(String[] args) {
//	UserAptitudePO po = new UserAptitudePO();
//	po.setUserId("37cc0ed9ed52476cb54d85cfe35f4406");
//	po.setCar(4);
//	po.setHouse(2);
//	po.setCity("深圳市");
//	po.setCompany(1);
//	po.setGetwayIncome(1);
//	po.setInsurance(2);
//	po.setLoanAmount("《5万以内》");
//	po.setMobile("13049692800");
//	po.setName("测试");
//	po.setOccupation(1);
//	po.setPublicFund("有，个人月缴300-500元");
//	BMApi api = new BMApi();
//	SendResult send = api.send(po, null);
//	System.out.println(JSON.toJSONString(send));
//	}
}
