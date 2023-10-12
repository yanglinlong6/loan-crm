package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.ec.v2.utlis.Md5Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.DATA_CONVERSION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

/**
 * 上海上海正佰融信息科技有限公司
 * 上海市浦东新区福山路450号15a
 * 卢木校
 * 13726022584
 */
@Component("apiSender_20038")
public class BaiRongApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(BaiRongApi.class);

    private static final String getKeyUrl = "http://139.224.211.195:8080/admin/Aesnew/getAeskey";

    private static final String checkUrl = "http://139.224.211.195:8080/admin/Aesnew/checkphone";

    private static final String sendUrl = "http://139.224.211.195:8080/admin/Aesnew/crmUserAdd";

    private int id = 98;

    private static String channel = "zawq06";

    private static String token = "27185a65612791bff0241425fd91dc3a";

    private static final String checkUrl2 = "http://crm.zbrzd.com/api/customer/check_mobile";

    private static final String sendUrl2 = "http://crm.zbrzd.com/api/customer/import_data";

    private static final int file_id = 1;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            if(StringUtils.isNotBlank(po.getChannel()) && po.getChannel().toLowerCase().contains("ttt")){
                return new SendResult(false,"【上海正佰融】不接收[头条]客户");
            }
            return sendResult2(po,select);
        }catch (Exception e){
            log.error("[上海正佰融]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海正佰融】分发未知异常："+e.getMessage()));
            return new SendResult(false,"[上海正佰融]分发异常:"+e.getMessage());
        }
    }

    private SendResult sendResult2(UserAptitudePO po, UserDTO select){

        String md5 = MD5Util.getMd5String(po.getMobile());
        JSONObject data = new JSONObject();
        data.put("mobile",md5);

        String result = HttpUtil.postForJSON(checkUrl2,data);
        log.info("【上海正佰融】验证重复:{}",result);
        if(0 != JSONUtil.toJSON(result).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【上海正佰融】分发重复:"+result));
            return new SendResult(false,"【上海正佰融】验证重复:"+result);
        }

        isHaveAptitude(po);
        data.clear();
        data.put("file_id",file_id);
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("age",null == po.getAge()?0:po.getAge());
        data.put("sex",po.getGender());
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
        data.put("wish_remarks",getContent(po));
        result = HttpUtil.postForJSON(sendUrl2,data);
        log.info("【上海正佰融】分发结果：{}",result);
        if(0 == JSONUtil.toJSON(result).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【上海正佰融】分发成功:"+result));
            return new SendResult(true,"【上海正佰融】分发成功："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海正佰融】分发失败:"+result));
        return new SendResult(false,"【上海正佰融】分发失败："+result);
    }



//    private SendResult sendResult(UserAptitudePO po, UserDTO select){
//        isHaveAptitude(po);
//        LinkedMultiValueMap<String,Object> map = new LinkedMultiValueMap<String,Object>();
//        map.add("id",id);
//        map.add("sign", MD5Util.getMd5String(id+token));
//        //﻿{"data":{"key":"d4aab56d9cf0bfb8c56702c332c45d1e","create_time":"1608776394","token":"36141be810a2d10c89ce60acacdda198"},"msg":"success","code":0}
//        String keyReuslt = HttpUtil.postFormForObject(getKeyUrl,map);
//        log.info("[上海正佰融]获取加密key结果:{}",keyReuslt);
//        JSONObject json = JSONUtil.toJSON(keyReuslt);
//        if(0 != json.getIntValue("code")){
//            return new SendResult(false,"[上海正佰融]获取可以失败:"+json.getString("msg"));
//        }
//        String key = json.getJSONObject("data").getString("key");
//        if(key.length() >= 32){
//            key = key.substring(0,16);
//        }
//        JSONObject data = new JSONObject();
//        data.put("loan",Integer.valueOf(LoanAmountUtil.transform(po.getLoanAmount())));
//        data.put("phone",po.getMobile());
//        data.put("username",po.getName());
//        data.put("house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
//        data.put("car", JudgeUtil.in(po.getCar(),1,2)?1:0);
//        data.put("insurance", JudgeUtil.in(po.getCar(),1,2)?1:0);
//        data.put("fund",po.getPublicFund().contains("有，")?1:0);
//        data.put("socital",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
//        data.put("xyk",JudgeUtil.in(po.getCreditCard(),1,2)?1:0);
//        data.put("wld",0);
//        data.put("qyns",po.getCompany() ==1 ? 1:0);
//        data.put("city",po.getCity());
//        data.put("source",channel);
//        data.put("content",getContent(po));
//        data.put("age",po.getAge());
//        if(po.getChannel().startsWith("ttt"))
//            data.put("promotion","邦正-头条");
//        else data.put("promotion","邦正-朋友圈");
//
//        LinkedMultiValueMap<String,Object> param = new LinkedMultiValueMap();
//        param.add("id",id);
//        param.add("param",CryptAES.AES_Encrypt(key,data.toJSONString()));
//        //成功{"code":0,"msg":"\u6dfb\u52a0\u6210\u529f","uid":"abqMzI4OTk="}
//        //重复：{"code":-3,"msg":"\u624b\u673a\u53f7\u91cd\u590d"}
//        JSONObject resultJson = JSONUtil.toJSON(HttpUtil.postFormForObject(sendUrl,param));
//        int code = resultJson.getIntValue("code");
//        String msg = resultJson.getString("msg");
//        log.info("【上海正佰融】分发结果：{}",msg);
//        if(0==code){
//            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【上海正佰融】http表单分发成功："+msg));
//            return new SendResult(true,"【上海正佰融】分发成功：{}"+msg);
//        }else if(-3 == code){
//            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【上海正佰融】分发重复："+msg));
//            return new SendResult(false,"【上海正佰融】分发重复：{}"+msg);
//        }
//        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海正佰融】分发失败："+msg));
//        return new SendResult(false,"【上海正佰融】分发失败：{}"+msg);
//    }

    public static class CryptAES {

        private static final String AESTYPE ="AES/ECB/PKCS5Padding";

        public static String AES_Encrypt(String keyStr, String plainText) {
            byte[] encrypt = null;
            try{
                Key key = generateKey(keyStr);
                Cipher cipher = Cipher.getInstance(AESTYPE);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                encrypt = cipher.doFinal(plainText.getBytes());
            }catch(Exception e){
                e.printStackTrace();
            }
            return new String(Base64.encodeBase64(encrypt));
        }

        public static String AES_Decrypt(String keyStr, String encryptData) {
            byte[] decrypt = null;
            try{
                Key key = generateKey(keyStr);
                Cipher cipher = Cipher.getInstance(AESTYPE);
                cipher.init(Cipher.DECRYPT_MODE, key);
                decrypt = cipher.doFinal(Base64.decodeBase64(encryptData));
            }catch(Exception e){
                e.printStackTrace();
            }
            return new String(decrypt).trim();
        }

        private static  Key generateKey(String key)throws Exception{
            try{
                SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
                return keySpec;
            }catch(Exception e){
                e.printStackTrace();
                throw e;
            }

        }
    }


//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人测试6");
//        po.setMobile("136319652326");
//        po.setCity("上海市");
//        po.setLoanAmount("5000000");
//        po.setCompany(0);
//        po.setPublicFund("公积金有，");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setInsurance(0);
//        po.setGetwayIncome(0);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setChannel("ttt_shanghai");
//        po.setUpdateDate(new Date());
//        ApiSender api = new BaiRongApi();
//        System.out.println(api.send(po,null));
//
//    }
}
