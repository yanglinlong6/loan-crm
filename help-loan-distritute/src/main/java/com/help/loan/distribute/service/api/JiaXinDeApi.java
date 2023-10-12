package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 嘉信德： apiSender_20015
 */
@Component("apiSender_20015")
public class JiaXinDeApi implements ApiSender {

    private static Logger LOG = LoggerFactory.getLogger(JiaXinDeApi.class);

    private static final String checkUrl = "http://116.62.140.219:8009/home/Push/check";

    private static final String URL = "http://116.62.140.219:8009/home/customer/import";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send2(po,select);
        }catch (Exception e){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【嘉信德】分发异常：{}"+e.getMessage()));
            LOG.error("【嘉信德】分发异常：{}",e.getMessage(),e);
            return new SendResult(false,""+e.getMessage());
        }
    }

    private SendResult send2(UserAptitudePO po, UserDTO select){

        Map<String,String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

        JSONObject data = new JSONObject();
        data.put("tel", MD5Util.getMd5String(po.getMobile()));
        String result = HttpClientProxy.doPost(checkUrl, data, "UTF-8", 3000, headers);
        LOG.info("【嘉信德-北京】撞库结果：{}",result);
        if(200 != JSONUtil.toJSON(result).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【嘉信德】分发重复："+result));
            return new SendResult(false,"【嘉信德】撞库："+result);
        }

        isHaveAptitude(po);
        LinkedMultiValueMap<String, Object> paramsMap = new LinkedMultiValueMap<String, Object>();
        if(org.apache.commons.lang3.StringUtils.isBlank(po.getName()) && null != select){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(StringUtils.isEmpty(po.getName())) {
                if(StringUtils.isEmpty(parse.get("openid"))) {
                    po.setName("公众号用户");
                } else {
                    po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
                }
            }
        }

        paramsMap.add("vender_name", "meiti202");
        if(org.apache.commons.lang3.StringUtils.isNotBlank(po.getChannel()) && po.getChannel().startsWith("tt")){
            paramsMap.add("channel","头条");
        }else{
            paramsMap.add("channel","朋友圈");
        }
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
        String appkey = "XnfOkO2Nm9CDDM6vcbJdg1BDPedp36BD";
        String noncestr = createUUID();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        paramsMap.add("appid", "12091");
        paramsMap.add("timestamp", timestamp);
        paramsMap.add("noncestr", noncestr);
        paramsMap.add("appkey", appkey);
        paramsMap.add("signature", sha1(paramSplicing(paramsMap)));
        paramsMap.remove(appkey);
        String postFormForObject = HttpUtil.postFormForObject(URL, paramsMap);
        JSONObject parseObject = JSON.parseObject(postFormForObject);
        int status = parseObject.getIntValue("resStatus");
        SendResult sendResult = new SendResult();
        if(1 == status) {
            LOG.info("嘉信德分发成功：userId ={} " , po.getUserId());
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,postFormForObject));
            sendResult.setSuccess(true);
        } else if(2 == status){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,postFormForObject));
        }else {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,postFormForObject));
            LOG.info("嘉信德分发失败：result ={} " ,postFormForObject);
        }
        sendResult.setResultMsg(postFormForObject);
        return sendResult;
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
            LOG.error("【嘉信德】分发加密异常：{}",e.getMessage(),e);
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
        LOG.info("send JXD signature = " + result);
        return result;
    }


//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("9e7710ddd0dc49c59dc3468214be44d1");
//        po.setName("测试伍散人1");
//        po.setHouse(1);
//        po.setCity("北京市");
//        po.setCompany(1);
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//        po.setLoanAmount("《30-50万》");
//        po.setMobile("18229491953");
//        po.setOccupation(1);
//        po.setPublicFund("有，个人月缴500元元");
//        po.setCreditCard(1);
//        po.setCar(1);
//        po.setGender(1);
//        po.setAge(33);
//        po.setUpdateDate(new Date());
//        JiaXinDeApi api = new JiaXinDeApi();
//        System.out.println(api.send(po,null));
//    }

}
