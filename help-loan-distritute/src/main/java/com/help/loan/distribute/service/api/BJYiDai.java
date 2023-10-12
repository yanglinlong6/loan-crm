package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
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
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * 	北京亿贷
 */
@Component("apiSender_10015")
public class BJYiDai implements ApiSender {
    private static final Logger log = LoggerFactory.getLogger(BJYiDai.class);

    private static final String checkUrl="http://crm.rcplay.cn/crm/a/check";
    private static final String sendUrl="http://crm.rcplay.cn/crm/a/postData";

    private static final String providerId="790a292eaec14acca9653cddc81c02b2";
    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{

            return sendResult(po,select);
        }catch (Exception e){
            log.error("[北京亿贷-北京]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京亿贷-北京]分发异常:"+e.getMessage()));
            return new SendResult(false,"[北京亿贷-北京]分发异常"+e.getMessage());
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        // 撞库检查
        JSONObject data = new JSONObject();
        data.put("phoneMd5", MD5Util.getMd5String(po.getMobile()));
        data.put("providerId",providerId);
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        //{"success":true,"message":"成功"}
        String checkResult = HttpClientProxy.doPost(checkUrl, data, "UTF-8", 3000, httpHeader);
        log.info("[北京亿贷-北京]撞库检查结果:{}",checkResult);
        if(!JSONUtil.toJSON(checkResult).getBooleanValue("success")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京亿贷-北京]撞库重复:"+checkResult));
            return new SendResult(false,"[北京亿贷-北京]撞库重复:"+checkResult);
        }
        isHaveAptitude(po);
        data.clear();
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
//        data.put("phoneMd5", MD5Util.getMd5String(po.getMobile()));
        data.put("price", LoanAmountUtil.transform(po.getLoanAmount()));
        if(JudgeUtil.in(po.getHouse(),1,2)){
            data.put("is_house",1);
        }else data.put("is_house",0);

        if(JudgeUtil.in(po.getCar(),1,2)){
            data.put("is_car",1);
        }else data.put("is_car",0);

        if(JudgeUtil.in(po.getInsurance(),1,2)){
            data.put("is_insurance",1);
        }else data.put("is_insurance",0);

        if(po.getPublicFund().contains("有，")){
            data.put("is_fund",1);
        }else data.put("is_fund",0);

        data.put("is_socital",0);

        if(JudgeUtil.in(po.getCreditCard(),1,2)){
            data.put("is_credit",1);
        }else data.put("is_credit",0);
        data.put("webank",0);

        if(JudgeUtil.in(po.getCompany(),1)){
            data.put("is_work",1);
        }else data.put("is_work",0);
        data.put("city",po.getCity());
        data.put("provider",providerId);
        data.put("remarks",getInfo(po));
        data.put("age",po.getAge());
        //{"msg":"提交成功!","code":200}   {\"success\":false,\"message\":\"手机密文：f6b4852fff9c6630db76fd8048c3bebd已重复\"}","success":false}
        String result = HttpClientProxy.doPost(sendUrl, data, "UTF-8", 3000, httpHeader);
        log.info("[北京亿贷-北京]分发结果:{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        if(json.getIntValue("code") == 200){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[北京亿贷-北京]分发成功:"+result));
            return new SendResult(true,"[北京亿贷-北京]分发成功:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京亿贷-北京]分发失败:"+result));
        return new SendResult(false,"[北京亿贷-北京]分发失败:"+result);

    }

    private java.lang.String getInfo(UserAptitudePO po){
        StringBuffer memo = new StringBuffer();
        if(po.getCompany() ==1){
            memo.append("有营业执照").append(",");
        }
        if(po.getPublicFund().contains("有，")){
            memo.append("有公积金").append("，");
        }

        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            memo.append("有银行代发工资").append(",");
        }
        if(JudgeUtil.in(po.getHouse(),1,2)){
            memo.append("有商品房").append(",");
        }
        if(JudgeUtil.in(po.getCar(),1,2)){
            memo.append("有车").append(",");
        }
        if(JudgeUtil.in(po.getInsurance(),1,2)){
            memo.append("有商业寿险保单").append(",");
        }
        memo.append("申请金额：").append(po.getLoanAmount());
        return memo.toString();
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("测试");
//        po.setMobile("13049692800");
//        po.setCity("北京市");
//        po.setLoanAmount("50000");
//        po.setCompany(0);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(0);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new BJYiDai();
//        System.out.println(api.send(po,null));
//    }

}
