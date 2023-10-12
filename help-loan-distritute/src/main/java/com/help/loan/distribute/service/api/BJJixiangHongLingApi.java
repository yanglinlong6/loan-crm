package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 北京吉祥鸿麟: 10109
 */
@Component("apiSender_10109")
public class BJJixiangHongLingApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(BJJixiangHongLingApi.class);

    private static final String apiKey = "LKuBJyGBhQU5Q2dm";

    private static final String sendUrl = "http://jixianghonglin.com/api/apiImport/3";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po,select);
        }catch (Exception e){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【吉祥鸿麟-北京】分发异常:"+e.getMessage()));
            return new SendResult(false,"[吉祥鸿麟-北京]分发异常:"+e.getMessage());
        }

    }

    public SendResult sendResult(UserAptitudePO po, UserDTO select){

        isHaveAptitude(po);
        JSONObject customer = new JSONObject();
        customer.put("name",po.getName());
        customer.put("phone",po.getMobile());
        customer.put("quota", LoanAmountUtil.transform(po.getLoanAmount()));
        customer.put("remark","");
        customer.put("sex",po.getGender());
        customer.put("star",0);
        customer.put("marriage",1);
        customer.put("age",po.getAge());
        if(JudgeUtil.in(po.getGetwayIncome(),1)){
            customer.put("salary",8000);
        }else if(JudgeUtil.in(po.getGetwayIncome(),2)){
            customer.put("salary",5000);
        }else
            customer.put("salary",0);

        customer.put("is_security",2);
        if(po.getPublicFund().contains("有，")){
            customer.put("is_provident_fund",1);
        }else
            customer.put("is_provident_fund",2);

        customer.put("city",po.getCity());
        customer.put("is_credit_card",2);
        if(JudgeUtil.in(po.getHouse(),1,2)){
            customer.put("real_estate",1);
        }else customer.put("real_estate",2);

        if(JudgeUtil.in(po.getCar(),1,2)){
            customer.put("car_production",1);
        }else customer.put("car_production",2);

        if(JudgeUtil.in(po.getInsurance(),1,2)){
            customer.put("insurance",1);
        }else customer.put("insurance",2);

        JSONArray customers = new JSONArray();
        customers.add(customer);

        JSONObject data = new JSONObject();
        data.put("api_key",apiKey);
        data.put("customers", customers);
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        //{"code":200,"msg":"添加成功"}
        //{"code":300,"msg":"客户信息重复！"}
        String result = HttpClientProxy.doPost(sendUrl, data, "UTF-8", 3000, httpHeader);
        log.info("[吉祥鸿麟-北京]分发结果:{}",result);
        JSONObject resultJson = JSONUtil.toJSON(result);
        int code = resultJson.getIntValue("code");
        if(200 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【吉祥鸿麟-北京】分发成功："+result));
            return new SendResult(true,"[吉祥鸿麟-北京]分发成功:"+result);
        }
        if("客户信息重复！".equals(resultJson.getString("msg"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【吉祥鸿麟-北京】分发重复:"+result));
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【吉祥鸿麟-北京】分发失败:"+result));
        return new SendResult(false,"[吉祥鸿麟-北京]分发失败:"+result);
    }


//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("刘永军测试");
//        po.setMobile("15988199461");
//        po.setCity("上海市");
//        po.setLoanAmount("50000");
//        po.setCompany(0);
//        po.setPublicFund("没有公积金");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setInsurance(1);
//        po.setGetwayIncome(0);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new BJJixiangHongLingApi();
//        System.out.println(api.send(po,null));
//    }


}
