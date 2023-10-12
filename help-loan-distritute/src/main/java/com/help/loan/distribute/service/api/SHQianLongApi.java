package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ec.v2.utlis.Md5Util;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.RSAUtils;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.http.HttpHeaders;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;


import org.apache.tomcat.util.codec.binary.Base64;
import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * 上海钱龙  apiSender_10117
 */
//@Component("apiSender_10117")
public class SHQianLongApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(SHQianLongApi.class);

    private static final String sendUrl =  "https://loanadmin.yofish.com/notcontrol/wo/apply.go";

    private static final String key = "6bde2e4d855045ca9ebf9ad37f36bb1c";

    private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXGhvzz/v4GJIS6smeIvK6wd/AhCIx3dEoBz9z9OIfxy+CjzpINRgxHs6W+BKUqzUz7S5NYvBEim2gJFXYi6WLN3SHqGIQ2ezbOxzH2NZc+fabZAcOzQE3pla8tKNXatSAupPgQV4ClyLPaIq7sm9uLWnZDQxPhdwnwQXQZ0LnpQIDAQAB";

    private static String signModel = "channel=%s&cphone=%s&timestamp=%s&key=%s";

    private static final String channel = "shqljr";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[上海钱龙]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海钱龙】分发未知异常："+e.getMessage()));
            return new SendResult(false,"[上海钱龙]分发异常:"+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select) throws Exception {

        isHaveAptitude(po);
        if(org.apache.commons.lang3.StringUtils.isBlank(po.getName()) && null != select){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(StringUtils.isEmpty(po.getName())) {
                if(StringUtils.isEmpty(parse.get("openid"))) {
                    po.setName("公众号用户");
                } else {
                    po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
                }
            }
        }
        JSONObject customer = new JSONObject();
        customer.put("cname",po.getName());
        customer.put("cphone",po.getMobile());
        customer.put("capplydate", DateUtil.formatToString(new Date(),DateUtil.yyyyMMddHHmmss2));
        customer.put("cidcard","");
        customer.put("iage",po.getAge());
        if(2 == po.getGender()){
            customer.put("igender", 0);
        }else if(1 == po.getGender()){
            customer.put("igender", 1);
        }else {
            customer.put("igender", 3);
        }
        customer.put("icitycode", "310100");
        customer.put("idistrictcode","310101");
        customer.put("imoney", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        customer.put("iusetimes",2);
        customer.put("imarriage",2);
        customer.put("ilocalaccount",1);
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            customer.put("iprofession",0);
        }
        if(po.getCompany() == 1){
            customer.put("iprofession",1);
            customer.put("ibusinesslicense",4);
            customer.put("imanageyears",3);
        }else{
            customer.put("iprofession",0);
        }
        if(1 == po.getGetwayIncome()){
            customer.put("icardincome",10000);
        }else if(2==po.getGetwayIncome()){
            customer.put("icardincome",50000);
        }else{
            customer.put("imonthlyincome",5000);
        }
        if(po.getPublicFund().contains("有，")){
            customer.put("iprovident",2);
        }else {
            customer.put("iprovident",0);
        }
        customer.put("isocialpay",0);
        if(JudgeUtil.in(po.getHouse(),1,2)){
            customer.put("ihouse",2);
        }else {
            customer.put("ihouse",0);
        }
        if(JudgeUtil.in(po.getCar(),1,2)){
            customer.put("icar",1);
        }else{
            customer.put("icar",0);
        }
        customer.put("ilifeinsurance",JudgeUtil.in(po.getInsurance(),1,2)?2:0);
        customer.put("iweilidaiquota",0);
        customer.put("liabilities",0);

        String timestamp = String.valueOf(System.currentTimeMillis());
        String sign = Md5Util.encryptMd5(String.format(signModel,channel,po.getMobile(),timestamp,key)).toUpperCase();
        String data = RSA.rsaEncrypt(customer.toJSONString(),publicKey);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try{
            HttpPost post = new HttpPost(sendUrl);
            post.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            List paramList = new ArrayList<>();
            paramList.add(new BasicNameValuePair("channel", channel));
            paramList.add(new BasicNameValuePair("timestamp", timestamp));
            paramList.add(new BasicNameValuePair("data", data));
            paramList.add(new BasicNameValuePair("sign", sign));
            post.setEntity(new UrlEncodedFormEntity(paramList, "utf-8"));
            response = httpclient.execute(post);
            //{"code":"1","desc":"执行成功"}
            //{"path":null,"code":"-1","error":"50X","desc":"手机号或渠道出现与数据库中一致,请检查数据"}
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            log.info("[上海钱龙]分发结果:{}",result);
            JSONObject json = JSONUtil.toJSON(result);
            if("1".equals(json.getString("code"))){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【上海钱龙】分发成功："+result));
                return new SendResult(true,"[上海钱龙]分发成功:"+result);
            }else if("-1".equals(json.getString("code"))){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【上海钱龙】分发重复："+result));
                return new SendResult(false,"[上海钱龙]分发重复:"+result);
            }
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海钱龙】分发失败："+result ));
            return new SendResult(false,"[上海钱龙]分发失败:"+result);
        }catch (Exception e){
            throw new Exception(e.getMessage(),e);
        }finally {
            httpclient.close();
            response.close();
        }
    }

    public static class RSA{
        public static final int MAX_ENCRYPT_BLOCK = 117;
        public static final String ALGORITHM_RSA = "RSA";
        public static final String CIPHER_TRANSFORMATION_RSA = "RSA/ECB/PKCS1Padding";
        public static final String CHARSET_UTF8 = "UTF-8";

        /*** 得到公钥 ** @param key 密钥字符串（经过base64编码）
         * @param charset
         *  @throws Exception
         */
        public static PublicKey getPublicKey(String key, String charset) throws Exception {
            byte[] keyBytes = Base64.decodeBase64(key.getBytes(charset));
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        }


        /*** 公钥加密 ** @param content 待加密内容 * @param publicKey 公钥 * @return 密文内容 * @throws Exception */
        public static String rsaEncrypt(String content, String publicKey) throws Exception {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                PublicKey pubKey = getPublicKey(publicKey, CHARSET_UTF8);
                Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_RSA);
                cipher.init(Cipher.ENCRYPT_MODE, pubKey);
                byte[] data = StringUtils.isEmpty(CHARSET_UTF8) ? content.getBytes() : content.getBytes(CHARSET_UTF8);
                int inputLen = data.length;
                int offSet = 0;
                byte[] cache;
                int i = 0;
                // 对数据分段加密
                while (inputLen - offSet > 0) {
                    if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                        cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                    } else {
                        cache = cipher.doFinal(data, offSet, inputLen - offSet);
                    }
                    out.write(cache, 0, cache.length);
                    i++; offSet = i * MAX_ENCRYPT_BLOCK;
                }
                byte[] encryptedData = Base64.encodeBase64(out.toByteArray());
                return StringUtils.isEmpty(CHARSET_UTF8) ? new String(encryptedData) : new String(encryptedData, CHARSET_UTF8);
            }catch (Exception e){
                throw new Exception( "error occured in rsaEncrypt: EncryptContent = " + content + ",charset = " + CHARSET_UTF8, e);
            }finally {
                out.close();
            }
        }

    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("张先森测试2");
//        po.setMobile("13632965528");
//        po.setCity("上海市");
//        po.setLoanAmount("5000000");
//        po.setCompany(1);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(0);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        SHQianLongApi api = new SHQianLongApi();
//        System.out.println(api.send(po,null));
//    }
}
