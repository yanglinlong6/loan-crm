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

import java.util.HashMap;
import java.util.Map;

/**
 * 上海壹乾商务咨询有限公司
 */
@Component("apiSender_10097")
public class SHYiqianApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(SHYiqianApi.class);

    private static final String sendUrl = "http://119.45.53.63:8080/api/customer/add";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;
    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【上海壹乾】分发异常：{}",e.getMessage(),e);
            return null;
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);

        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("sex",(null == po.getGender() || JudgeUtil.in(po.getGetwayIncome(),0,2)) ? "女":"男");
        data.put("mobile",po.getMobile());
        data.put("money", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("city",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        data.put("age",(null == po.getAge() || po.getAge() <=0)?30:po.getAge());
        data.put("shebao","无");
        data.put("gongjijin",po.getPublicFund().contains("有，")?"有":"无");
        data.put("baodan_is",JudgeUtil.in(po.getInsurance(),1,2)?"有":"无");
        data.put("license",JudgeUtil.in(po.getCompany(),1)?"有":"无");
        data.put("house",JudgeUtil.in(po.getHouse(),1,2)?"有":"无");
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?"有":"无");
        data.put("isbankpay",JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        data.put("source","XD");
        data.put("meiti","");
        data.put("remark","");

        //3：重复申请
        //5：失败
        //0：成功
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        //{"retCode":"017","retMsg":"客户信息申请频繁","retData":null}
        String response = HttpClientProxy.doPost(sendUrl, data, "UTF-8", 3000, httpHeader);
        log.info("【上海壹乾】分发结果：{}",response);
        JSONObject json = JSONUtil.toJSON(response);
        int code = json.getIntValue("code");
        if(code == 0){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【上海壹乾】分发成功:"+response));
            return new SendResult(true,"【上海壹乾】分发成功："+response);
        }
        if(code == 3){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【上海壹乾】分发重复:"+response));
            return new SendResult(false,"【上海壹乾】分发重复："+response);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海壹乾】分发失败:"+response));
        return new SendResult(false,"【上海壹乾】分发失败："+response);
    }

//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("3a1d24ac4761473abf9f4017b40288f6");
//        po.setName("测试林先");
//        po.setGender(1);
//        po.setHouse(1);
//        po.setAge(35);
//        po.setCity("杭州市");
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
//        po.setUpdateDate(new Date());
//        SHYiqianApi api = new SHYiqianApi();
//        System.out.println(api.send(po,null));
//
//    }
}
