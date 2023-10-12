package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 金启
 *
 */
@Component("apiSender_10063")
public class JinQiApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(JinQiApi.class);

    private static final String sendUrl = "http://175.24.191.130:8080/api/customer/add";
    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send2(po,select);
        }catch (Exception e){
            log.error("【金启】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【金启】分发异常："+e.getMessage()));
            return new SendResult(false,"【金启】分发异常："+e.getMessage());
        }

    }


    private SendResult send2(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        if(StringUtils.isBlank(po.getName()) && null != select){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(!parse.containsKey("openid") || StringUtils.isEmpty(parse.get("openid").toString())) {
                po.setName("公众号用户");
            } else {
                po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
            }
        }
        Map<String,Object> data = new HashMap<>();
        data.put("name",po.getName());
        data.put("sex",(po.getGender() == null || po.getGender() == 0)?"女":"男");
        data.put("mobile",po.getMobile());
        data.put("money", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("city",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        data.put("age",(po.getAge() == null || po.getAge() <=0 )?30:po.getAge());
        data.put("shebao","无");
        data.put("gongjijin",po.getPublicFund().contains("有，")?"有":"无");
        data.put("credit_card", JudgeUtil.in(po.getCreditCard(),1,2)?"有":"无");
        data.put("baodan_is",JudgeUtil.in(po.getInsurance(),1,2)?"有":"无");
        data.put("house",JudgeUtil.in(po.getHouse(),1,2)?"有":"无");
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?"有":"无");
        data.put("weili","无");
        data.put("isbankpay",JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        if(JudgeUtil.in(po.getCompany(),1)){
            data.put("Job","个体户");
        }else{
            data.put("Job","工薪族");
        }
        data.put("source","D");
        data.put("ip","127.0.0.1");
        data.put("meiti","D");
        data.put("time", DateUtil.to10());

        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        String result = HttpClientProxy.doPost(sendUrl, data, "UTF-8", 3000, httpHeader);
        log.info("【金启】分发结果：{}",result);
        JSONObject resultJson = JSONUtil.toJSON(result);
        int code = resultJson.getIntValue("code");
        if(0==code){//0：成功
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【金启】分发成功："+result));
            return new SendResult(true,result);
        }
        if(3 == code){//3：重复申请
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【金启】分发重复："+result));
            return new SendResult(false,result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【金启】分发重复："+result));
        return new SendResult(false,result);
    }

//        public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
//        po.setName("海燕");
//        po.setMobile("13671948202");
//        po.setCity("上海市");
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
//        JinQiApi api = new JinQiApi();
//        System.out.println(api.send(po,null));
//    }

}
