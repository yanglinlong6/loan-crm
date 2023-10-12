package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.DateUtil;
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

import java.util.Date;

/**
 * 昱胜-杭州：apiSender_10089
 */
//@Component("apiSender_10089")
public class YuShengApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(YuShengApi.class);

    private static final String sendUrl = "http://chengzhong.haishagang.com/sendCustomer";

    private static final String code = "10009003";

    private static final String clientsecret = "11d99035d9754a73a7281174484e38bf";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【昱胜】分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【昱胜】分发异常:"+e.getMessage()));
            return new SendResult(false,"【昱胜】分发异常："+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("phone",po.getMobile());
        data.put("sex",po.getGender());
        data.put("address",po.getCity());
        data.put("loanAmount", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("house", JudgeUtil.in(po.getHouse(),1,2)?"2":"1");
        data.put("car", JudgeUtil.in(po.getCar(),1,2)?"2":"1");
        data.put("insurance", JudgeUtil.in(po.getInsurance(),1,2)?"2":"1");
        data.put("accumulation",po.getPublicFund().contains("有，")?"2":"1");
        data.put("city",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        data.put("time", DateUtil.formatToString(new Date(),DateUtil.yyyyMMddHHmmss2));
        data.put("isLicense",po.getCompany() ==1 ?"2":"1");
        data.put("code",code);
        System.out.println(data.toJSONString());

        HttpHeaders headersJson = new HttpHeaders();
        headersJson.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headersJson.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headersJson.add("clientsecret",clientsecret);
        String result = HttpUtil.postForJSON(sendUrl,data,headersJson);

        JSONObject json = JSONUtil.toJSON(result);
        if(0 == json.getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【昱胜】分发成功:"+result));
            return new SendResult(true,"【昱胜】分发成功："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【昱胜】分发失败:"+result));
        return new SendResult(false,"【昱胜】分发失败："+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("测试数据1");
//        po.setMobile("15988199460");
//        po.setCity("上海市");
//        po.setLoanAmount("50000");
//        po.setCompany(0);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(0);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        YuShengApi api = new YuShengApi();
//        System.out.println(api.send(po,null));
//    }
}
