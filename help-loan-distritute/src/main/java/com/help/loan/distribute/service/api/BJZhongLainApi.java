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
 * 北京中联钬信息咨询有限公司:apiSender_10087
 */
@Component("apiSender_10087")
public class BJZhongLainApi implements ApiSender {

//    private static final String sendUrl = "https://dis.rcplay.cn/api/post";
    private static final String sendUrl = "http://api.yisudai.com/crm/a/postData";
    private static final String provider = "cd0b4582405e4551ba109f1001ad2954";
    private static final Logger log = LoggerFactory.getLogger(BJZhongLainApi.class);
    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【北京中联钬】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【北京中联钬】分发异常:"+e.getMessage()));
            return new SendResult(false,"【北京中联钬】分发异常："+e.getMessage());
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        isHaveAptitude(po);

        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("mobile",Long.valueOf(po.getMobile()));
        data.put("price", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("city",po.getCity());
        data.put("age",po.getAge());
        data.put("is_house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("is_car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("is_insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("is_fund",po.getPublicFund().contains("有，")?1:0);
        data.put("is_socital",0);
        data.put("is_credit",0);
        data.put("webank",0);
        if(po.getCompany() == 1){
            data.put("is_work",1);
        }else data.put("is_work",0);
//        data.put("gender",(null == po.getGender() || po.getGender() == 0)?2:po.getGender());
        if(po.getGetwayIncome() == 1){
            data.put("income",2) ;
        }else
            data.put("income",1);
        data.put("provider",provider);
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        //{"success":true,"message":"提交完成"}
        //{"success":false,"message":"客户已存在","code":500,"msg":"客户已存在"}
        String result = HttpClientProxy.doPost(sendUrl, data, "UTF-8", 3000, httpHeader);
        log.info("【北京中联钬】分发结果：{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        if(json.containsKey("code")){
            int code = json.getIntValue("code");
            if(200 == code){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【北京中联钬】分发成功:"+result));
                return new SendResult(true,"【北京中联钬】分发成功："+result);
            }else if(500 == code){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【北京中联钬】分发重复:"+result));
                return new SendResult(false,"【北京中联钬】分发重复："+result);
            }else{
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【北京中联钬】分发失败:"+result));
                return new SendResult(false,"【北京中联钬】分发失败："+result);
            }
        }else{
            boolean success = json.getBooleanValue("success");
            if(success){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【北京中联钬】分发成功:"+result));
                return new SendResult(true,"【北京中联钬】分发成功："+result);
            }
            String msg = json.getString("message");
            if("客户已存在".equals(msg)){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【北京中联钬】分发重复:"+result));
                return new SendResult(false,"【北京中联钬】分发重复："+result);
            }
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【北京中联钬】分发失败:"+result));
            return new SendResult(false,"【北京中联钬】分发失败："+result);
        }
    }


    public static void main(String[] args){
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId(null);
        po.setName("刘永军测试");
        po.setMobile("15988189461");
        po.setCity("上海市");
        po.setLoanAmount("500000");
        po.setCompany(0);
        po.setPublicFund("没有公积金");
        po.setCar(0);
        po.setHouse(1);
        po.setInsurance(1);
        po.setGetwayIncome(0);
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setAge(30);
        po.setGender(1);
        po.setUpdateDate(new Date());
        BJZhongLainApi api = new BJZhongLainApi();
        System.out.println(api.send(po,null));
    }


}
