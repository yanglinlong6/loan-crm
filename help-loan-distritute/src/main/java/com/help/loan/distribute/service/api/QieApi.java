package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 *  企鹅:
 */
public class QieApi implements ApiSender {
    private static Logger LOG = LoggerFactory.getLogger(QieApi.class);

    private static final String URL = "http://101.37.76.253:8009/customer/import";
    private static final String  vender_name = "bzwy";
    private static final String appid = "20014";
    private static final String appkey = "ZWMZgZ7K4PLlCls2JB0xPeGhmfr6fXve";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            isHaveAptitude(po);
            return send2(po,select);
        }catch (Exception e){
            LOG.error("【企鹅】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【企鹅】分发异常"));
            return new SendResult(false,""+e.getMessage());
        }
    }

    private SendResult send2(UserAptitudePO po, UserDTO select){

        String loanAmount = LoanAmountUtil.transformToWan(po.getLoanAmount());
        if(Integer.valueOf(loanAmount) <=5){
            loanAmount="5";
        }
        LinkedMultiValueMap<String, Object> paramsMap = new LinkedMultiValueMap<String, Object>();
        if(null != select){
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
        String postFormForObject = HttpUtil.postFormForObject(URL, paramsMap);
        JSONObject parseObject = JSON.parseObject(postFormForObject);
        LOG.info("【企峨】分发结果：{}",parseObject);
        SendResult result = new SendResult();
        int status;
        if("1".equals(parseObject.getString("resStatus"))) {
            status = 1;
            result.setSuccess(true);
        } else if("2".equals(parseObject.getString("resStatus"))){
            status = 0;
            result.setSuccess(false);
        }else {
            status = 2;
            result.setSuccess(false);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),status,"【企峨】"+postFormForObject));
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
            LOG.error("【企鹅】分发加密异常：{}",e.getMessage(),e);
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
        LOG.info("send 【企鹅】 signature = " + result);
        return result;
    }


//    public static void main(String[] args){
////        UserAptitudePO po = new UserAptitudePO();
////        po.setUserId("3e491487931543d7ab1f088e254f1812");
////        po.setName("伍散人测试");
////        po.setHouse(1);
////        po.setCity("南京市");
////        po.setCompany(1);
////        po.setGetwayIncome(1);
////        po.setInsurance(1);
////        po.setLoanAmount("《3万以下》");
////        po.setMobile("18229491955");
////        po.setOccupation(1);
////        po.setPublicFund("有，个人月缴500元");
////        po.setCreditCard(1);
////        po.setCar(1);
////        po.setUpdateDate(new Date());
////        QieApi api = new QieApi();
////        System.out.println(api.send(po,null));
//
////        System.out.println(MD5Util.getMd5String("19947571204"));
//    }
}
