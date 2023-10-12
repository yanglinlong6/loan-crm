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

/**
 * 苏州轩鼎总 苏州轩鼎
 */
@Component("apiSender_20006")
public class SZZhouApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(SZZhouApi.class);

    private static final String checkUrl = "http://106.14.157.223:8098/Customer/VerifyData?md5Mobile=%s";

    private static final String sendUrl="http://106.14.157.223:8098/Customer/Add";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【苏州轩鼎】推送异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【苏州轩鼎】推送异常:"+e.getMessage()));
            return new SendResult(false,"成都推送异常："+e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        String url = String.format(checkUrl, MD5Util.getMd5String(po.getMobile()));
        String result = HttpUtil.getForObject(url);
        log.info("【苏州轩鼎】撞库结果：{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        if(!json.getBooleanValue("IsSuccess")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【苏州轩鼎】推送重复:"+result));
            return new SendResult(false,"【苏州轩鼎】推送重复："+result);
        }
        JSONObject data = new JSONObject();
        data.put("Mobile",po.getMobile());
        data.put("CustomerName",po.getName());
        data.put("Sex",(null == po.getGender() || 1 != po.getGender())?"女":"男");
        data.put("Age",po.getAge() <= 25 ? 25:po.getAge());
        data.put("ApplyCity",po.getCity());
        data.put("ApplyAmount", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("WagesType",JudgeUtil.in(po.getGetwayIncome(),1,2)?"1":"3");
        data.put("IsHouse",JudgeUtil.in(po.getHouse(),1,2)?"1":"2");
        data.put("IsCar",JudgeUtil.in(po.getCar(),1,2)?"1":"2");
        data.put("IsInsurance",JudgeUtil.in(po.getInsurance(),1,2)?"1":"2");
        data.put("Social","2");
        data.put("Provident",po.getPublicFund().contains("有，")?"1":"2");
        data.put("Source",2);
        result = HttpUtil.postForJSON(sendUrl,data);
        log.info("【苏州轩鼎】推送结果：{}",result);
        json = JSONUtil.toJSON(result);
        if(json.getBooleanValue("IsSuccess")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【苏州轩鼎】推送成功:"+result));
            return new SendResult(true,"【苏州轩鼎】推送成功："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【苏州轩鼎】推送失败:"+result));
        return new SendResult(false,"【苏州轩鼎】推送失败："+result);
    }

    private String getInfo(UserAptitudePO po){
        StringBuffer memo = new StringBuffer();
        if(po.getCompany() ==1){
            memo.append("有营业执照").append(",");
        }
        if(po.getPublicFund().contains("有，")){
            memo.append("有公积金").append("，");
        }else memo.append("无公积金").append("，");
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
        if(null == po.getAge() || po.getAge() <=30){
            memo.append("年龄：30岁").append(",");
        }else{
            memo.append("年龄：").append(po.getAge()).append("岁,");
        }
        return memo.toString();
    }
//
//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人测试");
//        po.setMobile("13059692917");
//        po.setCity("苏州市");
//        po.setLoanAmount("50");
//        po.setCompany(1);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setGender(1);
//        po.setCreateBy("house");
//        po.setHouseExtension(5);
//        po.setUpdateDate(new Date());
//        ApiSender api = new SZZhouApi();
//        System.out.println(api.send(po,null));
//    }

}
