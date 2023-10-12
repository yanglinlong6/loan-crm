package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.RSAUtils;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

import java.security.PrivateKey;
import java.util.*;

/**
 * 深圳永捷信息技术有限公司：捷捷贷
 */
@Component("apiSender_10005")
public class JJDshanghaiApi implements ApiSender {

    private static final String JJD_URL = "http://www.jiejie888.com:8080/jjd/api";

    private static final String JJD_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMqK5usTJu/EKqxS"+
    		"RoOPXFrsri8/1rgbLmg802H1/+cBSQiZijAm944dVjL3IJfCwDvoz2c4FR0aqV63"+
    		"gUxve0f8s+3b9pHppN0JS/xE3mkUGhaNJtWNMkHm5lAz+mewHVxh/ut0Pj8muwyI"+
    		"BBVncMvhIQp1a+hSCShcBS9jdTqTAgMBAAECgYAPOU2OEcx1bSs/9Jc0QExS0UNu"+
    		"bb/36qMcVqYU0ObBOEmDCfdnEya8WdwmzIh4n7zPQ/qC6aY8n8pnQifH1QFRD5Uo"+
    		"YTbemCutA6hZPiAzDiL8y0RKT3WEr6KG/87Lyy7Q03jYBCXkyUCx4Kc7dDv1PPCi"+
    		"3gyJ55ru1sBDZyqi4QJBAPF9+uyktPKZvl/f7GPMBX05SgktUprCesYjsEv6Jk3d"+
    		"/5G7ZUZ2pT4yRbvtql65kPE0Mt5Iew3wJfJ4xQs9QJsCQQDWteYhj5zH8gJSQEHZ"+
    		"TjIzgspR0e7ZFafi9TWp9eHdlzCf8xo9RWwDEMXlSXI7x64N3a1bF8nDe4UUQwbn"+
    		"wWFpAkAVmqajGw0REw4D971nJC5tiH2GGqbkL3faWAECmwtoNe8SM4iY6C5aRRcV"+
    		"++oc9U7xNTFDz4oqsgxDB+jg24b3AkBwdhBXLdjJ5doPaPbUsp+a2XvNhW9cp3or"+
    		"Z5K3NkIbBfEDJpts0mrz5BozsdeytjFVs/H0T65vxMf8x+rbAzBZAkEAqZF8S8xG"+
    		"8dA2IbJOWaRhj9NDbNQ6BtQcQpKsf5aQLs7dUpVGF0xjuCWHP4WLhvi/Sy8Yy1zZ"+
    		"J4zALhAn2cyF5A==";

    private static final String JJD_PHONE_MATCH_API = "jjd.api.phone.match";

    private static final String JJD_CUST_IMPORT_API = "jjd.api.customer.import";

    private static final String JJD_FORMAT = "json";

    private static final String JJD_SIGN_TYPE = "RSA";

    private static final String JJD_UID = "2a220424b5944f36bf53a192a7bbd52b";

    private static final String JJD_SOURCE = "fx";

    private static final String JJD_SUCCESS_CODE = "1";

    private static Logger log = LoggerFactory.getLogger(JJDshanghaiApi.class);

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【捷捷贷-上海】分发异常",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,e.getMessage()));
            return new SendResult(false,"捷捷贷-上海分发异常："+e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        JSONObject o = new JSONObject();
        o.put("phone", po.getMobile());
        String sendJJDApi = sendJJDApi(o, JJD_PHONE_MATCH_API);
        JSONObject parseObject = JSON.parseObject(sendJJDApi);
        SendResult result = new SendResult();
        if(JJD_SUCCESS_CODE.equals(parseObject.getString("code"))) {
            if(StringUtils.isEmpty(po.getName()) && null != select){
                String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
                JSONObject parse = JSON.parseObject(userInfo);
                if(StringUtils.isEmpty(po.getName())) {
                    if(StringUtils.isEmpty(parse.get("openid"))) {
                        po.setName("公众号用户");
                    } else {
                        po.setName(EmojiFilter.filterEmoji(parse.getString("nickname")));
                    }
                }
            }
            o.put("username", po.getName());
            o.put("source", JJD_SOURCE);
            String sex = "1";
            if(po.getGender() == null) {
                sex = "1";
            } else if(po.getGender().equals(1)) {
                sex = "1";
            } else if(po.getGender().equals(2)) {
                sex = "2";
            }
            o.put("sex", sex);
            o.put("amount", LoanAmountUtil.transformToWan(po.getLoanAmount()));
            o.put("customerIp", "0");
            o.put("cityName", po.getCity());
            if(po.getOccupation() != null && po.getOccupation() != 0) {
                if(po.getOccupation() == 1 || po.getOccupation() == 3) {
                    o.put("identityType", 1);
                } else if(po.getOccupation() == 2) {
                    o.put("identityType", 2);
                } else if(po.getOccupation() == 4) {
                    o.put("identityType", 4);
                }
            }
            o.put("houseInfo", JudgeUtil.in(po.getHouse(),1,2)?3:1);
            o.put("carInfo", JudgeUtil.in(po.getCar(),1,2)?2:3);
            o.put("isHasPublicFund", po.getPublicFund().contains("有，")?2:1);
            o.put("warranty", JudgeUtil.in(po.getInsurance(),1,2)?1:3);
            o.put("hasCompany",JudgeUtil.in(po.getCompany(),1)?2:1);
            Integer wages = 2;
            if(po.getGetwayIncome().intValue() ==1) {
                wages = 1;
                o.put("cashIncome", 8000);
            } else if(po.getGetwayIncome().intValue() ==2) {
                wages = 1;
                o.put("cashIncome", 4000);
            }
            o.put("workerWagesPay", wages);
            String addCust = sendJJDApi(o, JJD_CUST_IMPORT_API);
            JSONObject parseObject2 = JSON.parseObject(addCust);
            if(JJD_SUCCESS_CODE.equals(parseObject2.getString("code"))) {
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,sendJJDApi));
                log.info("send JJD SUCCESS userid :{}" ,po.getUserId());
                result.setSuccess(true);
            } else {
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,sendJJDApi));
                log.info("send JJD failed userid = {}, result = -{}" ,po.getUserId(), addCust);
            }
            result.setResultMsg(addCust);
        } else {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,sendJJDApi));
            log.info("send JJD failed mobile repeat");
            result.setResultMsg(sendJJDApi);
        }
        return result;
    }

    private static String sendJJDApi(JSONObject o, String method) {
        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        String data = "";
        try {
            PrivateKey pk = RSAUtils.getPrivateKey(JJD_PRIVATE_KEY);
            data = RSAUtils.encrypt(o.toJSONString(), pk);
        } catch(Exception e) {
            e.printStackTrace();
        }
        params.add("data", data);
        params.add("method", method);
        params.add("dataEnc", "1");
        params.add("uid", JJD_UID);
        params.add("signType", JJD_SIGN_TYPE);
        params.add("format", JJD_FORMAT);
        params.add("timestamp", String.valueOf(new Date().getTime() / 1000));
        String sign = "";
        try {
            sign = RSAUtils.sign(paramSplicing(params), RSAUtils.getPrivateKey(JJD_PRIVATE_KEY));
        } catch(Exception e) {
            log.error("【捷捷贷-上海】分发，加密异常：{}",e.getMessage(),e);
        }
        params.add("sign", sign);
        return HttpUtil.postFormForObject(JJD_URL, params);
    }

    private static String paramSplicing(LinkedMultiValueMap<String, Object> paramsMap) {
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
        while(iterator.hasNext()) {
            String next = iterator.next();
            if(builder == null) {
                builder = new StringBuilder();
            } else {
                builder.append("&");
            }
            builder.append(next);
            builder.append("=");
            builder.append(paramsMap.get(next).get(0));
        }
        String result = "";
        if(builder != null) {
            result = builder.toString();
        }
        return result;
    }

//    public static void main(String[] args) {
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("fdf8bb58825343cb9f95a642860356e3");
//        po.setName("孔测试");
//        po.setMobile("13049692801");
//        po.setCity("上海市");
//        po.setLoanAmount("50000");
//        po.setCompany(1);
//        po.setPublicFund("有，个人月缴300-500元");
//        po.setCar(3);
//        po.setHouse(4);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(0);
//        JJDApi api = new JJDApi();
//        SendResult send = api.send(po, null);
//        System.out.println(JSON.toJSONString(send));
//    }

}
