package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

/**
 * 浦江代运营:apiSender_10041
 */
@Component("apiSender_10041")
public class PuJiang2Api implements  ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(PuJiang2Api.class);

    private static final String checkMobileUrl = "https://mgjr.360yhzd.com/api/yhzd/checkMd5";

    private static final String apiUrl = "https://mgjr.360yhzd.com/api/yhzd/addCust";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            isHaveAptitude(po);
            return sendResult(po,select);
        }catch (Exception e){
            LOG.error("【浦江】代运营异常："+e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【浦江】代运营异常："+e.getMessage()));
            return new SendResult(false,"");
        }
    }

    public SendResult sendResult(UserAptitudePO po, UserDTO select){
        JSONObject checkMobileData = new JSONObject();
        checkMobileData.put("mobileMd5",MD5Util.getMd5String(po.getMobile()));
        String checkMobileResult = HttpUtil.postForJSON(checkMobileUrl,checkMobileData);
        JSONObject checkMobileJson = JSONUtil.toJSON(checkMobileResult);
        if(-1 == checkMobileJson.getJSONObject("data").getIntValue("error_no")){
            LOG.error("浦江代运营：手机号码-{}【重复】:{}",po.getMobile(),checkMobileResult);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"浦江代运营:手机已存在："+checkMobileResult));
            return new SendResult(false,checkMobileResult);
        }

        Map<String, Object> content = new HashMap<>();
        String name = po.getName();
        if(StringUtils.isBlank(name) && null != select){
            JSONObject userInfo = JSONUtil.toJSON(WechatCenterUtil.getUserInfo(po.getUserId(),"",""));
            name = EmojiFilter.filterEmoji(userInfo.getString("nickname"),po.getUserId());
        }
        content.put("cust_name",name);
        content.put("mobile",po.getMobile());
        content.put("age",po.getAge());
        content.put("sex",po.getGender());
        content.put("city",po.getCity());
        content.put("apply_limit", LoanAmountUtil.transform(po.getLoanAmount()).toString());
        content.put("channel_source","wxpyq90");
        content.put("media_source","小微信融");
        if(JudgeUtil.in(po.getHouse(),1,2)){
            content.put("has_house","1");
        }else{
            content.put("has_house","0");
        }
        if(JudgeUtil.in(po.getCar(),1,2)){
            content.put("has_car","1");
        }else
            content.put("has_car","0");

        if(1 == po.getCompany().intValue())
            content.put("has_company","1");
        else  content.put("has_company","0");

        if(JudgeUtil.in(po.getCreditCard(),1,2)){
            content.put("has_credit","1");
        }else content.put("has_credit","0");

        if(JudgeUtil.in(po.getInsurance(),1,2)){
            content.put("has_policy","1");
        }else content.put("has_policy","0");

        content.put("has_social","0");

        if(StringUtils.isNotBlank(po.getPublicFund()) && po.getPublicFund().contains("有，")){
            content.put("has_fund","1");
        }else content.put("has_fund","0");
        if(JudgeUtil.in(po.getOccupation().intValue(),1,2,3))
            content.put("has_work","1");
        else  content.put("has_work","0");
        content.put("has_tax","0");
        content.put("wedebt_limit","0");
        JSONObject data = new JSONObject();
        data.put("enctryt_content", Crypto.encrypt(JSONUtil.toJsonString(content),Crypto.key));
        String result = HttpUtil.postForJSON(apiUrl,data);
        JSONObject sendJson = JSONUtil.toJSON(result);
        if(0 == sendJson.getJSONObject("data").getIntValue("error_no")){
            LOG.error("浦江代运营发送成功：手机-{}，结果：{}",po.getMobile(),sendJson.toJSONString());
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"浦江代运营:分发成功："+result));
            return new SendResult(true,"浦江代运营发送成功:"+sendJson.toJSONString());
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"浦江代运营:分发失败："+result));
        return new SendResult(false,"【浦江代运营】发送成功:"+sendJson.toJSONString());
    }


    public static class Crypto {

        public static String code = "UTF-8";
        public static String key = "mgjr@3des123#dhjr9763089";

        public static String encrypt(String data, String secretKey) {
            try {
                // 3DES加密
                byte[] encrpyted = tripleDES(Cipher.ENCRYPT_MODE, data.getBytes(code), secretKey.getBytes());
                byte[] encoded = Base64.encodeBase64(encrpyted); // Base64编码
                return new String(encoded);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public static String decrypt(String data, String secretKey) {
            try {
                byte[] decoded = Base64.decodeBase64(data); // Base64解码
                byte[] decrypted = tripleDES(Cipher.DECRYPT_MODE, decoded, secretKey.getBytes());// 3DES解密
                return new String(decrypted, code);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public static String decrypt(String data) {
            try {
                byte[] decoded = Base64.decodeBase64(data); // Base64解码
                byte[] decrypted = tripleDES(Cipher.DECRYPT_MODE, decoded, key.getBytes());// 3DES解密
                return new String(decrypted, code);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        private static byte[] tripleDES(int opmode, byte[] data, byte[] secretKey) {
            return cipher("DESede", "DESede/CBC/PKCS5Padding", opmode, data, "01234567".getBytes(), secretKey);
        }

        public static String SHA256(String data) {
            try {
                return DigestUtils.sha256Hex(data.getBytes(code));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public static String MD5(String data) {
            try {
                return DigestUtils.md5Hex(data.getBytes(code));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public static String MD5num(String data) {
            try {
                return byteArrayToString(DigestUtils.md5(data.getBytes(code)));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public static String encodeBase64(String data) {
            try {
                return Base64.encodeBase64String(data.getBytes(code));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public static String decodeBase64(String data) {
            try {
                return new String(Base64.decodeBase64(data), code);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        private static String byteArrayToString(byte[] b) {
            StringBuffer resultSb = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                resultSb.append(byteToNumString(b[i]));
            }
            return resultSb.toString();
        }

        private static String byteToNumString(byte b) {
            int _b = b;
            if (_b < 0) {
                _b = 256 + _b;
            }
            return String.valueOf(_b);
        }

        /**
         * 通用的对称加密算法
         *
         * @param algorithm      , 算法名称
         * @param transformation , 算法名称/工作模式/填充模式
         * @param opmode         ：Cipher.ENCRYPT_MODE和Cipher.DECRYPT_MODE
         * @param data           , 明文或密文数据
         * @param iv             , 初始化向量
         * @param secretKey      ，密钥
         * @return 加密或解密的结果
         */
        private static byte[] cipher(String algorithm, String transformation, int opmode, byte[] data, byte[] iv,
                                     byte[] secretKey) {
            try {
                // 转换密钥
                Key key = SecretKeyFactory.getInstance(algorithm).generateSecret(new DESedeKeySpec(secretKey));
                // 转换初始化向量
                IvParameterSpec spec = new IvParameterSpec(iv);

                // 加密解密器
                Cipher cipher = Cipher.getInstance(transformation);
                cipher.init(opmode, key, spec);

                // 加密解密操作
                return cipher.doFinal(data);
            } catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException
                    | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }
        }
    }

//
//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("3a1d24ac4761473abf9f4017b40288f6");
//        po.setName("测试林先森");
//        po.setHouse(0);
//        po.setAge(35);
//        po.setCity("深圳市");
//        po.setCompany(0);
//        po.setGetwayIncome(0);
//        po.setInsurance(0);
//        po.setLoanAmount("《10-30万》");
//        po.setMobile("13632965144");
//        po.setOccupation(1);
//        po.setPublicFund("无");
//        po.setOccupation(1);
//        po.setGetwayIncome(1);
//        po.setCreditCard(1);
//        po.setCar(1);
//        po.setUpdateDate(new Date());
//        PuJiang2Api api = new PuJiang2Api();
//        System.out.println(api.send(po,null));
//
//    }
}
