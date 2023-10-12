package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Component("apiSender_10045")
public class SpiderApi
        implements ApiSender {
    private static final Logger logger = LoggerFactory.getLogger(SpiderApi.class);
    private static final String URL = "http://8.129.22.74:8080/spider/customer/addCustomer";
    private static final String REPEAT_URL = "http://8.129.22.74:8080/spider/customer/hasPhone?md5Phone=%s";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    public SendResult send(UserAptitudePO po, UserDTO select) {

        String forObject = HttpUtil.getForObject(String.format(REPEAT_URL, md5(po.getMobile())));
        if("true".equals(forObject)) {
            logger.info("【深圳蜘蛛：】号码-{}，重复：{} ",po.getMobile(),forObject);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【深圳蜘蛛：】重复手机号"));
            return new SendResult(false,"【深圳蜘蛛：】重复手机号");
        }
        isHaveAptitude(po);
        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap();
        if(StringUtils.isBlank(po.getName()) && null != select) {
            JSONObject parse = JSON.parseObject(WechatCenterUtil.getUserInfo(po.getUserId(), "", ""));
            if(StringUtils.isBlank(parse.getString("openid"))) {
                po.setName("公众号用户");
            } else {
                po.setName(EmojiFilter.filterEmoji(parse.getString("nickname")));
            }
        }
        params.add("name", po.getName());
        params.add("mobile", po.getMobile());
        params.add("age", po.getAge());
        String sex = "男";
        if(po.getGender() == 1) {
            sex = "男";
        } else if(po.getGender() == 2) {
            sex = "女";
        }
        params.add("sex", sex);
        params.add("city", po.getCity().contains("市") ? po.getCity().substring(0,po.getCity().length()-1) : po.getCity());
        String house = "false";
        if((po.getHouse().intValue() == 1) || (po.getHouse().intValue() == 2)) {
            house = "true";
        }
        params.add("house", house);
        String car = "false";
        if(JudgeUtil.in(po.getCompany(),1,2)) {
            car = "true";
        }
        params.add("car", car);
        String insurance = "false";
        if(JudgeUtil.in(po.getInsurance(),1,2)) {
            insurance = "true";
        }
        params.add("baodan_is", insurance);
        params.add("shebao", "false");
        String accumulationFund = "false";
        if(po.getPublicFund().contains("有，")) {
            accumulationFund = "true";
        }
        params.add("gongjijin", accumulationFund);
        String wages = "false";
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)) {
            wages = "true";
        }
        params.add("isbankpay", wages);
        params.add("money", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        params.add("source", "道分");
        params.add("qualification", getQualification(po));

        SendResult result = new SendResult();
        String postForObject = HttpUtil.postFormForObject(URL, params);
        JSONObject obj = JSON.parseObject(postForObject);
        if("0".equals(obj.getJSONObject("messageModel").getString("code"))) {
            logger.info("【深圳蜘蛛：】api success userid =  " + po.getUserId());
            result.setSuccess(true);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【深圳蜘蛛：】success"));
        } else {
            logger.info("【深圳蜘蛛：】 api fail result =  " + postForObject);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【深圳蜘蛛：】失败："+postForObject));
        }
        result.setResultMsg(postForObject);
        try {
            Thread.sleep(1000);
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
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
        if(po.getCar().intValue() == 1) {
            car = "本地车，全款";
        } else if(po.getCar().intValue() == 2) {
            car = "本地车，按揭";
        } else if(po.getCar().intValue() == 4) {
            car = "外地车";
        } else if(po.getCar().intValue() == 3) {
            car = "无";
        }
        builder.append(car);
        builder.append(";");
        builder.append("房:");
        String house = "未知";
        if(po.getHouse().intValue() == 1) {
            if(po.getHouseState().intValue() == 1) {
                house = "本地商品房，工作所在地,全款房";
            } else if(po.getHouseState().intValue() == 2) {
                house = "本地商品房，工作所在地,按揭房";
            } else if(po.getHouseState().intValue() == 3) {
                house = "本地商品房，工作所在地,抵押房";
            } else {
                house = "本地商品房，工作所在地";
            }
        } else if(po.getHouse().intValue() == 2) {
            if(po.getHouseState().intValue() == 1) {
                house = "外地商品房，工作所在地,全款房";
            } else if(po.getHouseState().intValue() == 2) {
                house = "外地商品房，工作所在地,按揭房";
            } else if(po.getHouseState().intValue() == 3) {
                house = "外地商品房，工作所在地,抵押房";
            } else {
                house = "外地商品房";
            }
        } else if(po.getHouse().intValue() == 4) {
            house = "自建房产";
        } else if(po.getHouse().intValue() == 3) {
            house = "无";
        }
        builder.append(house);
        builder.append(";");
        String insurance = "未知";
        if(po.getInsurance().intValue() == 1) {
            insurance = "年交保费合计大于1万以上，已缴费两年以上";
        } else if(po.getInsurance().intValue() == 2) {
            insurance = "年交保费合计小于1万以下，已缴费两年以下";
        } else if(po.getInsurance().intValue() == 3) {
            insurance = "无";
        }
        builder.append("保险:");
        builder.append(insurance);
        builder.append(";");
        builder.append("芝麻分:");
        builder.append(po.getZhimaScore()==null?"位置":po.getZhimaScore());
        builder.append(";");
        builder.append("营业执照:");
        String company = "未知";
        if(po.getCompany() == 1) {
            company = "有，1年以上，并且有开票缴税";
        }else {
            company = "无";
        }
        builder.append(company);
        return builder.toString();
    }

    private static String md5(String param) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] digest = instance.digest(param.getBytes());
            return Hex.encodeHexString(digest);
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

//
//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("3a1d24ac4761473abf9f4017b40288f6");
//        po.setName("测试林先森3");
//        po.setHouse(0);
//        po.setAge(35);
//        po.setCity("上海市");
//        po.setCompany(1);
//        po.setInsurance(0);
//        po.setLoanAmount("《10-30万》");
//        po.setMobile("13632965171");
//        po.setOccupation(1);
//        po.setPublicFund("无");
//        po.setOccupation(1);
//        po.setGetwayIncome(1);
//        po.setCreditCard(1);
//        po.setCar(1);
//        po.setWeight(0.012231);
//        po.setUpdateDate(new Date());
//        po.setGender(1);
//        SpiderApi api = new SpiderApi();
//        System.out.println(api.send(po,null));
//
//    }
}
