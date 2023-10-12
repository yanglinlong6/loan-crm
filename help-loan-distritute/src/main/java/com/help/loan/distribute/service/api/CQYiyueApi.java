package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.ec.v2.utlis.Md5Util;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.cache.CacheService;
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
 * 重庆易悦
 */
@Component("apiSender_20133")
public class CQYiyueApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(CQYiyueApi.class);

    private static final String checkUrl = "http://118.31.57.148:8080/api/customer/check_mobile";

    private static final String sendUrl = "http://118.31.57.148:8080/api/customer/import_data";

    private static final int FILE_ID = 1;

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Autowired
    CacheService cacheService;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【重庆易悦】发送异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[重庆易悦]推送异常:"+e.getMessage()));
            return new SendResult(false,"【重庆易悦】推送异常:"+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po,UserDTO select){

        String md5 = Md5Util.encryptMd5(po.getMobile());
        JSONObject data = new JSONObject();
        data.put("mobile",md5);

        String checkResult = HttpUtil.postForJSON(checkUrl,data);
        int code = JSONUtil.toJSON(checkResult).getIntValue("code");
        if(0 != code){
            return new SendResult(false,"【重庆易悦】验证撞库："+checkResult);
        }

        isHaveAptitude(po);
        data.clear();
        data.put("file_id",FILE_ID);
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("age",null == po.getAge()?0:po.getAge());
        data.put("sex",null == po.getGender()?0:po.getGender());
        data.put("city",po.getCity());
        data.put("is_house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("is_car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("is_company",po.getCompany() ==1 ? 1:0);
        data.put("is_credit",JudgeUtil.in(po.getCreditCard(),1,2)?1:0);
        data.put("is_insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("is_social",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("is_fund",po.getPublicFund().contains("有，")?1:0);
        data.put("is_work",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("is_tax",0);
        data.put("webank",0);
        data.put("money_demand", LoanAmountUtil.transform(po.getLoanAmount()).toString());
        System.out.println(data.toJSONString());
        String result = HttpUtil.postForJSON(sendUrl,data);
        code = JSONUtil.toJSON(result).getIntValue("code");
        if(code == 0){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[重庆易悦]推送成功:"+result));
            return new SendResult(true,"【重庆易悦】推送成功："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[重庆易悦]推送失败:"+result));
        return new SendResult(false,"【重庆易悦】推送失败："+result);
    }


//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("3f031229fbc3492ab507ad30fabe73a8");
//        po.setName("宇测试90");
//        po.setMobile("18262627280");
//        po.setCity("重庆市");
//        po.setLoanAmount("300000");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setCompany(1);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setUpdateDate(new Date());
//        CQYiyueApi api = new CQYiyueApi();
//        System.out.println(api.send(po,null));
//    }

}
