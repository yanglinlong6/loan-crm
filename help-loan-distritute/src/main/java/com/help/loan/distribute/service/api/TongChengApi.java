package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
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
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 同城api对接
 */
@Component("apiSender_20018")
public class TongChengApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(TongChengApi.class);

    private static final String sendUrl = "http://tcyd.notify.succtime.com/customerNotify/crmNotify/BZ";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {

        try{
            return send2(po,select);
        }catch (Exception e){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【同城】："+e.getMessage()));
            log.error("【同城】：{}",e.getMessage(),e);
            return new SendResult(false,"【同城】："+e.getMessage());
        }
    }

    private SendResult send2(UserAptitudePO po, UserDTO select){
        if(!po.getChannel().startsWith("tt")){
            return new SendResult(false,"【同城】分发失败,不是头条客户");
        }
        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("customer_name",po.getName());
        data.put("customer_phone",po.getMobile());
        data.put("customer_sex",null == po.getGender()?0:po.getGender());
        data.put("loan_amount", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("house_info", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("vehicle_info", JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("insurance_policy", JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("accumulation_fund", po.getPublicFund().contains("有，")?1:0);
        data.put("social_security", JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("credit_info", JudgeUtil.in(po.getCreditCard(),1,2)?1:0);
        data.put("company_info", JudgeUtil.in(po.getCompany(),1)?1:0);
        data.put("city",po.getCity());

        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

        // {"fail":false,"code":1,"data":"success","success":true,"message":"success"}
        String result = HttpClientProxy.doPost(sendUrl, data, "UTF-8", 3000, httpHeader);
        log.info("【同城-杭州】分发结果：{}",result);

        JSONObject json = JSONUtil.toJSON(result);
        int code = json.getIntValue("code");
        if(1 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,result));
            return new SendResult(true,"【同城】分发成功");
        }
        if(2 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,result));
            return new SendResult(false,"【同城】分发重复");
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,result));
        return new SendResult(false,"【同城】分发失败");
    }

    public static void main(String[] args){
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
        po.setName("孙江测试");
        po.setMobile("15868171110");
        po.setCity("杭州市");
        po.setLoanAmount("50000");
        po.setCar(0);
        po.setHouse(0);
        po.setCompany(0);
        po.setPublicFund("没有公积金");
        po.setGetwayIncome(1);
        po.setInsurance(1);
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setUpdateDate(new Date());
        po.setChannel("ttt_");
        TongChengApi api = new TongChengApi();
        System.out.println(api.send(po,null));
    }

}
