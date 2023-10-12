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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * 天津前程:apiSender_10113
 */
@Component("apiSender_10113")
public class TianJiQianchenApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(TianJiQianchenApi.class);

    private static final String sendUrl = "http://39.106.104.228:8500/api/dfcustomer/upload";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[天津前程]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【天津前程】分发异常:"+e.getMessage()));
            return new SendResult(false,"[天津前程]分发异常:"+e.getMessage());
        }
    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("userName",po.getName());
        data.put("userMobile",po.getMobile());
        data.put("gender",po.getGender());
        data.put("age",po.getAge());
        data.put("applicationLimit", LoanAmountUtil.transform(po.getLoanAmount()));
        if(1 == po.getGetwayIncome()){
            data.put("salary",8000);
        }else if(2 == po.getGetwayIncome()){
            data.put("salary",5000);
        }else{
            data.put("salary",0);
        }
        if(1 == po.getCompany()){
            data.put("licenseNo","有");
        }else {
            data.put("licenseNo","无");
        }

        data.put("estate", JudgeUtil.in(po.getHouse(),1,2)?1:2);
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?1:2);
        data.put("gjj",po.getPublicFund().contains("有，")?1:2);
        data.put("insurancePolicy",JudgeUtil.in(po.getInsurance(),1,2)?1:2);
        System.out.println(data.toJSONString());
        HttpHeaders headersForm = new HttpHeaders();
        headersForm.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
        headersForm.add("Accept", "application/json;charset=UTF-8");
        //{"message":"系统异常","data":null,"code":500}
        //{"code":200,"message":"客户上传成功成功","data":{}}
        String result = HttpUtil.postForJSON(sendUrl,data,headersForm);
        log.info("[天津前程]发送结果:{}",result);
        int code = JSONUtil.toJSON(result).getIntValue("code");
        if(200 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【天津前程】分发成功："+result));
            return new SendResult(true,"[天津前程]分发成功:"+result);
        }else {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【天津前程】分发失败："+result));
            return new SendResult(true,"【天津前程】分发失败:"+result);
        }
    }
//
//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("3a1d24ac4761473abf9f4017b40288f6");
//        po.setName("测试林先2");
//        po.setHouse(1);
//        po.setAge(35);
//        po.setCity("天津市");
//        po.setCompany(0);
//        po.setInsurance(1);
//        po.setLoanAmount("《10-30万》");
//        po.setMobile("13632965155");
//        po.setOccupation(1);
//        po.setPublicFund("有，");
//        po.setOccupation(1);
//        po.setGetwayIncome(1);
//        po.setCreditCard(1);
//        po.setCar(1);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new TianJiQianchenApi();
//        System.out.println(api.send(po,null));
//
//    }
}
