package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 南京资信和-南京市
 */
@Component("apiSender_20084")
public class NJZixinheApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(NJZixinheApi.class);

//    private static final String checkUrl = "http://112.126.75.7:8100/customers?encrypt_phone=";
//
//    private static final String sendUrl = "http://112.126.75.7:8100/customers";
//
//    private static final String checkUrl_hz = "http://112.126.75.7:8400/customers?encrypt_phone=";
//
//    private static final String sendUrl_hz = "http://112.126.75.7:8400/customers";
    private static final String checkUrl = "http://112.126.75.7:8100/customers/check/MD5Encrypt?phone_encrypt=%s&company_id=%s";

    private static final String sendUrl = "http://112.126.75.7:8100/customers";

    private static final String companyId = "dfb4f69a-0bab-4c74-9293-541bbc87ace3";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【资信和】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【资信和】】推送未知异常："+e.getMessage()));
            return new SendResult(false,"【资信和】推送异常:"+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        String url = String.format(checkUrl,MD5Util.getMd5String(po.getMobile()),companyId);
        String result = restTemplate.postForObject(url,null,String.class);
        log.info("【资信和】撞库结果：{}",result);
        if(!"0".equals(JSONUtil.toJSON(result).getString("data"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【资信和】推送未知异常："+result));
            return new SendResult(false,"【资信和】客户已存在："+result);
        }

        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("phone",po.getMobile());
        data.put("gender",(null == po.getGender() || 1 != po.getGender())?"Female":"Male");
        data.put("age",po.getAge()<25?28:po.getAge());
        data.put("limit", Integer.valueOf(LoanAmountUtil.transform(po.getLoanAmount())));
        if(1 == po.getGetwayIncome()){
            data.put("income_year",8000*12);
        }else if(2==po.getGetwayIncome()){
            data.put("income_year",5000*12);
        }else data.put("income_year",0);
        data.put("customer_remark",getInfo(po));
        data.put("provider_code","1002");
        data.put("company_id",companyId);
        data.put("grade","Grade0");
        data.put("is_married",false);
        data.put("education","Junior");
        data.put("career","Staff");

        result=HttpUtil.convertUnicodeToCh(HttpUtil.postForJSON(sendUrl,data));
        log.info("[资信和]分发结果:{}",result);
        if(JudgeUtil.contain(result,"成功")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[资信和]推送成功:"+result));
            return new SendResult(true,"[资信和]推送成功:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[资信和]推送失败:"+result));
        return new SendResult(false,"[资信和]推送失败:"+result);
    }

    private String getInfo(UserAptitudePO po){
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
//        po.setName("伍先森测试29");
//        po.setMobile("13632912229");
//        po.setCity("南京市");
//        po.setLoanAmount("50000");
//        po.setCompany(1);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new NJZixinheApi();
//        System.out.println(api.send(po,null));
//
//    }

}
