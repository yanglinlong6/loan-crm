package com.loan.cps.api;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.api.jiejiedai.RSAUtils;
import com.loan.cps.common.HttpUtil;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.entity.UserAptitudePO;
import com.loan.cps.entity.UserDTO;

public class JJDApi implements ApiSender{
	
	private static final String JJD_URL = "http://www.jiejie888.com:8080/jjd/api";
	
	private static final String JJD_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJbX3nbQL1/REa8Gw2s0gcaRYPpfsGiSOGLB4PFSGzjAf6rK8ntw"
												+ "3YFJUFpAAtIklh/NR83IkWFCa3rT+ADMKFdWroWX5AUO3ZpMiZj+yXLajoHxg+WO1K9gW6unOXCEYvfpeeI7TFyA5OPiNM3LcO6/"
												+ "FCSv2eqEhvpKAlj9htgbAgMBAAECgYAb4m3ygsuvJ8PgGhz37W3sGiXWQugmfJlF0FdX5Pi5RgSTuw6WOSgJZVcgR+L9m9vQpy8V"
												+ "4HTXiOK7gwG+i/8M1HyGapZjaEY+zxSXP9b5U4Gg/034xFnlrnbf+VGYK14SjxnT/L7rjnrBdAJy1dFlwPfXC00AL3FBb5P3dbzl"
												+ "qQJBAOi1rn9WuzY9v9zUFGiuwz8s2p3pqSBqqy7cWblqfGKBmKiSHN9A8SI7yCdxzBUsuBI5biUpwvVmHF+c9mheWD8CQQCl8Kol"
												+ "8/aZDJHS7lTtXq50q7TfcmyPfbBBCbJS3Xs4+KOAoLTCfSOfRaWc+CdsxL2GQcSR1FH6/fCu7OVzviklAkBPkff1sKlY9apKDvZf"
												+ "iQpX6rVh84iR/gdEgbHoVYHsiNWzJirbJ7CL/RcGbcHTp7PU/3ArIul30Y94HgqfS0svAkA2cM+RpgloSuxolFJ+kcqQcmjjygl9"
												+ "xhGWNNUxRIKRvVj+8Tp8eEvAjDv6VJuynpVDP756zXqb6sYUzZm1sFSVAkEA2TUZPmhRDxllFwyyp+FvH5t2p90IBqWs4FBG+O8I"
												+ "vyd3vHIKk1Esr3FJYzhNjtCMDBDeGrhpvz5w1gBfUEGcfg==";
	
	private static final String JJD_PHONE_MATCH_API = "jjd.api.phone.match";
	
	private static final String JJD_CUST_IMPORT_API = "jjd.api.customer.import";
	
	private static final String JJD_FORMAT = "json";
	
    private static final String JJD_SIGN_TYPE = "RSA";
    
    private static final String JJD_UID = "8299d03771c2497884acffa036e02539";
    
    private static final String JJD_SOURCE = "zdt";
    
    private static final String JJD_SUCCESS_CODE = "1";
    
    private static Log logger = LogFactory.getLog(JJDApi.class);

	@Override
	public SendResult send(UserAptitudePO po, UserDTO select) {
		JSONObject o = new JSONObject();
		o.put("phone", po.getMobile());
		String sendJJDApi = sendJJDApi(o,JJD_PHONE_MATCH_API);
		JSONObject parseObject = JSON.parseObject(sendJJDApi);
		SendResult result = new SendResult();
		if(JJD_SUCCESS_CODE.equals(parseObject.getString("code"))) {
			String userInfo = WXCenterUtil.getUserInfo(po.getUserId(),"",  "");
			JSONObject parse = JSON.parseObject(userInfo);
			if(StringUtils.isEmpty(po.getName())) {
				if(StringUtils.isEmpty(parse.get("openid"))) {
					po.setName("公众号用户");
				}else {
					po.setName(parse.getString("nickname"));
				}
			}
			o.put("username", po.getName());
			o.put("source", JJD_SOURCE);
			String sex = "1";
			if(parse.getInteger("sex") == null) {
				sex = "1";
			}else if(parse.getInteger("sex").equals(1)) {
				sex="1";
			}else if(parse.getInteger("sex").equals(2)){
				sex="2";
			}
			o.put("sex", sex);
			String amount = po.getLoanAmount();
			if(Pattern.matches("\\d+", amount)) {
				amount = String.valueOf(Integer.valueOf(amount)<50000?5:Integer.valueOf(amount)/10000);
			}else {
				if("《5万以内》".equals(amount)) {
					amount = "5";
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
			o.put("amount", amount);
			o.put("customerIp", "0");
			o.put("cityName", po.getCity());
			if(po.getOccupation()!=null&&po.getOccupation()!=0) {
				if(po.getOccupation()==1||po.getOccupation()==3) {
					o.put("identityType", 1);
				}else if(po.getOccupation()==2) {
					o.put("identityType", 2);
				}else if(po.getOccupation()==4) {
					o.put("identityType",4);
				}
			}
			Integer house = 1;
			if(po.getHouse()==1||po.getHouse()==2) {
				house =3;
			}
			o.put("houseInfo",house);
			Integer car = 1;
			if(po.getCar()==1||po.getCar()==4) {
				car = 2;
			}
			o.put("carInfo",car);
			int accumulationFund = 0;
			if(Pattern.matches("^\\D*\\d+-\\d+\\D*$", po.getPublicFund())||Pattern.matches("^\\D*\\d+\\D*$", po.getPublicFund())||Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", po.getPublicFund())) {
				accumulationFund =1;
			}
			o.put("isHasPublicFund",accumulationFund);
			Integer insurancePolicy = 1;
			if( po.getInsurance() ==1) {
				insurancePolicy = 2;
			}
			o.put("warranty",insurancePolicy);
			Integer wages = 2;
			if(po.getGetwayIncome().equals(1)) {
				wages=1;
				o.put("cashIncome",8000);
			}else if(po.getGetwayIncome().equals(2)) {
				wages=1;
				o.put("cashIncome",4000);
			}
			o.put("workerWagesPay",wages);
			String addCust = sendJJDApi(o,JJD_CUST_IMPORT_API);
			JSONObject parseObject2 = JSON.parseObject(addCust);
			if(JJD_SUCCESS_CODE.equals(parseObject2.getString("code"))) {
				logger.info("send JJD SUCCESS userid = "+ po.getUserId());
				result.setSuccess(true);
			}else {
				logger.info("send JJD failed userid = "+ po.getUserId()+" result = "+addCust);
			}
			result.setResultMsg(addCust);
		}else {
			logger.info("send JJD failed mobile repeat");
			result.setResultMsg(sendJJDApi);
		}
		return result;
	}
	
	private static String sendJJDApi(JSONObject o,String method){
		LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<String,Object>();
		String data = "";
		try {
			PrivateKey pk = RSAUtils.getPrivateKey(JJD_PRIVATE_KEY);
			data = RSAUtils.encrypt(o.toJSONString(),pk);
		} catch (Exception e) {
			e.printStackTrace();
		}
		params.add("data", data);
        params.add("method", method);
        params.add("dataEnc", "1");
        params.add("uid", JJD_UID);
        params.add("signType", JJD_SIGN_TYPE);
        params.add("format", JJD_FORMAT);
        params.add("timestamp", String.valueOf(new Date().getTime()/1000));
        String sign= "";
		try {
			sign = RSAUtils.sign(paramSplicing(params), RSAUtils.getPrivateKey(JJD_PRIVATE_KEY));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        params.add("sign", sign);
        return HttpUtil.postFormForObject(JJD_URL, params);
	}
	
	private static String paramSplicing(LinkedMultiValueMap<String,Object> paramsMap) {
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
		return result;
	}
	
//	public static void main(String[] args) {
//		UserAptitudePO po = new UserAptitudePO();
//		po.setUserId("37cc0ed9ed52476cb54d85cfe35f4406");
//		po.setCar(4);
//		po.setHouse(2);
//		po.setCity("深圳市");
//		po.setCompany(1);
//		po.setGetwayIncome(1);
//		po.setInsurance(2);
//		po.setLoanAmount("《5万以内》");
//		po.setMobile("13049692800");
//		po.setName("测试");
//		po.setOccupation(1);
//		po.setPublicFund("有，个人月缴300-500元");
//		JJDApi api = new JJDApi();
//		SendResult send = api.send(po, null);
//		System.out.println(JSON.toJSONString(send));
//	}

}
