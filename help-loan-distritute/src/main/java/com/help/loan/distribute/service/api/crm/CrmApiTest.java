package com.help.loan.distribute.service.api.crm;


import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CrmApiTest {

    public static void main(String[] args) throws Exception {
        String appid = "b6c71d241e12fc41025fd19615723b3a";
        String secret = "84faebee50c6441382ec11307117f317";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCOt5G6EUSiQYFxESS3nCMrSndrdjKfXb4SF3sRgohWSbWrXPS5ea0ZnrkHbTUWfWNsH9SwdVYzyWJaG0uGbydHXDKwq8CDdfRJHl3UD5qmEYDaOMiQeRXmW6ekn6Wh2x9TBXXXVWsCl7sKhhXnRTCTzIuZNlSH0vVgaJ/BDaf7YQIDAQAB";
        int  sourceId = 9610;

        CrmData data = new CrmData();
        data.setAppid(appid);
        data.setNonc(UUID.randomUUID().toString());
        data.setTimestamp(System.currentTimeMillis());
        data.setEncryption(0);

        ParamBO param = new ParamBO();
        param.setName("李先森test");
        param.setGender(1);
        param.setMobile("13532875624");
        param.setCity("深圳市");
        param.setAge(32);
        param.setQuota(87000);
        param.setFund(1);
        param.setCar(1);
        param.setHouse(1);
        param.setPolicy(0);
        param.setLifeInsurance(0);
        param.setBusinessLicense(1);
        param.setPayroll(1);
        param.setRemark("公积金交了1年以上，月缴1000；\n有车贷；\n有房贷；\n营业执照1年以上；\n银行月代发工资8000；");
        param.setSourceId(sourceId);
        data.setParams(JSONUtil.toJsonString(param));

        JSONObject json = JSONUtil.toJSON(data);
        json.put("secret",secret);

        Set<String> keySet = json.keySet();
        List<String> keyList = new ArrayList<>(keySet);
        keyList.sort((o1,o2) -> o1.compareTo(o2));
        StringBuffer signString = new StringBuffer();
        for(int i=0;i<keyList.size();i++){
            String key = keyList.get(i);
            if(i == 0)
                signString.append(key).append("=").append(json.get(key));
            else
                signString.append("&").append(key).append("=").append(json.get(key));
        }
        data.setSign(SignUtil.sign(signString.toString()));
        String result = HttpUtil.postForJSON("Http://crm.daofen100.com/crm/customer/media/receive", JSONUtil.toJsonString(data));

        System.out.println(result);

    }




}
