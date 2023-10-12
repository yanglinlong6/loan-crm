package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.service.api.crm.CrmData;
import com.help.loan.distribute.service.api.crm.ParamBO;
import com.help.loan.distribute.service.api.crm.SignUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 杭州禹创-易贷金服：apiSender_20037
 */
@Component("apiSender_20037")
public class YuChuangIIAPI implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(YuChuangIIAPI.class);

    String appid = "82bab690a70c4d9e91998ad78979b936";
    String secret = "afa901d7e7d94c5cbbe9805f3e344426";
    int  sourceId = 12;

    private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCCha+Z5XKhtvdR2EgJI+FuI8oaopJnY6GcZmZSHA3ssv9UQDbK4dusSCeC8euPGFoMTMpN1im6lULJobkjnegvwdNx9gbFX10M5fUdrM+rbbKIJShuJZlfkczKuU3Vh0TxBcvP1WBz0FKoYVr+I48HX60hNazBQP82y811Dw626wIDAQAB";

    private static final String sendUrl = "http://www.jzhix.com/customer/media/receive";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;
    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send2(po,select);
        }catch (Exception e){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【杭州禹创】:"+e.getMessage()));
            return new SendResult(false,"【杭州禹创】分发失败");
        }

    }

    private SendResult send2(UserAptitudePO po, UserDTO select){

        return send3(po,select);
    }

    private SendResult send3(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        CrmData data = new CrmData();
        data.setAppid(appid);
        data.setSecret(secret);
        data.setNonc(UUID.randomUUID().toString());
        data.setTimestamp(System.currentTimeMillis());
        data.setEncryption(0);

        if(null != select){
            if(StringUtils.isEmpty(po.getName())) {
                String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
                JSONObject parse = JSON.parseObject(userInfo);
                if(StringUtils.isEmpty(parse.get("openid"))) {
                    po.setName("公众号用户");
                } else {
                    po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
                }
            }
        }
        ParamBO param = new ParamBO();
        param.setName(po.getName());
        param.setGender(po.getGender());
        param.setMobile(po.getMobile());
        param.setCity(po.getCity());
        param.setAge(0==po.getAge()?32:po.getAge());
        param.setQuota(LoanAmountUtil.transform(po.getLoanAmount()));
        if(po.getPublicFund().contains("有，"))
            param.setFund(1);
        else
            param.setFund(0);
        //车
        if(JudgeUtil.in(po.getCar(),1,2))
            param.setCar(1);
        else param.setCar(2);
        //房
        if(JudgeUtil.in(po.getHouse(),1,2))
            param.setHouse(1);
        else param.setHouse(2);

        //保单
        if(JudgeUtil.in(po.getInsurance(),1,2))
            param.setPolicy(1);
        else param.setPolicy(2);
        param.setLifeInsurance(0);
        if(JudgeUtil.in(po.getCompany(),1))
            param.setBusinessLicense(1);
        else param.setBusinessLicense(2);
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            param.setPayroll(1);
        }else
            param.setPayroll(0);
        param.setSourceId(sourceId);
        data.setParams(JSONUtil.toJsonString(param));

        JSONObject dataJson = JSONUtil.toJSON(data);
        Set<String> keySet = dataJson.keySet();
        List<String> keyList = new ArrayList<>(keySet);
        keyList.sort((o1,o2) -> o1.compareTo(o2));
        StringBuffer signString = new StringBuffer();
        for(int i=0;i<keyList.size();i++){
            String key = keyList.get(i);
            if(i == 0)
                signString.append(key).append("=").append(dataJson.get(key));
            else
                signString.append("&").append(key).append("=").append(dataJson.get(key));
        }
        data.setSign(SignUtil.sign(signString.toString()));
        String result = HttpUtil.postForJSON(sendUrl, JSONUtil.toJsonString(data));
        if(JSONUtil.isJsonString(result) && "0".equals(JSONUtil.toJSON(result).getString("code"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,result));
            return new SendResult(true,result);
        }else if("516".equals(JSONUtil.toJSON(result).getString("code"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【杭州禹创】手机号已存在"));
        }else{
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,result));
        }
        return new SendResult(false,result);
    }

//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("3a1d24ac4761473abf9f4017b40288f6");
//        po.setName("伍散人测试");
//        po.setHouse(1);
//        po.setAge(35);
//        po.setCity("杭州市");
//        po.setCompany(0);
//        po.setInsurance(1);
//        po.setLoanAmount("《10-30万》");
//        po.setMobile("13632965158");
//        po.setOccupation(1);
//        po.setPublicFund("有，");
//        po.setOccupation(1);
//        po.setGetwayIncome(1);
//        po.setCreditCard(1);
//        po.setGender(1);
//        po.setCar(1);
//        po.setUpdateDate(new Date());
//        YuChuangIIAPI api = new YuChuangIIAPI();
//        System.out.println(api.send(po,null));
//
//    }
}
