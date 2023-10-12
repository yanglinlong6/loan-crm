package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.DateUtil;
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
 * 柚贷网
 */
@Component("apiSender_20042")
public class YouDaiWangApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(YouDaiWangApi.class);

    private static final String checkUrl = "http://flow.pofengkeji.com/customer/getHit";

    private static final String sendUrl = "http://flow.pofengkeji.com/customer/addCustomer";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{

            JSONObject data = new JSONObject();
            data.put("MobilePhone", MD5Util.getMd5String(po.getMobile()));
            String response = HttpUtil.postForJSON(checkUrl,data);
            log.info("【柚贷网】撞库结果：{}",response);
            int code = JSONUtil.toJSON(response).getIntValue("code");
            if(code != 100){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【柚贷网】撞库重复:"+response));
                return new SendResult(false,"【柚贷网】撞库重复："+response);
            }
            return send2(po,select);
        }catch (Exception e){
            log.error("【柚贷网】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【柚贷网】:"+e.getMessage()));
            return new SendResult(false,"【柚贷网】分发异常："+e.getMessage());
        }
    }


    private SendResult send2(UserAptitudePO po, UserDTO select){

        isHaveAptitude(po);

        JSONObject data = new JSONObject();
        data.put("isbankpay", JudgeUtil.in(po.getGetwayIncome(),1,2));
        data.put("city",po.getCity());
        data.put("shebao",false);
        data.put("sex",(null == po.getGender() || po.getGender() != 1) ? "女":"男");
        data.put("mobile",po.getMobile());
        data.put("source","道分科技");
        data.put("gongjijin",po.getPublicFund().contains("有，"));
        data.put("house",JudgeUtil.in(po.getHouse(),1,2));
        data.put("baodan_is",JudgeUtil.in(po.getInsurance(),1,2));
        data.put("money", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("car",JudgeUtil.in(po.getCar(),1,2));
        data.put("name",po.getName());
        data.put("age",(null == po.getAge() || po.getGender()<=0)?30:po.getAge());
        data.put("time", DateUtil.to10());

        String response = HttpUtil.postForJSON(sendUrl,data);
        log.info("【柚贷网】分发结果：{}",response);
        JSONObject json = JSONUtil.toJSON(response);
        int code = json.getJSONObject("messageModel").getIntValue("code");
        if (code == 0) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【柚贷网】成功:"+response));
            return new SendResult(true,"【柚贷网】分发成功："+response);
        }
        String message = json.getJSONObject("messageModel").getString("messageText");
        if(JudgeUtil.in(message,"重复申请数据","今日已经提交过了")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【柚贷网】重复:"+response));
            return new SendResult(false,"【柚贷网】分发重复："+response);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【柚贷网】失败:"+response));
        return new SendResult(false,"【柚贷网】分发失败："+response);
    }



//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人测试");
//        po.setMobile("15088659593");
//        po.setCity("武汉市");
//        po.setLoanAmount("100000");
//        po.setCompany(1);
//        po.setPublicFund("没有公积金");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setInsurance(1);
//        po.setGetwayIncome(0);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setUpdateDate(new Date());
//        YouDaiWangApi api = new YouDaiWangApi();
//        System.out.println(api.send(po,null));
//    }
}
