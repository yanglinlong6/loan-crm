package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.ec.v2.utlis.Md5Util;
import com.help.loan.distribute.common.utils.AESUtil;
import com.help.loan.distribute.common.utils.HttpUtil;
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
 * 武汉速金所
 *
 */
@Component("apiSender_20072")
public class WHSuJinSuoApi implements ApiSender{

    private static final Logger LOG = LoggerFactory.getLogger(WHSuJinSuoApi.class);

    private static final String checkUrl = "http://139.224.251.4/api/customer/check";

    private static final String sendUrl = "http://139.224.251.4/api/ec/addCustomersByCBC";

    private static final String SOURCE = "BZWY";

    private static final String KEY = "whsjs20220214123";

    private static final String IV = "whsjs20220214123";
    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return send2(po,select);
        }catch (Exception e){
            LOG.error("[武汉速金所]发送异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【武汉速金所】:"+e.getMessage()));
            return new SendResult(false,"【武汉速金所】:"+e.getMessage());
        }

    }

    public SendResult send2(UserAptitudePO po, UserDTO select) throws Exception {

        JSONObject data = new JSONObject();
        data.put("mobileMd5", Md5Util.encryptMd5(po.getMobile()));

//        Map<String, String> httpHeader = new HashMap<>();
//        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/json");

        String result = HttpUtil.postForJSON(checkUrl,data);
        LOG.info("[武汉速金所]撞库结果:{}",result);
        if(200 != JSONUtil.toJSON(result).getIntValue("errCode")){
            return new SendResult(false,"【武汉速金所】重复:"+result);
        }

        data.clear();

        String city = po.getCity();
        data.put("name",po.getName());
        data.put("quota", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("sex",po.getGender());
        data.put("age",po.getAge());
        data.put("mobile",po.getMobile());
        data.put("city",city.endsWith("市")?city.substring(0,city.length()-1):city);
        data.put("source",SOURCE);

        JSONObject tag = new JSONObject();
        tag.put("house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        tag.put("car", JudgeUtil.in(po.getCar(),1,2)?1:0);
        tag.put("fund", po.getPublicFund().contains("有，")?1:0);
        tag.put("policy", JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        tag.put("socialInsurance",2);
        tag.put("salary", JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        tag.put("bsnslcs", JudgeUtil.in(po.getCompany(),1)?1:0);
        tag.put("enterpriseTax", 2);
        tag.put("company", 2);
        tag.put("loan",2);

        data.put("tag",tag);

        JSONObject config = new JSONObject();
        config.put("followUserId",11253969);
        config.put("optUserId",11253969);
        config.put("createUserId",11253969);
        config.put("groupId",0);

        data.put("ecConfig",config);

        String encryptData = AESUtil.java_openssl_encrypt(data.toJSONString(),KEY,IV);

        data.clear();

        data.put("data",encryptData);

        result = HttpUtil.postForJSON(sendUrl,data);
        LOG.info("[武汉速金所]发送结果:{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        int code = json.getIntValue("code");
        if(200 == code && json.getJSONObject("data").getJSONArray("successIdList").size() > 0){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【武汉速金所】成功:"+result));
            return new SendResult(true,result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【武汉速金所】失败:"+result));
        return new SendResult(false,result);
    }

//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("517135ba6a244cd0b1ac9210f495cd18");
//        po.setName("伍测试2");
//        po.setHouse(1);
//        po.setAge(35);
//        po.setCity("武汉市");
//        po.setCompany(0);
//        po.setInsurance(1);
//        po.setLoanAmount("100000");
//        po.setMobile("13410567151");
//        po.setOccupation(1);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setOccupation(1);
//        po.setGetwayIncome(1);
//        po.setCreditCard(0);
//        po.setCar(1);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new WHSuJinSuoApi();
//        System.out.println(api.send(po,null));
//
//    }
}
