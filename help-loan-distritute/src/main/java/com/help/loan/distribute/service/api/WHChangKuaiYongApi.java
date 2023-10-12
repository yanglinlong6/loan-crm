package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
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
 * 武汉畅快用
 */
@Component("apiSender_10105")
public class WHChangKuaiYongApi implements  ApiSender{

    private static final String sendUrl = "http://crmapi.lexidai.com/customer6?&sms=1&company=15";
    private static final Logger LOG = LoggerFactory.getLogger(WHChangKuaiYongApi.class);
    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            LOG.error("[武汉畅快用]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【武汉畅快用】:"+e.getMessage()));
            return new SendResult(false,"[武汉畅快用]分发异常:"+e.getMessage());
        }
    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        isHaveAptitude(po);

        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("sex",po.getGender());
        data.put("mobile",po.getMobile());
        data.put("age",po.getAge());
        data.put("social_insurance",po.getPublicFund().contains("有，")?1:0);
        data.put("house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("limit", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("city",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        data.put("from","xdt");
        //{"status":true,"timestamp":1604473424}
        //{"status":false,"error":{"error_message":1008,"error_code":"database异常 主键冲突？"},"timestamp":1604473500}
        String result = HttpUtil.postForJSON(sendUrl,data);
        LOG.info("[武汉畅快用]分发结果:{}",result);
        JSONObject jsonObject= JSONUtil.toJSON(result);
        boolean status = jsonObject.getBooleanValue("status");
        if(status){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,result));
            return new SendResult(true,"[武汉畅快用]分发成功:"+result);
        }else {
            if(result.contains("主键冲突")){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【武汉畅快用】手机号已存在"));
                return new SendResult(false,"[武汉畅快用]分发重复:"+result);
            }else {
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,result));
                return new SendResult(false,"[武汉畅快用]分发失败:"+result);
            }
        }
    }


    public static void main(String[] args){
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
        po.setName("武汉测试");
        po.setMobile("13671948332");
        po.setCity("武汉市");
        po.setLoanAmount("《3-10万》");
        po.setGender(1);
        po.setAge(35);
        po.setCar(0);
        po.setHouse(0);
        po.setCompany(0);
        po.setPublicFund("有，个人月缴300-800元");
        po.setGetwayIncome(1);
        po.setInsurance(1);
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setUpdateDate(new Date());
        ApiSender api = new WHChangKuaiYongApi();
        System.out.println(api.send(po,null));
    }
}
