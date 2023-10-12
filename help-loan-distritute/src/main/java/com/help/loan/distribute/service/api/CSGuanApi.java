package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 长沙关总 apiSender_20017
 */
@Component("apiSender_20017")
public class CSGuanApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(CSGuanApi.class);

    private static final String sendUrl = "http://114.64.255.69:9815/api";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【长沙关总】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【长沙关总】"+e.getMessage()));
            return new SendResult(false,"[长沙关总]分发异常："+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){


        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("phone",po.getMobile());
        data.put("age",po.getAge().toString());
        data.put("gender",JudgeUtil.in(po.getGender(),1)?"男":"女");
        data.put("house", JudgeUtil.in(po.getHouse(),1,2)?"有":"无");
        data.put("vehicle", JudgeUtil.in(po.getCar(),1,2)?"有":"无");
        data.put("chit", JudgeUtil.in(po.getInsurance(),1,2)?"有":"无");
        data.put("fund", po.getPublicFund().contains("有，")?"有":"无");
        data.put("undertakes", JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        data.put("business", JudgeUtil.in(po.getCompany(),1)?"有":"无");
        // {"code":"1","success":"成功"}
        String result = HttpUtil.postForJSON(sendUrl,data);
        if("1".equals(JSONUtil.toJSON(result).getString("code"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【长沙关总】"+result));
            return new SendResult(true,"【长沙关总】发送成功："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【长沙关总】"+result));
        return new SendResult(false,"【长沙关总】发送失败："+result);
    }


//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
//        po.setName("海测试4");
//        po.setMobile("13671948208");
//        po.setCity("南京市");
//        po.setLoanAmount("《3-10万》");
//        po.setGender(1);
//        po.setAge(35);
//        po.setCar(0);
//        po.setHouse(0);
//        po.setCompany(0);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setUpdateDate(new Date());
//        ApiSender api = new CSGuanApi();
//        System.out.println(api.send(po,null));
//    }
}
