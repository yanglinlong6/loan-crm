package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 光大国信
 */
@Component("apiSender_20024")
public class GuanDaGuoXinApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(GuanDaGuoXinApi.class);

    private static final String token = "bd706d19b9244d73c50e351188fb0c70";

    private static final String key = "822c67967b572a01";

    private static final String sendUrl = "http://api.milirong.com/open/customer/v4/push";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【光大国信】分发异常：{}-{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【光大国信】分发异常："+e.getMessage()));
            return new SendResult(false,"【光大国信】分发异常："+e.getMessage());
        }

    }

    public SendResult sendResult(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        JSONObject json = new JSONObject();
        json.put("mobile",po.getMobile());
        json.put("name",po.getName());
        json.put("amount", LoanAmountUtil.transform(po.getLoanAmount()));
        json.put("city",po.getCity());
        json.put("extend",getInfo(po));
        json.put("source","邦正朋友圈");

        Map<String,String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        headers.put("token",token);

        JSONObject data = new JSONObject();
        data.put("data", CryptAES.AES_Encrypt(key,json.toJSONString()));
        // {"code":200000,"message":null,"data":{"code":1,"info":"成功"}}
        String result = HttpClientProxy.doPost(sendUrl, data, "UTF-8", 3000, headers);
        log.info("【光大国信】分发结果：{}",result);
        int code = JSONUtil.toJSON(result).getJSONObject("data").getIntValue("code");
        if(1 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【光大国信】分发成功："+result));
            return new SendResult(true,"【光大国信】分发成功："+result);
        }else if(201== code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【光大国信】分发重复："+result));
            return new SendResult(false,"【光大国信】分发重复："+result);
        }
        return new SendResult(false,"【光大国信】分发失败："+result);
    }

    private String getInfo(UserAptitudePO po){
        StringBuffer memo = new StringBuffer();
        if(po.getCompany() ==1){
            memo.append("有公司").append("、");
        }
        if(po.getPublicFund().contains("有，")){
            memo.append("有公积金").append("、");
        }

        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            memo.append("有银行代发").append("、");
            memo.append("有社保").append("、");
        }
        if(JudgeUtil.in(po.getHouse(),1,2)){
            memo.append("有房").append("、");
        }
        if(JudgeUtil.in(po.getCar(),1,2)){
            memo.append("有车").append("、");
        }
        if(JudgeUtil.in(po.getInsurance(),1,2)){
            memo.append("有商业保险").append("、");
        }
        String content = memo.toString();
        if(content.endsWith("、")){
            content = content.substring(0,content.length()-1);
        }
        return content;
    }

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
            return new String(org.apache.commons.codec.binary.Base64.encodeBase64(encrypt));
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
//        po.setName("伍先森测试5");
//        po.setMobile("13632965535");
//        po.setCity("北京市");
//        po.setLoanAmount("5000000");
//        po.setCompany(1);
//        po.setPublicFund("公积金有，");
//        po.setCar(0);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new GuanDaGuoXinApi();
//        System.out.println(api.send(po,null));
//
//    }

}
