package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 全心贷：apiSender_10033
 */
@Component("apiSender_10033")
public class QuanXinDaiApi implements ApiSender {
    private static Logger LOG = LoggerFactory.getLogger(QuanXinDaiApi.class);

    private static final String checkUrl="https://admin.qxd-wh.com/home/Api/checkTel";

    private static final String token = "UWDSdzJHCq3KnrLwuBaPBBGX8Hs0oEY3";

    private static final String URL = "http://47.111.167.41:8009/customer/import";

    private static final String  vender_name = "daofen";
    private static final String appid = "20009";
    private static final String appkey = "zVzynUgd6m9T6pFpPYGv9lcuvpYz58tl";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            isHaveAptitude(po);
            return send2(po,select);
        }catch (Exception e){
            LOG.error("【全心贷】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,e.getMessage()));
            return new SendResult(false,""+e.getMessage());
        }
    }

    private SendResult send2(UserAptitudePO po, UserDTO select){

        // 撞库操作
        String md5Mobile= MD5Util.getMd5String(po.getMobile());
        String url = checkUrl+"?token="+token+"&tel="+md5Mobile;
        String checkResult = HttpUtil.getForObject(url);
        LOG.info("[全心贷-武汉]撞库结果:{}",checkResult);
        int state = JSONUtil.toJSON(checkResult).getJSONObject("data").getIntValue("state");
        if(0 != state){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,checkResult));
            return new SendResult(false,"[全心贷-武汉]分发重复:"+checkResult);
        }

        LinkedMultiValueMap<String, Object> paramsMap = new LinkedMultiValueMap<String, Object>();
        if(StringUtils.isBlank(po.getName()) && null != select){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(StringUtils.isBlank(po.getName())) {
                if(StringUtils.isBlank(parse.getString("openid"))) {
                    po.setName("公众号用户");
                } else {
                    po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
                }
            }
        }
        paramsMap.add("vender_name", vender_name);
        paramsMap.add("channel", vender_name);
        paramsMap.add("name", po.getName());
        paramsMap.add("city", po.getCity().replace("市", ""));
        String sex = "男";
        if(po.getGender() == null) {
            sex = "男";
        } else if(po.getGender() == 1) {
            sex = "男";
        } else if(po.getGender() == 2) {
            sex = "女";
        }
        paramsMap.add("sex", sex);
        paramsMap.add("telphone", po.getMobile());
        paramsMap.add("birthday", "1985-01-01");
        String vocation = "工薪族";
        if(po.getOccupation() == 2) {
            vocation = "小企业主";
        }
        paramsMap.add("vocation", vocation);
        String salary = "0-1999";
        if(po.getGetwayIncome() == 1) {
            salary = "4000-5999";
        }
        paramsMap.add("salary", salary);
        paramsMap.add("loan_amount", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        if(JudgeUtil.in(po.getCreditCard(),1,2)){
            paramsMap.add("have_credit_card"," Y");
        }else{
            paramsMap.add("have_credit_card","N");
        }

        SimpleDateFormat dataformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        paramsMap.add("loan_time", dataformat.format(new Date()));
        String house = "N";
        if(po.getHouse() == 1 || po.getHouse() == 2) {
            house = "Y";
        }
        paramsMap.add("have_house", house);
        String car = "N";
        if(po.getCar() == 1) {
            car = "Y";
        }
        paramsMap.add("have_car", car);
        String accumulationFund = "N";
        if(Pattern.matches("^\\D*\\d+-\\d+\\D*$", po.getPublicFund()) || Pattern.matches("^\\D*\\d+\\D*$", po.getPublicFund()) || Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", po.getPublicFund())) {
            accumulationFund = "Y";
        }
        paramsMap.add("have_fund", accumulationFund);
        String baoxian = "N";
        if(JudgeUtil.in(po.getInsurance(),1,2)) {
            baoxian = "Y";
        }
        paramsMap.add("have_baoxian", baoxian);
        if(JudgeUtil.in(po.getGetwayIncome(),1,2))
            paramsMap.add("salary_method", 2);
        else paramsMap.add("salary_method", 0);
        String noncestr = createUUID();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        paramsMap.add("appid", appid);
        paramsMap.add("timestamp", timestamp);
        paramsMap.add("noncestr", noncestr);
        paramsMap.add("appkey", appkey);
        paramsMap.add("signature", sha1(paramSplicing(paramsMap)));
        paramsMap.remove(appkey);
        System.out.println(JSONObject.toJSONString(paramsMap));
        String postFormForObject = HttpUtil.postFormForObject(URL, paramsMap);
        LOG.info("【全心贷】分发结果：result ={} " ,postFormForObject);
        JSONObject parseObject = JSON.parseObject(postFormForObject);
        SendResult result = new SendResult();
        if("1".equals(parseObject.getString("resStatus"))) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,postFormForObject));
            result.setSuccess(true);
        } else if("2".equals(parseObject.getString("resStatus"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,postFormForObject));
        }else {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,postFormForObject));
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
        } catch(NoSuchAlgorithmException e) {
            LOG.error("【全心贷】分发加密异常：{}",e.getMessage(),e);
        }
        return null;
    }

    private String paramSplicing(LinkedMultiValueMap<String, Object> paramsMap) {
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
        LOG.info("send 全心贷 signature = " + result);
        return result;
    }
//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
//        po.setName("海燕测试武汉");
//        po.setMobile("13671948204");
//        po.setCity("武汉市");
//        po.setLoanAmount("《3-10万》");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setCompany(0);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setUpdateDate(new Date());
//        ApiSender  api = new QuanXinDaiApi();
//        System.out.println(api.send(po,null));
//    }


}
