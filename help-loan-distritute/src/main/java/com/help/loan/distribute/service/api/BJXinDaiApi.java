package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
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
 *  北京鑫贷
 */
@Component("apiSender_20056")
public class BJXinDaiApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(BJXinDaiApi.class);
    private static final String sendUrl="http://crm.52juxin.com/index.php?s=/api/index/add";
    private static final int wxapp_id=10014;
    private static final int channel_id=27;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[北京鑫贷]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京鑫贷]分发异常:"+e.getMessage()));
            return new SendResult(false,"[北京鑫贷]分发异常:"+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po,UserDTO select){
        isHaveAptitude(po);

        JSONObject data=new JSONObject();
        data.put("wxapp_id",wxapp_id);
        data.put("channel_id",channel_id);
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("loans_price",LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("age",po.getAge());
        data.put("sex",po.getGender());
        data.put("city",po.getCity());
        if(JudgeUtil.in(po.getHouse(),1,2)){
            data.put("property",1);
        }else data.put("property",3);

        if(JudgeUtil.in(po.getCar(),1,2)){
            data.put("vehicle",1);
        }else data.put("vehicle",2);
        if(JudgeUtil.in(po.getInsurance(),1,2)){
            data.put("guarantee",1);
        }else data.put("guarantee",2);
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            data.put("drop",1);
            data.put("social",1);
        }else {
            data.put("drop",2);
            data.put("social",2);
        }
        if(JudgeUtil.in(po.getCreditCard(),1,2)){
            data.put("credit_card",1);
        }else data.put("credit_card",2);
        if(po.getPublicFund().contains("有，")){
            data.put("fund",1);
        }else  data.put("fund",2);
        //{"code":"0","msg":"提交成功"}
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
//        {"code":1,"msg":"SUCCESS","data":""}
        String result = HttpClientProxy.doPost(sendUrl, data, "UTF-8", 3000, httpHeader);
        log.info("[北京聚佳鑫]分发结果:{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        int code = json.getIntValue("code");
        if(1 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[北京鑫贷]分发成功:"+result));
            return new SendResult(true,"[北京鑫贷]发送成功:"+result);
        }
        String msg = json.getString("msg");
        if(msg.contains("号码已存在")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京鑫贷]分发重复:"+result));
            return new SendResult(false,"[北京鑫贷]分发重复:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京鑫贷]分发失败:"+result));
        return new SendResult(false,"[北京鑫贷]发送失败:"+result);
    }
//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("测试请忽略");
//        po.setMobile("13049692801");
//        po.setCity("北京市");
//        po.setLoanAmount("50000");
//        po.setCompany(0);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(0);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        BJXinDaiApi api = new BJXinDaiApi();
//        System.out.println(api.send(po,null));
//    }

}
