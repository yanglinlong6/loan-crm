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
public class JXDApi implements ApiSender{
	
	private static Log logger = LogFactory.getLog(Bank51Api.class);
	
	private static final String URL = "http://116.62.140.219:8009/customer/import";
	
	@Autowired
	private SessionService sessionService;

	@Override
	public SendResult send(UserAptitudePO po, UserDTO select) {
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
		paramsMap.add("vender_name", "zdt");
		paramsMap.add("channel", "qian66");
		paramsMap.add("name", po.getName());
		paramsMap.add("city", po.getCity().replace("市", ""));
		String sex = "男";
		if(parse.getInteger("sex") == null) {
			sex =  "男";
		}else if(parse.getInteger("sex")==1) {
			sex="男";
		}else if(parse.getInteger("sex")==2){
			sex="女";
		}
		paramsMap.add("sex", sex);
		paramsMap.add("telphone", po.getMobile());
		paramsMap.add("birthday", "1985-01-01");
		String vocation = "工薪族";
		if(po.getOccupation()==2) {
			vocation = "小企业主";
		}
		paramsMap.add("vocation", vocation);
		String salary = "0-1999";
		if(po.getGetwayIncome()==1) {
			salary = "4000-5999";
		}
		paramsMap.add("salary", salary);
		String amount = po.getLoanAmount();
		if(Pattern.matches("\\d+", amount)) {
			amount = String.valueOf(Integer.valueOf(amount)/10000);
		}else {
			if("《5万以内》".equals(amount)) {
				amount = "3";
			}else if("《5-20万》".equals(amount)) {
				amount = "10";
			}else if("《20-50万》".equals(amount)) {
				amount = "30";
			}else if("《50万以上》".equals(amount)) {
				amount = "100";
			}else {
				amount = "3";
			}
		}
		paramsMap.add("loan_amount", amount);
		SimpleDateFormat dataformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		paramsMap.add("loan_time", dataformat.format(new Date()));
		String house = "N";
		if(po.getHouse()==1||po.getHouse()==2) {
			house ="Y";
		}
		paramsMap.add("have_house", house);
		String car = "N";
		if(po.getCar()==1) {
			car ="Y";
		}
		paramsMap.add("have_car", car);
		String accumulationFund = "N";
		if(Pattern.matches("^\\D*\\d+-\\d+\\D*$", po.getPublicFund())||Pattern.matches("^\\D*\\d+\\D*$", po.getPublicFund())||Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", po.getPublicFund())) {
			accumulationFund ="Y";
		}
		paramsMap.add("have_fund", accumulationFund);
		String baoxian = "N";
		if(po.getInsurance()==1) {
			baoxian = "Y";
		}
		paramsMap.add("have_baoxian", baoxian);
		String appid = "12007";
		String appkey = "kVqZOjv5X1Xhn6UyfvLZLmjt3EtCYPZb";
		String noncestr = createUUID();
		String timestamp = String.valueOf(System.currentTimeMillis()/1000);
		paramsMap.add("appid", appid);
		paramsMap.add("timestamp", timestamp);
		paramsMap.add("noncestr", noncestr);
		paramsMap.add("appkey", appkey);
		paramsMap.add("signature", sha1(paramSplicing(paramsMap)));
		paramsMap.remove(appkey);
		String postFormForObject = HttpUtil.postFormForObject(URL, paramsMap); 
		JSONObject parseObject = JSON.parseObject(postFormForObject);
		SendResult result = new SendResult();
		if("1".equals(parseObject.getString("resStatus"))) {
			logger.info("send JXD success userId = "+po.getUserId());
			result.setSuccess(true);
		}else {
			logger.info("send JXD fail api result = "+postFormForObject);
		}
		result.setResultMsg(postFormForObject);
		return result;
	}
	
	private String createUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	private String sha1(String param) {
		try {
			MessageDigest instance = MessageDigest.getInstance("sha1");
			byte[] digest = instance.digest(param.getBytes());
			return Hex.encodeHexString(digest);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String paramSplicing(LinkedMultiValueMap<String,Object> paramsMap) {
		Set<String> keySet = paramsMap.keySet();
		List<String> sortList = new ArrayList<String>(keySet);
		sortList.sort(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		Iterator<String> iterator = sortList.iterator();
		StringBuilder builder = null;
		while (iterator.hasNext()) {
			String next = iterator.next();
			if (builder == null) {
				builder = new StringBuilder();
			}else {
				builder.append("&");
			}
			builder.append(next);
			builder.append("=");
			builder.append(paramsMap.get(next).get(0));
		}
		String result = "";
		if(builder!=null) {
			result = builder.toString();
		}
		logger.info("send JXD signature = "+result);
		return result;
	}
	
}
