package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
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
import java.util.*;
import java.util.regex.Pattern;

/**
 * 北京丰鼎金闪贷网 apiSender_20009
 * */
@Component("apiSender_20009")
public class BJFengDingApi implements ApiSender {

    private static Logger LOG = LoggerFactory.getLogger(BJFengDingApi.class);

    private static final Logger log = LoggerFactory.getLogger(BJFengDingApi.class);

    private static final String sendUrl="http://open.fuzejinfu.com/Customer/import";

    private static final String appid = "104206";

    private static final String appkey = "2p02JgWHkrpwsesdsdaDj0TMw9PZWKK0T7ai";

    private static final String vender_name = "熊猫朋友圈";

    private static final String channel = "腾讯信息流";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            isHaveAptitude(po);
            return send2(po,select);
        }catch (Exception e){
            LOG.error("【金闪贷-北京】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【金闪贷-北京】分发异常"+e.getMessage()));
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
        if("tt001".equals(po.getChannel())){
            paramsMap.add("vender_name", po.getChannel());
        }else
            paramsMap.add("vender_name", vender_name);
        paramsMap.add("channel", channel);
        paramsMap.add("name", po.getName());
        paramsMap.add("city", po.getCity().replace("市", ""));

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        paramsMap.add("timestamp", timestamp);
        paramsMap.add("noncestr", createUUID());

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
        paramsMap.add("loan_time", "12");
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

        paramsMap.add("appid", appid);
        paramsMap.add("appkey", appkey);
        paramsMap.add("signature", sha1(paramSplicing(paramsMap)));
        paramsMap.remove(appkey);
        String postFormForObject = HttpUtil.postFormForObject(sendUrl, paramsMap);
        JSONObject parseObject = JSON.parseObject(postFormForObject);
        LOG.info("【金闪贷-北京】分发结果：{}",parseObject);
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
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),status,"【金闪贷-北京】"+postFormForObject));
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
            LOG.error("【丰鼎金闪贷网-北京】分发加密异常：{}",e.getMessage(),e);
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
        LOG.info("send 【丰鼎金闪贷网-北京】 signature = " + result);
        return result;
    }


    public static void main(String[] args){

        BJFengDingApi api = new BJFengDingApi();

        UserAptitudePO po = new UserAptitudePO();
        po.setUserId("f4f8ccab843d43a4b0a221e534fbed99");
        po.setCar(4);
        po.setHouse(2);
        po.setCity("北京市");
        po.setCompany(1);
        po.setGetwayIncome(1);
        po.setInsurance(2);
        po.setLoanAmount("《3-5万》");
        po.setMobile("13632965140");
        po.setName("测试伍");
        po.setAge(33);
        po.setOccupation(1);
        po.setPublicFund("有，个人月缴300-500元");
        po.setCreditCard(1);
        po.setCar(1);
        SendResult result = api.send(po,null);
        System.out.println(JSONUtil.toJsonString(result));
    }

}
