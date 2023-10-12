package com.help.loan.distribute.service.api.credit;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.CustomerPO;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.ApiSender;
import com.help.loan.distribute.service.api.SendResult;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import com.help.loan.distribute.util.DESUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

/**
 * 苏州法泰法律咨询有限公司 : 信用卡法律咨询
 *
 */
@Component("apiSender_20067")
public class SZHFaTaiApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(SZHFaTaiApi.class);

    private static final Logger LOG = LoggerFactory.getLogger(SZHFaTaiApi.class);

    private static final String URL = "http://crm.dewufagu.com/crm/import/customer";

    private static String KEY = "323e3ea145e34b958b8aa204b4be4fb4";

    private static Long channelId = 45l;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult2(po,select);
        }catch (Exception e){
            log.error("[苏州法泰法律-信用卡逾期]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【苏州法泰法律-信用卡逾期】分发未知异常："+e.getMessage()));
            return new SendResult(false,"[苏州法泰法律-信用卡逾期]分发异常:"+e.getMessage());
        }
    }

    private SendResult sendResult2(UserAptitudePO po, UserDTO select){

        CustomerPO customerPO = new CustomerPO();
        customerPO.setName(po.getName());
        customerPO.setMobile(po.getMobile());
        customerPO.setChannel(channelId);
        customerPO.setCity("全国");
        customerPO.setAge(0);
        customerPO.setSex(Byte.valueOf("0"));
        customerPO.setNeed(po.getLoanAmount());
        String channel = po.getChannel().toLowerCase();
        if(StringUtils.isNotBlank(channel) && channel.contains("ttt")){
            customerPO.setMedia("头条");
        }else if(StringUtils.isNotBlank(channel) && channel.contains("msg")){
            customerPO.setMedia("短信");
        }else if(channel.contains("baidu")){
            customerPO.setMedia("百度");
        }else if(channel.contains("kuaishou")){
            customerPO.setMedia("快手");
        }else {
            customerPO.setMedia("微信朋友圈");
        }
        String type = (StringUtils.isBlank(po.getPublicFund()) || po.getPublicFund().equals("0"))?"未知":po.getPublicFund();
        customerPO.setField2(StringUtils.isBlank(po.getLoanAmount())?"未知":po.getLoanAmount());// 欠款金额
        customerPO.setField3(StringUtils.isBlank(po.getOverdue())?"未知":po.getOverdue());// 逾期时间
        customerPO.setField4(type);// 欠款类型：信用卡，网贷，银行
        customerPO.setField5(po.getCallTime());
        customerPO.setRemark(getContent2(po));

        LOG.info("[苏州法泰法律]客户信息:{}",customerPO.toString());

        JSONObject data = new JSONObject();
        data.put("channelId",channelId);
        data.put("data",DESUtil.encrypt(KEY,customerPO.toString()));
        String result = HttpUtil.postForJSON(URL,data);
        JSONObject json = JSONUtil.toJSON(result);
        LOG.info("【苏州法泰法律-信用卡逾期】发送结果：{}",result);
        int code = json.getIntValue("code");
        if(200 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【苏州法泰法律-信用卡逾期】分发成功："+result));
            return new SendResult(true,"【苏州法泰法律-信用卡逾期】分发成功："+result);
        }else if(601 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【苏州法泰法律-信用卡逾期】发送重复："+result));
            return new SendResult(false,"【苏州法泰法律-信用卡逾期】发送重复："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【苏州法泰法律-信用卡逾期】发送失败："+result));
        return new SendResult(false,"【苏州法泰法律-信用卡逾期】发送失败："+result);
    }

    public String getContent2(UserAptitudePO po){
        StringBuffer content = new StringBuffer();
        content.append(po.getCity());
        content.append(";").append("欠款金额：").append(po.getLoanAmount());
        if(StringUtils.isNotBlank(po.getPublicFund()) || !"0".equals(po.getPublicFund())){
            content.append(";").append("欠款类型：").append(po.getPublicFund());
        }
        if(StringUtils.isNotBlank(po.getOverdue())){
            content.append(";").append("欠款时间:").append(po.getOverdue());
        }
        return content.toString();
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
//        po.setUserId("73ad315820c84597b282495d168e3eeb");
//        po.setName("王");
//        po.setMobile("13383698753");
//        po.setCity("邢台市");
//        po.setLoanAmount("2-10万");
//        po.setCompany(1);
//        po.setPublicFund("信用卡,网贷");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setInsurance(0);
//        po.setGetwayIncome(0);
//        po.setOccupation(0);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setGender(1);
//        po.setChannel("ttt-zijian-1717201038870536-7044353641897000991");
//        po.setOverdue("");
//        po.setUpdateDate(new Date());
//        ApiSender api = new SZHFaTaiApi();
//        System.out.println(api.send(po,null));
//
//    }
}
