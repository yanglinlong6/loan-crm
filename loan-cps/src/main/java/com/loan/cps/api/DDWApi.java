package com.loan.cps.api;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.HttpUtil;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.entity.UserAptitudePO;
import com.loan.cps.entity.UserDTO;

@Component
public class DDWApi implements ApiSender{
	
	private static Log logger = LogFactory.getLog(GLWApi2.class);
	
	private static final String URL = "https://www.duodaiwang.com/spider/customer/addCustomer";
	
	private static final String REPEAT_URL = "https://www.duodaiwang.com/spider/customer/hasPhone?md5Phone=%s";
	
	@Override
	public SendResult send(UserAptitudePO po, UserDTO select) {
		LinkedMultiValueMap<String,Object> params= new LinkedMultiValueMap<String,Object>();
		String userInfo = WXCenterUtil.getUserInfo(po.getUserId(),"",  "");
		JSONObject parse = JSON.parseObject(userInfo);
		if(StringUtils.isEmpty(po.getName())) {
			if(StringUtils.isEmpty(parse.get("openid"))) {
				po.setName("公众号用户");
			}else {
				po.setName(parse.getString("nickname"));
			}
		}
		params.add("name",po.getName());
		params.add("mobile", po.getMobile());
		params.add("age", "30");
		String sex ="男" ;
		if(parse.getInteger("sex") == null) {
			sex ="男" ;
		}else if(parse.getInteger("sex")==1) {
			sex="男" ;
		}else if(parse.getInteger("sex")==2){
			sex="女" ;
		}
		params.add("sex", sex);
		params.add("city", po.getCity().contains("市")?po.getCity().replace("市", ""):po.getCity());
		String house = "false";
		if(po.getHouse()==1||po.getHouse()==2) {
			house ="true";
		}
		params.add("house",house);
		String car = "false";
		if(po.getCar()==1) {
			car ="true";
		}
		params.add("car", car);
		String insurance = "false";
		if(po.getInsurance()==1) {
			insurance="true";
		}
		params.add("baodan_is", insurance);
		params.add("shebao", "false");
		String accumulationFund = "false";
		if(Pattern.matches("^\\D*\\d+-\\d+\\D*$", po.getPublicFund())||Pattern.matches("^\\D*\\d+\\D*$", po.getPublicFund())||Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", po.getPublicFund())) {
			accumulationFund ="true";
		}
		params.add("gongjijin", accumulationFund);
		String wages = "false";
		if(po.getGetwayIncome().equals(1)) {
			wages="true";
		}
		params.add("isbankpay", wages);
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
		params.add("money", amount);
		params.add("source", "I5朋友圈02");
		params.add("qualification", getQualification(po));
		String forObject = HttpUtil.getForObject(String.format(REPEAT_URL, md5(po.getMobile())));
		SendResult result = new SendResult();
		if("true".equals(forObject)) {
			logger.info("send DDW fail api result =多贷网重复手机号 ");
			result.setResultMsg("多贷网重复手机号");
		}else {
			String postForObject = HttpUtil.postFormForObject(URL, params);
			JSONObject obj = JSON.parseObject(postForObject);
			if("0".equals(obj.getJSONObject("messageModel").getString("code"))) {
				logger.info("send DDW api success userid =  "+po.getUserId());
				result.setSuccess(true);
			}else {
				logger.info("send DDW api fail result =  "+postForObject);
			}
			result.setResultMsg(postForObject);
		}
		return result;
	}
	
	private String getQualification(UserAptitudePO po) {
		StringBuilder builder = new StringBuilder("");
		builder.append("公积金:");
		builder.append(po.getPublicFund());
		builder.append(";");
		builder.append("车:");
		String car = "未知";
		if(po.getCar()==1) {
			car = "本地车，全款";
		}else if(po.getCar()==2) {
			car = "本地车，按揭";
		}else if(po.getCar()==4) {
			car = "外地车";
		}else if(po.getCar()==3){
			car = "无";
		}
		builder.append(car);
		builder.append(";");
		builder.append("房:");
		String house = "未知";
		if(po.getHouse()==1) {
			if(po.getHouseState()==1) {
				house = "本地商品房，工作所在地,全款房";
			}else if(po.getHouseState()==2) {
				house = "本地商品房，工作所在地,按揭房";
			}else if(po.getHouseState()==3) {
				house = "本地商品房，工作所在地,抵押房";
			}else {
				house = "本地商品房，工作所在地";
			}
		}else if(po.getHouse()==2) {
			if(po.getHouseState()==1) {
				house = "外地商品房，工作所在地,全款房";
			}else if(po.getHouseState()==2) {
				house = "外地商品房，工作所在地,按揭房";
			}else if(po.getHouseState()==3) {
				house = "外地商品房，工作所在地,抵押房";
			}else {
				house = "外地商品房";
			}
		}else if(po.getHouse()==4) {
			house = "自建房产";
		}else if(po.getHouse()==3){
			house = "无";
		}
		builder.append(house);
		builder.append(";");
		String insurance = "未知";
		if(po.getInsurance()==1) {
			insurance = "年交保费合计大于1万以上，已缴费两年以上";
		}else if(po.getInsurance()==2) {
			insurance = "年交保费合计小于1万以下，已缴费两年以下";
		}else if(po.getInsurance()==3){
			insurance = "无";
		}
		builder.append("保险:");
		builder.append(insurance);
		builder.append(";");
		builder.append("芝麻分:");
		builder.append(po.getZhimaScore());
		builder.append(";");
		builder.append("营业执照:");
		String company = "未知";
		if(po.getInsurance()==1) {
			company = "有，1年以上，并且有开票缴税";
		}else if(po.getInsurance()==2) {
			company = "有，1年以下，并且有开票缴税";
		}else if(po.getInsurance()==3){
			company = "无";
		}
		builder.append(company);
		return builder.toString();
	}
	
	private String md5(String param) {
		try {
			MessageDigest instance = MessageDigest.getInstance("MD5");
			byte[] digest = instance.digest(param.getBytes());
			return Hex.encodeHexString(digest);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
